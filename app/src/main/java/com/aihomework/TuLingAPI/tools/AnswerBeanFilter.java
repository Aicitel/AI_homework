/***
 * Flit some inharmony answer
 */
package com.aihomework.TuLingAPI.tools;

import android.content.Context;

import com.aihomework.TuLingAPI.bean.AnswerBean;
import com.aihomework.constants.Constants;
import com.aihomework.userAction.UsrIntentTools;
import com.google.gson.Gson;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class AnswerBeanFilter {

	private static Properties properties = new Properties();

	//TODO monitor context
	private static Map<String, Map<String, String>> contextMap = new HashMap<String, Map<String, String>>();
	private static Map<String, String> innerContextMap = new HashMap<String, String>();
	private static Map<String, String> interruptMap = new HashMap<String, String>();
	static{

		//properties = loadSessionFromPropeties();

		innerContextMap.put("我想先休息一会", "好的，一会我们接着开始");
		innerContextMap.put("我想干完。。。再做", "不可以，需要先完成作业");
		innerContextMap.put("就是不想做", "那我只有把这个事告诉你的老师了");
		innerContextMap.put("不想理你", "对不起我只是想作为你的好朋友");
		innerContextMap.put("可不可以等会再做", "可以");

		contextMap.put("我不想学习", innerContextMap);
		contextMap.put("我不想做作业", innerContextMap);

		interruptMap.put("我想学习", "你想学数学语文还是英语?");
		interruptMap.put("我不想做作业了", "今天的作业并不多，做完老师会夸奖我们，不然要受批评了");
		interruptMap.put("我不想学习", "我陪你一起学习，让学习变得快乐一点");
		interruptMap.put("我什么都不想干", "可是我们要完成老师布置的作业呀");
		interruptMap.put("可不可以不做了", "不可以，我们需要完成老师布置的作业");
		interruptMap.put("可不可以等会再做", "不可以，我们需要完成老师布置的作业");
		interruptMap.put("不想理你了", "不理我，咱们也要先写完作业");
		interruptMap.put("真的不想做了", "我们必须完成老师布置的作业");
		interruptMap.put("我的本子呢", "先完成作业再找你的本子");
		interruptMap.put("我的书呢", "先完成作业再找你的书");
		interruptMap.put("我先找下作业", "不用找啦，都在我的脑子里");
		interruptMap.put("可不可以先做下一个", "你再努力想一下，是在不行就做下一题吧");
		interruptMap.put("这个好难读", "别放弃，多读两遍就会记住了");
		interruptMap.put("我还是不会", "没关系，我们以后经常复习");
		interruptMap.put("这个好难呀", "嗯，不过只要努力，都可以掌握的");
		interruptMap.put("这个简单", "那你加油呀");
		interruptMap.put("我想再休息一会", "好的");
		interruptMap.put("再等一会", "好的");
	}

	//Flit answer text according it's message
	public static AnswerBean textFilter(String message, String sessionID, String requestURL){
		AnswerBean answerBean = new AnswerBean();
		answerBean.setMessage(message);
		answerBean.setStatusCode("200");

		//if context exits, get context
		if(message.contains("不会")){
			answerBean.setStatusCode("200");
			answerBean.setText("再仔细想想");
		}else if(message.contains("想")||message.contains("做")||message.contains("数学")&&!message.contains("不")){
			answerBean.setStatusCode("300");
			answerBean.setText("好的那我们去做数学!");
        }else if(message.contains("想")||message.contains("做")||message.contains("语文")&&!message.contains("不")){
            answerBean.setStatusCode("301");
            answerBean.setText("好的那我们去做语文!");
        }else if(message.contains("想")||message.contains("做")||message.contains("英语")&&!message.contains("不")){
            answerBean.setStatusCode("302");
            answerBean.setText("好的那我们去做英语!");
		}else if(message.contains("不想做")||message.contains("不想学")||message.contains("不想干")){
            answerBean.setStatusCode("400");
            answerBean.setText("哈哈没关系，我来帮你！我们先把数学解决了");
        }else if(UsrIntentTools.passiveInputJudge(message)){
            answerBean.setStatusCode("400");
            answerBean.setText("哈哈有我在不怕！来，我们先去解决数学作业！");
        }
        else{
            if(!Constants.tulingUsed){
                answerBean.setText(UsrIntentTools.getOffStdResStr());
            }
            else {
                if (properties.containsKey(sessionID) && contextMap.containsKey(properties.getProperty(sessionID)) && contextMap.get(properties.getProperty(sessionID)).containsKey(message)) {
                    answerBean.setText(contextMap.get(properties.getProperty(sessionID)).get(message));
                } else if (interruptMap.containsKey(message)) {
                    answerBean.setText(interruptMap.get(message));
                } else {
                    String response = HttpTools.httpGet(requestURL, "info=" + message);
                    Gson gson = new Gson();
                    answerBean = gson.fromJson(response, AnswerBean.class);
                    answerBean.setMessage(message);
                }
                //savaSessionToPropeties(sessionID, message);
            }
        }
		return answerBean;
	}

	public static Properties loadSessionFromPropeties(Context context) {
        InputStreamReader in;
		try {
			//in = AnswerBeanFilter.class.getResourceAsStream("/historySession.properties");
			in = new InputStreamReader(context.getResources().getAssets().open("historySession.properties"));
			properties.load(in);
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return properties;
	}

	public static void savaSessionToPropeties(String sessionID, String message) {
		properties.setProperty(sessionID, message);
		try {
			OutputStream oFile = new FileOutputStream("src/historySession.properties", true);//true表示追加打开
			properties.store(oFile, null);
			oFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}