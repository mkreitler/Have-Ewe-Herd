/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package maingame;

/**
 *
 * @author mkreitler
 */
import java.util.ArrayList;

import pulpcore.image.CoreImage;
import pulpcore.math.CoreMath;
import pulpcore.sprite.Sprite;
import pulpcore.animation.Timeline;
import pulpcore.animation.Easing;
import pulpcore.sprite.Group;

public abstract class Critter extends ESprite {
    
    protected static final int  FALLBACK_ABORT              = -99;
    protected static final int  FALLBACK_NONE               = -1;
    protected static final int  FALLBACK_STOP               = 0;
    protected static final int  FALLBACK_WAIT               = 1;
    protected static final int  FALLBACK_RIGHT              = 2;
    protected static final int  FALLBACK_SPIN               = 3;
    protected static final int  FALLBACK_BREAKTHROUGH       = 4;
    protected static final int  FALLBACK_LEFT               = 5;

    private static final int    TWITCH_TIME                 = 100;
    private static final double TWITCH_SIZE                 = 0.0001;
    private static final int    SPAWN_INTERVAL              = 500;

    protected int row;
    protected int col;
    protected int dRow;         // Delta row -- apply this to move in the desired direction.
    protected int dCol;         // Delta col -- apply this to move in the desired direction.
    protected int curDir;
    protected int wantDir;
    protected int lastRow;
    protected int lastCol;
    protected int lastDir;

    protected int movePriority;            // The higher this value, the longer our "critter chain."
    protected int moveAttempts;            // Fail after 3 tries (per tick).
    protected int immobileBlockCount;

    protected ArrayList<Critter>    critterList;
    private boolean bTipRight;
    private int twitchClock;
    private boolean bAllowTwitch;
    private boolean bWantsRemoved;
    private Timeline currentTimeline;
    private boolean bHasMoved;
    private int spawnTimer;
    private boolean bWaiting;

    // Constructors ////////////////////////////////////////////////////////////
    public Critter(CoreImage image, int x, int y) {
        super(image, x, y);

        setAnchor(Sprite.CENTER);
    }

    // Public Methods //////////////////////////////////////////////////////////
    public void setWantsRemoved() { bWantsRemoved = true; }
    public void clearWantsRemoved() { bWantsRemoved = false; }
    public boolean wantsRemoved() { return bWantsRemoved; }
    public Timeline getTimeline() { return currentTimeline; }
    abstract public boolean isStopped();

    public int getLastRow() { return lastRow; }
    public int getLastCol() { return lastCol; }
    public int getLastDir() { return lastDir; }
    public void updateHistory() {
        lastRow = row;
        lastCol = col;
        lastDir = curDir;
    }

    public boolean isBackLinkedTo(Critter critter) {
        boolean bRowsMatch = lastRow == critter.getRow();
        boolean bColsMatch = lastCol == critter.getCol();
        boolean bDirsMatch = lastDir == critter.getCurrentDir();

        return bRowsMatch && bColsMatch && bDirsMatch && lastDir != 0;
    }

    @Override
    public void update(int elapsedTime) {
        super.update(elapsedTime);

        spawnTimer = Math.max(spawnTimer - elapsedTime, 0);

        if (wantDir != 0 && bAllowTwitch) {
            twitchClock -= elapsedTime;

            if (twitchClock < 0)
            {
                if (bTipRight) {
                    angle.animateTo(TWITCH_SIZE * CoreMath.PI / 180.0, TWITCH_TIME, Easing.NONE);
                    bTipRight = false;
                }
                else {
                    angle.animateTo(-TWITCH_SIZE * CoreMath.PI / 180.0, TWITCH_TIME, Easing.NONE);
                    bTipRight = true;
                }
                
                twitchClock += TWITCH_TIME;
            }
        }
        else {
            angle.animateTo(0, TWITCH_TIME);
        }
    }

    public void evaluateGoal(ArrayList<Critter>critterListIn) {
        critterList = critterListIn;

        if (critterList != null) {
            movePriority        = 0;
            moveAttempts        = 0;
            immobileBlockCount  = 0;

            // Decide the direction in which we want to move.
            chooseDirection();

            initMovementState();
        }
    }

    public int getRow() { return row; }
    public int getCol() { return col; }
    public int getGroupRow() { return 2 * row + 1; }
    public int getGroupCol() { return 2 * col + 1; }

    public int getMovePriority() {
        return movePriority;

    }
    public int getWantDir() { return wantDir; }
    public int getRowDelta() { return dRow; }
    public int getColDelta() { return dCol; }
    public int getCurrentDir() { return curDir; }
    public boolean isImmobile() { return isPermaBlocked(); }

