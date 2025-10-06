package org.example.service;

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
        board = new BoardTestImpl();
        timeProvider = new TimeProvidertestImpl();
    }

    @Test
    void startGameShouldAddActiveGame() {
        //when
        board.startGame(MEXICO, CANADA);

        //then
        Optional<GameDto> gameDtoOptional = findByTeams(board.getGames(), MEXICO, CANADA);
        assertThat(gameDtoOptional).isPresent();
        GameDto gameDto = gameDtoOptional.get();

        assertThat(gameDto.getHomeScore()).isEqualTo(0);
        assertThat(gameDto.getAwayScore()).isEqualTo(0);
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

        Optional<GameDto> gameDtoOptional = findByTeams(board.getGames(), MEXICO, CANADA);
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
        GameDto gameDtoRequest = createGameDto();

        //when
        board.updateScore(gameDtoRequest);

        //then
        Optional<GameDto> gameDtoOptional = findByTeams(board.getGames(), MEXICO, CANADA);
        assertThat(gameDtoOptional).isPresent();

        GameDto gameDto = gameDtoOptional.get();

        assertThat(gameDto.getHomeScore()).isEqualTo(gameDtoRequest.getHomeScore());
        assertThat(gameDto.getAwayScore()).isEqualTo(gameDtoRequest.getAwayScore());
    }

    @Test
    void updateScoreShouldThrowErrorWhenNoGameToUpdate() {
        //given
        GameDto gameDtoRequest = createGameDto();

        //when then
        BusinessException ex = assertThrows(BusinessException.class, () -> board.updateScore(gameDtoRequest));
        assertThat(ex.getBusinessMessage()).isEqualTo(ErrorMessage.NO_GAMES_FOUND.getMessage());
    }

    @Test
    void updateScoreShouldThrowErrorWhenTryingToDecreaseScore() {
        //given
        board.startGame(MEXICO, CANADA);
        GameDto gameDtoRequest = createGameDto();
        board.updateScore(gameDtoRequest);

        GameDto gameDtoRequestDecreaseScore = GameDto.builder()
                .homeTeam(MEXICO)
                .awayTeam(CANADA)
                .homeScore(0)
                .awayScore(2)
                .build();

        //when then
        BusinessException ex = assertThrows(BusinessException.class, () -> board.updateScore(gameDtoRequestDecreaseScore));
        assertThat(ex.getBusinessMessage()).isEqualTo(ErrorMessage.CANNOT_DECREASE_SCORE.getMessage());
    }

    @Test
    void getGamesSummaryShouldReturnEmptyListWhenNoGames() {
        //when
        List<GameDto> gamesSummary = board.getGamesSummary();

        //then
        assertThat(gamesSummary).isEmpty();
    }

    @Test
    void getGamesSummaryShouldReturnEmptyListWhenAllGamesFinished() {
        //given
        board.startGame(MEXICO, CANADA);
        board.finishGame(MEXICO, CANADA);

        //when
        List<GameDto> gamesSummary = board.getGamesSummary();

        //then
        assertThat(gamesSummary).isEmpty();
    }

    @Test
    void getGamesSummaryShouldReturnListOfActiveGames() {
        //given
        board.startGame(MEXICO, CANADA);
        GameDto gameDtoRequest = createGameDto();
        board.updateScore(gameDtoRequest);

        GameDto expectedGameDtoResponse = GameDto.builder()
                .homeTeam(gameDtoRequest.getHomeTeam())
                .awayTeam(gameDtoRequest.getAwayTeam())
                .homeScore(gameDtoRequest.getHomeScore())
                .awayScore(gameDtoRequest.getAwayScore())
                .build();

        //when
        List<GameDto> gamesSummary = board.getGamesSummary();

        //then
        assertThat(gamesSummary).hasSize(1);
        assertThat(gamesSummary.getFirst()).isEqualTo(expectedGameDtoResponse);
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


        GameDto updatedGame = createGameDto(SPAIN, brazil, 3, 4);
        board.updateScore(updatedGame);

        List<String> homeTeamsSorted = List.of(MEXICO, SPAIN, argentina, germany);

        GameDto expectedUpdatedGameResponse = GameDto.builder()
                .homeTeam(updatedGame.getHomeTeam())
                .awayTeam(updatedGame.getAwayTeam())
                .homeScore(updatedGame.getHomeScore())
                .awayScore(updatedGame.getAwayScore())
                .build();

        //when
        List<GameDto> games = board.getGames();

        //then
        assertThat(games).map(GameDto::getHomeTeam).isEqualTo(homeTeamsSorted);
        Optional<GameDto> gameDtoOptional = findByTeams(games, MEXICO, SPAIN);
        assertThat(gameDtoOptional).isPresent();
        GameDto gameDto = gameDtoOptional.get();
        assertThat(gameDto).isEqualTo(expectedUpdatedGameResponse);
    }

    private Optional<GameDto> findByTeams(List<GameDto> games, String homeTeam, String awayTeam) {
        List<GameDto> list = games.stream()
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


    private static GameDto createGameDto(String homeTeam, String awayTeam, int homeScore, int awayScore) {
        return GameDto.builder()
                .homeTeam(homeTeam)
                .awayTeam(awayTeam)
                .homeScore(homeScore)
                .awayScore(awayScore)
                .build();
    }

    private static GameDto createGameDto() {
        return GameDto.builder()
                .homeTeam(MEXICO)
                .awayTeam(CANADA)
                .homeScore(1)
                .awayScore(2)
                .build();
    }

}