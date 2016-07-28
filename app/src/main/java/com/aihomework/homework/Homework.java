package com.aihomework.homework;

/**
 * Created by bluemaple on 2016/3/4.
 */
public class Homework {
    int subjectType;
    int subTermCount=1;
    int questionType=-1;
    int wordLength;
    int stemType =-1;
    String choices;
    String stemText;
    String knowledgeType;
    String status;
    String guideText;
    String questionText;
    String answerText;
    String explainText;
    String wrongText;
    String reviewText;
    int showupTimes;
    int lastShowupTime;
    public void setSubTermCount(int subTermCount){this.subTermCount = subTermCount;}
    public int getSubTermCount(){return this.subTermCount;}
    public void setStemType(int stemType){this.stemType = stemType;}
    public int getStemType(){return this.stemType;}
    public void setWordLength(int wordLength){this.wordLength = wordLength;}
    public int getWordLength(){return this.wordLength;}
    public void setChoices(String choices){
        this.choices = choices;
    }
    public String[] getChoices(){return this.choices.split("[-]");}
    public void setStemText(String stemText){this.stemText = stemText;}
    public String getStemText(){return this.stemText;}
    public void setQuestionType(int questionType){
        this.questionType = questionType;
    }
    public int getQuestionType(){return this.questionType;}
    public void setStatus(String status){this.status = status;}
    public String getStatus(){return this.status;}
    public boolean isSpec(){return this.status!=null;}
    public void setSubjectType(int subjectType){
        this.subjectType = subjectType;
    }
    public int getSubjectType(){
        return this.subjectType;
    }
    public void setKnowledgeType(String knowledgeType){
        this.knowledgeType = knowledgeType;
    }
    public String getKnowledgeType(){
        return this.knowledgeType;
    }
    public boolean isLogic(){return this.knowledgeType.equals("1");}
    public void setGuideText(String guideText){
        this.guideText = guideText;
    }
    public String getGuideText(){
        return this.guideText;
    }
    public void setQuestionText(String questionText){
        this.questionText = questionText;
    }
    public String getQuestionText(){
        return this.questionText;
    }
    public void setAnswerText(String answerText){
        this.answerText = answerText;
    }
    public String getAnswerText(){
        return this.answerText;
    }
    public void setExplainText(String explainText){
        this.explainText = explainText;
    }
    public boolean hasAnswer(){return this.answerText.length()!=0;}
    public String getExplainText(){
        return this.explainText;
    }
    public void setWrongText(String wrongText){
        this.wrongText = wrongText;
    }
    public String getWrongText(){
        return this.wrongText;
    }
    public void setReviewText(String reviewText){this.reviewText = reviewText;}
    public String getReviewText(){return this.reviewText;}
    public void setShowupTimes(int showupTimes){
        this.showupTimes = showupTimes;
    }
    public int getShowupTimes(){
        return this.showupTimes;
    }
    public void setLastShowupTime(int lastShowupTime){
        this.lastShowupTime = lastShowupTime;
    }
    public int getLastShowupTime(){
        return this.lastShowupTime;
    }


}
