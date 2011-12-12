/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package levels;

import maingame.LevelSetup;
import maingame.PlayingScene;

/**
 *
 * @author mkreitler
 */

public class LvlMazeGames extends LevelSetup{
    // D = Daisy
    // M = Max
    // | = vertical wooden fence (up/down)
    // _ = horizontal wooden fence (side-to-side)
    // : = vertical electric fence
    // . = horizontal electric fence
    // S = sheep
    // B = black sheep
    // L = lamb
    private static final String[] GEOMETRY = {
        "                 ",
        " 0 0 0 0 0 0 0 0 ",
        "                 ",
        " 0 0 0 0 0 0 0 0 ",
        " . -   -   -   - ",
        ":0|0 0 0 0 0 0 0|",
        "   - -       - - ",
        " 0|0 0|0 0 0|0 0|",
        "                 ",
        " 0|B S|0 0 0|S S|",
        "                 ",
        " M|S B|0 0 0|0 D|",
        "   - -       - - ",
        " 0|0 0 0 0 0 0 0|",
        "   -   -   -   - ",
        ":0 0 0 0 0 0 0 0 ",
        " .               ",
    };

    public LvlMazeGames() {
        super(GEOMETRY, // Geometry
              0,        // Normal fence segments
              2,        // Electric fence segments
              "Max will smash those pens.", PlayingScene.MESSAGE_ATTENTION, PlayingScene.MESSAGE_TIME_FOREVER,
              "Can you block them all in?", PlayingScene.MESSAGE_NORMAL, PlayingScene.MESSAGE_TIME_FOREVER,
              100);
    }
}
