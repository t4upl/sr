package org.example.service;

import lombok.Getter;

public enum ErrorMessage {

    TEAM_IS_ALREADY_PLAYING("Team is already playing");

    @Getter
    private final String message;

    ErrorMessage(String message) {
        this.message = message;
    }
}
