package com.iflytek.voicedemo;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.speech.setting.IatSettings;

public class figureDemo extends MainActivity{

    //REMAINING custom widget
    EditText answerText1 = null;
    TextView problemText1 = null;
    EditText answerText2 = null;
    TextView problemText2 = null;
    EditText answerText3 = null;
    TextView problemText3 = null;
    TextView infoText = null;
    Button chatButton = null;
    Button submitButton = null;
    ImageView figureImage = null;

    //shared storage
    private SharedPreferences mSharedPreferences;

    //REMAINING should replaced by interface of DAO
    String[] problems = {"","2+5","3+11","5+24"};
    int[] answers = {-1,7,14,29};
    int order =1;

    //REMAINING xunfei interface

    private SpeechRecognizer mIat;
    private String mEngineType = SpeechConstant.TYPE_CLOUD;

    //REMAINING userMonitor interface

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.figuredemo);
        initLayout();
        mSharedPreferences = getSharedPreferences(IatSettings.PREFER_NAME,
                Activity.MODE_PRIVATE);

        mIat = SpeechRecognizer.createRecognizer(figureDemo.this, mInitListener);
    }
    public boolean judgeAnswer(int order){
        return (Integer.parseInt(answerText1.getText().toString()) == answers[order]);
    }
    /*
    public boolean judgeAnswer(int order) {
        switch (order) {
            case 1:
                return (Integer.parseInt(answerText1.getText().toString()) == 7);
            case 2:
                return (Integer.parseInt(answerText2.getText().toString()) == 15);
            case 3:
                return (Integer.parseInt(answerText3.getText().toString()) == 16);
        }
        return false;
    }*/
    public void initLayout(){
        answerText1 = (EditText)findViewById(R.id.answerText1);
        problemText1 = (TextView)findViewById(R.id.problemTextView1);
        answerText2 = (EditText)findViewById(R.id.answerText2);
        answerText2.setVisibility(View.INVISIBLE);
        problemText2 = (TextView)findViewById(R.id.problemTextView2);
        problemText2.setVisibility(View.INVISIBLE);
        answerText3 = (EditText)findViewById(R.id.answerText3);
        answerText3.setVisibility(View.INVISIBLE);
        problemText3 = (TextView)findViewById(R.id.problemTextView3);
        problemText3.setVisibility(View.INVISIBLE);

        infoText = (TextView)findViewById(R.id.dialogueTextView);

        chatButton = (Button)findViewById(R.id.chatButton);
        submitButton = (Button)findViewById(R.id.submitButton);
        figureImage = (ImageView)findViewById(R.id.figureView);

        //default engine use cloud type
        mEngineType = SpeechConstant.TYPE_CLOUD;

        //REMAINING should replaced by voice interaction
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(order>=4){
                    infoText.setText("全对！");
                }
                else {
                    if (judgeAnswer(order)) {
                        order++;
                        if(order<=3)
                            problemText1.setText(problems[order]);
                    }
                    else{
                        infoText.setText("第"+order+"题错了哦");
                    }
                }
            }
        });
    }
    /**
     * 初始化监听器。
     */
    private InitListener mInitListener = new InitListener() {

        @Override
        public void onInit(int code) {
            Log.d("TAG", "SpeechRecognizer init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
                //showTip("初始化失败，错误码：" + code);
                ;
            }
        }
    };

    /**
     * 听写监听器。
     */
    private RecognizerListener mRecognizerListener = new RecognizerListener() {

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
        }

        @Override
        public void onEndOfSpeech() {
            // 此回调表示：检测到了语音的尾端点，已经进入识别过程，不再接受语音输入
            //showTip("结束说话");
        }

        @Override
        public void onResult(RecognizerResult results, boolean isLast) {
           // Log.d(TAG, results.getResultString());
            //printResult(results);
            //REMAINING send results to AI

            if (isLast) {
                // TODO 最后的结果
            }
        }

        @Override
        public void onVolumeChanged(int volume, byte[] data) {
            //showTip("当前正在说话，音量大小：" + volume);
            //Log.d(TAG, "返回音频数据："+data.length);
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
    };
    public class MyOnClickListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                // 开始听写
                // 如何判断一次听写结束：OnResult isLast=true 或者 onError
                case R.id.iat_recognize:
                    //mResultText.setText(null);// 清空显示内容
                    //mIatResults.clear();
                    // 设置参数
                    setParam();
                    boolean isShowDialog = mSharedPreferences.getBoolean(
                            getString(R.string.pref_key_iat_show), true);
                    if (isShowDialog) {
                        // 显示听写对话框
                        //mIatDialog.setListener(mRecognizerDialogListener);
                        //mIatDialog.show();
                        //showTip(getString(R.string.text_begin));
                    } else {
                        // 不显示听写对话框
                        int  ret = 0;
                        ret = mIat.startListening(mRecognizerListener);
                        if (ret != ErrorCode.SUCCESS) {
                            //showTip("听写失败,错误码：" + ret);
                        } else {
                            //showTip(getString(R.string.text_begin));
                        }
                    }
                    break;
                // 停止听写
                case R.id.iat_stop:
                    mIat.stopListening();
                    //showTip("停止听写");
                    break;
                // 取消听写
                case R.id.iat_cancel:
                    mIat.cancel();
                    //showTip("取消听写");
                    break;
            }
        }
    }
    /**
     * 参数设置
     *
     * @param
     * @return
     */
    public void setParam() {
        // 清空参数
        mIat.setParameter(SpeechConstant.PARAMS, null);

        // 设置听写引擎
        mIat.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);
        // 设置返回结果格式
        mIat.setParameter(SpeechConstant.RESULT_TYPE, "json");

        String lag = mSharedPreferences.getString("iat_language_preference",
                "mandarin");
        if (lag.equals("en_us")) {
            // 设置语言
            mIat.setParameter(SpeechConstant.LANGUAGE, "en_us");
        } else {
            // 设置语言
            mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
            // 设置语言区域
            mIat.setParameter(SpeechConstant.ACCENT, lag);
        }

        // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
        mIat.setParameter(SpeechConstant.VAD_BOS, mSharedPreferences.getString("iat_vadbos_preference", "4000"));

        // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
        mIat.setParameter(SpeechConstant.VAD_EOS, mSharedPreferences.getString("iat_vadeos_preference", "1000"));

        // 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
        mIat.setParameter(SpeechConstant.ASR_PTT, mSharedPreferences.getString("iat_punc_preference", "1"));

        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
        mIat.setParameter(SpeechConstant.AUDIO_FORMAT,"wav");
        mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH, Environment.getExternalStorageDirectory()+"/msc/iat.wav");

        // 设置听写结果是否结果动态修正，为“1”则在听写过程中动态递增地返回结果，否则只在听写结束之后返回最终结果
        // 注：该参数暂时只对在线听写有效
        mIat.setParameter(SpeechConstant.ASR_DWA, mSharedPreferences.getString("iat_dwa_preference", "0"));
    }

}
