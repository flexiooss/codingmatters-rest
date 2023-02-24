package org.codingmatters.rest.api.generator.processor;

import okhttp3.Request;
import okhttp3.Response;
import org.codingmatters.rest.api.generator.AbstractProcessorHttpRequestTest;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by nelt on 5/30/17.
 */
public class ProcessorResponseHeadersTest extends AbstractProcessorHttpRequestTest {

    @Before
    public void setUp() throws Exception {
        ProcessorGeneratorTestHelper helper = new ProcessorGeneratorTestHelper(this.dir, this.fileHelper)
                .setUpWithResource("processor/processor-response.raml");
        this.compiled = helper.compiled();
//        this.fileHelper.printJavaContent("", this.dir.getRoot());
        this.classes = this.compiled.classLoader();
    }

    @Test
    public void headersWithSpecialChars() throws Exception {
        this.fileHelper.printFile(this.dir.getRoot(), "TestAPIProcessor.java");
        this.setupProcessorWithHandler(
                "headersWithSpecialCharsGetHandler",
                req -> this.createFilledResponse(
                        "org.generated.api.headerswithspecialcharsgetresponse.Status200",
                        "org.generated.api.HeadersWithSpecialCharsGetResponse$Builder",
                        "stringParam", "val", "arrayParam", new String[] {"val1", "val2"})
        );

        Response response = this.client.newCall(new Request.Builder().url(this.undertow.baseUrl() + "/api/headers-with-specials")
                .get()
                .build()).execute();

        assertThat(response.code(), is(200));
        assertThat(response.headers("string-param"), contains("val"));
        assertThat(response.headers("array-param"), contains("val1", "val2"));
    }

    @Test
    public void stringParams() throws Exception {
        this.setupProcessorWithHandler(
                "headersGetHandler",
                req -> this.createFilledHeadersGetResponse("stringParam", "val", "arrayParam", new String[] {"val1", "val2"})
        );

        Response response = this.client.newCall(new Request.Builder().url(this.undertow.baseUrl() + "/api/headers")
                .get()
                .build()).execute();

        assertThat(response.code(), is(200));
        assertThat(response.headers("stringParam"), contains("val"));
        assertThat(response.headers("arrayParam"), contains("val1", "val2"));
    }

    @Test
    public void intParams() throws Exception {
        this.setupProcessorWithHandler(
                "headersGetHandler",
                req -> this.createFilledHeadersGetResponse("intParam", Long.valueOf(12), "intArrayParam", new Long[] {Long.valueOf(12), Long.valueOf(42)})
        );

        Response response = this.client.newCall(new Request.Builder().url(this.undertow.baseUrl() + "/api/headers")
                .get()
                .build()).execute();

        assertThat(response.code(), is(200));
        assertThat(response.headers("intParam"), contains("12"));
        assertThat(response.headers("intArrayParam"), contains("12", "42"));
    }

    @Test
    public void realParams() throws Exception {
        this.setupProcessorWithHandler(
                "headersGetHandler",
                req -> this.createFilledHeadersGetResponse(
                        "realParam", Double.valueOf(12.42),
                        "realArrayParam", new Double[] {Double.valueOf(12.42), Double.valueOf(42.12)}
                )
        );

        Response response = this.client.newCall(new Request.Builder().url(this.undertow.baseUrl() + "/api/headers")
                .get()
                .build()).execute();

        assertThat(response.code(), is(200));
        assertThat(response.headers("realParam"), contains("12.42"));
        assertThat(response.headers("realArrayParam"), contains("12.42", "42.12"));
    }

