package com.aihomework.userAction;

import com.aihomework.voicedemo.EnglishDemo;

import net.sourceforge.pinyin4j.PinyinHelper;

import java.util.ArrayList;
import java.util.Random;

public class UsrIntentTools {
    public final static String[] numberPinyin = {"yi","er","san","si","wu","liu","qi","ba","jiu","shi","ling"};
    public final static String[] reviewKeywords = {"会了","懂了","下一题"};
    public final static String[] unregKeywords = {"不会","再说一遍","再讲一遍","不懂"};
    public final static String[] offStdResStrings = {"别开小差啦，快仔细想想","好好把作业做完哦，不然明天会被老师说的","认真一点啦，小朋友"};
    public final static String[] passiveStrings = {"好烦呢","你好烦呢","压力好大","好累呀","好无聊","好没劲"};
    public final static String[] breakStrings = {"我想再休息一会","再休息一会","再等一会","再等我一会","我还有点事"};
    public final static String[] normalResStrings = {"恩在的","嗯在的","干嘛","叫我干嘛","干吗","干什么"};
    public final static String[] intentStrings = {"好，那我们做数学吧","好，那我们做语文吧","好，那我们做英语吧","好，"};

    public static int similarJudge(String standardAnswer,String userAnswer){
        if(standardAnswer.length()!=userAnswer.length())
            return userAnswer.length()+1;
        int difPos = 0;
        for(int i =0;i<standardAnswer.length();i++){
            if(standardAnswer.charAt(i)!=userAnswer.charAt(i))
                difPos+=i;
        }
        return difPos;
    }

    public static boolean normalResJudge(String inputString){
        for(String normalString:normalResStrings)
            if(inputString.contains(normalString))
                return true;
        return false;
    }

    public static boolean breakReqJudge(String inputString){
        for(String breakString:breakStrings)
            if(inputString.contains(breakString))
                return true;
        if(inputString.matches(".*((休息)|(等一会)).*"))
            return true;
        return false;
    }

    public static boolean passiveInputJudge(String inputString){
        for(String passiveString:passiveStrings)
            if(inputString.contains(passiveString))
                return true;
        return false;
    }

    public static String getOffStdResStr(){
        return offStdResStrings[Math.abs((new Random()).nextInt())%(offStdResStrings.length-1)];
    }
    public static String getPinyin(String answer){
        if(answer.matches("[a-z]*"))
            return answer;
        StringBuilder pinyin = new StringBuilder();
        for(int i=0;i<answer.length();i++) {
            pinyin.append(PinyinHelper.toHanyuPinyinStringArray(answer.charAt(i))[0]);
            pinyin.append(" ");
        }
        return pinyin.toString();
    }

    public static int reviewIntentJudge(String rawString){
        for(int index=0;index<reviewKeywords.length;index++)
            if(rawString.contains(reviewKeywords[index]))
                return 0;
        for(int index=0;index<unregKeywords.length;index++)
            if(rawString.contains(unregKeywords[index]))
                return 1;
        return 2;
    }
    public static boolean vagueIntentJudgeCn(String rawString,String tarString){
        String[] tarArray = tarString.split("[,，]");
        for(String tar:tarArray) if(rawString.length()==tar.split(" ").length) return true;
        return false;
    }

    public static boolean vagueIntentJudgeEn(String rawString,String answerString){
        int length = answerString.split("[，,]")[0].split(" ").length;
        return (rawString.split(" ").length==length);
    }

