package com.aihomework.voicedemo;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aihomework.constants.Constants;

import java.util.ArrayList;
import java.util.Random;

public class MathSquare extends Activity {

    Random random = null;
    int remainTime = Constants.TIME_LIMIT_MS;
    boolean stop = true;
    ArrayList<ImageView>numberImages = null;

    AnimationDrawable anim = null;
    View squareLayout = null;
    Button restartButton = null;
    TextView answerText = null;
    TextView questionText = null;
    ImageView backspaceImage = null;
    ImageView figureImage = null;
    ProgressBar timeProgressBar = null;
    int result = -1;
    boolean rightFlag = false;
    int timeCount = 0;

    private TimeHandler timeHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.mathsquare_layout);

        timeHandler = new TimeHandler();

        squareLayout = findViewById(R.id.squareLayout);
        restartButton = (Button)findViewById(R.id.restartButton);
        restartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setNextQuestion();
            }
        });
        figureImage = (ImageView)findViewById(R.id.figureView);
        figureImage.setBackgroundResource(R.drawable.frame);
        anim = (AnimationDrawable) figureImage.getBackground();
        answerText = (TextView)findViewById(R.id.answerTextView);
        questionText = (TextView)findViewById(R.id.questionTextView);
        backspaceImage = (ImageView)findViewById(R.id.backspaceImage);
        backspaceImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(answerText.getText().toString().length()==0)
                    return;
                String answer = answerText.getText().toString();
                answer=answer.substring(0,answer.length()-1);
                answerText.setText(answer);
            }
        });
        timeProgressBar = (ProgressBar)findViewById(R.id.timeProgressBar);
        numberImages = new ArrayList<ImageView>();
        numberImages.add((ImageView)findViewById(R.id.zeroImage));
        numberImages.add((ImageView)findViewById(R.id.oneImage));
        numberImages.add((ImageView)findViewById(R.id.twoImage));
        numberImages.add((ImageView)findViewById(R.id.threeImage));
        numberImages.add((ImageView)findViewById(R.id.fourImage));
        numberImages.add((ImageView)findViewById(R.id.fiveImage));
        numberImages.add((ImageView)findViewById(R.id.sixImage));
        numberImages.add((ImageView)findViewById(R.id.sevenImage));
        numberImages.add((ImageView)findViewById(R.id.eightImage));
        numberImages.add((ImageView) findViewById(R.id.nineImage));
        answerText.setText("");
        timeProgressBar.setMax(remainTime);
        random = new Random();
        initNumberButtons();
        new Thread(TimeThread).start();
        setNextQuestion();

    }
    private void setNextQuestion(){
        stop = true;
        int number1 = getRandomNumber();
        int number2 = getRandomNumber();
        result = number1+number2;
        questionText.setText(number1+"+"+number2+"=");
        answerText.setText("");
        answerText.setTextColor(Color.rgb(0, 0, 0));
        refreshTimeBar();
        stop = false;
    }
    private void refreshTimeBar(){
        remainTime = Constants.TIME_LIMIT_MS;
        timeProgressBar.setProgress(remainTime);
    }
    private void judgeAnswer(){
        int answer = Integer.parseInt(answerText.getText().toString());
        if(answer==result){
            stop = true;
            rightFlag = true;
            //warning hard code
            answerText.setTextColor(Color.rgb(0,100,0));
            anim.stop();anim.start();
            stop = false;
        }
    }
    private void initNumberButtons(){
        for(int i=0;i<10;i++){
            final int fi = i;
            numberImages.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    answerText.append(fi+"");
                    judgeAnswer();
                }
            });
        }
    }
    private int getRandomNumber(){
        return random.nextInt(Constants.SQUARE_UPPER);
    }


    public class TimeHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(!stop) {
                remainTime -= 100;
                timeProgressBar.setProgress(remainTime);
                if (remainTime <= 0)
                    setRestart();
            }
            if(rightFlag) {
                timeCount++;
                if(timeCount>=8){
                    timeCount = 0;
                    rightFlag=false;
                    setNextQuestion();
                }
            }
        }
    }
    private void setRestart(){
        stop = true;
        answerText.setText("");
        questionText.setText("时 间 到");
        squareLayout.setVisibility(View.INVISIBLE);
        restartButton.setVisibility(View.VISIBLE);
    }

    Runnable TimeThread = new Runnable() {
        @Override
        public void run() {
            while(true){
                try {
                    Thread.sleep(100);
                    Message message = new Message();
                    timeHandler.sendMessage(message);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.exit(0);
    }
}
