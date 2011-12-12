package maingame;

//import pulpcore.CoreSystem;
//import static pulpcore.image.Colors.*;
//import pulpcore.image.CoreFont;
//import pulpcore.Input;
//import pulpcore.image.BlendMode;

//import pulpcore.math.CoreMath;
//import pulpcore.sprite.ImageSprite;
//import pulpcore.sprite.Sprite;
//import pulpcore.sound.Sound;
//import pulpcore.sprite.Label;

import levels.*;

import pulpcore.Stage;
import static pulpcore.image.Colors.*;
import pulpcore.image.CoreImage;
import pulpcore.scene.Scene2D;
import pulpcore.sprite.FilledSprite;
import pulpcore.sprite.Group;
import pulpcore.animation.Timeline;
import pulpcore.image.CoreFont;
import pulpcore.sprite.Sprite;
import pulpcore.animation.Easing;
import pulpcore.animation.event.RemoveSpriteEvent;
import pulpcore.sprite.ImageSprite;
import pulpcore.sprite.Label;
import pulpcore.sound.Sound;
import pulpcore.animation.event.SceneChangeEvent;

import java.util.ArrayList;

public class PlayingScene extends Scene2D implements HotSpriteListener {

    public static final int         MESSAGE_TIME_SHORT  = 2500;
    public static final int         MESSAGE_TIME_NORMAL = 5000;
    public static final int         MESSAGE_TIME_LONG   = 10000;
    public static final int         MESSAGE_TIME_FOREVER= -1;
    public static final int         MESSAGE_NORMAL      = 0;
    public static final int         MESSAGE_ATTENTION   = 1;
    public static final int         MESSAGE_ALERT       = 2;

    public static final int         MODE_SPRING         = 0;
    public static final int         MODE_SUMMER         = 1;
    public static final int         MODE_FALL           = 2;
    public static final int         MODE_TUTORIAL       = 3;

    protected static final int      GROUND_OFFSET_X     = 24;
    protected static final int      GROUND_OFFSET_Y     = 24;

    protected static final int      DEFAULT_PLOT_ROWS   = 8;
    protected static final int      DEFAULT_PLOT_COLS   = 8;

    protected static final int      FENCE_VERTICAL      = 0;
    protected static final int      FENCE_HORIZONTAL    = 1;

    protected static final int      FENCED_RIGHT        = 0x01;
    protected static final int      FENCED_DOWN         = 0x02;
    protected static final int      FENCED_LEFT         = 0x04;
    protected static final int      FENCED_UP           = 0x08;

    protected static final int      DIRECTION_RIGHT     = 0x01;
    protected static final int      DIRECTION_DOWN      = 0x02;
    protected static final int      DIRECTION_LEFT      = 0x04;
    protected static final int      DIRECTION_UP        = 0x08;

    protected static final int      RIGHT               = 0;
    protected static final int      DOWN                = 1;
    protected static final int      LEFT                = 2;
    protected static final int      UP                  = 3;

    private static final int        UI_LABEL_HEIGHT     = 30;
    private static final int        UI_BORDER_SIZE      = 2;
    private static final int        UI_SCORE_LABEL_DY   = 32;
    private static final int        UI_SCORES_START     = 80;
    private static final int        UI_SCORE_LABEL_X    = 155;

    private static int              sSeasonMode         = MODE_SPRING;

    public static void SET_SEASON(int season) { sSeasonMode = Math.min(Math.max(season, MODE_SPRING), MODE_TUTORIAL); }

    protected CoreImage[]       terrainImages;
    protected CoreImage[]       eFence                  = {null, null};
    protected CoreImage[]       fence                   = {null, null};
    protected Group             HUDlower                = null;
    protected Group             HUDright                = null;
    protected Group             highGroup               = null;
    protected Group             HUDscore                = null;

    // Game states.
    protected GameStateSetup        stateSetup          = null;
    protected GameStateExecute      stateExecute        = null;
    protected GameStateEndRound     stateEndRound       = null;
    protected GameStateRoundFailed  stateRoundFailed    = null;
    protected GameStateDeadlocked   stateDeadlocked     = null;

    private GameState               currentState        = null;
    private ArrayList[]             levelList           = {null, null, null, null};
    private int                     currentLevelIndex   = -1;
    private Label                   lastLabel           = null;

    // The LevelLayout.
    private LevelLayout             currentLevel        = null;

