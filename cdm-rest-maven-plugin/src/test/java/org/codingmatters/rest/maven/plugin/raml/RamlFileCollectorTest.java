package org.codingmatters.rest.maven.plugin.raml;

import org.junit.Test;
import org.raml.v2.api.RamlModelBuilder;
import org.raml.v2.api.RamlModelResult;
import org.raml.v2.api.model.common.ValidationResult;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.jar.JarFile;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertThat;

public class RamlFileCollectorTest {

    @Test
    public void fromClassloader__simpleSpec() throws Exception {
        RamlFileCollector.Builder builder = RamlFileCollector.spec("from-class-loader/simple-spec.raml");

        File specFile;
        try(RamlFileCollector collector = builder.build()) {
            specFile = collector.specFile();
            assertThat(specFile.getAbsolutePath(), startsWith(System.getProperty("java.io.tmpdir")));

            RamlModelResult raml = new RamlModelBuilder().buildApi(specFile);
            assertThat(raml.hasErrors(), is(false));
            assertThat(raml.getApiV10().resources().get(0).resourcePath(), is("/root"));
        }
        assertThat(specFile.exists(), is(false));
    }

    @Test
    public void fromClassloader__specWithInclude__whenIncludeAsideSpec() throws Exception {
        RamlFileCollector.Builder builder = RamlFileCollector.spec("from-class-loader/external-types.raml");

        File specFile;
        try(RamlFileCollector collector = builder.build()) {
            specFile = collector.specFile();
            assertThat(specFile.getAbsolutePath(), startsWith(System.getProperty("java.io.tmpdir")));

            RamlModelResult raml = new RamlModelBuilder().buildApi(specFile);
            assertThat(raml.hasErrors(), is(false));
            assertThat(raml.getApiV10().types().get(0).name(), is("AType"));
            assertThat(raml.getApiV10().types().get(1).name(), is("AnotherType"));
        }
        assertThat(specFile.exists(), is(false));
    }

    @Test
    public void fromClassloader__specWithInclude_whenIncludeInSubdir() throws Exception {
        RamlFileCollector.Builder builder = RamlFileCollector.spec("from-class-loader/external-types-in-subdir.raml");

        File specFile;
        try(RamlFileCollector collector = builder.build()) {
            specFile = collector.specFile();
            assertThat(specFile.getAbsolutePath(), startsWith(System.getProperty("java.io.tmpdir")));

            RamlModelResult raml = new RamlModelBuilder().buildApi(specFile);
            assertThat(raml.hasErrors(), is(false));
            assertThat(raml.getApiV10().types().get(0).name(), is("AType"));
            assertThat(raml.getApiV10().types().get(1).name(), is("AnotherType"));
        }
        assertThat(specFile.exists(), is(false));
    }

    @Test
    public void fromClassloader__specWithInclude_whenNestedIncludeInSubdir() throws Exception {
        RamlFileCollector.Builder builder = RamlFileCollector.spec("from-class-loader/external-types-in-nested-subdir.raml");

        File specFile;
        try(RamlFileCollector collector = builder.build()) {
            specFile = collector.specFile();
            assertThat(specFile.getAbsolutePath(), startsWith(System.getProperty("java.io.tmpdir")));

            RamlModelResult raml = new RamlModelBuilder().buildApi(specFile);
            for (ValidationResult validationResult : raml.getValidationResults()) {
                System.out.println(validationResult);
            }

            assertThat(raml.hasErrors(), is(false));
            assertThat(raml.getApiV10().types().get(0).name(), is("AType"));
            assertThat(raml.getApiV10().types().get(1).name(), is("AnotherType"));
        }
        assertThat(specFile.exists(), is(false));
    }


    @Test
    public void fromOneJar__simpleSpec() throws Exception {
        RamlFileCollector.Builder builder = RamlFileCollector.spec("simple-spec.raml")
                .classpathJar(this.resolveJarFromResource("from-one-jar.jar"));

        File specFile;
        try(RamlFileCollector collector = builder.build()) {
            specFile = collector.specFile();
            assertThat(specFile.getAbsolutePath(), startsWith(System.getProperty("java.io.tmpdir")));

            RamlModelResult raml = new RamlModelBuilder().buildApi(specFile);
            assertThat(raml.hasErrors(), is(false));
            assertThat(raml.getApiV10().resources().get(0).resourcePath(), is("/root"));
        }
        assertThat(specFile.exists(), is(false));
    }

    @Test
    public void fromOneJar__specWithInclude_whenIncludeAsideSpec() throws Exception {
        RamlFileCollector.Builder builder = RamlFileCollector.spec("external-types.raml")
                .classpathJar(this.resolveJarFromResource("from-one-jar.jar"));

        File specFile;
        try(RamlFileCollector collector = builder.build()) {
            specFile = collector.specFile();
            assertThat(specFile.getAbsolutePath(), startsWith(System.getProperty("java.io.tmpdir")));

            RamlModelResult raml = new RamlModelBuilder().buildApi(specFile);
            assertThat(raml.hasErrors(), is(false));
            assertThat(raml.getApiV10().types().get(0).name(), is("AType"));
            assertThat(raml.getApiV10().types().get(1).name(), is("AnotherType"));
        }
        assertThat(specFile.exists(), is(false));
    }

