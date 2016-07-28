package com.aihomework.homework;

import android.util.Log;

import com.aihomework.constants.Constants;
import com.aihomework.questions.ResultJudgeListener;
import com.aihomework.userAction.UsrIntentTools;
import com.aihomework.voicedemo.ChineseActivity;
import com.aihomework.voicedemo.EnglishActivity;
import com.aihomework.voicedemo.EnglishDemo;

import java.util.ArrayList;

/**
 * Created by bluemaple on 2016/2/25.
 *
 * data format
 *
 */
public class HomeworkMgr {

    private int[] subjectOrders = {0,0,0};
    private boolean nextFlag = false;
    private int order;
    private int wrongCount = 0;
    private int roundOrder = 0;
    private int subOrder;
    private int subjectType;

    //state 0 means normal, 1 means correcting, 2 means all right, 3 means review, 4 means correct finish
    private int homeworkState;
    private int problemCount;
    private ArrayList<Homework>homework = new ArrayList<Homework>();
    private ArrayList<Integer>inCorProbs = new ArrayList<Integer>();

    //流程时心理测量数据存储，此处仅包括每题的作答时间
    public long[] normalResTime;
    //流程时错误次数
    public int[] normalWrongTime;
    //订正时心理测量数据存储，此处包括作答时间与错误次数
    public long[][] correctingResTime;
    public int[] correctingWrongTime;
    //remaining 此处应当使用组合设计模式分类对待不同类型的题目

    private ResultJudgeListener resultJudgeListener = null;
    public void setResultJudgeListener(ResultJudgeListener resultJudgeListener){
        this.resultJudgeListener = resultJudgeListener;
    }
    /*
    private BaseSubjectActivity baseSubjectActivity = null;
    public void setBaseSubjectActivity(BaseSubjectActivity baseSubjectActivity){
        this.baseSubjectActivity = baseSubjectActivity;
    }*/

    public void initData(int[] orders){
        this.subjectOrders = orders;
        this.subOrder = 0;
        this.order = orders[this.subjectType];
        this.homeworkState = 0;
        this.problemCount = homework.size();
        normalResTime = new long[problemCount+1];
        normalWrongTime = new int[problemCount+1];
        correctingResTime = new long[problemCount+1][10];
        correctingWrongTime = new int[problemCount+1];
        for(int i=0;i<=problemCount;i++) {
            normalResTime[i] = 0;
            correctingWrongTime[i]=0;
            for(int j =0;j<10;j++) {
                correctingResTime[i][j] = 0;
            }
        }
    }
    public void setHomework(ArrayList<Homework>homework){
        this.homework = homework;
    }
    public ArrayList<Homework> getHomework(){
        return this.homework;
    }
    public void setSubjectType(int subjectType) {
        this.subjectType = subjectType;
    }
    public void initProb(){
        inCorProbs.clear();
    }
    public int getProCount(){
        return this.problemCount;
    }
    public String getWrongRadix(){
        return this.problemCount+" "+wrongCount+" "+wrongCount*100/problemCount;
    }
    public String getResData(){
        String totalResTimeString ="Total Response Time\n";
        long totalResTime = 0;
        String dataString = "Normal Response Time\n";
        for(int i = 0;i<getProCount();i++) {
            if(homework.get(i).isSpec()||!homework.get(i).hasAnswer()) continue;
            totalResTime+=normalResTime[i];
            dataString += ("\n" +i+" "+(float)normalResTime[i]/1000.0);
        }
        dataString+="\nCorrecting Wrong Count";
        for(int i = 0;i<problemCount;i++) {
            if(homework.get(i).isSpec()||!homework.get(i).hasAnswer()) continue;
            dataString += ("\n" +i+" "+ correctingWrongTime[i]);
        }
        /*
        dataString+="\nCorrecting Response Time";
        for(int i = 0;i<problemCount;i++) {
            for(int j=0;j<correctingWrongTime[i]+1;j++)
                dataString += ("\n"  +i+" "+ correctingResTime[i][j]);
        }*/
        return totalResTimeString+(float)totalResTime/1000+"\n"+dataString;
    }
    public boolean isReading(){
        ChineseActivity.print("type is "+homework.get(order).questionType+"");
        return homework.get(order).questionType==Constants.QUESTION_TYPE_CN_PARTREAD
                ||homework.get(order).questionType==Constants.QUESTION_TYPE_READ
                ||homework.get(order).questionType==Constants.QUESTION_TYPE_EN_READ
                ||homework.get(order).questionType==Constants.QUESTION_TYPE_EN_FOLLOWREAD
                ||homework.get(order).questionType==Constants.QUESTION_TYPE_EN_DIAG;
    }
    public int getStemType(){return homework.get(order).getStemType();}
    public String getShowRoute(){return "bookshow/"+homework.get(order).getQuestionText();}
    public void setSubjectOrder(){this.subjectOrders[this.getSubject()] = this.order;}
    public int[] getSubjectOrders(){return this.subjectOrders;}
    public boolean isSubjectEn(){
        return subjectType== Constants.CONSTANT_ENGLISH;
    }
    public boolean isSubjectMath(){
        return subjectType== Constants.CONSTANT_MATH;
    }
    public void setHomeworkState(int homeworkState){
        this.homeworkState = homeworkState;
    }
    public boolean isNormalState(){
        return homeworkState==0;
    }
    public boolean isCorrectState(){
        return homeworkState==1;
    }
    public boolean inRound(){
        return this.roundOrder!=0;
    }
    public boolean isSubterm(){
        return homework.get(order).getSubTermCount()!=1;
    }
    public boolean inSubterm(){
        return this.subOrder!=0;
    }
    public int getSubTermOrder(){
        return this.subOrder;
    }
    public boolean isReadText() {return Constants.READ_TEXT.equals(homework.get(order).getKnowledgeType());}
    public boolean isCopy(){return Constants.COPY.equals(homework.get(order).getKnowledgeType());}
    public boolean isDictation(){return Constants.DICTATION.equals(homework.get(order).getKnowledgeType());}
    public int getQuestionType(){return homework.get(order).getQuestionType();}
    public String getStemText(){return homework.get(order).getStemText();}
    public String[] getChoices(){return homework.get(order).getChoices();}

