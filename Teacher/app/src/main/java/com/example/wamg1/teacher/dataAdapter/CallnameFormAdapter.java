package com.example.wamg1.teacher.dataAdapter;


import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.wamg1.teacher.CallnameFormActivity;
import com.example.wamg1.teacher.R;
import com.example.wamg1.teacher.bean.CallName;

import java.util.List;

public class CallnameFormAdapter extends RecyclerView.Adapter<CallnameFormAdapter.ViewHolder> {

    private List<CallName> callNameList;
    private CallnameFormActivity mContext;


    class ViewHolder extends RecyclerView.ViewHolder{
        TextView textName;
        TextView textSno;
        RadioGroup radioGroup;
        RadioButton radioCuqing;
        RadioButton radioKuangke;
        RadioButton radioQingjia;
        RadioButton radioCidao;

        public ViewHolder(View view){
            super(view);
            textName=view.findViewById(R.id.text_name);
            textSno=view.findViewById(R.id.text_sno);
            radioGroup=view.findViewById(R.id.button_group);
            radioCuqing=view.findViewById(R.id.radioButton_cuqing);
            radioKuangke=view.findViewById(R.id.radioButton_kuangke);
            radioQingjia=view.findViewById(R.id.radioButton_qingjia);
            radioCidao=view.findViewById(R.id.radioButton_cidao);
        }


    }

    public CallnameFormAdapter(CallnameFormActivity mContext,List<CallName> callNameList){
        this.callNameList=callNameList;
        this.mContext=mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        final View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.call_name_item,parent,false);
        final ViewHolder holder=new ViewHolder(view);


        holder.radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(!((RadioButton)view.findViewById(checkedId)).isPressed())return; //没有这个的话设置初始值也会触发
                int mPostion=holder.getAdapterPosition();
                if(checkedId==R.id.radioButton_cuqing){   //状态，1到2没到3请假4迟到
                    mContext.updateCallname(mPostion,1);
                }else if(checkedId==R.id.radioButton_qingjia){
                    mContext.updateCallname(mPostion,3);
                }else if(checkedId==R.id.radioButton_kuangke){
                    mContext.updateCallname(mPostion,2);
                }else if(checkedId==R.id.radioButton_cidao){
                    mContext.updateCallname(mPostion,4);
                }
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CallName mCallName=callNameList.get(position);
        holder.textSno.setText(mCallName.getsNo().getUsername());
        //holder.textName.setText(("王小二"+(mCallName.getsNo().getUsername().substring(5))));
        holder.textName.setText(mCallName.getsNo().getName());
        if(mCallName.getState().equals("0")||mCallName.getState().equals("1")){
           // holder.radioGroup.check(R.id.radioButton_cuqing);  //状态，1到2没到3请假4迟到
            holder.radioCuqing.setChecked(true);
        }else if(mCallName.getState().equals("2")){
            //holder.radioGroup.check(R.id.radioButton_kuangke);
            holder.radioKuangke.setChecked(true);
        }else if(mCallName.getState().equals("3")){
            //holder.radioGroup.check(R.id.radioButton_qingjia);
            holder.radioQingjia.setChecked(true);
        }else if(mCallName.getState().equals("4")){
            //holder.radioGroup.check(R.id.radioButton_cidao);
            holder.radioCidao.setChecked(true);
        }


    }

    @Override
    public int getItemCount() {
        return callNameList.size();
    }






}
