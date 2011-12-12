/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package maingame;

/**
 *
 * @author mkreitler
 */
public class LevelSetup {
    private static final int    BASE_COMPLETION_SCORE   = 1000;

    private String[] geometry   = null;
    private int fenceResources  = 0;
    private int eFenceResources = 0;
    private String startMessage = null;
    private int startTextColor  = 0x00000000;
    private int startTextTime   = 0;
    private String endMessage   = null;
    private int endTextColor    = 0x00000000;
    private int endTextTime     = 0;
    private int completionScore = 0;

    public LevelSetup(String[] currentGeometry,
                      int fenceResourcesIn,
                      int eFenceResourcesIn,
                      String startMessage,
                      int messageColor,
                      int messageDuration,
                      String endMessage,
                      int endTextColor,
                      int endTextTime,
                      int completionScoreIn) {

        setup(currentGeometry,
              fenceResourcesIn,
              eFenceResourcesIn,
              startMessage,
              messageColor,
              messageDuration,
              endMessage,
              endTextColor,
              endTextTime,
              completionScoreIn);
    }

    public LevelSetup(String[] currentGeometry,
                      int fenceResourcesIn,
                      int eFenceResourcesIn,
                      String startMessage,
                      int messageColor,
                      int messageDuration,
                      String endMessage,
                      int endTextColor,
                      int endTextTime) {

        setup(currentGeometry,
                   fenceResourcesIn,
                   eFenceResourcesIn,
                   startMessage,
                   messageColor,
                   messageDuration,
                   endMessage,
                   endTextColor,
                   endTextTime,
                   BASE_COMPLETION_SCORE);
    }

    public void setup(String[] currentGeometry,
                      int fenceResourcesIn,
                      int eFenceResourcesIn,
                      String startMessage,
                      int messageColor,
                      int messageDuration,
                      String endMessage,
                      int endTextColor,
                      int endTextTime,
                      int completionScoreIn) {
        geometry        = currentGeometry;
        fenceResources  = fenceResourcesIn;
        eFenceResources = eFenceResourcesIn;

        this.startMessage   = startMessage;
        this.startTextColor = messageColor;
        this.startTextTime  = messageDuration;

        this.endMessage     = endMessage;
        this.endTextColor   = endTextColor;
        this.endTextTime    = endTextTime;

        completionScore     = completionScoreIn;
    }

    public int getCompletionScore() { return completionScore; }
    public String getMessage(boolean bStartMessage) { return bStartMessage ? startMessage : endMessage; }
    public int getMessageColor(boolean bStartColor) { return bStartColor ? startTextColor : endTextColor; }
    public int getMessageDuration(boolean bStartDuration) { return bStartDuration ? startTextTime : endTextTime; }

    public int getFenceResources() { return fenceResources; }

    public int getEFenceResources() { return eFenceResources; }

    public int getRows() {
        int rows = -1;

        if (geometry != null) {
            rows = geometry.length;
        }

        return rows;
    }

    public int getCols() {
        int cols = -1;

        if (geometry != null &&
            geometry[0] != null) {
            cols = geometry[0].length();
        }

        return cols;
    }

    public char getCharAt(int iRow, int iCol) {
        char charOut = '\0';

        if (geometry != null &&
            iRow >= 0 &&
            iRow < geometry.length &&
            geometry[iRow] != null &&
            iCol >= 0 &&
            iCol < geometry[iRow].length()) {

            charOut = geometry[iRow].charAt(iCol);
        }

        return charOut;
    }
}
