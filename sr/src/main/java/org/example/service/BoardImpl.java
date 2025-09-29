package org.example.service;

import java.util.ArrayList;
import java.util.List;

class BoardImpl implements Board {

    protected List<GameDto> games = new ArrayList<>();

    @Override
    public void startGame(String homeTeam, String awayTeam) {

    }

    @Override
    public void finishGame(String homeTeam, String awayTeam) {

    }

    @Override
    public void updateScore(GameDto gameDto) {

    }

    @Override
    public List<GameDto> getGamesSummary() {
        return List.of();
    }



}
