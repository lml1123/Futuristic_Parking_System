package com.example.futuristic;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyHolder> {

    Context c;
    ArrayList<Model> models, filterList;

    public MyAdapter(Context c, ArrayList<Model> models) {
        this.c = c;
        this.models = models;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_location, null);

        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder myHolder, final int i) {

        Model model = models.get(i);

        myHolder.mTitle.setText(models.get(i).getTitle());
        myHolder.mDes.setText(models.get(i).getDescription());
        myHolder.mImaeView.setImageResource(models.get(i).getImg());
        myHolder.mLoc.setText(models.get(i).getLocation());

        boolean isExpandable = models.get(i).isExpanded();
        myHolder.expandableLayout.setVisibility(isExpandable ? View.VISIBLE : View.GONE);

        if (models.get(i).getTitle().equals("1 Utama (OU)")) {
            FirebaseFirestore fStore = FirebaseFirestore.getInstance();
            fStore.collection("parking").document("oneUtama").collection("ou_b1")
                    .whereEqualTo("place", "One Utama")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                int size = task.getResult().size();

                                fStore.collection("parking").document("oneUtama").collection("ou_b2")
                                        .whereEqualTo("place", "One Utama")
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    int totalSize = size + task.getResult().size();
                                                    String TotalSize = String.valueOf(totalSize);
                                                    myHolder.TotalSlot.setText(TotalSize);
                                                }
                                            }
                                        });

                            }
                        }
                    });

            fStore.collection("parking").document("oneUtama").collection("ou_b1")
                    .whereEqualTo("Availability", "Available")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                int NotSize = task.getResult().size();

                                fStore.collection("parking").document("oneUtama").collection("ou_b2")
                                        .whereEqualTo("Availability", "Available")
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    int totalNotSize = NotSize + task.getResult().size();

                                                    if (totalNotSize <= 15) {
                                                        myHolder.CurrentSlot.setTextColor(Color.parseColor("#C51515"));
                                                    } else if (totalNotSize <= 35) {
                                                        myHolder.CurrentSlot.setTextColor(Color.parseColor("#FF9800"));
                                                    } else {
                                                        myHolder.CurrentSlot.setTextColor(Color.parseColor("#37F63F"));
                                                    }

                                                    String TotalNotSize = String.valueOf(totalNotSize);
                                                    myHolder.CurrentSlot.setText(TotalNotSize);
                                                }
                                            }
                                        });

                            }
                        }
                    });
        }
        else {
            myHolder.TotalSlot.setText("0");
            myHolder.CurrentSlot.setText("0");
            myHolder.TotalSlot.setTextColor(Color.parseColor("#000000"));
            myHolder.CurrentSlot.setTextColor(Color.parseColor("#000000"));
        }

        myHolder.mbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (models.get(i).getTitle().equals("1 Utama (OU)")) {
                    Intent intent = new Intent(c, ParkingOUtama.class);
                    c.startActivity(intent);
                } else {
                    Toast.makeText(view.getContext(), "Coming Soon", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    public void filterList(ArrayList<Model> filteredList) {
        models = filteredList;
        notifyDataSetChanged();
    }

    public class MyHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView mImaeView;
        TextView mTitle, mDes, mLoc, CurrentSlot, TotalSlot;
        ItemClickListener itemClickListener;
        RelativeLayout expandableLayout;
        RelativeLayout mview;
        Button mbutton;


        public MyHolder(@NonNull View itemView) {
            super(itemView);
            this.mImaeView = itemView.findViewById(R.id.imageIv);
            this.mTitle = itemView.findViewById(R.id.titleTv);
            this.mDes = itemView.findViewById(R.id.descriptionTv);
            this.mLoc = itemView.findViewById(R.id.locationTv);
            this.CurrentSlot = itemView.findViewById(R.id.currentNumber);
            this.TotalSlot = itemView.findViewById(R.id.totalNumber);
            mview = itemView.findViewById(R.id.ReView);
            expandableLayout = itemView.findViewById(R.id.expandableLayout);
            mbutton = itemView.findViewById(R.id.buttonBook);

            mview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Model model = models.get(getAdapterPosition());
                    model.setExpanded(!model.isExpanded());
                    notifyItemChanged(getAdapterPosition());
                }
            });

            mbutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Model model = models.get(getAdapterPosition());
                    notifyItemChanged(getAdapterPosition());
                }
            });

        }

        @Override
        public void onClick(View v) {
            this.itemClickListener.onItemClickListener(v, getLayoutPosition());
        }

        public void setItemClickListener(ItemClickListener ic) {
            this.itemClickListener = ic;
        }
    }
}