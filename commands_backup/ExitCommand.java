package org.example.commands;

import java.io.BufferedReader;

public class ExitCommand implements Command{
    @Override
    public void execute(String[] args, BufferedReader consoleReader) {
        System.out.println("Exiting.");
        System.exit(0);
    }
}
