package org.example.service;

import lombok.Value;

@Value
public class GameDto {
    String homeTeam;
    String awayTeam;
    int homeScore;
    int awayScore;
}
