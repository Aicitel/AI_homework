package com.aihomework.questions;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.aihomework.constants.Constants;
import com.aihomework.voicedemo.EnglishActivity;
import com.aihomework.voicedemo.EnglishDemo;
import com.aihomework.voicedemo.R;

/**
 * Created by bluemaple on 2016/5/7.
 */
public class StemFragment extends BaseQuestionFragment{

    private boolean isInitFinish = false;
    private ResultJudgeListener resultJudgeListener;
    private ImageView stemImage = null;
    private View view = null;

    Runnable initWakeThread = new Runnable() {
        @Override
        public void run() {
            while(!isInitFinish) {
                try {
                    Thread.sleep(10);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            initWakeHandler.sendMessage(new Message());
        }
    };
    Handler initWakeHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            drawView();
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        EnglishDemo.print("in on create");
        this.view =  inflater.inflate(R.layout.fragment_stem, container, false);

        ((ResultJudgeListener)this.getActivity()).registerMyTouchListener(
                new DrawTouchListener() {
                    @Override
                    public void onTouchEvent(MotionEvent event) {
                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                break;
                            case MotionEvent.ACTION_MOVE:
                                break;
                            case MotionEvent.ACTION_UP:
                                resultJudgeListener.immJudgeProcess("2333");
                                break;
                            default:
                                break;
                        }
                    }
                }
        );
        stemImage = (ImageView)view.findViewById(R.id.stemImageView);
        //drawView();
        isInitFinish = true;
        return view;
    }
    @Override
    public void setQuestionType(int type){
        super.QUESTION_TYPE = type;
        new Thread(initWakeThread).start();
    }

    @Override
    public void drawView() {
        int bg_type = QUESTION_TYPE/1000;
        EnglishActivity.print("stem question is "+QUESTION_TYPE+" "+bg_type+" "+(bg_type>>3));
        switch (bg_type){
            case Constants.CONSTANT_CHINESE:
                view.setBackgroundResource(R.drawable.bg_stem_cn);
                break;
            case Constants.CONSTANT_MATH:
                view.setBackgroundResource(R.drawable.bg_stem_ma);
                break;
            case Constants.CONSTANT_ENGLISH:
                view.setBackgroundResource(R.drawable.bg_stem_en);
                break;
        }

        switch (QUESTION_TYPE){
            case Constants.QUESTION_TYPE_MA_CHOICE:
                stemImage.setImageResource(R.drawable.stem_choice);
                break;
            case Constants.QUESTION_TYPE_MA_FILLIN:
                stemImage.setImageResource(R.drawable.stem_ma_fillin);
                break;
            case Constants.QUESTION_TYPE_MA_CAL:
                stemImage.setImageResource(R.drawable.stem_ma_cal);
                break;
            default:
                System.out.println("undefined question type");
        }
    }
    @Override
    public void setNextView() {

    }

    @Override
    public void setResultJudgeListener(ResultJudgeListener rl){
        this.resultJudgeListener = rl;
    }

    @Override
    public void onAnswerResponse(boolean isCorrect) {

    }
    @Override
    public void setRightAnswer(String rightAnswer){
    }

    @Override
    public boolean judgeAnswer(String userAnswer, String stdAnswer){
        return false;
    }
}
