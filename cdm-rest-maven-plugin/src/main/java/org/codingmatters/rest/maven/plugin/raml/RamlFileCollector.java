package org.codingmatters.rest.maven.plugin.raml;

import com.sun.net.httpserver.HttpHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.jar.JarFile;

public class RamlFileCollector implements AutoCloseable {
    static private final Logger log = LoggerFactory.getLogger(RamlFileCollector.class);

    static public final String INCLUDE_TOKEN = "!include";

    static public Builder spec(String specResourceName) {
        return new Builder(specResourceName);
    }

    static public class Builder {
        private final String specResourceName;
        private List<JarFile> classpathJars = new LinkedList();

        private Builder(String specResourceName) {
            this.specResourceName = specResourceName;
        }

        public Builder classpathJar(JarFile jar) {
            this.classpathJars.add(jar);
            return this;
        }

        public RamlFileCollector build() throws IOException {
            RamlFileCollector collector = new RamlFileCollector(
                    this.specResourceName,
                    this.classpathJars.toArray(new JarFile[this.classpathJars.size()])
            );
            collector.collect();
            return collector;
        }
    }

    private final String specResourceName;
    private final JarFile [] classpathJars;

    private File tempDir;
    private File specFile;

    private RamlFileCollector(String specResourceName, JarFile[] classpathJars) {
        this.specResourceName = specResourceName;
        this.classpathJars = classpathJars;
    }

    private void collect() throws IOException {
        this.tempDir = new File(System.getProperty("java.io.tmpdir"), UUID.randomUUID().toString());
        this.tempDir.mkdirs();

        this.specFile = this.gather(this.specResourceName, "");

        this.collectIncludes(this.pathPart(this.specResourceName), this.specFile, "");
    }

    private File gather(String resource, String toPath) throws IOException {
        log.info("looking up {} for path {}", resource, toPath);
        if(new File(resource).exists()) {
            log.debug("{} resource found as file. Will copy to {}.", resource, toPath);
            File file = new File(resource);
            try (InputStream in = new FileInputStream(file)) {
                return this.copyToTempDir(in, file.getName(), toPath);
            }
        } else {
            for (JarFile classpathJar : this.classpathJars) {
                if(classpathJar.getJarEntry(resource) != null) {
                    log.debug("{} resource found in classpath jars. Will copy to {}.", resource, toPath);
                    try (InputStream in = classpathJar.getInputStream(classpathJar.getJarEntry(resource))) {
                        return this.copyToTempDir(in, this.namePart(resource), toPath);
                    }
                }
            }
            if(this.existsInClassLoader(resource)) {
                log.debug("{} resource found in class loader. Will copy to {}.", resource, toPath);
                File file = this.fromClassLoader(resource);
                try(InputStream in = new FileInputStream(file)) {
                    return this.copyToTempDir(in, file.getName(), toPath);
                }
            } else {
                try {
                    URL url = new URL(resource);
                    if(System.getProperty("use.raml.cache", "true").equals("true")) {
                        if (url.getProtocol().equals("http") || url.getProtocol().equals("https")) {
                            File file = this.downloadToFile(url);
                            try (InputStream in = new FileInputStream(file)) {
                                return this.copyToTempDir(in, file.getName(), toPath);
                            }
                        }
                    }
                    /* include is a valid url, we leave it as is */
                    return null;
                } catch (MalformedURLException | URISyntaxException | InterruptedException e) {
                    log.error("error reading url resource : " + resource, e);
                    throw new IOException("resource not found " + resource, e);
                }
            }
        }
    }

    static private File ramlHttpCache = new File(System.getProperty("java.io.tmpdir") + "/" + RamlFileCollector.class.getName() + "-http--cache");
    static {
        ramlHttpCache.mkdirs();
    }
    static private long cacheTtl = 1000 * 60 * 20; //Long.parseLong(System.getProperty("raml.cache.ttl", "" + (1000 * 60 * 2)));
    static private HttpClient httpClient = HttpClient.newHttpClient();

    private File downloadToFile(URL url) throws URISyntaxException, IOException, InterruptedException {
        String key = url.toString().replaceAll("[:/]", "_");
        File cache = new File(ramlHttpCache, key);
        cache.mkdirs();
        File etag = new File(cache, "etag");
        File contents = new File(cache, "contents");
        File contentsTemp = new File(cache, "contents.temp");

        if(contents.exists() && this.shouldUseCache(url, contents)) {
            return contents;
        }

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(url.toURI());
        if(etag.exists()) {
            try {
                requestBuilder.header("If-None-Match", this.fileContents(etag));
            } catch (IOException e) {
                log.warn("[raml cache] unreadable etag : " + etag.getAbsolutePath() + " not using conditional query");
            }
        }

        HttpRequest request = requestBuilder
                .GET()
                .build();
        HttpResponse<Path> response = httpClient.send(request, HttpResponse.BodyHandlers.ofFile(contentsTemp.toPath()));
        try {
            if(response.statusCode() != 304) {
                Optional<String> responseEtag = response.headers().firstValue("etag");
                if (responseEtag.isPresent()) {
                    this.writeFileContent(etag, responseEtag.get());
                }
                if(contents.exists()) {
                    contents.delete();
                }
                contentsTemp.renameTo(contents);
                log.info("[raml cache] downloaded : {}", url);
            } else {
                log.info("[raml cache] not modified since downloaded : {}", url);
                contents.setLastModified(System.currentTimeMillis());
            }
        } finally {
            if(contentsTemp.exists()) {
                contentsTemp.delete();
            }
        }
        return contents;
    }

