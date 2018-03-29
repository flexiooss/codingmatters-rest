package org.codingmatters.rest.api.doc;

import net.sourceforge.plantuml.GeneratedImage;
import net.sourceforge.plantuml.SourceFileReader;
import org.codingmatters.tests.compile.FileHelper;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.TestName;
import org.raml.v2.api.RamlModelBuilder;
import org.raml.v2.api.RamlModelResult;

import java.io.File;
import java.io.IOException;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class ApiPumlGeneratorTest {

    @Rule
    public FileHelper fileHelper = new FileHelper();
    @Rule
    public TemporaryFolder dir = new TemporaryFolder();
    @Rule
    public TestName name = new TestName();


    @Test
    public void base__oneOverallPUML__onePUMLperRessource() throws Exception {
        RamlModelResult model = new RamlModelBuilder().buildApi(this.fileHelper.fileResource("processor/processor-base.raml"));
        new ApiPumlGenerator(model, this.dir.getRoot())
                .generate();

        assertThat(
                this.fileHelper.fileContent("TestAPI-sequence.puml", this.dir.getRoot()),
                is(this.lines(
                        "@startuml",
                        "participant \"Test API\" as api",
                        "",
                        "== Root ==",
                        "",
                        "--> api: <b>GET</b> /root ",
                        "activate api",
                        "deactivate api",
                        "" ,
                        "--> api: <b>DELETE</b> /root " ,
                        "activate api" ,
                        "deactivate api" ,
                        "" ,
                        "== Child1 ==" ,
                        "",
                        "--> api: <b>GET</b> /root/child1 " ,
                        "activate api" ,
                        "deactivate api" ,
                        "" ,
                        "== Subchild ==" ,
                        "",
                        "--> api: <b>GET</b> /root/child1/subchild " ,
                        "activate api" ,
                        "deactivate api" ,
                        "" ,
                        "== Child2 ==" ,
                        "",
                        "--> api: <b>GET</b> /root/child2 " ,
                        "activate api" ,
                        "deactivate api",
                        "@enduml"
                ))
        );

        assertThat(
                this.fileHelper.fileContent("TestAPI-Root-sequence.puml", this.dir.getRoot()),
                is(this.lines(
                        "@startuml",
                        "participant \"Test API\" as api",
                        "",
                        "== Root ==",
                        "",
                        "--> api: <b>GET</b> /root ",
                        "activate api",
                        "deactivate api",
                        "" ,
                        "--> api: <b>DELETE</b> /root " ,
                        "activate api" ,
                        "deactivate api" ,
                        "@enduml"
                ))
        );

        assertThat(
                this.fileHelper.fileContent("TestAPI-Child1-sequence.puml", this.dir.getRoot()),
                is(this.lines(
                        "@startuml",
                        "participant \"Test API\" as api",
                        "" ,
                        "== Child1 ==" ,
                        "",
                        "--> api: <b>GET</b> /root/child1 " ,
                        "activate api" ,
                        "deactivate api" ,
                        "@enduml"
                ))
        );

        assertThat(
                this.fileHelper.fileContent("TestAPI-Subchild-sequence.puml", this.dir.getRoot()),
                is(this.lines(
                        "@startuml",
                        "participant \"Test API\" as api",
                        "" ,
                        "== Subchild ==" ,
                        "",
                        "--> api: <b>GET</b> /root/child1/subchild " ,
                        "activate api" ,
                        "deactivate api" ,
                        "@enduml"
                ))
        );

        assertThat(
                this.fileHelper.fileContent("TestAPI-Child2-sequence.puml", this.dir.getRoot()),
                is(this.lines(
                        "@startuml",
                        "participant \"Test API\" as api",
                        "" ,
                        "== Child2 ==" ,
                        "",
                        "--> api: <b>GET</b> /root/child2 " ,
                        "activate api" ,
                        "deactivate api",
                        "@enduml"
                ))
        );

        this.generatePng("TestAPI-sequence.puml");
        this.generatePng("TestAPI-Root-sequence.puml");
        this.generatePng("TestAPI-Child1-sequence.puml");
        this.generatePng("TestAPI-Subchild-sequence.puml");
        this.generatePng("TestAPI-Child2-sequence.puml");
    }

    @Test
    public void requestResponse() throws Exception {
        RamlModelResult model = new RamlModelBuilder().buildApi(this.fileHelper.fileResource("processor/processor-request-response.raml"));
        new ApiPumlGenerator(model, this.dir.getRoot())
                .generate();

        assertThat(
                this.fileHelper.fileContent("TestAPI-sequence.puml", this.dir.getRoot()),
                is(this.lines(
                        "@startuml",
                        "participant \"Test API\" as api" ,
                        "" ,
                        "== Payload ==" ,
                        "" ,
                        "--> api: <b>POST</b> /payload \\n\\t<b>Req</b>" ,
                        "activate api" ,
                        "<- api: 200:  <b>Resp</b>" ,
                        "deactivate api",
                        "@enduml"
                ))
        );

        this.generatePng("TestAPI-sequence.puml");
    }

    @Test
    public void request() throws Exception {
        RamlModelResult model = new RamlModelBuilder().buildApi(this.fileHelper.fileResource("processor/processor-request.raml"));
        new ApiPumlGenerator(model, this.dir.getRoot())
                .generate();

        assertThat(
                this.fileHelper.fileContent("TestAPI-sequence.puml", this.dir.getRoot()),
                is(this.lines(
                        "@startuml" ,
                        "participant \"Test API\" as api" ,
                        "" ,
                        "== Payload ==" ,
                        "" ,
                        "--> api: <b>POST</b> /payload \\n\\t<b>Req</b>" ,
                        "activate api" ,
                        "deactivate api" ,
                        "" ,
                        "== FilePayload ==" ,
                        "" ,
                        "--> api: <b>POST</b> /file-payload \\n\\t<b>file</b>" ,
                        "activate api" ,
                        "deactivate api" ,
                        "" ,
                        "== ArbitraryObjectPayload ==" ,
                        "" ,
                        "--> api: <b>POST</b> /arbitrary-object-payload \\n\\t<b>object</b>" ,
                        "activate api" ,
                        "deactivate api" ,
                        "" ,
                        "== ArbitraryObjectArrayPayload ==" ,
                        "" ,
                        "--> api: <b>POST</b> /arbitrary-object-array-payload \\n\\t<b>[object]</b>" ,
                        "activate api" ,
                        "deactivate api" ,
                        "" ,
                        "== QueryParams ==" ,
                        "" ,
                        "--> api: <b>GET</b> /query-params?\\n\\t\\t<b>stringParam</b>=string&\\n\\t\\t<b>stringArrayParam</b>=string[] " ,
                        "activate api" ,
                        "deactivate api" ,
                        "" ,
                        "== QueryParamsWithBooleans ==" ,
                        "" ,
                        "--> api: <b>GET</b> /query-params-with-booleans?\\n\\t\\t<b>booleanParam</b>=boolean&\\n\\t\\t<b>booleanArrayParam</b>=boolean[] " ,
                        "activate api" ,
                        "deactivate api" ,
                        "" ,
                        "== UriParams ==" ,
                        "" ,
                        "--> api: <b>GET</b> /uri-param/<b>param</b> " ,
                        "activate api" ,
                        "deactivate api" ,
                        "" ,
                        "== TwoUriParams ==" ,
                        "" ,
                        "--> api: <b>GET</b> /uri-param/<b>param</b>/another/<b>param2</b> " ,
                        "activate api" ,
                        "deactivate api" ,
                        "" ,
                        "== ArrayUriParams ==" ,
                        "" ,
                        "--> api: <b>GET</b> /uri-param/<b>param</b>/another-one/<b>param</b> " ,
                        "activate api" ,
                        "deactivate api" ,
                        "" ,
                        "== HeaderParams ==" ,
                        "" ,
                        "--> api: <b>GET</b> /header-params [\\n\\t\\t<b>stringParam</b>=string\\n\\t\\t<b>arrayParam</b>=string[]\\n\\t\\t]" ,
                        "activate api" ,
                        "deactivate api" ,
                        "@enduml"
                ))
        );

        this.generatePng("TestAPI-sequence.puml");
    }


    @Test
    public void response() throws Exception {
        RamlModelResult model = new RamlModelBuilder().buildApi(this.fileHelper.fileResource("processor/processor-response.raml"));
        new ApiPumlGenerator(model, this.dir.getRoot())
                .generate();

        assertThat(
                this.fileHelper.fileContent("TestAPI-sequence.puml", this.dir.getRoot()),
                is(this.lines(
                        "@startuml", 
                                "participant \"Test API\" as api", 
                                "", 
                                "== Headers ==", 
                                "", 
                                "--> api: <b>GET</b> /headers ", 
                                "activate api", 
                                "<- api: 200: [\\n\\t\\t<b>stringParam</b>=string\\n\\t\\t<b>arrayParam</b>=string[]\\n\\t\\t] ",
                                "deactivate api", 
                                "", 
                                "== Payload ==", 
                                "", 
                                "--> api: <b>GET</b> /payload ", 
                                "activate api", 
                                "<- api: 200:  <b>Resp</b>", 
                                "deactivate api", 
                                "", 
                                "== PayloadList ==", 
                                "", 
                                "--> api: <b>GET</b> /payload-list ", 
                                "activate api", 
                                "<- api: 200:  <b>Resp[]</b>", 
                                "deactivate api", 
                                "", 
                                "== Status ==", 
                                "", 
                                "--> api: <b>GET</b> /status ", 
                                "activate api",
                                "alt",
                                "   <- api: 200:  ",
                                "else",
                                "   <- api: 201:  ",
                                "else",
                                "   <- api: 202:  ",
                                "end",
                                "deactivate api", 
                                "", 
                                "== ArbitraryObject ==", 
                                "", 
                                "--> api: <b>GET</b> /arbitrary-object ", 
                                "activate api", 
                                "<- api: 200:  <b>object</b>", 
                                "deactivate api", 
                                "", 
                                "== ArbitraryObjectArray ==", 
                                "", 
                                "--> api: <b>GET</b> /arbitrary-object-array ", 
                                "activate api", 
                                "<- api: 200:  <b>array</b>", 
                                "deactivate api", 
                                "@enduml"
                ))
        );

        this.generatePng("TestAPI-sequence.puml");
    }

    private void generatePng(String name) throws IOException {
        File outputDir = this.dir.newFolder();
        outputDir.mkdirs();

        SourceFileReader sourceFileReader = new SourceFileReader(new File(this.dir.getRoot(), name), outputDir, "UTF-8");
        if(sourceFileReader.hasError()) {
            for (GeneratedImage generatedImage : sourceFileReader.getGeneratedImages()) {
                System.err.println(generatedImage);
            }
            System.err.println(this.fileHelper.fileContent(name, outputDir));


            throw new AssertionError("failed generating png from puml");
        }
        for (GeneratedImage generatedImage : sourceFileReader.getGeneratedImages()) {
            System.out.println("generated : " + generatedImage);
        }
    }

    private String lines(String ... lines) {
        if(lines == null) return null;
        StringBuilder result = new StringBuilder();
        for (String line : lines) {
            result.append(line).append("\n");
        }

        return result.toString();
    }
}