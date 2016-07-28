package com.aihomework.voicedemo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aihomework.TuLingAPI.bean.AnswerBean;
import com.aihomework.TuLingAPI.tools.HttpTools;
import com.aihomework.constants.Constants;
import com.aihomework.speech.util.JsonParser;
import com.aihomework.userAction.UsrIntentTools;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by bluemaple on 2016/1/9.
 */
public class EntranceActivity extends Activity {

    private boolean isNetAvailble=false;

    private SharedPreferences mSharedPreferences;
    //图灵机器人API
    AnswerBean answer= null;

    //意图通信工具
    private Bundle bundle;
    //0 means no jmp, 1 means math
    private int jmpFlag = 0;
    //当前状态 0 表示主入口; 1 表示作业入口
    private int activityState = 0;

    //ListView homeworkList = null;
    //ArrayList<HashMap<String, Object>>listItem;
    //ArrayList<TextView>subjectList;
    ArrayList<TextView>activityList;
    ArrayList<ImageView>subjectImgList;
    AnimationDrawable anim = null;
    ImageView figureImage = null;
    TextView mResultText = null;
    TextView userText = null;
    Button handWriteButton = null;
    Button squareButton = null;
    Button readDemoButton = null;
    Button getHwButton = null;
    View activityLayout = null;
    View homeworkLayout = null;

    String judgeString = "";
    String TAG = "DEMO";
    private Toast mToast;
    //作业流程相关
    private int[] subjectOrders={0,0,0};
    private String[] homeworkTags = {"语文","数学","英语"};

    /**
     * 设备发声标记 为1时监听暂停
     *     封装入voiceSpeak方法
     */
    private int SpeakingFlag = 0;

    // 语音合成对象
    private SpeechSynthesizer mTts;
    // 默认发音人
    private String voicer = "xiaoyan";
    // 情感
    private String emot= "";
    // 发音缓存
    private String voiceBuffer="";

    // 语音听写对象
    private SpeechRecognizer mIat;
    // 引擎类型
    private String mEngineType = SpeechConstant.TYPE_CLOUD;
    // 用HashMap存储听写结果
    private HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();

    //Timer module
    //-1 means no response, 0 means normal ,-2 means break request
    static int responseFLAG = -1;
    Timer responseCheckTimer = null;

    class resHandler extends Handler {
        @Override
        public void handleMessage(Message msg){
            switch (msg.what) {
                case 1:
                    if(responseFLAG!=0)
                        if(responseFLAG==-1) voiceSpeak("小朋友你还在么？");
                        else if(responseFLAG==-2) responseFLAG++;
            }
        }
    }

    //WARNING 耦合
    final Handler timerHandler = new resHandler();

