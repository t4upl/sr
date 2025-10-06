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
        validateFinishGame(homeTeam, awayTeam);
        GameDto gameDto = findGame(homeTeam, awayTeam);
        games.remove(gameDto);
    }

    @Override
    public void updateScore(GameDto gameDto) {
        validateUpdateScore(gameDto);
        GameDto gameDtoFound = findGame(gameDto.getHomeTeam(), gameDto.getAwayTeam());
        gameDtoFound.updateScore(gameDto.getHomeScore(), gameDto.getAwayScore());
    }

    @Override
    public List<GameDto> getGamesSummary() {
        return this.games;
    }

    private boolean uiniqueGameExists(String homeTeam, String awayTeam) {
        List<GameDto> list = getListOfGames(homeTeam, awayTeam);
        return list.size() == 1;
    }

    private List<GameDto> getListOfGames(String homeTeam, String awayTeam) {
        return games.stream()
                .filter(game -> homeTeam.equals(game.getHomeTeam()) && awayTeam.equals(game.getAwayTeam()))
                .toList();
    }

    private GameDto findGame(String homeTeam, String awayTeam) {
        List<GameDto> list = getListOfGames(homeTeam, awayTeam);
        if (list.size() != 1) {
            throw new IllegalStateException(String.format("Cannot find unique game by homeTeam [%s], awayTeam [%s] awayTeam", homeTeam, awayTeam));
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

    private void validateFinishGame(String homeTeam, String awayTeam) {
        validateUniqueGame(homeTeam, awayTeam);
    }

    private void validateUniqueGame(String homeTeam, String awayTeam) {
        if (uiniqueGameExists(homeTeam, awayTeam)) {
            return;
        }

        List<GameDto> listOfGames = getListOfGames(homeTeam, awayTeam);
        if (listOfGames.size() > 1) {
            throw new BusinessException(ErrorMessage.MULTIPLE_GAMES_FOUND);
        }

        if (listOfGames.isEmpty()) {
            throw new BusinessException(ErrorMessage.NO_GAMES_FOUND);
        }
    }

    private void validateUpdateScore(GameDto gameDtoRequest) {
        validateUniqueGame(gameDtoRequest.getHomeTeam(), gameDtoRequest.getAwayTeam());
        validateCannotDecreaseScore(gameDtoRequest);
    }

    private void validateCannotDecreaseScore(GameDto gameDtoRequest) {
        GameDto gameDto = findGame(gameDtoRequest.getHomeTeam(), gameDtoRequest.getAwayTeam());
        if (gameDtoRequest.getHomeScore() < gameDto.getHomeScore() || gameDtoRequest.getAwayScore() < gameDto.getAwayScore()) {
            throw new BusinessException(ErrorMessage.CANNOT_DECREASE_SCORE);
        }
    }

}
