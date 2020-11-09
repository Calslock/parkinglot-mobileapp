package pi.parkinglot;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class MainActivity extends AppCompatActivity {

    EditText loginbox;
    EditText passwordbox;
    TextView testv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Parkinglot b29");
        loginbox = (EditText) findViewById(R.id.loginEmail);
        passwordbox = (EditText) findViewById(R.id.loginPassword);
        testv = (TextView) findViewById(R.id.stView);
        Log.i("Test", "to działa?");
    }

    public void goToRegister(View view) {
        Intent intent = new Intent(this, Register.class);
        String login = loginbox.getText().toString();
        intent.putExtra("logintoreg", login);
        startActivity(intent);
    }

    public void login(View view){
        String login = loginbox.getText().toString();
        String password = passwordbox.getText().toString();
        final JSONObject[] jresponse = new JSONObject[1];
            if(!login.matches("") && !password.matches("")){
                Log.i("Accepted credentials", "true");
                try {
                    JSONObject credentials = new JSONObject();
                    credentials.put("username", login);
                    credentials.put("password", password);
                    Log.i("JSON:", credentials.toString());

                    RequestQueue queue = Volley.newRequestQueue(this);

                    JsonObjectRequest jsonReq = new JsonObjectRequest("http://192.168.0.2:9000/parkinglot-management-system/auth/signin",
                            credentials,
                            new Response.Listener<JSONObject>(){
                                @Override
                                public void onResponse(JSONObject response) {
                                    Log.i("Volley", response.toString());
                                    jresponse[0] = response;
                                }},
                            new Response.ErrorListener(){
                                @Override
                                public void onErrorResponse(VolleyError error){
                                Log.e("Volley", error.toString());
                                }
                            });

                    queue.add(jsonReq);
                }catch(Exception e){
                    Log.e("ExceptionError", e.toString());
                }
            }

    }
}