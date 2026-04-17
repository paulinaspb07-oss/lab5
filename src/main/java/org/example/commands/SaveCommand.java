package org.example.commands;

import java.io.BufferedReader;
import static org.example.Main.saveCollectionToFile;

public class SaveCommand implements Command{
    @Override
    public void execute(String[] args, BufferedReader consoleReader) throws Exception {
        saveCollectionToFile();
    }
}
