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
import java.util.Iterator;

import pulpcore.animation.Timeline;
import pulpcore.sprite.Sprite;
import pulpcore.sprite.Group;

public class CritterSitter {
    protected static final int  LOWEST_PRIORITY         = 1000000;
    
    private static final int    PRIORITY_BIAS_BLOCKED   = 10;
    private static final int    LAMB_BONUS              = 250;
    private static final int    MAX_PREFLOCK_ATTEMPTS   = 3;

    private LevelLayout currentLevel            = null;
    private ArrayList<Critter> critterList      = null;
    private ArrayList<Critter> unmovedCritters  = null;
    private ArrayList<CritterLamb> lambList     = null;
    private boolean bLevelFailed                = false;
    private int currentTurn                     = -1;
    private Critter blockingCritter             = null;
    private Critter movedCritter                = null;
    private boolean bUpdatedMovingCritters      = false;
    private boolean bAllBlocksImmobile          = false;

    public CritterSitter(LevelLayout levelIn) {
        currentLevel = levelIn;

        critterList     = new ArrayList<Critter>();
        unmovedCritters = new ArrayList<Critter>();
        lambList        = new ArrayList<CritterLamb>();
    }

    public void addLamb(CritterLamb newLamb) {
        if (lambList != null) {
            lambList.add(newLamb);
        }
    }

    public void clearLambs() {
        if (lambList != null) {
            lambList.clear();
        }
    }

    public void insertLambs(Group layer) {
        if (lambList != null && layer != null) {
            for (int i=0; i<lambList.size(); ++i) {
                layer.add(lambList.get(i));
            }
        }
    }

    public void removeLambs() {
        if (lambList != null) {
            for (int i=0; i<lambList.size(); ++i) {
                CritterLamb lamb = lambList.get(i);
                if (lamb != null) {
                    Group layer = lamb.getParent();
                    if (layer != null) {
                        layer.remove(lamb);
                    }
                }
            }
        }
    }

    public void reset() {
        bLevelFailed    = false;
        currentTurn     = -1;

        if (critterList != null) critterList.clear();
        if (unmovedCritters != null) unmovedCritters.clear();

        removeLambs();
        clearLambs();
    }

    public void popLambs(Group lowLayer, Group highLayer) {
        if (lambList != null &&
            lowLayer != null &&
            highLayer != null &&
            currentLevel != null) {

            for (int i=0; i<lambList.size(); ++i) {
                CritterLamb lamb = lambList.get(i);
                surfaceLamb(lamb, lowLayer, highLayer);
            }
        }
    }

    public boolean levelFailed() { return bLevelFailed; }

    public boolean processTick(int turn) {
        boolean bSomeoneMoved = false;

        bLevelFailed = false;
        currentTurn  = turn;

        bAllBlocksImmobile = false;

        if (critterList != null &&
            unmovedCritters != null &&
            currentLevel != null &&
            currentLevel.critterDaisy != null &&
            currentLevel.critterMax != null &&
            currentLevel.flock != null) {

            // Pre move.
            buildMasterCritterList();
            evaluateCritterGoals();
            computeCritterMovePriorities();

            // Move.
            sortCrittersByPriority(critterList);
            copyCritters(critterList, unmovedCritters, true);
            removeStationaryCritters();

            updateCritterHistories();

            int moveCount = 0;
            boolean bAnyoneMoved = false;
            do {

                bAnyoneMoved = moveCritters();

                if (bAnyoneMoved) {
                    ++moveCount;
                }

                if (moveCount > 0) {
                    bSomeoneMoved = true;
                }

                if (!bAnyoneMoved && moveCount == 0) {
                    // Deadlocked.
                    break;
                }

            } while (bAnyoneMoved && !bLevelFailed);
        }

        return bSomeoneMoved;
    }

    protected int updateLambs() {
        int lambBonus = 0;

        if (lambList != null && currentLevel != null) {
            for (int i=0; i<lambList.size(); ++i) {
                CritterLamb lamb = lambList.get(i);
                if (lamb != null) {
                    if (currentLevel.hasSheep(lamb.getRow(), lamb.getCol())) {
                        surfaceLamb(lamb, currentLevel.groundLayer, currentLevel.scene.getParticleLayer());
                        lamb.spawnHeart(currentLevel.scene, currentLevel.scene.getParticleLayer());
                        lambBonus += LAMB_BONUS;
                    }
                    else {
                        submergeLamb(lamb, currentLevel.groundLayer, currentLevel.scene.getParticleLayer());
                    }
                }
            }
        }

        return lambBonus;
    }

