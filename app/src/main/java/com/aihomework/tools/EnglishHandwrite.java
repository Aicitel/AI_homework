package com.aihomework.tools;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.view.SurfaceHolder;

/**
 * Created by bluemaple on 2016/5/6.
 */
public class EnglishHandwrite implements HandwriteAdapter{

    private Paint paint;
    private SurfaceHolder surfaceHolder;
    private SurfaceHolder templateSurfaceHolder1;
    private Canvas canvas;
    private Canvas templateCanvas1;
    private Path path;
    private Path templatePath;

    private boolean[] isFull = {false};

    public EnglishHandwrite(Paint paint,SurfaceHolder surfaceHolder,SurfaceHolder templateSurfaceHolder1,
                            Path path, Path templatePath,SurfaceHolder.Callback callback )
    {
        this.paint = paint;
        this.surfaceHolder = surfaceHolder;
        this.templateSurfaceHolder1  = templateSurfaceHolder1;
        this.path = path;
        this.templatePath = templatePath;
        this.surfaceHolder.addCallback(callback);

        this.surfaceHolder.setFormat(PixelFormat.TRANSPARENT);
        this.templateSurfaceHolder1.setFormat(PixelFormat.TRANSPARENT);
    }

    public boolean isAllFull(){
        return isFull[0];
    }
    public void setFullFalse(){
        isFull[0]=false;
    }
    public void resetTemplate(){
        setFullFalse();
        clearTemplate();
    }
    public void resetTemplatePath(){
        templatePath.reset();
    }
    @Override
    public void movePathTo(float x,float y){
        path.moveTo(x, y);
        templatePath.moveTo(x / 2.5f, y / 2.5f);
    }
    @Override
    public void pathLineTo(float x, float y){
        path.lineTo(x, y);
        templatePath.lineTo(x/2.5f,y/2.5f);
    }

    @Override
    public void clearCanvas() {
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
                if (canvas != null && surfaceHolder!=null)
                    surfaceHolder.unlockCanvasAndPost(canvas);
            }
        }
    }

    @Override
    public void clearTemplate() {
        for (int i = 0; i < 4; i++) {
            try {
                if (templateSurfaceHolder1 != null) {
                    templateCanvas1 = templateSurfaceHolder1.lockCanvas();
                    templateCanvas1.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (templateCanvas1 != null&&templateSurfaceHolder1!=null)
                    templateSurfaceHolder1.unlockCanvasAndPost(templateCanvas1);
            }
        }
    }

    @Override
    public void drawTemplate(){
        SurfaceHolder holder=null;
        Canvas canvas = null;
        if(!isFull[0]){
            isFull[0]=true;
            holder = templateSurfaceHolder1;
            canvas = templateCanvas1;
        }

        try {
            if (holder != null) {
                canvas = holder.lockCanvas();
                canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                paint.setColor(Color.RED);
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(5);

                canvas.drawPath(templatePath, paint);
                templatePath.reset();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (canvas != null && holder!=null)
                holder.unlockCanvasAndPost(canvas);
        }
    }

    @Override
    public void drawView() {
        try {
            if (surfaceHolder != null) {
                canvas = surfaceHolder.lockCanvas();
                canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
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
}
