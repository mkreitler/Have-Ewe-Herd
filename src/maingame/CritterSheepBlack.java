/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package maingame;

/**
 *
 * @author mkreitler
 */
import pulpcore.image.CoreImage;

public class CritterSheepBlack extends CritterSheep {
    
    // Contructors /////////////////////////////////////////////////////////////
    public CritterSheepBlack(CoreImage image, int x, int y) {
        super(image, x, y);
    }

    // Public Methods //////////////////////////////////////////////////////////

    // Protected Methods ///////////////////////////////////////////////////////
    @Override
    protected int evaluateBlock(int blockage,
                                boolean bImmobile,
                                boolean bElectrified) {

        // Unhandled blockages make us STOP.
        int action = Critter.FALLBACK_STOP;

        // RULES:
        // 1) If blocked turn RIGHT.
        // 2) If still blocked, SPIN.
        // 3) If still blocked, STOP.

        if (bImmobile)
        {
            if (bImmobile) {
                ++immobileBlockCount;
            }

            switch (moveAttempts) {
                case 0:
                {
                    action = Critter.FALLBACK_LEFT;
                }
                break;

                case 1:
                {
                    action = Critter.FALLBACK_SPIN;
                }
                break;

                default:
                {
                    action = Critter.FALLBACK_STOP;
                }
                break;
            }

            ++moveAttempts;
        }
        else {
            action = Critter.FALLBACK_WAIT;
        }

        return action;
    }
}