    private boolean shouldUseCache(URL url, File contents) {
        if(url.toString().startsWith("https://raw.githubusercontent.com")) {
            if(! url.toString().contains("-SNAPSHOT")) {
                log.info("[raml cache] using cache for released version {}", url);
                return true;
            }
        }
        long lifetime = System.currentTimeMillis() - contents.lastModified();
        if(lifetime < cacheTtl) {
            log.info("[raml cache] using cache as ttl not expired for {}", url);
            return true;
        } else {
            return false;
        }
    }

    private void writeFileContent(File etag, String content) throws IOException {
        try(FileWriter writer = new FileWriter(etag)) {
            writer.write(content);
            writer.flush();
        }
    }

    private String fileContents(File etag) throws IOException {
        StringBuilder result = new StringBuilder();
        try(FileReader reader = new FileReader(etag)) {
            char[] buffer = new char[1024];
            for(int read = reader.read(buffer) ; read != -1 ; read = reader.read(buffer)) {
                result.append(buffer, 0, read);
            }
        }
        return result.toString();
    }

    private void collectIncludes(String lookupPath, File file, String toPath) throws IOException {
        if(file == null) return;
        log.debug("collecting includes from {}", file);
        try(BufferedReader reader = new BufferedReader(new FileReader(file))) {
            for(String line = reader.readLine() ; line != null ; line = reader.readLine()) {
                if(! line.trim().startsWith("#")) {
                    int start = line.indexOf(INCLUDE_TOKEN);
                    if (start != -1) {
                        String include = line.substring(start + INCLUDE_TOKEN.length()).trim();
                        log.info("found include {} in {}", include, line);

                        String includeResource = this.isIncludeAnHttpUrl(include) ? include : this.buildPath(lookupPath, include);
                        String includeDestinationPath = this.buildPath(toPath, this.pathPart(include));
                        String nestedIncludeLookupPath = this.buildPath(lookupPath, this.pathPart(include));

                        File included = this.gather(includeResource, includeDestinationPath);
                        if (include != null) {
                            this.collectIncludes(nestedIncludeLookupPath, included, includeDestinationPath);
                        }
                    }
                }
            }
        }
    }

    private boolean isIncludeAnHttpUrl(String include) {
        return include.startsWith("http://") || include.startsWith("https://");
    }

    private File fromClassLoader(String resource) throws IOException {
        URL resourceUrl = null;
        try {
            resourceUrl = Thread.currentThread().getContextClassLoader().getResource(resource);
            if(resourceUrl == null) {
                throw new IOException("no resource found for " + resource);
            }
            return new File(resourceUrl.toURI());
        } catch (URISyntaxException | IllegalArgumentException e) {
            throw new IOException("error reading resource " + resource + " from class loader with url " + resourceUrl, e);
        }
    }

    private boolean existsInClassLoader(String resource) {
        return Thread.currentThread().getContextClassLoader().getResource(resource) != null;
    }

    private File copyToTempDir(InputStream in, String name, String path) throws IOException {
        File dest = new File(new File(this.tempDir, path), name);
        dest.getParentFile().mkdirs();
        dest.createNewFile();

        log.debug("creating {}", dest);

        try(OutputStream out = new FileOutputStream(dest)) {
            byte [] buffer = new byte[1024];
            for(int read = in.read(buffer) ; read != -1 ; read = in.read(buffer)) {
                out.write(buffer, 0, read);
            }
            out.flush();
        }
        return dest;
    }

    private String buildPath(String start, String end) {
        if("".equals(start)) {
            return end;
        } else if(start.endsWith("/")){
            return start + end;
        } else {
            return start + "/" + end;
        }
    }

    private String pathPart(String path) {
        if(path.contains("/")) {
            return path.substring(0, path.lastIndexOf("/"));
        } else {
            return "";
        }
    }

    private String namePart(String path) {
        if(path.contains("/")) {
            return path.substring(path.lastIndexOf("/"));
        } else {
            return path;
        }
    }


    public File specFile() {
        return this.specFile;
    }

    @Override
    public void close() throws Exception {
        this.delete(this.tempDir);
    }

    private void delete(File file) {
        if(file.isDirectory()) {
            for (File child : file.listFiles()) {
                this.delete(child);
            }
        }
        file.delete();
    }

}
