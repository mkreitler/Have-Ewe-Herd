/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package maingame;

import pulpcore.image.CoreImage;

/**
 *
 * @author mkreitler
 */
public class CritterLamb extends Critter {
    private boolean bShowHearts;

    // Constructors ////////////////////////////////////////////////////////////
    public CritterLamb(CoreImage image, int x, int y) {
        super(image, x, y);

        bShowHearts = false;
    }

    // Public Methods //////////////////////////////////////////////////////////
    public void setShowHearts(boolean bDoShow) {
        bShowHearts = bDoShow;
    }

    @Override
    public boolean isStopped() { return true; }

    public boolean showHearts() { return bShowHearts; }

    // Protected Methods ///////////////////////////////////////////////////////
    protected int evaluateBlock(int blockage,
                                boolean bImmobile,
                                boolean bElectrified) {
        return Critter.FALLBACK_NONE;
    }

    protected void resolveWantDirection() {}

    // Private Methods /////////////////////////////////////////////////////////
    
}
