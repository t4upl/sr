package org.example.service;

import org.example.service.domain.GameDo;

import java.util.List;

public interface GameDoMapper {

    List<GameDo> mapGameDtoSytemListToGameDo(List<GameDtoSystem> games);



}
