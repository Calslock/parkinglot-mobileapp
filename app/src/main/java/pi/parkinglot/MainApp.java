package pi.parkinglot;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.JsonWriter;
import android.util.Log;
import android.widget.TextView;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class MainApp extends AppCompatActivity {

    JSONParser parser;
    JSONObject data;
    TextView tests;
    JWToken user;

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
                user = new JWToken(data);
                Log.e("z bundla", userdata);
                String zjson = user.returnData();
                Log.e("z jasiona", zjson);
            } catch (Exception e) {
                Log.e("ExceptionError", e.toString());
            }
        } else {
            Toaster.makeToast(getApplicationContext(), "Logowanie nie powiodło się");
            this.finish();
        }
    }
}