    public boolean isAllRightState(){
        return homeworkState==2;
    }
    public boolean isReviewState() {
        return homeworkState==3;
    }
    public boolean isFinishState(){
        return homeworkState==4;
    }
    public boolean isSpecState(){
        return homework.get(order).isSpec();
    }
    public boolean isNextAnswerCn(){
        return !homework.get(order).getAnswerText().matches("[a-zA-Z ]*");
    }
    public boolean hasAnswer(){
        return homework.get(order).getAnswerText().length()!=0;
    }
    public boolean hasCorVoice(){
        return !(homeworkState==0&&subjectType== Constants.CONSTANT_MATH);
    }
    public void changeHwState(int state){
        this.order = 0;
        this.homeworkState = state;
    }
    public void getLastProb(){
        if(this.order>0) {
            this.order--;
            this.subOrder = 0;
        }
    }
    public boolean isSetNext(){
        return this.nextFlag;
    }
    public void setNextFlag(boolean nextFlag){
        this.nextFlag = nextFlag;
    }
    public boolean isLogic(){
        if(this.subjectType==Constants.CONSTANT_MATH)
            return true;
        if(homework.get(order).isLogic())
            return true;
        return false;
    }
    public void getNextProb(){
        if(this.order<this.problemCount-1) {
            this.order++;
            this.subOrder = 0;
        }
    }
    public boolean isInCorEmpty(){
        return inCorProbs.size()==0;
    }
    public int getSubject(){
        return this.subjectType;
    }
    public int getRoundOrder(){
        return this.roundOrder;
    }
    public void resTimeInit(){
        normalResTime[order] = System.currentTimeMillis();
    }
    public String getWrongText(){
        return (homeworkState==0)?homework.get(order).getWrongText():homework.get(inCorProbs.get(order)).getWrongText();
    }
    public String getReviewText(){
        return homework.get(order).getReviewText();
    }
    public String getNextProbText(){

        if(homeworkState==0)
            normalResTime[order] = System.currentTimeMillis() - normalResTime[order];
        //此处为面板题目
        //hard code
        if(homeworkState==0) {
            EnglishDemo.print("order and count is_"+order+"_"+problemCount);
            if (order >= problemCount)
                return null;
            else {
                if(homework.get(order).getQuestionText().equals(""))
                    return homework.get(order).getStemText();
                return homework.get(order).getQuestionText();
            }
        }
        else
            return "out of int";
    }
    public boolean matchAnswerLength(String voiceString){
        String[] answerTexts = homework.get(order).getAnswerText().split("[,，]");
        if(answerTexts.length==1 && answerTexts[0].equals(""))
            return true;
        if(!getAnswerText().matches("[a-zA-Z ]*")) {
            for (int index = 0; index < answerTexts.length; index++)
                if (answerTexts[index].length() == voiceString.length())
                    return true;
        }
        else{
            if(!voiceString.matches("[a-zA-Z ]*"))
                return false;
            for (int index = 0; index < answerTexts.length; index++) {
                if(answerTexts[index].split("[ ]").length==voiceString.split("[ ]").length)
                    return true;
            }
        }
        return false;
    }

