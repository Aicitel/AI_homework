package com.aihomework.questions;

import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.TextAppearanceSpan;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aihomework.userAction.UsrIntentTools;
import com.aihomework.voicedemo.EnglishDemo;
import com.aihomework.voicedemo.R;

/**
 * Created by bluemaple on 2016/5/8.
 */
public class ChinesePartReadFragment extends BaseQuestionFragment {

    TextView readText = null;
    int cntHighlightIndex = 0;
    private ResultJudgeListener resultJudgeListener;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view =  inflater.inflate(R.layout.fragment_cn_partread, container, false);
        readText =(TextView) view.findViewById(R.id.readText);
        view.findViewById(R.id.nextButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resultJudgeListener.immJudgeProcess("2333");
            }
        });
        ((ResultJudgeListener)this.getActivity()).setFragmentProb((TextView) view.findViewById(R.id.readText));
        ((ResultJudgeListener)this.getActivity()).registerMyTouchListener(
            new DrawTouchListener() {
                @Override
                public void onTouchEvent(MotionEvent event) {
                }
            }
        );
        setNextView();
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
        //TODO set next word red
        String text = readText.getText().toString();
        SpannableString styledText = new SpannableString(text);
        for(int index=0;index<text.length();index++) {
            if(cntHighlightIndex==index)
                styledText.setSpan(new TextAppearanceSpan(this.getActivity(), R.style.cn_partread_highlight), index, index+1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            else
                styledText.setSpan(new TextAppearanceSpan(this.getActivity(), R.style.cn_partread_normal), index, index+1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        readText.setText(styledText, TextView.BufferType.SPANNABLE);
        cntHighlightIndex++;
    }

    @Override
    public void onAnswerResponse(boolean isCorrect) {
        if(isCorrect)
            setNextView();
    }
    @Override
    public void setRightAnswer(String string){

    }
    @Override
    public boolean judgeAnswer(String userAnswer, String stdAnswer){
        String[] answers = stdAnswer.split("[- －]");
        String[] subAnswers = answers[cntHighlightIndex-1].split("[,，]");
        for(String subAnswer:subAnswers) {
            EnglishDemo.print(userAnswer+" and "+subAnswer+" and "+(cntHighlightIndex-1));
            if (UsrIntentTools.pinyinAllMatches(userAnswer, subAnswer))
                return true;
        }
        return false;
    }

}
