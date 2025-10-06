package org.example.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BoardImplTest {

    private BoardTestImpl board;

    private static final String MEXICO = "Mexico";
    private static final String CANADA = "Canada";
    private static final String SPAIN = "Spain";

    @BeforeEach
    void setUp() {
        board = new BoardTestImpl();
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
        GameDto gameDtoRequest = GameDto.builder()
                .homeTeam(MEXICO)
                .awayTeam(CANADA)
                .homeScore(1)
                .awayScore(2)
                .build();

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
        GameDto gameDtoRequest = GameDto.builder()
                .homeTeam(MEXICO)
                .awayTeam(CANADA)
                .homeScore(1)
                .awayScore(2)
                .build();

        //when then
        BusinessException ex = assertThrows(BusinessException.class, () -> board.updateScore(gameDtoRequest));
        assertThat(ex.getBusinessMessage()).isEqualTo(ErrorMessage.NO_GAMES_FOUND.getMessage());
    }

    @Test
    void updateScoreShouldThrowErrorWhenTryingToDecreaseScore() {
        //given
        board.startGame(MEXICO, CANADA);
        GameDto gameDtoRequest = GameDto.builder()
                .homeTeam(MEXICO)
                .awayTeam(CANADA)
                .homeScore(1)
                .awayScore(2)
                .build();
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



}