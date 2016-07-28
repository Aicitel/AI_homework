package com.aihomework.questions;

import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aihomework.tools.MathHandwrite;
import com.aihomework.voicedemo.R;
import com.hanvon.HWCloudManager;

/**
 * Created by bluemaple on 2016/5/16.
 */
public class MathCalFragment extends BaseFormulaFragment {

    private ResultJudgeListener resultJudgeListener;

    @Override
    public void setResultJudgeListener(ResultJudgeListener rl){
        this.resultJudgeListener = rl;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        //TODO change layout
        View view =  inflater.inflate(R.layout.fragment_ma_cal, container, false);
        surface = (SurfaceView) view.findViewById(R.id.surface);
        surface.setZOrderOnTop(true);
        ((ResultJudgeListener)this.getActivity()).registerMyTouchListener(mTouchListener);
        view.findViewById(R.id.submitButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSubmit();
            }
        });

        hwCloudManager = new HWCloudManager(this.getActivity(), "8379a4b4-9ee1-41ce-a0ff-052c4cfc3430");
        mathHandwrite = new MathHandwrite(new Paint(),surface.getHolder(),
                new Path(),new SurfaceCallback());
        ((ResultJudgeListener)this.getActivity()).setFragmentProb((TextView) view.findViewById(R.id.problemTextView));

        mathHandwrite.clearCanvas();
        clearContent();
        mathHandwrite.clearCanvas();

        view.findViewById(R.id.delButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mathHandwrite.clearCanvas();
                clearContent();
                mathHandwrite.clearCanvas();
            }
        });

        return view;
    }
    @Override
    public void setNextView() {

    }
    @Override
    public void drawView(){

    }
    @Override
    public void onAnswerResponse(boolean isCorrect){
    }

    @Override
    public void sendResToHwProcess(String result){
        resultJudgeListener.immJudgeProcess(result);
    }

    @Override
    public void setRightAnswer(String rightAnswer){
    }
    @Override
    public boolean judgeAnswer(String userAnswer, String stdAnswer){
        return false;
    }
}
