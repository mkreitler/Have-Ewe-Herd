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

public class LvlRoundAndRound extends LevelSetup{
    private static final String[] GEOMETRY = {
        "                 ",
        " 0 0 0 0 0 0 0 0 ",
        " _ _ _           ",
        "|0 M 0|0 S 0 D 0 ",
        "                 ",
        " 0 0 0 0 0 0 0 0 ",
        "                 ",
        " 0 0 0 0 0 0 0 0 ",
        "                 ",
        " 0 S 0 0 0 0 0 0 ",
        "             _   ",
        " 0 0 0 S 0 0 0|0 ",
        "                 ",
        " 0 0 0 0 0 0 0|0 ",
        "                 ",
        " 0 0 0 0 0 0 0|0 ",
        "             _   ",
    };

    public LvlRoundAndRound() {
        super(GEOMETRY, // Geometry
              7,        // Normal fence segments
              3,        // Electric fence segments
              "Round and round they go!", PlayingScene.MESSAGE_NORMAL, PlayingScene.MESSAGE_TIME_FOREVER,
              "Fence 'em in!", PlayingScene.MESSAGE_NORMAL, PlayingScene.MESSAGE_TIME_FOREVER);
    }
}
