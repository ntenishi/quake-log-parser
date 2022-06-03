package com.app.quakelogparser;

import java.util.*;

public class GameSession implements GameListener {
    public static final String GAME_NAME_PREFIX = "game_";
    private int index;
    private final Map<String, Game> games;
    private Game currentGame;

    public GameSession() {
        index = 1;
        games = new LinkedHashMap<>();
    }

    public Map<String, Game> getGames() {
        return games;
    }

    public void initGame() {
        currentGame = new Game();
        currentGame.registerListener(this);
    }

    public void clientBegin(int id) {
        if (currentGame != null)
            currentGame.clientBegin(id);
    }

    public void clientUserInfoChanged(int id, String name) {
        if (currentGame != null)
            currentGame.clientUserInfoChanged(id, name);
    }

    public void clientDisconnect(int id) {
        if (currentGame != null)
            currentGame.clientDisconnect(id);
    }

    public void playerKilled(int killerID, int killedID, String mod) {
        if (currentGame != null)
            currentGame.playerKilled(killerID, killedID, mod);
    }

    public void exit() {
        currentGame = null;
    }

    @Override
    public void gameStarted() {
        games.put(GAME_NAME_PREFIX + index++, currentGame);
    }
}
