package com.app.quakelogparser;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameSessionTest {

    @Test
    void game_should_be_saved_when_there_were_more_than_one_player_simultaneously() {
        // given
        var gameSession = new GameSession();

        // when
        gameSession.initGame();
        gameSession.clientUserInfoChanged(1, "player1");
        gameSession.clientBegin(1);
        gameSession.clientUserInfoChanged(2, "player2");
        gameSession.clientBegin(2);
        gameSession.playerKilled(1, 2, "");

        // then
        assertEquals(1, gameSession.getGames().size());
    }

    @Test
    void game_should_not_be_saved_when_there_were_not_more_than_one_player_simultaneously() {
        // given
        var gameSession = new GameSession();

        // when
        gameSession.initGame();
        gameSession.clientUserInfoChanged(1, "player1");
        gameSession.clientBegin(1);
        gameSession.clientDisconnect(1);
        gameSession.clientUserInfoChanged(2, "player2");
        gameSession.clientBegin(2);

        // then
        assertEquals(0, gameSession.getGames().size());
    }

    @Test
    void game_should_not_handle_further_event_when_there_were_an_exit_event() {
        // given
        var gameSession = new GameSession();

        // when
        gameSession.initGame();
        gameSession.clientUserInfoChanged(1, "player1");
        gameSession.clientBegin(1);
        gameSession.clientUserInfoChanged(2, "player2");
        gameSession.clientBegin(2);
        gameSession.exit();
        gameSession.playerKilled(1, 2, "");

        // then
        assertEquals(0, gameSession.getGames().get("game_1").getTotalKills());
    }


}