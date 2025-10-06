package org.example.service;

import lombok.Getter;

public enum ErrorMessage {

    TEAM_IS_ALREADY_PLAYING("Team is already playing"),
    MULTIPLE_GAMES_FOUND("Multiple games found"),
    NO_GAMES_FOUND("No games found"),
    CANNOT_DECREASE_SCORE("Games cannot have their score decreased");

    @Getter
    private final String message;

    ErrorMessage(String message) {
        this.message = message;
    }
}
