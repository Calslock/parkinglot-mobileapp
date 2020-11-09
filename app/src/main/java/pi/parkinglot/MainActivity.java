package pi.parkinglot;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Parkinglot b11");
    }

    public void goToRegister(View view) {
        Intent intent = new Intent(this, Register.class);
        EditText loginbox = (EditText) findViewById(R.id.loginEmail);
        String login = loginbox.getText().toString();
        intent.putExtra("logintoreg", login);
        startActivity(intent);
    }
}