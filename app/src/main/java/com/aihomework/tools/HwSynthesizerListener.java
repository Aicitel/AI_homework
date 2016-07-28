package com.aihomework.tools;

import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;

public abstract class HwSynthesizerListener implements SynthesizerListener {

    protected String voiceBuffer = "";
    protected int speakFlag = 0;
    private HwRecognizerListener hwRecognizerListener = null;
    private SpeechSynthesizer mTts;
    private Toast mToast;
    public HwSynthesizerListener(Context context){
        //this.processController = processController;
        mToast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
        mTts = SpeechSynthesizer.createSynthesizer(context, new InitListener() {
            @Override
            public void onInit(int code) {
                //Log.d(TAG, "InitListener init() code = " + code);
                if (code != ErrorCode.SUCCESS) showTip("初始化失败,错误码："+code);
                else{
                    //TODO welcome
                    //welcome();
                }
            }
        });
    }
    public void setHwRecognizerListener(HwRecognizerListener recognizer){
        this.hwRecognizerListener = recognizer;
    }

    /**
     * 合成入口
     */
    public void voiceSpeak(String text){
        if(text.length()==0)
            return;
        if(speakFlag == 1)
            voiceBuffer = text;
        else {
            setSpeakFlag();
            mTts.startSpeaking(text, this);
        }
    }

    protected void resetSpeakFlag(){
        this.speakFlag = 0;
        hwRecognizerListener.resetSpeakFlag();
    }
    protected void setSpeakFlag(){
        this.speakFlag = 1;
        hwRecognizerListener.setSpeakFlag();
    }

    protected void showTip(final String str) {
        mToast.setText(str);
        mToast.show();
    }

    @Override
    public void onSpeakBegin() {
        //showTip("开始播放");
    }

    @Override
    public void onSpeakPaused() {
        //showTip("暂停播放");
    }

    @Override
    public void onSpeakResumed() {
        //showTip("继续播放");
    }

    @Override
    public void onBufferProgress(int percent, int beginPos, int endPos,
                                 String info) {
    }

    @Override
    public void onSpeakProgress(int percent, int beginPos, int endPos) {
        //responseFLAG = 0;
    }

    @Override
    public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
        // 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
        // 若使用本地能力，会话id为null
        //	if (SpeechEvent.EVENT_SESSION_ID == eventType) {
        //		String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
        //		Log.d(TAG, "session id =" + sid);
        //	}
    }

}
