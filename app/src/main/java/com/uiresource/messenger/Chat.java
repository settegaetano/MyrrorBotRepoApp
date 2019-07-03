package com.uiresource.messenger;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.uiresource.messenger.recylcerchat.ChatData;
import com.uiresource.messenger.recylcerchat.ConversationRecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

public class Chat extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView mRecyclerView;
    private ConversationRecyclerView mAdapter;
    private EditText text;
    private Button send;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setupToolbarWithUpNav(R.id.toolbar, "Nome del bot", R.drawable.ic_action_back);


        //Drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //RecyclerView
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new ConversationRecyclerView(this,setData());
        mRecyclerView.setAdapter(mAdapter);

        /*mRecyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mRecyclerView.smoothScrollToPosition(mRecyclerView.getAdapter().getItemCount() - 1);
            }
        }, 1000);*/

        text = (EditText) findViewById(R.id.et_message);
        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRecyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            mRecyclerView.smoothScrollToPosition(mRecyclerView.getAdapter().getItemCount() - 1);
                        }catch (IllegalArgumentException e){
                            e.getMessage();
                        }
                    }
                }, 500);
            }
        });
        send = (Button) findViewById(R.id.bt_send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!text.getText().equals("")) {
                    List<ChatData> data = new ArrayList<ChatData>();
                    ChatData item = new ChatData();
                    Date currentTime = Calendar.getInstance().getTime();
                    item.setTime(String.valueOf(currentTime.getHours()) + ":" + String.valueOf(currentTime.getMinutes()));
                    item.setType("2");//Imposto il layout della risposta, ovvero YOU
                    String mess = text.getText().toString(); //Domanda dell'utente
                    item.setText(mess);
                    data.add(item);
                    mAdapter.addItem(data);
                    mRecyclerView.smoothScrollToPosition(mRecyclerView.getAdapter().getItemCount() - 1);
                    text.setText("");
                    Background b = new Background();
                    b.execute(mess);
                }
            }
        });

        //Quando l'utente preme fuori dalla tastiera, quest'ultima viene nascosta
        mRecyclerView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                return false;
            }
        });

    }


    public List<ChatData> setData(){
        List<ChatData> data = new ArrayList<>();

        /*String text[] = {"15 September","Hi, Julia! How are you?", "Hi, Joe, looks great! :) ", "I'm fine. Wanna go out somewhere?", "Yes! Coffe maybe?", "Great idea! You can come 9:00 pm? :)))", "Ok!", "Ow my good, this Kit is totally awesome", "Can you provide other kit?", "I don't have much time, :`("};
        String time[] = {"", "5:30pm", "5:35pm", "5:36pm", "5:40pm", "5:41pm", "5:42pm", "5:40pm", "5:41pm", "5:42pm"};
        String type[] = {"0", "2", "1", "1", "2", "1", "2", "2", "2", "1"};

        for (int i=0; i<text.length; i++){
            ChatData item = new ChatData();
            item.setType(type[i]);
            item.setText(text[i]);
            item.setTime(time[i]);
            data.add(item);
        }*/




        return data;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    //OptionMenu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_userphoto, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_chats) {
            Intent intent = new Intent(Chat.this,Chat.class);
            startActivity(intent);
        } else if (id == R.id.nav_logout) {
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public class Background extends AsyncTask<String, Void, ArrayList<String>> {

        @Override
        protected void onPreExecute() {
        }

        /**
         * Operazioni effettuate dopo il Post
         *
         * @param s
         */
        @Override
        protected void onPostExecute(ArrayList<String> s) {

            try {

                //Esempio valore JSON {"intentName":"Identita utente","confidence":1,"answer":"Ti chiami Cataldo Musto"}

                String result = s.get(0);
                String mess = s.get(1);

                JSONObject arr = new JSONObject(result);

                String answer = arr.getString("answer");
                String intentName = arr.getString("intentName");
                double confidence = Double.parseDouble(arr.getString("confidence"));

                Log.e("INTENT", arr.toString());


                //YOUTUBE --> Valori soglia diversi
                if(intentName.equalsIgnoreCase("Video in base alle emozioni") || intentName.equalsIgnoreCase("Ricerca Video")){

                    List<ChatData> data = new ArrayList<ChatData>();
                    ChatData item = new ChatData();
                    Date currentTime = Calendar.getInstance().getTime();
                    item.setTime(String.valueOf(currentTime.getHours()) + ":" + String.valueOf(currentTime.getMinutes()));
                    item.setType("6");//Imposto il layout della risposta, ovvero YOUTUBE

                    //Url da visualizzare
                    String url = answer;

                    item.setText(url);
                    data.add(item);
                    mAdapter.addItem(data);
                    mRecyclerView.smoothScrollToPosition(mRecyclerView.getAdapter().getItemCount() - 1);


                }else if (intentName.equalsIgnoreCase("Interessi")  //INTENT GENERICI
                            || intentName.equalsIgnoreCase("Contatti")
                            || intentName.equalsIgnoreCase("Esercizio fisico")
                            || intentName.equalsIgnoreCase("Personalita")) {

                    Log.w("ANSWER",answer);

                    List<ChatData> data = new ArrayList<ChatData>();
                    ChatData item = new ChatData();
                    Date currentTime = Calendar.getInstance().getTime();
                    item.setTime(String.valueOf(currentTime.getHours()) + ":" + String.valueOf(currentTime.getMinutes()));
                    item.setType("1");

                    item.setText(answer);
                    data.add(item);
                    mAdapter.addItem(data);
                    mRecyclerView.smoothScrollToPosition(mRecyclerView.getAdapter().getItemCount() - 1);
                }else {
                    Log.w("ANSWER",answer);

                    List<ChatData> data = new ArrayList<ChatData>();
                    ChatData item = new ChatData();
                    Date currentTime = Calendar.getInstance().getTime();
                    item.setTime(String.valueOf(currentTime.getHours()) + ":" + String.valueOf(currentTime.getMinutes()));
                    item.setType("1");

                    item.setText(answer);
                    data.add(item);
                    mAdapter.addItem(data);
                    mRecyclerView.smoothScrollToPosition(mRecyclerView.getAdapter().getItemCount() - 1);

                }



                //PER I DATI NORMALI
                /*Log.w("ANSWER",answer);

                List<ChatData> data = new ArrayList<ChatData>();
                ChatData item = new ChatData();
                Date currentTime = Calendar.getInstance().getTime();
                item.setTime(String.valueOf(currentTime.getHours()) + ":" + String.valueOf(currentTime.getMinutes()));
                item.setType("1");

                item.setText(answer);
                data.add(item);
                mAdapter.addItem(data);
                mRecyclerView.smoothScrollToPosition(mRecyclerView.getAdapter().getItemCount() - 1);*/


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        /**
         * Operazioni da effettuare in background
         *
         * @param voids
         * @return
         */
        @Override
        protected ArrayList<String> doInBackground(String... voids) {

            String mess = voids[0];//Domanda dell'utente
            String result = "";
            String urlString = "http://settenettis.altervista.org/php/intentDetection.php";//Url per la query
            //String urlString = "http://localhost/MyrrorBot/php/intentDetection.php";

            try {

                //Imposto parametri per la connessione
                URL url = new URL(urlString);
                HttpURLConnection http = (HttpURLConnection) url.openConnection();
                http.setRequestMethod("POST");
                http.setDoInput(true);
                http.setDoOutput(true);


                OutputStream ops = http.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(ops,"UTF-8"));

                //Stringa di output
                String data = URLEncoder.encode("testo", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(voids[0]), "UTF-8");

                writer.write(data);
                writer.flush();
                writer.close();
                ops.close();

                InputStream ips = http.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(ips, "ISO-8859-1"));

                String line = "";

                while ((line = reader.readLine()) != null) {
                    result += line;


                    reader.close();
                    ips.close();
                    http.disconnect();

                }

                ArrayList<String> arrayList = new ArrayList<>();
                arrayList.add(result);
                arrayList.add(mess);

                return arrayList;

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            ArrayList<String> arrayList = new ArrayList<>();
            arrayList.add(result);
            arrayList.add(mess);

            return arrayList;
        }
    }

    //Conta le parole in una frase
    public static int countWords(String sentence) {
        if (sentence == null || sentence.isEmpty()) {
            return 0;
        }
        StringTokenizer tokens = new StringTokenizer(sentence);
        return tokens.countTokens();
    }

    //Inserisce stringa in un'altra
    public static String insertString(String originalString, String stringToBeInserted, int index) {

        // Create a new string
        String newString = new String();

        for (int i = 0; i < originalString.length(); i++) {

            // Insert the original string character into the new string
            newString += originalString.charAt(i);

            if (i == index) {

                // Insert the string to be inserted into the new string
                newString += stringToBeInserted;
            }
        }

        //Return the modified String
        return newString;
    }
}
