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

public class LvlYouWin extends LevelSetup{
    private static final String[] GEOMETRY = {
        "         _       ",
        " 0|0 0|0|0|0|0|0 ",
        "   _ _           ",
        " 0 0|0 0|0|0|0|S ",
        "                 ",
        " 0 0|S 0|0|S|0|0 ",
        "         _   _   ",
        " 0|S 0|S S 0 0 0 ",
        "                 ",
        " 0|0|0|M|0 S 0 D ",
        "           _     ",
        " 0|0|0|0|0|S|0|0 ",
        "   _ _           ",
        " 0 0 S 0|0|0|0|0 ",
        "                 ",
        " 0 0 0 0 0|0|0|0 ",
        "             _   ",
    };

    public LvlYouWin() {
        super(GEOMETRY, // Geometry
              10,       // Normal fence segments
              10,        // Electric fence segments
              "Nice herdin', partner!", WHITE, PlayingScene.MESSAGE_TIME_FOREVER,
              "Go nuts with all that fence!", WHITE, PlayingScene.MESSAGE_TIME_FOREVER);
    }
}
