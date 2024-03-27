package org.codingmatters.rest.server.acceptance;

import okhttp3.OkHttpClient;
import org.codingmatters.rest.api.RequestDelegate;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public abstract class RequestDelegateAcceptanceTest extends BaseAcceptanceTest {

    static private final Logger log = LoggerFactory.getLogger(RequestDelegateAcceptanceTest.class);

    private OkHttpClient client = new OkHttpClient();

    @Test
    public void method_get() throws Exception {
        AtomicReference<RequestDelegate.Method> method = new AtomicReference<>();
        this.withProcessor((requestDeleguate, responseDeleguate) -> {method.set(requestDeleguate.method());});

        this.client.newCall(this.requestBuilder().get().build()).execute();
        assertThat(method.get(), is(RequestDelegate.Method.GET));
    }

    @Test
    public void method_post() throws Exception {
        AtomicReference<RequestDelegate.Method> method = new AtomicReference<>();
        this.withProcessor((requestDeleguate, responseDeleguate) -> {method.set(requestDeleguate.method());});

        this.client.newCall(this.requestBuilder().post(this.emptyJsonBody()).build()).execute();
        assertThat(method.get(), is(RequestDelegate.Method.POST));
    }

    @Test
    public void method_put() throws Exception {
        AtomicReference<RequestDelegate.Method> method = new AtomicReference<>();
        this.withProcessor((requestDeleguate, responseDeleguate) -> {method.set(requestDeleguate.method());});

        this.client.newCall(this.requestBuilder().put(this.emptyJsonBody()).build()).execute();
        assertThat(method.get(), is(RequestDelegate.Method.PUT));
    }

    @Test
    public void method_patch() throws Exception {
        AtomicReference<RequestDelegate.Method> method = new AtomicReference<>();
        this.withProcessor((requestDeleguate, responseDeleguate) -> {method.set(requestDeleguate.method());});

        this.client.newCall(this.requestBuilder().patch(this.emptyJsonBody()).build()).execute();
        assertThat(method.get(), is(RequestDelegate.Method.PATCH));
    }

    @Test
    public void method_delete() throws Exception {
        AtomicReference<RequestDelegate.Method> method = new AtomicReference<>();
        this.withProcessor((requestDeleguate, responseDeleguate) -> {method.set(requestDeleguate.method());});

        this.client.newCall(this.requestBuilder().delete().build()).execute();
        assertThat(method.get(), is(RequestDelegate.Method.DELETE));
    }

    @Test
    public void pathMatcher() throws Exception {
        AtomicReference<Matcher> matcher = new AtomicReference<>();
        this.withProcessor((requestDeleguate, responseDeleguate) -> {matcher.set(requestDeleguate.pathMatcher("/[a]+/[b]+"));});

        this.client.newCall(this.requestBuilder("/aaa/bbb").get().build()).execute();
        assertThat(matcher.get().matches(), is(true));
    }

    @Test
    public void absolutePath() throws Exception {
        AtomicReference<String> absolutePath = new AtomicReference<>();
        this.withProcessor((requestDeleguate, responseDeleguate) -> {absolutePath.set(requestDeleguate.absolutePath("aaa/bbb"));});

        this.client.newCall(this.requestBuilder().get().build()).execute();
        assertThat(absolutePath.get(), is(this.baseUrl() + "/aaa/bbb"));
    }

    @Test
    public void payload() throws Exception {
        AtomicReference<String> payload = new AtomicReference<>();
        this.withProcessor((requestDeleguate, responseDeleguate) -> {
            String result = this.readAsString(requestDeleguate);
            payload.set(result);
        });

        String requestPayload = "{\"yop\":\"yop\"}";
        this.client.newCall(this.requestBuilder().post(this.jsonBody(requestPayload)).build()).execute();
        assertThat(payload.get(), is(requestPayload));
    }

    @Test
    public void queryParameters_singleParams() throws Exception {
        AtomicReference<Map<String, List<String>>> queryParameters = new AtomicReference<>();
        this.withProcessor((requestDeleguate, responseDeleguate) -> {queryParameters.set(requestDeleguate.queryParameters());});

        this.client.newCall(this.requestBuilder("?n1=v1&n2=v2").get().build()).execute();
        assertThat(queryParameters.get().size(), is(2));
        assertThat(queryParameters.get().get("n1"), contains("v1"));
        assertThat(queryParameters.get().get("n2"), contains("v2"));
    }

    @Test
    public void queryParameters_multipleParams() throws Exception {
        AtomicReference<Map<String, List<String>>> queryParameters = new AtomicReference<>();
        this.withProcessor((requestDeleguate, responseDeleguate) -> {queryParameters.set(requestDeleguate.queryParameters());});

        this.client.newCall(this.requestBuilder("?n=v1&n=v2").get().build()).execute();
        assertThat(queryParameters.get().size(), is(1));
        assertThat(queryParameters.get().get("n"), contains("v1", "v2"));
    }

    @Test
    public void queryParameters_undefined() throws Exception {
        AtomicReference<Map<String, List<String>>> queryParameters = new AtomicReference<>();
        this.withProcessor((requestDeleguate, responseDeleguate) -> {queryParameters.set(requestDeleguate.queryParameters());});

        this.client.newCall(this.requestBuilder("?n=v").get().build()).execute();
        assertThat(queryParameters.get().size(), is(1));
        assertThat(queryParameters.get().get("undefined"), is(nullValue()));
    }

    @Test
    public void queryParameters_emptyParam() throws Exception {
        AtomicReference<Map<String, List<String>>> queryParameters = new AtomicReference<>();
        this.withProcessor((requestDeleguate, responseDeleguate) -> {queryParameters.set(requestDeleguate.queryParameters());});

        this.client.newCall(this.requestBuilder("?n=").get().build()).execute();
        assertThat(queryParameters.get().size(), is(1));
        assertThat(queryParameters.get().get("n"), contains(""));
    }

    @Test
    public void path() throws Exception {
        AtomicReference<RequestDelegate> req = new AtomicReference<>();
        this.withProcessor((requestDeleguate, responseDeleguate) -> {
            req.set(requestDeleguate);
        });

        this.client.newCall(this.requestBuilder("/request/path").get().build()).execute();
        assertThat(req.get().path(), is("/request/path"));

        this.client.newCall(this.requestBuilder("/request/path?yop=yop").get().build()).execute();
        assertThat(req.get().path(), is("/request/path"));

        this.client.newCall(this.requestBuilder("/request/path/").get().build()).execute();
        assertThat(req.get().path(), is("/request/path/"));

        this.client.newCall(this.requestBuilder("").get().build()).execute();
        assertThat(req.get().path(), is("/"));
    }

    @Test
    public void headers() throws Exception {
        AtomicReference<RequestDelegate> req = new AtomicReference<>();
        this.withProcessor((requestDeleguate, responseDeleguate) -> {
            req.set(requestDeleguate);
        });
        this.client.newCall(this.requestBuilder("/request/path").addHeader( "toto", "val1" ).get().build()).execute();
        assertThat( req.get().headers().get( "toto" ).get( 0 ), is( "val1" ) );

        this.client.newCall(this.requestBuilder("/request/path").addHeader( "toto*", "utf-8''val%C3%A9" ).get().build()).execute();
        assertThat( req.get().headers().get( "toto" ).get( 0 ), is( "valé" ) );
    }

    @Test
    public void headersAreCaseInsensitive() throws Exception {
        AtomicReference<RequestDelegate> req = new AtomicReference<>();
        this.withProcessor((requestDeleguate, responseDeleguate) -> {
            req.set(requestDeleguate);
        });
        this.client.newCall(this.requestBuilder("/request/path").addHeader( "toto", "val1" ).get().build()).execute();
        assertThat( req.get().headers().get( "toto" ).get( 0 ), is( "val1" ) );
        assertThat( req.get().headers().get( "TOTO" ).get( 0 ), is( "val1" ) );

        this.client.newCall(this.requestBuilder("/request/path").addHeader( "TOTO", "val1" ).get().build()).execute();
        assertThat( req.get().headers().get( "toto" ).get( 0 ), is( "val1" ) );
        assertThat( req.get().headers().get( "TOTO" ).get( 0 ), is( "val1" ) );
    }

    @Test
    public void uriParams() throws Exception {
        AtomicReference<RequestDelegate> req = new AtomicReference<>();
        this.withProcessor((requestDeleguate, responseDeleguate) -> {
            req.set(requestDeleguate);
        });

        this.client.newCall(this.requestBuilder("/request/path").get().build()).execute();
        assertThat(req.get().path(), is("/request/path"));

        Map<String, List<String>> params = req.get().uriParameters("/{a}/{b}");
        assertThat(params.size(), is(2));
        assertThat(params.get("a").get(0), is("request"));
        assertThat(params.get("b").get(0), is("path"));
    }

    @Test
    public void uriParams__whenChangingPathExpression__thenParametersAreParsedAgain() throws Exception {
        AtomicReference<RequestDelegate> req = new AtomicReference<>();
        this.withProcessor((requestDeleguate, responseDeleguate) -> {
            req.set(requestDeleguate);
        });

        this.client.newCall(this.requestBuilder("/request/path").get().build()).execute();
        assertThat(req.get().path(), is("/request/path"));

        Map<String, List<String>> params = req.get().uriParameters("/request/{b}");
        assertThat(params.size(), is(1));
        assertThat(params.get("b").get(0), is("path"));

        params = req.get().uriParameters("/{a}/{b}");
        assertThat(params.size(), is(2));
        assertThat(params.get("a").get(0), is("request"));
        assertThat(params.get("b").get(0), is("path"));
    }

    @Test
    public void givenNoUriParams__whenUriWithEncodedCharacters__thenCharactersAreLeftEncoded() throws Exception {
        AtomicReference<RequestDelegate> req = new AtomicReference<>();
        this.withProcessor((requestDeleguate, responseDeleguate) -> {
            req.set(requestDeleguate);
        });

        this.client.newCall(this.requestBuilder("/request%2Fpath").get().build()).execute();
        assertThat(req.get().path(), is("/request%2Fpath"));

        log.info("request path : {}", req.get().path());
    }

    @Test
    public void givenUriParams__whenUriWithEncodedCharacters__thenCharactersAreLeftEncoded() throws Exception {
        AtomicReference<RequestDelegate> req = new AtomicReference<>();
        this.withProcessor((requestDeleguate, responseDeleguate) -> {
            req.set(requestDeleguate);
        });

        this.client.newCall(this.requestBuilder("/request/to%2Fpath").get().build()).execute();
        assertThat(req.get().path(), is("/request/to%2Fpath"));

        Map<String, List<String>> params = req.get().uriParameters("/request/{b}");
        assertThat(params.size(), is(1));
        assertThat(params.get("b").get(0), is("to%2Fpath"));


        log.info("request path : {}", req.get().path());
        log.info("request param : {}", params.get("b").get(0));
    }

    @Test
    public void givenUriParams__whenUriWithEncodedPLUS__thenCharactersAreLeftEncoded() throws Exception {
        AtomicReference<RequestDelegate> req = new AtomicReference<>();
        this.withProcessor((requestDeleguate, responseDeleguate) -> {
            req.set(requestDeleguate);
        });

        this.client.newCall(this.requestBuilder("/request/to%2Bpath").get().build()).execute();
        assertThat(req.get().path(), is("/request/to%2Bpath"));

        Map<String, List<String>> params = req.get().uriParameters("/request/{b}");
        assertThat(params.size(), is(1));
        assertThat(params.get("b").get(0), is("to%2Bpath"));


        log.info("request path : {}", req.get().path());
        log.info("request param : {}", params.get("b").get(0));
    }
}