    public int getNextAnswerLength(){
        return  (homeworkState==0)?homework.get(order).getAnswerText().length():homework.get(inCorProbs.get(order)).getAnswerText().length();
    }
    public String getAllAnswerText(){
        return homework.get(order).getAnswerText();
    }
    public String getAnswerText(){
        String rawAnswer = (homeworkState==0)?homework.get(order).getAnswerText():homework.get(inCorProbs.get(order)).getAnswerText();
        return rawAnswer.split("[,，]")[0];
    }
    public String getExplainText(){
        return (homeworkState==0)?homework.get(order).getExplainText():homework.get(inCorProbs.get(order)).getExplainText();
    }

    public int getInCorNum(){
        return inCorProbs.size();
    }
    public String getNextProbVoice(){
        if(homeworkState==0) {
            if (order >= problemCount)
                return null;
            return homework.get(order).getGuideText();
        }
        else if(homeworkState==1) {
            if (order >= inCorProbs.size())
                return null;
            return homework.get(inCorProbs.get(order)).getGuideText();
        }
        else
            return "out of int";
    }

    public boolean voiceJudgeAnswer(String answerString){
        if(order>=problemCount)
            return true;
        EnglishDemo.print("cnt problem " + homework.get(order).getGuideText());
        if(!homework.get(order).hasAnswer()) {
            EnglishDemo.print("no answer " + homework.get(order).getGuideText());
            return true;
        }
        if(homeworkState==0)
            normalResTime[order] = System.currentTimeMillis() - normalResTime[order];
        else if(homeworkState==1)
            correctingResTime[order][correctingWrongTime[order]] =
                    System.currentTimeMillis() - correctingResTime[order][correctingWrongTime[order]];

        if(judgeAnswer(order, answerString ,this.subOrder)) {
            return true;
        }
        else {
            wrongCount++;
            normalWrongTime[order]++;
            return false;
        }
    }
    public boolean jmpToNextQuestion(){
        boolean nextFlag = false;
        EnglishDemo.print("3 times wrong");
        if (homework.get(order).getSubTermCount() > (this.subOrder + 1)) {
            EnglishDemo.print("sub Order is " + this.subOrder);
            this.subOrder++;
        } else if (this.roundOrder + 1 < Constants.ROUND_COUNT && isCopy()) {
            this.roundOrder++;
            this.subOrder = 0;
        } else {
            order++;
            this.subOrder = 0;
            this.roundOrder = 0;
            nextFlag = true;
        }
        return nextFlag;
    }

    public void judgeProcessEnd(boolean result){
        try {

            if (result) {
                if (homework.get(order).getSubTermCount() > (this.subOrder + 1)) {
                    EnglishDemo.print("sub Order is " + this.subOrder);
                    this.subOrder++;
                } else if (this.roundOrder + 1 < Constants.ROUND_COUNT && isCopy()) {
                    this.roundOrder++;
                    this.subOrder = 0;
                } else {
                    order++;
                    this.subOrder = 0;
                    this.roundOrder = 0;
                    nextFlag = true;
                }
            }
        }
        catch (Exception e){
            EnglishDemo.print("qiguaizheliyouerrrrrrrrrrrrrrrrrrrrrrrr ");
        }
    }

