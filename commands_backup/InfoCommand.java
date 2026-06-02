package org.example.commands;

import static org.example.Main.*;
import java.io.BufferedReader;

public class InfoCommand implements Command{
    @Override
    public void execute(String[] args, BufferedReader consoleReader) throws Exception {
        System.out.println(collectionManager.getInfo());
    }
}
