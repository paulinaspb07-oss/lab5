package org.example.commands;

import java.io.BufferedReader;
import static org.example.Main.*;

public class FilterStartsWithNameCommand implements Command{
    @Override
    public void execute(String[] args, BufferedReader consoleReader) throws Exception {
        if (args.length < 2) throw new IllegalArgumentException("Missing substring.");
        collectionManager.filterStartsWithName(args[1]);
    }
}
