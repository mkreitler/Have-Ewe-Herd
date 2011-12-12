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
import pulpcore.animation.Easing;
import pulpcore.math.CoreMath;

public class UIWiggler extends HotSprite {

    private static final double     WIGGLE_ANGLE    = CoreMath.PI * 0.000267 / 180.0;
    private static final int        WIGGLE_TIME     = 333;
    
    private static final int        STATE_COOL      = 0;
    private static final int        STATE_HOT       = 1;

    private int                     state;
    private int                     wantState;
    private int                     stateTimer;

    // Constructors ////////////////////////////////////////////////////////////
    public UIWiggler(String imageFile, int x, int y) {
        super(imageFile, x, y);

        init();
    }

    public UIWiggler(CoreImage srcImage, int x, int y) {
        super(srcImage, x, y);

        init();
    }

    @Override
    public void update(int elapsedTimeMS) {
        super.update(elapsedTimeMS);

        stateTimer = Math.max(stateTimer - elapsedTimeMS, 0);

        if (state != wantState) {
            if (wantState == STATE_HOT) {
                onHot();
            }
            else {
                onCool();
            }
        }
    }

    // Public Methods //////////////////////////////////////////////////////////

    // Protected Methods ///////////////////////////////////////////////////////
    @Override
    protected void onHot() {
        wantState = STATE_HOT;

        if (stateTimer == 0) {
            // When we become hot, wiggle on.
            this.angle.animateTo(WIGGLE_ANGLE, WIGGLE_TIME, Easing.REGULAR_IN_OUT);
            stateTimer = WIGGLE_TIME;

            state = STATE_HOT;
            super.onHot();
        }
    }

    @Override
    protected void onCool() {
        wantState = STATE_COOL;

        if (stateTimer == 0) {
            // When we become cool, wiggle off.
            this.angle.animateTo(0, WIGGLE_TIME, Easing.REGULAR_IN_OUT);
            stateTimer = WIGGLE_TIME;

            state = STATE_COOL;
            super.onCool();
        }
    }

    // Private Methods /////////////////////////////////////////////////////////
    private void init() {
        state       = STATE_COOL;
        wantState   = STATE_COOL;
        stateTimer  = 0;
    }
}
