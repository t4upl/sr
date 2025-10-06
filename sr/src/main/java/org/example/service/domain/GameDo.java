package org.example.service.domain;

import lombok.*;

@Getter
@EqualsAndHashCode
@ToString
@Builder
public class GameDo {
    String homeTeam;
    String awayTeam;
    int homeScore;
    int awayScore;

    public void updateScore(int homeScore, int awayScore) {
        this.homeScore = homeScore;
        this.awayScore = awayScore;
    }

}