    // UI elements
    private ImageSprite             UIfences            = null;
    private ImageSprite             UIeFences           = null;
    private Label                   UIlabelFences       = null;
    private Label                   UIlabelEfences      = null;
    private Label                   UIlabelLevelScoreVal= null;
    private Label                   UIlabelFenceBonusVal= null;
    private Label                   UIlabelLambBonusVal = null;
    private Label                   UIlabelRetryBonusVal= null;
    private Label                   UIlabelScore        = null;
    private Label                   UIlabelScoreVal     = null;
    private CoreFont                fontNormal          = null;
    private CoreFont                fontAttention       = null;
    private CoreFont                fontAlert           = null;

    private UIWiggler               buttonReset         = null;
    private UIWiggler               buttonRetry         = null;
    private UIWiggler               buttonSkip          = null;
    private UIWiggler               buttonQuit          = null;

    // UI sounds
    private Sound                   mouseOver           = null;
    private Sound                   buttonPress         = null;

    // UI colors
    private final int HUDred  = 0xc8;
    private final int HUDgrn  = 0xab;
    private final int HUDblu  = 0x37;

    private final int backRed   = 5 * 0x44 / 6;
    private final int backGrn   = 5 * 0xaa / 6;
    private final int backBlu   = 5 * 0x00 / 6;

    private Scene2D                 titleScene          = null;
    private int                     score               = 0;

    // Critters.
    // Max
    // Daisy
    // The Sheep

    // HotSpriteListener Interface /////////////////////////////////////////////
    public void becameHot(HotSprite hotSprite) {
        if (currentLevel != null) {
            if (mouseOver != null) {
                mouseOver.play();
            }
        }
    }

    public void becameCool(HotSprite hotSprite) {
    }

    public void pressed(HotSprite hotSprite) {
        if (currentLevel != null) {
            if (buttonPress != null) {
                buttonPress.play();
            }

            if (hotSprite == buttonReset) {
                currentLevel.setSkipped();
                setState(stateSetup);
            }
            else if (hotSprite == buttonRetry) {
                currentLevel.setSkipped();
                stateSetup.setRetry();
                setState(stateSetup);
            }
            else if (hotSprite == buttonSkip) {
                currentLevel.setSkipped();
                currentLevel.resetRetryBonus();
                chooseNextLevel();
                setState(stateSetup);
            }
            else if (hotSprite == buttonQuit) {
                quit();
            }
        }
    }

    // Public Methods //////////////////////////////////////////////////////////
    public void quit() {
        this.setState(null);
        addEvent(new SceneChangeEvent(titleScene, 300));
    }

    public void resetScore() { score = 0; }
    public void addToScore(int moreScore) {
        score += moreScore;
        
        if (UIlabelScoreVal != null) {
            UIlabelScoreVal.setText("" + score);
        }
    }
    
    public void showResults(int levelScore, int retryBonusIn, int fenceBonusIn, int lambBonusIn) {
        // Fill out labels.
        if (UIlabelLevelScoreVal != null) {
            UIlabelLevelScoreVal.setText("" + levelScore);
        }

        if (UIlabelFenceBonusVal != null) {
            UIlabelFenceBonusVal.setText("" + fenceBonusIn);
        }

        if (UIlabelLambBonusVal != null) {
            UIlabelLambBonusVal.setText("" + lambBonusIn);
        }

        if (UIlabelRetryBonusVal != null) {
            UIlabelRetryBonusVal.setText("" + retryBonusIn);
        }

        if (HUDscore != null) {
            add(HUDscore);
        }
    }

    public void hideResults() {
        if (HUDscore != null) {
            remove(HUDscore);
        }
    }

    public void setTitleScene(Scene2D titleSceneIn) {
        titleScene = titleSceneIn;
    }

    public void setState(GameState newState) {
        // Clear any message associated with the previous state.
        clearLastMessage();

        if (currentState != null) {
            currentState.exit();
        }

        if (newState != null) {
            newState.enter();
        }

        currentState = newState;
    }