    protected CritterLamb getLamb(int iRow, int iCol) {
        CritterLamb lamb = null;

        // This routine assumes a maximum of one lamb per grid location.
        if (lambList != null) {
            for (int i=0; i<lambList.size(); ++i) {
                lamb = lambList.get(i);

                if (lamb.getRow() == iRow &&
                    lamb.getCol() == iCol) {

                    break;
                }
                else {
                    // Make sure we don't erroneously return the last lamb
                    // in the list.
                    lamb = null;
                }
            }
        }

        return lamb;
    }

    // Private Methods /////////////////////////////////////////////////////
    private void surfaceLamb(CritterLamb lamb, Group lowLayer, Group highLayer) {
        if (lamb != null && lamb.getParent() == lowLayer) {
            lowLayer.remove(lamb);
            lamb.setLocation(lamb.x.getAsInt() + PlayingScene.GROUND_OFFSET_X,
                             lamb.y.getAsInt() + PlayingScene.GROUND_OFFSET_Y);
            highLayer.add(lamb);

            if (currentLevel.hasSheep(lamb.getRow(), lamb.getCol())) {
                lamb.setShowHearts(true);
            }
        }
    }

    private void submergeLamb(CritterLamb lamb, Group lowLayer, Group highLayer) {
        if (lamb != null && lamb.getParent() == highLayer) {
            highLayer.remove(lamb);
            lamb.setLocation(lamb.x.getAsInt() - PlayingScene.GROUND_OFFSET_X,
                             lamb.y.getAsInt() - PlayingScene.GROUND_OFFSET_Y);
            lowLayer.add(lamb);

            if (currentLevel.hasSheep(lamb.getRow(), lamb.getCol())) {
                lamb.setShowHearts(false);
            }
        }
    }

    private void updateCritterHistories() {
        for (int i=0; i<critterList.size(); ++i) {
            Critter curCritter = critterList.get(i);

            if (curCritter != null) {
                curCritter.updateHistory();
            }
        }
    }

    private void sortCrittersByPriority(ArrayList<Critter>sortList) {
        
        // Order the critters by their move priority.
        // This makes it easier to process the list
        // and maintain prorities even when someone
        // decides to wait.

        // Simple min/max sort should do the trick.
        for (int i=0; i<sortList.size() - 1; ++i) {
            Critter firstCritter = sortList.get(i);

            int minIndex = i;
            int minValue = firstCritter.getMovePriority();

            for (int j=i+1; j<sortList.size(); ++j) {
                Critter secondCritter = sortList.get(j);

                int testValue = secondCritter.getMovePriority();
                if (minValue > testValue) {
                    minValue = testValue;

                    minIndex = j;
                }
            }
            
            if (minIndex != i) {
                // First critter wants to wait longer. Swap critters.
                Critter minCritter = sortList.get(minIndex);

                sortList.remove(firstCritter);
                sortList.add(i, null);
                sortList.remove(minCritter);
                sortList.add(minIndex, firstCritter);
                sortList.add(i, minCritter);

                // Clear out the 'null'.
                sortList.remove(null);

                firstCritter = sortList.get(i);  // Should be 'minCritter'.
                minCritter = sortList.get(minIndex); // Should be the original 'minCritter'.
            }
        }
    }

