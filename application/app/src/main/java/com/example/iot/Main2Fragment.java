package com.example.iot;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;
import static androidx.constraintlayout.widget.Constraints.TAG;


public class Main2Fragment extends Fragment{
    private ArrayList<LampItem> list = new ArrayList<>();
    private RecyclerView.Adapter adapter;
    private Gson gson = new Gson();
    private SharedPreferences mShared;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof Activity){
            Activity a = (Activity) context;
            mShared = PreferenceManager.getDefaultSharedPreferences(a);
        }


    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.fragment_main2, null);




        String json = mShared.getString("l_list", "");
        if (!json.equals("")) {
            Type type = new TypeToken<ArrayList<LampItem>>() {
            }.getType();
            list = gson.fromJson(json, type);
        }


        RecyclerView rv = v.findViewById(R.id.rv);
        ImageButton addLamp = v.findViewById(R.id.addlamp);
        addLamp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), PopupActivity.class);
                startActivityForResult(intent, 0);

            }
        });
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new RecyclerAdapter(list, getContext());
        rv.setAdapter(adapter);
        return v;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, "onActivityResult: input" );
        if (resultCode == RESULT_OK) {
            list.add(new LampItem(data.getStringExtra("name"), data.getStringExtra("code")));
            String json = gson.toJson(list);
            SharedPreferences.Editor editor = mShared.edit();
            editor.putString("l_list", json);
            editor.apply();
            adapter.notifyDataSetChanged();
        }

    }
}
