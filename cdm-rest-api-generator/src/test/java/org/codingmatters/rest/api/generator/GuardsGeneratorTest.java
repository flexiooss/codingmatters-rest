package org.codingmatters.rest.api.generator;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import org.codingmatters.rest.api.generator.guards.descriptor.Guarded;
import org.codingmatters.rest.api.generator.guards.descriptor.json.GuardedReader;
import org.codingmatters.tests.compile.FileHelper;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.raml.v2.api.RamlModelBuilder;
import org.raml.v2.api.RamlModelResult;

import java.io.File;
import java.io.FileReader;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertThrows;
import static org.hamcrest.Matchers.*;

public class GuardsGeneratorTest {

    @Rule
    public TemporaryFolder dir = new TemporaryFolder();

    @Rule
    public FileHelper fileHelper = new FileHelper();
    private JsonFactory jsonFactory = new JsonFactory();

    @Before
    public void setUp() throws Exception {
        RamlModelResult raml = new RamlModelBuilder().buildApi(this.fileHelper.fileResource("guards/permission-guards.raml"));
        new GuardsGenerator("org.generated", this.dir.getRoot(), this.jsonFactory).generate(raml);
        this.printDirContent(this.dir.getRoot(), "");
    }

    private void printDirContent(File f, String prefix) {
        System.out.println(prefix + "+ " + f.getName());
        if(f.isDirectory()) {
            for (File c : f.listFiles()) {
                this.printDirContent(c, "   ");
            }
        }
    }

    @Test
    public void whileGenerate__thenGuardsFileGenerated() throws Exception {
        File guardsFile = new File(this.dir.getRoot(), "org/generated/permission-guards-api.guards");
        assertThat(guardsFile.exists(), is(true));
        assertThat(guardsFile.isFile(), is(true));
    }

    @Test
    public void while__given__when__then() throws Exception {
        List<Guarded> actual;
        try(JsonParser parser = this.jsonFactory.createParser(new FileReader(new File(this.dir.getRoot(), "org/generated/permission-guards-api.guards")))) {
            actual = Arrays.asList(new GuardedReader().readArray(parser));
        }

        for (Guarded guarded : actual) {
            System.out.println(guarded);
        }

        assertThat(
                actual,
                contains(
                        Guarded.builder()
                                .path("/root/{param}").method("post")
                                .guards("permission::root-perm-1", "permission::root-perm-2")
                                .build(),
                        Guarded.builder()
                                .path("/root/{param}").method("get")
                                .guards("permission::root-perm-1", "permission::root-perm-2", "permission::root-get-perm")
                                .build(),
                        Guarded.builder()
                                .path("/root/{param}/inheritance").method("get")
                                .guards("permission::root-perm-1", "permission::root-perm-2")
                                .build(),
                        Guarded.builder()
                                .path("/root/{param}/enriched-inheritance").method("get")
                                .guards("permission::root-perm-1", "permission::root-perm-2", "permission::enriched-inheritance-perm")
                                .build(),
                        Guarded.builder()
                                .path("/root/{param}/enriched-inheritance/enriched-inheritance-at-method").method("get")
                                .guards("permission::root-perm-1", "permission::root-perm-2", "permission::enriched-inheritance-perm", "permission::enriched-inheritance-at-method-perm")
                                .build(),
                        Guarded.builder()
                                .path("/root/{param}/stop-inheritance/then-add-some-more").method("get")
                                .guards("permissions::added-after-clear-perm", "permissions::added-after-clear-method-perm")
                                .build(),
                        Guarded.builder()
                                .path("/root/{param}/stop-inheritance-and-add").method("get")
                                .guards("permissions::added-when-cleared-perm", "permissions::added-when-cleared-method-perm")
                                .build(),
                        Guarded.builder()
                                .path("/root/{param}/stop-inheritance-at-method").method("put")
                                .guards("permission::root-perm-1", "permission::root-perm-2")
                                .build()
                )
        );

    }
}