/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package levels;

/**
 *
 * @author mkreitler
 */
import maingame.LevelSetup;
import maingame.PlayingScene;
import static pulpcore.image.Colors.*;

public class LvlHardLine1 extends LevelSetup{
    private static final String[] GEOMETRY = {
        "                 ",
        " 0 0 0 D 0 0 0 0 ",
        "                 ",
        " 0 0 0 0 0 0 0 0 ",
        "                 ",
        " 0 0 0 0 0 0 0 0 ",
        "                 ",
        " 0 0 0 0 0 0 0 0 ",
        " _ _ _ _ _ _ _ _ ",
        " 0 0 0 0 0 0 0 0 ",
        "                 ",
        " 0 S 0 0 0 S 0 0 ",
        "                 ",
        " 0 0 0 M 0 0 S 0 ",
        "                 ",
        " 0 0 0 0 0 0 0 0 ",
        "                 ",
    };

    public LvlHardLine1() {
        super(GEOMETRY, // Geometry
              7,       // Normal fence segments
              3,        // Electric fence segments
              "\'Hard Line 1\'", PlayingScene.MESSAGE_NORMAL, PlayingScene.MESSAGE_TIME_FOREVER,
              "Now round 'em up...if you can.", PlayingScene.MESSAGE_NORMAL, PlayingScene.MESSAGE_TIME_FOREVER);
    }
}
