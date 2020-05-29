package com.example.diskret_project;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageItemViewHolder> {
    // Init inflater, list for messages and context
    LayoutInflater mInflater;
    private ArrayList<String> messages;
    private Context context;

    public MessageAdapter(ArrayList<String> Messages){
        messages = Messages;
    }

    /**
     * Purpose of this function is to inflate new item holder with message_layout
     * when it is created
     * @param parent - reference to RecyclerView
     * @param viewType - ???
     * @return instance of MessageItemViewHolder
     */
    @NonNull
    @Override
    public MessageItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        mInflater = LayoutInflater.from(context);

        View myView = mInflater.inflate(R.layout.message_layout, parent, false);

        return new MessageItemViewHolder(myView);
    }

    /**
     * Binds new Item holder
     * @param holder - reference to the holder
     * @param position - position of holder in the recyclerView
     */
    @Override
    public void onBindViewHolder(@NonNull MessageItemViewHolder holder, int position) {
        // IN the ArrayList, message consists of author and message itself,
        // separated by 'я'
        String[] authorMessage = messages.get(position).split("я");
        String author = authorMessage[0];
        String message = authorMessage[1];
        // bind function inside the item holder
        holder.bind(author, message);
    }

    /**
     * @return length of recyclerView
     */
    @Override
    public int getItemCount() {
        return messages.size();
    }

    /**
     * class that represents one item in the recyclerView
     */
    static class MessageItemViewHolder extends RecyclerView.ViewHolder{
        private TextView authorTextView;
        private TextView messageTextView;

        MessageItemViewHolder(@NonNull View itemView) {
            super(itemView);
            authorTextView = itemView.findViewById(R.id.authorTextView);
            messageTextView = itemView.findViewById(R.id.messageTextView);
        }
        void bind(String author, String message){
            authorTextView.setText(author);
            messageTextView.setText(message);
        }
    }
}
