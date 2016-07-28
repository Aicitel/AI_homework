package com.aihomework.tools;

/**
 * Created by bluemaple on 2016/5/6.
 */
public interface HandwriteAdapter {
    void movePathTo(float x ,float y);
    void pathLineTo(float x ,float y);
    void drawView();
    void clearCanvas();
    void drawTemplate();
    void clearTemplate();
    void resetTemplate();
    void resetTemplatePath();
    boolean isAllFull();
}
