package com.aihomework.voicedemo;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aihomework.TuLingAPI.bean.AnswerBean;
import com.aihomework.TuLingAPI.tools.HttpTools;
import com.aihomework.constants.Constants;
import com.aihomework.homework.HomeworkMgr;
import com.aihomework.speech.util.JsonParser;
import com.aihomework.tools.EnglishHandwrite;
import com.aihomework.userAction.UsrIntentTools;
import com.google.gson.Gson;
import com.hanvon.HWCloudManager;
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

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class EnglishDemo extends Activity{
    public static void print(String str){
        System.out.println("Aicitel "+str);
    }
    boolean allRightFlag = true;
    /**----------------------------------汉王云接口变量---------------------------------------*/

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d("TEST", "x " + event.getRawX() + " y" + event.getRawY());
        if (event.getY() >= top && event.getY() <= bottom) {
            if (englishHandwrite.isAllFull()) {
                englishHandwrite.resetTemplate();
            }
            start = true;
            init = now;
            now = System.currentTimeMillis();
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (now - init >= 100 && now - init <= 1000) {
                        sbuilder.append("-1,").append("0,");
                    }
                    englishHandwrite.movePathTo(event.getX() - left, event.getY() - top);
                    sbuilder.append((int) (event.getX())).append(",").append((int) (event.getY() - top)).append(",");
                    break;
                case MotionEvent.ACTION_MOVE:
                    englishHandwrite.pathLineTo(event.getX() - left, event.getY() - top);
                    sbuilder.append((int) (event.getX())).append(",").append((int) (event.getY() - top)).append(",");
                    break;

                default:
                    break;
            }

        }
        return true;
    }

    private HWCloudManager hwCloudManagerHandSingle;

    private SurfaceView surface;
    private SurfaceView wordSurface1;

    private TextView lineResult1;

    private EnglishHandwrite englishHandwrite = null;
    private boolean isDrawing = false;

    //warning hard code
    private int left;
    private int right;
    private int top;
    private int bottom;
    private long now, init;
    private boolean isRunning = true, start = false;

    private StringBuilder sbuilder = new StringBuilder();

    /**--------------------------------------讯飞语音变量-------------------------------------*/
    //test use
    public static String testString = "test: ";

    //图灵机器人API
    AnswerBean answer= null;
    public static boolean tulingUsed = false;

    //意图通信工具
    private Bundle bundle= null;
    int jmpFlag = 0;

    //question argvs
    int incorrectTime = 0;
    int continuousCorTime = 0;
    //boolean reviewFlag = false;

    //Timer module
    static int responseFLAG = 1;  //showTip
    private String TAG = "test";
    private Toast mToast;
    private void showTip(final String str) {
        mToast.setText(str);
        mToast.show();
    }
    //current voice string
    String cntVoiceString = "";

    /**
     * 设备发声标记 为1时监听暂停
     *     待封装
     */
    private int SpeakingFlag = 0;

    //REMAINING xunfei interface
    // 语音听写对象
    private SpeechRecognizer mIat;
    // 引擎类型
    private String mEngineType = SpeechConstant.TYPE_CLOUD;
    // 用HashMap存储听写结果
    private HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();


    // 语音合成对象
    private SpeechSynthesizer mTts;
    // buffer
    private String voiceBuffer="";

    private boolean isReading = false;
    /**------------------------------------UI与作业控制变量-----------------------------------*/

    /**
     * rightMark计时线程
     *      主线程sleep后setVisibility无效，原因未知
     *      因此使用复杂方法进行解决
     */

    public class RightMarkHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            rightMarkImage.setVisibility(View.INVISIBLE);
        }
    }
    RightMarkHandler rightMarkHandler = new RightMarkHandler();
    Runnable RightMarkThread = new Runnable() {
        @Override
        public void run() {
            try {
                Thread.sleep(200);
                Message message = new Message();
                rightMarkHandler.sendMessage(message);
            }
            catch(Exception e){
                print("sleep error");
            }

        }
    };

    AnimationDrawable anim = null;
    TextView problemText = null;
    Button submitButton = null;
    EditText debugEditText = null;
    TextView progressText = null;
    Button lastButton = null;
    Button nextButton = null;
    Button dataButton = null;
    Button delButton = null;
    ImageView figureImage = null;
    ImageView tulingImage = null;
    ImageView rightMarkImage = null;
    View surfaceLayout = null;
    TextView readText = null;
    TextView readTempText = null;
    View readLayout = null;
    View stemLayout = null;
    TextView stemTextView = null;
    ImageButton dictationButton = null;
    ArrayList<String>dictationBuffer = new ArrayList<String>();
    boolean initFinish = false;

    HomeworkMgr homeworkMgr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //remove title bar
        setContentView(R.layout.english_demo);

        mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        /**
         * your_android_key 是您在开发者中心申请的android_key 并 申请了云手写公式识别的服务
         * 开发者中心：http://developer.hanvon.com/
         *
         */
        hwCloudManagerHandSingle = new HWCloudManager(this, "8379a4b4-9ee1-41ce-a0ff-052c4cfc3430");

        problemText = (TextView)findViewById(R.id.problemTextView);
        figureImage = (ImageView) findViewById(R.id.figureView);
        figureImage.setBackgroundResource(R.drawable.frame);
        anim = (AnimationDrawable) figureImage.getBackground();
        rightMarkImage = (ImageView) findViewById(R.id.rightMarkImageView);

        surface = (SurfaceView) this.findViewById(R.id.surface);
        wordSurface1 = (SurfaceView) this.findViewById(R.id.wordSurface1);
        lineResult1 = (TextView) findViewById(R.id.line_result1);
        submitButton = (Button)findViewById(R.id.submitButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                immJudgeProcess(debugEditText.getText().toString());
            }
        });
        debugEditText = (EditText)findViewById(R.id.debugEditText);
        progressText= (TextView)findViewById(R.id.progressText);

        englishHandwrite = new EnglishHandwrite(new Paint(),surface.getHolder(),wordSurface1.getHolder(),new Path(), new Path(),new SurfaceCallback());

        surface.setZOrderOnTop(true);
        wordSurface1.setZOrderOnTop(true);
        dictationButton= (ImageButton)findViewById(R.id.dictationButton);
        dictationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int size = dictationBuffer.size();
                System.out.println("size " + size + " " + dictationBuffer.size());
                if (size >= 1) {
                    immJudgeProcess(dictationBuffer.get(size - 1));
                    dictationBuffer.clear();
                }
            }
        });

        (findViewById(R.id.rightImageView)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (allRightFlag) {
                    allRightFlag = false;
                    ((ImageView) v).setImageResource(R.drawable.closeright);
                } else {
                    allRightFlag = true;
                    ((ImageView) v).setImageResource(R.drawable.right);
                }
            }
        });

        surfaceLayout = findViewById(R.id.surfaceLayout);
        readText = (TextView)findViewById(R.id.readText);
        readTempText = (TextView)findViewById(R.id.readTempText);
        readLayout = findViewById(R.id.readLayout);

        stemLayout = findViewById(R.id.stemLayout);
        stemTextView = (TextView)findViewById(R.id.stemTextView);

        int[] location = new int[2];
        surface.getLocationOnScreen(location);

        /**----------------get homework from local file,remain to change to SDcard--------------*/
        Gson gson = new Gson();
        try {
            StringBuilder textString = new StringBuilder();
            //WARNING hard code
            char[] data = new char[4096];
            InputStreamReader inputStreamReader =
                    new InputStreamReader(getResources().getAssets().open("homework/enHomework.txt"));
            for(int n;(n=inputStreamReader.read(data))!=-1;)
                textString.append(new String(data,0,n));
            homeworkMgr = gson.fromJson(textString.toString(),HomeworkMgr.class);
            /**-----------bad construct, use to store cnt finished question count------------*/
            //homeworkMgr.initData(this.getIntent().getExtras().getIntArray(Constants.CONSTANT_DATA));
            homeworkMgr.initData(new int[]{0,0,0});
            initFinish = true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        mTts = SpeechSynthesizer.createSynthesizer(EnglishDemo.this, mTtsInitListener);
        mIat = SpeechRecognizer.createRecognizer(EnglishDemo.this, mInitListener);
        //设置返回结果无标点
        mIat.setParameter(SpeechConstant.ASR_PTT, "0");
        //设置前端点为10S
        mIat.setParameter(SpeechConstant.VAD_BOS, "10000");
        //设置默认识别语言为英语
        mIat.setParameter(SpeechConstant.LANGUAGE, "en_us");
        //setParam();
        //welcome方法置入初始化回调中
        //规约入welcome方法 其中置homeworkState为0
        //startListener();

    }
    class SurfaceCallback implements SurfaceHolder.Callback {

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width,
                                   int height) {
            // TODO Auto-generated method stub

        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            int[] location = new int[2];
            surface.getLocationInWindow(location);
            top = location[1];
            left = location[0];
            bottom = top + surface.getHeight();
            right = left + surface.getWidth();
            new Thread(wlineThread).start();
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            System.out.println("-----------surface Destroyed-----------");
            isRunning = false;

        }
    }

    Runnable wlineThread = new Runnable() {
        @Override
        public void run() {
            while (isRunning) {
                if(isDrawing) {
                    englishHandwrite.drawView();
                    if (start) {
                        long temp = System.currentTimeMillis() - now;
                        if (temp > 1000) {
                            sbuilder.append("-1").append(",").append("0");

                            String content = hwCloudManagerHandSingle.handLineLanguage("en", sbuilder.toString());

                            Message message = new Message();
                            Bundle bundle = new Bundle();
                            bundle.putString("content", content);
                            message.setData(bundle);
                            EnglishDemo.this.writeLineHandler.sendMessage(message);

                            start = false;
                            englishHandwrite.clearCanvas();
                            sbuilder = new StringBuilder();
                        }
                    }
                }
                try {
                    Thread.sleep(80);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    };


    Handler writeLineHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle bundle = msg.getData();
            String content = bundle.getString("content");
            showResult(content);
        }
    };

    private void showResult(String content) {
        JSONObject obj = null;
        try {
            System.out.println(content);
            obj = new JSONObject(content);
            if (obj.length()!=0) {
                if ("0".equals(obj.getString("code"))) {
                    String result = obj.getString("result");
                    if (null == result) {
                        Toast.makeText(getApplication(), "请重新输入", Toast.LENGTH_SHORT).show();
                    } else {
                        String[] words = result.split(",");
                        int len = words.length;
                        char[] wordsChar = new char[len];
                        int i = 0;
                        for (String word : words) {
                            if ("0".equals(word)) {
                                wordsChar[i++] = ' ';
                            } else {
                                wordsChar[i++] = (char) Integer.parseInt(word);
                            }
                        }
                        if(allRightFlag)
                            sendResToHwProcess("input");
                        else
                            sendResToHwProcess(wordsChar[0] + "");
                        //immJudgeProcess();
                        //if(res) drawTemplet();
                        lineResult1.setText(String.valueOf(wordsChar[0]));
                    }
                } else {
                    sendResToHwProcess("input");
                    Toast.makeText(getApplication(), obj.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**---------------------------作业流程方法-----------------------*/

    /**
     * 回答判断过程函数
     * argv int type 0 means 手写输入 1 means 语音输入
     *  即时反馈正误
     */
    public void immJudgeProcess(String answer){
        print("imm "+answer+" ");
        boolean result;
        String probText;
        result = homeworkMgr.voiceJudgeAnswer(answer);
        print(" result is " + result);
        if(isDrawing && homeworkMgr.isCopy()) {
            if (result && homeworkMgr.hasAnswer()) {
                print("here drawTemplet");
                englishHandwrite.drawTemplate();
                englishHandwrite.clearCanvas();
            } else {
                englishHandwrite.resetTemplatePath();
            }
        }
        if(isReading){
            readTempText.setText("[" + answer + "]");
        }

        rightAnswerResponse(result);
        //if(!homeworkMgr.isReviewState()) homeworkMgr.judgeProcessEnd(result,incorrectTime);
        //else return;
        homeworkMgr.judgeProcessEnd(result);

        if ((probText = homeworkMgr.getNextProbText()) != null) {
            setQuestionText(probText);
            if(result){
                if(isReading) {
                    readTempText.setText("[   ]");
                    readTempText.setTextColor(Color.rgb(0, 0, 0));
                }
                else{
                    //TODO hard code
                    progressText.setText(homeworkMgr.getRoundOrder()+" / 2");
                    progressText.setTextColor(Color.GREEN);
                }
            }
            //else resultView.append("\nans:"+homeworkMgr.getAnswerText());
            //hard code
            if((incorrectTime==0||incorrectTime==4) && !homeworkMgr.isSubterm()) {
                voiceSpeak(homeworkMgr.getNextProbVoice());
            }
            else{
                print("jingle "+incorrectTime);
            }
        }
        else changeHwState();
    }
    /**
     * 手写识别完成后判断是否自动进入提交判断流程
     * 现阶段抄写类直接进入，默写类需通过dictationButton提交并进入
     */
    public void sendResToHwProcess(String result){
        if(homeworkMgr.isCopy())
            immJudgeProcess(result);
        else if(homeworkMgr.isDictation()) {
            dictationBuffer.add(result);
            englishHandwrite.drawTemplate();
        }
        else
            System.out.println("booooooooooooooooooom");
    }
    /**
     * 进入下一题
     * argv String 即将呈现的题目文本
     */
    public void setQuestionText(String probText){
        //warning hard code
        Log.d("TEST", "setnextques");
        //切换至下一题时刷新已写窗口
        if(isDrawing && homeworkMgr.getSubTermOrder()==0)
            englishHandwrite.clearTemplate();
        setAnswerLayout();
        if(isReading){
            problemText.setText("");
            readText.setText(probText);
            readTempText.setTextColor(Color.rgb(0, 0, 0));
            if(probText.contains(" "))
                readText.setTextSize(28);
            else {
                readText.setTextSize(48);
                readText.setGravity(Gravity.CENTER);
            }
        }
        else
            problemText.setText(probText);
    }

    /**
     * 根据不同题型设置不同的界面
     */
    private void setAnswerLayout(){
        print("shit " + homeworkMgr.getQuestionType() + " text:" + homeworkMgr.getNextProbText());
        if(homeworkMgr.getQuestionType()==Constants.QUESTION_TYPE_READ){
            isReading = true;
            isDrawing = false;
            surfaceLayout.setVisibility(View.INVISIBLE);
            stemLayout.setVisibility(View.INVISIBLE);
            readLayout.setVisibility(View.VISIBLE);
            if(homeworkMgr.isReadText())
                readTempText.setVisibility(View.INVISIBLE);
            else
                readTempText.setVisibility(View.VISIBLE);
        }
        else if(homeworkMgr.getQuestionType()==Constants.QUESTION_TYPE_COPY){
            isReading = false;
            isDrawing = true;
            readLayout.setVisibility(View.INVISIBLE);
            stemLayout.setVisibility(View.INVISIBLE);
            dictationButton.setVisibility(View.INVISIBLE);
            progressText.setTextColor(Color.BLACK);
            progressText.setVisibility(View.VISIBLE);
            surfaceLayout.setVisibility(View.VISIBLE);
        }
        else if(homeworkMgr.getQuestionType()==Constants.QUESTION_TYPE_WRITE){
            isReading = false;
            isDrawing = true;
            readLayout.setVisibility(View.INVISIBLE);
            stemLayout.setVisibility(View.INVISIBLE);
            progressText.setVisibility(View.INVISIBLE);
            dictationButton.setVisibility(View.VISIBLE);
            surfaceLayout.setVisibility(View.VISIBLE);
        }
        else{
            isDrawing = false;
            isReading = false;
            surfaceLayout.setVisibility(View.INVISIBLE);
            readLayout.setVisibility(View.INVISIBLE);
            stemTextView.setText(homeworkMgr.getStemText());
            stemLayout.setVisibility(View.VISIBLE);
        }
    }
    /**
     * WARNING
     */
    public void setProb2Answer(){
        problemText.setText(homeworkMgr.getAnswerText());
        dictationBuffer.clear();
    }
    /**
     * 跳至下一题
     */
    public void goNextProb(){
        String text;
        homeworkMgr.getNextProb();
        englishHandwrite.clearCanvas();
        if ((text = homeworkMgr.getNextProbText())!=null) {
            setQuestionText(text);
            voiceSpeak(homeworkMgr.getNextProbVoice());
        }
        else changeHwState();
    }
    /**
     * 跳至上一题
     */
    public void goLastProb(){
        englishHandwrite.clearCanvas();
        String text;
        homeworkMgr.getLastProb();
        if ((text = homeworkMgr.getNextProbText())!=null) {
            setQuestionText(text);
            voiceSpeak(homeworkMgr.getNextProbVoice());
        }
        else changeHwState();
    }
    /**
     * 设置为下一题
     */
    public void setNextProb(){
        String text;
        //warning hard code
        homeworkMgr.judgeProcessEnd(true);
        if ((text = homeworkMgr.getNextProbText())!=null) {
            setQuestionText(text);
            voiceSpeak(homeworkMgr.getNextProbVoice());
        }
        else changeHwState();
    }
    /**
     * 作业状态变更
     */
    public void changeHwState(){
        if(homeworkMgr.isInCorEmpty()) {
            showTip("结束阶段");
            contCorVoicePlay();
        }
        else {
            showTip("订正阶段");
            responseFLAG = 0;
            homeworkMgr.changeHwState(1);
            correctVoicePlay();
        }
    }
    /**
     * 正误判断反馈
     */
    public void rightAnswerResponse(boolean isCorrect){
        String incorrectText="",explainText=homeworkMgr.getExplainText();
        //Remaining 封装入figureMgr中

        if(isReading){
            if(isCorrect) {
                readTempText.setTextColor(Color.rgb(0, 255, 0));
            }
            else
                readTempText.setTextColor(Color.rgb(255,0,0));
        }
        if(isCorrect) {
            if(homeworkMgr.hasAnswer()) {
                anim.stop();
                rightMarkImage.setVisibility(View.VISIBLE);
                new Thread(RightMarkThread).start();
                anim.start();

            }
        }
        if (isCorrect) {
            if(homeworkMgr.hasAnswer()&&homeworkMgr.isLogic()) {
                continuousCorTime++;
            }
            incorrectTime = 0;
        } else {
            incorrectTime++;
            continuousCorTime = 0;
        }
        if(isCorrect&&continuousCorTime>=3 ) {
            explainText += "太棒啦，连对好多了哦，加油";
            continuousCorTime = 0;
        }

        String wrongText="";
        if("".equals(wrongText = homeworkMgr.getWrongText())&&(homeworkMgr.isDictation()||homeworkMgr.isCopy())&&incorrectTime<=3)
            wrongText = "不对哦";
        String text = (isCorrect)?explainText:wrongText;

        voiceSpeak(text);
    }

    /**
     * 返回nav应答反馈
     */
    public void returnNavResponse(int type){
        Log.d("TEST", "in return nav");
        String text = "";
        if(type==1)
            text="那我们回到开始吧";
        if(type==2)
            text="真棒，今天的作业做完啦，休息一下吧";
        jmpFlag = 1;
        voiceSpeak(text);
    }
    /**
     * 全对反馈
     */
    public void contCorVoicePlay(){
        voiceSpeak("太棒啦");
        //hard code
        homeworkMgr.setHomeworkState(4);
    }
    /**
     * 订正起始语音
     */
    public void correctVoicePlay(){
        //REMAINING 作业情况反馈
        int inCorNum = homeworkMgr.getInCorNum();
        String text = "这次作业错了"+inCorNum+"道题"
                + (inCorNum<3?",还不错哦！。":"有点多呢，是不是开小差了呀？。")
                +"下面我们来看看做错的题目吧！。"
                +homeworkMgr.getNextProbVoice();
        voiceSpeak(text);
        setQuestionText(homeworkMgr.getNextProbText());
    }
    /**---------------------------听写监听相关方法-----------------------*/

    /**
     * 监听入口
     */
    public void startListener(){
        if(SpeakingFlag == 0) {
            int ret = mIat.startListening(mRecognizerListener);
            if (ret != ErrorCode.SUCCESS) {
                showTip("听写失败,错误码：" + ret);
            } else {
                showTip(getString(R.string.text_begin));
                //TODO
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
        public void onResult(RecognizerResult results, boolean isLast) {
            printResult(results);
            if (isLast) {
                if(homeworkMgr.isReviewState()){
                    //warning hard code
                    int res = UsrIntentTools.reviewIntentJudge(cntVoiceString);
                    if(res ==0 ) {
                        homeworkMgr.setHomeworkState(0);
                        setNextProb();
                    }
                    else if(res==1)voiceSpeak(homeworkMgr.getWrongText());
                    else {
                        answer = HttpTools.getAnswer(cntVoiceString, Constants.SESSION_ID);
                        //if(answer.getStatusCode().contains("30"))
                        //    jmpFlag = Integer.parseInt(answer.getStatusCode());
                        voiceSpeak(answer.getText());
                        //voiceSpeak("别开小差啦，好好消化一下");
                    }
                }
                else if(homeworkMgr.isNormalState()){
                    if(isReading||isDrawing) {
                        if (isReading && UsrIntentTools.vagueIntentJudgeEn(cntVoiceString, homeworkMgr.getAllAnswerText())) {
                            print(cntVoiceString+" "+homeworkMgr.getAllAnswerText());
                            immJudgeProcess(cntVoiceString);
                        }
                        else if (homeworkMgr.isDictation() && cntVoiceString.contains("不会")) {
                            setProb2Answer();
                            voiceSpeak("好吧，那我们再来抄一遍");
                        } else {
                            answer = HttpTools.getAnswer(cntVoiceString, Constants.SESSION_ID);
                            //if (answer.getStatusCode().contains("30"))
                            //    jmpFlag = Integer.parseInt(answer.getStatusCode());
                            voiceSpeak(answer.getText());
                        }
                    }
                }
                startListener();
            }
        }

        @Override
        public void onVolumeChanged(int volume, byte[] data) {
        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
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
        StringBuilder resultBuffer = new StringBuilder();
        for (String key : mIatResults.keySet()) {
            resultBuffer.append(mIatResults.get(key));
        }
        cntVoiceString = resultBuffer.toString();
        //TODO how mIatResults do
        //cntVoiceString = text;
    }
    /**
     * 合成入口
     */
    public void voiceSpeak(String text){
        if(text.length()==0)
            return;
        if(SpeakingFlag == 1)
            voiceBuffer = text;
        else {
            SpeakingFlag = 1;
            mTts.startSpeaking(text, mTtsListener);
        }
    }
    /**
     * 初始化监听。语音合成
     */
    private InitListener mTtsInitListener = new InitListener() {
        @Override
        public void onInit(int code) {
            Log.d(TAG, "InitListener init() code = " + code);
            if (code != ErrorCode.SUCCESS) showTip("初始化失败,错误码："+code);
            else{
                welcome();
            }
        }
    };
    /**
     * 合成回调监听。
     */
    private SynthesizerListener mTtsListener = new SynthesizerListener() {

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
            responseFLAG = 0;
        }

        @Override
        public void onCompleted(SpeechError error) {
            if (error == null) {
                //showTip("播放完成");
                if(!voiceBuffer.equals("")) {
                    SpeakingFlag = 0;
                    String nextVoice = voiceBuffer;
                    voiceBuffer = "";
                    voiceSpeak(nextVoice);
                }
                else {
                    SpeakingFlag = 0;
                    if(jmpFlag!=0){
                        Intent intent;
                        if(jmpFlag==1) {
                            intent = new Intent(EnglishDemo.this, EntranceActivity.class);
                            homeworkMgr.setSubjectOrder();
                            intent.putExtra(Constants.CONSTANT_SUBJECT, homeworkMgr.getSubject());
                            SharedPreferences sp = getSharedPreferences(Constants.SHARED_FILE_NAME, Activity.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putString(Constants.SHARED_EN_WRONG_RADIX, homeworkMgr.getWrongRadix());
                            editor.commit();
                            //intent.putExtra(Constants.CONSTANT_DATA, homeworkMgr.getSubjectOrders());
                            startActivity(intent);
                            System.exit(0);
                        }
                        else {
                        }
                        startListener();
                    }
                    if(homeworkMgr.isNormalState()) {
                        print("in normal state "+homeworkMgr.getStemText()+" ");
                        if(!homeworkMgr.hasAnswer())
                            immJudgeProcess("default");
                        startListener();
                    }
                    else if(homeworkMgr.isReviewState()){
                        //计时器开启
                        startListener();
                    }
                    else if(homeworkMgr.isCorrectState()){
                        Log.d("TEST","in Cor judge");
                        startListener();
                    }
                    else if(homeworkMgr.isAllRightState()){
                        Log.d("TEST","in allright judge");
                        returnNavResponse(2);
                    }
                    else if(homeworkMgr.isFinishState()){
                        Log.d("TEST","in finish judge");
                        returnNavResponse(2);
                    }
                    else{
                        returnNavResponse(1);
                        Log.d("errrrrrrrror","out of index");
                    }
                }
            } else {
                print("here no answer");
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

    public void welcome(){
        String text = homeworkMgr.getNextProbVoice();
        setQuestionText(homeworkMgr.getNextProbText());
        voiceSpeak(text);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mIat.cancel();
        mIat.destroy();
        System.exit(0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        isRunning = true;
    }


}
