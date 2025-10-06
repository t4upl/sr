package org.example.service;

import org.example.service.domain.GameDo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BoardImplTest {

    private BoardTestImpl board;
    private TimeProvidertestImpl timeProvider;

    private static final String MEXICO = "Mexico";
    private static final String CANADA = "Canada";
    private static final String SPAIN = "Spain";

    @BeforeEach
    void setUp() {
        timeProvider = new TimeProvidertestImpl();
        board = new BoardTestImpl(timeProvider, new GameDoMapperImpl());
    }

    @Test
    void startGameShouldAddActiveGame() {
        //when
        board.startGame(MEXICO, CANADA);

        //then
        List<GameDo> gameDoList = getGames();
        Optional<GameDo> gameDtoOptional = findByTeams(gameDoList, MEXICO, CANADA);
        assertThat(gameDtoOptional).isPresent();
        GameDo gameDo = gameDtoOptional.get();

        assertThat(gameDo.getHomeScore()).isEqualTo(0);
        assertThat(gameDo.getAwayScore()).isEqualTo(0);
    }

    @Test
    void startGameShouldThrowErrorIfTeamIsAlreadyPlayingAGame() {
        //given
        board.startGame(MEXICO, CANADA);

        //when then
        BusinessException ex = assertThrows(BusinessException.class, () -> board.startGame(MEXICO, SPAIN));

        assertThat(ex.getBusinessMessage()).isEqualTo(ErrorMessage.TEAM_IS_ALREADY_PLAYING.getMessage());
    }

    @Test
    void finishGameShouldFinishActiveGame() {
        //when
        board.startGame(MEXICO, CANADA);

        //then
        board.finishGame(MEXICO, CANADA);

        Optional<GameDo> gameDtoOptional = findByTeams(board.getGameDoList(), MEXICO, CANADA);
        assertThat(gameDtoOptional).isEmpty();
    }

    @Test
    void finishGameShouldThrowErrorWhenNoActiveGame() {
        //when then
        BusinessException ex = assertThrows(BusinessException.class, () -> board.finishGame(MEXICO, SPAIN));
        assertThat(ex.getBusinessMessage()).isEqualTo(ErrorMessage.NO_GAMES_FOUND.getMessage());
    }

    @Test
    void updateScoreShouldUpdateScoreForActiveGame() {
        //given
        board.startGame(MEXICO, CANADA);
        GameDo gameDoRequest = createGameDto();

        //when
        board.updateScore(gameDoRequest);

        //then
        Optional<GameDo> gameDtoOptional = findByTeams((board.getGameDoList()), MEXICO, CANADA);
        assertThat(gameDtoOptional).isPresent();

        GameDo gameDo = gameDtoOptional.get();

        assertThat(gameDo.getHomeScore()).isEqualTo(gameDoRequest.getHomeScore());
        assertThat(gameDo.getAwayScore()).isEqualTo(gameDoRequest.getAwayScore());
    }

    @Test
    void updateScoreShouldThrowErrorWhenNoGameToUpdate() {
        //given
        GameDo gameDoRequest = createGameDto();

        //when then
        BusinessException ex = assertThrows(BusinessException.class, () -> board.updateScore(gameDoRequest));
        assertThat(ex.getBusinessMessage()).isEqualTo(ErrorMessage.NO_GAMES_FOUND.getMessage());
    }

    @Test
    void updateScoreShouldThrowErrorWhenTryingToDecreaseScore() {
        //given
        board.startGame(MEXICO, CANADA);
        GameDo gameDoRequest = createGameDto();
        board.updateScore(gameDoRequest);

        GameDo gameDoRequestDecreaseScore = GameDo.builder()
                .homeTeam(MEXICO)
                .awayTeam(CANADA)
                .homeScore(0)
                .awayScore(2)
                .build();

        //when then
        BusinessException ex = assertThrows(BusinessException.class, () -> board.updateScore(gameDoRequestDecreaseScore));
        assertThat(ex.getBusinessMessage()).isEqualTo(ErrorMessage.CANNOT_DECREASE_SCORE.getMessage());
    }

    @Test
    void getGamesSummaryShouldReturnEmptyListWhenNoGames() {
        //when
        List<GameDo> gamesSummary = board.getGamesSummary();

        //then
        assertThat(gamesSummary).isEmpty();
    }

    @Test
    void getGamesSummaryShouldReturnEmptyListWhenAllGamesFinished() {
        //given
        board.startGame(MEXICO, CANADA);
        board.finishGame(MEXICO, CANADA);

        //when
        List<GameDo> gamesSummary = board.getGamesSummary();

        //then
        assertThat(gamesSummary).isEmpty();
    }

    @Test
    void getGamesSummaryShouldReturnListOfActiveGames() {
        //given
        board.startGame(MEXICO, CANADA);
        GameDo gameDoRequest = createGameDto();
        board.updateScore(gameDoRequest);

        GameDo expectedGameDoResponse = GameDo.builder()
                .homeTeam(gameDoRequest.getHomeTeam())
                .awayTeam(gameDoRequest.getAwayTeam())
                .homeScore(gameDoRequest.getHomeScore())
                .awayScore(gameDoRequest.getAwayScore())
                .build();

        //when
        List<GameDo> gamesSummary = board.getGamesSummary();

        //then
        assertThat(gamesSummary).hasSize(1);
        assertThat(gamesSummary.getFirst()).isEqualTo(expectedGameDoResponse);
    }

    @Test
    void getGamesSummaryShouldReturnListOfActiveGamesSorted() {
        //given
        String brazil = "BRAZIL";
        String germany = "GERMANY";
        String france = "FRANCE";
        String argentina = "ARGENTINA";
        String australia = "AUSTRALIA";

        timeProvider.setCurrentTime(LocalDateTime.of(1990, 1, 2, 3, 4));
        board.startGame(MEXICO, CANADA);

        timeProvider.setCurrentTime(LocalDateTime.of(1990, 1, 2, 3, 5));

        board.startGame(SPAIN, brazil);

        timeProvider.setCurrentTime(LocalDateTime.of(1990, 1, 2, 3, 6));
        board.startGame(germany, france);

        timeProvider.setCurrentTime(LocalDateTime.of(1990, 1, 2, 3, 6));
        board.startGame(argentina, australia);


        GameDo updatedGame = createGameDto(SPAIN, brazil, 3, 4);
        board.updateScore(updatedGame);

        List<String> homeTeamsSorted = List.of(MEXICO, SPAIN, argentina, germany);

        GameDo expectedUpdatedGameResponse = GameDo.builder()
                .homeTeam(updatedGame.getHomeTeam())
                .awayTeam(updatedGame.getAwayTeam())
                .homeScore(updatedGame.getHomeScore())
                .awayScore(updatedGame.getAwayScore())
                .build();

        //when
        List<GameDo> games = board.getGamesSummary();

        //then
        assertThat(games).extracting(GameDo::getHomeTeam).containsExactlyElementsOf(homeTeamsSorted);
        Optional<GameDo> gameDtoOptional = findByTeams(games, SPAIN, brazil);
        assertThat(gameDtoOptional).isPresent();
        GameDo gameDo = gameDtoOptional.get();
        assertThat(gameDo).isEqualTo(expectedUpdatedGameResponse);
    }

    private Optional<GameDo> findByTeams(List<GameDo> games, String homeTeam, String awayTeam) {
        List<GameDo> list = games.stream()
                .filter(game -> homeTeam.equals(game.getHomeTeam()) && awayTeam.equals(game.getAwayTeam()))
                .toList();
        if (list.size() > 1) {
            throw new IllegalStateException(String.format("Searching games [%s] by homeTeam [%s], awayTeam [%s] returned more than one results. At most one expected", games, homeTeam, awayTeam));
        }

        if (list.size() == 1) {
            return Optional.of(list.getFirst());
        }
        return Optional.empty();
    }

    private static GameDo createGameDto(String homeTeam, String awayTeam, int homeScore, int awayScore) {
        return GameDo.builder()
                .homeTeam(homeTeam)
                .awayTeam(awayTeam)
                .homeScore(homeScore)
                .awayScore(awayScore)
                .build();
    }

    private static GameDo createGameDto() {
        return GameDo.builder()
                .homeTeam(MEXICO)
                .awayTeam(CANADA)
                .homeScore(1)
                .awayScore(2)
                .build();
    }

    private List<GameDo> getGames() {
        return board.getGameDoList();
    }

}