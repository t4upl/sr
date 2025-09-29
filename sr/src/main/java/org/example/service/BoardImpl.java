package org.example.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class BoardImpl implements Board {

    protected List<GameDto> games = new ArrayList<>();

    @Override
    public void startGame(String homeTeam, String awayTeam) {
        validateStartGame(homeTeam, awayTeam);

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

    private void validateStartGame(String homeTeam, String awayTeam) {
        Set<String> teams = new HashSet<>();
        this.games.forEach(gameDto -> {
            teams.add(gameDto.getHomeTeam());
            teams.add(gameDto.getAwayTeam());
        });

        if (teams.contains(homeTeam) || teams.contains(awayTeam)) {
            throw new BusinessException(ErrorMessage.TEAM_IS_ALREADY_PLAYING.getMessage());
        }


    }



}
