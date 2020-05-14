package com.example.wamg1.teacher.dataAdapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.wamg1.teacher.R;
import com.example.wamg1.teacher.bean.Notice;

import java.util.List;

public class NoticeAdapter extends RecyclerView.Adapter<NoticeAdapter.ViewHolder> {
    private List<Notice> noticeList;
    private Context mContext;
    class ViewHolder extends RecyclerView.ViewHolder{

        TextView textNoticeTitle;
        TextView textNotice;
        TextView textTime;
        public ViewHolder(View view){
            super(view);
            textNotice=view.findViewById(R.id.text_notice);
            textNoticeTitle=view.findViewById(R.id.text_notice_title);
            textTime=view.findViewById(R.id.text_time);

        }
    }

    public NoticeAdapter(List<Notice> noticeList){
        this.noticeList=noticeList;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(mContext==null){
            mContext=parent.getContext();
        }
        View view= LayoutInflater.from(mContext).inflate(R.layout.notice_item,parent,false);
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
        Notice notice=noticeList.get(position);

        holder.textNoticeTitle.setText(notice.getTitle());
        holder.textNotice.setText(notice.getNotice());
        holder.textTime.setText(notice.getCreatedAt());

    }

    @Override
    public int getItemCount() {
        return noticeList.size();
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
        noticeList.remove(pos);
        notifyItemRemoved(pos);
    }



}
