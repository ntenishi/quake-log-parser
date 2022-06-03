package com.app.quakelogparser;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.*;
import java.util.stream.Collectors;
import static com.app.quakelogparser.PlayerStatus.*;

@JsonPropertyOrder({"total_kills", "players", "kills", "kills_by_means"})
public class Game {
    public static final int WORLD_ID = 1022;
    private int totalKills;
    private boolean isStarted;
    private final Set<Player> players;
    private final Map<Integer, Player> clients;
    private final Map<String, Integer> killsByMeans;
    private GameListener listener;

    public Game(){
        totalKills = 0;
        isStarted = false;
        players = new HashSet<>();
        clients = new HashMap<>();
        killsByMeans = new HashMap<>();
    }

    public void registerListener(GameListener listener) {
        this.listener = listener;
    }

    public boolean hasStarted() {
        return isStarted;
    }

    @JsonIgnore
    public Set<Player> getPlayers() {
        return players;
    }

    @JsonProperty("players")
    public Set<String> getPlayersName() {
        return players.stream()
                .map(Player::getName)
                .collect(Collectors.toSet());
    }

    @JsonProperty("total_kills")
    public int getTotalKills() {
        return totalKills;
    }

    @JsonProperty("kills_by_means")
    public Map<String, Integer> getKillsByMeans() {
        return killsByMeans;
    }

    public Map<String, Integer> getKills() {
        return players.stream().sorted((p1, p2) -> Integer.compare(p2.getScore(), p1.getScore()))
                .collect(Collectors.toMap(Player::getName, Player::getScore, (e1, e2) -> e1, LinkedHashMap::new));
    }

    public void clientBegin(int id) {
        var player = clients.get(id);
        if (player != null) {
            player.setStatus(ACTIVE);
            players.add(player);
        }

        if (!isStarted && clientsActiveCount() >= 2) {
            isStarted = true;

            if (listener != null)
                listener.gameStarted();
        }
    }

    private int clientsActiveCount() {
        return (int) clients.values().stream()
                .filter(player -> player.getStatus() == ACTIVE)
                .count();
    }

    public void clientUserInfoChanged(int id, String name) {
        if (clients.containsKey(id)) {
            clients.get(id).setName(name);
        } else {
            var player = getOrCreatePlayer(id, name);
            player.setStatus(CONNECTED);
            clients.put(id, getOrCreatePlayer(id, name));
        }
    }

    private Player getOrCreatePlayer(int id, String name) {
        return players.stream()
                .filter(player -> player.getName().equals(name))
                .findFirst()
                .orElse(new Player(id, name));

    }

    public void clientDisconnect(int id) {
        clients.get(id).setStatus(DISCONNECTED);
        clients.remove(id);
    }

    public void playerKilled(int killerID, int killedID, String mod) {
        totalKills++;

        if (killedYourself(killerID, killedID)) {
            addScore(killedID, -1);
        } else {
            addScore(killerID, 1);
        }

        killsByMeans.put(mod, killsByMeans.getOrDefault(mod, 0) + 1);
    }

    private boolean killedYourself(int killerID, int killedID) {
        return killerID == WORLD_ID || killerID == killedID;
    }

    private void addScore(int playerID, int point) {
        var player = clients.get(playerID);
        if (player != null)
            player.addScore(point);
    }

}