    public static boolean vagueAnswerJudgeEn(String rawString,String tarString){
        String[] tarArray = tarString.split(" ");
        String[] pinyin;
        boolean flag;
        for(int i=0;i<rawString.length();i++){
            flag = false;
            pinyin = PinyinHelper.toHanyuPinyinStringArray(rawString.charAt(i));
            if(pinyin==null)
                return true;
            for(int j=0;j<pinyin.length;j++){
                if(pinyin[j].matches(tarArray[i]+"."))
                    flag = true;
            }
            if(!flag) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断语音输入字符是否为发音相同字符
     * @param voiceString 输入字符串,汉字形式
     * @param tarString 答案拼音串,汉字形式
     * @return boolean
     */
    public static boolean pinyinAllMatches(String voiceString,String tarString){
        if(voiceString==null || voiceString.equals(""))
            return true;
        char[] voiceChars = voiceString.toCharArray();
        ArrayList<String[]> voicePinyinList = new ArrayList<String[]>();
        for(char cnt:voiceChars)
            voicePinyinList.add(PinyinHelper.toHanyuPinyinStringArray(cnt));
        String[] tarPinyin = PinyinHelper.toHanyuPinyinStringArray(tarString.charAt(0));
        //answer string consist of english characters
        if(tarPinyin==null)
            return false;
        for(String tar:tarPinyin)
            EnglishDemo.print("miaomiaomiao? " + tar);
        try {
            for(int index=0;index<voicePinyinList.size();index++){
                for(String cnt:voicePinyinList.get(index))
                    for(String tar:tarPinyin)
                        if(tar.contains(cnt)||cnt.contains(tar))
                            return true;
            }
            return false;
        }
        catch (Exception e){
            //TODO bad construct
            return false;
        }
    }
    /**
     * 判断语音输入字符是否为发音相同字符
     * @param voiceString 输入字符串,汉字形式
     * @param tarString 答案拼音串,拼音形式
     * @return boolean
     */
    public static boolean pinyinMatches(String voiceString,String tarString){
        if(voiceString==null || voiceString.equals(""))
            return true;
        String[] resPinyin;
        //TODO just for debug
        if(voiceString.matches("[int]*"))
            return true;
        else if(voiceString.matches("[a-z]*"))
            return false;
        String[] tarPinyin = tarString.split("[ ]");
        try {
            if (tarPinyin.length == 1) {
                for (int index = 0; index < voiceString.length(); index++) {
                    resPinyin = PinyinHelper.toHanyuPinyinStringArray(voiceString.charAt(index));
                    for (String res : resPinyin) {
                        System.out.println("pinyin " + res + " " + tarString);
                        if (res.contains(tarString)) return true;
                    }
                }
                return false;
            } else {
                boolean flag;
                for (int tarIndex = 0; tarIndex < tarPinyin.length; tarIndex++) {
                    flag = false;
                    tarString = tarPinyin[tarIndex];
                    for (int index = 0; index < voiceString.length(); index++) {
                        resPinyin = PinyinHelper.toHanyuPinyinStringArray(voiceString.charAt(index));
                        for (String res : resPinyin) if (res.contains(tarString)) flag = true;
                    }
                    if (!flag) return false;
                }
                return true;
            }
        }
        catch (Exception e){
            //TODO bad construct
            return false;
        }
    }

    /**
     * 数学学科有且仅有三种情况
     *      1.ascii数字
     *      2.utf汉字数字
     *      3.utf汉字同音非数字
     *      分为{1}与{2，3}两类进行判断
     * @param rawString 回答中的字符串
     * @param tarString 答案中的字符串
     * @return boolean
     */
    public static boolean vagueAnswerJudgeMa(String rawString,String tarString){
        String[] answers = tarString.split("[,， ]");
        if(rawString.contains(tarString))
            return true;
        if(rawString.matches("[1234567890]*")){
            if(rawString.equals(answers[0]))
                return true;
            else
                return false;
        }
        else{
            if(answers.length==1)
                return false;
            String answer = answers[1];
            if(rawString.length()!=answers[1].length())
                return false;
            else{
                String cntRes,cntAns;
                boolean corFlag;
                String[] resPinyin,answerPinyin;
                for(int index = 0;index<rawString.length();index++){
                    corFlag = false;
                    //可能包含多个元素
                    resPinyin = PinyinHelper.toHanyuPinyinStringArray(rawString.charAt(index));
                    //此有且仅有1元素
                    answerPinyin = PinyinHelper.toHanyuPinyinStringArray(answer.charAt(index));
                    //逐一匹配
                    for(int i=0;i<resPinyin.length;i++){
                        cntRes = resPinyin[i].substring(0, resPinyin[i].length()-1);
                        cntAns = answerPinyin[0].substring(0, answerPinyin[0].length()-1);
                        if(cntRes.matches(cntAns))
                            corFlag = true;
                    }
                    if(!corFlag) return false;
                }
                return true;
            }
        }
    }
}
