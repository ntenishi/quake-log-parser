package com.app.quakelogparser;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.mockito.Mockito.*;

class ParserTest {
    private final GameSession gameSession = mock(GameSession.class);

    @Test void parse_should_call_initGame() {
        // given
        var lines = new String[] {"  0:00 InitGame: \\sv_floodProtect\\1\\sv_maxPing\\0\\sv_minPing\\0\\sv_maxRate\\10000\\sv_minRate\\0\\sv_hostname\\Code Miner Server\\g_gametype\\0\\sv_privateClients\\2\\sv_maxclients\\16\\sv_allowDownload\\0\\dmflags\\0\\fraglimit\\20\\timelimit\\15\\g_maxGameClients\\0\\capturelimit\\8\\version\\ioq3 1.36 linux-x86_64 Apr 12 2009\\protocol\\68\\mapname\\q3dm17\\gamename\\baseq3\\g_needpass\\0"};
        var parser = new Parser(Arrays.stream(lines), gameSession);

        // when
        parser.parse();

        // then
        verify(gameSession, times(1)).initGame();
    }

    @Test void parse_should_call_exitGame() {
        // given
        var lines = new String[] {" 15:00 Exit: Timelimit hit."};
        var parser = new Parser(Arrays.stream(lines), gameSession);

        // when
        parser.parse();

        // then
        verify(gameSession, times(1)).exit();
    }

    @Test void parse_should_call_clientBegin() {
        // given
        var lines = new String[] {" 20:37 ClientBegin: 2"};
        var parser = new Parser(Arrays.stream(lines), gameSession);

        // when
        parser.parse();

        // then
        verify(gameSession, times(1)).clientBegin(2);
    }

    @Test void parse_should_call_clientUserinfoChanged() {
        // given
        var lines = new String[] {" 20:38 ClientUserinfoChanged: 3 n\\Isgalamido\\t\\0\\model\\uriel/zael\\hmodel\\uriel/zael\\g_redteam\\\\g_blueteam\\\\c1\\5\\c2\\5\\hc\\100\\w\\0\\l\\0\\tt\\0\\tl\\0"};
        var parser = new Parser(Arrays.stream(lines), gameSession);

        // when
        parser.parse();

        // then
        verify(gameSession, times(1)).clientUserInfoChanged(3, "Isgalamido");
    }

    @Test void parse_should_call_clientDisconnect() {
        // given
        var lines = new String[] {" 21:10 ClientDisconnect: 10"};
        var parser = new Parser(Arrays.stream(lines), gameSession);

        // when
        parser.parse();

        // then
        verify(gameSession, times(1)).clientDisconnect(10);
    }

    @Test void parse_should_call_playerKilled() {
        // given
        var lines = new String[] {" 21:42 Kill: 1022 2 22: <world> killed Isgalamido by by by MOD_TRIGGER_HURT"};
        var parser = new Parser(Arrays.stream(lines), gameSession);

        // when
        parser.parse();

        // then
        verify(gameSession, times(1)).playerKilled(1022, 2, "MOD_TRIGGER_HURT");
    }

    @Test void parse_should_call_not_call_any_method() {
        // given
        var lines = new String[] {" 15:00 -----:------------"};
        var parser = new Parser(Arrays.stream(lines), gameSession);

        // when
        parser.parse();

        // then
        verify(gameSession, never()).initGame();
        verify(gameSession, never()).exit();
        verify(gameSession, never()).clientBegin(anyInt());
        verify(gameSession, never()).clientDisconnect(anyInt());
        verify(gameSession, never()).clientUserInfoChanged(anyInt(), anyString());
        verify(gameSession, never()).playerKilled(anyInt(), anyInt(), anyString());
    }

}