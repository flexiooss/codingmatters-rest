package org.codingmatters.rest.api.generator.processor;

import okhttp3.Request;
import okhttp3.Response;
import org.codingmatters.rest.api.generator.AbstractProcessorHttpRequestTest;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.concurrent.atomic.AtomicReference;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

/**
 * Created by nelt on 5/30/17.
 */
public class ProcessorRequestHeadersTest extends AbstractProcessorHttpRequestTest {

    private AtomicReference requestHolder = new AtomicReference();

    @Before
    public void setUp() throws Exception {
        ProcessorGeneratorTestHelper helper = new ProcessorGeneratorTestHelper(this.dir, this.fileHelper)
                .setUpWithResource("processor/processor-request.raml");
        this.compiled = helper.compiled();
        this.classes = this.compiled.classLoader();
        this.setupProcessor("headerParamsGetHandler");
    }

    @Test
    public void stringParameter() throws Exception {
        Response response = this.client.newCall(new Request.Builder().url(this.undertow.baseUrl() + "/api/header-params")
                .header("stringParam", "val")
                .get()
                .build()).execute();
        Object request = requestHolder.get();

        assertThat(response.code(), is(200));
        assertThat(request, is(notNullValue()));
        assertThat(
                this.classes.wrap(request).as("org.generated.api.HeaderParamsGetRequest").call("stringParam").get(),
                is("val")
        );
    }

    @Test
    public void longParameter() throws Exception {
        Response response = this.client.newCall(new Request.Builder().url(this.undertow.baseUrl() + "/api/header-params")
                .header("intParam", "12")
                .get()
                .build()).execute();
        Object request = requestHolder.get();
        assertThat(response.code(), is(200));
        assertThat(request, is(notNullValue()));
        assertThat(
                this.classes.wrap(request).as("org.generated.api.HeaderParamsGetRequest").call("intParam").get(),
                is(12L)
        );
    }

    @Test
    public void doubleParameter() throws Exception {
        Response response = this.client.newCall(new Request.Builder().url(this.undertow.baseUrl() + "/api/header-params")
                .header("realParam", "12.42")
                .get()
                .build()).execute();
        Object request = requestHolder.get();
        assertThat(response.code(), is(200));
        assertThat(request, is(notNullValue()));
        assertThat(
                this.classes.wrap(request).as("org.generated.api.HeaderParamsGetRequest").call("realParam").get(),
                is(12.42d)
        );
    }

    @Test
    public void datetimeParameter() throws Exception {
        Response response = this.client.newCall(new Request.Builder().url(this.undertow.baseUrl() + "/api/header-params")
                .header("datetimeParam", "2018-12-25T23:59:59.123Z")
                .get()
                .build()).execute();
        Object request = requestHolder.get();
        assertThat(response.code(), is(200));
        assertThat(request, is(notNullValue()));
        assertThat(
                this.classes.wrap(request).as("org.generated.api.HeaderParamsGetRequest").call("datetimeParam").get(),
                is(LocalDateTime.parse("2018-12-25T23:59:59.123"))
        );
    }

    @Test
    public void dateParameter() throws Exception {
        Response response = this.client.newCall(new Request.Builder().url(this.undertow.baseUrl() + "/api/header-params")
                .header("dateParam", "2018-12-25")
                .get()
                .build()).execute();
        Object request = requestHolder.get();
        assertThat(response.code(), is(200));
        assertThat(request, is(notNullValue()));
        assertThat(
                this.classes.wrap(request).as("org.generated.api.HeaderParamsGetRequest").call("dateParam").get(),
                is(LocalDate.parse("2018-12-25"))
        );
    }

    @Test
    public void timeParameter() throws Exception {
        Response response = this.client.newCall(new Request.Builder().url(this.undertow.baseUrl() + "/api/header-params")
                .header("timeParam", "23:59:59.123Z")
                .get()
                .build()).execute();
        Object request = requestHolder.get();
        assertThat(response.code(), is(200));
        assertThat(request, is(notNullValue()));
        assertThat(
                this.classes.wrap(request).as("org.generated.api.HeaderParamsGetRequest").call("timeParam").get(),
                is(LocalTime.parse("23:59:59.123"))
        );
    }

    @Test
    public void boolParameter() throws Exception {
        Response response = this.client.newCall(new Request.Builder().url(this.undertow.baseUrl() + "/api/header-params")
                .header("boolParam", "true")
                .get()
                .build()).execute();
        Object request = requestHolder.get();
        assertThat(response.code(), is(200));
        assertThat(request, is(notNullValue()));
        assertThat(
                this.classes.wrap(request).as("org.generated.api.HeaderParamsGetRequest").call("boolParam").get(),
                is(Boolean.TRUE)
        );

        response = this.client.newCall(new Request.Builder().url(this.undertow.baseUrl() + "/api/header-params")
                .header("boolParam", "false")
                .get()
                .build()).execute();
        request = requestHolder.get();
        assertThat(response.code(), is(200));
        assertThat(request, is(notNullValue()));
        assertThat(
                this.classes.wrap(request).as("org.generated.api.HeaderParamsGetRequest").call("boolParam").get(),
                is(Boolean.FALSE)
        );

        response = this.client.newCall(new Request.Builder().url(this.undertow.baseUrl() + "/api/header-params")
                .header("boolParam", "1")
                .get()
                .build()).execute();
        request = requestHolder.get();
        assertThat(response.code(), is(200));
        assertThat(request, is(notNullValue()));
        assertThat(
                this.classes.wrap(request).as("org.generated.api.HeaderParamsGetRequest").call("boolParam").get(),
                is(Boolean.TRUE)
        );

        response = this.client.newCall(new Request.Builder().url(this.undertow.baseUrl() + "/api/header-params")
                .header("boolParam", "0")
                .get()
                .build()).execute();
        request = requestHolder.get();
        assertThat(response.code(), is(200));
        assertThat(request, is(notNullValue()));
        assertThat(
                this.classes.wrap(request).as("org.generated.api.HeaderParamsGetRequest").call("boolParam").get(),
                is(Boolean.FALSE)
        );
    }

