package com.aihomework.tools;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class HttpTools {
    static public String getResponseResult(HttpResponse response)
    {
        if (null == response)
        {
            return "error";
        }

        HttpEntity httpEntity = response.getEntity();
        try
        {
            InputStream inputStream = httpEntity.getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    inputStream));
            String result = "";
            String line = "";
            while (null != (line = reader.readLine()))
            {
                result += line;
            }
            return result;
            //mResult.setText("Response Content from server: " + result);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return "error";
    }
}
