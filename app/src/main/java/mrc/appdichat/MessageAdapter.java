package mrc.appdichat;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

/**
 * Created by HP on 13-02-2018.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<Messages> messageList;
    private FirebaseAuth mAuth;

    public MessageAdapter(List<Messages> messageList) {
        this.messageList = messageList;

    }

    @Override
    public MessageAdapter.MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_single_layout, parent, false);

        return new MessageViewHolder(view);
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder

    {
        View view;
        TextView messageView;

        public MessageViewHolder(View view) {
            super(view);
            this.view = view;
            messageView = view.findViewById(R.id.message_text_view);
        }
    }

    @Override
    public void onBindViewHolder(MessageAdapter.MessageViewHolder viewHolder, int position) {


        Messages messages = messageList.get(position);
        viewHolder.messageView.setText(messages.getMessage());

        mAuth = FirebaseAuth.getInstance();
        String ourId = mAuth.getUid();
        String from = messages.getFrom();
        if (from != null && from.equals(ourId)) {


        } else {
            viewHolder.messageView.setBackgroundColor(Color.WHITE);
            viewHolder.messageView.setTextColor(Color.BLACK);

            /*RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) viewHolder.messageView.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_START);
            params.addRule(RelativeLayout.ALIGN_START);
            params.setMarginStart(0);
            params.setMargins(8,8,8,8);
            viewHolder.messageView.setLayoutParams(params);*/
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) viewHolder.messageView.getLayoutParams();
            params.removeRule(RelativeLayout.ALIGN_PARENT_END);
            params.addRule(RelativeLayout.ALIGN_PARENT_START);
            params.setMarginStart(8);
            viewHolder.messageView.setLayoutParams(params);

        }


    }


    @Override
    public int getItemCount() {
        return messageList.size();
    }
}
