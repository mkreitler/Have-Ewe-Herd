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
import pulpcore.sprite.Sprite;
import pulpcore.sprite.ImageSprite;
import pulpcore.animation.Timeline;
import pulpcore.sprite.Group;
import pulpcore.animation.Easing;
import pulpcore.animation.event.RemoveSpriteEvent;

public class HeartParticle extends ImageSprite {
    private static final int    OFFSET_X        = -8;
    private static final int    OFFSET_Y        = -8;
    private static final int    DRIFT_SPEED     = 5;
    private static final int    MAIN_SPEED      = 40;
    private static final int    MIN_DURATION    = 667;
    private static final int    MAX_DURATION    = 1333;

    private static final int    ALPHA_START_INTERVAL    = 100;
    
    private int duration;

    // Constructors ////////////////////////////////////////////////////////////
    public HeartParticle(CoreImage image, int x, int y) {
        super(image, x, y);

        duration = 0;
    }

    public HeartParticle(String imageName, int x, int y) {
        super(imageName, x, y);

        duration = 0;
    }
    
    // Public Methods //////////////////////////////////////////////////////////
    public void init(PlayingScene scene, Group layer, int posX, int posY, int direction) {
        // Update internal variables to match emitter movement.

        int velX = (int)(2.0 * (0.5 - Math.random()) * DRIFT_SPEED + 0.5f);
//        int velY = (int)(2.0 * (0.5 - Math.random()) * DRIFT_SPEED + 0.5f);

        int velY = -MAIN_SPEED;

//        switch(direction) {
//            case PlayingScene.DIRECTION_RIGHT:
//            {
//                velX = MAIN_SPEED;
//            }
//            break;
//
//            case PlayingScene.DIRECTION_DOWN:
//            {
//                velY = MAIN_SPEED;
//            }
//            break;
//
//            case PlayingScene.DIRECTION_LEFT:
//            {
//                velX = -MAIN_SPEED;
//            }
//            break;
//
//            default: // Includes DIRECTION_UP case.
//            {
//                velY = -MAIN_SPEED;
//            }
//            break;
//
//        }

        x.set(posX + OFFSET_X);
        y.set(posY + OFFSET_Y);

        float param = (float)Math.random();
        duration = (int)(param * MIN_DURATION + (1.f - param) * MAX_DURATION + 0.5f);

        int goalX = posX + OFFSET_X + velX * duration / 1000;
        int goalY = posY + OFFSET_Y + velY * duration / 1000;

        setAnchor(Sprite.CENTER);
        layer.add(this);

        Timeline timeline = new Timeline();

        timeline.animateTo(this.x, goalX, duration, Easing.REGULAR_OUT);
        timeline.animateTo(this.y, goalY, duration, Easing.REGULAR_OUT);
        timeline.at(ALPHA_START_INTERVAL).animateTo(this.alpha, 0, duration - ALPHA_START_INTERVAL, Easing.REGULAR_OUT);
        timeline.add(new RemoveSpriteEvent(layer, this, duration));

        scene.addTimeline(timeline);
    }

    @Override
    public void update(int elapsedTime) {
        super.update(elapsedTime);

        duration = Math.max(duration - elapsedTime, 0);
    }

    public boolean isAvailable() {
        return duration == 0;
    }

    // Protected Methods ///////////////////////////////////////////////////////
    
    // Private Methods /////////////////////////////////////////////////////////
}