    TimerTask responseChecker = new TimerTask() {
        @Override
        public void run() {
            Message message = new Message();
            message.what = 1;
            timerHandler.sendMessage(message);
        }
    };

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //remove title bar
        setContentView(R.layout.entrance_layout);
        // 初始化识别无UI识别对象
        // 使用SpeechRecognizer对象，可根据回调消息自定义界面；
        mIat = SpeechRecognizer.createRecognizer(EntranceActivity.this, mInitListener);
        mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        mResultText = (TextView)findViewById(R.id.entranceText);
        handWriteButton = (Button)findViewById(R.id.handWriteButton);
        squareButton = (Button)findViewById(R.id.mathsquareButton);
        squareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EntranceActivity.this, MathSquare.class));
                System.exit(0);
            }
        });
        getHwButton = (Button)findViewById(R.id.getButton);
        getHwButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EntranceActivity.this, SumActivity.class));
                System.exit(0);
            }
        });
        figureImage = (ImageView)findViewById(R.id.figureView);
        figureImage.setBackgroundResource(R.drawable.frame);
        anim = (AnimationDrawable) figureImage.getBackground();
        /*
        subjectList = new ArrayList<TextView>();
        subjectList.add((TextView)findViewById(R.id.textViewCN));
        subjectList.add((TextView)findViewById(R.id.textViewMA));
        subjectList.add((TextView)findViewById(R.id.textViewEN));*/
        subjectImgList = new ArrayList<ImageView>();
        subjectImgList.add((ImageView)findViewById(R.id.imageViewCN));
        subjectImgList.add((ImageView)findViewById(R.id.imageViewMA));
        subjectImgList.add((ImageView)findViewById(R.id.imageViewEN));

        homeworkLayout = findViewById(R.id.homeworkLayout);

        activityList = new ArrayList<TextView>();
        activityList.add((TextView)findViewById(R.id.homeworkLabel));
        activityList.add((TextView)findViewById(R.id.gameLabel));
        activityList.add((TextView)findViewById(R.id.socialLabel));
        activityLayout = findViewById(R.id.activityLayout);
        LabelOnClickInit();

        userText = (TextView)findViewById(R.id.userTextView);

        mTts = SpeechSynthesizer.createSynthesizer(EntranceActivity.this, mTtsInitListener);
        mIat.setParameter(SpeechConstant.ASR_PTT, "0");
        mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        //设置前端点为10S
        mIat.setParameter(SpeechConstant.VAD_BOS, "10000");
        bundle = this.getIntent().getExtras();
        if(bundle!=null && !bundle.isEmpty()&&bundle.containsKey(Constants.CONSTANT_SUBJECT))
            ;//subjectOrders = bundle.getIntArray(Constants.CONSTANT_DATA);
        else{
            getSharedPreferences(Constants.SHARED_FILE_NAME, Activity.MODE_PRIVATE).edit().clear();
        }
        responseCheckTimer = new Timer(true);
        responseCheckTimer.schedule(responseChecker,60000, 60000);

        if(isNetworkAvailable(this.getBaseContext()))
            isNetAvailble= true;

        Constants.tulingUsed = true;

        /* in init block
        if(bundle==null || bundle.isEmpty())
            askAction(0);
        else
            askAction(1);
            */

    }
    /**
     * 设置label点击初始化
     */
    public void LabelOnClickInit(){
        activityList.get(0).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    enterHwState();
                }
            });
        activityList.get(1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EntranceActivity.this, RobotGameActivity.class));
                System.exit(0);
            }
        });
    }
    /**
     * 进入作业入口
     */
    public void enterHwState(){
        voiceSpeak("今天有四大题语文作业，两大题数学作业，两大题英语作业，你想先做哪一科？");
        activityState = 1;
        anim.stop();anim.start();
        setListVisible();
        activityLayout.setVisibility(View.INVISIBLE);
    }
    /**
     * 触发关键词/点击作业label后该方法使视图可见
     */
    public void setListVisible(){
        homeworkLayout.setVisibility(View.VISIBLE);
        for(int i=0;i<3;i++){
            final int setI = i;
            subjectImgList.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    jmpJudge(setI);
                }
            });
        }
        //setListItemLength();
    }
    /**
     * ViewList动态设置长度
     */
    /*
    public void setListItemLength(){
        for(int i=0;i<3;i++) {
            ViewGroup.LayoutParams frame = subjectBarImgList.get(i).getLayoutParams();
            frame.width = Constants.questionCounts[i]*20;
            subjectBarImgList.get(i).setLayoutParams(frame);
        }
    }*/
    /**
     * 跳转判断
     */
    public void jmpJudge(int subjectType){
        if(isNetAvailble) {
            if (subjectOrders[subjectType] >= Constants.questionCounts[subjectType]) {
                voiceSpeak("这科作业做完啦，我们换一个吧");
                anim.stop();anim.start();
            }
            else {
                Intent intent;
                if (subjectType == Constants.CONSTANT_MATH)
                    intent = new Intent(EntranceActivity.this, ChineseActivity.class);
                else if(subjectType == Constants.CONSTANT_CHINESE)
                    intent = new Intent(EntranceActivity.this, ChineseActivity.class);
                else
                    intent = new Intent(EntranceActivity.this, ChineseActivity.class);
                intent.putExtra(Constants.CONSTANT_SUBJECT, subjectType);
                //intent.putExtra(Constants.CONSTANT_DATA, subjectOrders);
                startActivity(intent);
                System.exit(0);
            }
        }
        else {
            showTip("请查看网络连接");
            if(isNetworkAvailable(this.getBaseContext()))
                isNetAvailble= true;
        }
    }
    /**
     * 初始方法
     * argv int type 0 means 首次打开 1 means 其他
     */
    public void askAction(int type){
        String welcomeText = "嗨你好呀，又见面啦";
        String text = (type==0)?welcomeText:"下面学什么？";
        // 设置参数
        voiceSpeak(text);
        anim.stop();anim.start();
    }
    /**
     * 合成接口
     */
    public void voiceSpeak(String text){
        /*
        SpeakingFlag = 1;
        mTts.startSpeaking(text, mTtsListener);
        */
        if(text.length()==0)
            return;
        if(SpeakingFlag == 1)
            voiceBuffer = text;
        else {
            SpeakingFlag = 1;
            mTts.startSpeaking(text, mTtsListener);
        }
    }

    public void startListening(){
        int ret = 0;
        if(SpeakingFlag==0) {
            ret = mIat.startListening(mRecognizerListener);
            if (ret != ErrorCode.SUCCESS) {
                showTip("听写失败,错误码：" + ret);
            } else {
                showTip(getString(R.string.text_begin));
            }
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
            }
        }
    };


    public Handler chatHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            voiceSpeak(answer.getText());
            responseFLAG = 0;
        }
    };

    public Runnable chatThread = new Runnable() {
        @Override
        public void run() {
            answer = HttpTools.getAnswer(judgeString, Constants.SESSION_ID);
            Message message = new Message();
            Bundle bundle = new Bundle();
            message.setData(bundle);
            EntranceActivity.this.chatHandler.sendMessage(message);

        }
    };


    /**
     * 听写监听器。
     */
    private RecognizerListener mRecognizerListener = new RecognizerListener() {

        @Override
        public void onBeginOfSpeech() {
            // 此回调表示：sdk内部录音机已经准备好了，用户可以开始语音输入
            showTip("开始说话");
        }

        @Override
        public void onError(SpeechError error) {
            // Tips：
            // 错误码：10118(您没有说话)，可能是录音机权限被禁，需要提示用户打开应用的录音权限。
            // 如果使用本地功能（语记）需要提示用户开启语记的录音权限。
            Log.d("TEST", "invoke");
            showTip(error.getPlainDescription(true));
            startListening();
        }

        @Override
        public void onEndOfSpeech() {
            // 此回调表示：检测到了语音的尾端点，已经进入识别过程，不再接受语音输入
            showTip("结束说话");
        }

        @Override
        public void onResult(RecognizerResult results, boolean isLast) {
            Log.d(TAG, results.getResultString());
            //parseResult();
            printResult(results);
            //REMAINING reduce to function
            if (isLast) {
                EnglishDemo.print(judgeString);
                if(activityState==0&&judgeString.matches(".*什么.*作业.*")) {
                    enterHwState();
                }
                else if(activityState==1) {
                    if (judgeString.matches(".*数学.*")) {
                        jmpJudge(Constants.CONSTANT_MATH);
                    } else if (judgeString.matches(".*语文.*")) {
                        jmpJudge(Constants.CONSTANT_CHINESE);
                    } else if (judgeString.matches(".*英语.*")) {
                        jmpJudge(Constants.CONSTANT_ENGLISH);
                    }
                    else if(responseFLAG == 0) {
                        if (UsrIntentTools.breakReqJudge(judgeString)) {
                            voiceSpeak("好的，那我一会再来哦");
                            anim.stop();anim.start();
                            responseFLAG = -2;
                        } else if (UsrIntentTools.normalResJudge(judgeString)) {
                            jmpFlag = 1;
                            voiceSpeak("那我们先开始做数学吧，不然明天要被老师批评的！");
                            anim.stop();anim.start();
                        }
                    }
                }
                //hard code session ID
                //TODO change to new thread
                new Thread(chatThread).start();
                //if(answer.getStatusCode().equals("400"))
                //    jmpFlag = 1;
            }
        }


        @Override
        public void onVolumeChanged(int volume, byte[] data) {
            showTip("当前正在说话，音量大小：" + volume);
            Log.d(TAG, "返回音频数据："+data.length);
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
     * 初始化监听。
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
                if(bundle==null || bundle.isEmpty() || !bundle.containsKey(Constants.CONSTANT_SUBJECT)) {
                    askAction(0);
                }
                else {
                    enterHwState();
                    askAction(1);
                }
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
                showTip("播放完成");
            } else {
                if(jmpFlag==1) {
                    //TODO go math

                    System.exit(0);
                }
                showTip(error.getPlainDescription(true));
            }
            //置设备发音标记为0
            SpeakingFlag = 0;
            //Log.d("TEST","invoke");
            startListening();
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
        userText.setText(resultBuffer.toString());
        judgeString = resultBuffer.toString();
        resultBuffer = null;
    }

    private void showTip(final String str) {
        mToast.setText(str);
        mToast.show();
    }
    /**
     * 判断网络连接
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
        } else {
            NetworkInfo[] info = cm.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 返回键退出
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTts.destroy();
        mIat.cancel();
        mIat.destroy();
        System.exit(0);
    }
}