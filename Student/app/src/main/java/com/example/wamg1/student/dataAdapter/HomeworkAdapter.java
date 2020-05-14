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
import com.example.wamg1.student.httpOperate.HomeworkListData;

import java.util.List;

public class HomeworkAdapter extends RecyclerView.Adapter<HomeworkAdapter.ViewHolder> {

    private Context mContext;
    private List<HomeworkListData> homeworkList;




    static class ViewHolder extends RecyclerView.ViewHolder{
        CardView cardView;
        TextView textTitle;
        TextView textState;
        TextView textScore;

        public ViewHolder(View view){
            super(view);
            cardView=(CardView)view;
            textTitle=view.findViewById(R.id.text_homework_title);
            textState=view.findViewById(R.id.text_yijiao);
            textScore=view.findViewById(R.id.text_weijiao);
        }
    }
    public HomeworkAdapter(List<HomeworkListData> homeworkList){
        this.homeworkList=homeworkList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(mContext ==null){
            mContext=parent.getContext();
        }
        View view= LayoutInflater.from(mContext).inflate(R.layout.homework_title_item,parent,false);

        //return new ViewHolder(view);
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
        HomeworkListData mHomeworkListData=homeworkList.get(position);
        holder.textTitle.setText( mHomeworkListData.getTitle());

        if(!mHomeworkListData.getSub()){
            holder.textState.setText("未交");
            holder.textScore.setText(null);
            holder.textState.setTextColor(Color.RED);
        }else{
            holder.textState.setTextColor(Color.BLACK);
            holder.textState.setText("已交");
            if(mHomeworkListData.getScore()==null){
                holder.textScore.setText("未阅");
            }else{
                holder.textScore.setText(("分数:"+mHomeworkListData.getScore()));

            }
        }


    }

    @Override
    public int getItemCount() {
        return homeworkList.size();
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
        homeworkList.remove(pos);
        notifyItemRemoved(pos);
    }


}
