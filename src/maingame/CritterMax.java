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

public class CritterMax extends Critter {
    private static final int    MOVE_PRIORITY_BIAS      = 5;    // Max wants to move last.
    private static final int    HEART_COUNT             = 5;

    private boolean bNextToDaisy;

    // Contructors /////////////////////////////////////////////////////////////
    public CritterMax(CoreImage image, int x, int y) {
        super(image, x, y);

        bNextToDaisy = false;
    }

    // Public Methods //////////////////////////////////////////////////////////
    @Override
    public boolean isImmobile() {
        return bNextToDaisy || isPermaBlocked();
    }

    @Override
    public boolean isStopped() { return false; }

    @Override
    protected void reset() {
        super.reset();

        bNextToDaisy = false;
    }

    // Protected Methods ///////////////////////////////////////////////////////
    @Override
    protected void setStartDirection(int startDir) {
        wantDir = startDir;
        computeMoveDeltas();
    }

    protected int evaluateBlock(int blockage,
                                boolean bImmobile,
                                boolean bElectrified) {

        // Unhandled blockages make us WAIT.
        int action = Critter.FALLBACK_STOP;

        boolean bBlockedByFence = (blockage & LevelLayout.BLOCKAGE_FENCE) != 0;

        // RULES:
        // If next to Daisy, STOP.
        // If blocked by a wooden fence, BREAK THROUGH.
        // If blocked by an electrified object, turn RIGHT, SPIN, or STOP.
        // Otherwise, WAIT.

        if (blockage == LevelLayout.BLOCKAGE_CRITTER &&
            bImmobile) {
            // Blocked by Daisy.
            action = Critter.FALLBACK_STOP;
        }
        else if (bBlockedByFence && !bElectrified) {
            action = Critter.FALLBACK_BREAKTHROUGH;
        }
        else if (bElectrified && bImmobile) {
            ++immobileBlockCount;
            
            switch(moveAttempts) {
                case 0:
                {
                    action = Critter.FALLBACK_RIGHT;
                }
                break;

                case 1:
                {
                    action = Critter.FALLBACK_SPIN;
                }
                break;

                default:
                {
                    action = Critter.FALLBACK_WAIT;
                }
                break;
            }
        }
        else {
            action = Critter.FALLBACK_WAIT;
        }

        if (action != Critter.FALLBACK_WAIT)
        {
            ++moveAttempts;
        }

        return action;
    }

    protected void resolveWantDirection() {
        // RULES:
        // 1) Max stays by Daisy.
        // 2) If he can see her, Max goes to Daisy.

        bNextToDaisy = false;

        Critter daisy = null;

        for (int iCritter = 0; iCritter < critterList.size(); ++iCritter) {
            Critter curCritter = critterList.get(iCritter);

            if (curCritter != null) {
                if (curCritter.getClass() == CritterDaisy.class) {
                    daisy = curCritter;
                    break;
                }
            }
        }

        // By default, keep moving in our last direction.
        wantDir = curDir;

        if (daisy != null) {
            int oneNormDist = Math.abs(daisy.getRow() - getRow()) +
                              Math.abs(daisy.getCol() - getCol());

            boolean bRowsMatch = daisy.getRow() == getRow();
            boolean bColsMatch = daisy.getCol() == getCol();

            if (oneNormDist == 1) {
                // Max is next to Daisy, so he won't move.

                bNextToDaisy = true;
                wantDir      = 0;
            }
            else if(bRowsMatch || bColsMatch)  {

                // Max sees daisy, so he wants to move
                // toward her.
                if (bRowsMatch) {
                    if (daisy.getCol() - getCol() > 0) {
                        // Daisy is to Max's right.
                        wantDir = PlayingScene.DIRECTION_RIGHT;
                    }
                    else {
                        // Daisy is to Max's left.
                        wantDir = PlayingScene.DIRECTION_LEFT;
                    }
                }
                else {
                    if (daisy.getRow() - getRow() > 0) {
                        // Daisy is below Max.
                        wantDir = PlayingScene.DIRECTION_DOWN;

                    }
                    else {
                        // Daisy is above Max.
                        wantDir = PlayingScene.DIRECTION_UP;
                    }
                }
            }
        }
    }

    @Override
    protected void setMovePriority(int basePriority) {
        movePriority = MOVE_PRIORITY_BIAS;
    }

    // Private Methods /////////////////////////////////////////////////////////
}
