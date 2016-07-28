package com.aihomework.questions;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import com.aihomework.constants.Constants;
import com.aihomework.tools.MathHandwrite;
import com.aihomework.userAction.HomeworkRectifor;
import com.aihomework.voicedemo.EnglishDemo;
import com.hanvon.HWCloudManager;

import org.json.JSONObject;

public abstract class BaseFormulaFragment extends BaseQuestionFragment{

    protected HWCloudManager hwCloudManager;

    private static int top,left;
    private static int bottom;
    public boolean allRightFlag = false;

    private int[] linePosX = {0,0,0,0};
    protected int lineGap;
    private int width;
    private boolean isRunning = true;
    protected StringBuilder sbuilder1 = new StringBuilder();
    protected StringBuilder sbuilder2 = new StringBuilder();

    protected MathHandwrite mathHandwrite = null;
    protected SurfaceView surface;

    /**
     * Fragment中，注册
     * 接收MainActivity的Touch回调的对象
     * 重写其中的onTouchEvent函数，并进行该Fragment的逻辑处理
     */
    protected DrawTouchListener mTouchListener = new DrawTouchListener() {
        @Override
        public void onTouchEvent(MotionEvent event) {
            if (event.getY() >= top && event.getY() <= bottom) {
                int x,y;
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mathHandwrite.movePathTo(event.getX()-left, event.getY() - top);

                        x = (int)event.getX()-left;
                        y = (int)event.getY()-top;
                        if(y<lineGap)
                            sbuilder1.append(x).append(",").append(y).append(",");
                        else
                            sbuilder2.append(x).append(",").append(y).append(",");
                        break;

                    case MotionEvent.ACTION_MOVE:
                        mathHandwrite.pathLineTo(event.getX()-left, event.getY() - top);
                        x = (int)event.getX()-left;
                        y = (int)event.getY()-top;
                        if(y<lineGap)
                            sbuilder1.append(x).append(",").append(y).append(",");
                        else
                            sbuilder2.append(x).append(",").append(y).append(",");
                        break;

                    case MotionEvent.ACTION_UP:
                        y = (int)event.getY()-top;
                        if(y<lineGap)
                            sbuilder1.append("-1,").append("0,");
                        else
                            sbuilder2.append("-1,").append("0,");

                    default:
                        break;
                }
            }
        }
    };
    protected void onSubmit() {
        /*
        String[] array = sbuilder2.toString().split(",");
        if (array.length >= 4) {
            for (int i = 0; i < array.length; i += 2) {
                int x = Integer.parseInt(array[i]);
                if (x == -1) {
                    sbuilder1.append("-1,0,");
                    continue;
                }
                int y = Integer.parseInt(array[i + 1]);
                x = x + y / lineGap * width;
                y = y - y / lineGap * lineGap;
                sbuilder1.append(x).append(",").append(y).append(",");
            }
        }*/

        sbuilder1.append("-1").append(",").append("-1");
        sbuilder2.append("-1").append(",").append("-1");
        new Thread(resultThread).start();
    }

    Handler discernHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle bundle = msg.getData();
            String content1 = bundle.getString("content1");
            String content2 = bundle.getString("content2");
            showResult(content1,content2);
        }
    };

    protected void showResult(String content1,String content2) {
        try {
            JSONObject resultJson1 = new JSONObject(content1);
            JSONObject resultJson2 = new JSONObject(content2);
            if (resultJson1.getString("code").equals("0") &&resultJson2.getString("code").equals("0")) {
                String result1 = HomeworkRectifor.mathRectify(resultJson1.getJSONArray("formulas").getString(0));
                String result2 = HomeworkRectifor.mathRectify(resultJson2.getJSONArray("formulas").getString(0));
                if (null == result1 || null == result2) {
                    Toast.makeText(this.getActivity().getApplication(), "请重新输入", Toast.LENGTH_SHORT).show();
                } else {
                    //remain 正常判断
                    //resultView.setText("n:" + result);
                    EnglishDemo.print("n " + result1+result2);
                    if(allRightFlag)
                        sendResToHwProcess(Constants.MAGIC_ANSEWER);
                        //immJudgeProcess(Constants.MAGIC_ANSEWER);
                    else
                        sendResToHwProcess(result1+result2);
                        //immJudgeProcess(result);
                }
            } else {
                //Toast.makeText(this.getActivity().getApplication(), "请重试", Toast.LENGTH_SHORT).show();
                sendResToHwProcess(Constants.MAGIC_ANSEWER);
                Log.d("TEST", "error");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setNextView() {

    }
    @Override
    public void drawView(){

    }

    class SurfaceCallback implements SurfaceHolder.Callback {

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width,
                                   int height) {
            // TODO Auto-generated method stub

        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            int[] location = new int[2];
            surface.getLocationOnScreen(location);
            top = location[1];
            left = location[0];
            width = surface.getWidth();
            bottom = top + surface.getHeight();
            //TODO change line gap argvs
            for(int i=0;i<4;i++)
                linePosX[i]=(bottom-top)/2*(i+1);
            lineGap = linePosX[1]-linePosX[0];
            linePosX[0]+=lineGap;
            linePosX[1]+=lineGap;
            EnglishDemo.print("top "+top+" bottom "+bottom+" lineGap "+lineGap+" linePosX 0 "+linePosX[0]+" linePosX 1 "+linePosX[1]);
            new Thread(formulaThraed).start();
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            System.out.println("-----------surface Destroyed-----------");
            isRunning = false;

        }
    }
    Runnable resultThread = new Runnable() {
        @Override
        public void run() {
            String content1 = hwCloudManager
                    .formulaLanguage(sbuilder1.toString());
            String content2 = hwCloudManager
                    .formulaLanguage(sbuilder2.toString());
            Bundle mBundle = new Bundle();
            mBundle.putString("content1", content1);
            mBundle.putString("content2", content2);
            Message msg = new Message();
            msg.setData(mBundle);
            discernHandler.sendMessage(msg);

            sbuilder1 = new StringBuilder();
            sbuilder2 = new StringBuilder();
            /*
            sbuilder3 = new StringBuilder();
            sbuilder4 = new StringBuilder();
            */
        }
    };

    Runnable formulaThraed = new Runnable() {
        @Override
        public void run() {
            while (isRunning) {
                if(true)
                    mathHandwrite.drawView();
                try {
                    Thread.sleep(80);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    };

    protected void clearContent(){
        this.sbuilder1 = new StringBuilder();
        this.sbuilder2 = new StringBuilder();
    }

    abstract public void sendResToHwProcess(String result);
}
