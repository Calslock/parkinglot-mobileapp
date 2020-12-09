package pi.parkinglot.accountData;

import androidx.lifecycle.ViewModelProviders;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import pi.parkinglot.R;
import pi.parkinglot.User;

public class accountDataFragment extends Fragment {

    private AccountDataViewModel mViewModel;
    User user;

    TextView firstName, lastName, companyName, username;

    public static accountDataFragment newInstance() {
        return new accountDataFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mViewModel = ViewModelProviders.of(this).get(AccountDataViewModel.class);
        View root = inflater.inflate(R.layout.account_data_fragment, container, false);
        try {
            Bundle bundle = getActivity().getIntent().getExtras();
            user = bundle.getParcelable("user");

            firstName = (TextView) root.findViewById(R.id.acFirstName);
            firstName.setText(user.getFirstName());

            lastName = (TextView) root.findViewById(R.id.acLastName);
            lastName.setText(user.getLastName());

            companyName = (TextView) root.findViewById(R.id.acCompanyName);
            companyName.setText(user.getCompanyName());

            username = (TextView) root.findViewById(R.id.acUsername);
            username.setText(user.getUsername());

        } catch(Exception e){
            Log.e("ExceptionError", e.toString());
        }
        return root;
    }

}