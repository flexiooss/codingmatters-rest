package org.codingmatters.rest.api.generator.client;

import org.codingmatters.rest.api.generator.client.support.RequesterClientTestSetup;
import org.codingmatters.tests.compile.FileHelper;
import org.codingmatters.tests.compile.helpers.ClassLoaderHelper;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.rules.TemporaryFolder;

public class RequesterClientGeneratorResponseMediaTypesTest {
    public TemporaryFolder dir = new TemporaryFolder();
    public FileHelper fileHelper = new FileHelper();
    public RequesterClientTestSetup support = new RequesterClientTestSetup("processor/processor-media-types.raml", this.dir, this.fileHelper);

    private ClassLoaderHelper classes;

    @Rule
    public RuleChain chain= RuleChain
            .outerRule(this.dir)
            .around(this.fileHelper)
            .around(this.support);

    @Before
    public void setUp() throws Exception {
        this.fileHelper.printJavaContent("", this.dir.getRoot());
        this.classes = this.support.compiled().classLoader();
    }

    @Test
    public void name() throws Exception {
        
    }
}