    @Override
    public void load() {
        // Create background.
        add(new FilledSprite(pulpcore.image.Colors.rgb(backRed, backGrn, backBlu)));

        // Regular fence.
        fence[FENCE_VERTICAL]   = CoreImage.load("fence_v.png");
        fence[FENCE_HORIZONTAL] = CoreImage.load("fence_h.png");

        // Electric fence.
        eFence[FENCE_VERTICAL]   = CoreImage.load("efence_v.png");
        eFence[FENCE_HORIZONTAL] = CoreImage.load("efence_h.png");

        // Pasture.
        if (sSeasonMode == PlayingScene.MODE_SPRING || sSeasonMode == PlayingScene.MODE_TUTORIAL) {
            terrainImages = CoreImage.load("pasture_spring.png").split(2, 3);
        }
        else if (sSeasonMode == PlayingScene.MODE_SUMMER) {
            terrainImages = CoreImage.load("pasture_summer.png").split(2, 3);
        }
        else if (sSeasonMode == PlayingScene.MODE_FALL) {
            terrainImages = CoreImage.load("pasture_fall.png").split(2, 3);
        }

        // Create a level layout manager.
        createLevelLayout();

        highGroup = new Group();
        add(highGroup);

        // Init UI.
        initUI();

        // Must create states AFTER creating the level.
        createStates();

        // Lay out the level elements.
        createLevelList();

        // Init scoring.
        resetScore();

        // Start playing.
        setState(stateSetup);
    }

    public LevelSetup getNextLevelSetup() {
        LevelSetup nextSetup = null;

        if (levelList != null &&
            sSeasonMode >= 0 &&
            sSeasonMode < levelList.length &&
            currentLevelIndex >= 0 &&
            currentLevelIndex < levelList[sSeasonMode].size()) {

            nextSetup = (LevelSetup)levelList[sSeasonMode].get(currentLevelIndex);
        }

        return nextSetup;
    }

    public boolean chooseNextLevel() {
        boolean bNextLevelValid = false;

        if (levelList != null) {
            int nextLevelIndex = currentLevelIndex + 1;
            if (nextLevelIndex == Math.min(nextLevelIndex, levelList[sSeasonMode].size() - 1)) {
                bNextLevelValid = true;
                currentLevelIndex = nextLevelIndex;
            }
        }

        return bNextLevelValid;
    }

    public void choosePreviousLevel() {
        if (levelList != null) {
            --currentLevelIndex;
            currentLevelIndex = Math.max(currentLevelIndex, 0);
        }
    }

    public void showMessage(String message, int messageColor) {
        showMessage(message, messageColor, MESSAGE_TIME_NORMAL);
    }

    public void showMessage(String message, int messageType, int messageTime) {
        if (HUDlower != null) {
            CoreFont messageFont;

            switch(messageType) {
                case PlayingScene.MESSAGE_ATTENTION:
                {
                    messageFont = fontAttention;
                }
                break;

                case PlayingScene.MESSAGE_ALERT:
                {
                    messageFont = fontAlert;
                }
                break;

                default:
                 // Includes case MESSAGE_NORMAL:
                {
                    messageFont = fontNormal;
                }
                break;
            }

            // Clear previous message, if any.
            clearLastMessage();

            // Add messages (play in a loop)
            Timeline timeline = new Timeline();
            int x = Stage.getWidth() / 2;
            int y = HUDlower.height.getAsInt() / 2;
            int startTime = 0;

            // Add the "border".
            int labelWidth = -1; // auto
            int labelHeight = 10;
            Label label = new Label(messageFont, message, x, y, labelWidth, labelHeight);
            label.setAnchor(Sprite.CENTER);
            label.alpha.set(0);

            // Animate (zoom).
            timeline.at(startTime).animate(label.alpha, 0, 255, 100);
            timeline.at(startTime).animate(label.height, 200, 50, 500, Easing.STRONG_OUT);

            if (messageTime > 0) {
                timeline.add(new RemoveSpriteEvent(HUDlower, label, messageTime));
            }

            lastLabel = label;

            HUDlower.add(label);

            addTimeline(timeline);
        }
    }

    public Group getParticleLayer() { return this.highGroup; }
    
    @Override
    public void update(int elapsedTime) {
        if (currentState != null) {
            currentState.update(elapsedTime);
        }

        checkHeartSpawn();

        if (currentLevel != null) {
            currentLevel.updateSounds(elapsedTime);
        }
    }

    protected void updateResources(int fences, int eFences) {
        if (UIlabelFences != null) {
            UIlabelFences.setText("" + fences);
        }

        if (UIlabelEfences != null) {
            UIlabelEfences.setText("" + eFences);
        }
    }

