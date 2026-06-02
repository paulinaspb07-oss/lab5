package org.example.commands;

import org.example.Main;
import java.io.BufferedReader;

public class SaveCommand implements Command {
    @Override
    public void execute(String[] args, BufferedReader input) throws Exception {
        if (args.length > 1) {
            Main.saveCollectionToFile(args[1]);
        } else {
            Main.saveCollectionToFile();
        }
        System.out.println("Collection saved.");
    }
}