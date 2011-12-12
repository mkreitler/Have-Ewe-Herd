package maingame;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author mkreitler
 */
import pulpcore.sprite.ImageSprite;
import pulpcore.sprite.Group;
import pulpcore.image.BlendMode;
import pulpcore.sprite.Sprite;

public abstract class GameStateFencePlacer extends GameState {
    private static final String MESSAGE_OUT_OF_FENCE    = "No more fence!";

    protected Group             cursorLayer             = null;
    protected LevelLayout       currentLevel            = null;
    protected ImageSprite[]     fenceCursor             = {null, null};

    // Public Methods //////////////////////////////////////////////////////////
    public GameStateFencePlacer(PlayingScene sceneIn, LevelLayout levelIn) {
        super(sceneIn);

        currentLevel = levelIn;
        createFenceCursor();
    }

    @Override
    public abstract void enter();

    @Override
    public void update(int elapsedTime) {
        handleInput();
    }

    public abstract void onClickedMax();

    @Override
    public void exit() {
        removeCursor();
    }

    // Protected Methods ///////////////////////////////////////////////////////
    protected void removeCursor() {
        if (cursorLayer != null) {
            cursorLayer.remove(fenceCursor[PlayingScene.FENCE_VERTICAL]);
            cursorLayer.remove(fenceCursor[PlayingScene.FENCE_HORIZONTAL]);
        }
    }

    protected abstract void onPlacedFence(int groupRow, int groupCol);

    // Prvate Methods //////////////////////////////////////////////////////////
    private void createFenceCursor() {
        // Fence cursor.
        fenceCursor[PlayingScene.FENCE_VERTICAL]   = new ImageSprite("fence_cursor_v.png", 0, 0);
        fenceCursor[PlayingScene.FENCE_HORIZONTAL] = new ImageSprite("fence_cursor_h.png", 0, 0);

        cursorLayer = new Group();
        cursorLayer.setLocation(PlayingScene.GROUND_OFFSET_X, PlayingScene.GROUND_OFFSET_Y);
        scene.add(cursorLayer);

        fenceCursor[0].setBlendMode(BlendMode.Add());
        fenceCursor[1].setBlendMode(BlendMode.Add());
        fenceCursor[0].setAnchor(ImageSprite.CENTER);
        fenceCursor[1].setAnchor(ImageSprite.CENTER);
    }

