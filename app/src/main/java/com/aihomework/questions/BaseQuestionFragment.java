package com.aihomework.questions;

import android.app.Fragment;

/**
 * Created by bluemaple on 2016/5/7.
 */
public abstract class BaseQuestionFragment extends Fragment{
    protected int QUESTION_TYPE = -1;
    public void setQuestionType(int type){
        this.QUESTION_TYPE = type;
    }
    abstract public void setRightAnswer(String rightAnswer);
    abstract public void setResultJudgeListener(ResultJudgeListener rl);
    abstract public void onAnswerResponse(boolean isCorrect);
    abstract public void setNextView();
    abstract public void drawView();
    abstract public boolean judgeAnswer(String userAnswer,String stdAnswer);
}
