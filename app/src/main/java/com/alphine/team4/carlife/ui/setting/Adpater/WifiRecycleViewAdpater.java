package com.alphine.team4.carlife.ui.setting.Adpater;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alphine.team4.carlife.R;

import java.util.List;

public class WifiRecycleViewAdpater extends RecyclerView.Adapter<WifiRecycleViewAdpater.MyViewHolder>{
    private List<ScanResult> mDatas;
    Context context;
    private OnItemClickListener mOnItemClickListener;
    private int position;

    public WifiRecycleViewAdpater(Context context,List<ScanResult> data){
        this.context = context;
        mDatas = data;
    }

    @NonNull
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.wifi_list,parent,false);
        MyViewHolder myViewHolder= new MyViewHolder(view);
        return myViewHolder;
    }
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        this.position=position;
        holder.txwifi.setText(mDatas.get(position).SSID);
        int level = mDatas.get(position).level;
        if (level <= 0 && level >= -50) {
            holder.txinfor.setText("信号很好");
        } else if (level < -50 && level >= -70) {
            holder.txinfor.setText("信号较好");
        } else if (level < -70 && level >= -80) {
            holder.txinfor.setText("信号一般");
        } else if (level < -80 && level >= -100) {
            holder.txinfor.setText("信号较差");
        } else {
            holder.txinfor.setText("信号很差");
        }
    }

    @Override
    public int getItemCount() {
        if(mDatas!=null)return mDatas.size();
        return 0;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        //设置一个监听，其实就是要设置一个回调的接口。
        Log.e("TAG", "1+listener:"+listener);
        this.mOnItemClickListener = listener;
    }


    public void setData(List<ScanResult> data){
        this.mDatas=data;
    }

    /**
     * 编写回调的步骤
     * 1.创建这个接口
     * 2.定义接口内部的方法
     * 3.提供设置接口的方法
     * 4.接口方法的调用
     */

    public interface OnItemClickListener{
        void onItemClick(String wifiSSID,String wifitype,int position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView txwifi;
        TextView txinfor;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txwifi = itemView.findViewById(R.id.textView_wifi);
            txinfor = itemView.findViewById(R.id.textView_information);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemClickListener != null){
                        mOnItemClickListener.onItemClick(txwifi.getText().toString(),mDatas.get(position).capabilities,position);
                    }
                }
            });
        }
    }
}
