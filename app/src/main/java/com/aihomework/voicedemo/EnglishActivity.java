package com.aihomework.voicedemo;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.aihomework.constants.Constants;
import com.aihomework.tools.HwRecognizerListener;
import com.aihomework.tools.HwSynthesizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechError;

public class EnglishActivity extends BaseSubjectActivity{
    static public void print(String str){
        System.out.println("Aicitel " + str);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initListener();
        welcome();
    }

    @Override
    public void initListener(){
        hwRecognizerListener = new HwRecognizerListener(this,homeworkMgr.getSubject()) {
            @Override
            public void onResult(RecognizerResult results, boolean isLast) {
                String cntVoiceString = getResult(results);
                print(cntVoiceString+" end "+isLast);
                if (isLast) {
                    if(homeworkMgr.isNormalState() && !cntVoiceString.equals("")){
                        //if(homeworkMgr.isReading()&& UsrIntentTools.vagueIntentJudgeCn(cntVoiceString, homeworkMgr.getAllAnswerText()))
                        if(homeworkMgr.isReading())
                        {
                            immJudgeProcess(cntVoiceString);
                        }
                        else if(homeworkMgr.isDictation()&&cntVoiceString.contains("不会")) {
                            //setProb2Answer();
                            //voiceSpeak("好吧，那我们再来抄一遍");
                        }
                        else {
                            //answer = HttpTools.getAnswer(cntVoiceString, Constants.SESSION_ID);
                            //voiceSpeak(answer.getText());
                        }
                    }
                    startListener();
                }
            }
        };
        hwSynthesizerListener = new HwSynthesizerListener(this) {
            @Override
            public void onCompleted(SpeechError speechError) {
                if (speechError == null) {
                    //showTip("播放完成");
                    if(!voiceBuffer.equals("")) {
                        speakFlag = 0;
                        String nextVoice = voiceBuffer;
                        voiceBuffer = "";
                        voiceSpeak(nextVoice);
                    }
                    else {
                        print("voice speak");
                        speakFlag = 0;
                        if(jmpFlag!=0){
                            Intent intent;
                            if(jmpFlag==1) {
                                intent = new Intent(EnglishActivity.this, EntranceActivity.class);
                                homeworkMgr.setSubjectOrder();
                                intent.putExtra(Constants.CONSTANT_SUBJECT, homeworkMgr.getSubject());
                                //intent.putExtra(Constants.CONSTANT_DATA, homeworkMgr.getWrongRadix());
                                SharedPreferences sp = getSharedPreferences(Constants.SHARED_FILE_NAME, Activity.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sp.edit();
                                editor.putString(Constants.SHARED_CN_WRONG_RADIX, homeworkMgr.getWrongRadix());
                                editor.commit();
                                startActivity(intent);
                                System.exit(0);
                            }
                            else {

                            }
                            hwRecognizerListener.startListener();
                        }
                        if(homeworkMgr.isNormalState()) {
                            if(!homeworkMgr.hasAnswer())
                                immJudgeProcess("default");
                            else
                                hwRecognizerListener.startListener();
                        }
                        else if(homeworkMgr.isReviewState()){
                            //计时器开启
                            hwRecognizerListener.startListener();
                        }
                        else if(homeworkMgr.isCorrectState()){
                            print("in Cor judge");
                            hwRecognizerListener.startListener();
                        }
                        else if(homeworkMgr.isAllRightState()){
                            print("in allright judge");
                            returnNavResponse(2);
                        }
                        else if(homeworkMgr.isFinishState()){
                            print("in finish judge");
                            returnNavResponse(2);
                        }
                        else{
                            returnNavResponse(1);
                            print("out of index");
                        }
                    }
                } else {
                    print("here no answer");
                    //if(!homeworkMgr.hasAnswer())
                    //    immJudgeProcess("default");
                }
            }
        };
    }

    @Override
    public void welcome() {
        setQuestionText(homeworkMgr.getNextProbText(), homeworkMgr.getQuestionType());
    }
    @Override
    public void returnNavResponse(int type){
        //TODO return nav;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.exit(0);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


}