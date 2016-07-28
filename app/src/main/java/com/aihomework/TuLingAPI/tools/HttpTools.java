package com.aihomework.TuLingAPI.tools;

import com.aihomework.TuLingAPI.bean.AnswerBean;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/***
 * call tuling123 api
 * 
 * @author low
 *
 */
public class HttpTools {


	/***
	 * call it to get tuling123 answer
	 * @param message
	 * @param sessionID
	 * @return
	 */
	public static AnswerBean getAnswer(String message, String sessionID){
		String requestURL = "http://www.tuling123.com/openapi/api?key=027e7f60fd014599c052aaf4b731ae75";
		return AnswerBeanFilter.textFilter(message, sessionID, requestURL);
	}


	public static String httpGet(String requestURL, String message){
		StringBuilder response = new StringBuilder();
		try {
			URL url = new URL(requestURL);

			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setDoOutput(true);
			urlConnection.setDoInput(true);
			urlConnection.setRequestMethod("GET");
			urlConnection.setRequestProperty("content-type", "application/x-www-form-urlencoded");
			OutputStreamWriter out = new OutputStreamWriter(urlConnection.getOutputStream());
			out.write(message);
			out.flush();
			out.close();

			BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
			String line;

			while ((line = reader.readLine()) != null){
				response.append(line);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return response.toString();
	}

}
