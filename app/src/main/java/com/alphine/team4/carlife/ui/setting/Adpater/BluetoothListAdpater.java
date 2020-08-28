package com.alphine.team4.carlife.ui.setting.Adpater;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alphine.team4.carlife.R;

import java.util.List;

public class BluetoothListAdpater extends RecyclerView.Adapter<BluetoothListAdpater.MyViewHolder> {

    private List<BluetoothDevice> mDatas;
    Context context;
    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener mOnLongClickListener;
    private int position;

    public BluetoothListAdpater(Context context, List<BluetoothDevice> data){
        this.context = context;
        mDatas = data;
    }

    @NonNull
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.blue_list,parent,false);
        MyViewHolder myViewHolder= new MyViewHolder(view);
        return myViewHolder;
    }
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        this.position=position;
        holder.txname.setText(mDatas.get(position).getName());
        holder.txaddress.setText(mDatas.get(position).getAddress());
        switch (mDatas.get(position).getBondState()) {
            case BluetoothDevice.BOND_BONDING:
                holder.txstate.setText("正在配对......");
                break;
            case BluetoothDevice.BOND_BONDED:
                holder.txstate.setText("配对完成");
                break;
            case BluetoothDevice.BOND_NONE:
                holder.txstate.setText("取消配对");
            default:
                holder.txstate.setText(" ");
                break;
        }
    }

    @Override
    public int getItemCount() {
        if(mDatas!=null)return mDatas.size();
        return 0;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        //设置一个监听，其实就是要设置一个回调的接口。
        this.mOnItemClickListener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        //设置一个监听，其实就是要设置一个回调的接口。
        this.mOnLongClickListener = listener;
    }

    /**
     * 编写回调的步骤
     * 1.创建这个接口
     * 2.定义接口内部的方法
     * 3.提供设置接口的方法
     * 4.接口方法的调用
     */

    public interface OnItemClickListener{
        void onItemClick(BluetoothDevice bluetoothDevice);
    }

    public interface OnItemLongClickListener{
        void OnLongClick(BluetoothDevice bluetoothDevice);
    }

    public void setData(List<BluetoothDevice> data){
        this.mDatas=data;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView txname;
        TextView txaddress;
        TextView txstate;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txname = itemView.findViewById(R.id.tv_butename);
            txaddress = itemView.findViewById(R.id.tv_blueaddress);
            txstate = itemView.findViewById(R.id.tv_bluestate);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemClickListener != null){
                        mOnItemClickListener.onItemClick(mDatas.get(position));
                    }
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mOnLongClickListener.OnLongClick(mDatas.get(position));
                    return false;
                }
            });
        }
    }
}
