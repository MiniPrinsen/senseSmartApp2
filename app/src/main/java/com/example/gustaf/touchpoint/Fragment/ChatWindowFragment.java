package com.example.gustaf.touchpoint.Fragment;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.example.gustaf.touchpoint.Adapters.ChatArrayAdapter;
import com.example.gustaf.touchpoint.ChatActivity;
import com.example.gustaf.touchpoint.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * This is the chat window page. It is here we show the chat to the user.
 */
public class ChatWindowFragment extends Fragment {
    private Button                          buttonSend;
    private static final String             URL="http://ec2-52-58-76-230.eu-central-1.compute.amazonaws.com:8090/sensesmart/chatbot";
    private EditText                        chatText;
    private ChatArrayAdapter                chatArrayAdapter;
    private ListView                        listView;

    private ChatActivity                            chat;

    public ChatWindowFragment(){}

    /**
     * Here we are inflating the view and sets the behavior of send button etc.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat_window, container, false);
        findViewsById(view);

        buttonSend.setOnClickListener(customListener);
        chatText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chat.hideInfo();
            }
        });

        chatArrayAdapter = new ChatArrayAdapter(getContext(), R.layout.chatbubbles_layout);
        listView.setAdapter(chatArrayAdapter);
        listView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {


                chat.hideInfo();
                hideKeyboard(getContext());

                return false;
            }
        });
        return view;
    }


    /**
     * Function to cluster all the findviewsbyId.
     * @param container main view
     */
    private void findViewsById(View container) {
        buttonSend = (Button) container.findViewById(R.id.send_btn);
        chat = (ChatActivity) getActivity();
        chatText = (EditText)container.findViewById(R.id.msgBox);
        listView = (ListView) container.findViewById(R.id.listanmedView);




    }

    /**
     * @param message to show in dialog
     */
    private void showDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //do things
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * It's here we are defining what happens if we press the send button.
     */
    private final View.OnClickListener customListener = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId() /*to get clicked view id**/) {
                case R.id.send_btn:
                    try {
                        //If the field contains something, we can actually send the message
                        //and reset the field.
                        if (chatText.length() != 0) {
                            GetXMLTask task = new GetXMLTask();
                            task.execute(URL);
                            sendChatMessage();
                            chatText.setText("");
                            chatText.setHint("Enter message...");
                        }

                    }
                    catch (RuntimeException ex){
                        Log.v("RUNTIME","EXCEPTION"+ex);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;

                default:
                    break;
            }
        }
    };

    /**
     * Function to hide the keyboard.
     * @param ctx Context
     */
    private static void hideKeyboard(Context ctx) {
        InputMethodManager inputManager = (InputMethodManager) ctx
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        // check if no view has focus:
        View v = ((Activity) ctx).getCurrentFocus();
        if (v == null)
            return;

        inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    /**
     * Function to send the chat message.
     */
    private void sendChatMessage(){
        chatArrayAdapter.add(new MessageContainer(false, chatText.getText().toString()));
        chatText.setText("");
    }

    /**
     * Function to receive the response from the server and inflate it onto the view.
     * @param message the message
     */
    private void receiveChatMessage(String message){
        if (message != null) {

            chatArrayAdapter.add(new MessageContainer(true, message));
            listView.setSelection(chatArrayAdapter.getCount() - 1);
        }

    }
    /**
     * Class to retrieve data from the server. This is where we get the response from the bot.
     */
    private class GetXMLTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            String output = null;
            for (String url : urls) {
                output = getOutputFromUrl(url);
            }
            return output;
        }

        private String getOutputFromUrl(String url) {
            StringBuilder output = new StringBuilder("");
            try {
                InputStream stream = getHttpConnection(url);
                BufferedReader buffer = new BufferedReader(new InputStreamReader(stream, "UTF-8"));

                String s;
                while ((s = buffer.readLine()) != null)
                    output.append(s);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            return output.toString();

        }

        // Makes HttpURLConnection and returns InputStream
        // It is here we send the message the user types into the app.
        private InputStream getHttpConnection(String urlString)
                throws IOException {
            InputStream stream = null;
            String sendMessage = chatText.getText().toString();
            String id = chat.getIdString();
            String requestURL = urlString + "?id="+id+"&message=" + URLEncoder.encode(sendMessage, "UTF-8");
            //Log.d("JOHANNAID", chat.getIdString());
            java.net.URL url = new URL(requestURL);
            URLConnection connection = url.openConnection();

            try {
                HttpURLConnection httpConnection = (HttpURLConnection) connection;
                httpConnection.setRequestMethod("GET");
                httpConnection.connect();

                if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    stream = httpConnection.getInputStream();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return stream;
        }

        /**
         * Receives the chat message.
         * @param output chat message
         */
        @Override
        protected void onPostExecute(String output) {
            try {
                output = URLDecoder.decode(output, "UTF-8");
                receiveChatMessage(output);



            } catch (Exception e) {
                showDialog(getContext().getString(R.string.POST_error));
            }
        }
    }

}
