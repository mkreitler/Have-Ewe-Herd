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

public class LvlChainReaction extends LevelSetup{
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
        " 0 0 0 0 0 D 0 0 ",
        "   -             ",
        " 0|0 0 S 0 0 0 0 ",
        "             - - ",
        " 0 0 0 0 0 B 0 0 ",
        "                 ",
        " 0 0 0 0 0 0 0 0 ",
        "                 ",
        " 0 0 0 0 0 0 S 0 ",
        "                 ",
        " 0 0 0 0 0 M 0 0 ",
        "                 ",
        " 0 0 S 0 S 0 0 0|",
        "               - ",
        " 0 0 0|0 0 0 0 0 ",
        "     -           ",
    };

    public LvlChainReaction() {
        super(GEOMETRY, // Geometry
              10,        // Normal fence segments
              0,        // Electric fence segments
              "Need a ch-ch-chain reaction.", PlayingScene.MESSAGE_ATTENTION, PlayingScene.MESSAGE_TIME_FOREVER,
              "Chain complete -- now lock 'em up!", PlayingScene.MESSAGE_NORMAL, PlayingScene.MESSAGE_TIME_FOREVER,
              100);
    }
}
