import maingame.PlayingScene;

import static pulpcore.image.Colors.*;
import pulpcore.sprite.Button;
import pulpcore.sprite.Group;
import pulpcore.sprite.FilledSprite;
import pulpcore.sprite.ImageSprite;
import pulpcore.sprite.Sprite;
import pulpcore.Stage;
import maingame.HotSprite;
import maingame.HotSpriteListener;
import maingame.UIWiggler;
import pulpcore.sound.Sound;
import pulpcore.animation.event.SceneChangeEvent;
import pulpcore.scene.Scene2D;

public class OptionScene extends Scene2D implements HotSpriteListener {

    Scene2D titleScene = null;

    Button toggleButton;
    Button backButton;
    Button errorButton;
    
    Group componentLayer;

    UIWiggler wiggleBack;
    UIWiggler wigglePlay;

    Sound mouseOver;
    Sound buttonPress;

    public void setTitleScene(Scene2D titleSceneIn) { titleScene = titleSceneIn; }

    public void becameHot(HotSprite hotSprite) {
        mouseOver.play();
    }

    public void becameCool(HotSprite hotSprite) {
    }

    public void pressed(HotSprite hotSprite) {
        buttonPress.play();

        // Go back to the previous scene
        if (hotSprite == wiggleBack && titleScene != null) {
            Stage.setScene(titleScene);
        }
        else {
            // Launch the tutorial levels.
            startGame(PlayingScene.MODE_TUTORIAL);
        }
    }

    @Override
    public void load() {
        wiggleBack = new UIWiggler("ui_back.png", 225, 575);
        wigglePlay = new UIWiggler("ui_play.png", 115, 575);

        wiggleBack.setListener(this);
        wiggleBack.setAnchor(Sprite.CENTER);
        wigglePlay.setListener(this);
        wigglePlay.setAnchor(Sprite.CENTER);

        mouseOver   = Sound.load("sound_mouseover.wav");
        buttonPress = Sound.load("sound_press.wav");

        Sound introSound = Sound.load("sound_sheep.wav");
        introSound.play();

        componentLayer = new Group();
        componentLayer.add(wiggleBack);
        componentLayer.add(wigglePlay);


        add(new FilledSprite(BLACK));
        add(new ImageSprite("infoscreen.png", 0, 0));
        addLayer(componentLayer);
    }

    // Private Methods /////////////////////////////////////////////////////////
    private void startGame(int season) {
        // TODO: unite this cut-and-paste code with the code in TitleScene.
        if (titleScene != null) {
            componentLayer.alpha.animateTo(0, 300);

            PlayingScene.SET_SEASON(season);
            PlayingScene newGame = new PlayingScene();
            newGame.setTitleScene(titleScene);

            addEvent(new SceneChangeEvent(newGame, 300));
        }
    }
}
