package pi.parkinglot;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainApp extends AppCompatActivity {

    JWToken userToken;
    AppBarConfiguration mAppBarConfiguration;
    User user;

    TextView sideBarName;
    TextView sideBarUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_app);
        this.setTitle("Menu główne");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, "test", Snackbar.LENGTH_LONG)
                        .setAction("Akcjatestowa", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_accountData, R.id.nav_cars, R.id.nav_parkings, R.id.nav_payments)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);


        if(savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                userToken = extras.getParcelable("userToken");
            }
        }

        if(userToken != null){
            try {
                final String url = "http://192.168.0.2:9000/parkinglot-management-system/api/users/" + userToken.getId();
                RequestQueue queue = Volley.newRequestQueue(this);
                JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.GET,
                        url,
                        null,
                        new Response.Listener<JSONObject>(){
                            @Override
                            public void onResponse(JSONObject response) {
                                createUserAndContent(response);
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
        } else {
            Toaster.makeToast(getApplicationContext(), "Logowanie nie powiodło się");
            this.finish();
        }
    }

    @SuppressLint("SetTextI18n")
    private void createUserAndContent(JSONObject response){
        Log.e("Użytkownik", response.toString());
        user = new User(response);

        sideBarName = (TextView) findViewById(R.id.sideBarName);
        sideBarUsername = (TextView) findViewById(R.id.sideBarUsername);

        sideBarName.setText(user.getFirstName() + " " + user.getLastName());
        sideBarUsername.setText(user.getUsername());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp(){
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }

    public boolean logout(MenuItem item){
        userToken = null;
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        this.finish();
        return true;
    }
}