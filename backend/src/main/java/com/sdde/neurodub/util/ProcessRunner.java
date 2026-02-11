package com.sdde.neurodub.util;

import java.io.IOException;
import java.util.List;

public interface ProcessRunner {
    ProcessResult run(List<String> command) throws IOException, InterruptedException;

    record ProcessResult(int exitCode, String stdout, String stderr) {
    }
}
