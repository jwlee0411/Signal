package com.example.iot;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.enrico.colorpicker.colorDialog;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sdsmdg.harjot.crollerTest.Croller;
import com.sdsmdg.harjot.crollerTest.OnCrollerChangeListener;

import java.lang.reflect.Type;
import java.util.ArrayList;

import uz.shift.colorpicker.LineColorPicker;
import uz.shift.colorpicker.OnColorChangedListener;

import static androidx.constraintlayout.widget.Constraints.TAG;
import static androidx.core.content.ContextCompat.checkSelfPermission;


public class MainFragment extends Fragment implements colorDialog.ColorSelectedListener {
    private ConstraintLayout main, sub;
    private Seekbar_RecyclerAdapter adapter;
    private Activity a;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private TextView tv_croller;
    private DatabaseReference myRef;
    private SharedPreferences mShared;
    private ArrayList<LampItem> list = new ArrayList<>();
    private Gson gson = new Gson();
    private String phoneNum;
    private CheckBox checkBox;
    private TextView tempTv, humidTv;
    private Double tempAvg = 0.0, humidAvg = 0.0;
    private ConstraintLayout tempHumid;
    private Handler handler;
    private int pnmCnt=0;

    private String code, desc;
    private int red;
    private int green;
    private int blue;
    ArrayList<String> numArray = new ArrayList<>();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof Activity) {
            a = (Activity) context;

            mShared = PreferenceManager.getDefaultSharedPreferences(a);
            String json = mShared.getString("l_list", "");
            if (!json.equals("")) {
                Type type = new TypeToken<ArrayList<LampItem>>() {
                }.getType();
                list = gson.fromJson(json, type);
            }
        }


    }


    void tnH() {
        humidAvg = 0.0;

        tempAvg = 0.0;
        if (RecyclerAdapter.list.size() != 1) {
            for (int i = 0; i < RecyclerAdapter.list.size(); i++) {
                humidAvg += RecyclerAdapter.list.get(i).humid;
                Log.e(TAG, "onClick: "+humidAvg );
            }
            humidAvg = humidAvg / RecyclerAdapter.list.size();
        } else humidAvg = Double.parseDouble(RecyclerAdapter.list.get(0).humid + "");


        if (RecyclerAdapter.list.size() != 1) {
            for (int i = 0; i < RecyclerAdapter.list.size(); i++) {
                if (RecyclerAdapter.list.get(i).temperature != null)
                    tempAvg += RecyclerAdapter.list.get(i).temperature + pnmCnt;
            }
            tempAvg = tempAvg / RecyclerAdapter.list.size();
        } else tempAvg = RecyclerAdapter.list.get(0).temperature + pnmCnt;

        database.getReference().child("temperature").setValue(Math.round(tempAvg));
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.fragment_main, null);
        View v2 = LayoutInflater.from(getContext()).inflate(R.layout.fragment_main2, null);
        tempTv = v.findViewById(R.id.temp);
        humidTv = v.findViewById(R.id.humi);
        Button smsTest = v.findViewById(R.id.smstest);
        Button telenum = v.findViewById(R.id.telenum);
        Button setSentence = v.findViewById(R.id.set_sentence);
        checkBox = v.findViewById(R.id.fireDectedOn);
        main = v.findViewById(R.id.mainLayout);
        tempHumid = v.findViewById(R.id.tempNhumi);
        LineColorPicker colorSeekBar = v.findViewById(R.id.color_seek_bar);
        handler = new Handler();

        database.getReference().child("SetDesc").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                desc = dataSnapshot.getValue()+"";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        setSentence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText edittext = new EditText(getContext());
                AlertDialog.Builder builder = new AlertDialog.Builder(a);
                builder.setTitle("화재시 문자로 받을 알림의 문구를 설정해주세요.");
                builder.setView(edittext);
                builder.setPositiveButton("입력",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if (!edittext.getText().toString().equals("")) {
                                    database.getReference().child("SetDesc").setValue(edittext.getText().toString());
                                }
                            }
                        });
                builder.show();
            }
        });
        if (mShared.getBoolean("checked", false)) {
            checkBox.setChecked(true);
        } else {
            checkBox.setChecked(false);
            Intent intent = new Intent(getContext(), FiredectedService.class);
            a.stopService(intent);
        }
        database.getReference().child("TempPnmCnt").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                pnmCnt = Integer.parseInt(dataSnapshot.getValue()+"");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        // set color palette
        colorSeekBar.setColors(new int[]{Color.parseColor("#F78c6c"), Color.parseColor("#feb44d"), Color.parseColor("#fcf087"), Color.parseColor("#e6e966"), Color.parseColor("#9ff4b9"), Color.parseColor("#42d2c8"), Color.parseColor("#7acee1"), Color.parseColor("#d2a2e4")});
        Log.e(TAG, "onCreateView: ");
        colorSeekBar.setOnColorChangedListener(new OnColorChangedListener() {
            @Override
            public void onColorChanged(int color) {
                String hexColor = Integer.toHexString(color);
                red = Integer.parseInt(hexColor.substring(2, 4), 16);
                green = Integer.parseInt(hexColor.substring(4, 6), 16);
                blue = Integer.parseInt(hexColor.substring(6, 8), 16);
                Log.e(TAG, "onColorPicked: " + red + "," + green + "," + blue);
                Log.e(TAG, "onColorChangeListener: " + hexColor);
                for (int j = 0; j < list.size(); j++) {
                    code = list.get(j).getCode();
                    myRef = database.getReference(code).child("Color");

                    myRef.child("Color_R").setValue(red).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), "전송에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                        }
                    });

                    myRef.child("Color_G").setValue(green).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), "전송에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                        }
                    });

                    myRef.child("Color_B").setValue(blue).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), "전송에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        tempHumid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tnH();
                tempTv.setText("현재 온도 : " + Math.round(tempAvg) + "℃");
                humidTv.setText("현재 습도 :" + Math.round(humidAvg) + "%");
            }
        });





        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = mShared.edit();
                editor.putBoolean("checked", checkBox.isChecked());
                editor.apply();
                if (!checkBox.isChecked()) {
                    Intent intent = new Intent(getContext(), FiredectedService.class);
                    a.stopService(intent);
                }
            }
        });

        String json = mShared.getString("phoneNum", "");
        if (!json.equals("")) {
            Type type = new TypeToken<ArrayList<String>>() {
            }.getType();
            numArray = gson.fromJson(json, type);
        }
        smsTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkSelfPermission(a, Manifest.permission.SEND_SMS)
                        != PackageManager.PERMISSION_GRANTED) {

                    // Should we show an explanation?
                    if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        // Explain to the user why we need to write the permission.
                        Toast.makeText(getContext(), "Read/Write external storage", Toast.LENGTH_SHORT).show();
                    }

                    requestPermissions(new String[]{Manifest.permission.SEND_SMS}, 123);

                    // MY_PERMISSION_REQUEST_STORAGE is an
                    // app-defined int constant

                } else {
                    createNotification();
                    for (int i=0; i<numArray.size(); i++) {
                        phoneNum = numArray.get(i);
                        Log.e(TAG, "onClick: " + phoneNum);
                        if (phoneNum.equals("")) {
                            Toast.makeText(getContext(), "전화번호 오류", Toast.LENGTH_SHORT).show();
                        }
                        SmsManager sms = SmsManager.getDefault();
                        if (desc.equals("")) {
                            sms.sendTextMessage(phoneNum, null, "불이 났어요!!", null, null);
                        } else {
                            sms.sendTextMessage(phoneNum, null, desc, null, null);
                        }
                    }

                }
            }
        });

        telenum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), PhoneNumActivity.class);
                startActivity(intent);
            }
        });
        ImageButton imageButton = v2.findViewById(R.id.addlamp);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter.notifyDataSetChanged();
            }
        });
        adapter = new Seekbar_RecyclerAdapter(getContext());
        RecyclerView rv = v.findViewById(R.id.seek_rv);
        rv.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        rv.setAdapter(adapter);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tv_croller = getActivity().findViewById(R.id.tv_croller);
        Croller croller = getActivity().findViewById(R.id.croller);
        croller.setLabel("밝기!");
        croller.setOnCrollerChangeListener(new OnCrollerChangeListener() {
            @Override
            public void onProgressChanged(Croller croller, int progress) {
                tv_croller.setText(progress + "");
            }

            @Override
            public void onStartTrackingTouch(Croller croller) {

            }

            @Override
            public void onStopTrackingTouch(Croller croller) {
                for (int i = 0; i < list.size(); i++) {
                    code = list.get(i).getCode();
                    database.getReference().child(code).child("brightness").setValue(croller.getProgress());
                }
            }
        });


    }


    @Override
    public void onColorSelection(DialogFragment dialogFragment, int i) {

    }
    public void createNotification() {

        String NOTIFICATION_CHANNEL_ID = "Channel_id";

        long pattern[] = {0, 1000, 500, 1000};

        NotificationManager mNotificationManager =
                (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Your Notifications",
                    NotificationManager.IMPORTANCE_HIGH);

            notificationChannel.setDescription("");
//            Log.e(TAG, "createNotification: asd" );
//            notificationChannel.setSound(Uri.parse("android.resource://"
//                    + getActivity().getPackageName() + "/" + R.raw.siren), audioAttributes);
//            Log.e(TAG, "createNotification: asd" );
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(pattern);
            notificationChannel.enableVibration(true);
            mNotificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getActivity().getApplicationContext(), NOTIFICATION_CHANNEL_ID);

        notificationBuilder.setAutoCancel(true)
                .setColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.colorAccent))
                .setContentTitle("Signal")
                .setContentText("테스트 - 불이 났어요!!")
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setSound(Uri.parse("android.resource://"
                + getActivity().getPackageName() + "/" + R.raw.siren))
                .setAutoCancel(true);

        mNotificationManager.notify(1000, notificationBuilder.build());
    }
}
//얘들아 살려줘
