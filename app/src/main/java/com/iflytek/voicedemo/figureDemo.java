package com.iflytek.voicedemo;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;
import com.iflytek.speech.setting.IatSettings;
import com.iflytek.speech.util.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class figureDemo extends MainActivity{
    //showTip
    private String TAG = "test";
    private Toast mToast;
    private void showTip(final String str) {
        mToast.setText(str);
        mToast.show();
    }
    //current voice string
    String cntVoiceString = "";

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
    // 语音听写对象
    private SpeechRecognizer mIat;
    // 引擎类型
    private String mEngineType = SpeechConstant.TYPE_CLOUD;
    // 用HashMap存储听写结果
    private HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();


    // 语音合成对象
    private SpeechSynthesizer mTts;
    // 默认发音人
    private String voicer = "xiaoyan";
    // 情感
    private String emot= "";


    //REMAINING userMonitor interface

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.figuredemo);
        initLayout();
        mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        mSharedPreferences = getSharedPreferences(IatSettings.PREFER_NAME,
                Activity.MODE_PRIVATE);

        mTts = SpeechSynthesizer.createSynthesizer(figureDemo.this, mTtsInitListener);
        mIat = SpeechRecognizer.createRecognizer(figureDemo.this, mInitListener);
        startListener();
    }
    public boolean judgeAnswer(int order){
        if(answerText1.getText()==null || answerText1.getText().toString().equals(""))
            return false;
        return (Integer.parseInt(answerText1.getText().toString()) == answers[order]);
    }

    public void initLayout(){
        answerText1 = (EditText)findViewById(R.id.answerText1);
        problemText1 = (TextView)findViewById(R.id.problemTextView1);

        infoText = (TextView)findViewById(R.id.dialogueTextView);

        chatButton = (Button)findViewById(R.id.chatButton);
        submitButton = (Button)findViewById(R.id.submitButton);
        figureImage = (ImageView)findViewById(R.id.figureView);

        //default engine use cloud type
        mEngineType = SpeechConstant.TYPE_CLOUD;

        //REMAINING should replaced by voice interaction
        //          should get questions from db
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (order >= 4) {
                    infoText.setText("全对！");
                } else {
                    if (judgeAnswer(order)) {
                        infoText.setText("第" + order + "题对啦");
                        questionCorResponse(true);
                        order++;
                        if (order <= 3)
                            problemText1.setText(problems[order]);
                    } else {
                        infoText.setText("第" + order + "题错了哦");
                        questionCorResponse(false);
                    }
                }
            }
        });
    }
    /**
     * 返回nav应答反馈
     */
    public void returnNavResponse(){
        String text = "那我们回到开始吧";
        // 设置参数
        setParam();
        int code = mTts.startSpeaking(text, mTtsListener);
        startActivity(new Intent(figureDemo.this, entranceDemo.class));
    }
    /**
     * 正误判断反馈
     */
    public void questionCorResponse(boolean isCorrect){
        String text = (isCorrect)?"好棒答对了！":"不太对哦，再想想看？";
        int code = mTts.startSpeaking(text, mTtsListener);
    }
    /**
     * 监听入口
     */
    public void startListener(){
        int ret = mIat.startListening(mRecognizerListener);
        if (ret != ErrorCode.SUCCESS) {
            showTip("听写失败,错误码：" + ret);
        } else {
            showTip(getString(R.string.text_begin));
        }
    }


    /**
     * 初始化监听器。
     */
    private InitListener mInitListener = new InitListener() {

        @Override
        public void onInit(int code) {
            Log.d("TAG", "SpeechRecognizer init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
                showTip("初始化失败，错误码：" + code);
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
            showTip(error.getPlainDescription(true));
        }

        @Override
        public void onEndOfSpeech() {
            // 此回调表示：检测到了语音的尾端点，已经进入识别过程，不再接受语音输入
            //showTip("结束说话");
        }

        @Override
        public void onResult(RecognizerResult results, boolean isLast) {
            printResult(results);
            //REMAINING send results to AI

            if (isLast) {
                //unicode code = 不想做
                String reg=".*\\u4e0d\\u60f3\\u505a.*";

                if(cntVoiceString.matches(reg))
                    returnNavResponse();
                else
                    startListener();
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

    private void printResult(RecognizerResult results) {
        String text = JsonParser.parseIatResult(results.getResultString());

        String sn = null;
        // 读取json结果中的sn字段
        try {
            JSONObject resultJson = new JSONObject(results.getResultString());
            sn = resultJson.optString("sn");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mIatResults.put(sn, text);

        StringBuffer resultBuffer = new StringBuffer();
        for (String key : mIatResults.keySet()) {
            resultBuffer.append(mIatResults.get(key));
        }
        //mResultText.setText(resultBuffer.toString());
        cntVoiceString = resultBuffer.toString();
        resultBuffer = null;
    }


    /**
     * 初始化监听。语音合成
     */
    private InitListener mTtsInitListener = new InitListener() {
        @Override
        public void onInit(int code) {
            Log.d(TAG, "InitListener init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
                showTip("初始化失败,错误码："+code);
            } else {
                // 初始化成功，之后可以调用startSpeaking方法
                // 注：有的开发者在onCreate方法中创建完合成对象之后马上就调用startSpeaking进行合成，
                // 正确的做法是将onCreate中的startSpeaking调用移至这里
            }
        }
    };

    /**
     * 合成回调监听。
     */
    private SynthesizerListener mTtsListener = new SynthesizerListener() {

        @Override
        public void onSpeakBegin() {
            showTip("开始播放");
        }

        @Override
        public void onSpeakPaused() {
            showTip("暂停播放");
        }

        @Override
        public void onSpeakResumed() {
            showTip("继续播放");
        }

        @Override
        public void onBufferProgress(int percent, int beginPos, int endPos,
                                     String info) {
            // 合成进度
            // mPercentForBuffering = percent;
            // showTip(String.format(getString(R.string.tts_toast_format),
            //         mPercentForBuffering, mPercentForPlaying));
        }

        @Override
        public void onSpeakProgress(int percent, int beginPos, int endPos) {
            // 播放进度
            //mPercentForPlaying = percent;
            //showTip(String.format(getString(R.string.tts_toast_format),
            //        mPercentForBuffering, mPercentForPlaying));
        }

        @Override
        public void onCompleted(SpeechError error) {
            if (error == null) {
                //showTip("播放完成");

            } else if (error != null) {
                showTip(error.getPlainDescription(true));
            }
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

    /**
     * 返回键退出
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.exit(0);
    }
}
