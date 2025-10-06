package org.example.service;

import jdk.jfr.MetadataDefinition;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;
import org.example.service.domain.GameDo;

import java.time.LocalDateTime;

@Builder
@Getter
public class GameDtoSystem {

    private GameDo gameDo;
    private Metadata metadata;

    @Value
    public static class Metadata {
        LocalDateTime gameStart;
    }

}
