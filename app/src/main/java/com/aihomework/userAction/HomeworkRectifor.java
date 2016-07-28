package com.aihomework.userAction;

import net.sourceforge.pinyin4j.PinyinHelper;

public class HomeworkRectifor {
    static public String mathRectify(String rawString){
        return rawString.replace(" ","").replace('\\','1').replace('|','1').replace('l','1').replace('T','7');
    }

    static public String getPinyin(String rawString){
        if((rawString.charAt(0)>='A'&&rawString.charAt(0)<='Z')||(rawString.charAt(0)>='a'&&rawString.charAt(0)<='z')) {
            return rawString;
        }
        else{
            String resultString ="";
            for(char cntChar:rawString.toCharArray())
                resultString+=PinyinHelper.toHanyuPinyinStringArray(cntChar)[0];
            return resultString;
        }
    }

    /**
     * 单个汉字所有拼音
     * @param rawString
     * @return
     */
    static public String getAllPinyin(String rawString){
        String[] results = PinyinHelper.toHanyuPinyinStringArray(rawString.toCharArray()[0]);
        String result = results[0];
        for(String cntPinyin:results)
            result+=(","+cntPinyin);
        return result;
    }
}
