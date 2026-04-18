package org.example.commands;

import java.io.BufferedReader;

import static org.example.Main.loadCollectionFromFile;

public class LoadCommand implements Command {
    @Override
    public void execute(String[] args, BufferedReader consoleReader) throws Exception {
        if (args.length > 1 && args[1] != null && !args[1].trim().isEmpty()) {
            loadCollectionFromFile(args[1]);
        } else {
            loadCollectionFromFile();
        }
    }
}
