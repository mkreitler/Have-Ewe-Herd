import maingame.PlayingScene;

import static pulpcore.image.Colors.*;
import pulpcore.animation.event.SceneChangeEvent;
import pulpcore.scene.Scene2D;
import pulpcore.sprite.FilledSprite;
import pulpcore.sprite.Button;
import pulpcore.sprite.Group;
import pulpcore.sprite.ImageSprite;
import pulpcore.sprite.Sprite;
import pulpcore.Stage;
import pulpcore.sound.Sound;

import maingame.UIWiggler;
import maingame.HotSpriteListener;
import maingame.HotSprite;

public class TitleScene extends Scene2D implements HotSpriteListener {
    private static final int WIGGLER_OFFSTAGE_DX    = 600;
    private static final int EXIT_STAGE_TIME_MS     = 200;
    
    Button playButton;
    Button optionsButton;
    Group componentLayer;

    UIWiggler wigglePlay;
    UIWiggler wiggleLearn;

    UIWiggler wiggleSpring;
    UIWiggler wiggleSummer;
    UIWiggler wiggleFall;
    UIWiggler wiggleBack;

    boolean bSeasonsShiftedOut;

    Sound mouseOver;
    Sound buttonPress;

    public void becameHot(HotSprite hotSprite) {
        mouseOver.play();
    }

    public void becameCool(HotSprite hotSprite) {
    }

    public void pressed(HotSprite hotSprite) {
        buttonPress.play();
        
        if (hotSprite == wigglePlay) {
            shiftInSeasonButtons();
        }
        else if(hotSprite == wiggleBack) {
            shiftOutSeasonButtons();
        }
        else if (hotSprite == wiggleSpring) {
            // TODO: set 'spring' levels.
            startGame(PlayingScene.MODE_SPRING);
        }
        else if (hotSprite == wiggleSummer) {
            // TODO: set 'summer' levels.
            startGame(PlayingScene.MODE_SUMMER);
        }
        else if (hotSprite == wiggleFall) {
            // TODO: set 'fall' levels.
            startGame(PlayingScene.MODE_FALL);
        }
        else if (hotSprite == wiggleLearn) {
            // Pushes the scene onto the stack (doesn't unload this Scene)
            OptionScene options = new OptionScene();
            options.setTitleScene(this);
            Stage.setScene(options);
        }
    }

    @Override
    public void load() {
        
//      Label title = new Label(CoreFont.load("hello.font.png"), "Hello World", 320, 240);
//      title.setAnchor(Sprite.CENTER);
//        playButton = Button.createLabeledButton("Play", 500, 380);
//        playButton.setAnchor(Sprite.CENTER);
//        optionsButton = Button.createLabeledButton("Options", 500, 430);
//        optionsButton.setAnchor(Sprite.CENTER);

        wigglePlay = new UIWiggler("ui_play.png", 375, 515);
        wigglePlay.setListener(this);
        wigglePlay.setAnchor(Sprite.CENTER);

        wiggleLearn = new UIWiggler("ui_learn.png", 265, 515);
        wiggleLearn.setListener(this);
        wiggleLearn.setAnchor(Sprite.CENTER);

        wiggleSpring = new UIWiggler("ui_spring.png", 155 + WIGGLER_OFFSTAGE_DX, 515);
        wiggleSpring.setListener(this);
        wiggleSpring.setAnchor(Sprite.CENTER);

        wiggleSummer = new UIWiggler("ui_summer.png", 265 + WIGGLER_OFFSTAGE_DX, 515);
        wiggleSummer.setListener(this);
        wiggleSummer.setAnchor(Sprite.CENTER);

        wiggleFall = new UIWiggler("ui_fall.png", 375 + WIGGLER_OFFSTAGE_DX, 515);
        wiggleFall.setListener(this);
        wiggleFall.setAnchor(Sprite.CENTER);

        wiggleBack = new UIWiggler("ui_back.png", 485 + WIGGLER_OFFSTAGE_DX, 515);
        wiggleBack.setListener(this);
        wiggleBack.setAnchor(Sprite.CENTER);

        bSeasonsShiftedOut = true;

        mouseOver   = Sound.load("sound_mouseover.wav");
        buttonPress = Sound.load("sound_press.wav");

        Sound introMoo = Sound.load("sound_bull.wav");
        introMoo.play();
        
        componentLayer = new Group();
        componentLayer.add(wigglePlay);
        componentLayer.add(wiggleLearn);

        componentLayer.add(wiggleSpring);
        componentLayer.add(wiggleSummer);
        componentLayer.add(wiggleFall);
        componentLayer.add(wiggleBack);

        add(new FilledSprite(BLACK));
        add(new ImageSprite("background.png", 0, 0));
//      add(title);
        addLayer(componentLayer);
    }