    protected GameState getCurrentState() { return currentState; }

    // Private Methods /////////////////////////////////////////////////////////
    private void checkHeartSpawn() {
        if (currentLevel != null &&
            currentLevel.scene != null &&
            currentLevel.critterDaisy != null &&
            currentLevel.critterMax != null &&
            (currentState == stateExecute || currentState == stateEndRound)) {

            int daisyRow = currentLevel.critterDaisy.getRow();
            int daisyCol = currentLevel.critterDaisy.getCol();

            int maxRow   = currentLevel.critterMax.getRow();
            int maxCol   = currentLevel.critterMax.getCol();

            if (maxRow == daisyRow || maxCol == daisyCol) {
                currentLevel.critterMax.spawnHeart(currentLevel.scene,
                                                   currentLevel.scene.getParticleLayer());
            }

            currentLevel.updateLambs();
        }
    }

    private void createLevelList() {
        ArrayList<LevelSetup> tutorialLevels = new ArrayList<LevelSetup>();
        ArrayList<LevelSetup> springLevels = new ArrayList<LevelSetup>();
        ArrayList<LevelSetup> summerLevels = new ArrayList<LevelSetup>();
        ArrayList<LevelSetup> fallLevels = new ArrayList<LevelSetup>();

        levelList[0] = springLevels;
        levelList[1] = summerLevels;
        levelList[2] = fallLevels;
        levelList[3] = tutorialLevels;

        for (int i=0; i<levelList.length; ++i) {
            ArrayList<LevelSetup> tempList = null;
            if (i == 0) tempList = springLevels;
            if (i == 1) tempList = summerLevels;
            if (i == 2) tempList = fallLevels;
            if (i == 3) tempList = tutorialLevels;

            if (tempList != null) {
                switch (i) {
                    case 0: // Spring levels
                    {
                        tempList.add(new LvlGoGetEm());
                        tempList.add(new LvlChutesAndLadders());
                        tempList.add(new LvlNoRightOfWay());
                        tempList.add(new LvlBigField());
                        tempList.add(new LvlConductYourself());
                        tempList.add(new LvlPenaltyBox());
                        tempList.add(new LvlCongaLine());
                        tempList.add(new LvlRicochet());
                        tempList.add(new LvlCarChase());
                        tempList.add(new LvlHardLine1());

                        tempList.add(new LvlYouWin());
                    }
                    break;

                    case 1: // Summer levels
                    {
                        tempList.add(new LvlDoublePlay());
                        tempList.add(new LvlFourSquare());
                        tempList.add(new LvlHardLine2());
                        tempList.add(new LvlStairwayToHeaven());
                        tempList.add(new LvlTriplePlay());
                        tempList.add(new LvlGoGetEm2());
                        tempList.add(new LvlNoFlock());
                        tempList.add(new LvlGrandSlam());
                        tempList.add(new LvlJailBreak());
                        tempList.add(new LvlRoundAndRound());

                        tempList.add(new LvlYouWin());
                    }
                    break;

                    case 2: // Fall levels
                    {
                        tempList.add(new LvlHeadOnCollision());
                        tempList.add(new LvlChainReaction());
                        tempList.add(new LvlThreeByThree());
                        tempList.add(new LvlDoubleCross());
                        tempList.add(new LvlChatanoogaChooChoo());
                        tempList.add(new LvlMazeGames());
                        tempList.add(new LvlMarkOfZorro());
                        tempList.add(new LvlEweTurn());
                        tempList.add(new LvlPinball());
                        tempList.add(new LvlNineBall());
                        tempList.add(new LvlPinball2());
                        tempList.add(new LvlEightBall());
                        tempList.add(new LvlCrissCross());
                        tempList.add(new LvlGridiron());
                        tempList.add(new LvlFourCorners());

                        tempList.add(new LvlYouWin());
                    }
                    break;

                    default: // Tutorial levels
                    {
                        tempList.add(new LvlTutorial01());
                        tempList.add(new LvlTutorial02());
                        tempList.add(new LvlTutorial03());
                        tempList.add(new LvlTutorial04());
                        tempList.add(new LvlTutorial05());
                        tempList.add(new LvlTutorial06());
                        tempList.add(new LvlTutorial07());
                        tempList.add(new LvlTutorial08());
                        tempList.add(new LvlTutorial09());
                        tempList.add(new LvlTutorial10());
                        tempList.add(new LvlTutorial11());
                    }
                    break;
                }
            }
        }

        currentLevelIndex = 0;
    }

