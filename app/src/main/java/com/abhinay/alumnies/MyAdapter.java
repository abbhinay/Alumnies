package com.abhinay.alumnies;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.abhinay.alumnies.R;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter <MyAdapter.ViewHolder> {

    RecyclerView recyclerView;
    Context context;
    ArrayList<String> items = new ArrayList<>();

    public void update(String name, String email, String branch){
        items.add(name+ "\n" + email + "\n" + branch);
        notifyDataSetChanged(); //refreshes the recycler view automatically
    }

    public MyAdapter(RecyclerView recyclerView, Context context, ArrayList<String> items, ArrayList<String> urls) {
        this.recyclerView = recyclerView;
        this.context = context;
        this.items = items;
        Log.d("adapter", "inside adapter");
        //this.urls = urls;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Log.d("adapter", "inside onCreateViewHolder");
        View view = LayoutInflater.from(context).inflate(R.layout.profile_card, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdapter.ViewHolder viewHolder, int i) {
        viewHolder.name.setText(items.get(i));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        public ViewHolder(View itemView) {
            super(itemView);
            Log.d("adapter", "inside viewHolder");
            name = itemView.findViewById(R.id.name);
            //email = itemView.findViewById(R.id.email);
            //branch = itemView.findViewById(R.id.branch);
//            itemView.setOnClickListener(new View.OnClickListener(){
//                @Override
//                public void onClick(View view){
//                    int position = recyclerView.getChildLayoutPosition(view);
//                    //intent to open the file in browser. change here.
//                    Intent intent = new Intent();
//                    intent.setDataAndType(Uri.parse(urls.get(position)), Intent.ACTION_VIEW );
//                    context.startActivity(intent);
//                }
//            });
        }
    }
}
