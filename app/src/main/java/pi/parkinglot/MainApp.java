package pi.parkinglot;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class MainApp extends AppCompatActivity {

    JSONParser parser;
    JSONObject data;
    TextView tests;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_app);
        this.setTitle("Menu główne");
        tests = (TextView)findViewById(R.id.testView);
        parser = new JSONParser();
        String userdata = null;
        if(savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                userdata = extras.getString("userdata");
            }
        }else{
            userdata = (String) savedInstanceState.getSerializable("userdata");
        }
        if(userdata != null){
            try {
                data = (JSONObject) parser.parse(userdata);
                tests.setText(userdata);
            } catch (Exception e) {
                Log.e("ExceptionError", e.toString());
            }
        }
    }
}