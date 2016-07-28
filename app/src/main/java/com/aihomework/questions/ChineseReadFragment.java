package com.aihomework.questions;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aihomework.voicedemo.R;

/**
 * Created by bluemaple on 2016/5/7.
 */
public class ChineseReadFragment extends BaseQuestionFragment{

    private ResultJudgeListener resultJudgeListener;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view =  inflater.inflate(R.layout.fragment_cn_read, container, false);
        ((ResultJudgeListener)this.getActivity()).setFragmentProb((TextView)view.findViewById(R.id.readText));
        setNextView();((ResultJudgeListener)this.getActivity()).registerMyTouchListener(
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
                        default:
                            break;
                    }
                }
            }
        );
        return view;
    }

    @Override
    public void drawView() {

    }
    @Override
    public void setResultJudgeListener(ResultJudgeListener rl){
        this.resultJudgeListener = rl;
    }


    @Override
    public void setNextView() {

    }
    @Override
    public void onAnswerResponse(boolean isCorrect) {

    }
    @Override
    public void setRightAnswer(String string){

    }
    @Override
    public boolean judgeAnswer(String userAnswer, String stdAnswer){
        return false;
    }
}
