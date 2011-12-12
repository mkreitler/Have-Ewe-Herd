/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package maingame;

/**
 *
 * @author mkreitler
 */
public interface HotSpriteListener {
    public void becameHot(HotSprite hotSprite);
    public void becameCool(HotSprite hotSprite);
    public void pressed(HotSprite hotSprite);
}