    public void judgeProcessEnd(boolean result,int incorrectTime){
        try {
            if (result) {
                if (homework.get(order).getSubTermCount() > (this.subOrder + 1)) {
                    EnglishDemo.print("sub Order is " + this.subOrder);
                    this.subOrder++;
                } else if (this.roundOrder + 1 < Constants.ROUND_COUNT && isCopy()) {
                    this.roundOrder++;
                    this.subOrder = 0;
                } else {
                    order++;
                    this.subOrder = 0;
                    this.roundOrder = 0;
                    nextFlag = true;
                }
            }
            else if(incorrectTime>=3)
                jmpToNextQuestion();
        }
        catch (Exception e){
            EnglishDemo.print("qiguaizheliyouerrrrrrrrrrrrrrrrrrrrrrrr ");
        }
    }

    public int getCntOrder(){
        return this.order;
    }

    public boolean judgeAnswer(int order, String answerString,int answerIndex){
        if(resultJudgeListener.judgeAnswerFromFrag(answerString,homework.get(order).getAnswerText()))
            return true;

        if(subjectType== Constants.CONSTANT_MATH) {
            if(answerString.equals(Constants.MAGIC_ANSEWER)) return true;
            return UsrIntentTools.vagueAnswerJudgeMa(answerString,homework.get(order).getAnswerText());
        }
        else if(subjectType== Constants.CONSTANT_CHINESE) {
            //REMAINING 每种提醒的答案判别方式不同
            //          需要考虑架构进行重用
            System.out.println("subterm count is "+homework.get(order).getSubTermCount());
            //非朗读课文时的回答判定
            //TODO refactor to local
            if(!homework.get(order).getKnowledgeType().equals(Constants.READ_TEXT)) {
                if (homework.get(order).getSubTermCount() != 1) {
                    EnglishDemo.print("in not read text sub term not 1 "+homework.get(order).getAnswerText());
                    String[] answers = homework.get(order).getAnswerText().split("[- －]");
                    String[] subAnswers = answers[answerIndex].split("[,， ]");
                    for (int index = 0; index < subAnswers.length; index++) {
                        EnglishDemo.print("To Judge "+answerString+" "+subAnswers[index]);
                        if(subAnswers[index].contains(answerString))
                            return true;
                        //if(answerString.contains(HomeworkRectifor.getAllPinyin(subAnswers[index])))
                        //    return true;
                        if (UsrIntentTools.pinyinMatches(answerString, subAnswers[index]))
                            return true;
                    }
                } else {
                    String[] answers = homework.get(order).getAnswerText().split("[，,]");
                    System.out.println("qus: " + homework.get(order).getQuestionText() + " " + homework.get(order).getAnswerText() + " " + answerString);
                    if (answerString.contains(answers[0])) return true;
                    if (UsrIntentTools.pinyinMatches(answerString, answers[0])) return true;
                    for (int index = 0; index < answers.length; index++) {
                        if (answerString.contains(answers[index]) ||
                                UsrIntentTools.pinyinAllMatches(answerString,answers[index])) {
                            EnglishDemo.print("true "+ homework.get(order).getAnswerText() + " " + answerString);
                            return true;
                        }
                    }
                }
                EnglishDemo.print("false "+ homework.get(order).getAnswerText() + " " + answerString);
                return false;
            }
            else{
                int conformCount = 1;
                String answerText =homework.get(order).getAnswerText();
                for(int i=0;i<answerText.length();i++){
                    for(int j=0;j<answerString.length();j++) {
                        if (answerString.charAt(j) == answerText.charAt(i)) {
                            conformCount++;
                            break;
                        }
                    }
                }
                return ((answerText.length()/conformCount)<=1);
            }
        }
        else if(subjectType== Constants.CONSTANT_ENGLISH) {
            if(homework.get(order).getAnswerText().length()==0) {
                Log.d("TEST","here get 0 length right "+homework.get(order).getAnswerText()+" "+homework.get(order).getQuestionText());
                return true;
            }
            EnglishActivity.print("not 0 length " + homework.get(order).getAnswerText() + "_" + "_" + answerString + "_" + homework.get(order).getQuestionText());
            String[] answers = homework.get(order).getAnswerText().split("[,，]");
            for(int index=0;index<answers.length;index++)
                if (answers[index].toLowerCase().equals(answerString.toLowerCase())
                        ||answerString.toLowerCase().contains(answers[index].toLowerCase()))
                    return true;
        }
        return false;
    }

}
