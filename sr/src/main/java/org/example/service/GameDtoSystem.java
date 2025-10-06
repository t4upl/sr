package org.example.service;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Value;
import org.example.service.domain.GameDo;

import java.time.LocalDateTime;

@Builder
@Getter
@EqualsAndHashCode
public class GameDtoSystem {

    private GameDo gameDo;
    private Metadata metadata;

    @Value
    public static class Metadata {
        LocalDateTime gameStart;
    }

}
