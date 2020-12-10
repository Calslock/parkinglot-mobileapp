package pi.parkinglot.cars;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProviders;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.List;
import java.util.zip.Inflater;

import pi.parkinglot.Car;
import pi.parkinglot.JWToken;
import pi.parkinglot.R;
import pi.parkinglot.User;
import pi.parkinglot.UserDao;
import pi.parkinglot.UserRoomDatabase;

public class carsFragment extends Fragment {

    private CarsViewModel mViewModel;

    UserRoomDatabase userDB;
    UserDao userDao;

    User user;
    JWToken userToken;

    View scrollView;
    LinearLayout linLayout;

    public static carsFragment newInstance() {
        return new carsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mViewModel = ViewModelProviders.of(this).get(CarsViewModel.class);
        View root = inflater.inflate(R.layout.cars_fragment, container, false);
        userDB = UserRoomDatabase.getDatabase(mContext);
        userDao = userDB.userDao();
        scrollView = (ScrollView) root.findViewById(R.id.carMainView);
        linLayout = (LinearLayout) scrollView.findViewById(R.id.carLinearLayout);
        try {
            Bundle bundle = getActivity().getIntent().getExtras();
            userToken = bundle.getParcelable("userToken");

            long id = userToken.getId();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    user = userDao.getUser(id);

                    List<Car> cars = user.getCars();
                    if(cars.size() == 0){
                        TextView textView = new TextView(mContext);
                        textView.setTextSize(40);
                        textView.setText("Brak samochodów");
                        linLayout.addView(textView);
                    } else {
                        for (int i = 0; i < cars.size(); i++) {
                            Car car = cars.get(i);
                            View carView = inflater.inflate(R.layout.car, null);

                            TextView brand, model, licensePlate;
                            brand = (TextView) carView.findViewById(R.id.carBrand);
                            model = (TextView) carView.findViewById(R.id.carModel);
                            licensePlate = (TextView) carView.findViewById(R.id.carLicensePlate);

                            brand.setText(car.getBrand().toString());
                            model.setText(car.getModel().toString());
                            licensePlate.setText(car.getLicenseNumber().toString());

                            linLayout.addView(carView);
                        }
                    }
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