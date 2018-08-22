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
                        "|||",
                        "group '[[#Root-get-method Root GET]]'",
                        "--> api: <b>GET</b> /root ",
                        "activate api",
                        "deactivate api",
                        "end" ,
                        "|||" ,
                        "group '[[#Root-delete-method Root DELETE]]'" ,
                        "--> api: <b>DELETE</b> /root " ,
                        "activate api" ,
                        "deactivate api" ,
                        "end" ,
                        "" ,
                        "== Child1 ==" ,
                        "|||",
                        "group '[[#Child1-get-method Child1 GET]]'",
                        "--> api: <b>GET</b> /root/child1 " ,
                        "activate api" ,
                        "deactivate api" ,
                        "end" ,
                        "" ,
                        "== Subchild ==" ,
                        "|||",
                        "group '[[#Subchild-get-method Subchild GET]]'",
                        "--> api: <b>GET</b> /root/child1/subchild " ,
                        "activate api" ,
                        "deactivate api" ,
                        "end" ,
                        "" ,
                        "== Child2 ==" ,
                        "|||",
                        "group '[[#Child2-get-method Child2 GET]]'",
                        "--> api: <b>GET</b> /root/child2 " ,
                        "activate api" ,
                        "deactivate api",
                        "end" ,
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
                        "|||",
                        "group '[[#Root-get-method Root GET]]'",
                        "--> api: <b>GET</b> /root ",
                        "activate api",
                        "deactivate api",
                        "end" ,
                        "|||" ,
                        "group '[[#Root-delete-method Root DELETE]]'",
                        "--> api: <b>DELETE</b> /root " ,
                        "activate api" ,
                        "deactivate api" ,
                        "end",
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
                        "|||",
                        "group '[[#Child1-get-method Child1 GET]]'",
                        "--> api: <b>GET</b> /root/child1 " ,
                        "activate api" ,
                        "deactivate api" ,
                        "end",
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
                        "|||",
                        "group '[[#Subchild-get-method Subchild GET]]'",
                        "--> api: <b>GET</b> /root/child1/subchild " ,
                        "activate api" ,
                        "deactivate api" ,
                        "end",
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
                        "|||",
                        "group '[[#Child2-get-method Child2 GET]]'",
                        "--> api: <b>GET</b> /root/child2 " ,
                        "activate api" ,
                        "deactivate api",
                        "end",
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
                        "|||" ,
                        "group '[[#Payload-post-method Payload POST]]'",
                        "--> api: <b>POST</b> /payload \\n\\t<b>[[#Req-type Req]]</b>" ,
                        "activate api" ,
                        "<- api: 200: <b>[[#Resp-type Resp]]</b> " ,
                        "deactivate api",
                        "end",
                        "" ,
                        "== already ==" ,
                        "|||" ,
                        "group '[[#Already-post-method already POST]]'",
                        "--> api: <b>POST</b> /already \\n\\t<b>[[#AlreadyDefinedType-type AlreadyDefinedType]]</b>" ,
                        "activate api" ,
                        "<- api: 200: <b>[[#AlreadyDefinedType-type AlreadyDefinedType]]</b> " ,
                        "deactivate api",
                        "end",
                        "" ,
                        "== alreadyArray ==" ,
                        "|||" ,
                        "group '[[#AlreadyArray-post-method alreadyArray POST]]'",
                        "--> api: <b>POST</b> /alreadyArray \\n\\t<b>[[#AlreadyDefinedType-type AlreadyDefinedType]]</b>" ,
                        "activate api" ,
                        "<- api: 200: <b>object[]</b> " ,
                        "deactivate api",
                        "end",
                        "" ,
                        "== alreadyArrayLiteral ==" ,
                        "|||" ,
                        "group '[[#AlreadyArrayLiteral-post-method alreadyArrayLiteral POST]]'",
                        "--> api: <b>POST</b> /alreadyArrayLiteral \\n\\t<b>[[#AlreadyDefinedType-type AlreadyDefinedType]]</b>" ,
                        "activate api" ,
                        "<- api: 200: <b>[[#AlreadyDefinedType-type AlreadyDefinedType]][]</b> " ,
                        "deactivate api",
                        "end",
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
                        "|||" ,
                        "group '[[#Payload-post-method Payload POST]]'",
                        "--> api: <b>POST</b> /payload \\n\\t<b>[[#Req-type Req]]</b>" ,
                        "activate api" ,
                        "deactivate api" ,
                        "end" ,
                        "" ,
                        "== FilePayload ==" ,
                        "|||" ,
                        "group '[[#FilePayload-post-method FilePayload POST]]'" ,
                        "--> api: <b>POST</b> /file-payload \\n\\t<b>file</b>" ,
                        "activate api" ,
                        "deactivate api" ,
                        "end" ,
                        "" ,
                        "== ArbitraryObjectPayload ==" ,
                        "|||" ,
                        "group '[[#ArbitraryObjectPayload-post-method ArbitraryObjectPayload POST]]'" ,
                        "--> api: <b>POST</b> /arbitrary-object-payload \\n\\t<b>object</b>" ,
                        "activate api" ,
                        "deactivate api" ,
                        "end" ,
                        "" ,
                        "== ArbitraryObjectArrayPayload ==" ,
                        "|||" ,
                        "group '[[#ArbitraryObjectArrayPayload-post-method ArbitraryObjectArrayPayload POST]]'" ,
                        "--> api: <b>POST</b> /arbitrary-object-array-payload \\n\\t<b>object[]</b>" ,
                        "activate api" ,
                        "deactivate api" ,
                        "end" ,
                        "" ,
                        "== QueryParams ==" ,
                        "|||" ,
                        "group '[[#QueryParams-get-method QueryParams GET]]'" ,
                        "--> api: <b>GET</b> /query-params?\\n\\t\\t<b>stringParam</b>=string&\\n\\t\\t<b>stringArrayParam</b>=string[] " ,
                        "activate api" ,
                        "deactivate api" ,
                        "end" ,
                        "" ,
                        "== QueryParamsWithBooleans ==" ,
                        "|||" ,
                        "group '[[#QueryParamsWithBooleans-get-method QueryParamsWithBooleans GET]]'" ,
                        "--> api: <b>GET</b> /query-params-with-booleans?\\n\\t\\t<b>booleanParam</b>=boolean&\\n\\t\\t<b>booleanArrayParam</b>=boolean[] " ,
                        "activate api" ,
                        "deactivate api" ,
                        "end" ,
                        "" ,
                        "== UriParams ==" ,
                        "|||" ,
                        "group '[[#UriParams-get-method UriParams GET]]'" ,
                        "--> api: <b>GET</b> /uri-param/{<b>param</b>=string} " ,
                        "activate api" ,
                        "deactivate api" ,
                        "end" ,
                        "" ,
                        "== TwoUriParams ==" ,
                        "|||" ,
                        "group '[[#TwoUriParams-get-method TwoUriParams GET]]'" ,
                        "--> api: <b>GET</b> /uri-param/{<b>param</b>=string}/another/{<b>param2</b>=string} " ,
                        "activate api" ,
                        "deactivate api" ,
                        "end" ,
                        "" ,
                        "== ArrayUriParams ==" ,
                        "|||" ,
                        "group '[[#ArrayUriParams-get-method ArrayUriParams GET]]'" ,
                        "--> api: <b>GET</b> /uri-param/{<b>param</b>=string}/another-one/{<b>param</b>=string} " ,
                        "activate api" ,
                        "deactivate api" ,
                        "end" ,
                        "" ,
                        "== HeaderParams ==" ,
                        "|||" ,
                        "group '[[#HeaderParams-get-method HeaderParams GET]]'" ,
                        "--> api: <b>GET</b> /header-params " +
                                "\\n\\t[\\t<b>stringParam</b>=string" +
                                "\\n\\t\\t<b>arrayParam</b>=string[]" +
                                "\\n\\t\\t<b>intParam</b>=integer" +
                                "\\n\\t\\t<b>intArrayParam</b>=integer[]" +
                                "\\n\\t\\t<b>realParam</b>=number" +
                                "\\n\\t\\t<b>realArrayParam</b>=number[]" +
                                "\\n\\t\\t<b>dateParam</b>=date-only" +
                                "\\n\\t\\t<b>dateArrayParam</b>=date-only[]" +
                                "\\n\\t\\t<b>datetimeParam</b>=datetime-only" +
                                "\\n\\t\\t<b>datetimeArrayParam</b>=datetime-only[]" +
                                "\\n\\t\\t<b>timeParam</b>=time-only" +
                                "\\n\\t\\t<b>timeArrayParam</b>=time-only[]" +
                                "\\n\\t\\t<b>boolParam</b>=boolean" +
                                "\\n\\t\\t<b>boolArrayParam</b>=boolean[]" +
                                "\\n\\t\\t]" ,
                        "activate api" ,
                        "deactivate api" ,
                        "end",
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
                                "|||",
                                "group '[[#Headers-get-method Headers GET]]'",
                                "--> api: <b>GET</b> /headers ", 
                                "activate api", 
                                "<- api: 200:  \\n\\t[\\t<b>stringParam</b>=string\\n\\t\\t<b>arrayParam</b>=string[]\\n\\t\\t]",
                                "deactivate api", 
                                "end",
                                "",
                                "== Payload ==",
                                "|||",
                                "group '[[#Payload-get-method Payload GET]]'",
                                "--> api: <b>GET</b> /payload ",
                                "activate api", 
                                "<- api: 200: <b>[[#Resp-type Resp]]</b> ",
                                "deactivate api", 
                                "end",
                                "",
                                "== PayloadList ==",
                                "|||",
                                "group '[[#PayloadList-get-method PayloadList GET]]'",
                                "--> api: <b>GET</b> /payload-list ",
                                "activate api", 
                                "<- api: 200: <b>[[#Resp-type Resp]][]</b> ",
                                "deactivate api", 
                                "end",
                                "",
                                "== Status ==",
                                "|||",
                                "group '[[#Status-get-method Status GET]]'",
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
                                "end",
                                "",
                                "== ArbitraryObject ==",
                                "|||",
                                "group '[[#ArbitraryObject-get-method ArbitraryObject GET]]'",
                                "--> api: <b>GET</b> /arbitrary-object ",
                                "activate api", 
                                "<- api: 200: <b>object</b> ",
                                "deactivate api", 
                                "end",
                                "",
                                "== ArbitraryObjectArray ==",
                                "|||",
                                "group '[[#ArbitraryObjectArray-get-method ArbitraryObjectArray GET]]'",
                                "--> api: <b>GET</b> /arbitrary-object-array ",
                                "activate api", 
                                "<- api: 200: <b>object[]</b> ",
                                "deactivate api",
                                "end",
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