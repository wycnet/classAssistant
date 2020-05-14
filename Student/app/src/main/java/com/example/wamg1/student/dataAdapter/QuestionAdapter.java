package com.example.wamg1.student.dataAdapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.wamg1.student.R;
import com.example.wamg1.student.bean.Ansower;
import com.example.wamg1.student.bean.Question;
import com.example.wamg1.student.httpOperate.QuestionGSONData;
import com.example.wamg1.student.logTest.LogUtil;
import com.google.gson.Gson;

import java.util.List;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.ViewHolder> {
    private Context mContext;

   // private List<Question> questionsList;
    private List<Ansower> ansowerList;

    static class ViewHolder extends RecyclerView.ViewHolder{
        CardView cardView;
        TextView questionTitle;
        TextView questionDay;
        TextView questionState;
        TextView questionItem;
        QuestionGSONData questionGSONData;

        public ViewHolder(View view){
            super(view);
            cardView=(CardView)view;
            questionTitle=view.findViewById(R.id.qusetion_title);
            questionDay=view.findViewById(R.id.question_day);
            questionState=view.findViewById(R.id.text_state);
            questionItem=view.findViewById(R.id.text_item);
        }
    }

    public QuestionAdapter(List<Ansower> ansowerList){
        this.ansowerList=ansowerList;
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
        Question question=ansowerList.get(position).getqNo();
        Ansower ansower=ansowerList.get(position);
        String qFile=question.getqFile();
       // LogUtil.e("aa",qFile);
        holder.questionGSONData=parseJson(qFile);
        holder.questionTitle.setText("题目："+holder.questionGSONData.getTitle());
        holder.questionDay.setText(question.getCreatedAt());
        if(ansower.getAnswer()==null || !ansower.getAnswer()){
            holder.questionState.setText("未答");
            holder.questionState.setTextColor(Color.RED);
            holder.questionItem.setText(null);
        }else{
            holder.questionState.setTextColor(Color.BLACK);
            holder.questionState.setText("已选:"+ansower.getOption()+"  答案:"+holder.questionGSONData.getAnsower());
            String item=holder.questionGSONData.getOptionA()+"\n"+holder.questionGSONData.getOptionB()+"\n"+
                    holder.questionGSONData.getOptionC()+"\n"+holder.questionGSONData.getOptionD();
            holder.questionItem.setText(item);
        }
       // LogUtil.d("QuestionAdapter", "onBindViewHolder: "+holder.questionGSONData.getTitle()+" "+question.getCreatedAt());
    }

    @Override
    public int getItemCount() {
        return ansowerList.size();
    }

    private QuestionGSONData parseJson(String jsonData){
        Gson gson=new Gson();
        QuestionGSONData question=gson.fromJson(jsonData,QuestionGSONData.class);
        return question;
    }

    //添加接口
    public interface OnItemOnClickListener{
        void onItemOnClick(View view, int pos);
        void onItemLongOnClick(View view, int pos);
    }

    private OnItemOnClickListener mOnItemOnClickListener;
    //此方法给外部调
    public void setOnItemClickListener(OnItemOnClickListener listener){
        this.mOnItemOnClickListener = listener;
    }

    //添加删除item的方法
    public void removeItem(int pos){
            ansowerList.remove(pos);
        notifyItemRemoved(pos);
    }

}