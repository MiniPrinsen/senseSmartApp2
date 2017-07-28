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
import android.widget.RelativeLayout;

import com.example.gustaf.touchpoint.Adapters.ChatArrayAdapter;
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
    public static final String URL ="http://35.158.191.6:8080/sensesmart/hey";
    private EditText chatText;
    RelativeLayout chatBackground;
    //ImageView backButton;
    //ImageView circleToolbar;
    //ImageView infoButton;



    private ChatArrayAdapter chatArrayAdapter;
    private ListView listView;
    //protected Toolbar toolbar;
    View view;
    private String circleString;

    public ChatWindowFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_chat_window, container, false);
        findViewsById(view);
        Bundle bundle = this.getArguments();
        if(bundle != null) {
            circleString = bundle.getString("cityobject");
        }

        //Picasso.with(getContext()).load(circleString).transform(new Blur().getTransformation(getContext(), circleString)).into(chatBackground);


        /*circleToolbar = new ImageView(getContext());
        Picasso.with(getContext()).load(circleString).transform(new CircleImageTransformation()).into(circleToolbar, new com.squareup.picasso.Callback() {
            @Override
            public void onSuccess() {

                int color = Color.parseColor("#54D87C");
                circleToolbar.setBackgroundColor(color);
                circleToolbar.setBackgroundResource(R.drawable.roundcorner);
            }

            @Override
            public void onError() {

            }
        });
        infoButton = new ImageView(getContext());
        infoButton.setImageResource(R.drawable.ic_arrow_back);
        backButton = new ImageView(getContext());
        backButton.setImageResource(R.drawable.ic_arrow_back);
        int color = Color.parseColor("#51ACC7");
        circleToolbar.setLayoutParams(new Toolbar.LayoutParams(130,130,Gravity.CENTER_HORIZONTAL|Gravity.TOP));
        infoButton.setColorFilter(color);
        infoButton.setLayoutParams(new Toolbar.LayoutParams(70,70,Gravity.END));

        toolbar.addView(infoButton);
        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InfoChatFragment infoChatFragment = new InfoChatFragment();
                FragmentManager fragManager = getActivity().getSupportFragmentManager();
                fragManager
                        .beginTransaction()
                        .addToBackStack(null)
                        .replace(android.R.id.content, infoChatFragment)
                        .commit();
            }
        });
        toolbar.addView(circleToolbar);
        backButton.setColorFilter(color);
        backButton.setLayoutParams(new Toolbar.LayoutParams(70,70, Gravity.START));
        toolbar.addView(backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(getContext());
                removeFragment();
            }
        });*/
      //  ((BaseActivity)getActivity()).hideNavigationBar(true);
        //setToolBarTitle(null, view);
        buttonSend.setOnClickListener(customListener);

        chatArrayAdapter = new ChatArrayAdapter(getContext(), R.layout.chatbubbles_layout);
        listView.setAdapter(chatArrayAdapter);

        listView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                hideKeyboard(getContext());

                return false;
            }
        });
        /*View parent = view.findViewById(R.id.chat_window_content);
        parent.post(new Runnable() {
            public void run() {
                // Post in the parent's message queue to make sure the parent
                // lays out its children before we call getHitRect()
                Rect delegateArea = new Rect();
                ImageView delegate = backButton;
                delegate.getHitRect(delegateArea);
                delegateArea.top -= 600;
                delegateArea.bottom += 300;
                delegateArea.left -= 600;
                delegateArea.right += 300;
                TouchDelegate expandedArea = new TouchDelegate(delegateArea,
                        delegate);
                // give the delegate to an ancestor of the view we're
                // delegating the
                // area to
                if (View.class.isInstance(delegate.getParent())) {
                    ((View) delegate.getParent())
                            .setTouchDelegate(expandedArea);
                }
            };
        });*/



        return view;
    }

    private void findViewsById(View container) {
        buttonSend = (Button) container.findViewById(R.id.send_btn);
        chatBackground = (RelativeLayout) container.findViewById(R.id.chatBackground);
        //goBackBtn = (Button)container.findViewById(R.id.btn_back);
        chatText = (EditText)container.findViewById(R.id.msgBox);
        listView = (ListView) container.findViewById(R.id.listanmedView);
        //toolbar = (Toolbar) container.findViewById(R.id.main_toolbar);


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

    public static void hideKeyboard(Context ctx) {
        InputMethodManager inputManager = (InputMethodManager) ctx
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        // check if no view has focus:
        View v = ((Activity) ctx).getCurrentFocus();
        if (v == null)
            return;

        inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    private boolean sendChatMessage(){
        chatArrayAdapter.add(new MessageContainer(false, chatText.getText().toString()));
        chatText.setText("");

        return true;
    }

    private void recieveChatMessage(String message){
        if (message != null) {
            chatArrayAdapter.add(new MessageContainer(true, message));
            listView.setSelection(chatArrayAdapter.getCount() - 1);
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
                showDialog("Something went wrong, try again");
            }
        }
    }

}
