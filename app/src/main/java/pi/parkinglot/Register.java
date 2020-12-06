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
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.regex.Pattern;

public class Register extends AppCompatActivity {

    EditText
        loginbox,
        passwordbox,
        firstnamebox,
        lastnamebox,
        companynamebox,
        confirmpasswordbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setTitle("Rejestracja");
        this.setupFloatingLabelError();

        String login = "";
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                if (!extras.getString("logintoreg").equals("")) {
                    login = extras.getString("logintoreg");
                }
            }
        } else {
            if (!((String) savedInstanceState.getSerializable("logintoreg")).equals("")) {
                login = (String) savedInstanceState.getSerializable("logintoreg");
            }
            EditText logreg = (EditText) findViewById(R.id.emailRegister);
            logreg.setText(login);
        }

        loginbox = (EditText) findViewById(R.id.emailRegister);
        passwordbox = (EditText) findViewById(R.id.passwordRegister);
        firstnamebox = (EditText) findViewById(R.id.firstName);
        lastnamebox = (EditText) findViewById(R.id.lastName);
        companynamebox = (EditText) findViewById(R.id.companyName);
        confirmpasswordbox = (EditText) findViewById(R.id.passwordConfirm);
    }

    public void register(View view){
        String login = loginbox.getText().toString();
        String password = passwordbox.getText().toString();
        String firstname = firstnamebox.getText().toString();
        String lastname = lastnamebox.getText().toString();
        String companyname = companynamebox.getText().toString();
        String confirmpassword = confirmpasswordbox.getText().toString();
        final String url = "http://192.168.0.2:9000/parkinglot-management-system/auth/signup";
        if(checkRequirements(login, password, firstname, lastname, companyname, confirmpassword)){
            try {
                JSONObject credentials = new JSONObject();
                credentials.put("firstName", firstname);
                credentials.put("lastName", lastname);
                credentials.put("companyName", companyname);
                credentials.put("username", login);
                credentials.put("password", password);
                JSONArray roleArray = new JSONArray();
                roleArray.put("");
                credentials.put("roles", roleArray);
                RequestQueue queue = Volley.newRequestQueue(this);
                JsonObjectRequest jsonReq = new JsonObjectRequest(
                        url,
                        credentials,
                        new Response.Listener<JSONObject>(){
                            @Override
                            public void onResponse(JSONObject response) {
                                Toaster.makeToast(getApplicationContext(), "Pomyślnie zarejestrowano! Kliknij w link w wiadomości wysłanej na e-mail aby potwierdzić konto.");
                                gotoLogin();
                            }},
                        new Response.ErrorListener(){
                            @Override
                            public void onErrorResponse(VolleyError error){
                                Log.e("Volley", error.toString());
                                Toaster.makeToast(getApplicationContext(), "Wystąpił błąd podczas przetwarzania żądania");
                            }
                        });

                jsonReq.setRetryPolicy(new DefaultRetryPolicy(
                        5000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                ));

                queue.add(jsonReq);
            }catch(Exception e){
                Log.e("ExceptionError", e.toString());
            }
        }
    }


    private boolean checkRequirements(String login, String password, String firstname, String lastname, String companyname, String confirmpassword){
        if (!(companyname.length()==0) && companyname.length()<3){
            Toaster.makeToast(this, "Nieprawidłowa nazwa firmy");
            return false;
        }
        if (!login.matches(".+@.+\\..+")){
            Toaster.makeToast(this, "E-mail jest nieprawidłowy");
            return false;
        }
        if (password.length()<8){
            Toaster.makeToast(this, "Hasło jest nieprawidłowe");
            return false;
        }
        if (!password.equals(confirmpassword)){
            Toaster.makeToast(this, "Hasła nie zgadzają się");
            return false;
        }
        if (!firstname.matches("(([A-ZĘĄŚŁŻŹĆŃÓ])[a-zżźćńółęąś]{2,})")){
            Toaster.makeToast(this, "Nieprawidłowe imię");
            return false;
        }
        if (!lastname.matches("(([A-ZĘĄŚŁŻŹĆŃÓ])[a-z-A-ZżźćńółęąśĘĄŚŁŻŹĆŃÓ]{2,})")){
            Toaster.makeToast(this, "Nieprawidłowe nazwisko");
            return false;
        }
        return true;
    }

    public void gotoLogin(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        this.finish();
    }

    private void setupFloatingLabelError(){
        final TextInputLayout
                emailLabel = (TextInputLayout)findViewById(R.id.registerEmailLayout),
                passwordLabel = (TextInputLayout)findViewById(R.id.registerPasswordLayout),
                companyNameLabel = (TextInputLayout)findViewById(R.id.registerCompanyNameLayout),
                firstNameLabel = (TextInputLayout)findViewById(R.id.registerFirstNameLayout),
                lastNameLabel = (TextInputLayout)findViewById(R.id.registerLastNameLayout),
                confirmPasswordLabel = (TextInputLayout)findViewById(R.id.registerPasswordConfirmLayout);

        passwordLabel.setHelperText("Hasło powinno zawierać co najmniej 8 znaków");
        passwordLabel.setHelperTextEnabled(true);
        passwordLabel.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()==0) {
                    passwordLabel.setErrorEnabled(false);
                    passwordLabel.setHelperText("Hasło musi zawierać co najmniej 8 znaków");
                    passwordLabel.setHelperTextEnabled(true);
                }
                else if(s.length()>0 && s.length()<8) {
                    passwordLabel.setHelperTextEnabled(false);
                    passwordLabel.setError("Hasło musi zawierać co najmniej 8 znaków");
                    passwordLabel.setErrorEnabled(true);
                } else {
                    passwordLabel.setHelperTextEnabled(false);
                    passwordLabel.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        emailLabel.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

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
            public void afterTextChanged(Editable s) {

            }
        });

        companyNameLabel.setHelperText("Niewymagane");
        companyNameLabel.setHelperTextEnabled(true);

        companyNameLabel.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()==0){
                    companyNameLabel.setHelperText("Niewymagane");
                    companyNameLabel.setHelperTextEnabled(true);
                } else if (s.length()<3){
                    companyNameLabel.setHelperTextEnabled(false);
                    companyNameLabel.setError("Nazwa firmy musi zawierać co najmniej 3 znaki");
                    companyNameLabel.setErrorEnabled(true);
                } else {
                    companyNameLabel.setErrorEnabled(false);
                    companyNameLabel.setHelperTextEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        firstNameLabel.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!Pattern.matches("(([A-ZĘĄŚŁŻŹĆŃÓ])[a-zżźćńółęąś]{2,})", s)){
                    firstNameLabel.setError("Niepoprawny format");
                    firstNameLabel.setErrorEnabled(true);
                } else {
                    firstNameLabel.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        lastNameLabel.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!Pattern.matches("(([A-ZĘĄŚŁŻŹĆŃÓ])[a-z-A-ZżźćńółęąśĘĄŚŁŻŹĆŃÓ]{2,})", s)){
                    lastNameLabel.setError("Niepoprawny format");
                    lastNameLabel.setErrorEnabled(true);
                } else {
                    lastNameLabel.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        confirmPasswordLabel.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().equals(passwordLabel.getEditText().getText().toString())){
                    confirmPasswordLabel.setError("Hasła nie zgadzają się");
                    confirmPasswordLabel.setErrorEnabled(true);
                } else {
                    confirmPasswordLabel.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}