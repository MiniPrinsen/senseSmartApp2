package com.example.gustaf.touchpoint.Adapters;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.gustaf.touchpoint.Fragment.MessageContainer;
import com.example.gustaf.touchpoint.R;

import java.util.ArrayList;
import java.util.List;


/**
 * Adapter for displaying messages.
 */
public class ChatArrayAdapter extends ArrayAdapter {
    private List<MessageContainer>      chatMessageList = new ArrayList<>();

    public ChatArrayAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);

    }

    /**
     * Adding a message to the list.
     * @param object MessageContainer
     */
    public void add(MessageContainer object) {


        chatMessageList.add(object);
    }

    public int getCount() {
        return this.chatMessageList.size();
    }

    public MessageContainer getItem(int index) {
        return this.chatMessageList.get(index);
    }

    /**
     * This is where we add the messages to the list. We are also checking who wrote the message
     * and by that changing the gravity of the view we want to inflate. If the user wrote the mess,
     * set the gravity to RIGHT. otherwise, set the gravity to LEFT.
     */
    public @NonNull
    View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View row = convertView;
        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) this.getContext().
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.chatbubbles_layout, parent, false);
        }

        LinearLayout singleMessageContainer = (LinearLayout) row.findViewById(R.id.singleMessageContainer);
        MessageContainer chatMessageObj = getItem(position);
        TextView chatText = (TextView) row.findViewById(R.id.singleMessage);



        /* Checks if the object should be to the left or right and
         * sets the properties connected to each side
        **/
        int chatbubbleuser = R.drawable.chatbubble_user;

        int chatbubblebot = R.drawable.chatbubble_bot;
        LinearLayout.LayoutParams userChatMessage = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams botChatMessage = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        botChatMessage.setMargins(30,0,300,0);
        userChatMessage.setMargins(300,0,30,0);
        int leftTextColor = ContextCompat.getColor(getContext(), R.color.colorGrayDark);
        int rightTextColor = ContextCompat.getColor(getContext(), R.color.colorWhite);

        if(chatMessageObj != null) {
            chatText.setText(chatMessageObj.message);
            chatText.setBackgroundResource(chatMessageObj.left ? chatbubblebot :
                    chatbubbleuser);
            chatText.setLayoutParams(chatMessageObj.left ? botChatMessage : userChatMessage);
            singleMessageContainer.setGravity(chatMessageObj.left ? Gravity.START : Gravity.END);
            chatText.setTextColor(chatMessageObj.left ? leftTextColor : rightTextColor);
        }
        return row;
    }
}