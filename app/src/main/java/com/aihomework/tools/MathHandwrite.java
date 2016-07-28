package com.aihomework.tools;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.view.SurfaceHolder;

public class MathHandwrite implements HandwriteAdapter {

    //需要通过尺寸标定图片中的横线位置（暂不考虑横线数量）
    //通过标定的位置 对落笔点进行判别 录入不同的轨迹中 作为每行分开的识别提交
    private SurfaceHolder surfaceHolder = null;

    private Paint paint;
    private Canvas canvas;
    private Path path;
    public MathHandwrite(Paint paint,SurfaceHolder surfaceHolder,
                         Path path,SurfaceHolder.Callback callback )
    {
        this.paint = paint;
        this.surfaceHolder = surfaceHolder;
        this.path = path;
        this.surfaceHolder.addCallback(callback);
        this.surfaceHolder.setFormat(PixelFormat.TRANSPARENT);
    }
    @Override
    public void movePathTo(float x ,float y){
        path.moveTo(x, y);
    }
    @Override
    public void pathLineTo(float x ,float y){
        path.lineTo(x, y);
    }
    @Override
    public void drawView(){
        try {
            if (surfaceHolder != null) {
                canvas = surfaceHolder.lockCanvas();
                canvas.setDrawFilter(new PaintFlagsDrawFilter(0,
                        Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                paint.setColor(Color.RED);
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(5);
                canvas.drawPath(path, paint);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (canvas != null && surfaceHolder!=null)
                surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }
    @Override
    public boolean isAllFull(){
        return true;
    }
    @Override
    public void clearCanvas(){
        for (int i = 0; i < 4; i++) {
            try {
                if (surfaceHolder != null) {
                    canvas = surfaceHolder.lockCanvas();
                    canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                    path.reset();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (canvas != null&&surfaceHolder!=null)
                    surfaceHolder.unlockCanvasAndPost(canvas);
            }
        }
    }
    @Override
    public void drawTemplate(){

    }
    @Override
    public void clearTemplate(){

    }
    @Override
    public void resetTemplate() {

    }
    @Override
    public void resetTemplatePath() {

    }
}
