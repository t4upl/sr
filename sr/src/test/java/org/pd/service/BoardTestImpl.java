package org.pd.service;

import org.pd.service.domain.GameDo;

import java.util.List;

class BoardTestImpl extends BoardImpl {


    public BoardTestImpl(TimeProvider timeProvider, GameDoMapper gameDoMapper) {
        super(timeProvider, gameDoMapper);
    }

    List<GameDo> getGameDoList() {
        return this.mapGameDtoSytemListToGameDo(this.games);
    }

}
