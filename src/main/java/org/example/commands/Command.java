package org.example.commands;

import java.io.BufferedReader;

public interface Command {
    void execute(String[] args, BufferedReader consoleReader) throws Exception;
}
