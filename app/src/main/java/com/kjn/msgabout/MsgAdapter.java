package com.kjn.msgabout;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.brick.robotctrl.R;

import java.util.List;

/**
 * Created by kjnijk on 2016-09-04.
 */
  public class MsgAdapter extends ArrayAdapter<Msg> {
                private int resourceId;
                public MsgAdapter(Context context , int textViewResourceId, List<Msg> objects){
                    super(context,textViewResourceId,objects);
                    resourceId=textViewResourceId;
                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    Msg msg=getItem(position);
                    View view;
                    ViewHolder viewHolder;
                    if(convertView==null){
                        view= LayoutInflater.from(getContext()).inflate(resourceId,null);
                        viewHolder=new ViewHolder();
                        viewHolder.leftLayout=(LinearLayout)view.findViewById(R.id.left_layout);
                        viewHolder.rightLayout=(LinearLayout)view.findViewById(R.id.right_layout);
                        viewHolder.leftMsg=(TextView)view.findViewById(R.id.left_msg);
                        viewHolder.rightMsg=(TextView)view.findViewById(R.id.right_msg);
                        viewHolder.headRobot=(ImageView)view.findViewById(R.id.head_robot);
                        viewHolder.headMan=(ImageView)view.findViewById(R.id.head_man);
                        view.setTag(viewHolder);
        }
        else{
            view=convertView;
            viewHolder=(ViewHolder)view.getTag();
        }
        if(msg.getType()== Msg.TYPE_RECEIVED){
            viewHolder.leftLayout.setVisibility(View.VISIBLE);
            viewHolder.rightLayout.setVisibility(View.GONE);
            viewHolder.headRobot.setImageResource(msg.getImageId());
            viewHolder.leftMsg.setText(msg.getContent());
        }else if(msg.getType()== Msg.TYPE_SEND){
            viewHolder.leftLayout.setVisibility(View.GONE);
            viewHolder.rightLayout.setVisibility(View.VISIBLE);
            viewHolder.headMan.setImageResource(msg.getImageId());
            viewHolder.rightMsg.setText(msg.getContent());
        }
        return view;
    }
    class ViewHolder{
        LinearLayout leftLayout;
        LinearLayout rightLayout;
        TextView leftMsg;
        TextView rightMsg;
        ImageView headMan;
        ImageView headRobot;
    }
}
