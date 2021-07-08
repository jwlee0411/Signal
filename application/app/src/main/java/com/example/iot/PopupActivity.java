package com.example.iot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PopupActivity extends AppCompatActivity {
    TextView tv_confirm, tv_cancel;
    EditText et_name, et_code;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    private static final String TAG = "PopupActivity";

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_popup);
        tv_confirm=findViewById(R.id.tv_confirm);
        tv_cancel=findViewById(R.id.tv_cancel);
        et_name=findViewById(R.id.popup_name);
        et_code=findViewById(R.id.popuo_code);
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tv_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "onClick: clicked" );
                if (et_code.getText().toString().equals("") || et_name.getText().toString().equals("")){
                    Toast.makeText(PopupActivity.this, "빈칸이 있어요ㅠㅠ", Toast.LENGTH_SHORT).show();
                }
                else{
                    DatabaseReference myRef = database.getReference();
                    ValueEventListener valueEventListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.hasChild(et_code.getText().toString())){
                                Intent intent = new Intent();
                                Log.e(TAG, "onDataChange: haschild");
                                intent.putExtra("name", et_name.getText().toString());
                                intent.putExtra("code", et_code.getText().toString());
                                setResult(RESULT_OK, intent);
                                finish();
                            }
                            else{
                                Toast.makeText(PopupActivity.this, "잘못된 코드 입니다.", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(PopupActivity.this, "Error", Toast.LENGTH_SHORT).show();
                        }
                    };
                    myRef.addListenerForSingleValueEvent(valueEventListener);
                }
            }

        });

    }
}
