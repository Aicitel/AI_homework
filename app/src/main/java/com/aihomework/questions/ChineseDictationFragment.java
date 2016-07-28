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
import com.aihomework.tools.ChineseHandwrite;
import com.aihomework.voicedemo.R;
import com.hanvon.HWCloudManager;

import java.util.ArrayList;

public class ChineseDictationFragment extends BaseWordWriteFragment {

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
        View view =  inflater.inflate(R.layout.fragment_cn_dictation, container, false);
        surface = (SurfaceView) view.findViewById(R.id.surface);
        SurfaceView wordSurface1 = (SurfaceView) view.findViewById(R.id.wordSurface1);
        SurfaceView wordSurface2 = (SurfaceView) view.findViewById(R.id.wordSurface2);
        surface.setZOrderOnTop(true);
        wordSurface1.setZOrderOnTop(true);
        wordSurface2.setZOrderOnTop(true);
        ((ResultJudgeListener)this.getActivity()).registerMyTouchListener(mTouchListener);

        super.hwCloudManager = new HWCloudManager(this.getActivity(), "8379a4b4-9ee1-41ce-a0ff-052c4cfc3430");
        super.handwrite = new ChineseHandwrite(new Paint(),surface.getHolder(), wordSurface1.getHolder(), wordSurface2.getHolder(),
                new Path(),new Path(),new SurfaceCallback());
        questionTextView = (TextView)view.findViewById(R.id.problemTextView);
        ((ResultJudgeListener)this.getActivity()).setFragmentProb(questionTextView);
        ((ResultJudgeListener)this.getActivity()).setCanvasVisibility(wordSurface2);

        view.findViewById(R.id.dictationButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int size = dictationBuffer.size();
                if (resultJudgeListener.getAnswerTextLength() > 1 && size >= 2) {
                    resultJudgeListener.immJudgeProcess(dictationBuffer.get(size - 2) + dictationBuffer.get(size - 1));
                    dictationBuffer.clear();
                } else if (resultJudgeListener.getAnswerTextLength() == 1 && size >= 1) {
                    resultJudgeListener.immJudgeProcess(dictationBuffer.get(size - 1));
                    dictationBuffer.clear();
                }
            }
        });

        super.HWRecogIndex = Constants.HW_SINGLE_WRITE;
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
        if(isCorrect){

        }
        else{
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