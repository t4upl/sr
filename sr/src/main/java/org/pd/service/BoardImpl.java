package org.pd.service;

import lombok.RequiredArgsConstructor;
import org.pd.service.domain.GameDo;

import java.util.*;

@RequiredArgsConstructor
class BoardImpl implements Board {

    protected List<GameDtoSystem> games = new ArrayList<>();
    private final TimeProvider timeProvider;
    private final GameDoMapper gameDoMapper;

    @Override
    public void startGame(String homeTeam, String awayTeam) {
        validateStartGame(homeTeam, awayTeam);
        games.add(createGame(homeTeam, awayTeam));
    }

    private GameDtoSystem createGame(String homeTeam, String awayTeam) {
        GameDo gameDo = GameDo.builder()
                .homeTeam(homeTeam)
                .awayTeam(awayTeam)
                .homeScore(0)
                .awayScore(0)
                .build();

        return GameDtoSystem.builder()
                .gameDo(gameDo)
                .metadata(new GameDtoSystem.Metadata(this.timeProvider.getLocalDateTime()))
                .build();
    }

    @Override
    public void finishGame(String homeTeam, String awayTeam) {
        validateFinishGame(homeTeam, awayTeam);
        GameDtoSystem gameDtoSystem = findGameDtoSystem(homeTeam, awayTeam);
        games.remove(gameDtoSystem);
    }

    @Override
    public void updateScore(GameDo gameDo) {
        validateUpdateScore(gameDo);
        GameDo gameDoFound = findGame(gameDo.getHomeTeam(), gameDo.getAwayTeam());
        gameDoFound.updateScore(gameDo.getHomeScore(), gameDo.getAwayScore());
    }

    @Override
    public List<GameDo> getGamesSummary() {
        Comparator<GameDtoSystem> compareByGameStartThenHomeTeam = Comparator.comparing((GameDtoSystem gameDtoSystem) -> gameDtoSystem.getMetadata().getGameStart())
                .thenComparing(gameDtoSystem -> gameDtoSystem.getGameDo().getHomeTeam());
        List<GameDtoSystem> list = this.games.stream().sorted(compareByGameStartThenHomeTeam).toList();
        return mapGameDtoSytemListToGameDo(list);
    }

    private boolean uiniqueGameExists(String homeTeam, String awayTeam) {
        List<GameDtoSystem> list = getListOfGamesGameDtoSystem(homeTeam, awayTeam);
        return list.size() == 1;
    }

    private List<GameDtoSystem> getListOfGamesGameDtoSystem(String homeTeam, String awayTeam) {
        return games.stream()
                .filter(game -> homeTeam.equals(game.getGameDo().getHomeTeam()) && awayTeam.equals(game.getGameDo().getAwayTeam()))
                .toList();
    }

    private GameDtoSystem findGameDtoSystem(String homeTeam, String awayTeam) {
        List<GameDtoSystem> list = getListOfGamesGameDtoSystem(homeTeam, awayTeam);
        if (list.size() != 1) {
            throw new IllegalStateException(String.format("Cannot find unique game by homeTeam [%s], awayTeam [%s] awayTeam", homeTeam, awayTeam));
        }
        return list.getFirst();
    }

    private GameDo findGame(String homeTeam, String awayTeam) {
        GameDtoSystem gameDtoSystem = findGameDtoSystem(homeTeam, awayTeam);
        return this.gameDoMapper.mapGameDtoSytemListToGameDo(List.of(gameDtoSystem)).getFirst();
    }

    private void validateStartGame(String homeTeam, String awayTeam) {
        Set<String> teams = new HashSet<>();
        this.games.forEach(gameDto -> {
            teams.add(gameDto.getGameDo().getHomeTeam());
            teams.add(gameDto.getGameDo().getAwayTeam());
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

        List<GameDtoSystem> listOfGames = getListOfGamesGameDtoSystem(homeTeam, awayTeam);
        if (listOfGames.size() > 1) {
            throw new BusinessException(ErrorMessage.MULTIPLE_GAMES_FOUND);
        }

        if (listOfGames.isEmpty()) {
            throw new BusinessException(ErrorMessage.NO_GAMES_FOUND);
        }
    }

    private void validateUpdateScore(GameDo gameDoRequest) {
        validateUniqueGame(gameDoRequest.getHomeTeam(), gameDoRequest.getAwayTeam());
        validateCannotDecreaseScore(gameDoRequest);
    }

    private void validateCannotDecreaseScore(GameDo gameDoRequest) {
        GameDo gameDo = findGame(gameDoRequest.getHomeTeam(), gameDoRequest.getAwayTeam());
        if (gameDoRequest.getHomeScore() < gameDo.getHomeScore() || gameDoRequest.getAwayScore() < gameDo.getAwayScore()) {
            throw new BusinessException(ErrorMessage.CANNOT_DECREASE_SCORE);
        }
    }

    protected List<GameDo> mapGameDtoSytemListToGameDo(List<GameDtoSystem> games) {
        return this.gameDoMapper.mapGameDtoSytemListToGameDo(games);
    }

}
