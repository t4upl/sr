package org.pd.service;

import org.pd.service.domain.GameDo;

import java.util.List;

public interface GameDoMapper {

    List<GameDo> mapGameDtoSytemListToGameDo(List<GameDtoSystem> games);



}
