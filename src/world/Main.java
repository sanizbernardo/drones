package world;

import engine.GameEngine;
import engine.IWorldRules;

public class Main {

    public static void main(String[] args) {
        try {
            boolean vSync = true;
            IWorldRules worldRules = new TestWorld();
            //create a game engine
            GameEngine gameEng = new GameEngine("GAME", 600, 480, vSync, worldRules);
            //start the game loop
            gameEng.start();
        } catch (Exception excp) {
            excp.printStackTrace();
            System.exit(-1);
        }
    }
}