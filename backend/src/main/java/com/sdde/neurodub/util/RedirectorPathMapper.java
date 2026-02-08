package com.sdde.neurodub.util;

import java.nio.file.Path;

public final class RedirectorPathMapper {
    private RedirectorPathMapper() {
    }

    public static Path mapToRedirector(Path gameRoot, Path originalRelativePath) {
        return gameRoot.resolve("RedirectorData").resolve(originalRelativePath);
    }
}
