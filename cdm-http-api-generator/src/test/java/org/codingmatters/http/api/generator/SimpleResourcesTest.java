package org.codingmatters.http.api.generator;

import org.codingmatters.value.objects.spec.Spec;
import org.junit.Test;
import org.raml.v2.api.RamlModelBuilder;
import org.raml.v2.api.RamlModelResult;
import org.raml.v2.api.model.v10.api.Api;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Arrays;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * Created by nelt on 5/1/17.
 */
public class SimpleResourcesTest {
    @Test
    public void explore() throws Exception {
        RamlModelResult ramlModel = new RamlModelBuilder().buildApi(this.fileResource("simple-resources.raml"));
        assertThat(ramlModel.hasErrors(), is(false));
        assertThat(ramlModel.isVersion10(), is(true));

        Api api = ramlModel.getApiV10();
        assertThat(api, is(notNullValue()));

        assertThat(api.resources().get(0).displayName().value(), is("RootResource"));
        assertThat(api.resources().get(0).methods().get(0).method(), is("get"));
        assertThat(api.resources().get(0).methods().get(1).method(), is("post"));
        assertThat(api.resources().get(0).methods().get(2).method(), is("put"));
        assertThat(api.resources().get(0).methods().get(3).method(), is("delete"));
        assertThat(api.resources().get(0).methods().get(4).method(), is("head"));
        assertThat(api.resources().get(0).methods().get(5).method(), is("patch"));
        assertThat(api.resources().get(0).methods().get(6).method(), is("options"));
        assertThat(api.resources().get(0).resources().get(0).displayName().value(), is("MiddleResource"));
        assertThat(api.resources().get(0).resources().get(0).methods().get(0).method(), is("get"));
        assertThat(api.resources().get(0).resources().get(0).resources().get(0).displayName().value(), is("FirstResource"));
        assertThat(api.resources().get(0).resources().get(0).resources().get(0).methods().get(0).method(), is("get"));
        assertThat(api.resources().get(0).resources().get(0).resources().get(1).displayName().value(), is("SecondResource"));
        assertThat(api.resources().get(0).resources().get(0).resources().get(1).methods().get(0).method(), is("get"));
    }

    @Test
    public void rootResource() throws Exception {
        Spec spec = new ApiSpecGenerator().generate(new RamlModelBuilder().buildApi(this.fileResource("simple-resources.raml")));
        for(String method : Arrays.asList("Get", "Post", "Put", "Delete", "Head", "Patch", "Options")) {
            String request = "RootResource" + method + "Request";
            String response = "RootResource" + method + "Response";
            assertThat(request, spec.valueSpec(request), is(notNullValue()));
            assertThat(response, spec.valueSpec(response), is(notNullValue()));
        }
    }

    @Test
    public void middleResource() throws Exception {
        Spec spec = new ApiSpecGenerator().generate(new RamlModelBuilder().buildApi(this.fileResource("simple-resources.raml")));

        assertThat(spec.valueSpec("MiddleResourceGetRequest"), is(notNullValue()));
        assertThat(spec.valueSpec("MiddleResourceGetResponse"), is(notNullValue()));
    }

    @Test
    public void leafs() throws Exception {
        Spec spec = new ApiSpecGenerator().generate(new RamlModelBuilder().buildApi(this.fileResource("simple-resources.raml")));

        assertThat(spec.valueSpec("FirstResourceGetRequest"), is(notNullValue()));
        assertThat(spec.valueSpec("FirstResourceGetResponse"), is(notNullValue()));

        assertThat(spec.valueSpec("SecondResourceGetRequest"), is(notNullValue()));
        assertThat(spec.valueSpec("SecondResourceGetResponse"), is(notNullValue()));

    }

    private File fileResource(String resource) throws URISyntaxException {
        return new File(Thread.currentThread().getContextClassLoader().getResource(resource).toURI());
    }
}
