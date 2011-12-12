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

public class CritterSheep extends Critter {
    private int spookDirection;
    private int flockDirection;
    private int spookDirectionCount;
    private int flockDirectionCount;
    private boolean bFlockedStart;
    
    // Contructors /////////////////////////////////////////////////////////////
    public CritterSheep(CoreImage image, int x, int y) {
        super(image, x, y);
    }

    // Public Methods //////////////////////////////////////////////////////////
    public boolean isStopped() { return wantDir == 0; }

    // Protected Methods ///////////////////////////////////////////////////////
    @Override
    protected void setFlockedStart(boolean bFlockedStart) { this.bFlockedStart = bFlockedStart; }
    
    @Override
    protected void clearFlockedStart() { bFlockedStart = false; }

    protected int evaluateBlock(int blockage,
                                boolean bImmobile,
                                boolean bElectrified) {

        // Unhandled blockages make us STOP.
        int action = Critter.FALLBACK_STOP;

        // RULES:
        // 1) If blocked turn RIGHT.
        // 2) If still blocked, SPIN.
        // 3) If still blocked, STOP.

        if (bFlockedStart)
        {
            // If we were forced to move via flocking but
            // we're blocked, wait.
            action = Critter.FALLBACK_WAIT;
        }
        else if(bImmobile)
        {
            if (bImmobile) {
                ++immobileBlockCount;
            }

            switch (moveAttempts) {
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
                    reset();
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

    @Override
    protected void resetStoredBehaviors() {
        super.resetStoredBehaviors();
        
        spookDirection = 0;
        spookDirectionCount = 0;

        flockDirection = 0;
        flockDirectionCount = 0;
    }

    @Override
    protected void addSpookDirection(int direction) {
        if ((spookDirection & direction) == 0) {
            spookDirection += direction;
            ++spookDirectionCount;
        }

        // Being spooked clears our polite flocking behavior.
        clearFlockedStart();
    }

    @Override
    protected void addFlockDirection(int direction) {
        if ((flockDirection & direction) == 0) {
            flockDirection += direction;
            ++flockDirectionCount;
        }
    }

    @Override
    protected void reset() {
        super.reset();

        resetStoredBehaviors();
    }
    
    protected void resolveWantDirection() {
        // RULES:
        // 1) Spook, if stationary.
        // 2) Flock, if stationary.
        // 3) Keep doing what we're doing.

        // Default to our current direction.
        wantDir = curDir;

        if (spookDirection != 0) {
            resolveCompositeDirection(spookDirection, spookDirectionCount);
        }
        else if (wantDir == 0) {
            if (flockDirection != 0) {
                resolveCompositeDirection(flockDirection, flockDirectionCount);
            }
        }

        spookDirection = 0;
        spookDirectionCount = 0;

        flockDirection = 0;
        flockDirectionCount = 0;
    }

    private void resolveCompositeDirection(int totalDir, int compositeCount) {
        // Now calculate a resultant direction. Since we can't move
        // diagonally, we have to use heuristics to resolve the
        // direction.
        if (totalDir % 5 == 0)
        {
            // Hit from equally opposing forces.
            wantDir = 0;
        }
        else if (compositeCount == 3) {
            // Hit from three sides, so move in the unbalanced direction.
            wantDir = totalDir - totalDir / 5 * 5;
        }
        else if (compositeCount == 1) {
            // Move in the direction of impact.
            wantDir = totalDir;
        }
        else {
            // Hit from two perpendicular directions.
            // Favor the usual directional bias (right-down-left-up).
            if ((totalDir & PlayingScene.DIRECTION_RIGHT) != 0) {
                wantDir = PlayingScene.DIRECTION_RIGHT;
            }
            else if ((totalDir & PlayingScene.DIRECTION_DOWN) != 0) {
                wantDir = PlayingScene.DIRECTION_DOWN;
            }
            else if ((totalDir & PlayingScene.DIRECTION_LEFT) != 0) {
                wantDir = PlayingScene.DIRECTION_LEFT;
            }
            else {
                // We shouldn't be able to get here, but just in case...
                wantDir = PlayingScene.DIRECTION_UP;
            }
        }
    }

    @Override
    protected boolean tagAlong() {
        boolean bTagging = false;

        // If we're near other sheep, moving in the direction
        // of the majority (including our current movement).
        int tallies[] = {0, 0, 0, 0};

        // Tally up the movement directions of all adjacent sheep.
        if (curDir == 0) {
            for (int i=0; i<critterList.size(); ++i) {

                Critter testCritter = critterList.get(i);

                if (testCritter != null &&
                    testCritter.getClass() == CritterSheep.class) {

                    CritterSheep testSheep = (CritterSheep)testCritter;
                    int oneNormDist = Math.abs(getRow() - testSheep.getRow()) +
                                      Math.abs(getCol() - testSheep.getCol());

                    if (oneNormDist <= 1) {
                        // This sheep is adjacent, or it's us. In either
                        // case, tally its movement.
                        int moveDir = testSheep.getCurrentDir();

                        switch (moveDir) {
                            case PlayingScene.DIRECTION_RIGHT:
                            {
                                ++tallies[PlayingScene.RIGHT];
                            }
                            break;

                            case PlayingScene.DIRECTION_DOWN:
                            {
                                ++tallies[PlayingScene.DOWN];
                            }
                            break;

                            case PlayingScene.DIRECTION_LEFT:
                            {
                                ++tallies[PlayingScene.LEFT];
                            }
                            break;

                            case PlayingScene.DIRECTION_UP:
                            {
                                ++tallies[PlayingScene.UP];
                            }
                            break;

                            default:
                            {
                                // No movement.
                            }
                            break;
                        }
                    }
                }
            }

            // Which direction has the most occurrances?
            int most      = tallies[0];
            int mostIndex = 0;
            for (int i=1; i<tallies.length; ++i) {
                if (tallies[i] > most) {
                    most        = tallies[i];
                    mostIndex   = i;
                }
            }

            // If there is a tie, ignore the flock (can't choose).
            boolean bFollowMajority = true;
            for (int i=0; i<tallies.length; ++i) {
                if (i == mostIndex) continue;

                if (tallies[i] == most) {
                    bFollowMajority = false;
                    break;
                }
            }

            if (bFollowMajority) {
                wantDir = (int)(Math.pow(2, mostIndex) + 0.5);
                bTagging = true;
            }
        }

        return bTagging;
    }
}
