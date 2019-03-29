package org.codingmatters.rest.api.client.okhttp;

import okhttp3.*;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class OkHttpResponseDelegateTest {

    @Test
    public void testEncodedHeader() throws Exception {
        Response response = createFakeResponse();
        OkHttpResponseDelegate responseDelegate = new OkHttpResponseDelegate( response );
        assertThat( responseDelegate.header( "X-Encoded*" )[0], is( "kéké" ) );
        assertThat( responseDelegate.header( "X-No-Need-Encoding*" )[0], is( "toto" ) );
        assertThat( responseDelegate.header( "X-Encoded-No-Decoded" )[0], is( "utf-8''k%C3%A9k%C3%A9" ) );
    }

    private Response createFakeResponse() {
        return new Response.Builder()
                .code( 200 )
                .header( "X-Encoded*", "utf-8''k%C3%A9k%C3%A9" )
                .header( "X-No-Need-Encoding*", "utf-8''toto" )
                .header( "X-Encoded-No-Decoded", "utf-8''k%C3%A9k%C3%A9" )
                .request( new Request.Builder().url( "https://toto.com" ).build() )
                .protocol( Protocol.HTTP_1_0 )
                .message( "Hello" )
                .body( ResponseBody.create( MediaType.parse( "application/json" ), "{}" ) )
                .build();
    }
}