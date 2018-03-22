package org.codingmatters.rest.api.generator.client;

import com.fasterxml.jackson.core.JsonFactory;
import org.codingmatters.rest.api.client.RequesterFactory;
import org.codingmatters.rest.api.client.test.TestRequesterFactory;
import org.codingmatters.rest.api.generator.client.support.RequesterClientTestSetup;
import org.codingmatters.tests.compile.FileHelper;
import org.codingmatters.tests.compile.helpers.ClassLoaderHelper;
import org.codingmatters.tests.compile.helpers.helpers.ObjectHelper;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.rules.TemporaryFolder;

import java.util.HashMap;
import java.util.Map;

import static org.codingmatters.rest.api.generator.client.support.ClientGeneratorHelper.API_PACK;
import static org.codingmatters.rest.api.generator.client.support.ClientGeneratorHelper.CLIENT_PACK;
import static org.codingmatters.tests.compile.helpers.ClassLoaderHelper.c;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class RequesterClientGeneratorResponseHeadersTest {
    public TemporaryFolder dir = new TemporaryFolder();
    public FileHelper fileHelper = new FileHelper();
    public RequesterClientTestSetup support = new RequesterClientTestSetup("processor/processor-response.raml", this.dir, this.fileHelper);

    @Rule
    public RuleChain chain= RuleChain
            .outerRule(this.dir)
            .around(this.fileHelper)
            .around(this.support);
    private ClassLoaderHelper classes;

    @Before
    public void setUp() throws Exception {
        this.fileHelper.printJavaContent("", this.dir.getRoot());
        this.fileHelper.printFile(this.dir.getRoot(), "HeadersClient.java");
        this.classes = this.support.compiled().classLoader();
    }


    @Test
    public void headers() throws Exception {
        TestRequesterFactory requesterFactory = new TestRequesterFactory();
        JsonFactory jsonFactory = new JsonFactory();
        String baseUrl = "https://path.to/me";


        Map<String, String[]> headers = new HashMap<>();
        headers.put("stringParam", new String [] {"value"});
        headers.put("arrayParam", new String [] {"v1", "v2"});
        requesterFactory.nextResponse(TestRequesterFactory.Method.GET, 200, null, headers);

        ObjectHelper response = this.classes.get(CLIENT_PACK + ".TestAPIRequesterClient")
                .newInstance(RequesterFactory.class, JsonFactory.class, String.class)
                .with(requesterFactory, jsonFactory, baseUrl)
                .call("headers")
                    .call("get", c(API_PACK + ".HeadersGetRequest"))
                    .with(
                            this.classes.get(API_PACK + ".HeadersGetRequest")
                                .call("builder")
                                .call("build")
                                .get()
                    );

        System.out.println(response.get());

        ObjectHelper status200 = response.as(c(API_PACK + ".HeadersGetResponse"))
                .call("status200").as(API_PACK + ".headersgetresponse.Status200");

        assertThat(status200.call("stringParam").get(), is("value"));
        assertThat(status200.call("arrayParam").call("get", int.class).with(0).get(), is("v1"));
        assertThat(status200.call("arrayParam").call("get", int.class).with(1).get(), is("v2"));
    }

}
