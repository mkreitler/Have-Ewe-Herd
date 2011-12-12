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

public class LvlHardLine2 extends LevelSetup{
    private static final String[] GEOMETRY = {
        "                 ",
        " 0 0 0 D|0 0 0 0 ",
        "                 ",
        " 0 0 0 0|0 0 0 0 ",
        "         _   _   ",
        " 0 0 0 0|0 0 0 0 ",
        "                 ",
        " 0 0 0 0|0 0 S 0 ",
        "   _             ",
        " 0|0 0 0|0 0 0 0 ",
        "                 ",
        " 0|0|0 0|0 0 S 0 ",
        "                 ",
        " 0|0 0 M|0 S 0 0 ",
        "   _             ",
        " 0 0 0 0|0 0 0 0 ",
        "                 ",
    };

    public LvlHardLine2() {
        super(GEOMETRY, // Geometry
              4,       // Normal fence segments
              4,       // Electric fence segments
              "\'Hard Line 2\'", PlayingScene.MESSAGE_NORMAL, PlayingScene.MESSAGE_TIME_FOREVER,
              "You knew there'd be a sequel.", PlayingScene.MESSAGE_NORMAL, PlayingScene.MESSAGE_TIME_FOREVER);
    }
}
