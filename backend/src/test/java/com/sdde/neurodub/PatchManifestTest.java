package com.sdde.neurodub;

import com.sdde.neurodub.util.PatchManifest;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PatchManifestTest {

    @Test
    void setsFields() {
        PatchManifest manifest = new PatchManifest();
        manifest.setPatchId("patch-1");
        manifest.setGamePathHash("hash");
        manifest.setToolVersions(Map.of("wwiseutil", "external"));
        manifest.setCreatedAt(Instant.parse("2024-01-01T00:00:00Z"));

        assertEquals("patch-1", manifest.getPatchId());
        assertEquals("hash", manifest.getGamePathHash());
        assertEquals("external", manifest.getToolVersions().get("wwiseutil"));
        assertEquals(Instant.parse("2024-01-01T00:00:00Z"), manifest.getCreatedAt());
    }
}
