package edu.temple.jsonparser_11_5_19;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    ImageView dogPicture;
    TextView textView;

    Handler dogHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            // response to process from worker thread - String received here is JSON String, knowing this convert it to JSONObject
            try {
                JSONObject dogObject = new JSONObject(msg.obj.toString()); // one constructor takes String and creates object for you
                // Access
                String status = dogObject.getString("status"); // <--- key for JSON object to get it's value
                if (status.equals("success")) {
                    textView.setText("Fetch successful!");
                    // Take value associated with "message" and use that image as ImageView's source, installed Picasso as dependency to load image
                    Picasso.get().load(dogObject.getString("message")).into(dogPicture); // <--- message is key for value of URL of image to use
                } else {

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return true;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dogPicture = findViewById(R.id.imageView);
        textView = findViewById(R.id.textView);


        new Thread() {
            @Override
            public void run() {
                URL url = null;
                try {
                    url = new URL("https://dog.ceo/api/breeds/image/random");
                    BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
                    StringBuilder builder = new StringBuilder(); // StringBuilder, keep adding on bits of a string
                    String response;
                    while ((response = reader.readLine()) != null) {
                        builder.append(response);
                    }
                    // Need to use Handler
                    Message msg = Message.obtain();
                    msg.obj = builder.toString(); // gives you string created from StringBuilder object
                    dogHandler.sendMessage(msg);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }
}
