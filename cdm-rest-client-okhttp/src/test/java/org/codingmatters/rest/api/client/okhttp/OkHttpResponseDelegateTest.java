package org.codingmatters.rest.api.client.okhttp;

import okhttp3.*;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class OkHttpResponseDelegateTest {

    @Test
    public void testEncodedHeader() throws Exception {
        Response response = createFakeResponse();
        OkHttpResponseDelegate responseDelegate = new OkHttpResponseDelegate(response);
        assertThat(responseDelegate.header("X-Encoded")[0], is("kéké"));
        assertThat(responseDelegate.header("X-No-Need-Encoding")[0], is("toto"));
        assertThat(responseDelegate.header("X-Encoded-No-Decoded")[0], is("kéké"));
        assertThat(responseDelegate.header("X-Toto")[1], is("plages"));
        assertThat(responseDelegate.header("X-Toto")[0], is("des"));
        assertThat(responseDelegate.header("X-Toto")[2], is("kéké"));
    }

    @Test
    public void givenGetHeaderNames_whenHeaderIsEncodedAndNonCoded_thenGetOnlyOne() throws IOException {
        Response response = createFakeResponse();
        OkHttpResponseDelegate responseDelegate = new OkHttpResponseDelegate(response);
        assertThat(Arrays.stream(responseDelegate.headerNames())
                        .filter(name -> name.equalsIgnoreCase("X-Toto"))
                        .count(),
                is(1L)
        );
    }

    @Test
    public void givenGetHeaderNames_whenHeaderIsEncoded_thenGetNameWithoutStar() throws IOException {
        Response response = createFakeResponse();
        OkHttpResponseDelegate responseDelegate = new OkHttpResponseDelegate(response);
        assertThat(Arrays.stream(responseDelegate.headerNames())
                        .filter(name -> name.equalsIgnoreCase("X-Encoded*"))
                        .count(),
                is(0L)
        );
        assertThat(Arrays.stream(responseDelegate.headerNames())
                        .filter(name -> name.equalsIgnoreCase("X-Encoded"))
                        .count(),
                is(1L)
        );
    }

    @Test
    public void givenGetRawHeaderNames_whenHeaderIsEncoded_thenGetNameWithStar() throws IOException {
        Response response = createFakeResponse();
        OkHttpResponseDelegate responseDelegate = new OkHttpResponseDelegate(response);
        assertThat(Arrays.stream(responseDelegate.rawHeaderNames())
                        .filter(name -> name.equalsIgnoreCase("X-Encoded*"))
                        .count(),
                is(1L)
        );
        assertThat(Arrays.stream(responseDelegate.rawHeaderNames())
                        .filter(name -> name.equalsIgnoreCase("X-Encoded"))
                        .count(),
                is(0L)
        );
        assertThat(Arrays.stream(responseDelegate.rawHeaderNames())
                        .filter(name -> name.equalsIgnoreCase("X-Toto*"))
                        .count(),
                is(1L)
        );
        assertThat(Arrays.stream(responseDelegate.rawHeaderNames())
                        .filter(name -> name.equalsIgnoreCase("X-Toto"))
                        .count(),
                is(1L)
        );
    }

    private Response createFakeResponse() {
        return new Response.Builder()
                .code(200)
                .header("X-Encoded*", "utf-8''k%C3%A9k%C3%A9")
                .header("X-No-Need-Encoding*", "utf-8''toto")
                .header("X-Encoded-No-Decoded", "utf-8''k%C3%A9k%C3%A9")
                .addHeader("X-Toto", "des")
                .addHeader("X-Toto*", "utf-8''plages")
                .addHeader("X-Toto*", "utf-8''k%C3%A9k%C3%A9")
                .request(new Request.Builder().url("https://toto.com").build())
                .protocol(Protocol.HTTP_1_0)
                .message("Hello")
                .body(ResponseBody.create(MediaType.parse("application/json"), "{}"))
                .build();
    }
}