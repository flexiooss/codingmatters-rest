package org.codingmatters.rest.api.generator.processor;

import okhttp3.Request;
import okhttp3.Response;
import org.codingmatters.rest.api.generator.AbstractProcessorHttpRequestTest;
import org.codingmatters.tests.compile.helpers.helpers.ClassHelper;
import org.codingmatters.tests.compile.helpers.helpers.ObjectHelper;
import org.codingmatters.value.objects.values.ObjectValue;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Array;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by nelt on 6/1/17.
 */
public class ProcessorResponsePayloadTest extends AbstractProcessorHttpRequestTest {

    @Before
    public void setUp() throws Exception {
        ProcessorGeneratorTestHelper helper = new ProcessorGeneratorTestHelper(this.dir, this.fileHelper)
                .setUpWithResource("processor/processor-response.raml");
        this.compiled = helper.compiled();
        this.classes = this.compiled.classLoader();
    }

    @Test
    public void payload() throws Exception {
        this.setupProcessorWithHandler(
                "payloadGetHandler",
                req -> this.createFilledPayloadGetResponse()
        );

        Response response = this.client.newCall(new Request.Builder().url(this.undertow.baseUrl() + "/api/payload")
                .get()
                .build()).execute();
        assertThat(response.code(), is(200));
        assertThat(response.body().string(), is("{\"prop\":\"val\"}"));
    }

    @Test
    public void payload_empty() throws Exception {
        this.setupProcessorWithHandler(
                "payloadGetHandler",
                req -> this.createNullPayloadGetResponse()
        );

        Response response = this.client.newCall(new Request.Builder().url(this.undertow.baseUrl() + "/api/payload")
                .get()
                .build()).execute();
        assertThat(response.code(), is(200));
        assertThat(response.body().string(), is(""));
    }

    private Object createFilledPayloadGetResponse() {
        Object response = null;
        try {
            ObjectHelper payloadBuilder = this.classes.get("org.generated.types.Resp$Builder").newInstance();
            ObjectHelper payload = payloadBuilder.call("prop", String.class).with("val").call("build");
            ObjectHelper status200 = this.classes.get("org.generated.api.payloadgetresponse.Status200$Builder").newInstance()
                    .call("payload", this.classes.get("org.generated.types.Resp").get()).with(payload.get())
                    .call("build");
            response = this.classes.get("org.generated.api.PayloadGetResponse$Builder").newInstance()
                    .call("status200", this.classes.get("org.generated.api.payloadgetresponse.Status200").get()).with(status200.get())
                    .call("build")
                    .get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(response);
        return response;
    }

    private Object createNullPayloadGetResponse() {
        Object response = null;
        try {
            Object status200Builder = this.compiled.getClass("org.generated.api.payloadgetresponse.Status200$Builder").newInstance();
            Object status200 = this.compiled.on(status200Builder).invoke("build");
            Object builder = this.compiled.getClass("org.generated.api.PayloadGetResponse$Builder").newInstance();
            this.compiled.on(builder).invoke("status200", this.compiled.getClass("org.generated.api.payloadgetresponse.Status200")).with(status200);
            response = this.compiled.on(builder).invoke("build");
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(response);
        return response;
    }



    @Test
    public void payloadList() throws Exception {
        this.setupProcessorWithHandler(
                "payloadListGetHandler",
                req -> this.createFilledPayloadListGetResponse()
        );

        Response response = this.client.newCall(new Request.Builder().url(this.undertow.baseUrl() + "/api/payload-list")
                .get()
                .build()).execute();
        assertThat(response.code(), is(200));
        assertThat(response.body().string(), is("[{\"prop\":\"val\"}]"));
    }



    private Object createFilledPayloadListGetResponse() {
        Object response = null;
        try {
            Object payloadBuilder = this.compiled.getClass("org.generated.types.Resp$Builder").newInstance();
            this.compiled.on(payloadBuilder).invoke("prop", String.class).with("val");
            Object payload = this.compiled.on(payloadBuilder).invoke("build");

            Object status200Builder = this.compiled.getClass("org.generated.api.payloadlistgetresponse.Status200$Builder").newInstance();
            Object respArray = Array.newInstance(this.compiled.getClass("org.generated.types.Resp"), 1);
            Array.set(respArray, 0, payload);
            this.compiled.on(status200Builder).invoke("payload", respArray.getClass()).with(respArray);
            Object status200 = this.compiled.on(status200Builder).invoke("build");

            Object builder = this.compiled.getClass("org.generated.api.PayloadListGetResponse$Builder").newInstance();
            this.compiled.on(builder).invoke("status200", this.compiled.getClass("org.generated.api.payloadlistgetresponse.Status200")).with(status200);
            response = this.compiled.on(builder).invoke("build");
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(response);
        return response;
    }

    @Test
    public void arbitraryObjectPayload() throws Exception {
        ObjectValue objectValue = ObjectValue.builder()
                .property("prop", p -> p.stringValue("str"))
                .build();

        Object resp = this.classes.get("org.generated.api.ArbitraryObjectGetResponse")
                .call("builder")
                .call("status200", this.classes.get("org.generated.api.arbitraryobjectgetresponse.Status200").get())
                    .with(this.classes.get("org.generated.api.arbitraryobjectgetresponse.Status200")
                        .call("builder")
                        .call("payload", ObjectValue.class).with(objectValue)
                        .call("build")
                        .get())
                .call("build").get();


        this.setupProcessorWithHandler(
                "arbitraryObjectGetHandler",
                req -> resp
        );

        Response response = this.client.newCall(new Request.Builder().url(this.undertow.baseUrl() + "/api/arbitrary-object")
                .get()
                .build()).execute();


        assertThat(response.code(), is(200));
        assertThat(response.body().string(), is("{\"prop\":\"str\"}"));
    }

    @Test
    public void arbitraryObjectArrayPayload() throws Exception {
        this.fileHelper.printJavaContent("", this.dir.getRoot());

        ClassHelper objectValueClass = this.classes.get(ObjectValue.class.getName());

        ObjectValue objectValue = ObjectValue.builder()
                .property("prop", p -> p.stringValue("str"))
                .build();

        Object resp = this.classes.get("org.generated.api.ArbitraryObjectArrayGetResponse")
                .call("builder")
                .call("status200", this.classes.get("org.generated.api.arbitraryobjectarraygetresponse.Status200").get())
                    .with(this.classes.get("org.generated.api.arbitraryobjectarraygetresponse.Status200")
                        .call("builder")
                        .call("payload", objectValueClass.array().get())
                            .with(objectValueClass.array().newArray(objectValue).get())
                        .call("build")
                        .get())
                .call("build").get();


        this.setupProcessorWithHandler(
                "arbitraryObjectArrayGetHandler",
                req -> resp
        );

        Response response = this.client.newCall(new Request.Builder().url(this.undertow.baseUrl() + "/api/arbitrary-object-array")
                .get()
                .build()).execute();


        assertThat(response.code(), is(200));
        assertThat(response.body().string(), is("[{\"prop\":\"str\"}]"));
    }
}
