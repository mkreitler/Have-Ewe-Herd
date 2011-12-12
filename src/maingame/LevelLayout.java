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

import pulpcore.math.CoreMath;
import pulpcore.image.CoreImage;
import pulpcore.sprite.Group;
import pulpcore.sprite.Sprite;
import pulpcore.sprite.ImageSprite;
import pulpcore.animation.Timeline;
import pulpcore.animation.event.RemoveSpriteEvent;
import pulpcore.animation.Easing;

public class LevelLayout {
    protected static final int  BLOCKAGE_NONE       = 0x00;
    protected static final int  BLOCKAGE_FENCE      = 0x01;
    protected static final int  BLOCKAGE_CRITTER    = 0x02;
    protected static final int  BLOCKAGE_ELECTRICAL = 0x04;
    protected static final int  BLOCKAGE_IMMOBILE   = 0x08;

    private static final int    SHEEP_FADE_TIME     = 333;
    private static final int    DEADLOCK_TICKS      = 0;
    private static final int    FENCE_BONUS         = 100;
    private static final int    STARTING_RETRY_BONUS= 400;

    protected Group[][]         tiles = {
        {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
    };

    protected boolean[][]               locks           = null;
    protected Plot[][]                  plots           = null;
    protected Group                     groundLayer     = null;
    protected CritterMax                critterMax      = null;
    protected CritterDaisy              critterDaisy    = null;
    protected ArrayList<Critter>        flock           = null;
    protected PlayingScene              scene           = null;

    private WaitSound                   moo             = null;
    private WaitSound                   baa             = null;
    private WaitSound                   shock           = null;
    private WaitSound                   crash           = null;
    private WaitSound                   banjo           = null;

    private ArrayList<WaitSound>        nonBuildSoundList   = null;
    private ArrayList<WaitSound>        buildSoundList      = null;

    private CritterSitter               critterSitter   = null;
    private int                         resourceFences  = 0;
    private int                         resourceEFences = 0;
    private LevelSetup                  currentGeometry = null;
    private int                         deadlockCount   = 0;
    private boolean                     bSkipped        = false;
    private int                         buildSoundIndex = 0;
    private int                         buildSoundDelta = 1;

    private int                         retryBonus      = STARTING_RETRY_BONUS;
    private int                         lambBonus       = 0;

    // Constructors ////////////////////////////////////////////////////////////
    public LevelLayout(PlayingScene sceneIn) {
        scene = sceneIn;

        // 'scene' MUST BE SET IN ORDER TO CALL THESE!
        groundLayer = new Group();
        groundLayer.setLocation(PlayingScene.GROUND_OFFSET_X, PlayingScene.GROUND_OFFSET_Y);
        scene.add(groundLayer);

        createPastureTiles();
        createPlots();
        createLocks();
        createCritters();
        loadSounds();

        // Make a CritterSitter.
        critterSitter = new CritterSitter(this);
    }

    public int getRetryBonus() { return retryBonus; }
    public int getLambBonus() { return lambBonus; }
    public int getFenceBonus() {
        return (getFenceResources() + getEFenceResources()) * FENCE_BONUS;
    }
    public int getLevelScore() {
        int levelScore = 0;

        if (currentGeometry != null) {
            levelScore = currentGeometry.getCompletionScore();
        }

        return levelScore;
    }

    // Public Methods //////////////////////////////////////////////////////////
    public boolean checkForFencedPlots() {
        computeFenceValues();
        return removeSheep();
    }
    
    public void setFenceResources(int amount) { resourceFences = amount; updateResources(); }
    public void setEFenceResources(int amount) { resourceEFences = amount; updateResources(); }
    public int getFenceResources() { return resourceFences; }
    public int getEFenceResources() { return resourceEFences; }
    public void useFenceResource() { resourceFences = Math.max(resourceFences - 1, 0); updateResources(); playSoundBuild(false); }
    public void useEFenceResource() { resourceEFences = Math.max(resourceEFences - 1, 0); updateResources(); playSoundBuild(false); }
    public void freeFenceResource() { ++resourceFences; updateResources(); playSoundBuild(false); }
    public void freeEFenceResource() { ++resourceEFences; updateResources(); playSoundBuild(false); }
    public void updateResources() {
        if (scene != null) {
            scene.updateResources(resourceFences, resourceEFences);
        }
    }

    public Sprite removeObject(int groupRow, int groupCol) {
        Sprite removedSprite = null;

        if (groupRow >= 0 &&
            groupRow < tiles.length &&
            groupCol >= 0 &&
            groupCol < tiles[0].length &&
            tiles[groupRow][groupCol] != null) {

            if (tiles[groupRow][groupCol].size() > 0) {
                Sprite sprite = tiles[groupRow][groupCol].get(0);

                // If this is a sheep, remove it from the flock.
                if (sprite.getClass() == CritterSheep.class ||
                    sprite.getClass() == CritterSheepBlack.class) {
                    flock.remove((CritterSheep)sprite);
                }

                removedSprite = sprite;
                tiles[groupRow][groupCol].removeAll();
            }
        }

        return removedSprite;
    }

    public void addFence(int spriteX,
                         int spriteY,
                         int groupRow,
                         int groupCol,
                         boolean bVertical,
                         boolean bWooden,
                         boolean bLock) {
        if (groupRow >= 0 &&
            groupRow < tiles.length &&
            groupCol >= 0 &&
            groupCol < tiles[0].length &&
            tiles[groupRow][groupCol] != null) {

            if (tiles[groupRow][groupCol].size() == 0) {

                if (bLock &&
                    locks != null &&
                    locks[groupRow] != null) {
                    locks[groupRow][groupCol] = true;
                }

                Sprite newFence = null;

                int imageIndex = PlayingScene.FENCE_HORIZONTAL;
                if (bVertical) {
                    imageIndex = PlayingScene.FENCE_VERTICAL;
                }

                if (bWooden) {
                    newFence = new ImageSprite(scene.fence[imageIndex],
                                               spriteX,
                                               spriteY);
                }
                else
                {
                    ESprite eFenceSprite = new ESprite(scene.eFence[imageIndex],
                                                       spriteX,
                                                       spriteY);
                    newFence = eFenceSprite;
                }

                newFence.setAnchor(Sprite.CENTER);

                tiles[groupRow][groupCol].add(newFence);
            }
        }
    }

    public void setSkipped() { bSkipped = true; }
    public boolean skipped() { return bSkipped; }
    public void clearSkipped() { bSkipped = false; }

    public void electrify(ESprite eSprite, int groupRow, int groupCol, int turn, int depth) {
        // Recursively electrify the specified eSprite and
        // its neighbors.

        // Only electrify uncharged objects.
        if (eSprite != null && !eSprite.isElectrified()) {
            
            // Electrify this tile.
            eSprite.electrify(turn);

            // Electrify nearest neighbors in all directions.
            int newGroupRow     = -1;
            int newGroupCol     = -1;
            ESprite nextSprite  = null;

            // Right.
            newGroupRow = groupRow;
            newGroupCol = groupCol + 1;
            nextSprite = getESpriteFromTile(newGroupRow, newGroupCol);

            Sprite woodSprite = getSpriteFromTile(newGroupRow, newGroupCol);
            if (woodSprite != null &&
                woodSprite.getClass() != Sprite.class) {
                // Not just a sprite...so it must be some kind of Esprite.
                woodSprite = null;
            }

            // Allow electrification two spaces away so we can conduct
            // to adjacent fences or animals. However, make sure we're
            // not conducting electricity through wooden fences.
            if (woodSprite == null) {
                if (nextSprite == null) {
                    ++newGroupCol;
                    nextSprite = getESpriteFromTile(newGroupRow, newGroupCol);
                }

                if (nextSprite != null) {
                    electrify(nextSprite, newGroupRow, newGroupCol, turn, depth + 1);
                }
            }

            // Down.
            newGroupRow = groupRow + 1;
            newGroupCol = groupCol;
            nextSprite = getESpriteFromTile(newGroupRow, newGroupCol);

            woodSprite = getSpriteFromTile(newGroupRow, newGroupCol);
            if (woodSprite != null &&
                woodSprite.getClass() != Sprite.class) {
                // Not just a sprite...so it must be some kind of Esprite.
                woodSprite = null;
            }

            // Allow electrification two spaces away so we can conduct
            // to adjacent fences or animals.
            if (woodSprite == null) {
                if (nextSprite == null) {
                    ++newGroupRow;
                    nextSprite = getESpriteFromTile(newGroupRow, newGroupCol);
                }

                if (nextSprite != null) {
                    electrify(nextSprite, newGroupRow, newGroupCol, turn, depth + 1);
                }
            }

            // Left.
            newGroupRow = groupRow;
            newGroupCol = groupCol - 1;
            nextSprite = getESpriteFromTile(newGroupRow, newGroupCol);

            woodSprite = getSpriteFromTile(newGroupRow, newGroupCol);
            if (woodSprite != null &&
                woodSprite.getClass() != Sprite.class) {
                // Not just a sprite...so it must be some kind of Esprite.
                woodSprite = null;
            }

            // Allow electrification two spaces away so we can conduct
            // to adjacent fences or animals.
            if (woodSprite == null) {
                if (nextSprite == null) {
                    --newGroupCol;
                    nextSprite = getESpriteFromTile(newGroupRow, newGroupCol);
                }

                if (nextSprite != null) {
                    electrify(nextSprite, newGroupRow, newGroupCol, turn, depth + 1);
                }
            }

            // Up.
            newGroupRow = groupRow - 1;
            newGroupCol = groupCol;
            nextSprite = getESpriteFromTile(newGroupRow, newGroupCol);

            woodSprite = getSpriteFromTile(newGroupRow, newGroupCol);
            if (woodSprite != null &&
                woodSprite.getClass() != Sprite.class) {
                // Not just a sprite...so it must be some kind of Esprite.
                woodSprite = null;
            }

            // Allow electrification two spaces away so we can conduct
            // to adjacent fences or animals.
            if (woodSprite == null) {
                if (nextSprite == null) {
                    --newGroupRow;
                    nextSprite = getESpriteFromTile(newGroupRow, newGroupCol);
                }

                if (nextSprite != null) {
                    electrify(nextSprite, newGroupRow, newGroupCol, turn, depth + 1);
                }
            }

            if (depth == 0) playSoundShock(false);
        }
    }

    public ESprite getESpriteFromTile(int groupRow, int groupCol) {
        ESprite eSprite = null;

        Sprite sprite = getSpriteFromTile(groupRow, groupCol);

        if (sprite != null &&
            (sprite.getClass() == ESprite.class || isCritter(sprite)) ) {
            eSprite = (ESprite)sprite;
        }

        return eSprite;
    }

    public Sprite getSpriteFromTile(int groupRow, int groupCol) {
        Sprite sprite = null;

        if (groupRow >= 0 &&
            groupRow < tiles.length &&
            groupCol >= 0 &&
            groupCol < tiles[0].length &&
            tiles[groupRow][groupCol] != null &&
            tiles[groupRow][groupCol].size() > 0) {

            sprite = tiles[groupRow][groupCol].get(0);
        }

        return sprite;
    }

    public boolean isGroupRowInRange(int row) { return row >= 0 && row < tiles.length; }
    public boolean isGroupColInRange(int col) { return col >= 0 && col < tiles[0].length; }
    public boolean isRowInRange(int row) { return row >= 0 && row < tiles.length / 2; }
    public boolean isColInRange(int col) { return col >= 0 && col < tiles[0].length / 2; }

    public void clearTile(int groupRow, int groupCol) {

        if (groupRow >= 0 &&
            groupRow < tiles.length &&
            groupCol >= 0 &&
            groupCol < tiles[0].length &&
            tiles[groupRow][groupCol] != null) {

            tiles[groupRow][groupCol].removeAll();
        }
    }

    public int getFencePosX(int iCol, boolean bVertical) {
        int fencePosX = 0;

        if (scene.terrainImages[0] != null) {

            int width = scene.terrainImages[0].getWidth();

            if (bVertical) {
                fencePosX = iCol * width;
            }
            else {
                fencePosX = iCol * width + width / 2;
            }
        }

        return fencePosX;
    }

    public int getFencePosY(int iRow, boolean bVertical) {
        int fencePosY = 0;

        if (scene.terrainImages[0] != null) {
            int height = scene.terrainImages[0].getHeight();

            if (bVertical) {
                fencePosY = iRow * height + height / 2;
            }
            else {
                fencePosY = iRow * height;
            }
        }

        return fencePosY;
    }

    public int getPlotRows() {
        int plotRows = 0;

        if (plots != null) {
            plotRows = plots.length;
        }

        return plotRows;
    }

    public int getPlotCols() {
        int cols = 0;

        if (plots[0] != null) {
            cols = plots[0].length;
        }

        return cols;
    }

    public int getSpriteCountForGroup(int iRow, int iCol) {
        int spritesInPlot = 0;

        if (iRow >= 0 &&
            iRow < tiles.length &&
            iCol >= 0 &&
            iCol < tiles[0].length &&
            tiles[iRow][iCol] != null) {

            spritesInPlot = tiles[iRow][iCol].size();
        }

        return spritesInPlot;
    }

    public boolean hasRoomForSprite(int iRow, int iCol) {
        boolean bHasRoom = false;

        if (tiles[0] != null &&
            iRow >= 0 &&
            iCol >= 0 &&
            iRow < tiles.length &&
            iCol < tiles[0].length) {
            if (tiles[iRow][iCol].size() == 0) {
                bHasRoom = true;
            }
        }

        return bHasRoom;
    }

    public boolean isMouseOverMax() {
        boolean bMouseOverMax = false;

        if (critterMax != null) {

            bMouseOverMax = critterMax.isMouseOver();
        }

        return bMouseOverMax;
    }

    public boolean isOccupied(int groupRow, int groupCol) {
        boolean bIsOccupied = true;

        if (groupRow >= 0 &&
            groupRow < tiles.length &&
            groupCol >= 0 &&
            groupCol < tiles[0].length &&
            tiles[groupRow][groupCol] != null) {

            bIsOccupied = tiles[groupRow][groupCol].size() > 0;
        }

        return bIsOccupied;
    }

    public void showLevelEndMessage() {
        if (scene != null && currentGeometry != null) {
        String endMessage = currentGeometry.getMessage(false);
            if (endMessage != null) {
                scene.showMessage(endMessage,
                                  currentGeometry.getMessageColor(false),
                                  currentGeometry.getMessageDuration(false));
            }
        }
    }

    public boolean isLocked(int groupRow, int groupCol) {
        boolean bIsLocked = false;

        if (locks != null &&
            locks[0] != null &&
            groupRow >= 0 &&
            groupRow < locks.length &&
            groupCol >= 0 &&
            groupCol < locks[groupRow].length) {

            bIsLocked = locks[groupRow][groupCol];
        }

        return bIsLocked;
    }

    public void resetSounds() {
        if (nonBuildSoundList != null) {
            for (int i=0; i<nonBuildSoundList.size(); ++i) {
                WaitSound curSound = nonBuildSoundList.get(i);

                if (curSound != null) {
                    curSound.reset();
                }
            }
        }

        if (buildSoundList != null) {
            for (int i=0; i<buildSoundList.size(); ++i) {
                WaitSound curSound = buildSoundList.get(i);

                if (curSound != null) {
                    curSound.reset();
                }
            }

            buildSoundIndex = 0;
            buildSoundDelta = 1;
        }
    }

    public void setUp(LevelSetup geometry) {
        // Traverse the level geometry and create the
        // appropriate objects.
        currentGeometry = geometry;

        resetLevel();

        lambBonus  = 0;

        int groupRows = geometry.getRows();
        int groupCols = geometry.getCols();

        for (int iGroupRow = 0; iGroupRow < groupRows; ++iGroupRow) {
            for (int iGroupCol = 0; iGroupCol < groupCols; ++iGroupCol) {

                char currentObject = geometry.getCharAt(iGroupRow, iGroupCol);
                
                switch (currentObject) {
                    case '0':
                    {
                        // Empty pasture land.
                    }
                    break;
                    
                    case '_':
                    case '-':
                    {
                        // Horizontal fence, wooden.
                        int iRow = iGroupRow / 2;
                        int iCol = iGroupCol / 2;
                        this.addFence(getFencePosX(iCol, false),
                                      getFencePosY(iRow, false),
                                      iGroupRow,
                                      iGroupCol,
                                      false,
                                      true,
                                      true);
                    }
                    break;
                    
                    case '|':
                    {
                        // Vertical fence, wooden.
                        int iRow = iGroupRow / 2;
                        int iCol = iGroupCol / 2;
                        this.addFence(getFencePosX(iCol, true),
                                      getFencePosY(iRow, true),
                                      iGroupRow,
                                      iGroupCol,
                                      true,
                                      true,
                                      true);
                    }
                    break;

                    case '.':
                    case '=':
                    {
                        // Horizontal fence, electric.
                        int iRow = iGroupRow / 2;
                        int iCol = iGroupCol / 2;
                        this.addFence(getFencePosX(iCol, false),
                                      getFencePosY(iRow, false),
                                      iGroupRow,
                                      iGroupCol,
                                      false,
                                      false,
                                      true);
                    }
                    break;
                    
                    case ':':
                    {
                        // Vertical fence, electric.
                        int iRow = iGroupRow / 2;
                        int iCol = iGroupCol / 2;
                        this.addFence(getFencePosX(iCol, true),
                                      getFencePosY(iRow, true),
                                      iGroupRow,
                                      iGroupCol,
                                      true,
                                      false,
                                      true);
                    }
                    break;
                    
                    case 'M':
                    {
                        // Max.
                        addCritter(critterMax, iGroupRow, iGroupCol);
                    }
                    break;
                    
                    case 'L':
                    {
                        // Lamb.
                        if (critterSitter != null) {
                            CoreImage lambImage = CoreImage.load("lamb.png");

                            int iRow = iGroupRow / 2;
                            int iCol = iGroupCol / 2;

                            CritterLamb lamb = new CritterLamb(lambImage, 0, 0);
                            critterSitter.addLamb(lamb);
                            lamb.setGridLocation(iRow, iCol, 0);
                        }
                    }
                    break;

                    case 'D':
                    {
                        // Dasiy.
                        addCritter(critterDaisy, iGroupRow, iGroupCol);
                    }
                    break;
                    
                    case 'S':
                    {
                        // Sheep.
                        CritterSheep newSheep = new CritterSheep(CoreImage.load("sheep.png"), 0, 0);
                        addCritter(newSheep, iGroupRow, iGroupCol);
                    }
                    break;

                    case 'B':
                    {
                        // Black sheep.
                        CritterSheepBlack newSheep = new CritterSheepBlack(CoreImage.load("sheep_black.png"), 0, 0);
                        addCritter(newSheep, iGroupRow, iGroupCol);
                    }
                    break;
                    
                    default:
                    {
                        // Invalid character.
                    }
                    break;
                }
            }
        }

        postSetupLevel();
    }

    public void resetRetryBonus() {
        retryBonus = STARTING_RETRY_BONUS;
    }

    /**
     * Tick all board elements. Return 'true' if anyone moved.
     *
     * @param dt
     * @return
     */
    public boolean tick(int dt, int turn) {
        boolean bSomeoneMoved = false;

        if (critterSitter != null) {
            bSomeoneMoved = critterSitter.processTick(turn);

            if (critterSitter.levelFailed()) {

                scene.setState(scene.stateRoundFailed);

                // Prevent twitching.
                resetAllCritters(false);
            }
        }

        // Update state of electrified objects.
        // Do this after everything moves to ensure
        // electrification lasts through the next
        // turn.
        updateElectrification(turn);

        if (!bSomeoneMoved) {
            // Give deferred actions a chance to catch up.
            ++deadlockCount;

            if (deadlockCount < DEADLOCK_TICKS) {
                bSomeoneMoved = true;
            }
        }
        else {
            deadlockCount = 0;
        }

        return bSomeoneMoved;
    }

    public boolean levelFailed() {
        boolean bFailed = false;

        if (critterSitter != null) {
            bFailed = critterSitter.levelFailed();
        }

        return bFailed;
    }

    public boolean wantsStart() {
        boolean bWantsStart = false;

        if (pulpcore.Input.isMousePressed() &&
            critterMax != null) {
            bWantsStart = critterMax.isMousePressed();
        }

        return bWantsStart;
    }

    public void updateSounds(int elapsedTime) {
        if (nonBuildSoundList != null) {
            for (int i=0; i<nonBuildSoundList.size(); ++i) {
                WaitSound curSound = nonBuildSoundList.get(i);
                if (curSound != null) {
                    curSound.update(elapsedTime);
                }
            }
        }

        if (buildSoundList != null) {
            for (int i=0; i<buildSoundList.size(); ++i) {
                WaitSound curSound = buildSoundList.get(i);
                if (curSound != null) {
                    curSound.update(elapsedTime);
                }
            }
        }
    }

    protected void updateLambs() {
        if (critterSitter != null) {
            if (scene.getCurrentState() == scene.stateExecute) {
                lambBonus = critterSitter.updateLambs();
            }
            else
            {
                critterSitter.updateLambs();
            }
        }
    }

    // Protected Methods ///////////////////////////////////////////////////////
    protected boolean hasSheep(int iRow, int iCol) {
        boolean bHasSheep = false;

        int groupRow = iRow * 2 + 1;
        int groupCol = iCol * 2 + 1;
        
        if (groupRow >= 0 &&
            groupRow < tiles.length &&
            groupCol >= 0 &&
            groupCol < tiles[0].length &&
            tiles[groupRow][groupCol] != null) {

            Sprite occupant = tiles[groupRow][groupCol].get(0);

            if (occupant != null) {
                Class spriteClass = occupant.getClass();

                if (spriteClass == CritterSheep.class ||
                    spriteClass == CritterSheepBlack.class) {
                    bHasSheep = true;
                }
            }
        }

        return bHasSheep;
    }

    protected void popLambs() {
        if (scene != null && critterSitter != null) {
            critterSitter.popLambs(groundLayer, scene.getParticleLayer());
        }
    }

    protected void setAllCrittersAsUnmoved() {
        if (critterDaisy != null) critterDaisy.clearHasMoved();

        if (critterMax != null) critterMax.clearHasMoved();

        for (int i=0; i<flock.size(); ++i) {
            Critter sheep = flock.get(i);
            if (sheep != null) sheep.clearHasMoved();
        }
    }

    protected void startExecution() {
        lockExistingFences();

        if (critterMax != null && critterDaisy != null) {

            int rowDelta = critterDaisy.getRow() - critterMax.getRow();
            int colDelta = critterDaisy.getCol() - critterMax.getCol();

            if (rowDelta == 0) {
                if (colDelta < 0) {
                    critterMax.setStartDirection(PlayingScene.DIRECTION_LEFT);
                }
                else {
                    critterMax.setStartDirection(PlayingScene.DIRECTION_RIGHT);
                }
            }
            else if (colDelta == 0) {
                if (rowDelta < 0) {
                    critterMax.setStartDirection(PlayingScene.DIRECTION_UP);
                }
                else {
                    critterMax.setStartDirection(PlayingScene.DIRECTION_DOWN);
                }
            }

            deadlockCount = 0;
        }

        playSoundBull(false);
    }

    protected void destroyFence(int groupRow, int groupCol) {
        Sprite fence = this.getSpriteFromTile(groupRow, groupCol);

        if (fence != null && !isCritter(fence)) {

            // Found a fence.
            Sprite fenceSprite = removeObject(groupRow, groupCol);

            // Throw the fence around.
            int duration = 500 + CoreMath.rand(200);
            int moveDistance = CoreMath.rand(50, 80);
            double moveDirection = CoreMath.rand(0, 2*Math.PI);

            int startX = fenceSprite.x.getAsInt();
            int startY = fenceSprite.y.getAsInt();

            int goalX = startX + (int)(moveDistance * Math.cos(moveDirection));
            int goalY = startY + (int)(moveDistance * Math.sin(moveDirection));

            double startAngle = fenceSprite.angle.getAsInt() * Math.PI / 180.f;
            double endAngle   = startAngle + (double)(1 - 2 * CoreMath.rand(0, 1)) * -3 * Math.PI;

            scene.highGroup.add(fenceSprite);

            Timeline timeline = new Timeline();

            timeline.animateTo(fenceSprite.x, goalX, duration, Easing.REGULAR_OUT);
            timeline.animateTo(fenceSprite.y, goalY, duration, Easing.REGULAR_OUT);
            timeline.animate(fenceSprite.angle, startAngle, endAngle, duration);
            timeline.at(0).animate(fenceSprite.alpha, 255, 0, duration);
            
            timeline.add(new RemoveSpriteEvent(scene.highGroup, fenceSprite, duration));

            if (locks != null && locks[groupRow] != null) {
                locks[groupRow][groupCol] = false;
            }

            scene.addTimeline(timeline);

            playSoundCrash(false);
        }
    }

    protected void clearTiles() {
        for (int iRow = 0; iRow < tiles.length; ++iRow) {
            for (int iCol = 0; iCol < tiles[0].length; ++iCol) {
                if (tiles[iRow][iCol] != null) {
                    tiles[iRow][iCol].removeAll();
                }
            }
        }
    }

    protected void clearNonFenceTiles() {
        for (int iRow = 0; iRow < tiles.length; ++iRow) {
            for (int iCol = 0; iCol < tiles[0].length; ++iCol) {
                if (tiles[iRow][iCol] != null) {
                    Sprite tileSprite = tiles[iRow][iCol].get(0);
                    if (tileSprite != null &&
                        tileSprite.getClass() != ImageSprite.class &&
                        tileSprite.getClass() != ESprite.class) {
                        tiles[iRow][iCol].removeAll();
                    }
                }
            }
        }
    }

    protected void clearPlots() {
        for (int iRow = 0; iRow < plots.length; ++iRow) {
            if (plots[iRow] != null) {
                for (int iCol = 0; iCol < plots[0].length; ++iCol) {
                    plots[iRow][iCol].reset();
                }
            }
        }
    }

    protected boolean isCritter(Sprite sprite) {
        boolean bIsCritter = false;

        // 'Critter' is an abstract class, so there's no need
        // to check for it explicitly.

        if (sprite != null)
        {
            Class spriteClass = sprite.getClass();

            bIsCritter = spriteClass == CritterSheep.class ||
                         spriteClass == CritterMax.class ||
                         spriteClass == CritterDaisy.class ||
                         spriteClass == CritterSheepBlack.class ||
                         spriteClass == CritterLamb.class;
        }

        return bIsCritter;
    }

    protected void lockExistingFences() {
        if (locks != null) {
            for (int iRow = 0; iRow < tiles.length; ++iRow) {
                if (locks[iRow] != null) {
                    for (int iCol = 0; iCol < tiles[0].length; ++iCol) {
                        if (tiles[iRow][iCol].size() > 0) {
                            Sprite testSprite = tiles[iRow][iCol].get(0);

                            if (!isCritter(testSprite)) {
                                // Must be fence.
                                locks[iRow][iCol] = true;
                            }
                        }
                    }
                }
            }
        }
    }

    protected void retryLevel() {
        if (critterSitter != null) {
            critterSitter.reset();
        }

        clearNonFenceTiles();
        clearPlots();
        clearLocks();

        // Reset all critters.
        resetAllCritters(true);

        resetLevel();

        int usedFence   = 0;
        int usedEFence  = 0;

        int groupRows = currentGeometry.getRows();
        int groupCols = currentGeometry.getCols();

        for (int iGroupRow = 0; iGroupRow < groupRows; ++iGroupRow) {
            for (int iGroupCol = 0; iGroupCol < groupCols; ++iGroupCol) {
                boolean bFoundFence     = false;
                boolean bFoundEFence    = false;

                Sprite fenceSprite = tiles[iGroupRow][iGroupCol].get(0);
                if (fenceSprite != null &&
                    fenceSprite.getClass() == ImageSprite.class) {
                    usedFence += 1;
                    bFoundFence = true;
                }
                else if (fenceSprite != null &&
                         fenceSprite.getClass() == ESprite.class) {
                    usedEFence += 1;
                    bFoundEFence = true;
                }

                char currentObject = currentGeometry.getCharAt(iGroupRow, iGroupCol);

                switch (currentObject) {
                    case '0':
                    {
                        // Empty pasture land.
                    }
                    break;

                    case '_':
                    case '-':
                    {
                        // Horizontal fence, wooden.
                        if (!bFoundFence) {
                            int iRow = iGroupRow / 2;
                            int iCol = iGroupCol / 2;
                            this.addFence(getFencePosX(iCol, false),
                                          getFencePosY(iRow, false),
                                          iGroupRow,
                                          iGroupCol,
                                          false,
                                          true,
                                          true);
                        }
                        else {
                            this.locks[iGroupRow][iGroupCol] = true;
                            usedFence -= 1;
                        }
                    }
                    break;

                    case '|':
                    {
                        // Vertical fence, wooden.
                        if (!bFoundFence) {
                            int iRow = iGroupRow / 2;
                            int iCol = iGroupCol / 2;
                            this.addFence(getFencePosX(iCol, true),
                                          getFencePosY(iRow, true),
                                          iGroupRow,
                                          iGroupCol,
                                          true,
                                          true,
                                          true);
                        }
                        else {
                            this.locks[iGroupRow][iGroupCol] = true;
                            usedFence -= 1;
                        }
                    }
                    break;

                    case '.':
                    case '=':
                    {
                        // Horizontal fence, electric.
                        if (!bFoundEFence) {
                            int iRow = iGroupRow / 2;
                            int iCol = iGroupCol / 2;
                            this.addFence(getFencePosX(iCol, false),
                                          getFencePosY(iRow, false),
                                          iGroupRow,
                                          iGroupCol,
                                          false,
                                          false,
                                          true);
                        }
                        else {
                            this.locks[iGroupRow][iGroupCol] = true;
                            usedEFence -= 1;
                        }
                    }
                    break;

                    case ':':
                    {
                        // Vertical fence, electric.
                        if (!bFoundEFence) {
                            int iRow = iGroupRow / 2;
                            int iCol = iGroupCol / 2;
                            this.addFence(getFencePosX(iCol, true),
                                          getFencePosY(iRow, true),
                                          iGroupRow,
                                          iGroupCol,
                                          true,
                                          false,
                                          true);
                        }
                        else {
                            this.locks[iGroupRow][iGroupCol] = true;
                            usedEFence -= 1;
                        }
                    }
                    break;

                    case 'M':
                    {
                        // Max.
                        addCritter(critterMax, iGroupRow, iGroupCol);
                    }
                    break;

                    case 'L':
                    {
                        // Lamb.
                        if (critterSitter != null) {
                            CoreImage lambImage = CoreImage.load("lamb.png");

                            int iRow = iGroupRow / 2;
                            int iCol = iGroupCol / 2;

                            CritterLamb lamb = new CritterLamb(lambImage, 0, 0);
                            critterSitter.addLamb(lamb);
                            lamb.setGridLocation(iRow, iCol, 0);
                        }
                    }
                    break;

                    case 'D':
                    {
                        // Dasiy.
                        addCritter(critterDaisy, iGroupRow, iGroupCol);
                    }
                    break;

                    case 'S':
                    {
                        // Sheep.
                        CritterSheep newSheep = new CritterSheep(CoreImage.load("sheep.png"), 0, 0);
                        addCritter(newSheep, iGroupRow, iGroupCol);
                    }
                    break;

                    case 'B':
                    {
                        // Black sheep.
                        CritterSheepBlack newSheep = new CritterSheepBlack(CoreImage.load("sheep_black.png"), 0, 0);
                        addCritter(newSheep, iGroupRow, iGroupCol);
                    }
                    break;

                    default:
                    {
                        // Invalid character.
                    }
                    break;
                }
            }
        }

        // Account for the used fence.
        setFenceResources(currentGeometry.getFenceResources() - usedFence);
        setEFenceResources(currentGeometry.getEFenceResources() - usedEFence);

        postSetupLevel();
    }

    protected void postSetupLevel() {
        addLambs();
        resetElectrification();
    }

    protected void startLevel() {
        if (critterSitter != null) {
            critterSitter.reset();
        }

        createGround();

        clearTiles();
        clearPlots();
        clearLocks();
        
        // Reset all critters.
        resetAllCritters(true);

        // Reset all electrified objects.
        updateElectrification(-1);
    }

    private static final int SOUND_REPLAY_WAIT_MS   = 500;

    private int bullSoundTimer = 0;
    private int sheepSoundTimer = 0;
    private int crashSoundTimer = 0;
    private int banjoSoundTimer = 0;

    protected void playSoundShock(boolean bReset) {
        if (shock != null) shock.play(false);
    }

    protected void playSoundBull(boolean bReset) {
        if (moo != null) moo.play(false);
    }

    protected void playSoundSheep(boolean bReset) {
        if (baa != null) baa.play(false);
    }

    protected void playSoundCrash(boolean bReset) {
        if (crash != null) crash.play(false);
    }

    protected void playSoundBanjo(boolean bReset) {
        if (bReset) {
            banjoSoundTimer = 0;
        }

        if (banjoSoundTimer == 0) {
            if (banjo != null) {
                banjo.play(false);
                banjoSoundTimer = SOUND_REPLAY_WAIT_MS;
            }
        }
    }

    protected void playSoundBuild(boolean bReset) {
        if (bReset) {
            buildSoundIndex = 0;
            buildSoundDelta = 1;
        }

        if (buildSoundList != null &&
            buildSoundIndex >= 0 &&
            buildSoundIndex < buildSoundList.size()) {

            WaitSound buildSound = buildSoundList.get(buildSoundIndex);
            if (buildSound != null) {
                buildSound.play(false);

                buildSoundIndex += buildSoundDelta;
                if (buildSoundIndex >= buildSoundList.size()) {
                    buildSoundIndex = buildSoundList.size() - 2;
                    buildSoundDelta *= -1;
                }
                else if (buildSoundIndex < 0) {
                    buildSoundIndex = 1;
                    buildSoundDelta *= -1;
                }
            }
        }
    }

    protected void addLambs() {
        if (critterSitter != null) {
            critterSitter.insertLambs(groundLayer);
        }
    }

    // Private Methods /////////////////////////////////////////////////////////
    private void loadSounds() {
        if (nonBuildSoundList == null) {
            nonBuildSoundList = new ArrayList<WaitSound>();
        }
        else {
            nonBuildSoundList.clear();
        }

        if (nonBuildSoundList != null) {
            moo = new WaitSound("sound_bull.wav");
            baa = new WaitSound("sound_sheep.wav");
            shock = new WaitSound("sound_zap.wav");
            crash = new WaitSound("sound_crash.wav");
            banjo = new WaitSound("sound_banjo.wav");
            
            nonBuildSoundList.add(moo);
            nonBuildSoundList.add(baa);
            nonBuildSoundList.add(shock);
            nonBuildSoundList.add(crash);
            nonBuildSoundList.add(banjo);
        }

        if (buildSoundList == null) {
            buildSoundList = new ArrayList<WaitSound>();
        }
        else {
            buildSoundList.clear();
        }

        if (buildSoundList != null) {
            buildSoundList.add(new WaitSound("sound_build_handsaw.wav"));
            buildSoundList.add(new WaitSound("sound_build_hammering.wav"));
            buildSoundList.add(new WaitSound("sound_build_electricsaw.wav"));
        }

        buildSoundIndex = 0;
        buildSoundDelta = 1;
    }

    private boolean removeSheep() {
        if (plots != null &&
            plots[0] != null &&
            flock != null &&
            scene != null &&
            critterSitter != null) {

            boolean bRemovedSheep = false;

            for (int iRow = 0; iRow < plots.length; ++iRow) {
                for (int iCol = 0; iCol < plots[0].length; ++iCol) {
                    if (plots[iRow][iCol].isEnclosed(0) &&
                        plots[iRow][iCol].getGroup() != null) {

                        // What is inside this plot?
                        Group plotGroup = plots[iRow][iCol].getGroup();

                        Sprite plotSprite = plotGroup.get(0);
                        if (plotSprite != null &&
                            (plotSprite.getClass() == CritterSheep.class || plotSprite.getClass() == CritterSheepBlack.class)) {

                            CritterSheep plotSheep = (CritterSheep)plotSprite;
                            flock.remove(plotSheep);

                            // Remove the sheep.
                            Timeline timeline = new Timeline();
                            timeline.at(0).animate(plotSheep.alpha, 255, 0, SHEEP_FADE_TIME);
                            timeline.add(new RemoveSpriteEvent(plotGroup,
                                         plotSheep,
                                         SHEEP_FADE_TIME));

                            // Remove any associated lamb.
                            CritterLamb lamb = critterSitter.getLamb(iRow, iCol);
                            if (lamb != null) {
                                timeline.at(0).animate(lamb.alpha, 255, 0, SHEEP_FADE_TIME);
                                timeline.add(new RemoveSpriteEvent(lamb.getParent(),
                                             lamb,
                                             SHEEP_FADE_TIME));
                            }

                            bRemovedSheep = true;

                            scene.addTimeline(timeline);
                        }
                    }
                }
            }

            if (bRemovedSheep) {
                resetSounds();
                lockExistingFences();
                playSoundSheep(false);
            }
        }

        return flock.isEmpty();
    }

    private void computeFenceValues() {
        for (int iRow = 0; iRow < plots.length; ++iRow) {

            int topFenceRow = 2 * iRow;
            topFenceRow     = Math.min(topFenceRow, tiles.length - 3);
            Group[] fenceTop = tiles[topFenceRow];
            Group[] fenceMid = tiles[topFenceRow + 1];
            Group[] fenceBtm = tiles[topFenceRow + 2];

            for (int iCol = 0; iCol < plots[0].length; ++iCol) {
                int fenceLeftCol = Math.min(2 * iCol, fenceTop.length - 3);

                if (plots[iRow][iCol] != null) {

                    // Clear out old values. Necessary because
                    // the user may have added new fences, which
                    // changes enclosure values.
                    plots[iRow][iCol].reset();

                    boolean bFenceAbove   = fenceTop[fenceLeftCol + 1].size() > 0;
                    boolean bFenceBelow   = fenceBtm[fenceLeftCol + 1].size() > 0;
                    boolean bFenceToRight = fenceMid[fenceLeftCol + 2].size() > 0;
                    boolean bFenceToLeft  = fenceMid[fenceLeftCol].size() > 0;

                    int fenceValue = 0;
                    if (bFenceToRight) fenceValue += PlayingScene.FENCED_RIGHT;
                    if (bFenceBelow) fenceValue += PlayingScene.FENCED_DOWN;
                    if (bFenceToLeft) fenceValue += PlayingScene.FENCED_LEFT;
                    if (bFenceAbove) fenceValue += PlayingScene.FENCED_UP;

                    plots[iRow][iCol].setFenceValue(fenceValue);
                }
            }
        }

        // Mark enclosures.
        for (int iRow = 0; iRow < plots.length; ++iRow) {
            for (int iCol = 0; iCol < plots[0].length; ++iCol) {
                if (plots[iRow][iCol] != null) {
                    plots[iRow][iCol].isEnclosed(0);
                }
            }
        }
    }

    protected void resetAllCritters(boolean bResetTimeline) {
        if (critterDaisy != null) {
            critterDaisy.reset();

            if (bResetTimeline) {
                scene.removeTimeline(critterDaisy.getTimeline(), true);
            }
        }

        if (critterMax != null) {
            critterMax.reset();

            if (bResetTimeline) {
                scene.removeTimeline(critterMax.getTimeline(), true);
            }
        }

        if (flock != null) {
            for (int i=0; i<flock.size(); ++i) {
                Critter critter = flock.get(i);
                if (critter != null) {
                    critter.reset();

                    if (bResetTimeline) {
                        scene.removeTimeline(critter.getTimeline(), true);
                    }
                }
            }
        }
    }

    protected Critter getCritterInDirection(Critter critter, int direction, boolean bCheckBlock) {
        Critter neighbor = null;

        int groupRow = critter.getGroupRow();
        int groupCol = critter.getGroupCol();
        int blockRow = groupRow;
        int blockCol = groupCol;

        switch (direction) {
            case PlayingScene.DIRECTION_RIGHT:
            {
                groupCol += 2;
                blockCol += 1;
            }
            break;

            case PlayingScene.DIRECTION_DOWN:
            {
                groupRow += 2;
                blockRow += 1;
            }
            break;

            case PlayingScene.DIRECTION_LEFT:
            {
                groupCol -= 2;
                blockCol -= 1;
            }
            break;

            case PlayingScene.DIRECTION_UP:
            {
                groupRow -= 2;
                blockRow -= 1;
            }
            break;
        }

        Sprite sprite = this.getSpriteFromTile(groupRow, groupCol);
        if (sprite != null && isCritter(sprite)) {
            neighbor = (Critter)sprite;
        }

        if (bCheckBlock) {
            Sprite blockSprite = this.getSpriteFromTile(blockRow, blockCol);
            if (blockSprite != null) {
                neighbor = null;
            }
        }

        return neighbor;
    }

    private void updateElectrification(int currentTurn) {
        for (int iRow = 0; iRow < tiles.length; ++iRow) {
            for (int iCol = 0; iCol < tiles[0].length; ++iCol) {
                if (tiles[iRow][iCol] != null) {
                    Sprite tileSprite = tiles[iRow][iCol].get(0);

                    if (tileSprite != null) {
                        boolean bIsElectricSprite = (tileSprite.getClass() == ESprite.class);
                        boolean bIsCritterSprite  = (isCritter(tileSprite));

                        if (bIsElectricSprite || bIsCritterSprite) {

                            ESprite eSprite = (ESprite)tileSprite;
                            if (currentTurn > eSprite.electrifiedWhen()) {
                                eSprite.resetElectrified();
                            }
                        }
                    }
                }
            }
        }
    }

    private void addCritter(Critter newCritter, int iGroupRow, int iGroupCol) {
        if (iGroupRow >= 0 &&
            iGroupRow < tiles.length &&
            iGroupCol >= 0 &&
            iGroupCol < tiles[0].length &&
            tiles[iGroupRow][iGroupCol] != null) {

            int spriteX = 0;
            int spriteY = 0;

            int width = newCritter.getImage().getWidth();
            int height = newCritter.getImage().getHeight();

            newCritter.setLocation(iGroupCol / 2 * width + width / 2,
                                   iGroupRow / 2 * height + height / 2);

            newCritter.updateHistory();

            tiles[iGroupRow][iGroupCol].add(newCritter);

            // Add sheep to the flock.
            if (newCritter.getClass() == CritterSheep.class ||
                newCritter.getClass() == CritterSheepBlack.class) {
                flock.add(newCritter);
            }
        }
    }

    private void createCritters() {
        critterMax      = new CritterMax(CoreImage.load("bull.png"), 0, 0);
        critterDaisy    = new CritterDaisy(CoreImage.load("cow.png"), 0, 0);

        // Create sheep.
        flock           = new ArrayList<Critter>();
    }

    private void createGround() {
        if (scene != null && groundLayer != null) {
            groundLayer.removeAll();

            // Populate the ground layer with random terrain.
            int spriteWidth = scene.terrainImages[0].getWidth();
            int spriteHeight = scene.terrainImages[0].getHeight();

            for (int iRow = 0; iRow < tiles.length / 2; ++iRow) {
                for (int iCol = 0; iCol < tiles[0].length / 2; ++iCol) {
                    int terrainIndex = CoreMath.rand(0, scene.terrainImages.length - 1);
                    ImageSprite terrainSprite = new ImageSprite(scene.terrainImages[terrainIndex],
                                                                iCol * spriteWidth,
                                                                iRow * spriteHeight);
                    terrainSprite.setAnchor(Sprite.NORTH_WEST);
                    groundLayer.add(terrainSprite);
                }
            }
        }
    }

    private void resetElectrification() {
        for (int iRow = 0; iRow < tiles.length; ++iRow) {
            for (int iCol = 0; iCol < tiles[0].length; ++iCol) {
                if (tiles[iRow][iCol] != null) {
                    Sprite tileSprite = tiles[iRow][iCol].get(0);

                    if (tileSprite != null) {
                        boolean bIsElectricSprite = (tileSprite.getClass() == ESprite.class);
                        boolean bIsCritterSprite  = (isCritter(tileSprite));

                        if (bIsElectricSprite || bIsCritterSprite) {
                            ESprite eSprite = (ESprite)tileSprite;
                            eSprite.resetElectrified();
                        }
                    }
                }
            }
        }
    }

    private void resetLevel() {
        retryBonus = Math.max(retryBonus - 100, 0);
        
        if (flock != null) {
            flock.clear();
        }

        if (currentGeometry != null) {
            setFenceResources(currentGeometry.getFenceResources());
            setEFenceResources(currentGeometry.getEFenceResources());
        }

        // Show the level's start message, if any.
        String startMessage = currentGeometry.getMessage(true);
        if (scene != null && startMessage != null) {
            scene.showMessage(startMessage,
                              currentGeometry.getMessageColor(true),
                              currentGeometry.getMessageDuration(true));
        }
    }

    private void createPastureTiles() {

        // Create layers to hold sections of fence.
        if (scene != null) {
            for (int iRow = 0; iRow < tiles.length; ++iRow) {
                for (int iCol = 0; iCol < tiles[0].length; ++iCol) {
                    // Create a series of layers to manage terrain draw order.
                    tiles[iRow][iCol] = new Group();
                    tiles[iRow][iCol].setLocation(PlayingScene.GROUND_OFFSET_X, PlayingScene.GROUND_OFFSET_Y);
                    scene.add(tiles[iRow][iCol]);
                }
            }
        }
    }

    private void clearLocks() {
        if (locks != null) {
            for (int iRow = 0; iRow < locks.length; ++iRow) {
                if (locks[iRow] != null) {
                    for (int iCol = 0; iCol < locks[iRow].length; ++iCol) {
                        locks[iRow][iCol] = false;
                    }
                }
            }
        }
    }

    private void createLocks() {
        locks = new boolean[tiles.length][];

        for (int iRow = 0; iRow < locks.length; ++iRow) {
            locks[iRow] = new boolean[tiles[0].length];

            for (int iCol = 0; iCol < locks[iRow].length; ++iCol) {
                locks[iRow][iCol] = false;
            }
        }
    }

    private void createPlots() {
        // Reserve space for a new row of plots.
        plots = new Plot[tiles.length / 2][];

        for (int iRow = 0; iRow < plots.length; ++iRow) {
            // Reserve space for all the columns in this row.
            plots[iRow] = new Plot[tiles[0].length / 2];

            for (int iCol = 0; iCol < tiles[0].length / 2; ++iCol) {
                // Create the new row of plots.
                Group plotGroup = tiles[2 * iRow + 1][2 * iCol + 1];
                plotGroup.setLocation(PlayingScene.GROUND_OFFSET_X, PlayingScene.GROUND_OFFSET_Y);
                plots[iRow][iCol] = new Plot(iRow, iCol, plotGroup);
            }
        }

        // Assign neighbors.
        for (int iRow = 0; iRow < plots.length; ++iRow) {
            int indexUp     = iRow - 1;
            int indexDown   = iRow + 1;

            for (int iCol = 0; iCol < plots[0].length; ++iCol) {
                int indexRight = iCol + 1;
                int indexLeft  = iCol - 1;

                if (plots[iRow][iCol] != null) {
                    Plot plotRight = null;
                    Plot plotDown  = null;
                    Plot plotLeft  = null;
                    Plot plotUp    = null;

                    if (indexRight < plots[0].length) {
                        plotRight = plots[iRow][indexRight];
                    }

                    if (indexDown < plots.length) {
                        plotDown = plots[indexDown][iCol];
                    }

                    if (indexLeft >= 0) {
                        plotLeft = plots[iRow][indexLeft];
                    }

                    if (indexUp >= 0) {
                        plotUp = plots[indexUp][iCol];
                    }

                    plots[iRow][iCol].setNeighbors(plotRight, plotDown, plotLeft, plotUp);
                }
            }
        }
    }
}
