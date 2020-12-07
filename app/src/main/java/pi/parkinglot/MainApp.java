package pi.parkinglot;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONObject;

public class MainApp extends AppCompatActivity {

    TextView tests;
    JWToken userToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_app);
        this.setTitle("Menu główne");

        tests = (TextView)findViewById(R.id.testView);

        if(savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                userToken = extras.getParcelable("userToken");
            }
        }

        if(userToken != null){
            try {
                //TODO user handling
            } catch (Exception e) {
                Log.e("ExceptionError", e.toString());
            }
        } else {
            Toaster.makeToast(getApplicationContext(), "Logowanie nie powiodło się");
            this.finish();
        }
    }
}