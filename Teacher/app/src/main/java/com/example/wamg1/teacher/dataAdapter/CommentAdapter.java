package com.example.wamg1.teacher.dataAdapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.wamg1.teacher.R;
import com.example.wamg1.teacher.bean.Comment;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    private Context mContext;

    private List<Comment> commentList;

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

    public CommentAdapter(List<Comment> commentList){
        this.commentList=commentList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(mContext==null){
            mContext=parent.getContext();
        }
        View view= LayoutInflater.from(mContext).inflate(R.layout.talk_item_list,parent,false);


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
        Comment comment=commentList.get(position);
        if(comment.getUser()!=null){
            holder.textName.setText(comment.getUser().getName());
        }
        holder.textTime.setText(comment.getCreatedAt());
        holder.textTalk.setText(comment.getComment());

    }

    @Override
    public int getItemCount() {
        return commentList.size();
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
        commentList.remove(pos);
        notifyItemRemoved(pos);
    }

}