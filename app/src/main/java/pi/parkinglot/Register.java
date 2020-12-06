package pi.parkinglot;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

import com.google.android.material.textfield.TextInputLayout;

import java.util.regex.Pattern;

public class Register extends AppCompatActivity {

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
    }

    protected void register(){

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