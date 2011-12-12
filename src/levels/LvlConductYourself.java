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
public class LvlConductYourself extends LevelSetup{
    private static final String[] GEOMETRY = {
        "     .           ",
        " 0 0|0|0 0 0 0 0 ",
        "                 ",
        " 0 0|0|0 0 0 0 0 ",
        "                 ",
        " 0 0|0|0 0 0 0 0 ",
        "           _ _ _ ",
        " 0 0 0 0 S S S 0:",
        "           _ _ _ ",
        " 0 0 S 0 0 0 0 0 ",
        "                 ",
        " 0 0 S 0 0 0 0 0 ",
        "                 ",
        " 0 0 S 0 0 0 0 0 ",
        "                 ",
        " M 0 0 0 0 0 D 0 ",
        "                 ",
    };

    public LvlConductYourself() {
        super(GEOMETRY, // Geometry
              2,        // Normal fence segments
              2,        // Electric fence segments
              "Conduct yourself appropriately.", PlayingScene.MESSAGE_NORMAL, PlayingScene.MESSAGE_TIME_FOREVER,
              "Box 'em up!", PlayingScene.MESSAGE_NORMAL, PlayingScene.MESSAGE_TIME_FOREVER);
    }
}
