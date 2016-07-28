package com.aihomework.voicedemo;

import android.app.Activity;
import android.app.FragmentManager;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.aihomework.constants.Constants;
import com.aihomework.homework.HomeworkMgr;
import com.aihomework.questions.BaseQuestionFragment;
import com.aihomework.questions.ChineseCopyFragment;
import com.aihomework.questions.ChineseDictationFragment;
import com.aihomework.questions.ChineseMulPronounceFragment;
import com.aihomework.questions.ChinesePartReadFragment;
import com.aihomework.questions.ChineseReadFragment;
import com.aihomework.questions.DrawTouchListener;
import com.aihomework.questions.EnglishCopyFragment;
import com.aihomework.questions.EnglishDictationFragment;
import com.aihomework.questions.EnglishReadFragment;
import com.aihomework.questions.MathCalFragment;
import com.aihomework.questions.ResultJudgeListener;
import com.aihomework.questions.StemFragment;
import com.aihomework.tools.HwRecognizerListener;
import com.aihomework.tools.HwSynthesizerListener;
import com.aihomework.userAction.HomeworkRectifor;
import com.aihomework.userAction.UsrIntentTools;
import com.google.gson.Gson;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.InputStreamReader;

public abstract class BaseSubjectActivity extends Activity implements ResultJudgeListener {
    static public void print(String str){
        System.out.println("Aicitel "+str);
    }

    //TODO construct
    protected int incorrectTime = 0;

    /**--------------------------------动画、UI变量-----------------------------------------------*/
    AnimationDrawable anim = null;
    ImageView figureImage = null;
    protected ImageView markImage = null;

    protected static String homeworkString = "cn";

