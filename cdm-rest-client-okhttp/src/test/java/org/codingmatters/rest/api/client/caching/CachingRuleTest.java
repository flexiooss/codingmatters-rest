package org.codingmatters.rest.api.client.caching;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.codingmatters.rest.api.client.caching.rules.And;
import org.codingmatters.rest.api.client.caching.rules.HeaderNotEmpty;
import org.codingmatters.rest.api.client.caching.rules.MethodMatches;
import org.codingmatters.rest.api.client.caching.rules.UrlMatches;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class CachingRuleTest {

    @Test
    public void whenUrlMatches__thenTrueWhenPatternMatches() throws Exception {
        assertTrue(UrlMatches.regex(".*over/\\w+.rain.bow").matches(new Request.Builder().url("https://some.where/over/the/rain.bow").build()));
        assertFalse(UrlMatches.regex("^(over)").matches(new Request.Builder().url("https://some.where/over/the/rain.bow").build()));
    }

    @Test
    public void givenMethodMatches__whenGET__thenOnlyMatchesGETRequest() throws Exception {
        assertTrue(MethodMatches.GET.matches(new Request.Builder().get().url("https://some.where/over/the/rain.bow").build()));
        assertFalse(MethodMatches.GET.matches(new Request.Builder().head().url("https://some.where/over/the/rain.bow").build()));
        assertFalse(MethodMatches.GET.matches(new Request.Builder().delete().url("https://some.where/over/the/rain.bow").build()));
        assertFalse(MethodMatches.GET.matches(new Request.Builder().post(RequestBody.create("hello", MediaType.get("text/plain"))).url("https://some.where/over/the/rain.bow").build()));
        assertFalse(MethodMatches.GET.matches(new Request.Builder().put(RequestBody.create("hello", MediaType.get("text/plain"))).url("https://some.where/over/the/rain.bow").build()));
        assertFalse(MethodMatches.GET.matches(new Request.Builder().patch(RequestBody.create("hello", MediaType.get("text/plain"))).url("https://some.where/over/the/rain.bow").build()));
    }

    @Test
    public void givenMethodMatches__whenHEAD__thenOnlyMatchesHEADRequest() throws Exception {
        assertFalse(MethodMatches.HEAD.matches(new Request.Builder().get().url("https://some.where/over/the/rain.bow").build()));
        assertTrue(MethodMatches.HEAD.matches(new Request.Builder().head().url("https://some.where/over/the/rain.bow").build()));
        assertFalse(MethodMatches.HEAD.matches(new Request.Builder().delete().url("https://some.where/over/the/rain.bow").build()));
        assertFalse(MethodMatches.HEAD.matches(new Request.Builder().post(RequestBody.create("hello", MediaType.get("text/plain"))).url("https://some.where/over/the/rain.bow").build()));
        assertFalse(MethodMatches.HEAD.matches(new Request.Builder().put(RequestBody.create("hello", MediaType.get("text/plain"))).url("https://some.where/over/the/rain.bow").build()));
        assertFalse(MethodMatches.HEAD.matches(new Request.Builder().patch(RequestBody.create("hello", MediaType.get("text/plain"))).url("https://some.where/over/the/rain.bow").build()));
    }

    @Test
    public void givenMethodMatches__whenDELETE__thenOnlyMatchesDELETERequest() throws Exception {
        assertFalse(MethodMatches.DELETE.matches(new Request.Builder().get().url("https://some.where/over/the/rain.bow").build()));
        assertFalse(MethodMatches.DELETE.matches(new Request.Builder().head().url("https://some.where/over/the/rain.bow").build()));
        assertTrue(MethodMatches.DELETE.matches(new Request.Builder().delete().url("https://some.where/over/the/rain.bow").build()));
        assertFalse(MethodMatches.DELETE.matches(new Request.Builder().post(RequestBody.create("hello", MediaType.get("text/plain"))).url("https://some.where/over/the/rain.bow").build()));
        assertFalse(MethodMatches.DELETE.matches(new Request.Builder().put(RequestBody.create("hello", MediaType.get("text/plain"))).url("https://some.where/over/the/rain.bow").build()));
        assertFalse(MethodMatches.DELETE.matches(new Request.Builder().patch(RequestBody.create("hello", MediaType.get("text/plain"))).url("https://some.where/over/the/rain.bow").build()));
    }

    @Test
    public void givenMethodMatches__whenPOST__thenOnlyMatchesPOSTRequest() throws Exception {
        assertFalse(MethodMatches.POST.matches(new Request.Builder().get().url("https://some.where/over/the/rain.bow").build()));
        assertFalse(MethodMatches.POST.matches(new Request.Builder().head().url("https://some.where/over/the/rain.bow").build()));
        assertFalse(MethodMatches.POST.matches(new Request.Builder().delete().url("https://some.where/over/the/rain.bow").build()));
        assertTrue(MethodMatches.POST.matches(new Request.Builder().post(RequestBody.create("hello", MediaType.get("text/plain"))).url("https://some.where/over/the/rain.bow").build()));
        assertFalse(MethodMatches.POST.matches(new Request.Builder().put(RequestBody.create("hello", MediaType.get("text/plain"))).url("https://some.where/over/the/rain.bow").build()));
        assertFalse(MethodMatches.POST.matches(new Request.Builder().patch(RequestBody.create("hello", MediaType.get("text/plain"))).url("https://some.where/over/the/rain.bow").build()));
    }

    @Test
    public void givenMethodMatches__whenPUT__thenOnlyMatchesPUTRequest() throws Exception {
        assertFalse(MethodMatches.PUT.matches(new Request.Builder().get().url("https://some.where/over/the/rain.bow").build()));
        assertFalse(MethodMatches.PUT.matches(new Request.Builder().head().url("https://some.where/over/the/rain.bow").build()));
        assertFalse(MethodMatches.PUT.matches(new Request.Builder().delete().url("https://some.where/over/the/rain.bow").build()));
        assertFalse(MethodMatches.PUT.matches(new Request.Builder().post(RequestBody.create("hello", MediaType.get("text/plain"))).url("https://some.where/over/the/rain.bow").build()));
        assertTrue(MethodMatches.PUT.matches(new Request.Builder().put(RequestBody.create("hello", MediaType.get("text/plain"))).url("https://some.where/over/the/rain.bow").build()));
        assertFalse(MethodMatches.PUT.matches(new Request.Builder().patch(RequestBody.create("hello", MediaType.get("text/plain"))).url("https://some.where/over/the/rain.bow").build()));
    }

    @Test
    public void givenMethodMatches__whenPATCH__thenOnlyMatchesPATCHRequest() throws Exception {
        assertFalse(MethodMatches.PATCH.matches(new Request.Builder().get().url("https://some.where/over/the/rain.bow").build()));
        assertFalse(MethodMatches.PATCH.matches(new Request.Builder().head().url("https://some.where/over/the/rain.bow").build()));
        assertFalse(MethodMatches.PATCH.matches(new Request.Builder().delete().url("https://some.where/over/the/rain.bow").build()));
        assertFalse(MethodMatches.PATCH.matches(new Request.Builder().post(RequestBody.create("hello", MediaType.get("text/plain"))).url("https://some.where/over/the/rain.bow").build()));
        assertFalse(MethodMatches.PATCH.matches(new Request.Builder().put(RequestBody.create("hello", MediaType.get("text/plain"))).url("https://some.where/over/the/rain.bow").build()));
        assertTrue(MethodMatches.PATCH.matches(new Request.Builder().patch(RequestBody.create("hello", MediaType.get("text/plain"))).url("https://some.where/over/the/rain.bow").build()));
    }

    @Test
    public void given__whenHeaderNotEmpty__thenMatchesWhenNotNull_andNotEmpty() throws Exception {
        assertFalse(HeaderNotEmpty.name("plop").matches(new Request.Builder().get().url("https://some.where/over/the/rain.bow").build()));
        assertFalse(HeaderNotEmpty.name("plop").matches(new Request.Builder().header("plop", "").get().url("https://some.where/over/the/rain.bow").build()));
        assertTrue(HeaderNotEmpty.name("plop").matches(new Request.Builder().header("plop", "plop").get().url("https://some.where/over/the/rain.bow").build()));
    }

    @Test
    public void whenAll__thenMatchesOnlyIfAllMatch() throws Exception {
        assertTrue(And.all(CachingRule.ACCEPT, CachingRule.ACCEPT, CachingRule.ACCEPT).matches(new Request.Builder().get().url("https://some.where/over/the/rain.bow").build()));
        assertFalse(And.all(CachingRule.ACCEPT, CachingRule.DENY, CachingRule.ACCEPT).matches(new Request.Builder().get().url("https://some.where/over/the/rain.bow").build()));
    }
}