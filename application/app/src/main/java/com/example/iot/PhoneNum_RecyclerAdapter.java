package com.example.iot;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import static androidx.constraintlayout.widget.Constraints.TAG;
import static com.example.iot.RecyclerAdapter.list;

public class PhoneNum_RecyclerAdapter extends RecyclerView.Adapter<PhoneNum_RecyclerAdapter.ItemViewHolder>{
    Context mContext;
    SharedPreferences sharedPreferences;
    ArrayList<String> numArray;
    Gson gson= new Gson();

    public PhoneNum_RecyclerAdapter(Context mContext, ArrayList<String> numArray) {
        this.mContext = mContext;
        this.numArray = numArray;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
    }

    public PhoneNum_RecyclerAdapter() {
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.telenum_recyclerview_item, parent, false);
        return new ItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, final int position) {
        holder.tv.setText(numArray.get(position));
        holder.Delbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                numArray.remove(position);
                String json = gson.toJson(numArray);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("phoneNum", json);
                editor.apply();
                Log.e(TAG, "onClick: apply" );
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return numArray.size();
    }
    class ItemViewHolder extends RecyclerView.ViewHolder {
        private TextView tv;
        private ImageButton Delbtn;
        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            tv = itemView.findViewById(R.id.tv_num);
            Delbtn = itemView.findViewById(R.id.btn_numDel);
        }
    }
}

