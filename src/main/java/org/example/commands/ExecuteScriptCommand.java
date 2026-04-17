package org.example.commands;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import static org.example.Main.*;import static org.example.Main.*;

public class ExecuteScriptCommand implements Command{
    @Override
    public void execute(String[] args, BufferedReader consoleReader) throws IOException {
        if (scriptDepth >= MAX_SCRIPT_DEPTH) {
            System.err.println("Maximum script recursion depth exceeded.");
            return;
        }
        scriptDepth++;
        if (args.length < 2) throw new IllegalArgumentException("Missing file name.");
        String scriptFileName = args[1];
        try (BufferedReader scriptReader = new BufferedReader(new InputStreamReader(new FileInputStream(scriptFileName), "UTF-8"))) {
            String line;
            while ((line = scriptReader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                processCommand(line, scriptReader);
            }
        } catch (IOException e) {
            System.err.println("Error reading script: " + e.getMessage());
        } finally {
            scriptDepth--;
        }
    }
}
