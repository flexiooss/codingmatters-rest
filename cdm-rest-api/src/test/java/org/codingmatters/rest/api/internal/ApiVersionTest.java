package org.codingmatters.rest.api.internal;

import org.codingmatters.rest.api.Api;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;

public class ApiVersionTest {

    @Test
    public void givenARelease__whenVersion__thenTheRelease() {
        assertThat(ApiVersion.version("1.6.0", 1763890000000L), is("1.6.0"));
    }

    @Test
    public void givenASnapshot__whenVersion__thenTheTimeOfTheBuild() {
        assertThat(ApiVersion.version("1.6.0-SNAPSHOT", 1763890000000L), is("1.6.0.1763890000000"));
    }

    @Test
    public void givenNoVersion__whenVersion__thenTheTimeOfTheBuild() {
        assertThat(ApiVersion.version(null, 1763890000000L), is("1763890000000"));
    }

    @Test
    public void givenAClassInAJar__whenBuilt__thenTheTimeOfItsEntry_theSameForEveryProcess() {
        long built = ApiVersion.built(Test.class);

        assertThat(built, greaterThan(0L));
        assertThat(ApiVersion.built(Test.class), is(built));
    }

    @Test
    public void givenTheSameClass__whenVersionFromLater__thenTheSameVersion() throws Exception {
        String version = Api.versionFrom(ApiVersionTest.class);
        Thread.sleep(5);

        assertThat(Api.versionFrom(ApiVersionTest.class), is(version));
    }
}
