package pi.parkinglot;

import android.content.Context;
import android.widget.Toast;

public class Toaster {

    public static void makeToast(Context context, CharSequence text){
        Toast.makeText(context, text, Toast.LENGTH_LONG).show();
    }
}
