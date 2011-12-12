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

public class LvlStairwayToHeaven extends LevelSetup{
    private static final String[] GEOMETRY = {
        "                 ",
        " 0 D 0 0 0 0 0 0 ",
        "   _ _ _ _ _ .   ",
        " 0 0 0 0 0 0 S 0:",
        "           .   . ",
        " 0 0 0 0 0 S 0:0 ",
        "         .   .   ",
        " 0 0 0 0 S 0:0 0 ",
        "       .   .     ",
        " 0 0 0 S 0:0 0 0 ",
        "     .   .       ",
        " 0 0 S 0:0 0 0 0 ",
        "   .   .         ",
        " 0 S 0:0 0 0 0 0 ",
        "     .           ",
        " 0 M:0 0 0 0 0 0 ",
        "                 ",
    };

    public LvlStairwayToHeaven() {
        super(GEOMETRY, // Geometry
              6,       // Normal fence segments
              2,        // Electric fence segments
              "\'Stairway to Heaven.\' Get it?", PlayingScene.MESSAGE_NORMAL, PlayingScene.MESSAGE_TIME_FOREVER,
              "Your mama would be proud!", PlayingScene.MESSAGE_NORMAL, PlayingScene.MESSAGE_TIME_FOREVER);
    }
}
