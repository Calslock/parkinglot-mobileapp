package pi.parkinglot.accountData;

import androidx.lifecycle.ViewModelProviders;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import pi.parkinglot.JWToken;
import pi.parkinglot.R;
import pi.parkinglot.Toaster;
import pi.parkinglot.User;
import pi.parkinglot.UserDao;
import pi.parkinglot.UserRoomDatabase;

public class accountDataFragment extends Fragment {

    private AccountDataViewModel mViewModel;
    JWToken userToken;
    User user;

    UserRoomDatabase userDB;
    UserDao userDao;

    TextView firstName, lastName, companyName, username;
    TextView sideBarName, sideBarUsername;
    Button bChangePassword, bChangeAccountData;
    EditText oldPassword, passwordChange, confirmPasswordChange;
    EditText eFirstName, eLastName, eCompanyName, eUsername;

    AlertDialog.Builder builder;

    public static accountDataFragment newInstance() {
        return new accountDataFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mViewModel = ViewModelProviders.of(this).get(AccountDataViewModel.class);
        View root = inflater.inflate(R.layout.account_data_fragment, container, false);
        userDB = UserRoomDatabase.getDatabase(mContext);
        userDao = userDB.userDao();
        try {
            Bundle bundle = getActivity().getIntent().getExtras();
            userToken = bundle.getParcelable("userToken");

            builder = new AlertDialog.Builder(mContext);

            long id = userToken.getId();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    user = userDao.getUser(id);

                    updateData(root);
                    updateSideBar(root);

                    bChangePassword = (Button) root.findViewById(R.id.acChangePasswordButton);
                    bChangePassword.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            builder.setTitle("Zmień hasło");
                            LayoutInflater inflater = getLayoutInflater();
                            View dialogView = inflater.inflate(R.layout.change_password_dialog, null);
                            builder.setView(dialogView);

                            builder.setPositiveButton("Zmień hasło", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    oldPassword = (EditText) dialogView.findViewById(R.id.acOldPasswordEditText);
                                    passwordChange = (EditText) dialogView.findViewById(R.id.acChangePasswordEditText);
                                    confirmPasswordChange = (EditText) dialogView.findViewById(R.id.acChangePasswordConfirmTextEdit);

                                    String old = oldPassword.getText().toString();
                                    String password = passwordChange.getText().toString();
                                    String confirmPassword = confirmPasswordChange.getText().toString();

                                    JSONObject jsonMiniUser = new JSONObject();

                                    if(password.length()>=8 || password.equals(confirmPassword)) {
                                        try {
                                            jsonMiniUser.put("id", user.getId());
                                            jsonMiniUser.put("username", user.getId());
                                            jsonMiniUser.put("oldPassword", old);
                                            jsonMiniUser.put("newPassword", password);

                                            final String url = "http://192.168.0.2:9000/parkinglot-management-system/api/users/" + user.getUsername() + "/changePassword";
                                                RequestQueue queue = Volley.newRequestQueue(mContext);
                                                JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.PUT,
                                                        url,
                                                        jsonMiniUser,
                                                        new Response.Listener<JSONObject>(){
                                                            @Override
                                                            public void onResponse(JSONObject response) {
                                                                Toaster.makeToast(mContext, "Pomyślnie zmieniono hasło!");
                                                            }},
                                                        new Response.ErrorListener(){
                                                            @Override
                                                            public void onErrorResponse(VolleyError error){
                                                                Log.e("Volley", error.toString());
                                                                Toaster.makeToast(mContext, "Wystąpił błąd z wysyłaniem danych");
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
                                            }catch(Exception e){
                                                Log.e("ExceptionError", e.toString());
                                            }
                                    }
                                }
                            });

                            builder.setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });

