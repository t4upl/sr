package org.example.service;

import java.util.ArrayList;
import java.util.List;

class BoardImpl implements Board {

    protected List<GameDto> games = new ArrayList<>();

    @Override
    public void startGame(String homeTeam, String awayTeam) {
        games.add(GameDto.builder()
                        .homeTeam(homeTeam)
                        .awayTeam(awayTeam)
                        .homeScore(0)
                        .awayScore(0)
                .build());
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
