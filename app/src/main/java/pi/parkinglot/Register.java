package pi.parkinglot;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

public class Register extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setTitle("Rejestracja");

        String login = "";
        if(savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                login = extras.getString("logintoreg");
            }
        }else{
            login = (String) savedInstanceState.getSerializable("logintoreg");
        }
        EditText logreg = (EditText) findViewById(R.id.emailRegister);
        logreg.setText(login);
    }

    protected void register(){

    }
}