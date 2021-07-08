package com.example.iot;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class PhoneNumActivity extends AppCompatActivity {
    Gson gson= new Gson();
    ImageButton addNum;
    ArrayList<String> numArray= new ArrayList<>();
    RecyclerView numRv;
    RecyclerView.Adapter adapter;
    SharedPreferences sharedPreferences;
    String regExp = "^[0-9]*$";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_num);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(PhoneNumActivity.this);
        String json = sharedPreferences.getString("phoneNum", "");
        if (!json.equals("")) {
            Type type = new TypeToken<ArrayList<String>>() {
            }.getType();
            numArray = gson.fromJson(json, type);
        }

        addNum = findViewById(R.id.addNum);
        numRv = findViewById(R.id.num_rv);
        addNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText edittext = new EditText(PhoneNumActivity.this);
                final AlertDialog.Builder builder = new AlertDialog.Builder(PhoneNumActivity.this);
                builder.setTitle("화재 발생시 문자를 받을 전화번호를 입력해주세요. ( - 제외)");
                builder.setView(edittext);
                builder.setPositiveButton("입력",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if (!edittext.getText().toString().equals("") && edittext.getText().toString().matches(regExp)) {
                                    numArray.add(edittext.getText().toString());
                                    String json = gson.toJson(numArray);
                                    Log.e("numARRAY", "onClick: "+numArray );
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString("phoneNum", json);
                                    editor.apply();
                                }
                                else{
                                    Toast.makeText(getApplicationContext(), "전화번호를 입력해주세요", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                builder.show();
                //numArray.add(sharedPreferences.getString("telenum", ""));
                adapter.notifyDataSetChanged();
            }
        });
        numRv.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        adapter = new PhoneNum_RecyclerAdapter(this, numArray);
        numRv.setAdapter(adapter);

    }
}
