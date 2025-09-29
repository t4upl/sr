package org.example.service;

import java.util.List;

class BoardTestImpl extends BoardImpl {

    List<GameDto> getGames() {
        return this.games;
    }
}
