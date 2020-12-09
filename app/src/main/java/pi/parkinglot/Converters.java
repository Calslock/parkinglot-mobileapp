package pi.parkinglot;

import android.util.Log;

import androidx.room.TypeConverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Converters {
    @TypeConverter
    public static List<String> fromString(String encodedList){
        String[] arr = encodedList.split("%");
        return new ArrayList<>(Arrays.asList(arr));
    }

    @TypeConverter
    public static String listToString(List<String> list){
        StringBuilder encodedStringBuilder = new StringBuilder();
        for(int i = 0; i<list.size(); i++){
            encodedStringBuilder.append(list.get(i));
            encodedStringBuilder.append("%");
        }
        String encodedString = encodedStringBuilder.toString();
        encodedString = encodedString.substring(0, encodedString.length() - 1);
        return encodedString;
    }

    @TypeConverter
    public static List<Car> carFromString(String encodedString){
        List<Car> cars= new ArrayList<>();
        String[] arr = encodedString.split("%");
        for (String s : arr) {
            cars.add(Car.toCar(s));
        }
        return cars;
    }

    @TypeConverter
    public static String listToCars(List<Car> list){
        StringBuilder encodedStringBuilder = new StringBuilder();
        for(int i=0; i<list.size(); i++){
            encodedStringBuilder.append(Car.toStaticParcelFormat(list.get(i)));
            encodedStringBuilder.append("%");
        }
        String encodedString = encodedStringBuilder.toString();
        encodedString = encodedString.substring(0, encodedString.length() - 1);
        return encodedString.toString();
    }
}
