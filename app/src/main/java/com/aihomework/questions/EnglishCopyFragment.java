package com.aihomework.questions;

import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aihomework.constants.Constants;
import com.aihomework.tools.EnglishHandwrite;
import com.aihomework.voicedemo.R;
import com.hanvon.HWCloudManager;

public class EnglishCopyFragment extends BaseWordWriteFragment {

    private ResultJudgeListener resultJudgeListener;
    private TextView progressText;
    private int roundTime = 0;
    private String answerResult = "";

    @Override
    public void setResultJudgeListener(ResultJudgeListener rl){
        this.resultJudgeListener = rl;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view =  inflater.inflate(R.layout.fragment_en_copy, container, false);
        surface = (SurfaceView) view.findViewById(R.id.surface);
        SurfaceView wordSurface1 = (SurfaceView) view.findViewById(R.id.wordSurface1);
        surface.setZOrderOnTop(true);
        wordSurface1.setZOrderOnTop(true);
        ((ResultJudgeListener)this.getActivity()).registerMyTouchListener(mTouchListener);
        progressText = (TextView)view.findViewById(R.id.progressText);

        super.hwCloudManager = new HWCloudManager(this.getActivity(), "8379a4b4-9ee1-41ce-a0ff-052c4cfc3430");
        super.handwrite = new EnglishHandwrite(new Paint(),surface.getHolder(), wordSurface1.getHolder(),
                new Path(),new Path(),new SurfaceCallback());
        ((ResultJudgeListener)this.getActivity()).setFragmentProb((TextView) view.findViewById(R.id.problemTextView));

        super.HWRecogIndex = Constants.HW_ENGLISH_WORD;
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
        if (isCorrect) {
            handwrite.drawTemplate();
            handwrite.clearCanvas();
            progressText.setText((++roundTime) + " / 2");
        } else {
            handwrite.resetTemplatePath();
            resultJudgeListener.executeResponse(this.answerResult);
        }
    }

    @Override
    public void sendResToHwProcess(String result){
        resultJudgeListener.immJudgeProcess(result);
        this.answerResult = result;
    }
    @Override
    public void setRightAnswer(String string){

    }

    @Override
    public boolean judgeAnswer(String userAnswer, String stdAnswer){
        return false;
    }
}
