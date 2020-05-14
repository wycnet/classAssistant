package com.example.wamg1.teacher.dataAdapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.example.wamg1.teacher.QuestionAnsowerActivity;
import com.example.wamg1.teacher.R;
import com.example.wamg1.teacher.bean.Question;
import com.example.wamg1.teacher.bean.Talk;
import com.example.wamg1.teacher.httpOperate.QuestionGSONData;
import com.example.wamg1.teacher.logTest.LogUtil;
import com.google.gson.Gson;


import java.util.List;

public class TalkAdapter extends RecyclerView.Adapter<TalkAdapter.ViewHolder> {
    private Context mContext;

    private List<Talk> talkList;

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView textName;
        TextView textTime;
        TextView textTalk;

        public ViewHolder(View view){
            super(view);
            textName=view.findViewById(R.id.text_name);
            textTalk=view.findViewById(R.id.text_talk);
            textTime=view.findViewById(R.id.text_time);

        }
    }

    public TalkAdapter(List<Talk> talkList){
        this.talkList=talkList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(mContext==null){
            mContext=parent.getContext();
        }
        View view= LayoutInflater.from(mContext).inflate(R.layout.talk_item,parent,false);


        // 添加点击监听功能
        final ViewHolder holder=new ViewHolder(view);

        if(mOnItemOnClickListener!=null){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemOnClickListener.onItemOnClick(holder.itemView,holder.getAdapterPosition());
                }
            });
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mOnItemOnClickListener.onItemLongOnClick(holder.itemView,holder.getAdapterPosition());
                    return false;
                }
            });
        }

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Talk talk=talkList.get(position);
        if(talk.getUser()!=null){
            holder.textName.setText(talk.getUser().getName());
        }
        holder.textTime.setText(talk.getCreatedAt());
        holder.textTalk.setText(talk.getTalk());

    }

    @Override
    public int getItemCount() {
       return talkList.size();
    }



    //添加接口
    public interface OnItemOnClickListener{
        void onItemOnClick(View view,int pos);
        void onItemLongOnClick(View view ,int pos);
    }

    private OnItemOnClickListener mOnItemOnClickListener;
    //此方法给外部调
    public void setOnItemClickListener(OnItemOnClickListener listener){
        this.mOnItemOnClickListener = listener;
    }

    //添加删除item的方法
    public void removeItem(int pos){
        talkList.remove(pos);
        notifyItemRemoved(pos);
    }

}