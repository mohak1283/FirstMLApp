package com.example.android.firstmlapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.ml.vision.label.FirebaseVisionLabel;

import java.security.acl.LastOwnerException;
import java.util.List;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MyViewHolder> {

    private Context context;
    private List<FirebaseVisionLabel> firebaseVisionLabels;
    private static final String LOG_TAG = MainAdapter.class.getSimpleName();

    public MainAdapter(Context context, List<FirebaseVisionLabel> firebaseVisionLabels) {
        super();
        this.context = context;
        this.firebaseVisionLabels = firebaseVisionLabels;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.list_items, parent, false);


        return new MyViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        FirebaseVisionLabel firebaseVisionLabel = firebaseVisionLabels.get(position);
        String label = firebaseVisionLabel.getLabel();
        String confidence = String.valueOf(firebaseVisionLabel.getConfidence());


        holder.textTextView.setText(label);
        holder.confidenceTextView.setText("Probability: " + confidence);


    }


    @Override
    public int getItemCount() {
        if (firebaseVisionLabels.size() == 0) {
            return 0;
        }else {
            return firebaseVisionLabels.size();
        }

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView textTextView;
        TextView confidenceTextView;


        public MyViewHolder(View itemView) {
            super(itemView);
            textTextView = itemView.findViewById(R.id.text);
            confidenceTextView = itemView.findViewById(R.id.confidence);
        }
    }



}
