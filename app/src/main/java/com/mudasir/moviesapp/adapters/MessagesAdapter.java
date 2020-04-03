package com.mudasir.moviesapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mudasir.moviesapp.R;
import com.mudasir.moviesapp.models.messages;

import java.util.ArrayList;
import java.util.List;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessagesViewHolder> {

    private List<messages> messagesList=new ArrayList<>();
    private Context mContext;

    public MessagesAdapter(List<messages> messagesList, Context mContext) {
        this.messagesList = messagesList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public MessagesAdapter.MessagesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(mContext).inflate(R.layout.single_item_message,parent,false);

        return new MessagesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessagesAdapter.MessagesViewHolder holder, int position) {


       messages messages= messagesList.get(position);

       holder.textViewName.setText(messages.getUsername());
       holder.textViewMessage.setText(messages.getMessage());

    }

    @Override
    public int getItemCount() {
        return messagesList.size();
    }



    public class MessagesViewHolder extends RecyclerView.ViewHolder {

        TextView textViewName;
        TextView textViewMessage;


        public MessagesViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewName=itemView.findViewById(R.id.text_message_name);
            textViewMessage=itemView.findViewById(R.id.text_message_body);

        }
    }

}