    private boolean moveCritters() {
        boolean bAnybodyMoved = false;
        int deadlockCount = 0;

        while (unmovedCritters.size() > 0) {
            bUpdatedMovingCritters = false;
            
            for (int i=0; i<unmovedCritters.size(); ++i) {
                Critter curCritter = unmovedCritters.get(i);

                movedCritter = null;
                int moveResult = moveCritter(curCritter, 0);

                if (moveResult == Critter.FALLBACK_ABORT) {
                    // Immediately end the level.
                    bLevelFailed = true;

                    // Exit all loops.
                    unmovedCritters.clear();
                    break;
                }
                else if (moveResult == Critter.FALLBACK_WAIT) {
                    // Stop twitching while we wait.
                    curCritter.stopTwitching();
                    
                    ++deadlockCount;

                    if (deadlockCount >= critterList.size())
                    {
                        if (!bAllBlocksImmobile) {
                            // Evaluate the moves again, this time
                            // forcing all blocks to be immobile.

                            bAllBlocksImmobile = true;
                            deadlockCount = 0;
                            break;
                        }
                        else {
                            // All remaining unmoved critters are waiting.
                            // Force them to re-assess their desired
                            // direction.
                            for (int j=0; j<unmovedCritters.size(); ++j) {
                                Critter clearCritter = unmovedCritters.get(j);
                                if (clearCritter != null &&
                                    isSheep(clearCritter)) {
                                    clearCritter.reset();
                                }
                            }

                            // Break out of the loop.
                            unmovedCritters.clear();
                            break;
                        }
                    }
                }
                else if (moveResult == Critter.FALLBACK_NONE ||
                         moveResult == Critter.FALLBACK_STOP) {
                    // No fallback required, so this critter
                    // must have moved normally.

                    if (movedCritter != null) {
                        // Mark the critters as 'moved'.
                        movedCritter.setHasMoved();

                        // Remove this critter from the 'unmoved' list.
                        movedCritter.setWantsRemoved();

                        // When critters stop, reset their movement properties.
                        if (moveResult == Critter.FALLBACK_STOP) {
                            movedCritter.reset();
                        }
                    }

                    boolean bCritterMoved = moveResult == Critter.FALLBACK_NONE;

                    // FALLBACK_NONE signals at least one successful move.
                    bAnybodyMoved |= bCritterMoved;

                    if (bCritterMoved) {
                        bAllBlocksImmobile = false;
                        deadlockCount = 0;
                    }

                    if (movedCritter != null) {
                        break;
                    }
                }

                if (bUpdatedMovingCritters) {
                    break;
                }
            }

            // Remove critters that have finished moving.
            boolean bRemovedOne = true;

            while (bRemovedOne && unmovedCritters.size() > 0) {
                for (int i=0; i<unmovedCritters.size(); ++i) {
                    bRemovedOne = false;

                    if (unmovedCritters.get(i).wantsRemoved())
                    {
                        unmovedCritters.remove(i);
                        bRemovedOne = true;
                        break;
                    }
                }
            }
        }

        return bAnybodyMoved;
    }

    private int moveCritter(Critter movingCritter, int preflockAttempts) {
        int result = Critter.FALLBACK_NONE;

        int newRow = movingCritter.getRow() + movingCritter.getRowDelta();
        int newCol = movingCritter.getCol() + movingCritter.getColDelta();

        // Can we move where we want?
        int blockage = isBlocked(movingCritter.getRow(),
                                 movingCritter.getCol(),
                                 newRow,
                                 newCol);

        if (blockage == LevelLayout.BLOCKAGE_NONE) {
            movingCritter.setHasMoved();
            movedCritter = movingCritter;

            if (newRow < 0 ||
                newCol < 0 ||
                newRow >= currentLevel.plots.length ||
                newCol >= currentLevel.plots[0].length) {

                // Moved out of bounds.
                currentLevel.removeObject(movingCritter.getGroupRow(),
                                          movingCritter.getGroupCol());
                result = Critter.FALLBACK_ABORT;
            }
            else {
                // Bring along nearby friends.
                if (movingCritter.getClass() == CritterSheep.class) {
                    flockNeighbors(movingCritter, true);
                }
                
                // Move the critter.
                setGridLocation(movingCritter, newRow, newCol);
            }
        }
        else if (blockingCritter != null &&
                 isSheep(blockingCritter) &&
                 !blockingCritter.hasMoved() &&
                 !blockingCritter.isWaiting() &&
                 (blockage & LevelLayout.BLOCKAGE_IMMOBILE) == 0) {

            // When blocked by another critter which hasn't yet moved,
            // try moving *that* critter.
            movingCritter.setWaiting();

            // Let this critter spook other animals into motion.
            spookSheep(movingCritter, true);

            // Try moving the blocking critter.
            result = moveCritter(blockingCritter, 0);

            movingCritter.clearWaiting();
        }
        else if (blockingCritter != null &&
                 isSheep(blockingCritter) &&
                 preflockAttempts < MAX_PREFLOCK_ATTEMPTS &&
                 !blockingCritter.isWaiting() &&
                 (blockage & LevelLayout.BLOCKAGE_IMMOBILE) == 0 &&
                 (blockingCritter.getWantDir() + movingCritter.getWantDir()) % 5 != 0) {

            // When blocked by another moving sheep that's not running
            // headlong into us, adopt its direction.
            movingCritter.overrideWantDir(blockingCritter.getCurrentDir());
            ++preflockAttempts;

            // TODO: prevent infinite recursion.
            moveCritter(movingCritter, preflockAttempts);
        }
        else
        {
            if ((blockage & LevelLayout.BLOCKAGE_ELECTRICAL) != 0) {

                // Shock ourselves when hitting an electrified obstacle.
                currentLevel.electrify(movingCritter,
                                       movingCritter.getGroupRow(),
                                       movingCritter.getGroupCol(),
                                       currentTurn,
                                       0);
            }

            // Blocked. Let the critter figure out its next move.
            boolean bImmobileBlock = (blockage & LevelLayout.BLOCKAGE_IMMOBILE) != 0;
            boolean bElectricBlock = ((blockage & LevelLayout.BLOCKAGE_ELECTRICAL) != 0);

            int fallbackAction = movingCritter.evaluateBlock(blockage,
                                                             bImmobileBlock,
                                                             bElectricBlock);

            result = resolveFallback(movingCritter, fallbackAction);

            if (fallbackAction == Critter.FALLBACK_RIGHT ||
                fallbackAction == Critter.FALLBACK_LEFT ||
                fallbackAction == Critter.FALLBACK_SPIN) {

                if (bImmobileBlock) {
                    // Might as well try to move again, now.
                    // Waiting isn't going to help.
                    result = moveCritter(movingCritter, preflockAttempts);
                }
            }
            else if (fallbackAction == Critter.FALLBACK_STOP) {
                movedCritter = movingCritter;
            }
        }

        return result;
    }

