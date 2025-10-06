package org.example.service;

import org.example.service.domain.GameDo;

import java.util.List;

public interface Board {

    void startGame(String homeTeam, String awayTeam);
    void finishGame(String homeTeam, String awayTeam);
    void updateScore(GameDo gameDo);
    List<GameDo> getGamesSummary();

}
