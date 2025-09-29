package org.example.service;

import lombok.*;

@Getter
@EqualsAndHashCode
@ToString
@Builder
public class GameDto {
    String homeTeam;
    String awayTeam;
    int homeScore;
    int awayScore;

    public void updateScore(int homeScore, int awayScore) {
        this.homeScore = homeScore;
        this.awayScore = awayScore;
    }

}