                            builder.show();
                            setupFloatingLabelErrorChangePassword(dialogView);
                        }
                    });

                    bChangeAccountData = (Button) root.findViewById(R.id.acEditAccountDataButton);
                    bChangeAccountData.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            builder.setTitle("Zmień dane konta");
                            LayoutInflater inflater = getLayoutInflater();
                            View dialogView = inflater.inflate(R.layout.change_account_data_dialog, null);
                            builder.setView(dialogView);

                            builder.setPositiveButton("Edytuj dane", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    eFirstName = (EditText) dialogView.findViewById(R.id.acChangeFirstNameEditText);
                                    eLastName = (EditText) dialogView.findViewById(R.id.acChangeLastNameEditText);
                                    eCompanyName = (EditText) dialogView.findViewById(R.id.acChangeCompanyNameEditText);
                                    eUsername = (EditText) dialogView.findViewById(R.id.acChangeUsernameEditText);

                                    String firstName = eFirstName.getText().toString();
                                    String lastName = eLastName.getText().toString();
                                    String companyName = eCompanyName.getText().toString();
                                    String username = eUsername.getText().toString();

                                    if(firstName.length()!=0 && Pattern.matches("(([A-ZĘĄŚŁŻŹĆŃÓ])[a-zżźćńółęąś]{2,})", firstName)){
                                        user.setFirstName(firstName);
                                    }
                                    if(lastName.length()!=0 && Pattern.matches("(([A-ZĘĄŚŁŻŹĆŃÓ])[a-z-A-ZżźćńółęąśĘĄŚŁŻŹĆŃÓ]{2,})", lastName)){
                                        user.setLastName(lastName);
                                    }
                                    if(companyName.length()>=3){
                                        user.setCompanyName(companyName);
                                    }
                                    if(firstName.length()!=0 && Pattern.matches(".+@.+\\..+", username)) {
                                        user.setUsername(username);
                                    }

                                    JSONObject jsonUser = user.toJSON();

                                    final String url = "http://192.168.0.2:9000/parkinglot-management-system/api/users/" + user.getId();
                                        try {
                                            RequestQueue queue = Volley.newRequestQueue(mContext);
                                            JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.PUT,
                                                    url,
                                                    jsonUser,
                                                    new Response.Listener<JSONObject>(){
                                                        @Override
                                                        public void onResponse(JSONObject response) {
                                                            updateData(root);
                                                            updateSideBar(root);
                                                        }},
                                                    new Response.ErrorListener(){
                                                        @Override
                                                        public void onErrorResponse(VolleyError error){
                                                            Log.e("Volley", error.toString());
                                                            Toaster.makeToast(mContext, "Wystąpił błąd z wysyłaniem danych");
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
                                        }catch(Exception e){
                                            Log.e("ExceptionError", e.toString());
                                        }
                                }
                            });

                            builder.setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });

                            builder.show();
                            setupFloatingLabelErrorEditAccountData(dialogView);
                        }
                    });

                }
            }).start();
        } catch(Exception e){
            Log.e("ExceptionError", e.toString());
        }
        return root;
    }

    private Context mContext;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mContext = null;
    }

    private void setupFloatingLabelErrorChangePassword(View root){
        final TextInputLayout oldPassword = (TextInputLayout) root.findViewById(R.id.acOldPasswordLabel);
        final TextInputLayout passwordLabel = (TextInputLayout) root.findViewById(R.id.acChangePasswordLabel);
        final TextInputLayout confirmPasswordLabel = (TextInputLayout) root.findViewById(R.id.acChangePasswordConfirmLabel);


        oldPassword.setError("To pole nie może być puste");
        oldPassword.setErrorEnabled(true);
        oldPassword.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()>0){
                    oldPassword.setErrorEnabled(false);
                }else{
                    oldPassword.setError("To pole nie może być puste");
                    oldPassword.setErrorEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

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

    private void setupFloatingLabelErrorEditAccountData(View root){
        final TextInputLayout firstName = (TextInputLayout) root.findViewById(R.id.acChangeFirstNameLabel);
        final TextInputLayout lastName = (TextInputLayout) root.findViewById(R.id.acChangeLastNameLabel);
        final TextInputLayout companyName = (TextInputLayout) root.findViewById(R.id.acChangeCompanyNameLabel);
        final TextInputLayout username = (TextInputLayout) root.findViewById(R.id.acChangeUsernameLabel);

        firstName.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!Pattern.matches("(([A-ZĘĄŚŁŻŹĆŃÓ])[a-zżźćńółęąś]{2,})", s) && !(s.length() == 0)){
                    firstName.setError("Niepoprawny format");
                    firstName.setErrorEnabled(true);
                } else {
                    firstName.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        lastName.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!Pattern.matches("(([A-ZĘĄŚŁŻŹĆŃÓ])[a-z-A-ZżźćńółęąśĘĄŚŁŻŹĆŃÓ]{2,})", s) && !(s.length() == 0)){
                    lastName.setError("Niepoprawny format");
                    lastName.setErrorEnabled(true);
                } else {
                    lastName.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        companyName.setHelperText("Niewymagane");
        companyName.setHelperTextEnabled(true);

        companyName.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()==0){
                    companyName.setHelperText("Niewymagane");
                    companyName.setHelperTextEnabled(true);
                } else if (s.length()<3){
                    companyName.setHelperTextEnabled(false);
                    companyName.setError("Nazwa firmy musi zawierać co najmniej 3 znaki");
                    companyName.setErrorEnabled(true);
                } else {
                    companyName.setErrorEnabled(false);
                    companyName.setHelperTextEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        username.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!Pattern.matches(".+@.+\\..+", s) && !(s.length() == 0)){
                    username.setError("Podaj poprawny email");
                    username.setErrorEnabled(true);
                } else {
                    username.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void updateData(View root){
        firstName = (TextView) root.findViewById(R.id.acFirstName);
        firstName.setText(user.getFirstName());

        lastName = (TextView) root.findViewById(R.id.acLastName);
        lastName.setText(user.getLastName());

        companyName = (TextView) root.findViewById(R.id.acCompanyName);
        companyName.setText(user.getCompanyName());

        username = (TextView) root.findViewById(R.id.acUsername);
        username.setText(user.getUsername());
    }

    private void updateSideBar(View root){
        NavigationView navigationView = (NavigationView) getActivity().findViewById(R.id.nav_view);

        View headerView = navigationView.getHeaderView(0);
        sideBarName = (TextView) headerView.findViewById(R.id.sideBarName);
        sideBarUsername = (TextView) headerView.findViewById(R.id.sideBarUsername);

        sideBarName.setText(user.getFirstName() + " " + user.getLastName());
        sideBarUsername.setText(user.getUsername());
    }

}