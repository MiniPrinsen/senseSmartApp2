package com.example.gustaf.touchpoint.Fragment;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.TouchDelegate;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.gustaf.touchpoint.Adapters.ChatArrayAdapter;
import com.example.gustaf.touchpoint.BaseActivity;
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
 * A simple {@link Fragment} subclass.
 */
public class ChatWindowFragment extends Fragment {
    Button buttonSend;
    boolean askedForName = false;
    public static final String URL =
            "http://130.240.135.203:9999/TouchPoint/sayhello";
    private Button goBackBtn;
    private EditText chatText;



    private ChatArrayAdapter chatArrayAdapter;
    private ListView listView;
    protected Toolbar toolbar;
    View view;

    public ChatWindowFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_chat_window, container, false);
        findViewsById(view);

        ImageView imgview = new ImageView(getContext());
        imgview.setImageResource(R.drawable.ic_arrow_back);
        imgview.setLayoutParams(new Toolbar.LayoutParams(70,70, Gravity.START));
        toolbar.addView(imgview);
        imgview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), BaseActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(i);
                view.setVisibility(View.INVISIBLE);
            }
        });
      //  ((BaseActivity)getActivity()).hideNavigationBar(true);
        setToolBarTitle("HEJ", view);
        buttonSend.setOnClickListener(customListener);

        chatArrayAdapter = new ChatArrayAdapter(getContext(), R.layout.chatbubbles_layout);
        listView.setAdapter(chatArrayAdapter);

        listView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(chatText.getWindowToken(), 0);

                return false;
            }
        });



        return view;
    }

    private void findViewsById(View container) {
        buttonSend = (Button) container.findViewById(R.id.send_btn);
        //goBackBtn = (Button)container.findViewById(R.id.btn_back);
        chatText = (EditText)container.findViewById(R.id.msgBox);
        listView = (ListView) container.findViewById(R.id.listanmedView);
        toolbar = (Toolbar) container.findViewById(R.id.gustaf_toolbar);


    }

    protected void setToolBarTitle(String title, View view){
        TextView toolbarText = (TextView)view.findViewById(R.id.toolbar_title);
        toolbarText.setText(title);
        toolbar.setTitle("");

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

    private void doStuff(){
        toolbar.post(new Runnable() {
            // Post in the parent's message queue to make sure the parent
            // lays out its children before you call getHitRect()
            @Override
            public void run() {
                Rect delegateArea = new Rect();
                goBackBtn.setEnabled(true);
                goBackBtn.setOnClickListener(customListener);

                // The hit rectangle for the ImageButton
                goBackBtn.getHitRect(delegateArea);

                // Extend the touch area of the ImageButton beyond its bounds
                // on the right and bottom.
                delegateArea.right += 150;
                delegateArea.bottom += 150;
                delegateArea.top += 50;


                // Instantiate a TouchDelegate.
                // "delegateArea" is the bounds in local coordinates of
                // the containing view to be mapped to the delegate view.
                // "myButton" is the child view that should receive motion
                // events.
                TouchDelegate touchDelegate = new TouchDelegate(delegateArea,
                        goBackBtn);

                // Sets the TouchDelegate on the parent view, such that touches
                // within the touch delegate bounds are routed to the child.
                if (View.class.isInstance(goBackBtn.getParent())) {
                    ((View) goBackBtn.getParent()).setTouchDelegate(touchDelegate);
                }
            }
        });
    }

    private View.OnClickListener customListener = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId() /*to get clicked view id**/) {
                case R.id.send_btn:
                    try {
                        if (chatText.length() != 0) {
                            GetXMLTask task = new GetXMLTask();
                            task.execute(URL);
                            sendChatMessage();
                            chatText.setText("");
                            chatText.setHint("Skriv ett meddelande...");
                        }

                    }
                    catch (RuntimeException ex){
                        Log.v("RUNTIME","EXCEPTION"+ex);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;
                /*case R.id.btn_back:
                    try {
                        Intent myIntent = new Intent(getContext(), ChatFragment.class);
                        myIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivityForResult(myIntent, 0);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }*/

                default:
                    break;
            }
        }
    };

    private boolean sendChatMessage(){
        chatArrayAdapter.add(new MessageContainer(false, chatText.getText().toString()));
        chatText.setText("");
        return true;
    }

    private void recieveChatMessage(String message){
        if (message != null) {
            chatArrayAdapter.add(new MessageContainer(true, message));
        }

    }
    /**
     * Klass för att hämta data från server
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
            StringBuffer output = new StringBuffer("");
            try {
                InputStream stream = getHttpConnection(url);
                BufferedReader buffer = new BufferedReader(new InputStreamReader(stream, "UTF-8"));

                String s = "";
                while ((s = buffer.readLine()) != null)
                    output.append(s);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            return output.toString();

        }

        // Makes HttpURLConnection and returns InputStream
        private InputStream getHttpConnection(String urlString)
                throws IOException {
            InputStream stream = null;
            //EditText message = (EditText) findViewById(R.id.msgBox);
            String sendMessage = chatText.getText().toString();
            String requestURL = urlString + "?message=" + URLEncoder.encode(sendMessage, "UTF-8");
            java.net.URL url = new URL(requestURL);
            requestURL = URLDecoder.decode(requestURL, "UTF-8");
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

        @Override
        protected void onPostExecute(String output) {
            try {
                output = URLDecoder.decode(output, "UTF-8");
                recieveChatMessage(output);



            } catch (Exception e) {
                showDialog("Kunde inte nå servern, kontrollera din internetuppkoppling.");
                recieveChatMessage("HEJ GOS");
            }
        }
    }

}
