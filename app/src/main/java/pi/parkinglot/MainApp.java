package pi.parkinglot;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import org.json.JSONObject;

public class MainApp extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_app);
        String userdata = null;
        if(savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                userdata = extras.getString("userdata");
            }
        }else{
            userdata = (String) savedInstanceState.getSerializable("userdata");
        }
    }
}