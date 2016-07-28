package com.aihomework.questions;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import com.aihomework.constants.Constants;
import com.aihomework.tools.HandwriteAdapter;
import com.aihomework.voicedemo.EnglishActivity;
import com.aihomework.voicedemo.EnglishDemo;
import com.hanvon.HWCloudManager;

import org.json.JSONObject;

public abstract class BaseWordWriteFragment extends BaseQuestionFragment {

    protected HWCloudManager hwCloudManager;
    private int left;
    private int top;
    private int bottom;
    private long now;
    private boolean isRunning = true, start = false;
    public boolean allRightFlag = false;

    protected StringBuilder sbuilder = new StringBuilder();
    protected HandwriteAdapter handwrite = null;
    protected SurfaceView surface;

    protected int HWRecogIndex = -1;

    /**
     * Fragment中，注册
     * 接收MainActivity的Touch回调的对象
     * 重写其中的onTouchEvent函数，并进行该Fragment的逻辑处理
     */
    protected DrawTouchListener mTouchListener = new DrawTouchListener() {
        @Override
        public void onTouchEvent(MotionEvent event) {
            if (event.getY() >= top && event.getY() <= bottom) {
                if(handwrite.isAllFull()){
                    handwrite.resetTemplate();
                }
                start = true;
                long init = now;
                now = System.currentTimeMillis();
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (now - init >= 100 && now - init <= 1000) {
                            sbuilder.append("-1,").append("0,");
                        }
                        handwrite.movePathTo(event.getX() - left, event.getY() - top);
                        sbuilder.append((int) (event.getX())).append(",").append((int) (event.getY() - top)).append(",");
                        break;
                    case MotionEvent.ACTION_MOVE:
                        handwrite.pathLineTo(event.getX() - left, event.getY() - top);
                        sbuilder.append((int) (event.getX())).append(",").append((int) (event.getY() - top)).append(",");
                        break;

                    default:
                        break;
                }
            }
        }
    };

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
            surface.getLocationInWindow(location);
            top = location[1];
            left = location[0];
            bottom = top + surface.getHeight();
            new Thread(wlineThread).start();
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            System.out.println("-----------surface Destroyed-----------");
            isRunning = false;

        }
    }


    Runnable wlineThread = new Runnable() {
        @Override
        public void run() {
            while (isRunning) {
                if(true) {
                    handwrite.drawView();
                    if (start) {
                        long temp = System.currentTimeMillis() - now;
                        if (temp > 1000) {
                            sbuilder.append("-1").append(",").append("0");
                            String content = null;
                            switch (HWRecogIndex) {
                                case Constants.HW_SINGLE_WRITE:
                                    content = hwCloudManager.handSingleLanguage("1", "chns", sbuilder.toString());
                                    break;
                                case Constants.HW_ENGLISH_WORD:
                                    content = hwCloudManager.handLineLanguage("en", sbuilder.toString());
                                    break;
                                default:
                                    EnglishActivity.print("error in wlineThread of -1 HWRecogIndex");
                                    break;
                            }
                            //String content = "content";
                            Message message = new Message();
                            Bundle bundle = new Bundle();
                            bundle.putString("content", content);
                            message.setData(bundle);
                            BaseWordWriteFragment.this.writeLineHandler.sendMessage(message);

                            start = false;
                            handwrite.clearCanvas();
                            sbuilder = new StringBuilder();
                        }
                    }
                }
                try {
                    Thread.sleep(80);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    };


    Handler writeLineHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle bundle = msg.getData();
            String content = bundle.getString("content");
            showResult(content);
        }
    };

    private void showResult(String content) {
        JSONObject obj = null;
        try {
            System.out.println(content);
            obj = new JSONObject(content);
            if (obj.length()!=0) {
                if ("0".equals(obj.getString("code"))) {
                    String result = obj.getString("result");
                    if (null == result) {
                        Toast.makeText(this.getActivity().getApplication(), "请重新输入", Toast.LENGTH_SHORT).show();
                    } else {
                        String[] words = result.split(",");
                        int len = words.length;
                        char[] wordsChar = new char[len];
                        int i = 0;
                        for (String word : words) {
                            if ("0".equals(word)) {
                                wordsChar[i++] = ' ';
                            } else {
                                wordsChar[i++] = (char) Integer.parseInt(word);
                            }
                        }
                        EnglishDemo.print(wordsChar[0] + " "+"total "+result);
                        if(allRightFlag)
                            sendResToHwProcess("input");
                        else {
                            switch (HWRecogIndex) {
                                case Constants.HW_SINGLE_WRITE:
                                    sendResToHwProcess(wordsChar[0] + "");
                                    break;
                                case Constants.HW_ENGLISH_WORD:
                                    String str  =new String(wordsChar);
                                    sendResToHwProcess(str.split(" ")[0]);
                                    break;
                                default:
                                    EnglishActivity.print("error in wlineThread of -1 HWRecogIndex");
                                    break;
                            }
                        }
                    }
                } else {
                    sendResToHwProcess("input");
                    // Toast.makeText(this.getActivity().getApplication(), obj.toString(), Toast.LENGTH_SHORT).show();
                }
            }
            else{
                Toast.makeText(this.getActivity().getBaseContext(),"please check network",Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    abstract public void sendResToHwProcess(String result);


}
