package org.codingmatters.rest.api.generator.client;

import com.fasterxml.jackson.core.JsonFactory;
import org.codingmatters.rest.api.client.RequesterFactory;
import org.codingmatters.rest.api.generator.client.support.TestRequesterFactory;
import org.junit.Test;

import static org.codingmatters.rest.api.generator.client.support.ClientGeneratorHelper.API_PACK;
import static org.codingmatters.rest.api.generator.client.support.ClientGeneratorHelper.CLIENT_PACK;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class RequesterClientGeneratorRequestUriParametersTest extends AbstractRequesterClientGeneratorTest {
    @Test
    public void uriParams() throws Exception {
        TestRequesterFactory requesterFactory = new TestRequesterFactory();
        JsonFactory jsonFactory = new JsonFactory();
        String baseUrl = "https://path.to/me";

        requesterFactory.nextResponse(TestRequesterFactory.Method.GET, 200);

        Object client = this.compiled().getClass(CLIENT_PACK + ".TestAPIRequesterClient")
                .getConstructor(RequesterFactory.class, JsonFactory.class, String.class)
                .newInstance(requesterFactory, jsonFactory, baseUrl);

        Object resource = this.compiled().on(client).invoke("uriParams");

        Object requestBuilder = this.compiled()
                .onClass(API_PACK + ".UriParamsGetRequest")
                .invoke("builder");

        this.compiled().on(requestBuilder).invoke("param", String.class).with("val");

        Object request = this.compiled().on(requestBuilder).invoke("build");

        this.compiled().on(resource)
                .invoke("get", this.compiled().getClass(API_PACK + ".UriParamsGetRequest"))
                .with(request);

        assertThat(requesterFactory.calls(), hasSize(1));
        assertThat(requesterFactory.calls().get(0).method(), is(TestRequesterFactory.Method.GET));
        assertThat(requesterFactory.calls().get(0).path(), is("/uri-param/val"));
    }

    @Test
    public void twoUriParams() throws Exception {
        TestRequesterFactory requesterFactory = new TestRequesterFactory();
        JsonFactory jsonFactory = new JsonFactory();
        String baseUrl = "https://path.to/me";

        requesterFactory.nextResponse(TestRequesterFactory.Method.GET, 200);

        Object client = this.compiled().getClass(CLIENT_PACK + ".TestAPIRequesterClient")
                .getConstructor(RequesterFactory.class, JsonFactory.class, String.class)
                .newInstance(requesterFactory, jsonFactory, baseUrl);

        Object resource = this.compiled().on(client).invoke("uriParams");
        resource = this.compiled().on(resource).invoke("twoUriParams");

        Object requestBuilder = this.compiled()
                .onClass(API_PACK + ".TwoUriParamsGetRequest")
                .invoke("builder");

        this.compiled().on(requestBuilder).invoke("param", String.class).with("val");
        this.compiled().on(requestBuilder).invoke("param2", String.class).with("val2");

        Object request = this.compiled().on(requestBuilder).invoke("build");

        this.compiled().on(resource)
                .invoke("get", this.compiled().getClass(API_PACK + ".TwoUriParamsGetRequest"))
                .with(request);

        assertThat(requesterFactory.calls(), hasSize(1));
        assertThat(requesterFactory.calls().get(0).method(), is(TestRequesterFactory.Method.GET));
        assertThat(requesterFactory.calls().get(0).path(), is("/uri-param/val/another/val2"));
    }

    @Test
    public void arrayUriParams() throws Exception {
        TestRequesterFactory requesterFactory = new TestRequesterFactory();
        JsonFactory jsonFactory = new JsonFactory();
        String baseUrl = "https://path.to/me";

        requesterFactory.nextResponse(TestRequesterFactory.Method.GET, 200);

        Object client = this.compiled().getClass(CLIENT_PACK + ".TestAPIRequesterClient")
                .getConstructor(RequesterFactory.class, JsonFactory.class, String.class)
                .newInstance(requesterFactory, jsonFactory, baseUrl);

        Object resource = this.compiled().on(client).invoke("uriParams");
        resource = this.compiled().on(resource).invoke("arrayUriParams");

        Object requestBuilder = this.compiled()
                .onClass(API_PACK + ".ArrayUriParamsGetRequest")
                .invoke("builder");

        this.compiled().on(requestBuilder).invoke("param", String[].class).with(new Object[] {new String [] {"v1", "v2"}});

        Object request = this.compiled().on(requestBuilder).invoke("build");

        this.compiled().on(resource)
                .invoke("get", this.compiled().getClass(API_PACK + ".ArrayUriParamsGetRequest"))
                .with(request);

        assertThat(requesterFactory.calls(), hasSize(1));
        assertThat(requesterFactory.calls().get(0).method(), is(TestRequesterFactory.Method.GET));
        assertThat(requesterFactory.calls().get(0).path(), is("/uri-param/v1/another-one/v2"));
    }
}