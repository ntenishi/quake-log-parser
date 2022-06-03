package com.app.quakelogparser;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;

public class Main {
    private static final String DEFAULT_LOG_FILE = "src/main/resources/qgames.log";

    public static void main(String[] args) {
        String file = DEFAULT_LOG_FILE;
        if (args.length > 0) {
            file = args[0];
        }

        try {
            var games = Parser.parse(file);

            var gamesJson = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(games);
            System.out.println(gamesJson);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
