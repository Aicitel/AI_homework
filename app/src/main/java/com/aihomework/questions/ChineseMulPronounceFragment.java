package com.aihomework.questions;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.aihomework.tools.ChoiceAdapter;
import com.aihomework.voicedemo.R;

/**
 * Created by bluemaple on 2016/5/8.
 */
public class ChineseMulPronounceFragment extends BaseQuestionFragment {

    ChoiceAdapter choiceAdapter = null;
    ListView choiceListView = null;
    private ResultJudgeListener resultJudgeListener;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view =  inflater.inflate(R.layout.fragment_cn_choice, container, false);
        ((ResultJudgeListener)this.getActivity()).setFragmentProb((TextView) view.findViewById(R.id.problemTextView));
        ((ResultJudgeListener)this.getActivity()).registerMyTouchListener(
                new DrawTouchListener() {
                    @Override
                    public void onTouchEvent(MotionEvent event) {
                    }
                }
        );
        choiceAdapter = new ChoiceAdapter(this.getActivity().getBaseContext());
        choiceAdapter.setData(((ResultJudgeListener) this.getActivity()).getChoices());
        choiceAdapter.notifyDataSetChanged();
        choiceListView = (ListView)view.findViewById(R.id.choice_listview);
        choiceListView.setAdapter(choiceAdapter);
        choiceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                resultJudgeListener.immJudgeProcess(position + "");
            }
        });
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