    private boolean isSheep(Critter testCritter) {
        boolean bIsSheep = false;

        Class testClass = testCritter.getClass();

        bIsSheep = testClass == CritterSheep.class ||
                   testClass == CritterSheepBlack.class;

        return bIsSheep;
    }

    private void addToMovingCritters(Critter movingCritter) {
        // Try to add this critter to the moving critters list.
        // If it's already there
        if (!movingCritter.hasMoved() &&
            !unmovedCritters.contains(movingCritter)) {

            movingCritter.evaluateGoal(critterList);
            movingCritter.computeMoveDeltas();
            
            unmovedCritters.add(movingCritter);

            // Re-sort the list.
            sortCrittersByPriority(unmovedCritters);

            bUpdatedMovingCritters = true;
        }
    }

    private void flockNeighbors(Critter movingCritter, boolean bAddToMovingCritters) {
        // Looking only at adjacent, non-moving neighbors,
        // ask them to tag along.

        if (currentLevel != null) {
            int direction = 1;
            for (int i=0; i< 4; ++i) {
                Critter neighbor = currentLevel.getCritterInDirection(movingCritter, direction, true);

                if (neighbor != null &&
                    neighbor.getClass() == CritterSheep.class &&
//                    neighbor.getCurrentDir() == 0 &&
                    neighbor.isStopped() &&
                    !isBlockedInDirection(movingCritter, direction) &&
                    !isBlockedInDirection(movingCritter, movingCritter.getCurrentDir()) &&
                    !isBlockedInDirection(neighbor, movingCritter.getCurrentDir())) {

                    neighbor.addFlockDirection(movingCritter.getWantDir());

                    if (bAddToMovingCritters) {
                        addToMovingCritters(neighbor);
                        neighbor.setFlockedStart(true);
                    }
                }

                direction *= 2;
            }
        }
    }

    private int resolveFallback(Critter critter, int fallbackAction) {
        // Default to stopping this critter.
        int resolvedAction = fallbackAction;

        switch(fallbackAction) {
            case Critter.FALLBACK_WAIT:
            {
                // Tank the critter's priority and
                // hope that someone moves out of the way.
                critter.resolveWait();
                
                break;
            }

            case Critter.FALLBACK_LEFT:
            {
                resolvedAction = critter.resolveTurnLeft();

                if (currentLevel != null)
                {
                    if (critter.getClass() == CritterMax.class) {
                        currentLevel.playSoundBull(false);
                    }
                    else if (critter.getClass() == CritterSheep.class ||
                             critter.getClass() == CritterSheepBlack.class) {
                        currentLevel.playSoundSheep(false);
                    }
                }

            }
            break;

            case Critter.FALLBACK_RIGHT:
            {
                resolvedAction = critter.resolveTurnRight();

                if (currentLevel != null)
                {
                    if (critter.getClass() == CritterMax.class) {
                        currentLevel.playSoundBull(false);
                    }
                    else if (critter.getClass() == CritterSheep.class ||
                             critter.getClass() == CritterSheepBlack.class) {
                        currentLevel.playSoundSheep(false);
                    }
                }

                break;
            }

            case Critter.FALLBACK_SPIN:
            {
                resolvedAction = critter.resolveSpin();

                break;
            }

            case Critter.FALLBACK_BREAKTHROUGH:
            {
                currentLevel.destroyFence(critter.getGroupRow() + critter.getRowDelta(),
                                          critter.getGroupCol() + critter.getColDelta());

                resolvedAction = critter.resolveBreakThrough();
                break;
            }

            default:
            {
                // FALLBACK_STOP and unhandled cases end up here.
                break;
            }
        }

        return resolvedAction;
    }

