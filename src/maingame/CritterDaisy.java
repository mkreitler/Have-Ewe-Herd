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

public class CritterDaisy extends Critter {
    // Contructors /////////////////////////////////////////////////////////////
    public CritterDaisy(CoreImage image, int x, int y) {
        super(image, x, y);
    }

    // Public Methods //////////////////////////////////////////////////////////
    @Override
    public boolean isImmobile() { return true; }

    @Override
    public boolean isStopped() { return true; }

    // Protected Methods ///////////////////////////////////////////////////////
    protected void resolveWantDirection() {
        // Daisy doesn't move, so she needs no direction.
        wantDir = 0;
    }

    protected int evaluateBlock(int blockage,
                                boolean bImmobile,
                                boolean bElectrified) {
        // Daisy always STOPs.
        return Critter.FALLBACK_STOP;
    }
}
