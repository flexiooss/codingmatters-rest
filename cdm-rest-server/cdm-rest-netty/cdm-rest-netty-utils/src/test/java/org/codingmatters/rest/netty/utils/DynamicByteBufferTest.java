package org.codingmatters.rest.netty.utils;

import io.netty.buffer.Unpooled;
import org.junit.After;
import org.junit.Test;

import java.io.InputStream;
import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class DynamicByteBufferTest {

    private static final int MAX_IN_MEMORY = 30 * 1024;

    private final DynamicByteBuffer buffer = new DynamicByteBuffer(MAX_IN_MEMORY);

    @After
    public void tearDown() {
        this.buffer.release();
    }

    private static void assertRead(InputStream stream, byte[] expected) throws Exception {
        byte[] read = stream.readAllBytes();
        assertThat(read.length, is(expected.length));
        assertThat(Arrays.equals(read, expected), is(true));
    }

    private static byte[] bytes(int size) {
        byte[] result = new byte[size];
        for (int i = 0; i < size; i++) {
            result[i] = (byte) (i % 251);
        }
        return result;
    }

    @Test
    public void givenBodyKeptInMemory__whenStreamedTwice__thenBothStreamsReadTheWholeBody() throws Exception {
        byte[] body = bytes(2_000);
        this.buffer.accumulate(Unpooled.wrappedBuffer(body));

        try (InputStream first = this.buffer.stream()) {
            assertRead(first, body);
        }
        try (InputStream second = this.buffer.stream()) {
            assertRead(second, body);
        }
    }

    @Test
    public void givenBodyKeptInMemory__whenTwoStreamsAreOpen__thenTheyAreIndependent() throws Exception {
        byte[] body = bytes(2_000);
        this.buffer.accumulate(Unpooled.wrappedBuffer(body));

        try (InputStream first = this.buffer.stream(); InputStream second = this.buffer.stream()) {
            assertThat(first.read(), is(0));
            assertThat(first.read(), is(1));
            assertRead(second, body);
        }
    }

    @Test
    public void givenBodySpilledToFile__whenStreamedTwice__thenBothStreamsReadTheWholeBody() throws Exception {
        byte[] body = bytes(MAX_IN_MEMORY + 10_000);
        this.buffer.accumulate(Unpooled.wrappedBuffer(body));

        try (InputStream first = this.buffer.stream()) {
            assertThat(first.readAllBytes().length, is(body.length));
        }
        try (InputStream second = this.buffer.stream()) {
            assertThat(second.readAllBytes().length, is(body.length));
        }
    }
}
