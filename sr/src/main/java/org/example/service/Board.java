package org.example.service;

import java.util.List;

public interface Board {

    void startGame(String homeTeam, String awayTeam);
    void finishGame(String homeTeam, String awayTeam);
    void updateScore(GameDto gameDto);
    List<GameDto> getGamesSummary();

}
