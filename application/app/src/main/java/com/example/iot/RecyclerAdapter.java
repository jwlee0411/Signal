package com.example.iot;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;

import me.jfenn.colorpickerdialog.dialogs.ColorPickerDialog;
import me.jfenn.colorpickerdialog.interfaces.OnColorPickedListener;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ItemViewHolder> {
    private final Context mContext;
    static ArrayList<LampItem> list;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private Gson gson = new Gson();
    private SharedPreferences sharedPreferences;

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    public RecyclerAdapter(Context context) {
        this.mContext = context;
    }

    RecyclerAdapter(ArrayList<LampItem> list, Context mContext) {
        this.mContext = mContext;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        RecyclerAdapter.list = list;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item, parent, false);
        return new ItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ItemViewHolder holder, final int position) {
        database.getReference().child(list.get(position).getCode()).child("Hum").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue()!=null)
                    list.get(position).humid=(Integer.parseInt(dataSnapshot.getValue()+""));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(mContext, "서버에서 받아오는데 실패했습니다!", Toast.LENGTH_SHORT).show();
            }
        });

        database.getReference().child(list.get(position).getCode()).child("Temp").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue()!=null)
                list.get(position).temperature=(Double.parseDouble(dataSnapshot.getValue()+""));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(mContext, "서버에서 받아오는데 실패했습니다!", Toast.LENGTH_SHORT).show();
            }
        });

        database.getReference().child(list.get(position).getCode()).child("PowerOn").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue()!=null) {
                    if (Integer.parseInt(dataSnapshot.getValue() + "") == 1) {
                        holder.btn_onoff.setChecked(true);
                    } else holder.btn_onoff.setChecked(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(mContext, "서버에서 받아오는데 실패했습니다!", Toast.LENGTH_SHORT).show();
            }
        });


        holder.tv.setText(list.get(position).getName());
        holder.btn_onoff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.btn_onoff.isChecked()) {
                    final DatabaseReference myRef = database.getReference(list.get(position).getCode());
                    myRef.child("PowerOn").setValue(1).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            myRef.child("brightness").setValue(100);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(mContext, "전송에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                        }
                    });


                } else {
                    DatabaseReference myRef = database.getReference(list.get(position).getCode());
                    myRef.child("PowerOn").setValue(0).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            //holder.btn_onoff.setText("ON");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(mContext, "전송에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });


        holder.btn_colorpicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ColorPickerDialog()
                        .withAlphaEnabled(false)
                        .withPresets(Color.RED, Color.GREEN, Color.BLUE)
                        .withListener(new OnColorPickedListener<ColorPickerDialog>() {
                            @Override
                            public void onColorPicked(@Nullable ColorPickerDialog pickerView, int color) {
                                DatabaseReference myRef = database.getReference(list.get(position).getCode()).child("Color");

                                String hexColor = Integer.toHexString(color);
                                Log.e(TAG, "onColorPicked: "+hexColor );
                                int red, green, blue;

                                red = Integer.parseInt(hexColor.substring(2, 4), 16);
                                green = Integer.parseInt(hexColor.substring(4, 6), 16);
                                blue = Integer.parseInt(hexColor.substring(6, 8), 16);
                                Log.e(TAG, "onColorPicked: " + red +","+ green +","+ blue);

                                myRef.child("Color_R").setValue(red).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(mContext, "전송에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                myRef.child("Color_G").setValue(green).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(mContext, "전송에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                myRef.child("Color_B").setValue(blue).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(mContext, "전송에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }
                        }).show(((AppCompatActivity) mContext).getSupportFragmentManager(), "colorpicker");
            }
        });
        holder.btn_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list.remove(position);
                String json = gson.toJson(list);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("l_list", json);
                editor.apply();
                Log.e(TAG, "onClick: apply" );
                notifyDataSetChanged();
            }
        });
        holder.btn_light.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View layout = inflater.inflate(R.layout.seekbar_dialog, (ViewGroup) view.findViewById(R.id.root));
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext)
                        .setView(layout);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alertDialog = builder.create();

                alertDialog.show();
                final SeekBar sb = layout.findViewById(R.id.seekbar);
                final TextView set_size_help_text = layout.findViewById(R.id.help_text);
                DatabaseReference myRef = database.getReference(list.get(position).getCode()).child("brightness");
                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                     set_size_help_text.setText(String.valueOf(dataSnapshot.getValue()));
                     sb.setProgress(Integer.parseInt(dataSnapshot.getValue()+""));
                        Log.e(TAG, "onDataChange: "+dataSnapshot.getValue() );
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        set_size_help_text.setText("" + progress);
                        DatabaseReference myRef = database.getReference(list.get(position).getCode());
                        myRef.child("brightness").setValue(progress).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(mContext, "전송에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        private ImageButton btn_colorpicker;
        private ImageButton btn_del, btn_light;
        private Switch btn_onoff;
        private TextView tv;
        View v = LayoutInflater.from(mContext).inflate(R.layout.fragment_main, null);

        ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            tv = itemView.findViewById(R.id.tv_name);
            btn_onoff = itemView.findViewById(R.id.btn_onoff);
            btn_colorpicker = itemView.findViewById(R.id.btn_colorpicker);
            btn_del = itemView.findViewById(R.id.btn_del);
            btn_light = itemView.findViewById(R.id.btn_light);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    
                }
            });

        }
    }
}
