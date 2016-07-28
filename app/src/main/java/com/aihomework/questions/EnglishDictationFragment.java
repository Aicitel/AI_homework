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

import java.util.ArrayList;

public class EnglishDictationFragment extends BaseWordWriteFragment {

    ArrayList<String> dictationBuffer = new ArrayList<String>();

    private ResultJudgeListener resultJudgeListener;
    private TextView questionTextView = null;

    @Override
    public void setResultJudgeListener(ResultJudgeListener rl){
        this.resultJudgeListener = rl;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view =  inflater.inflate(R.layout.fragment_en_dictation, container, false);
        surface = (SurfaceView) view.findViewById(R.id.surface);
        SurfaceView wordSurface1 = (SurfaceView) view.findViewById(R.id.wordSurface1);
        surface.setZOrderOnTop(true);
        wordSurface1.setZOrderOnTop(true);
        ((ResultJudgeListener)this.getActivity()).registerMyTouchListener(mTouchListener);
        //progressText = (TextView)view.findViewById(R.id.progressText);

        super.hwCloudManager = new HWCloudManager(this.getActivity(), "8379a4b4-9ee1-41ce-a0ff-052c4cfc3430");
        super.handwrite = new EnglishHandwrite(new Paint(),surface.getHolder(), wordSurface1.getHolder(),
                new Path(),new Path(),new SurfaceCallback());
        ((ResultJudgeListener)this.getActivity()).setFragmentProb((TextView)view.findViewById(R.id.problemTextView));
        questionTextView = (TextView)view.findViewById(R.id.problemTextView);
        view.findViewById(R.id.dictationButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resultJudgeListener.immJudgeProcess(dictationBuffer.size()!=0?dictationBuffer.get(0):"");
                dictationBuffer.clear();
            }
        });

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
            //handwrite.drawTemplate();
        } else {
            handwrite.resetTemplatePath();
        }
    }

    @Override
    public void sendResToHwProcess(String result){
        dictationBuffer.add(result);
        handwrite.drawTemplate();
    }

    @Override
    public void setRightAnswer(String rightAnswer){
        questionTextView.setText(rightAnswer);
    }

    @Override
    public boolean judgeAnswer(String userAnswer, String stdAnswer){
        return false;
    }
}
