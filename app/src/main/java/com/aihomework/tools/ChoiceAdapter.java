package com.aihomework.tools;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.aihomework.constants.Constants;
import com.aihomework.voicedemo.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * Created by bluemaple on 2016/4/15.
 */
public class ChoiceAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private List<Map<String, Object>> mData;

    public ChoiceAdapter(Context context){
        this.mInflater = LayoutInflater.from(context);
        mData = new ArrayList<Map<String, Object>>();
    }
    public boolean isContentNull(){return mData==null;}
    public void setData(String[] data){
        mData = new ArrayList<Map<String, Object>>();

        for(int i=0;i<data.length;i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("choice", Constants.CHOICE_TITLE[i]);
            map.put("choiceAnswer", data[i]);
            mData.add(map);
        }
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mData.size();
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        if (convertView == null) {

            holder=new ViewHolder();

            convertView = mInflater.inflate(R.layout.choicelist, null);
            holder.choice = (TextView)convertView.findViewById(R.id.choice);
            holder.choiceAnswer = (TextView)convertView.findViewById(R.id.choiceAnswer);
            convertView.setTag(holder);

        }else {

            holder = (ViewHolder)convertView.getTag();
        }


        holder.choice.setText((String)mData.get(position).get("choice"));
        holder.choiceAnswer.setText((String) mData.get(position).get("choiceAnswer"));

        return convertView;
    }

    public final class ViewHolder{
        public TextView choice;
        public TextView choiceAnswer;
    }

}
