package org.codingmatters.rest.netty.utils;

import io.netty.buffer.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class DynamicByteBuffer {
    static private final Logger log = LoggerFactory.getLogger(DynamicByteBuffer.class);

    enum Mode {
        IN_MEMORY,
        TO_FILE
    }

    private Mode currentMode = Mode.IN_MEMORY;
    private final ByteBuf smallBuffer;
    private File temporaryFile;
    private OutputStream temporaryOut;
    private long size = 0L;

    public DynamicByteBuffer(int maxInMemoryCapacity) {
        this.smallBuffer = Unpooled.directBuffer(256, maxInMemoryCapacity);
    }

    public InputStream stream() throws FileNotFoundException {
        if(this.currentMode.equals(Mode.IN_MEMORY)) {
            return new ByteBufInputStream(this.smallBuffer);
        } else {
            return new FileInputStream(this.temporaryFile);
        }
    }

    public void accumulate(ByteBuf src) throws IOException {
        int appendedSize = src.readableBytes();

        if(this.currentMode.equals(Mode.IN_MEMORY)) {
            if (this.smallBuffer.maxWritableBytes() >= appendedSize) {
                log.debug("accumulating {} bytes in mem", appendedSize);
                this.smallBuffer.writeBytes(src);
            } else {
                log.debug("switching to file");
                this.temporaryFile = File.createTempFile("request-body", ".bin", new File(System.getProperty("java.io.tmpdir")));
                this.temporaryFile.createNewFile();
                this.temporaryFile.deleteOnExit();
                this.temporaryOut = new FileOutputStream(this.temporaryFile);
                this.temporaryOut.write(ByteBufUtil.getBytes(this.smallBuffer));
                this.currentMode = Mode.TO_FILE;
            }
        }
        if(this.currentMode.equals(Mode.TO_FILE)) {
            log.debug("accumulating {} bytes to file", appendedSize);
            this.temporaryOut.write(ByteBufUtil.getBytes(src));
        }

        this.size += appendedSize;
        log.debug("size is now {} bytes", this.size);
    }

    public long size() {
        return this.size;
    }

    public void release() {
        this.smallBuffer.release();
        if(this.temporaryOut != null) {
            try {
                this.temporaryOut.flush();
            } catch (IOException e) {
                log.error("[GRAVE] while release dynamic byte buffer, failed flushing temporary output", e);
            }
            try {
                this.temporaryOut.close();
            } catch (IOException e) {
                log.error("[GRAVE] while release dynamic byte buffer, failed closing temporary output", e);
            }
        }
        if(this.temporaryFile != null && this.temporaryFile.exists()) {
            if(! this.temporaryFile.delete()) {
                log.error("[GRAVE] while release dynamic byte buffer, failed deleting temporary file : {}", this.temporaryFile);
            }
        }
    }

}