    private void createLevelLayout() {
        currentLevel = new LevelLayout(this);
    }

    private void createStates() {
        stateSetup          = new GameStateSetup(this, currentLevel);
        stateExecute        = new GameStateExecute(this, currentLevel);
        stateEndRound       = new GameStateEndRound(this, currentLevel);
        stateRoundFailed    = new GameStateRoundFailed(this, currentLevel);
        stateDeadlocked     = new GameStateDeadlocked(this, currentLevel);
    }

    private void initUI() {
        fontNormal      = CoreFont.load("hello.font.png").tint(WHITE);
        fontAttention   = CoreFont.load("hello.font.png").tint(YELLOW);
        fontAlert       = CoreFont.load("hello.font.png").tint(RED);

        HUDlower = new Group(0,
                             Stage.getHeight() - terrainImages[0].getHeight(),
                             Stage.getWidth(),
                             terrainImages[0].getHeight());

        HUDlower.add(new FilledSprite(BLACK));

        HUDright = new Group(Stage.getWidth() - terrainImages[0].getWidth(),
                             0,
                             terrainImages[0].getWidth(),
                             Stage.getHeight() - terrainImages[0].getHeight());

        HUDright.add(new FilledSprite(pulpcore.image.Colors.rgb(HUDred, HUDgrn, HUDblu)));

        initScoreCardHUD(terrainImages[0].getWidth(), terrainImages[0].getHeight());

        add(HUDright);
        add(HUDlower);

        CoreImage fenceResource = CoreImage.load("resource_fence.png");
        CoreImage eFenceResource = CoreImage.load("resource_efence.png");

        // Values to help position widgets.
        int widgetWidth  = fenceResource.getWidth();
        int widgetHeight = fenceResource.getHeight();

        // Values to help with labels.
        CoreFont messageFont = CoreFont.load("hello.font.png").tint(WHITE);
        int labelWidth = -1; // auto
        int labelHeight = UI_LABEL_HEIGHT;

        int curHeight = labelHeight / 2;

        // Add widgets.
        // Score lables.
        UIlabelScore = new Label(messageFont,
                                 "Score",
                                 widgetWidth / 2,
                                 curHeight,
                                 labelWidth,
                                 labelHeight);
        UIlabelScore.setAnchor(Sprite.CENTER);
        HUDright.add(UIlabelScore);

        curHeight += labelHeight;

        UIlabelScoreVal = new Label(messageFont,
                                    "0",
                                    widgetWidth / 2,
                                    curHeight,
                                    labelWidth,
                                    labelHeight);
        UIlabelScoreVal.setAnchor(Sprite.CENTER);
        HUDright.add(UIlabelScoreVal);

        curHeight += widgetHeight;

        UIfences = new ImageSprite(fenceResource, widgetWidth / 2, curHeight);
        UIfences.setAnchor(Sprite.CENTER);
        HUDright.add(UIfences);

        curHeight += widgetHeight / 2 + labelHeight / 2;

        // Fence resource label.
        UIlabelFences = new Label(messageFont,
                                  "0",
                                  widgetWidth / 2,
                                  curHeight,
                                  labelWidth,
                                  labelHeight);
        UIlabelFences.setAnchor(Sprite.CENTER);
        HUDright.add(UIlabelFences);

        curHeight += labelHeight / 2 + widgetHeight / 2;
        UIeFences = new ImageSprite(eFenceResource, widgetWidth / 2, curHeight);
        UIeFences.setAnchor(Sprite.CENTER);
        HUDright.add(UIeFences);

        curHeight += widgetHeight / 2 + labelHeight / 2;

        UIlabelEfences = new Label(messageFont,
                                  "0",
                                  widgetWidth / 2,
                                  curHeight,
                                  labelWidth,
                                  labelHeight);
        UIlabelEfences.setAnchor(Sprite.CENTER);
        HUDright.add(UIlabelEfences);

        curHeight += labelHeight / 2 + widgetHeight;

        // Add navigation buttons.
        buttonReset = new UIWiggler("ui_reset.png", widgetWidth / 2, curHeight);
        buttonReset.setAnchor(Sprite.CENTER);
        buttonReset.setListener(this);
        HUDright.add(buttonReset);

        curHeight += widgetHeight;

        buttonRetry = new UIWiggler("ui_retry.png", widgetWidth / 2, curHeight);
        buttonRetry.setAnchor(Sprite.CENTER);
        buttonRetry.setListener(this);
        HUDright.add(buttonRetry);

        curHeight += widgetHeight;

        buttonSkip = new UIWiggler("ui_skip.png", widgetWidth / 2, curHeight);
        buttonSkip.setAnchor(Sprite.CENTER);
        buttonSkip.setListener(this);
        HUDright.add(buttonSkip);

        curHeight += widgetHeight;

        buttonQuit = new UIWiggler("ui_quit.png", widgetWidth / 2, curHeight);
        buttonQuit.setAnchor(Sprite.CENTER);
        buttonQuit.setListener(this);
        HUDright.add(buttonQuit);

        // Create UI sounds.
        mouseOver   = Sound.load("sound_mouseover.wav");
        buttonPress = Sound.load("sound_press.wav");
    }

