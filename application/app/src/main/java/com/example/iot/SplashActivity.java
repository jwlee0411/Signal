package com.example.iot;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import static androidx.core.content.ContextCompat.checkSelfPermission;

public class SplashActivity extends AppCompatActivity {
    SharedPreferences pref;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        if (ContextCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Explain to the user why we need to write the permission.
                Toast.makeText(SplashActivity.this, "Read/Write external storage", Toast.LENGTH_SHORT).show();
            }

            requestPermissions(new String[]{Manifest.permission.SEND_SMS}, 123);

            // MY_PERMISSION_REQUEST_STORAGE is an
            // app-defined int constant

            boolean first = pref.getBoolean("isFirst", false);
            if (first == false || pref.getString("telenum", "").equals("")) {
                Log.e("Is first Time?", "first");
                SharedPreferences.Editor editor = pref.edit();
                editor.putBoolean("isFirst", true);
                editor.apply();

                final EditText edittext = new EditText(SplashActivity.this);
                AlertDialog.Builder builder = new AlertDialog.Builder(SplashActivity.this);
                builder.setTitle("화재 발생시 문자를 받을 전화번호를 입력해주세요. (-제외)");
                builder.setView(edittext);
                builder.setPositiveButton("입력",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if (!edittext.getText().toString().equals("")) {
                                    SharedPreferences.Editor editor = pref.edit();
                                    editor.putString("telenum", edittext.getText().toString());
                                    editor.apply();
                                    Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
//                                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
//                                        startActivity(intent);
                                            finish();
                                        }
                                    }, 2000);
                                } else {
                                    Toast.makeText(SplashActivity.this, "전화번호는 이후 앱에서 등록할 수 있습니다", Toast.LENGTH_LONG).show();
                                    Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
//                                        finish();
                                        }
                                    }, 2000);
                                }
                            }
                        });
                builder.show();

            } else {
                Log.e("Is first Time?", "not first");
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
//                    finish();
                    }
                }, 2000);
            }


        }
    }
}