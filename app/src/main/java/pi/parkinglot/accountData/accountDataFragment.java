package pi.parkinglot.accountData;

import androidx.lifecycle.ViewModelProviders;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
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

import com.google.android.material.textfield.TextInputLayout;

import pi.parkinglot.JWToken;
import pi.parkinglot.R;
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
    Button bChangePassword;
    EditText passwordChange, confirmPasswordChange;

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

                    firstName = (TextView) root.findViewById(R.id.acFirstName);
                    firstName.setText(user.getFirstName());

                    lastName = (TextView) root.findViewById(R.id.acLastName);
                    lastName.setText(user.getLastName());

                    companyName = (TextView) root.findViewById(R.id.acCompanyName);
                    companyName.setText(user.getCompanyName());

                    username = (TextView) root.findViewById(R.id.acUsername);
                    username.setText(user.getUsername());

                    passwordChange = (EditText) root.findViewById(R.id.acChangePasswordEditText);
                    confirmPasswordChange = (EditText) root.findViewById(R.id.acChangePasswordConfirmTextEdit);

                    bChangePassword = (Button) root.findViewById(R.id.acChangePasswordButton);
                    bChangePassword.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            builder.setTitle("Zmień hasło");
                            LayoutInflater inflater = getLayoutInflater();
                            View dialogView = inflater.inflate(R.layout.change_password_dialog, null);
                            builder.setView(dialogView);

                            builder.show();
                            setupFloatingLabelError(dialogView);
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

    private void setupFloatingLabelError(View root){
        final TextInputLayout passwordLabel = (TextInputLayout) root.findViewById(R.id.acChangePasswordLabel);
        final TextInputLayout confirmPasswordLabel = (TextInputLayout) root.findViewById(R.id.acChangePasswordConfirmLabel);

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

}