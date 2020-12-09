package pi.parkinglot.accountData;

import androidx.lifecycle.ViewModelProviders;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

}