    public Timeline setGridLocation(int rowIn, int colIn, int duration) {
        if (row != rowIn || col != colIn) {
            bAllowTwitch = true;
        }

        row = rowIn;
        col = colIn;

        int intWidth = width.getAsInt();
        int intHeight = height.getAsInt();

        int endX   = colIn * intWidth + intWidth / 2;
        int endY   = rowIn * intHeight + intHeight / 2;

        if (duration > 0) {

            currentTimeline = new Timeline();

            currentTimeline.animateTo(x, endX, duration, Easing.REGULAR_IN_OUT);
            currentTimeline.animateTo(y, endY, duration, Easing.REGULAR_IN_OUT);
        }
        else
        {
            setLocation(endX, endY);
        }

        // Clear the flock start flag whenever we move.
        clearFlockedStart();

        return currentTimeline;
    }

    @Override
    public void setLocation(int x, int y) {
        super.setLocation(x, y);

        // Assuming our location is relative to our parent layer,
        // there's no need to correct for GROUND_OFFSET values.
        int dx = width.getAsInt();
        int dy = height.getAsInt();

        if (dx > 0 && dy > 0) {
            row = (y - dy / 2) / dy;
            col = (x - dx / 2) / dx;
        }
    }

    public void spawnHeart(PlayingScene scene, Group layer) {
        if (spawnTimer == 0) {
            // Is there an available heart?
            HeartParticle newHeart = new HeartParticle("heart.png", x.getAsInt(), y.getAsInt());

            if (newHeart != null) {
                newHeart.init(scene, layer, x.getAsInt(), y.getAsInt(), curDir);
                spawnTimer = (int)(SPAWN_INTERVAL * (1.0 - Math.random() * 0.5) + 0.5);
            }
        }
    }

    // Protected Methods ///////////////////////////////////////////////////////
    protected boolean tagAlong() { return false; }
    protected void setFlockedStart(boolean bFlockedStart) {}
    protected void clearFlockedStart() {}

    protected void overrideWantDir(int newDir) {
        reset();
        wantDir = newDir;
        computeMoveDeltas();
        spawnTimer = 0;
    }

    protected int resolveSpin() {
        wantDir = getSpinDirection();

        computeMoveDeltas();

        return Critter.FALLBACK_WAIT;
    }

    protected int resolveTurnRight() {
        wantDir = getTurnRightDirection();

        computeMoveDeltas();

        return Critter.FALLBACK_WAIT;
    }

    protected int resolveTurnLeft() {
        wantDir = getTurnLeftDirection();

        computeMoveDeltas();

        return Critter.FALLBACK_WAIT;
    }

    protected int resolveBreakThrough() {
        moveAttempts = 0;

        return Critter.FALLBACK_NONE;
    }

    protected void resolveWait() {
        // Just...wait.
    }

    protected abstract void resolveWantDirection();

    protected abstract int evaluateBlock(int blockage,
                                         boolean bImmobile,
                                         boolean bElectrified);

    protected void chooseDirection() {
        // By default, keep our current direction and deltas.
        wantDir = curDir;

        resolveWantDirection();
    }

    protected void setMovePriority(int basePriority) {
        // Default to top priority.
        movePriority = basePriority;
    }

    protected void setStartDirection(int startDir) {}
    protected boolean isPermaBlocked() { return hasMoved(); }
    protected void setWaiting() { bWaiting = true; }
    protected void clearWaiting() { bWaiting = false; }
    protected boolean isWaiting() { return bWaiting; }

    protected void stopTwitching() {
        angle.animateTo(0, TWITCH_TIME);
        bAllowTwitch = false;
    }

    protected void reset() {
        curDir  = 0;
        wantDir = 0;

        moveAttempts = 0;

        stopTwitching();
        twitchClock  = CoreMath.rand(100) * TWITCH_TIME / 100;
    }

    protected int getTurnRightDirection() {
        return (curDir * 2) % 15;
    }

    protected int getSpinDirection() {
        return (curDir * 4) % 15;
    }

    protected int getTurnLeftDirection() {
        return (curDir * 16 / 2) % 15;
    }

    protected boolean hasMoved() { return bHasMoved; }
    protected void setHasMoved() { bHasMoved = true; }
    protected void clearHasMoved() { bHasMoved = false; }

    protected void addSpookDirection(int direction) {}
    protected void addFlockDirection(int direction) {}
    protected void resetStoredBehaviors() {}
    protected void initMovementState() {
        clearHasMoved();
        clearWaiting();
    }

    protected void computeMoveDeltas() {

        // Assume no movement.
        dRow = 0;
        dCol = 0;

        if (wantDir != 0) {

            // Compute row and column deltas based on our desired move
            // direction.
            switch (wantDir) {
                case PlayingScene.DIRECTION_RIGHT:
                {
                    dCol = 1;
                }
                break;

                case PlayingScene.DIRECTION_DOWN:
                {
                    dRow = 1;
                }
                break;

                case PlayingScene.DIRECTION_LEFT:
                {
                    dCol = -1;
                }
                break;

                case PlayingScene.DIRECTION_UP:
                {
                    dRow = -1;
                }
                break;

                default:
                {
                    // Shouldn't get here.
                }
                break;
            }
        }

        // Align our actual direction with the desired direction.
        curDir = wantDir;
    }

    // Private Methods /////////////////////////////////////////////////////////
}
