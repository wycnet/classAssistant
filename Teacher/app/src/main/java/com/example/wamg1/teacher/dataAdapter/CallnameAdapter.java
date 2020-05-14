package com.example.wamg1.teacher.dataAdapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.wamg1.teacher.R;
import com.example.wamg1.teacher.httpOperate.CallnameDada;


import java.util.List;

public class CallnameAdapter extends RecyclerView.Adapter<CallnameAdapter.ViewHolder> {

    private Context mContext;
    private List<CallnameDada> callnameList;




    static class ViewHolder extends RecyclerView.ViewHolder{
        CardView cardView;
        TextView textCuqing;
        TextView textKuangke;
        TextView textQingjia;
        TextView textCidao;
        TextView textCreateTime;

        public ViewHolder(View view){
            super(view);
            cardView=(CardView) view;
            textCuqing=view.findViewById(R.id.text_cuqing);
            textKuangke=view.findViewById(R.id.text_kuangkedd);
            textCidao=view.findViewById(R.id.text_cidao);
            textQingjia=view.findViewById(R.id.text_qingjia);
            textCreateTime=view.findViewById(R.id.text_notice_title);

        }
    }

    public CallnameAdapter(List<CallnameDada> callnameDadas){
        callnameList=callnameDadas;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(mContext ==null){
            mContext=parent.getContext();
        }
        View view= LayoutInflater.from(mContext).inflate(R.layout.call_name_form_item,parent,false);

        //return new ViewHolder(view);
        final ViewHolder holder=new ViewHolder(view);
//        holder.cardView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                int position=holder.getAdapterPosition();
//                CallnameDada callnameDada=callnameList.get(position);
//                Intent intent=new Intent(mContext, CallnameFormActivity.class);
//                intent.putExtra("times",callnameDada.getCreateTime());
//                mContext.startActivity(intent);
//            }
//        });

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
        CallnameDada mCallnameDada=callnameList.get(position);
        holder.textCuqing.setText(mCallnameDada.getCuqing());
        holder.textKuangke.setText(mCallnameDada.getKuangke());
        holder.textQingjia.setText(mCallnameDada.getQingjia());
        holder.textCidao.setText(mCallnameDada.getCidao());
        holder.textCreateTime.setText(mCallnameDada.getCreateTime());

    }

    @Override
    public int getItemCount() {
        return callnameList.size();
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
        callnameList.remove(pos);
        notifyItemRemoved(pos);
    }


}
