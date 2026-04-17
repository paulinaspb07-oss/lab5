package org.example.commands;

import static org.example.Main.*;
import java.io.BufferedReader;

public class RemoveByIDCommand implements Command{
    @Override
    public void execute(String[] args, BufferedReader consoleReader) {
        if (args.length < 2) throw new IllegalArgumentException("Missing id.");
        int remId = Integer.parseInt(args[1]);
        collectionManager.removeById(remId);
    }
}
