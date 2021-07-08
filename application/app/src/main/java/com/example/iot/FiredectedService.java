package com.example.iot;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class FiredectedService extends Service {
    NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "default");
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    SharedPreferences pref;
    String phoneNum, first;
    Context context;
    SoundPool sound;
    String desc;
    Gson gson = new Gson();
    ArrayList<String> numArray = new ArrayList<>();
    int tak;

    public FiredectedService() {
    }

    public FiredectedService(Context context) {
        this.context = context;
    }

    @Override
    public void onCreate() {
        database.getReference().child("SetDesc").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                desc = dataSnapshot.getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        Log.e(TAG, "onCreate: " );
        super.onCreate();
        Thread dectect = new Thread(new Dectect());
        dectect.start();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    private  class Dectect implements Runnable{
        @Override
        public void run() {
            Log.e(TAG, "run: runingg");
            pref = PreferenceManager.getDefaultSharedPreferences(FiredectedService.this);
            database.getReference().child("FireDetected").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Log.e(TAG, "onDataChange: 1234565789" );
                    if(Integer.parseInt(dataSnapshot.getValue()+"")==1){
                        String json = pref.getString("phoneNum", "");
                        if (!json.equals("")) {
                            Type type = new TypeToken<ArrayList<String>>() {
                            }.getType();
                            numArray = gson.fromJson(json, type);
                        }
                        Log.e(TAG, "fs: "+numArray );
                        for (int i=0; i<numArray.size(); i++) {
                            phoneNum = numArray.get(i);
                            if (phoneNum.equals("")) {
                                Toast.makeText(getApplicationContext(), "전화번호 오류", Toast.LENGTH_SHORT).show();
                            } else {
                                createNotification();
                                SmsManager sms = SmsManager.getDefault();
                                if (desc.equals("")) {
                                    sms.sendTextMessage(phoneNum, null, "불이 났어요!!", null, null);
                                } else {
                                    sms.sendTextMessage(phoneNum, null, desc, null, null);
                                }
                                Log.e("wow2", "onDataChange: sending");

//                            // 사이렌 재생 코드 시작
//                            sound = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
//                            tak = sound.load(FiredectedService.this, R.raw.siren, 1);
//                            sound.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
//                                @Override
//                                public void onLoadComplete(SoundPool soundPool, int i, int i1) {
//                                    sound.play(1, 1, 1, 0, 0, 1);
//                                }
//                            });
//                            //사이렌 재생 코드 끝
                                Log.e(TAG, "onDataChange: soundplay");
                                try {
                                    Thread.sleep(10000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                final DatabaseReference myRef = database.getReference();
                                myRef.child("FireDetected").setValue(0).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                    }
                                });

                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }
    public void createNotification() {


        database.getReference().child("FirstDetect").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                first = dataSnapshot.getValue()+"";
                builder.setSmallIcon(R.mipmap.ic_launcher);
                builder.setContentTitle("Signal");
                builder.setContentText(first + "에서 가스가 감지되었습니다!");

                builder.setColor(Color.RED);
                // 사용자가 탭을 클릭하면 자동 제거
                builder.setAutoCancel(true);

                // 알림 표시
                NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    notificationManager.createNotificationChannel(new NotificationChannel("default", "기본 채널", NotificationManager.IMPORTANCE_DEFAULT));
                }

                // id값은
                // 정의해야하는 각 알림의 고유한 int값
                notificationManager.notify(1, builder.build());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