    @Test
    public void fromOneJar__specWithInclude_whenIncludeInSubdir() throws Exception {
        RamlFileCollector.Builder builder = RamlFileCollector.spec("external-types-in-subdir.raml")
                .classpathJar(this.resolveJarFromResource("from-one-jar.jar"));

        File specFile;
        try(RamlFileCollector collector = builder.build()) {
            specFile = collector.specFile();
            assertThat(specFile.getAbsolutePath(), startsWith(System.getProperty("java.io.tmpdir")));

            RamlModelResult raml = new RamlModelBuilder().buildApi(specFile);
            assertThat(raml.hasErrors(), is(false));
            assertThat(raml.getApiV10().types().get(0).name(), is("AType"));
            assertThat(raml.getApiV10().types().get(1).name(), is("AnotherType"));
        }
        assertThat(specFile.exists(), is(false));
    }

    @Test
    public void fromOneJar__specWithInclude_whenNestedIncludeInSubdir() throws Exception {
        RamlFileCollector.Builder builder = RamlFileCollector.spec("external-types-in-nested-subdir.raml")
                .classpathJar(this.resolveJarFromResource("from-one-jar.jar"));

        File specFile;
        try(RamlFileCollector collector = builder.build()) {
            specFile = collector.specFile();
            assertThat(specFile.getAbsolutePath(), startsWith(System.getProperty("java.io.tmpdir")));

            RamlModelResult raml = new RamlModelBuilder().buildApi(specFile);
            for (ValidationResult validationResult : raml.getValidationResults()) {
                System.out.println(validationResult);
            }

            assertThat(raml.hasErrors(), is(false));
            assertThat(raml.getApiV10().types().get(0).name(), is("AType"));
            assertThat(raml.getApiV10().types().get(1).name(), is("AnotherType"));
        }
        assertThat(specFile.exists(), is(false));
    }


    @Test
    public void fromTwoJars__simpleSpec() throws Exception {
        RamlFileCollector.Builder builder = RamlFileCollector.spec("simple-spec.raml")
                .classpathJar(this.resolveJarFromResource("from-two-jars-1.jar"))
                .classpathJar(this.resolveJarFromResource("from-two-jars-2.jar"));

        File specFile;
        try(RamlFileCollector collector = builder.build()) {
            specFile = collector.specFile();
            assertThat(specFile.getAbsolutePath(), startsWith(System.getProperty("java.io.tmpdir")));

            RamlModelResult raml = new RamlModelBuilder().buildApi(specFile);
            assertThat(raml.hasErrors(), is(false));
            assertThat(raml.getApiV10().resources().get(0).resourcePath(), is("/root"));
        }
        assertThat(specFile.exists(), is(false));
    }

    @Test
    public void fromTwoJars__specWithInclude_whenIncludeAsideSpec() throws Exception {
        RamlFileCollector.Builder builder = RamlFileCollector.spec("external-types.raml")
                .classpathJar(this.resolveJarFromResource("from-two-jars-1.jar"))
                .classpathJar(this.resolveJarFromResource("from-two-jars-2.jar"));

        File specFile;
        try(RamlFileCollector collector = builder.build()) {
            specFile = collector.specFile();
            assertThat(specFile.getAbsolutePath(), startsWith(System.getProperty("java.io.tmpdir")));

            RamlModelResult raml = new RamlModelBuilder().buildApi(specFile);
            assertThat(raml.hasErrors(), is(false));
            assertThat(raml.getApiV10().types().get(0).name(), is("AType"));
            assertThat(raml.getApiV10().types().get(1).name(), is("AnotherType"));
        }
        assertThat(specFile.exists(), is(false));
    }

    @Test
    public void fromTwoJars__specWithInclude_whenIncludeInSubdir() throws Exception {
        RamlFileCollector.Builder builder = RamlFileCollector.spec("external-types-in-subdir.raml")
                .classpathJar(this.resolveJarFromResource("from-two-jars-1.jar"))
                .classpathJar(this.resolveJarFromResource("from-two-jars-2.jar"));

        File specFile;
        try(RamlFileCollector collector = builder.build()) {
            specFile = collector.specFile();
            assertThat(specFile.getAbsolutePath(), startsWith(System.getProperty("java.io.tmpdir")));

            RamlModelResult raml = new RamlModelBuilder().buildApi(specFile);
            assertThat(raml.hasErrors(), is(false));
            assertThat(raml.getApiV10().types().get(0).name(), is("AType"));
            assertThat(raml.getApiV10().types().get(1).name(), is("AnotherType"));
        }
        assertThat(specFile.exists(), is(false));
    }

    @Test
    public void fromTwoJars__specWithInclude_whenNestedIncludeInSubdir() throws Exception {
        RamlFileCollector.Builder builder = RamlFileCollector.spec("external-types-in-nested-subdir.raml")
                .classpathJar(this.resolveJarFromResource("from-two-jars-1.jar"))
                .classpathJar(this.resolveJarFromResource("from-two-jars-2.jar"));

        File specFile;
        try(RamlFileCollector collector = builder.build()) {
            specFile = collector.specFile();
            assertThat(specFile.getAbsolutePath(), startsWith(System.getProperty("java.io.tmpdir")));

            RamlModelResult raml = new RamlModelBuilder().buildApi(specFile);
            for (ValidationResult validationResult : raml.getValidationResults()) {
                System.out.println(validationResult);
            }

            assertThat(raml.hasErrors(), is(false));
            assertThat(raml.getApiV10().types().get(0).name(), is("AType"));
            assertThat(raml.getApiV10().types().get(1).name(), is("AnotherType"));
        }
        assertThat(specFile.exists(), is(false));
    }





    private JarFile resolveJarFromResource(String resource) throws IOException, URISyntaxException {
        return new JarFile(new File(Thread.currentThread().getContextClassLoader().getResource(resource).toURI()));
    }
}
