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

public class LvlMarkOfZorro extends LevelSetup{
    private static final String[] GEOMETRY = {
        " - - - - - - - - ",
        " 0 0 0 0 0 0 D|0 ",
        "             -   ",
        " 0 B 0 0 0 0|0 0 ",
        "           -     ",
        " 0 0 B B 0|0 0 0 ",
        "         -       ",
        " 0 0 0 0|0 0 B 0 ",
        "       -         ",
        " 0 0 0|0 0 B 0 0 ",
        "     -           ",
        " 0 0|0 0 0 0 B 0 ",
        "   -             ",
        " 0|0 0 0 0 0 M 0 ",
        " - - - - - - - - ",
        " 0 0 0 0 0 0 0 0 ",
        "                 ",
    };

    public LvlMarkOfZorro() {
        super(GEOMETRY, // Geometry
              6,        // Normal fence segments
              6,        // Electric fence segments
              "The Mark of Zorro.", PlayingScene.MESSAGE_ATTENTION, PlayingScene.MESSAGE_TIME_FOREVER,
              "The Pen is mightier than the sword!", PlayingScene.MESSAGE_NORMAL, PlayingScene.MESSAGE_TIME_FOREVER,
              100);
    }
}