    private void initScoreCardHUD(int tileWidth, int tileHeight) {
        int plotRows    = DEFAULT_PLOT_ROWS;
        int plotCols    = DEFAULT_PLOT_COLS;

        if (currentLevel != null &&
            currentLevel.plots != null &&
            currentLevel.plots[0] != null) {
            plotRows = currentLevel.plots.length;
            plotCols = currentLevel.plots[0].length;
        }

        int playRegionDX= 2 * GROUND_OFFSET_X + tileWidth * plotCols;
        int playRegionDY= 2 * GROUND_OFFSET_Y + tileHeight * plotRows;

        CoreImage scorePlacardImage = CoreImage.load("score_placard.png");

        int scoreWidth  = scorePlacardImage.getWidth();
        int scoreHeight = scorePlacardImage.getHeight();

        HUDscore = new Group(playRegionDX / 2,
                             playRegionDY / 2,
                             scoreWidth,
                             scoreHeight);

        HUDscore.setAnchor(Sprite.CENTER);
        HUDscore.add(new ImageSprite(scorePlacardImage, 0, 0));

        // Labels
        // Values to help position widgets.
        int curHeight = UI_SCORES_START;

        // Values to help with labels.
        CoreFont messageFont = CoreFont.load("hello.font.png").tint(WHITE);
        int labelWidth = -1; // auto
        int labelHeight = UI_SCORE_LABEL_DY;

        // Add widgets.

        // Level score
        UIlabelLevelScoreVal = new Label(messageFont,
                                         "0",
                                         UI_SCORE_LABEL_X,
                                         curHeight,
                                         labelWidth,
                                         labelHeight);
        UIlabelLevelScoreVal.setAnchor(Sprite.CENTER);
        HUDscore.add(UIlabelLevelScoreVal);

        curHeight += labelHeight;

        // Retry bonus
        UIlabelRetryBonusVal = new Label(messageFont,
                                         "0",
                                         UI_SCORE_LABEL_X,
                                         curHeight,
                                         labelWidth,
                                         labelHeight);
        UIlabelRetryBonusVal.setAnchor(Sprite.CENTER);
        HUDscore.add(UIlabelRetryBonusVal);

        curHeight += labelHeight;

        // Fence bonus
        UIlabelFenceBonusVal = new Label(messageFont,
                                         "0",
                                         UI_SCORE_LABEL_X,
                                         curHeight,
                                         labelWidth,
                                         labelHeight);
        UIlabelFenceBonusVal.setAnchor(Sprite.CENTER);
        HUDscore.add(UIlabelFenceBonusVal);

        curHeight += labelHeight;

        // Lamb bonus
        UIlabelLambBonusVal = new Label(messageFont,
                                        "0",
                                        UI_SCORE_LABEL_X,
                                        curHeight,
                                        labelWidth,
                                        labelHeight);
        UIlabelLambBonusVal.setAnchor(Sprite.CENTER);
        HUDscore.add(UIlabelLambBonusVal);

        curHeight += labelHeight;
    }

    private void setUpLevel(LevelSetup geometry) {
        if (currentLevel != null) {
            currentLevel.setUp(geometry);
        }
    }

    private void clearLastMessage() {
        if (HUDlower != null && lastLabel != null) {
            HUDlower.remove(lastLabel);
            lastLabel = null;
        }
    }
}
