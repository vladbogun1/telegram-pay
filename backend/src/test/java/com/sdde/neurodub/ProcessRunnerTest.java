package com.sdde.neurodub;

import com.sdde.neurodub.util.ProcessRunner;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProcessRunnerTest {

    @Test
    void usesMockProcessRunner() throws IOException, InterruptedException {
        ProcessRunner runner = command -> new ProcessRunner.ProcessResult(0, String.join(" ", command), "");
        ProcessRunner.ProcessResult result = runner.run(List.of("sdde", "tool", "--version"));
        assertEquals(0, result.exitCode());
        assertEquals("sdde tool --version", result.stdout());
    }
}
