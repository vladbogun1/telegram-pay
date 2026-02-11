package com.sdde.neurodub;

import com.sdde.neurodub.util.ChecksumUtil;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ChecksumUtilTest {

    @Test
    void computesSha256() throws Exception {
        Path tempFile = Files.createTempFile("checksum", ".txt");
        Files.writeString(tempFile, "hello");
        String hash = ChecksumUtil.sha256(tempFile);
        assertEquals("2cf24dba5fb0a30e26e83b2ac5b9e29e1b161e5c1fa7425e73043362938b9824", hash);
    }
}
