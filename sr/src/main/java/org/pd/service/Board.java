package org.pd.service;

import org.pd.service.domain.GameDo;

import java.util.List;

public interface Board {

    void startGame(String homeTeam, String awayTeam);
    void finishGame(String homeTeam, String awayTeam);
    void updateScore(GameDo gameDo);
    List<GameDo> getGamesSummary();

}
