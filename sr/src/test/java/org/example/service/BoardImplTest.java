package org.example.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BoardImplTest {

    private BoardTestImpl board;

    private static final String MEXICO = "Mexico";
    private static final String CANADA = "Canada";

    @BeforeEach
    void setUp() {
        board = new BoardTestImpl();
    }


    @Test
    void shouldStartGame() {
        board.startGame(MEXICO, CANADA);



    }
}