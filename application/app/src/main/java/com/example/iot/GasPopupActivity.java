package com.example.iot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.lzyzsd.circleprogress.ArcProgress;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class GasPopupActivity extends AppCompatActivity {


    int pnmCnt =0;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    private TextView presentTemp;
    private ArcProgress arcProgress;
    private EditText setGasLim;
    private Button temp_plus, temp_minus, gasZeroPoint, tempRefresh, confirm, cancle, gasReset;
    private String temp;
    int position;
    int gasZero;
    int gasLim;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_gas_popup);
        position = getIntent().getIntExtra("position", 0);

        database.getReference().child("TempPnmCnt").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                pnmCnt = Integer.parseInt(dataSnapshot.getValue()+"");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        Log.e("살려줘", "onCreate: +"+position );
        database.getReference().child(RecyclerAdapter.list.get(position).getCode()).child("GasZeroPoint").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.e("GasZero", "onDataChange: "+position + RecyclerAdapter.list.get(position).getCode() + " " + dataSnapshot.getValue());
                gasZero = Integer.parseInt(dataSnapshot.getValue()+"");
                arcProgress.setProgress(Integer.parseInt(Math.round(RecyclerAdapter.list.get(position).getGas())+"")-gasZero);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        position = getIntent().getIntExtra("position", 0);

        confirm = findViewById(R.id.setting_confirm);
        cancle = findViewById(R.id.setting_cancle);
        arcProgress = findViewById(R.id.gas_arc);
        setGasLim = findViewById(R.id.et_setGasLim);
        temp_plus = findViewById(R.id.temp_plus);
        temp_minus = findViewById(R.id.temp_minus);
        gasZeroPoint = findViewById(R.id.gas_zero_point);
        presentTemp = findViewById(R.id.present_temp);
        tempRefresh = findViewById(R.id.temp_refresh);
        gasReset = findViewById(R.id.gas_reset);

        arcProgress.setBottomText(RecyclerAdapter.list.get(position).getName());
        arcProgress.setProgress(Integer.parseInt(Math.round(RecyclerAdapter.list.get(position).getGas())+"")-gasZero);
        database.getReference().child(RecyclerAdapter.list.get(position).getCode()).child("GasDetected").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                setGasLim.setHint("현재 수치 : "+dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        database.getReference().child("temperature").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                temp = dataSnapshot.getValue()+"";
                presentTemp.setText("현재수치 : "+ temp);
                Log.e("temp", "onDataChange: "+dataSnapshot.getValue() );
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        tempRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MainFragment().tnH();
            }
        });
        temp_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                database.getReference("temperature").setValue(temp = Integer.parseInt(temp)+1+"");
                Log.e("Temp ", "onClick: temp = "+ temp);
                pnmCnt+=1;
                database.getReference().child("TempPnmCnt").setValue(pnmCnt).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(GasPopupActivity.this, "성공", Toast.LENGTH_SHORT).show();
                        database.getReference().child(RecyclerAdapter.list.get(position).getCode()).child("valueChange").setValue(1);
                    }
                });
            }
        });
        temp_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                database.getReference("temperature").setValue(temp = Integer.parseInt(temp)-1+"");
                Log.e("Temp ", "onClick: temp = "+ temp);
                pnmCnt-=1;
                database.getReference().child("TempPnmCnt").setValue(pnmCnt).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(GasPopupActivity.this, "성공", Toast.LENGTH_SHORT).show();
                        database.getReference().child(RecyclerAdapter.list.get(position).getCode()).child("valueChange").setValue(1);
                    }
                });
            }
        });

        gasZeroPoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gasZero = Integer.parseInt(Math.round(RecyclerAdapter.list.get(position).getGas())+"");
                database.getReference().child(RecyclerAdapter.list.get(position).getCode()).child("GasZeroPoint").setValue(gasZero);
                database.getReference().child(RecyclerAdapter.list.get(position).getCode()).child("valueChange").setValue(1);
            }
        });
        gasReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                database.getReference().child(RecyclerAdapter.list.get(position).getCode()).child("GasZeroPoint").setValue(gasZero=0);
                database.getReference().child(RecyclerAdapter.list.get(position).getCode()).child("valueChange").setValue(1);
            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!setGasLim.getText().toString().equals("")) {
                    gasLim = Integer.parseInt(setGasLim.getText() + "");
                    database.getReference().child(RecyclerAdapter.list.get(position).getCode()).child("GasDetected").setValue(gasLim);
                    database.getReference().child(RecyclerAdapter.list.get(position).getCode()).child("valueChange").setValue(1);
                    arcProgress.setMax(gasLim);
                }
                finish();
            }
        });
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });




    }
}