    private void setGridLocation(Critter critter, int row, int col) {
        // Move from old location...
        int oldGroupRow = critter.getGroupRow();
        int oldGroupCol = critter.getGroupCol();

        if (oldGroupRow >= 0 &&
            oldGroupRow < currentLevel.tiles.length &&
            oldGroupCol >= 0 &&
            oldGroupCol < currentLevel.tiles[0].length &&
            currentLevel.tiles[oldGroupRow][oldGroupCol] != null) {

            currentLevel.tiles[oldGroupRow][oldGroupCol].remove(critter);
        }

        // ...to new location.
        int newGroupRow = 2 * row + 1;
        int newGroupCol = 2 * col + 1;

        if (newGroupRow >= 0 &&
            newGroupRow < currentLevel.tiles.length &&
            newGroupCol >= 0 &&
            newGroupCol < currentLevel.tiles[0].length &&
            currentLevel.tiles[newGroupRow][newGroupCol] != null) {

            currentLevel.tiles[newGroupRow][newGroupCol].add(critter);
        }

        Timeline timeline = critter.setGridLocation(row,
                                                    col,
                                                    9 * GameStateExecute.TICK_INTERVAL_MS / 10);
        if (currentLevel != null &&
            currentLevel.scene != null &&
            timeline != null) {

            currentLevel.scene.addTimeline(timeline);
        }
        else
        {
            critter.setLocation(col * critter.width.getAsInt() + critter.width.getAsInt() / 2,
                                row * critter.height.getAsInt() + critter.height.getAsInt() / 2);
        }
    }

    private boolean isBlockedInDirection(Critter critter, int direction) {
        boolean bIsBlocked = false;

        int blockRow = critter.getGroupRow();
        int blockCol = critter.getGroupCol();

        switch (direction) {
            case PlayingScene.DIRECTION_RIGHT:
            {
                blockCol += 1;
            }
            break;

            case PlayingScene.DIRECTION_DOWN:
            {
                blockRow += 1;
            }
            break;

            case PlayingScene.DIRECTION_LEFT:
            {
                blockCol -= 1;
            }
            break;

            case PlayingScene.DIRECTION_UP:
            {
                blockRow -= 1;
            }
            break;
        }

        Sprite blockSprite = currentLevel.getSpriteFromTile(blockRow, blockCol);
        if (blockSprite != null) {
            bIsBlocked = true;
        }

        return bIsBlocked;
    }

