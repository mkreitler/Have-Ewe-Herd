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

public class LvlGoGetEm extends LevelSetup{
    private static final String[] GEOMETRY = {
        "                 ",
        " 0 0 0 0 0 0 0 0 ",
        "   _             ",
        " 0|0 0 0 0 0 0 0 ",
        "                 ",
        " 0 0 0 0 0 0 0 0 ",
        "                 ",
        " 0 0 0 0 0 0 0 0 ",
        "                 ",
        " 0 M S S 0 0 D 0 ",
        "                 ",
        " 0 0 0 0 0 0 0 0 ",
        "                 ",
        " 0 0 0 0 0 0 0 0 ",
        "                 ",
        " 0 0 0 0 0|0 0 0 ",
        "         _       ",
    };

    public LvlGoGetEm() {
        super(GEOMETRY, // Geometry
              3,        // Normal fence segments
              2,        // Electric fence segments
              "That's about it. Go get 'em!", PlayingScene.MESSAGE_NORMAL, PlayingScene.MESSAGE_TIME_FOREVER,
              "Well done!", PlayingScene.MESSAGE_NORMAL, PlayingScene.MESSAGE_TIME_FOREVER);
    }
}