    // Private Methods /////////////////////////////////////////////////////////
    private void shiftInSeasonButtons() {
        // Animated alternative to Stage.setScene(new PlayingScene());
        if (wigglePlay != null &&
            wiggleLearn != null &&
            wiggleSpring != null &&
            wiggleSummer != null &&
            wiggleFall != null &&
            wiggleBack != null &&
            bSeasonsShiftedOut) {

            bSeasonsShiftedOut = false;

            wigglePlay.x.animateTo(wigglePlay.x.getAsInt() - WIGGLER_OFFSTAGE_DX, EXIT_STAGE_TIME_MS);
            wiggleLearn.x.animateTo(wiggleLearn.x.getAsInt() - WIGGLER_OFFSTAGE_DX, EXIT_STAGE_TIME_MS);
            wiggleSpring.x.animateTo(wiggleSpring.x.getAsInt() - WIGGLER_OFFSTAGE_DX, EXIT_STAGE_TIME_MS);
            wiggleSummer.x.animateTo(wiggleSummer.x.getAsInt() - WIGGLER_OFFSTAGE_DX, EXIT_STAGE_TIME_MS);
            wiggleFall.x.animateTo(wiggleFall.x.getAsInt() - WIGGLER_OFFSTAGE_DX, EXIT_STAGE_TIME_MS);
            wiggleBack.x.animateTo(wiggleBack.x.getAsInt() - WIGGLER_OFFSTAGE_DX, EXIT_STAGE_TIME_MS);
        }
    }

    private void shiftOutSeasonButtons() {
        // Animated alternative to Stage.setScene(new PlayingScene());
        if (wigglePlay != null &&
            wiggleLearn != null &&
            wiggleSpring != null &&
            wiggleSummer != null &&
            wiggleFall != null &&
            wiggleBack != null &&
            !bSeasonsShiftedOut) {

            bSeasonsShiftedOut = true;

            wigglePlay.x.animateTo(wigglePlay.x.getAsInt() + WIGGLER_OFFSTAGE_DX, EXIT_STAGE_TIME_MS);
            wiggleLearn.x.animateTo(wiggleLearn.x.getAsInt() + WIGGLER_OFFSTAGE_DX, EXIT_STAGE_TIME_MS);
            wiggleSpring.x.animateTo(wiggleSpring.x.getAsInt() + WIGGLER_OFFSTAGE_DX, EXIT_STAGE_TIME_MS);
            wiggleSummer.x.animateTo(wiggleSummer.x.getAsInt() + WIGGLER_OFFSTAGE_DX, EXIT_STAGE_TIME_MS);
            wiggleFall.x.animateTo(wiggleFall.x.getAsInt() + WIGGLER_OFFSTAGE_DX, EXIT_STAGE_TIME_MS);
            wiggleBack.x.animateTo(wiggleBack.x.getAsInt() + WIGGLER_OFFSTAGE_DX, EXIT_STAGE_TIME_MS);
        }
    }

    private void startGame(int season) {
        componentLayer.alpha.animateTo(0, 300);

        PlayingScene.SET_SEASON(season);
        PlayingScene newGame = new PlayingScene();
        newGame.setTitleScene(this);

        addEvent(new SceneChangeEvent(newGame, 300));
    }
}
