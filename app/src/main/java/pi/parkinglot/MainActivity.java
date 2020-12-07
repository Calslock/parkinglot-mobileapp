package pi.parkinglot;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    EditText loginbox;
    EditText passwordbox;
    TextView versionbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Parkinglot");
        this.setupFloatingLabelError();
        loginbox = (EditText) findViewById(R.id.loginEmail);
        passwordbox = (EditText) findViewById(R.id.loginPassword);
        versionbox = (TextView) findViewById(R.id.version);
        versionbox.setText("v0.3b120");
    }

    public void goToRegister(View view) {
        Intent intent = new Intent(this, Register.class);
        String login = loginbox.getText().toString();
        intent.putExtra("logintoreg", login);
        startActivity(intent);
    }

    public void gotoMain(JSONObject response){
        Intent intent = new Intent(this, MainApp.class);
        intent.putExtra("userdata", response.toString());
        startActivity(intent);
        this.finish();
    }

    public void login(View view){
        String login = loginbox.getText().toString();
        String password = passwordbox.getText().toString();
        final String url = "http://192.168.0.2:9000/parkinglot-management-system/auth/signin";
        if(login.matches(".+@.+\\..+") && !password.matches("")){
                try {
                    JSONObject credentials = new JSONObject();
                    credentials.put("username", login);
                    credentials.put("password", password);
                    RequestQueue queue = Volley.newRequestQueue(this);
                    JsonObjectRequest jsonReq = new JsonObjectRequest(
                            url,
                            credentials,
                            new Response.Listener<JSONObject>(){
                                @Override
                                public void onResponse(JSONObject response) {
                                    gotoMain(response);
                                }},
                            new Response.ErrorListener(){
                                @Override
                                public void onErrorResponse(VolleyError error){
                                Log.e("Volley", error.toString());
                                Toaster.makeToast(getApplicationContext(), "Wystąpił błąd z logowaniem");
                                }
                            });
                    queue.add(jsonReq);
                }catch(Exception e){
                    Log.e("ExceptionError", e.toString());
                }
            }
    }

    private void setupFloatingLabelError(){
        final TextInputLayout emailLabel = (TextInputLayout)findViewById(R.id.emailTextInputLayout);
        //final TextInputLayout passwordLabel = (TextInputLayout)findViewById(R.id.passwordTextInputLayout);

        emailLabel.getEditText().addTextChangedListener(new TextWatcher(){
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!Pattern.matches(".+@.+\\..+", s)){
                    emailLabel.setError("Podaj poprawny email");
                    emailLabel.setErrorEnabled(true);
                } else {
                    emailLabel.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }
}