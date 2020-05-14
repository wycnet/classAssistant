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
import com.example.wamg1.teacher.httpOperate.QuestionGSONData;
import com.example.wamg1.teacher.logTest.LogUtil;
import com.google.gson.Gson;


import java.util.List;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.ViewHolder> {
    private Context mContext;

    private List<Question> questionsList;

    static class ViewHolder extends RecyclerView.ViewHolder{
        CardView cardView;
        TextView questionTitle;
        TextView questionDay;
        QuestionGSONData questionGSONData;

        public ViewHolder(View view){
            super(view);
            cardView=(CardView)view;
            questionTitle=view.findViewById(R.id.qusetion_title);
            questionDay=view.findViewById(R.id.question_day);
        }
    }

    public QuestionAdapter(List<Question> questionsList){
        this.questionsList=questionsList;
    }

    @NonNull
    @Override
    public QuestionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(mContext==null){
            mContext=parent.getContext();
        }
        View view= LayoutInflater.from(mContext).inflate(R.layout.question_item,parent,false);

      //  return new ViewHolder(view);

       // 添加点击监听功能
         final QuestionAdapter.ViewHolder holder=new QuestionAdapter.ViewHolder(view);



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
    public void onBindViewHolder(@NonNull QuestionAdapter.ViewHolder holder, int position) {
        Question question=questionsList.get(position);
        String qFile=question.getqFile();
        LogUtil.e("aa",qFile);
        holder.questionGSONData=parseJson(qFile);
        holder.questionTitle.setText("题目："+holder.questionGSONData.getTitle());
        holder.questionDay.setText(question.getCreatedAt());
        LogUtil.d("QuestionAdapter", "onBindViewHolder: "+holder.questionGSONData.getTitle()+" "+question.getCreatedAt());
    }

    @Override
    public int getItemCount() {
        return questionsList.size();
    }

    private QuestionGSONData parseJson(String jsonData){
        Gson gson=new Gson();
        QuestionGSONData question=gson.fromJson(jsonData,QuestionGSONData.class);
        return question;
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
        questionsList.remove(pos);
        notifyItemRemoved(pos);
    }

}