    private void handleInput() {
        // Did the user click on Max?
        if (currentLevel != null &&
            currentLevel.wantsStart()) {

            onClickedMax();
        }
        else if(scene.terrainImages[0] != null &&
                currentLevel != null &&
                fenceCursor[0] != null &&
                fenceCursor[1] != null) {
            
            int mouseX = pulpcore.Input.getMouseX();
            int mouseY = pulpcore.Input.getMouseY();

            int testMouseX = mouseX - PlayingScene.GROUND_OFFSET_X;
            int testMouseY = mouseY - PlayingScene.GROUND_OFFSET_Y;

            int tileWidth = scene.terrainImages[0].getWidth();
            int tileHeight = scene.terrainImages[0].getHeight();

            int iRow    = testMouseY / tileHeight;
            int iCol    = testMouseX / tileWidth;

            int iLocY   = testMouseY - iRow * tileHeight;
            int iLocX   = testMouseX - iCol * tileWidth;

            int iDistY  = iLocY;
            int iDistX  = iLocX;

            if (iDistY > tileHeight / 2) iDistY = tileHeight - iDistY;
            if (iDistX > tileWidth / 2) iDistX = tileWidth - iDistX;

            boolean bHiLow = false;
            boolean bSide  = false;

            if (iDistY > iDistX) {
                bSide = true;
            }
            else
            {
                bHiLow = true;
            }

            if (iRow < 0 ||
                iCol < 0 ||
                iRow >= currentLevel.getPlotRows() ||
                iCol >= currentLevel.getPlotCols()) {

                // Mouse not on pasture.
                bHiLow = false;
                bSide  = false;
            }
            else if (currentLevel != null && currentLevel.isMouseOverMax()) {
                // Mouse is over Max the bull.
                bHiLow = false;
                bSide  = false;
            }

            int whichCase = 0;
            if (!bHiLow && bSide) {
                // Switch to vertical fence.
                if (!cursorLayer.contains(fenceCursor[PlayingScene.FENCE_VERTICAL])) {
                    cursorLayer.remove(fenceCursor[PlayingScene.FENCE_HORIZONTAL]);
                    cursorLayer.add(fenceCursor[PlayingScene.FENCE_VERTICAL]);
                }

                whichCase = 1;
            }
            else if (bHiLow && !bSide) {
                // Switch to horizontal fence.
                if (!cursorLayer.contains(fenceCursor[PlayingScene.FENCE_HORIZONTAL])) {
                    cursorLayer.remove(fenceCursor[PlayingScene.FENCE_VERTICAL]);
                    cursorLayer.add(fenceCursor[PlayingScene.FENCE_HORIZONTAL]);
                }

                whichCase = -1;
            }
            else {
                removeCursor();
            }

            int cursorX = mouseX;
            int cursorY = mouseY;

            switch (whichCase) {
                case 1: // Vertical fence.
                {
                    int groupRow = 2 * iRow + 1;
                    int groupCol = 2 * iCol;
                    if (iLocX > tileWidth / 2) {
                        groupCol += 2;
                        ++iCol;
                    }

                    cursorX = currentLevel.getFencePosX(iCol, true);
                    cursorY = currentLevel.getFencePosY(iRow, true);

                    if (!currentLevel.isLocked(groupRow, groupCol)) {
                        if (pulpcore.Input.isMousePressed()) {
                            if (currentLevel.isOccupied(groupRow, groupCol)) {
                                // Is this an electric fence?
                                Sprite sprite = currentLevel.getSpriteFromTile(groupRow, groupCol);
                                if (sprite.getClass() == ESprite.class) {
                                    currentLevel.freeEFenceResource();
                                }
                                else {
                                    currentLevel.freeFenceResource();
                                }

                                currentLevel.clearTile(groupRow, groupCol);
                            }
                            else if (pulpcore.Input.isShiftDown() || pulpcore.Input.isPressed(pulpcore.Input.KEY_MOUSE_BUTTON_3)) {
                                if (currentLevel.getEFenceResources() > 0) {
                                    currentLevel.addFence(cursorX,
                                                          cursorY,
                                                          groupRow,
                                                          groupCol,
                                                          true,
                                                          false,
                                                          false);

                                    currentLevel.useEFenceResource();
                                    onPlacedFence(groupRow, groupCol);
                                }
                                else
                                {
                                    scene.showMessage(MESSAGE_OUT_OF_FENCE, PlayingScene.MESSAGE_ALERT, PlayingScene.MESSAGE_TIME_NORMAL);
                                }
                            }
                            else {
                                if (currentLevel.getFenceResources() > 0) {
                                    currentLevel.addFence(cursorX,
                                                          cursorY,
                                                          groupRow,
                                                          groupCol,
                                                          true,
                                                          true,
                                                          false);

                                    currentLevel.useFenceResource();
                                    onPlacedFence(groupRow, groupCol);
                                }
                                else
                                {
                                    scene.showMessage(MESSAGE_OUT_OF_FENCE, PlayingScene.MESSAGE_ALERT, PlayingScene.MESSAGE_TIME_NORMAL);
                                }
                            }
                        }
                    }
                }
                break;

                case -1: // Horizontal fence.
                {
                    int groupRow = 2 * iRow;
                    int groupCol = 2 * iCol + 1;
                    if (iLocY > tileHeight / 2) {
                        groupRow += 2;
                        ++iRow;
                    }

                    cursorX = currentLevel.getFencePosX(iCol, false);
                    cursorY = currentLevel.getFencePosY(iRow, false);

                    if (!currentLevel.isLocked(groupRow, groupCol)) {
                        if (pulpcore.Input.isMousePressed()) {
                            if (currentLevel.isOccupied(groupRow, groupCol)) {
                                // Is this an electric fence?
                                Sprite sprite = currentLevel.getSpriteFromTile(groupRow, groupCol);
                                if (sprite.getClass() == ESprite.class) {
                                    currentLevel.freeEFenceResource();
                                }
                                else {
                                    currentLevel.freeFenceResource();
                                }

                                currentLevel.clearTile(groupRow, groupCol);
                            }
                            else if (pulpcore.Input.isShiftDown() || pulpcore.Input.isPressed(pulpcore.Input.KEY_MOUSE_BUTTON_3)) {
                                if (currentLevel.getEFenceResources() > 0) {
                                    currentLevel.addFence(cursorX,
                                                          cursorY,
                                                          groupRow,
                                                          groupCol,
                                                          false,
                                                          false,
                                                          false);

                                    currentLevel.useEFenceResource();
                                    onPlacedFence(groupRow, groupCol);
                                }
                                else
                                {
                                    scene.showMessage(MESSAGE_OUT_OF_FENCE, PlayingScene.MESSAGE_ALERT, PlayingScene.MESSAGE_TIME_NORMAL);
                                }
                            }
                            else {
                                if (currentLevel.getFenceResources() > 0) {
                                    currentLevel.addFence(cursorX,
                                                          cursorY,
                                                          groupRow,
                                                          groupCol,
                                                          false,
                                                          true,
                                                          false);

                                    currentLevel.useFenceResource();
                                    onPlacedFence(groupRow, groupCol);
                                }
                                else
                                {
                                    scene.showMessage(MESSAGE_OUT_OF_FENCE, PlayingScene.MESSAGE_ALERT, PlayingScene.MESSAGE_TIME_NORMAL);
                                }
                            }
                        }
                    }
                }
                break;

                default:
                    // Do nothing.
                break;
            }

            fenceCursor[0].setLocation(cursorX, cursorY);
            fenceCursor[1].setLocation(cursorX, cursorY);
        }
    }
}
