package org.codingmatters.rest.api.generator;

import org.codingmatters.rest.api.tests.utils.FileHelper;
import org.codingmatters.value.objects.spec.Spec;
import org.junit.Rule;
import org.junit.Test;
import org.raml.v2.api.RamlModelBuilder;

import java.util.Arrays;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * Created by nelt on 5/1/17.
 */
public class SimpleResourcesTest {

    @Rule
    public FileHelper fileHelper = new FileHelper();

    @Test
    public void rootResource() throws Exception {
        Spec spec = new ApiGenerator("org.generated.types").generate(new RamlModelBuilder().buildApi(this.fileHelper.fileResource("types/simple-resources.raml")));
        for(String method : Arrays.asList("Get", "Post", "Put", "Delete", "Head", "Patch", "Options")) {
            String request = "RootResource" + method + "Request";
            String response = "RootResource" + method + "Response";
            assertThat(request, spec.valueSpec(request), is(notNullValue()));
            assertThat(response, spec.valueSpec(response), is(notNullValue()));
        }
    }

    @Test
    public void middleResource() throws Exception {
        Spec spec = new ApiGenerator("org.generated.types").generate(new RamlModelBuilder().buildApi(this.fileHelper.fileResource("types/simple-resources.raml")));

        assertThat(spec.valueSpec("MiddleResourceGetRequest"), is(notNullValue()));
        assertThat(spec.valueSpec("MiddleResourceGetResponse"), is(notNullValue()));
    }

    @Test
    public void leafs() throws Exception {
        Spec spec = new ApiGenerator("org.generated.types").generate(new RamlModelBuilder().buildApi(this.fileHelper.fileResource("types/simple-resources.raml")));

        assertThat(spec.valueSpec("FirstResourceGetRequest"), is(notNullValue()));
        assertThat(spec.valueSpec("FirstResourceGetResponse"), is(notNullValue()));

        assertThat(spec.valueSpec("SecondResourceGetRequest"), is(notNullValue()));
        assertThat(spec.valueSpec("SecondResourceGetResponse"), is(notNullValue()));

    }

}
