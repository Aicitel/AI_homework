package com.aihomework.tools;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.aihomework.constants.Constants;
import com.aihomework.speech.util.JsonParser;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedHashMap;

public abstract class HwRecognizerListener implements RecognizerListener {

    private Toast mToast;
    private SpeechRecognizer mIat;
    protected int responseFLAG = 1;
    protected int speakFlag = 0;

    private HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();
    public HwRecognizerListener(Context context,int type){
        mToast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
        mIat = SpeechRecognizer.createRecognizer(context,new InitListener() {
            @Override
            public void onInit(int code) {
                Log.d("TAG", "SpeechRecognizer init() code = " + code);
                if (code != ErrorCode.SUCCESS) {
                    showTip("初始化失败，错误码：" + code);
                }
            }
        });

        //设置返回结果无标点
        mIat.setParameter(SpeechConstant.ASR_PTT, "0");
        //设置前端点为10S
        mIat.setParameter(SpeechConstant.VAD_BOS, "10000");
        if(type== Constants.CONSTANT_CHINESE || type== Constants.CONSTANT_MATH) {
            //设置默认识别语言为中文普通话
            mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
            mIat.setParameter(SpeechConstant.ACCENT, "mandarin ");
        }
        else if(type== Constants.CONSTANT_ENGLISH)
            //设置默认识别语言为英语
            mIat.setParameter(SpeechConstant.LANGUAGE, "en_us");
    }

    /**
     * 监听入口
     */
    public void startListener(){
        if(speakFlag == 0) {
            int ret = mIat.startListening(this);
            if (ret != ErrorCode.SUCCESS) {
                showTip("听写失败,错误码：" + ret);
            } else {
                showTip("请开始说话");
                //TODO
            }
        }
    }
    public void setSpeakFlag(){
        this.speakFlag = 1;
    }
    public void resetSpeakFlag(){
        this.speakFlag = 0;
    }

    protected String getResult(RecognizerResult results) {
        String text = JsonParser.parseIatResult(results.getResultString());

        String sn = null;
        // 读取json结果中的sn字段
        try {
            JSONObject resultJson = new JSONObject(results.getResultString());
            sn = resultJson.optString("sn");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //TODO set imatResults
        mIatResults.put(sn, text);
        StringBuilder resultBuffer = new StringBuilder();
        for (String key : mIatResults.keySet()) {
            resultBuffer.append(mIatResults.get(key));
        }
        return resultBuffer.toString();
        //return  JsonParser.parseIatResult(results.getResultString());
    }

    protected void showTip(final String str) {
        mToast.setText(str);
        mToast.show();
    }

    @Override
    public void onBeginOfSpeech() {
        // 此回调表示：sdk内部录音机已经准备好了，用户可以开始语音输入
        //showTip("开始说话");
    }

    @Override
    public void onError(SpeechError error) {
        // Tips：
        // 错误码：10118(您没有说话)，可能是录音机权限被禁，需要提示用户打开应用的录音权限。
        // 如果使用本地功能（语记）需要提示用户开启语记的录音权限。
        //showTip(error.getPlainDescription(true));
        startListener();
    }

    @Override
    public void onEndOfSpeech() {
        // 此回调表示：检测到了语音的尾端点，已经进入识别过程，不再接受语音输入
        //showTip("结束说话");

        //此处检测到语音输入 标记无应答flag为0
        responseFLAG = 0;
    }

    @Override
    public void onVolumeChanged(int volume, byte[] data) {
    }

    @Override
    public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
    }

    public void onDestroy(){
        mIat.cancel();
        mIat.destroy();
    }
}
