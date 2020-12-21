package pi.parkinglot;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    EditText loginbox;
    EditText passwordbox;

    JWToken userToken;
    User user;

    UserRoomDatabase userDB;
    UserDao userDao;

    //TODO user only verification
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Parkinglot");
        this.setupFloatingLabelError();
        loginbox = (EditText) findViewById(R.id.loginEmail);
        passwordbox = (EditText) findViewById(R.id.loginPassword);
        userDB = UserRoomDatabase.getDatabase(getApplicationContext());
        userDao = userDB.userDao();
    }

    public void goToRegister(View view) {
        Intent intent = new Intent(this, Register.class);
        String login = loginbox.getText().toString();
        intent.putExtra("logintoreg", login);
        startActivity(intent);
    }

    public void createUserGoToMain(JSONObject response){
        user = new User(response);
        new Thread(new Runnable() {
            @Override
            public void run() {
                userDao.deleteAll();
                userDao.insert(user);
                Intent intent = new Intent(getApplicationContext(), MainApp.class);
                intent.putExtra("userToken", userToken);
                startActivity(intent);
            }
        }).start();
        this.finish();
    }

    public void getToken(View view){
        String login = loginbox.getText().toString();
        String password = passwordbox.getText().toString();
        final String url = "http://192.168.0.2:9000/parkinglot-management-system/auth/signin";
        if(login.matches(".+@.+\\..+") && !password.matches("")){
                try {
                    JSONObject credentials = new JSONObject();
                    credentials.put("username", login);
                    credentials.put("password", password);
                    RequestQueue queue = Volley.newRequestQueue(this);
                    JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.POST,
                            url,
                            credentials,
                            response -> login(response),
                            error -> {
                            Log.e("Volley", error.toString());
                            Toaster.makeToast(getApplicationContext(),
                                    "Wystąpił błąd z logowaniem");
                            });
                    queue.add(jsonReq);
                }catch(Exception e){
                    Log.e("ExceptionError", e.toString());
                }
            }
    }

    public void login(JSONObject response){
        userToken = new JWToken(response);
        try {
            final String url = "http://192.168.0.2:9000/parkinglot-management-system/api/users/" + userToken.getId();
            RequestQueue queue = Volley.newRequestQueue(this);
            JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.GET,
                    url,
                    null,
                    new Response.Listener<JSONObject>(){
                        @Override
                        public void onResponse(JSONObject response) {
                            createUserGoToMain(response);
                        }},
                    new Response.ErrorListener(){
                        @Override
                        public void onErrorResponse(VolleyError error){
                            Log.e("Volley", error.toString());
                            Toaster.makeToast(getApplicationContext(), "Nie można było pobrać danych użytkownika");
                        }
                    }){
                @Override
                public Map<String, String> getHeaders(){
                    Map<String, String> params = new HashMap<>();
                    params.put("Authorization", userToken.getTokenType() + " " + userToken.getAccessToken());
                    return params;
                }
            };
            queue.add(jsonReq);
        } catch (Exception e) {
            Log.e("ExceptionError", e.toString());
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