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
public class LvlNoFlock extends LevelSetup{
    private static final String[] GEOMETRY = {
        "                 ",
        " 0 0 0 0 0 0 0 0 ",
        "                 ",
        " 0 0 0 0 0 0 0 0 ",
        "                 ",
        " 0 0 D 0 0 0 0 0 ",
        "   _ _ _         ",
        " 0|0 0 0|0 0 0 0 ",
        "                 ",
        " 0 0 0 S 0 0 0 0 ",
        "                 ",
        " 0 S 0 0 0 0 0 0 ",
        "                 ",
        " 0 0 B 0 0 0 0 0 ",
        "                 ",
        " 0 0 M 0 0 0 0 0 ",
        "                 ",
    };

    public LvlNoFlock() {
        super(GEOMETRY, // Geometry
              8,        // Normal fence segments
              0,        // Electric fence segments
              "Black sheep don't flock.", PlayingScene.MESSAGE_ATTENTION, PlayingScene.MESSAGE_TIME_FOREVER,
              "Not that there's anything wrong with that.", PlayingScene.MESSAGE_NORMAL, PlayingScene.MESSAGE_TIME_FOREVER);
    }
}
