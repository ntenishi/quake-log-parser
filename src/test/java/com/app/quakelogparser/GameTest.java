package com.app.quakelogparser;

import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GameTest {

    @Test void game_should_not_be_started_when_there_is_only_one_player() {
        // given
        var game = new Game();

        // when
        game.clientUserInfoChanged(1, "player1");
        game.clientBegin(1);

        // then
        assertFalse(game.hasStarted());

    }

    @Test void game_should_be_started_when_there_are_more_than_one_player_simultaneously() {
        // given
        var game = new Game();
        var gameListener = mock(GameListener.class);
        game.registerListener(gameListener);

        // when
        game.clientUserInfoChanged(1, "player1");
        game.clientBegin(1);
        game.clientUserInfoChanged(2, "player2");
        game.clientBegin(2);

        // then
        verify(gameListener, times(1)).gameStarted();
        assertTrue(game.hasStarted());
    }

    @Test void player_should_decrement_point_when_killed_by_world() {
        // given
        var game = new Game();

        // when
        game.clientUserInfoChanged(1, "player1");
        game.clientBegin(1);
        game.playerKilled(1022, 1, "MOD_TRIGGER_HURT");

        // then
        assertEquals(-1, game.getPlayers().stream().findFirst().get().getScore());
    }

    @Test void player_should_decrement_point_when_killed_yourself() {
        // given
        var game = new Game();

        // when
        game.clientUserInfoChanged(1, "player1");
        game.clientBegin(1);
        game.playerKilled(1, 1, "MOD_ROCKET_SPLASH");

        // then
        assertEquals(-1, game.getPlayers().stream().findFirst().get().getScore());
    }

    @Test void player_should_increment_point_when_kills_another_player() {
        // given
        var game = new Game();

        // when
        game.clientUserInfoChanged(1, "player1");
        game.clientBegin(1);
        game.playerKilled(1, 2, "MOD_ROCKET_SPLASH");

        // then
        assertEquals(1, game.getPlayers().stream().findFirst().get().getScore());
    }

    @Test void test_kill_by_means() {
        // given
        var game = new Game();

        // when
        game.clientUserInfoChanged(1, "player1");
        game.clientBegin(1);
        game.playerKilled(1, 2, "MOD_ROCKET_SPLASH");

        // then
        assertEquals(Map.of("MOD_ROCKET_SPLASH", 1), game.getKillsByMeans());
    }

    @Test void test_players_list() {
        // given
        var game = new Game();

        // when
        game.clientUserInfoChanged(1, "player1");
        game.clientBegin(1);
        game.clientUserInfoChanged(2, "player2");
        game.clientUserInfoChanged(3, "player3");
        game.clientBegin(3);

        // then
        assertEquals(Set.of("player1", "player3"), game.getPlayersName());
    }

    @Test void test_total_kills() {
        // given
        var game = new Game();

        // when
        game.playerKilled(1, 1, "");
        game.playerKilled(1, 2, "");
        game.playerKilled(1022, 1, "");

        // then
        assertEquals(3, game.getTotalKills());
    }

    @Test void test_kills_ranking() {
        // given
        var game = new Game();

        // when
        game.clientUserInfoChanged(1, "player1");
        game.clientBegin(1);
        game.clientUserInfoChanged(2, "player2");
        game.clientBegin(2);
        game.clientUserInfoChanged(2, "player2new");
        game.playerKilled(1, 1, "");
        game.playerKilled(1, 2, "");
        game.playerKilled(1022, 1, "");
        game.playerKilled(2, 1, "");
        game.clientDisconnect(1);
        game.clientDisconnect(2);

        // then
        assertEquals(Map.of("player2new", 1, "player1", -1), game.getKills());
    }

}