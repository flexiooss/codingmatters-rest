package org.codingmatters.rest.io;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class ReferenceCounterTest {

    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();

    private File file;
    private ReferenceCounter referenceCounter;

    @Before
    public void setUp() throws Exception {
        this.file = this.tmp.newFile();
        this.referenceCounter = new ReferenceCounter(this.file);

        assertTrue(this.file.exists());
    }

    @Test
    public void whenFileCreate__thenFileDoesntExists() throws Exception {
        File f = File.createTempFile("temporary", "file");
        assertTrue(f.exists());
    }

    @Test
    public void whenJustCreated__thenReferenceCountIs0() {
        assertThat(this.referenceCounter.count(), is(0));
    }


    @Test
    public void whenIncrementIsCalled__thenReferenceCountIsIncremented() {
        for (int i = 0; i < 10; i++) {
            this.referenceCounter.increment();
        }
        int initial = this.referenceCounter.count();

        this.referenceCounter.increment();
        assertThat(this.referenceCounter.count(), is(initial + 1));
    }

    @Test
    public void whenDecrementIsCalled__thenReferenceCountIsDecremented() {
        for (int i = 0; i < 10; i++) {
            this.referenceCounter.increment();
        }
        int initial = this.referenceCounter.count();

        this.referenceCounter.decrement();
        assertThat(this.referenceCounter.count(), is(initial - 1));
    }

    @Test
    public void givenReferenceCountIs1__whenDecrementIsCalled__thenFileIsDeleted() {
        this.referenceCounter.increment();

        this.referenceCounter.decrement();
        assertFalse(this.file.exists());
    }
}