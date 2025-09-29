package org.example.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BoardImplTest {

    private Board board;

    @BeforeEach
    void setUp() {
        board = new BoardTestImpl();
    }


    @Test
    void emptyTest() {
        System.out.println("WORKS");
    }
}