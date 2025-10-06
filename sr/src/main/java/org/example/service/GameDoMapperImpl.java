package org.example.service;

import org.example.service.domain.GameDo;

import java.util.List;

public class GameDoMapperImpl implements GameDoMapper {

    @Override
    public List<GameDo> mapGameDtoSytemListToGameDo(List<GameDtoSystem> games) {
        return games.stream()
                .map(GameDtoSystem::getGameDo)
                .toList();
    }
}
