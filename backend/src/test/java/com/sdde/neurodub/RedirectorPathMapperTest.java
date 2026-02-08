package com.sdde.neurodub;

import com.sdde.neurodub.util.RedirectorPathMapper;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RedirectorPathMapperTest {

    @Test
    void mapsRedirectorPath() {
        Path gameRoot = Path.of("C:/Games/SDDE");
        Path mapped = RedirectorPathMapper.mapToRedirector(gameRoot, Path.of("Text/EN_Gameplay.bin"));
        assertEquals(Path.of("C:/Games/SDDE/RedirectorData/Text/EN_Gameplay.bin"), mapped);
    }
}
