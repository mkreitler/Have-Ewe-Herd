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
public class LvlEightBall extends LevelSetup{
    private static final String[] GEOMETRY = {
        "                 ",
        " 0 0 0 0 D 0 0 0 ",
        "   _ _ _ _ _ _ _ ",
        " 0 S S 0 S 0 S S ",
        "                 ",
        " 0 S 0 S 0 S 0 S ",
        "                 ",
        " 0 0 S 0 B 0 S 0 ",
        "                 ",
        " 0 0 0 S 0 S 0 0 ",
        "                 ",
        " 0 0 0 0 S 0 0 0 ",
        "                 ",
        " 0 0 0 0 0 0 0 0 ",
        "                 ",
        " 0 0 0 0 M 0 0 0 ",
        "                 ",
    };

    public LvlEightBall() {
        super(GEOMETRY, // Geometry
              18,        // Normal fence segments
              0,        // Electric fence segments
              "Let's play some 8-ball.", PlayingScene.MESSAGE_ATTENTION, PlayingScene.MESSAGE_TIME_FOREVER,
              "Black sheep...corner picket!", PlayingScene.MESSAGE_NORMAL, PlayingScene.MESSAGE_TIME_FOREVER);
    }
}