    public class OnLoadFinishHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            Gson gson = new Gson();
            if(!msg.getData().getString(Constants.HW_KEY_STRING).equals("error")){
                homeworkMgr = gson.fromJson(msg.getData().getString(Constants.HW_KEY_STRING),HomeworkMgr.class);
                homeworkMgr.initData(new int[]{0, 0, 0});
                homeworkMgr.setResultJudgeListener(BaseSubjectActivity.this);
                //homeworkMgr.setBaseSubjectActivity(BaseSubjectActivity.this);
            }
            else{
                try {
                    StringBuilder textString = new StringBuilder();
                    //WARNING hard code
                    char[] data = new char[4096];
                    InputStreamReader inputStreamReader =
                            new InputStreamReader(getResources().getAssets().open("homework/"+homeworkString+"Homework.txt"));
                    for(int n;(n=inputStreamReader.read(data))!=-1;)
                        textString.append(new String(data,0,n));
                    homeworkMgr = gson.fromJson(textString.toString(),HomeworkMgr.class);
                            ///homeworkMgr.initData(this.getIntent().getExtras().getIntArray(Constants.CONSTANT_DATA));
                    homeworkMgr.initData(new int[]{0,0,0});
                    homeworkMgr.setResultJudgeListener(BaseSubjectActivity.this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            initWithWelcome();
        }
    }
    OnLoadFinishHandler onLoadFinishHandler = new OnLoadFinishHandler();
    public class MarkHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            markImage.setVisibility(View.INVISIBLE);
            if(msg.what==0) {
                questionFragment.onAnswerResponse(true);
                setNextQuestion();
            }
            else{
                markImage.setVisibility(View.INVISIBLE);
                questionFragment.onAnswerResponse(false);
                if(incorrectTime>=3 && !homeworkMgr.isDictation() && !homeworkMgr.isCopy()) {
                    hwSynthesizerListener.voiceSpeak(homeworkMgr.getWrongText());
                    if(incorrectTime>=5) {
                        hwSynthesizerListener.voiceSpeak("我们先放一放这题，看下一题吧");
                        if(homeworkMgr.jmpToNextQuestion())
                            setNextQuestion();
                        else
                            questionFragment.setNextView();
                        incorrectTime = 0;
                    }
                }
                else if(incorrectTime>=3 && homeworkMgr.isCopy()){
                    //TODO too many time wrong copying
                }
                else if(incorrectTime>=3 && homeworkMgr.isDictation()){
                    questionFragment.setRightAnswer(homeworkMgr.getAnswerText());
                    hwSynthesizerListener.voiceSpeak("错好多次了，我们再抄一遍吧");
                }
            }
        }
    }
    MarkHandler rightMarkHandler = new MarkHandler();
    class MarkThread implements Runnable {
        int isRight=-1;
        public void setIsRight(int isRight){
            this.isRight = isRight;
        }
        @Override
        public void run() {
            try {
                Thread.sleep(600);
                Message message = new Message();
                message.what = isRight;
                rightMarkHandler.sendMessage(message);
            }
            catch(Exception e){
                print("sleep error");
            }
        }
    }
    class HwLoadThread implements Runnable{
        private String subjectString = "";
        public void setSubjectString(String subjectString){
            this.subjectString = subjectString;
        }
        @Override
        public void run() {
            try {
                String url = Constants.baseURL+"/"+this.subjectString;
                String res = "error";
                try
                {
                    HttpGet httpGet = new HttpGet(url);
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpResponse response = httpClient.execute(httpGet);
                    res = com.aihomework.tools.HttpTools.getResponseResult(response);
                }
                catch (Exception e)
                {
                    res = "error";
                    e.printStackTrace();
                }
                Message message = new Message();
                Bundle bundle = new Bundle();
                bundle.putString(Constants.HW_KEY_STRING, res);
                message.setData(bundle);
                onLoadFinishHandler.sendMessage(message);
            }
            catch(Exception e){
                print("sleep error");
            }
        }
    }

    /**----------------------------------thread变量-------------------------------------------*/

    /**
     * load thread used to get homework from http
     */
    private HwLoadThread hwLoadThread = new HwLoadThread();

    /**
     * mark thread
     */
    private MarkThread markThread = new MarkThread();


    /**----------------------------------fragment控制变量-------------------------------------*/
    protected FragmentManager fm = null;
    protected DrawTouchListener drawTouchListener = null;
    protected BaseQuestionFragment questionFragment = null;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        drawTouchListener.onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void registerMyTouchListener(DrawTouchListener listener)
    {
        drawTouchListener=listener;
    }
    /**--------------------------------------讯飞语音变量-------------------------------------*/

    protected HwRecognizerListener hwRecognizerListener = null;
    protected HwSynthesizerListener hwSynthesizerListener = null;
    @Override
    public void executeResponse(String wrongAnswerResString){
        if(wrongAnswerResString.matches("[a-z]*")&&homeworkMgr.getAnswerText().matches("[a-z]*")) {
            String baseString = "写的不对哦";
            int difPos = -1;
            if((difPos = UsrIntentTools.similarJudge(homeworkMgr.getAnswerText(),wrongAnswerResString))<wrongAnswerResString.length())
                baseString = "第"+difPos+"个字母不是"+wrongAnswerResString.charAt(difPos)+"是"+homeworkMgr.getAnswerText().charAt(difPos);
            hwSynthesizerListener.voiceSpeak(baseString);
        }
    }

    /**------------------------------------UI与作业控制变量-----------------------------------*/

    protected int jmpFlag = 0;

    protected HomeworkMgr homeworkMgr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        print(" in chinese ");
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //TODO switch option of background, homework
        setContentView(R.layout.chinese_demo);

        int type = this.getIntent().getExtras().getInt(Constants.CONSTANT_SUBJECT);
        if(type==Constants.CONSTANT_MATH)
            homeworkString = "ma";
        else if(type==Constants.CONSTANT_ENGLISH)
            homeworkString = "en";

        hwLoadThread.setSubjectString(homeworkString);
        new Thread(hwLoadThread).start();

        fm = getFragmentManager();
        markImage = (ImageView)findViewById(R.id.rightMarkImageView);
        figureImage = (ImageView) findViewById(R.id.figureView);
        figureImage.setBackgroundResource(R.drawable.frame);
        anim = (AnimationDrawable) figureImage.getBackground();

    }
    protected void initWithWelcome(){
        initListener();
        welcome();
    }

    @Override
    public void setCanvasVisibility(View view){
        if(!homeworkMgr.isSubterm()&&homeworkMgr.getAnswerText().length()<=1) {
            print("should invisible");
            view.setVisibility(View.INVISIBLE);

        }
    }
    @Override
    public void setFragmentProb(TextView textView){
        if(homeworkMgr==null)
            print("home Mgr null");
        else {
            print("next prob is " + homeworkMgr.getNextProbText());
            String probText = homeworkMgr.getNextProbText();
            if(homeworkMgr.isDictation())
                probText = HomeworkRectifor.getPinyin(probText);
            textView.setText(probText);
        }
    }
    @Override
    public String[] getChoices(){
        return homeworkMgr.getChoices();
    }

    /**---------------------------作业流程方法-----------------------*/
    @Override
    public int getAnswerTextLength(){
        return homeworkMgr.getAnswerText().length();
    }

    /*
    public boolean judgeAnswerFromFrag(String userAnswer,String stdAnswer){
        return this.questionFragment.judgeAnswer(userAnswer,stdAnswer);
    }*/

    @Override
    public void immJudgeProcess(String answer){
        boolean result;
        print("hard to believe here in imm " + answer);
        //TODO set subject in it
        result = homeworkMgr.voiceJudgeAnswer(answer);
        rightAnswerResponse(result);
        print("before judgeProcessEnd " + homeworkMgr.getNextProbText());
        //TODO second argv is incorrect time
        //setNextQuestion(result);
    }

    protected void setNextQuestion(){
        String probText;
        homeworkMgr.judgeProcessEnd(true);
        if ((probText = homeworkMgr.getNextProbText()) != null) {
            if(homeworkMgr.isSetNext()) {
                print("is set next");
                setQuestionText(probText, homeworkMgr.getQuestionType());
            }
            else
                print("not set next");
        }
        else changeHwState();
    }

    /**
     * 作业状态变更
     */

    public void changeHwState(){
        contCorVoicePlay();
    }
    public void contCorVoicePlay(){
        hwSynthesizerListener.voiceSpeak("真棒，今天的作业做完啦");
        homeworkMgr.setHomeworkState(4);
    }
    /**
     * 进入下一题
     * argv String 即将呈现的题目文本
     * 5.7 更新，重构题目，设置下一题questionFragment
     */
    public void setQuestionText(String probText,int questionType){
        hwSynthesizerListener.voiceSpeak(homeworkMgr.getNextProbVoice());
        if(questionFragment!=null)
            fm.beginTransaction().remove(questionFragment).commit();
        print("setnextques "+probText+" "+questionType);
        //切换至下一题时刷新已写窗口
        switch (questionType) {
            case Constants.QUESTION_TYPE_STEM:
                questionFragment = new StemFragment();
                questionFragment.setQuestionType(homeworkMgr.getStemType());
                //questionFragment.drawView();
                break;
            case Constants.QUESTION_TYPE_CN_READ:
                questionFragment = new ChineseReadFragment();
                break;
            case Constants.QUESTION_TYPE_CN_COPY:
                questionFragment = new ChineseCopyFragment();
                break;
            case Constants.QUESTION_TYPE_CN_DICT:
                questionFragment = new ChineseDictationFragment();
                break;
            case Constants.QUESTION_TYPE_CN_PARTREAD:
                questionFragment = new ChinesePartReadFragment();
                break;
            case Constants.QUESTION_TYPE_CN_CHOICE:
                questionFragment = new ChineseMulPronounceFragment();
                break;
            case Constants.QUESTION_TYPE_MA_FILLIN:
                questionFragment = new MathCalFragment();
                break;
            case Constants.QUESTION_TYPE_MA_CAL:
                questionFragment = new MathCalFragment();
                break;
            case Constants.QUESTION_TYPE_EN_READ:
            case Constants.QUESTION_TYPE_EN_FOLLOWREAD:
            case Constants.QUESTION_TYPE_EN_DIAG:
                questionFragment = new EnglishReadFragment();
                break;
            case Constants.QUESTION_TYPE_EN_COPY:
                questionFragment = new EnglishCopyFragment();
                break;
            case Constants.QUESTION_TYPE_EN_DICT:
                questionFragment = new EnglishDictationFragment();
                break;
        }
        fm.beginTransaction().replace(R.id.question_fragment, questionFragment).commit();
        questionFragment.setResultJudgeListener(this);
        homeworkMgr.setNextFlag(false);
    }
    /**
     * 正误判断反馈
     */
    public void rightAnswerResponse(boolean isCorrect) {
        //TODO 全部交给handler处理
        if(isCorrect) {
            incorrectTime = 0;
            anim.stop();
            anim.start();
            if(homeworkMgr.hasAnswer()) {
                markImage.setImageResource(R.drawable.mark_right);
                markImage.setVisibility(View.VISIBLE);
                markThread.setIsRight(Constants.INT_RIGHT);
                new Thread(markThread).start();
            }
            else
                setNextQuestion();
        }
        else{
            incorrectTime++;
            markImage.setImageResource(R.drawable.mark_wrong);
            markImage.setVisibility(View.VISIBLE);
            markThread.setIsRight(Constants.INT_WRONG);
            new Thread(markThread).start();
        }
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

    protected void welcome(){
        setQuestionText(homeworkMgr.getNextProbText(), homeworkMgr.getQuestionType());
    }

    @Override
    public boolean judgeAnswerFromFrag(String userAnswer,String stdAnswer){
        return this.questionFragment.judgeAnswer(userAnswer,stdAnswer);
    }

    abstract public void initListener();
    abstract public void returnNavResponse(int type);




}
