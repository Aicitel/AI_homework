package com.aihomework.questions;

import android.view.View;
import android.widget.TextView;

public interface ResultJudgeListener {

    /**
     * bad construct, coupling
     * @param view ready to be set invisible
     */
    void setCanvasVisibility(View view);

    /**
     * send text to fragment
     * @param textView problem text in fragment
     */
    void setFragmentProb(TextView textView);

    /**
     * call main activity to execute wrong answer response
     * @param wrongAnswerResString string ready to response
     */
    void executeResponse(String wrongAnswerResString);

    /**
     * detach touching control to fragment
     * @param listener listener to be registered (fragment in this case)
     */
    void registerMyTouchListener(DrawTouchListener listener);

    /**
     * send answer length for judging whether to next
     * @return length
     */
    int getAnswerTextLength();

    /**
     * core method in activity, control the whole homework process
     * @param answer answer from fragment
     */
    void immJudgeProcess(String answer);

    /**
     * get choices from activity, perhaps replace setFragmentProb
     *
     */
    String[] getChoices();

    /**
     * judge answer from question fragmennt
     * @return
     */
    boolean judgeAnswerFromFrag(String userAnswer,String stdAnswer);
}
