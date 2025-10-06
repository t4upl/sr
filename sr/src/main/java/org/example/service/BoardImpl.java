package org.example.service;

import java.util.*;

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
        GameDto gameDto = findGame(homeTeam, awayTeam);
        games.remove(gameDto);
    }

    @Override
    public void updateScore(GameDto gameDto) {
        GameDto gameDtoFound = findGame(gameDto.getHomeTeam(), gameDto.getAwayTeam());
        gameDtoFound.updateScore(gameDto.getHomeScore(), gameDto.getAwayScore());
    }

    @Override
    public List<GameDto> getGamesSummary() {
        return List.of();
    }

    private GameDto findGame(String homeTeam, String awayTeam) {
        List<GameDto> list = games.stream()
                .filter(game -> homeTeam.equals(game.getHomeTeam()) && awayTeam.equals(game.getAwayTeam()))
                .toList();

        if (list.size() > 1) {
            throw new BusinessException(ErrorMessage.MULTIPLE_GAMES_FOUND);
        }

        if (list.isEmpty()) {
            throw new BusinessException(ErrorMessage.NO_GAMES_FOUND);
        }

        return list.getFirst();
    }

    private void validateStartGame(String homeTeam, String awayTeam) {
        Set<String> teams = new HashSet<>();
        this.games.forEach(gameDto -> {
            teams.add(gameDto.getHomeTeam());
            teams.add(gameDto.getAwayTeam());
        });

        if (teams.contains(homeTeam) || teams.contains(awayTeam)) {
            throw new BusinessException(ErrorMessage.TEAM_IS_ALREADY_PLAYING);
        }


    }



}
