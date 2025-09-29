package org.example.service;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class GameDto {
    String homeTeam;
    String awayTeam;
    int homeScore;
    int awayScore;
}
