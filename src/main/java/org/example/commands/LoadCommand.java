package org.example.commands;

import org.example.Main;
import java.io.BufferedReader;

public class LoadCommand implements Command {
    @Override
    public void execute(String[] args, BufferedReader input) throws Exception {
        if (args.length > 1) {
            Main.loadCollectionFromFile(args[1]);
        } else {
            Main.loadCollectionFromFile();
        }
        System.out.println("Collection loaded.");
    }
}