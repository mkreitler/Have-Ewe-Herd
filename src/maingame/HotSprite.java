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
import pulpcore.sprite.ImageSprite;

public class HotSprite extends ImageSprite{
    private boolean bWasHot             = false;
    private HotSpriteListener listener  = null;

    // Constructors ////////////////////////////////////////////////////////////
    public HotSprite(String imageFile, int x, int y) {
        super(imageFile, x, y);
    }

    public HotSprite(CoreImage srcImage, int x, int y) {
        super(srcImage, x, y);
    }

    public void setListener(HotSpriteListener newListener) {
        listener = newListener;
    }

    @Override
    public void update(int elapsedTime) {
        super.update(elapsedTime);

        boolean bIsHot = isMouseOver();

        if (bIsHot && !bWasHot) {
            onHot();
        }
        else if (!bIsHot && bWasHot) {
            onCool();
        }

        if (isMousePressed()) {
            onPressed();
        }

        bWasHot = bIsHot;
    }

    // Public Methods //////////////////////////////////////////////////////////

    // Protected Methods ///////////////////////////////////////////////////////
    protected void onHot() {
        if (listener != null) listener.becameHot(this);
    }

    protected void onCool() {
        if (listener != null) listener.becameCool(this);
    }

    protected void onPressed() {
        if (listener != null) listener.pressed(this);
    }

    // Private Methods /////////////////////////////////////////////////////////
}
