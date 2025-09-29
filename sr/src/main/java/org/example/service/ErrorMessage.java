package org.example.service;

import lombok.Getter;

public enum ErrorMessage {

    TEAM_IS_ALREADY_PLAYING("Team is already playing"),
    MULTIPLE_GAMES_FOUND("Multiple games found"),
    NO_GAMES_FOUND("No games found");

    @Getter
    private final String message;

    ErrorMessage(String message) {
        this.message = message;
    }
}