    @Test
    public void dateParams() throws Exception {
        this.setupProcessorWithHandler(
                "headersGetHandler",
                req -> this.createFilledHeadersGetResponse(
                        "dateParam", LocalDate.parse("2011-02-09"),
                        "dateArrayParam", new LocalDate[] {LocalDate.parse("2011-02-09"), LocalDate.parse("2013-04-05")}
                )
        );

        Response response = this.client.newCall(new Request.Builder().url(this.undertow.baseUrl() + "/api/headers")
                .get()
                .build()).execute();

        assertThat(response.code(), is(200));
        assertThat(response.headers("dateParam"), contains("2011-02-09"));
        assertThat(response.headers("dateArrayParam"), contains("2011-02-09", "2013-04-05"));
    }

    @Test
    public void datetimeParams() throws Exception {
        this.setupProcessorWithHandler(
                "headersGetHandler",
                req -> this.createFilledHeadersGetResponse(
                        "datetimeParam", LocalDateTime.parse("2011-02-09T12:42:18.132"),
                        "datetimeArrayParam", new LocalDateTime[] {LocalDateTime.parse("2011-02-09T12:42:18.132"), LocalDateTime.parse("2013-04-05T12:42:18.132")}
                )
        );

        Response response = this.client.newCall(new Request.Builder().url(this.undertow.baseUrl() + "/api/headers")
                .get()
                .build()).execute();

        assertThat(response.code(), is(200));
        assertThat(response.headers("datetimeParam"), contains("2011-02-09T12:42:18.132"));
        assertThat(response.headers("datetimeArrayParam"), contains("2011-02-09T12:42:18.132", "2013-04-05T12:42:18.132"));
    }

    @Test
    public void timeParams() throws Exception {
        this.setupProcessorWithHandler(
                "headersGetHandler",
                req -> this.createFilledHeadersGetResponse(
                        "timeParam", LocalTime.parse("12:42:18.132"),
                        "timeArrayParam", new LocalTime[] {LocalTime.parse("12:42:18.132"), LocalTime.parse("12:42:18.987")}
                )
        );

        Response response = this.client.newCall(new Request.Builder().url(this.undertow.baseUrl() + "/api/headers")
                .get()
                .build()).execute();

        assertThat(response.code(), is(200));
        assertThat(response.headers("timeParam"), contains("12:42:18.132"));
        assertThat(response.headers("timeArrayParam"), contains("12:42:18.132", "12:42:18.987"));
    }

    @Test
    public void boolParams() throws Exception {
        this.setupProcessorWithHandler(
                "headersGetHandler",
                req -> this.createFilledHeadersGetResponse(
                        "boolParam", Boolean.FALSE,
                        "boolArrayParam", new Boolean[] {Boolean.TRUE, Boolean.FALSE}
                )
        );

        Response response = this.client.newCall(new Request.Builder().url(this.undertow.baseUrl() + "/api/headers")
                .get()
                .build()).execute();

        assertThat(response.code(), is(200));
        assertThat(response.headers("boolParam"), contains("false"));
        assertThat(response.headers("boolArrayParam"), contains("true", "false"));
    }

    private Object createFilledHeadersGetResponse(String singleProp, Object single, String arrayProp, Object[] array) {
        return this.createFilledResponse(
                "org.generated.api.headersgetresponse.Status200",
                "org.generated.api.HeadersGetResponse$Builder",
                singleProp, single, arrayProp, array);
    }
    private Object createFilledResponse(String resonseStatusClass, String resonseBuilderClass, String singleProp, Object single, String arrayProp, Object[] array) {
        Object response = null;
        try {
            Object status200Builder = this.compiled.getClass(resonseStatusClass + "$Builder").newInstance();
            this.compiled.on(status200Builder).invoke(singleProp, single.getClass()).with(single);
            this.compiled.on(status200Builder).invoke(arrayProp, array.getClass()).with(new Object[] {array});
            Object status200 = this.compiled.on(status200Builder).invoke("build");
            Object builder = this.compiled.getClass(resonseBuilderClass).newInstance();
            this.compiled.on(builder).invoke("status200", this.compiled.getClass(resonseStatusClass)).with(status200);
            response = this.compiled.on(builder).invoke("build");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }


}
