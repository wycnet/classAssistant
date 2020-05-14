package com.example.wamg1.student.dataAdapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.wamg1.student.R;
import com.example.wamg1.student.bean.Class;

import java.util.List;


public class ClassAapter extends RecyclerView.Adapter<ClassAapter.ViewHolder> {
    private Context mContext;

    private List<Class> mClassList;

    static class ViewHolder extends RecyclerView.ViewHolder{
        CardView cardView;
        TextView className;
        TextView classNo;
        public ViewHolder(View view){
            super(view);
            cardView=(CardView)view;
            className=view.findViewById(R.id.class_name);
            classNo=view.findViewById(R.id.class_no);
        }
    }

    public ClassAapter(List<Class> classList){
        mClassList=classList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(mContext==null){
            mContext=parent.getContext();
        }

        View view= LayoutInflater.from(mContext).inflate(R.layout.card_item,parent,false);

        //return new ViewHolder(view);

        //添加点击监听功能
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
        Class aClass=mClassList.get(position);
        holder.className.setText(aClass.getcName());
        holder.classNo.setText(aClass.getObjectId());
    }

    @Override
    public int getItemCount() {
        return mClassList.size();
    }

    //添加点击监听事件
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
       // homeworkList.remove(pos);
        notifyItemRemoved(pos);
    }
}
