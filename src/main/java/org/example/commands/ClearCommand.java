package org.example.commands;

import java.io.BufferedReader;
import static org.example.Main.*;

public class ClearCommand implements Command{
    @Override
    public void execute(String[] args, BufferedReader consoleReader) throws Exception {
        collectionManager.clear();
    }
}
