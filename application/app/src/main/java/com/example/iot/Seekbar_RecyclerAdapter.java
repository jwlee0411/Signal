package com.example.iot;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.lzyzsd.circleprogress.ArcProgress;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Seekbar_RecyclerAdapter extends RecyclerView.Adapter<Seekbar_RecyclerAdapter.ItemViewHolder>{
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    Context mContext;
    int gasZero;

    public Seekbar_RecyclerAdapter(Context mContext) {
        this.mContext = mContext;
    }


    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.seekbar_recyclerview_item, parent, false);
        return new ItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ItemViewHolder holder, final int position) {
        database.getReference().child(RecyclerAdapter.list.get(position).getCode()).child("GasDetected").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                holder.arcProgress.setMax(Integer.parseInt(dataSnapshot.getValue()+""));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        holder.arcProgress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, GasPopupActivity.class);
                intent.putExtra("position", position);
                mContext.startActivity(intent);
            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    holder.arcProgress.setBottomText(RecyclerAdapter.list.get(position).getName());
                } catch (IndexOutOfBoundsException e){
                    e.printStackTrace();
                }
            }
        }).start();
        database.getReference().child(RecyclerAdapter.list.get(position).getCode()).child("GasSensor").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                database.getReference().child(RecyclerAdapter.list.get(position).getCode()).child("GasZeroPoint").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue()!=null){
                            gasZero = Integer.parseInt(dataSnapshot.getValue()+"");
                            holder.arcProgress.setProgress(Integer.parseInt(Math.round(RecyclerAdapter.list.get(position).getGas())+"")-gasZero);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                if (dataSnapshot.getValue()!=null)
                    RecyclerAdapter.list.get(position).setGas(Double.parseDouble(dataSnapshot.getValue()+""));
                    holder.arcProgress.setProgress(Integer.parseInt(Math.round(RecyclerAdapter.list.get(position).getGas())+"")-gasZero);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return RecyclerAdapter.list.size();
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        private ArcProgress arcProgress;
        ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            arcProgress = itemView.findViewById(R.id.arc_progress);
        }
    }
}
