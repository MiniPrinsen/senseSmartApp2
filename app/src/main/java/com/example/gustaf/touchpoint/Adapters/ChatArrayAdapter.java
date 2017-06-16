package com.example.gustaf.touchpoint.Adapters;

/**
 * Created by Gustaf on 16-07-07.
 */

import android.content.Context;
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
 * Adapter to display messages
 */
public class ChatArrayAdapter extends ArrayAdapter {
    private TextView chatText;
    private List<MessageContainer> chatMessageList = new ArrayList<>();
    private LinearLayout singleMessageContainer;

    public ChatArrayAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public void add(MessageContainer object) {
        super.add(object);
        chatMessageList.add(object);
    }

    public int getCount() {
        return this.chatMessageList.size();
    }

    public MessageContainer getItem(int index) {
        return this.chatMessageList.get(index);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) this.getContext().
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.chatbubbles_layout, parent, false);
        }
        singleMessageContainer = (LinearLayout) row.findViewById(R.id.singleMessageContainer);
        MessageContainer chatMessageObj = getItem(position);
        chatText = (TextView) row.findViewById(R.id.singleMessage);
        chatText.setText(chatMessageObj.message);


        /* Checks if the object should be to the left or right and
         * sets the properties connected to each side
        **/
        chatText.setBackgroundResource(chatMessageObj.left ? R.drawable.chatbubble_left :
                R.drawable.chatbubble_right);
        singleMessageContainer.setGravity(chatMessageObj.left ? Gravity.LEFT : Gravity.RIGHT);

        int leftTextColor = ContextCompat.getColor(getContext(), R.color.colorGrayDark);
        int rightTextColor = ContextCompat.getColor(getContext(), R.color.colorWhite);
        chatText.setTextColor(chatMessageObj.left ? leftTextColor : rightTextColor);


        return row;
    }


}