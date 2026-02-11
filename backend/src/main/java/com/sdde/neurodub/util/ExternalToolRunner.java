package com.sdde.neurodub.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class ExternalToolRunner implements ProcessRunner {
    @Override
    public ProcessResult run(List<String> command) throws IOException, InterruptedException {
        ProcessBuilder builder = new ProcessBuilder(command);
        Process process = builder.start();
        String stdout = readAll(process.getInputStream());
        String stderr = readAll(process.getErrorStream());
        int exitCode = process.waitFor();
        return new ProcessResult(exitCode, stdout, stderr);
    }

    private String readAll(java.io.InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append(System.lineSeparator());
            }
        }
        return output.toString();
    }
}
