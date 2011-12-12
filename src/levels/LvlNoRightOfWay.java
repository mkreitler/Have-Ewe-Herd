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

public class LvlNoRightOfWay extends LevelSetup{
    private static final String[] GEOMETRY = {
        "                 ",
        " 0 0 0 0 0 0 0 0 ",
        "   _             ",
        " 0|0|0 0 0 0 0 0 ",
        "                 ",
        " 0 0 0 0 D 0 0 0 ",
        "                 ",
        " 0 0 0 0 0 0 0 0 ",
        "         .       ",
        " 0 0 0 S 0 0 0 0 ",
        "                 ",
        " 0 0 0 0 0 0 0 0 ",
        "                 ",
        " 0 0 0 0 M 0 0 0 ",
        "                 ",
        " 0 0 0 0 0 0 0 0 ",
        "                 ",
    };

    public LvlNoRightOfWay() {
        super(GEOMETRY, // Geometry
              1,        // Normal fence segments
              2,        // Electric fence segments
              "I call this one, \'No Right of Way.\'", PlayingScene.MESSAGE_NORMAL, PlayingScene.MESSAGE_TIME_FOREVER,
              "Have enough fence left?", PlayingScene.MESSAGE_NORMAL, PlayingScene.MESSAGE_TIME_FOREVER);
    }
}
