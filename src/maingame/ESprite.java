package maingame;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author mkreitler
 */
import pulpcore.image.CoreImage;

public class ESprite extends HotSprite {
    private static final int    SWITCH_TIME_MS  = 33;
    private static final int    CHARGE_DURATION = 1000;

    private final int[]  ELECTRIC_RED    = {64, 160};
    private final int[]  ELETRIC_GREEN   = {64, 160};
    private final int[]  ELETRIC_BLUE    = {255, 64};

    private CoreImage images[] = {null, null, null};
    private int switchTimer;
    private int switchesRemaining;
    private boolean bElectrified;
    private int electrifiedWhen;

    // Publice Methods /////////////////////////////////////////////////////////
    public ESprite(CoreImage image, int x, int y) {
        super(image, x, y);

        // Create tinted images.
        images[0] = image;
        images[1] = image.tint(pulpcore.image.Colors.rgb(ELECTRIC_RED[0],
                                                         ELETRIC_GREEN[0],
                                                         ELETRIC_BLUE[0]));
        images[2] = image.tint(pulpcore.image.Colors.rgb(ELECTRIC_RED[1],
                                                         ELETRIC_GREEN[1],
                                                         ELETRIC_BLUE[1]));

        switchTimer     = 0;
        bElectrified    = false;

        this.setImage(images[0]);
    }

    public void electrify(int turn) {
        switchesRemaining = CHARGE_DURATION / SWITCH_TIME_MS;
        bElectrified    = true;
        electrifiedWhen = turn;
    }

    public boolean isElectrified() { return bElectrified; }
    public void resetElectrified() { bElectrified = false; electrifiedWhen = -1; }
    public int electrifiedWhen() { return electrifiedWhen; }

    @Override
    public void update(int elapsedTime) {
        if (switchesRemaining > 0) {
            switchTimer += elapsedTime;

            int switchCount = switchTimer / SWITCH_TIME_MS;
            boolean bSwitch = switchCount % 2 != 0;

            switchesRemaining -= switchCount;

            switchTimer -= switchCount * SWITCH_TIME_MS;

            if (bSwitch) {
                if (this.getImage() != images[1]) {
                    this.setImage(images[1]);
                }
                else
                {
                    this.setImage(images[2]);
                }
            }
        }
        else if (this.getImage() != images[0]) {
            this.setImage(images[0]);
        }

        super.update(elapsedTime);
    }
}

// /////////////////////////////////////////////////////////////////////////////

