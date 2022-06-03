package com.app.quakelogparser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Parser {
    private static final int COMMAND_START_INDEX = 7;
    private static final Pattern clientBeginPattern = Pattern.compile("ClientBegin: (?<id>\\d+)");
    private static final Pattern clientUserinfoChangedPattern = Pattern.compile("ClientUserinfoChanged: (?<id>\\d+) n\\\\(?<name>.*?)\\\\");
    private static final Pattern clientDisconnectPattern = Pattern.compile("ClientDisconnect: (?<id>\\d+)");
    private static final Pattern killPattern = Pattern.compile("Kill: (?<killerID>\\d+) (?<killedID>\\d+) .* by (?<mod>\\w*)$");
    private final Stream<String> lines;
    private final GameSession gameSession;

    public static Map<String, Game> parse(String file) throws IOException {
        try (Stream<String> stream = Files.lines(Paths.get(file))) {
            return new Parser(stream, new GameSession()).parse();
        }
    }

    public Parser(Stream<String> lines, GameSession gameSession) {
        this.lines = lines;
        this.gameSession = gameSession;
    }

    public Map<String, Game> parse() {
        lines.forEach(this::parseLine);

        return gameSession.getGames();
    }

    public void parseLine(String line) {
        if (line.startsWith("InitGame:", COMMAND_START_INDEX)) {
            gameSession.initGame();
        } else if (line.startsWith("Exit:", COMMAND_START_INDEX)) {
            gameSession.exit();
        } else if (line.startsWith("ClientBegin:", COMMAND_START_INDEX)) {
            var m = clientBeginPattern.matcher(line);
            if (m.find()) {
                gameSession.clientBegin(Integer.parseInt(m.group("id")));
            }
        } else if (line.startsWith("ClientUserinfoChanged:", COMMAND_START_INDEX)) {
            var m = clientUserinfoChangedPattern.matcher(line);
            if (m.find()) {
                gameSession.clientUserInfoChanged(Integer.parseInt(m.group("id")), m.group("name"));
            }
        } else if (line.startsWith("ClientDisconnect:", COMMAND_START_INDEX)) {
            var m = clientDisconnectPattern.matcher(line);
            if (m.find()) {
                gameSession.clientDisconnect(Integer.parseInt(m.group("id")));
            }
        } else if (line.startsWith("Kill:", COMMAND_START_INDEX)) {
            var m = killPattern.matcher(line);

            if (m.find()) {
                gameSession.playerKilled(Integer.parseInt(m.group("killerID")),
                        Integer.parseInt(m.group("killedID")),
                        m.group("mod"));
            }
        }
    }
}