    private int isBlocked(int startRow,
                          int startCol,
                          int destRow,
                          int destCol) {

        int blockage = LevelLayout.BLOCKAGE_NONE;

        int groupRow = 2 * startRow + 1;
        int groupCol = 2 * startCol + 1;

        int groupDestRow = 2 * destRow + 1;
        int groupDestCol = 2 * destCol + 1;

        int groupFenceRow = groupRow + (destRow - startRow);
        int groupFenceCol = groupCol + (destCol - startCol);

        blockingCritter = null;

        // Check for fence.
        Sprite blockSprite = null;

        if (groupFenceRow >= 0 &&
            groupFenceRow < currentLevel.tiles.length &&
            groupFenceCol >= 0 &&
            groupFenceCol < currentLevel.tiles[0].length &&
            currentLevel.tiles[groupFenceRow][groupFenceCol] != null &&
            currentLevel.tiles[groupFenceRow][groupFenceCol].size() > 0) {

            // Blocked by fence...but which kind?
            blockSprite = currentLevel.tiles[groupFenceRow][groupFenceCol].get(0);

            blockage = LevelLayout.BLOCKAGE_FENCE + LevelLayout.BLOCKAGE_IMMOBILE;

            if (blockSprite.getClass() == ESprite.class) {
                // Must be an electric fence. Electrify it.
                ESprite eFence = (ESprite)blockSprite;
                currentLevel.electrify(eFence, groupFenceRow, groupFenceCol, currentTurn, 0);

                // Denote electrical blockage.
                blockage |= LevelLayout.BLOCKAGE_ELECTRICAL;
            }
        }
        else if (groupDestRow > 0 &&
                 groupDestRow < currentLevel.tiles.length &&
                 groupDestCol > 0 &&
                 groupDestCol < currentLevel.tiles[0].length &&
                 currentLevel.tiles[groupDestRow][groupDestCol] != null &&
                 currentLevel.tiles[groupDestRow][groupDestCol].size() > 0) {

            // Blocked by another critter.
            blockSprite = currentLevel.tiles[groupDestRow][groupDestCol].get(0);

            if (isCritter(blockSprite)) {
                blockingCritter = (Critter)blockSprite;

                blockage = LevelLayout.BLOCKAGE_CRITTER;

                if (bAllBlocksImmobile || blockingCritter.isImmobile()) {
                    blockage |= LevelLayout.BLOCKAGE_IMMOBILE;
                }

                if (blockingCritter.isElectrified()) {
                    // Denote electrical blockage.
                    blockage |= LevelLayout.BLOCKAGE_ELECTRICAL;
                }
            }
        }

        return blockage;
    }

    /**
     * Crappy test to make sure a sprite is a critter.
     * TODO: subclass sprite for all game sprites so
     * we can add simple checks like isCritter().
     *
     * @param sprite
     * @return
     */
    private boolean isCritter(Sprite sprite) {
        boolean bIsCritter = false;

        if (currentLevel != null) {
            bIsCritter = currentLevel.isCritter(sprite);
        }

        return bIsCritter;
    }

    private void copyCritters(ArrayList<Critter>listFrom, ArrayList<Critter>listTo, boolean bClearFirst) {
        if (bClearFirst) {
            listTo.clear();
        }

        int startIndex = listTo.size();
        for (int i=0; i<listFrom.size(); ++i) {
            listTo.add(startIndex + i, listFrom.get(i));
        }
    }

    private void removeStationaryCritters() {
        boolean bFoundOne = true;

        while (bFoundOne) {
            bFoundOne = false;
            Iterator<Critter>critterator = unmovedCritters.iterator();

            while (critterator.hasNext()) {
                Critter curCritter = critterator.next();

                if (curCritter != null && curCritter.getWantDir() == 0) {
                    unmovedCritters.remove(curCritter);
                    bFoundOne = true;
                    break;
                }
            }
        }
    }

    private void computeCritterMovePriorities() {
        for (int iCritter = 0; iCritter < critterList.size(); ++iCritter) {
            Critter curCritter = critterList.get(iCritter);

            computeMovePriorityFor(curCritter);
        }
    }

    private void computeMovePriorityFor(Critter critter) {
        if (critter != null) {
            int basePriority = computeBaseMovePriorities(critter, 0, critter);
            critter.setMovePriority(basePriority);
        }
    }

    private int computeBaseMovePriorities(Critter critter,
                                          int basePriority,
                                          Critter startCritter) {

        // The more critters you're leading through the world,
        // the higher your movement priority.

        // This routine recursively traverses the critters, assuming
        // some of them form "chains" like one-way linked lists. Stopped
        // critters and gaps between critters break the chain.

        for (int i=0; i<critterList.size(); ++i) {
            Critter linkCritter = critterList.get(i);

            if (linkCritter == startCritter) {
                // Infinite loop!
                continue;
            }
            else if(linkCritter != critter &&
                    critter.isBackLinkedTo(linkCritter)) {

                // Found a back-link.
                basePriority += 1;

                // Who is back-linked to this link?
                computeBaseMovePriorities(linkCritter, basePriority, startCritter);
                break;
            }
        }

        return basePriority;
    }

    private boolean isBlockedByLevel(Critter critter) {
        boolean bBlockedByLevel = false;

        int newGroupRow = critter.getGroupRow() + critter.getRowDelta();
        int newGroupCol = critter.getGroupCol() + critter.getColDelta();

        Sprite blockSprite = currentLevel.getSpriteFromTile(newGroupRow, newGroupCol);
        bBlockedByLevel = (blockSprite != null);

        return bBlockedByLevel;
    }

