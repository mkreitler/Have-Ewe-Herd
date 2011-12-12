/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package maingame;

/**
 *
 * @author mkreitler
 */
import pulpcore.sprite.Group;

public class Plot {
    public static final int ENCLOSED_YES    = -1;
    public static final int ENCLOSED_NO     = 0;
    public static final int ENCLOSED_MAYBE  = 1;

    private boolean bTesting    = false;
    private int enclosed        = ENCLOSED_MAYBE;
    private int fenceValue      = 0;

    private Plot[] neighbors    = {null, null, null, null};
    private Group group         = null;

    public Plot(int row, int col, Group groupIn) {
        reset();

        group = groupIn;
    }

    public Group getGroup() { return group; }

    public void setFenceValue(int fenceValue) {
        this.fenceValue = fenceValue;

        if (fenceValue == PlayingScene.FENCED_RIGHT +
                          PlayingScene.FENCED_DOWN +
                          PlayingScene.FENCED_LEFT +
                          PlayingScene.FENCED_UP) {
            enclosed = ENCLOSED_YES;
        }
    }

    public int getFenceValue() { return fenceValue; }

    public boolean isBeingTested() { return bTesting; }

    public boolean isFenced() { return fenceValue != 0; }

    public final void reset() {
        bTesting    = false;
        enclosed    = ENCLOSED_MAYBE;
        fenceValue  = 0;
    }

    public void setNeighbors(Plot right,
                             Plot down,
                             Plot left,
                             Plot up) {

        // Establish links to neighboring plots.
        neighbors[PlayingScene.RIGHT]    = right;
        neighbors[PlayingScene.DOWN]     = down;
        neighbors[PlayingScene.LEFT]     = left;
        neighbors[PlayingScene.UP]       = up;
    }

    public boolean isEnclosed(int testDepth) {
        boolean bIsEnclosed = false;

        // We're enclosed if all our neighbors are enclosed.

        // First, mark ourselves as 'being tested' to prevent
        // infinite recursion.
        bTesting = true;

        if (enclosed == ENCLOSED_YES) {
            bIsEnclosed = true;
        }
        else if (enclosed == ENCLOSED_NO) {
            bIsEnclosed = false;
        }
        else
        {
            // We're not sure we're enclosed, so we have to
            // evaluate our neighbors' enclosed states. A
            // 'false' result in any direction immediately
            // tells us we're not enclosed.
            boolean bEnclosedRight  = false;
            boolean bEnclosedDown   = false;
            boolean bEnclosedLeft   = false;
            boolean bEnclosedUp     = false;

            // Check for enclosure to the right.
            if ((fenceValue & PlayingScene.FENCED_RIGHT) != 0) {
                bEnclosedRight = true;
            }
            else if(neighbors[PlayingScene.RIGHT] != null &&
                    neighbors[PlayingScene.RIGHT].isBeingTested() == false) {
                if(neighbors[PlayingScene.RIGHT].isEnclosed(testDepth + 1)) {
                    bEnclosedRight = true;
                }
                else {
                    enclosed = ENCLOSED_NO;
                }
            }
            else if (neighbors[PlayingScene.RIGHT] == null) {
                // Immediate fail.
                enclosed = ENCLOSED_NO;
            }
            else { // This neighbor is already being tested.
                bEnclosedRight = true;
            }

            // Check for enclosure below.
            if (enclosed != ENCLOSED_NO) {
                if ((fenceValue & PlayingScene.FENCED_DOWN) != 0) {
                    bEnclosedDown = true;
                }
                else if(neighbors[PlayingScene.DOWN] != null
                        &&
                    neighbors[PlayingScene.DOWN].isBeingTested() == false) {
                    if (neighbors[PlayingScene.DOWN].isEnclosed(testDepth + 1)) {
                        bEnclosedDown = true;
                    }
                    else {
                        enclosed = ENCLOSED_NO;
                    }
                }
                else if (neighbors[PlayingScene.DOWN] == null) {
                    // Immediate fail.
                    enclosed = ENCLOSED_NO;
                }
                else { // This neighbor is already being tested.
                    bEnclosedDown = true;
                }
            }

            // Check for enclosure to the left.
            if (enclosed != ENCLOSED_NO) {
                if ((fenceValue & PlayingScene.FENCED_LEFT) != 0) {
                    bEnclosedLeft = true;
                }
                else if(neighbors[PlayingScene.LEFT] != null
                        &&
                    neighbors[PlayingScene.LEFT].isBeingTested() == false) {
                    if (neighbors[PlayingScene.LEFT].isEnclosed(testDepth + 1)) {
                        bEnclosedLeft = true;
                    }
                    else {
                        enclosed = ENCLOSED_NO;
                    }
                }
                else if (neighbors[PlayingScene.LEFT] == null) {
                    // Immediate fail.
                    enclosed = ENCLOSED_NO;
                }
                else { // This neighbor is already being tested.
                    bEnclosedLeft = true;
                }
            }

            // Check for enclosure above.
            if (enclosed != ENCLOSED_NO) {
                if ((fenceValue & PlayingScene.FENCED_UP) != 0) {
                    bEnclosedUp = true;
                }
                else if(neighbors[PlayingScene.UP] != null
                        &&
                    neighbors[PlayingScene.UP].isBeingTested() == false) {
                    if (neighbors[PlayingScene.UP].isEnclosed(testDepth + 1)) {
                        bEnclosedUp = true;
                    }
                    else {
                        enclosed = ENCLOSED_NO;
                    }
                }
                else if (neighbors[PlayingScene.UP] == null) {
                    // Immediate fail.
                    enclosed = ENCLOSED_NO;
                }
                else { // This neighbor is already being tested.
                    bEnclosedUp = true;
                }
            }

            if (enclosed != ENCLOSED_NO) {
                bIsEnclosed = bEnclosedRight &&
                              bEnclosedDown &&
                              bEnclosedLeft &&
                              bEnclosedUp;
            }
        }

        bTesting = false;

        if (testDepth == 0 && bIsEnclosed) {
            enclosed = ENCLOSED_YES;
        }

        return bIsEnclosed;
    }
}