    @Test
    public void stringArrayParameter() throws Exception {
        Response response = this.client.newCall(new Request.Builder().url(this.undertow.baseUrl() + "/api/header-params")
                .header("arrayParam", "val1")
                .addHeader("arrayParam", "val2")
                .addHeader("arrayParam", "val3")
                .get()
                .build()).execute();
        Object request = requestHolder.get();

        assertThat(response.code(), is(200));
        assertThat(request, is(notNullValue()));
        assertThat(
                (Iterable<String>) this.classes.wrap(request).as("org.generated.api.HeaderParamsGetRequest").call("arrayParam").get(),
                contains("val1", "val2", "val3")
        );
    }

    @Test
    public void longArrayParameter() throws Exception {
        Response response = this.client.newCall(new Request.Builder().url(this.undertow.baseUrl() + "/api/header-params")
                .header("intArrayParam", "1")
                .addHeader("intArrayParam", "2")
                .addHeader("intArrayParam", "3")
                .get()
                .build()).execute();
        Object request = requestHolder.get();

        assertThat(response.code(), is(200));
        assertThat(request, is(notNullValue()));
        assertThat(
                (Iterable<Long>) this.classes.wrap(request).as("org.generated.api.HeaderParamsGetRequest").call("intArrayParam").get(),
                contains(1L, 2L, 3L)
        );
    }

    @Test
    public void doubleArrayParameter() throws Exception {
        Response response = this.client.newCall(new Request.Builder().url(this.undertow.baseUrl() + "/api/header-params")
                .header("realArrayParam", "1.3")
                .addHeader("realArrayParam", "2.5")
                .get()
                .build()).execute();
        Object request = requestHolder.get();

        assertThat(response.code(), is(200));
        assertThat(request, is(notNullValue()));
        assertThat(
                (Iterable<Double>) this.classes.wrap(request).as("org.generated.api.HeaderParamsGetRequest").call("realArrayParam").get(),
                contains(1.3d, 2.5d)
        );
    }

    @Test
    public void datetimeArrayParameter() throws Exception {
        Response response = this.client.newCall(new Request.Builder().url(this.undertow.baseUrl() + "/api/header-params")
                .header("datetimeArrayParam", "2018-12-25T23:59:59.123Z")
                .addHeader("datetimeArrayParam", "2018-12-26T23:59:59.123Z")
                .get()
                .build()).execute();
        Object request = requestHolder.get();

        assertThat(response.code(), is(200));
        assertThat(request, is(notNullValue()));
        assertThat(
                (Iterable<LocalDateTime>) this.classes.wrap(request).as("org.generated.api.HeaderParamsGetRequest").call("datetimeArrayParam").get(),
                contains(LocalDateTime.parse("2018-12-25T23:59:59.123"), LocalDateTime.parse("2018-12-26T23:59:59.123"))
        );
    }

    @Test
    public void dateArrayParameter() throws Exception {
        Response response = this.client.newCall(new Request.Builder().url(this.undertow.baseUrl() + "/api/header-params")
                .header("dateArrayParam", "2018-12-25")
                .addHeader("dateArrayParam", "2018-12-26")
                .get()
                .build()).execute();
        Object request = requestHolder.get();

        assertThat(response.code(), is(200));
        assertThat(request, is(notNullValue()));
        assertThat(
                (Iterable<LocalDateTime>) this.classes.wrap(request).as("org.generated.api.HeaderParamsGetRequest").call("dateArrayParam").get(),
                contains(LocalDate.parse("2018-12-25"), LocalDate.parse("2018-12-26"))
        );
    }

    @Test
    public void timeArrayParameter() throws Exception {
        Response response = this.client.newCall(new Request.Builder().url(this.undertow.baseUrl() + "/api/header-params")
                .header("timeArrayParam", "22:59:59.123Z")
                .addHeader("timeArrayParam", "23:59:59.123Z")
                .get()
                .build()).execute();
        Object request = requestHolder.get();

        assertThat(response.code(), is(200));
        assertThat(request, is(notNullValue()));
        assertThat(
                (Iterable<LocalDateTime>) this.classes.wrap(request).as("org.generated.api.HeaderParamsGetRequest").call("timeArrayParam").get(),
                contains(LocalTime.parse("22:59:59.123"), LocalTime.parse("23:59:59.123"))
        );
    }

    @Test
    public void boolArrayParameter() throws Exception {
        Response response = this.client.newCall(new Request.Builder().url(this.undertow.baseUrl() + "/api/header-params")
                .header("boolArrayParam", "true")
                .addHeader("boolArrayParam", "false")
                .get()
                .build()).execute();
        Object request = requestHolder.get();

        assertThat(response.code(), is(200));
        assertThat(request, is(notNullValue()));
        assertThat(
                (Iterable<Boolean>) this.classes.wrap(request).as("org.generated.api.HeaderParamsGetRequest").call("boolArrayParam").get(),
                contains(Boolean.TRUE, Boolean.FALSE)
        );
    }

    private void setupProcessor(String handlerMethod) throws Exception {
        this.setupProcessorWithHandler(
                handlerMethod,
                req -> {
                    requestHolder.set(req);
                    return null;
                });
    }
}
