package org.codingmatters.rest.api.client.okhttp;

import org.codingmatters.rest.api.client.ResponseDelegate;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.*;

public class OkHttpRequesterFactoryTest {

    public static void main(String[] args) {
        OkHttpRequesterFactory reqFactory = new OkHttpRequesterFactory(OkHttpClientWrapper.build(), () -> args[0]);

        while(true) {
            try {
                ResponseDelegate response = reqFactory.create().path("/").get();
                try(InputStream in = response.bodyStream()) {
                    byte [] buffer = new byte[1024];
                    for(int read = in.read(buffer) ; read != -1 ; read = in.read(buffer));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}