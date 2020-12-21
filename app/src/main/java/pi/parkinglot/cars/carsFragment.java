package pi.parkinglot.cars;

import androidx.lifecycle.ViewModelProviders;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pi.parkinglot.Car;
import pi.parkinglot.JWToken;
import pi.parkinglot.R;
import pi.parkinglot.Toaster;
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

    AlertDialog.Builder builder;
    Spinner modelSpinner, brandSpinner;

    EditText licenseNumberEditText;

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
        Bundle bundle = getActivity().getIntent().getExtras();
        userToken = bundle.getParcelable("userToken");
        try {
            long id = userToken.getId();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    user = userDao.getUser(id);
                    refreshCars(inflater);
                }
            }).start();
        } catch(Exception e){
            Log.e("ExceptionError", e.toString());
        }
        builder = new AlertDialog.Builder(mContext);
        FloatingActionButton fab = root.findViewById(R.id.carFab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Integer> brands = new HashMap<>();
                Map<String, Integer> models = new HashMap<>();
                String[] car = new String[2];
                builder.setTitle("Dodaj samochód");
                LayoutInflater infalter = getLayoutInflater();
                View dialogView = infalter.inflate(R.layout.add_car_dialog, null);
                builder.setView(dialogView);

                brandSpinner = (Spinner) dialogView.findViewById(R.id.carBrandSpinner);
                modelSpinner = (Spinner) dialogView.findViewById(R.id.carModelSpinner);
                licenseNumberEditText = (EditText) dialogView.findViewById(R.id.carLicenseNumberEditText);

                brandSpinner.setEnabled(false);
                modelSpinner.setEnabled(false);

                try {
                    final String url = "http://192.168.0.2:9000/parkinglot-management-system/api/cars/brands";
                    RequestQueue queue = Volley.newRequestQueue(mContext);
                    JsonArrayRequest jsonReq = new JsonArrayRequest(Request.Method.GET,
                            url,
                            null,
                            new Response.Listener<JSONArray>() {
                                @Override
                                public void onResponse(JSONArray response) {
                                    try{
                                        for(int i=0; i<response.length(); i++) {
                                            JSONObject brand = (JSONObject) response.get(i);
                                            brands.put((String) brand.get("name"), (Integer) brand.get("id"));
                                        }
                                            List<String> brandList = new ArrayList<>(brands.keySet());
                                            String[] brandArray = new String[brandList.size()];
                                            brandArray = brandList.toArray(brandArray);
                                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext,
                                                    R.layout.support_simple_spinner_dropdown_item,
                                                    brandArray);
                                            brandSpinner.setAdapter(adapter);
                                            brandSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                @Override
                                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                                    car[0] = (String) brandList.get(position);
                                                    final String url = "http://192.168.0.2:9000/parkinglot-management-system/api/cars/brands/" + brandList.get(position) + "/models";
                                                    RequestQueue queue = Volley.newRequestQueue(mContext);
                                                    JsonArrayRequest jsonReq = new JsonArrayRequest(Request.Method.GET,
                                                            url,
                                                            null,
                                                            new Response.Listener<JSONArray>() {
                                                                @Override
                                                                public void onResponse(JSONArray response) {
                                                                    try {
                                                                        models.clear();
                                                                        for (int i = 0; i < response.length(); i++) {
                                                                            JSONObject model = (JSONObject) response.get(i);
                                                                            models.put((String) model.get("name"), (Integer) model.get("id"));
                                                                        }
                                                                        List<String> modelList = new ArrayList<>(models.keySet());
                                                                        String[] modelArray = new String[modelList.size()];
                                                                        modelArray = modelList.toArray(modelArray);
                                                                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext,
                                                                                R.layout.support_simple_spinner_dropdown_item,
                                                                                modelArray);
                                                                        modelSpinner.setAdapter(adapter);
                                                                        modelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                                            @Override
                                                                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                                                                car[1] = (String) modelList.get(position);
                                                                            }
                                                                            @Override
                                                                            public void onNothingSelected(AdapterView<?> parent) {

                                                                            }
                                                                        });
                                                                        modelSpinner.setEnabled(true);
                                                                    } catch (Exception e){
                                                                        Log.e("ExceptionError", e.toString());
                                                                    }
                                                                }
                                                            },
                                                            new Response.ErrorListener() {
                                                                @Override
                                                                public void onErrorResponse(VolleyError error) {
                                                                    Log.e("Volley", error.toString());
                                                                    Toaster.makeToast(mContext, "Nie można było pobrać danych o modelach");
                                                                }
                                                            }) {
                                                        @Override
                                                        public Map<String, String> getHeaders() {
                                                            Map<String, String> params = new HashMap<>();
                                                            params.put("Authorization", userToken.getTokenType() + " " + userToken.getAccessToken());
                                                            return params;
                                                        }
                                                    };
                                                    queue.add(jsonReq);
                                                }
                                                @Override
                                                public void onNothingSelected(AdapterView<?> parent) {

                                                }
                                            });
                                            brandSpinner.setEnabled(true);
                                        } catch (Exception e) {
                                            Log.e("ExceptionError", e.toString());
                                        }
                                    }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.e("Volley", error.toString());
                                    Toaster.makeToast(mContext, "Nie można było pobrać danych o samochodach");
                                }
                            }){
                        @Override
                        public Map<String, String> getHeaders() {
                            Map<String, String> params = new HashMap<>();
                            params.put("Authorization", userToken.getTokenType() + " " + userToken.getAccessToken());
                            return params;
                        }
                    };
                    queue.add(jsonReq);
                } catch (Exception e){
                    Log.e("ExceptionError", e.toString());
                }

                builder.setPositiveButton("Dodaj samochód", (dialog, which) -> {
                    String licenseNumber = String.valueOf(licenseNumberEditText.getText());
                    if(car[0] == null || car[1] == null){
                        Toaster.makeToast(mContext, "Nie wybrano marki lub modelu samochodu");
                    }
                    else if(licenseNumber.length() != 7){
                        Toaster.makeToast(mContext, "Podano nieprawidłowy numer rejestracyjny");
                    }
                    else{
                        try {
                            String url = "http://192.168.0.2:9000/parkinglot-management-system/api/cars";
                            JSONObject request = new JSONObject();
                            request.put("licenseNumber", licenseNumber);
                            JSONObject brand = new JSONObject();
                            JSONObject model = new JSONObject();
                            brand.put("name", car[0]);
                            model.put("brand", brand);
                            model.put("name", car[1]);
                            request.put("model", model);
                            request.put("username", user.getUsername());
                            Log.e("requset", request.toString());

                            RequestQueue queue = Volley.newRequestQueue(mContext);
                            JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.POST,
                                    url,
                                    request,
                                    new Response.Listener<JSONObject>() {
                                        @Override
                                        public void onResponse(JSONObject response) {
                                            Toaster.makeToast(mContext, "Dodano samochód do bazy");
                                            refreshCars(inflater);
                                            dialog.cancel();
                                        }
                                    },
                                    new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            Log.e("Volley", error.toString());
                                            Toaster.makeToast(mContext, "Nie można było dodać samochodu do bazy");
                                        }
                                    }){
                                @Override
                                public Map<String, String> getHeaders() {
                                    Map<String, String> params = new HashMap<>();
                                    params.put("Authorization", userToken.getTokenType() + " " + userToken.getAccessToken());
                                    return params;
                                }
                            };
                            queue.add(jsonReq);
                        } catch (Exception e){
                            Log.e("ExceptionError", e.toString());
                        }
                    }
                });

                builder.setNegativeButton("Anuluj", (dialog, which) -> {
                    dialog.cancel();
                });

                builder.show();
                setupFloatingLabelErrorAddCar(dialogView);
            }
        });
        return root;
    }

    private void refreshCars(LayoutInflater inflater){
        List<Car> cars = user.getCars();
        linLayout.removeAllViews();
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

    private void setupFloatingLabelErrorAddCar(View root){

    }

}