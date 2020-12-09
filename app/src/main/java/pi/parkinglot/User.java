package pi.parkinglot;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class User implements Parcelable {

    private long id;
    private String firstName;
    private String lastName;
    private String companyName;
    private String username;
    private List<String> roles;
    private List<Car> cars;

    public User(JSONObject data){
        roles = new ArrayList<>();
        cars = new ArrayList<>();
        try {
            this.id = ((Number) data.get("id")).longValue();
            this.firstName = (String) data.get("firstName");
            this.lastName = (String) data.get("lastName");
            this.companyName = (String) data.get("companyName");
            this.username = (String) data.get("username");
            JSONArray rolesArray = (JSONArray) data.get("roles");
            for(int i=0; i<rolesArray.length(); i++){
                JSONObject roleSingleton = (JSONObject) rolesArray.get(i);
                String roleToAdd = (String) roleSingleton.get("authority");
                roles.add(roleToAdd);
            }
            JSONArray carsArray = (JSONArray) data.get("cars");
            for(int i=0; i<carsArray.length(); i++){
                JSONObject carSingleton = (JSONObject) carsArray.get(i);
                JSONObject modelSingleton = (JSONObject) carSingleton.get("model");
                JSONObject brandSingleton = (JSONObject) modelSingleton.get("brand");

                long carId = ((Number) carSingleton.get("id")).longValue();
                String brand = (String) brandSingleton.get("name");
                String model = (String) modelSingleton.get("name");
                String licenseNumber = (String) carSingleton.get("licenseNumber");
                Car car = new Car(carId, brand, model, licenseNumber);
                this.cars.add(car);
            }
        } catch (Exception e){
            Log.e("ExceptionError", e.toString());
        }
    }

    public User(Parcel in) {
        id = in.readLong();
        firstName = in.readString();
        lastName = in.readString();
        companyName = in.readString();
        username = in.readString();
        roles = in.createStringArrayList();
        cars = new ArrayList<>();
        int size = in.readInt();
        for(int i=0; i<size; i++){
            Car car = new Car(in.readString());
            cars.add(car);
        }
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getUsername() {
        return username;
    }

    public List<String> getRoles() {
        return roles;
    }

    public List<Car> getCars() {
        return cars;
    }

    public String toLog() {
        return id+" "+firstName+" "+lastName+" "+companyName+" "+username;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeString(companyName);
        dest.writeString(username);
        dest.writeStringList(roles);
        dest.writeInt(cars.size());
        for(int i=0; i<cars.size(); i++){
            dest.writeString(cars.get(i).toParcelFormat());
        }
    }
}