    private void evaluateCritterGoals() {
        // First, let everyone scare and flock the sheep.
//        for (int iCritter = 0; iCritter < critterList.size(); ++iCritter) {
//            Critter curCritter = critterList.get(iCritter);
//
//            if (curCritter != null) {
//                spookSheep(curCritter, false);
//            }
//        }

        // Compute desired direction.
        for (int iCritter = 0; iCritter < critterList.size(); ++iCritter) {
            Critter curCritter = critterList.get(iCritter);

            if (curCritter != null) {
                curCritter.clearFlockedStart();
                curCritter.evaluateGoal(critterList);
                curCritter.computeMoveDeltas();
                curCritter.resetStoredBehaviors();
            }
        }
    }

    private void spookSheep(Critter spookingCritter, boolean bAddToMovingCritters) {
        if (currentLevel != null &&
            currentLevel.critterMax != null) {

            int critterDir = spookingCritter.getWantDir();

            int newTestRow = spookingCritter.getGroupRow() + spookingCritter.getRowDelta();
            int newTestCol = spookingCritter.getGroupCol() + spookingCritter.getColDelta();

            // Are we traversing rows (as opposed to columns)?
            boolean bTestingRows = spookingCritter.getRowDelta() != 0;

            // Use a loop limit that's guaranteed to get across the board.
            int loopLimit = Math.max(currentLevel.tiles.length, currentLevel.tiles[0].length);
            
            // FOR NOW: only spook one animal over, possibly skipping a fence section.
            loopLimit = 2;

            for (int i=0; i<loopLimit; ++i) {
                if (!currentLevel.isGroupRowInRange(newTestRow) ||
                    !currentLevel.isGroupColInRange(newTestCol)) {

                    // Moved beyond the edge of the tile set.
                    break;
                }

                boolean bFenceRow = newTestRow / 2 * 2 == newTestRow;
                boolean bFenceCol = newTestCol / 2 * 2 == newTestCol;

                Sprite sprite = currentLevel.getSpriteFromTile(newTestRow, newTestCol);

                newTestRow += spookingCritter.getRowDelta();
                newTestCol += spookingCritter.getColDelta();

                if (sprite == null) {
                    // We abort when there's a gap in critters. Since we're
                    // checking critters *and* fences, we have to make sure
                    // we're not checking a fence space before we bail.
                    if (bTestingRows && bFenceRow) {
                        // This is an even row index, which means
                        // we're on the fence, so to speak.
                        continue;
                    }
                    else if (!bTestingRows && bFenceCol) {
                        // On a fence column.
                        continue;
                    }
                    else {
                        // Found an empty critter space.
                        break;
                    }
                }

                if (sprite != null) {
                    if (!isCritter(sprite)) {
                        // Bail.
                        break;

                        // Blocked by something other than a critter.
                        // Only check one more space.
//                        loopLimit = Math.min(i + 2, loopLimit);
//                        continue;
                    }
                    else if((sprite.getClass() == CritterSheep.class || sprite.getClass() == CritterSheepBlack.class) &&
                            sprite != spookingCritter) {
                        CritterSheep curCritter = (CritterSheep)sprite;

                        // Try to spook this sheep. Only spook
                        // stationary sheep we're about to hit.
                        int sheepDir = curCritter.getWantDir();
                        if (sheepDir == 0 || bAllBlocksImmobile) {
                            // The "spooker" is going to hit us. Move in his direction.
                            curCritter.addSpookDirection(critterDir);

                            if (bAddToMovingCritters) {
                                addToMovingCritters(curCritter);
                            }
                        }
                    }
                }
                
                newTestRow += spookingCritter.getRowDelta();
                newTestCol += spookingCritter.getColDelta();
            }
        }
    }

    private void buildMasterCritterList() {

        // For safety: empty previous contents.
        critterList.clear();

        // Add Daisy.
        critterList.add(currentLevel.critterDaisy);

        // Add Max.
        critterList.add(currentLevel.critterMax);

        // Add sheep.
        copyCritters(currentLevel.flock, critterList, false);

        // Make sure all critters want to stay in the list.
        for (int i=0; i<critterList.size(); ++i) {
            critterList.get(i).clearWantsRemoved();
        }
    }
}
