package com.example.wamg1.teacher.dataAdapter;

import android.content.Context;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.wamg1.teacher.R;
import com.example.wamg1.teacher.bean.SubmitWork;

import java.util.List;

public class SubHomeworkListAdapter extends   RecyclerView.Adapter<SubHomeworkListAdapter.ViewHolder> {

    private List<SubmitWork> submitWorkList;
    private Context mContext;

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView sNo;
        TextView sName;
        TextView score;

        public ViewHolder(View itemView) {
            super(itemView);
            sNo=itemView.findViewById(R.id.text_sno);
            sName=itemView.findViewById(R.id.text_sname);
            score=itemView.findViewById(R.id.text_state);

        }
    }

    public SubHomeworkListAdapter(List<SubmitWork> submitWorkList){
        this.submitWorkList=submitWorkList;
    }

    @NonNull
    @Override
    public SubHomeworkListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(mContext==null){
            mContext=parent.getContext();
        }
        View view= LayoutInflater.from(mContext).inflate(R.layout.homeworklist_item,parent,false);


        // 添加点击监听功能
        final SubHomeworkListAdapter.ViewHolder holder=new SubHomeworkListAdapter.ViewHolder(view);

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
    public void onBindViewHolder(@NonNull SubHomeworkListAdapter.ViewHolder holder, int position) {
        SubmitWork submitWork=submitWorkList.get(position);
        holder.sNo.setText(submitWork.getsNo().getUsername());
        holder.sName.setText(submitWork.getsNo().getName());
        if(submitWork.getSub()==null||(!submitWork.getSub())){
            holder.score.setText("未交");
            holder.score.setTextColor(Color.RED);
        }else if(submitWork.getScore()==null || submitWork.getScore().equals("")){
            holder.score.setTextColor(Color.BLUE);
            holder.score.setText(("未改"));
        } else{
            holder.score.setTextColor(Color.BLACK);
            holder.score.setText(("分数:"+submitWork.getScore()));
        }

    }

    @Override
    public int getItemCount() {
        return submitWorkList.size();
    }


    //添加接口
    public interface OnItemOnClickListener{
        void onItemOnClick(View view,int pos);
        void onItemLongOnClick(View view ,int pos);
    }

    private OnItemOnClickListener mOnItemOnClickListener;
    //此方法给外部调
    public void setOnItemClickListener(SubHomeworkListAdapter.OnItemOnClickListener listener){
        this.mOnItemOnClickListener = listener;
    }

    //添加删除item的方法
    public void removeItem(int pos){
        submitWorkList.remove(pos);
        notifyItemRemoved(pos);
    }
}
