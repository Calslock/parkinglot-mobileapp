package pi.parkinglot;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "user_table")
@TypeConverters({Converters.class})
public class User implements Parcelable {

    @PrimaryKey
    private long id;

    @ColumnInfo(name = "firstName")
    private String firstName;

    @ColumnInfo(name = "lastName")
    private String lastName;

    @ColumnInfo(name = "companyName")
    private String companyName;

    @ColumnInfo(name = "username")
    private String username;

    @TypeConverters({Converters.class})
    @ColumnInfo(name = "roles")
    private List<String> roles;

    @TypeConverters({Converters.class})
    @ColumnInfo(name = "cars")
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
                long brandId = ((Number) brandSingleton.get("id")).longValue();
                String model = (String) modelSingleton.get("name");
                long modelId = ((Number) modelSingleton.get("id")).longValue();
                String licenseNumber = (String) carSingleton.get("licenseNumber");
                Car car = new Car(carId, brand, brandId, model, modelId, licenseNumber);
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

    public User(long id, String firstName, String lastName, String companyName, String username, List<String> roles, List<Car> cars) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.companyName = companyName;
        this.username = username;
        this.roles = roles;
        this.cars = cars;
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

    public void setId(long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public List<Car> getCars() {
        return cars;
    }

    public void setCars(List<Car> cars) {
        this.cars = cars;
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

    public JSONObject toJSON(){
        JSONObject jobj = new JSONObject();

        try{
            jobj.put("id", id);
            jobj.put("firstName", firstName);
            jobj.put("lastName", lastName);
            jobj.put("companyName", companyName);
            jobj.put("username", username);
            jobj.put("email", username);

            JSONArray rolesArray = new JSONArray();

            for(int i=0; i<roles.size(); i++){
                JSONObject roleObject = new JSONObject();
                long rid;
                String name, authority;
                switch (roles.get(i)){
                    case "ADMIN":   rid = 1;
                                    name = "ADMIN";
                                    authority = "ADMIN";
                                    break;
                    case "OWNER":   rid = 2;
                                    name = "OWNER";
                                    authority = "OWNER";
                                    break;
                    case "EMPLOYEE": rid = 3;
                                    name = "EMPLOYEE";
                                    authority = "EMPLOYEE";
                                    break;
                    case "USER":
                    default:    rid = 4;
                                name = "USER";
                                authority = "USER";
                }
                roleObject.put("id", rid);
                roleObject.put("name", name);
                roleObject.put("authority", authority);

                rolesArray.put(roleObject);
            }

            JSONArray carArray = new JSONArray();

            for(int i=0; i<cars.size(); i++){
                Car car = cars.get(i);
                JSONObject carObject = new JSONObject();
                JSONObject modelObject = new JSONObject();
                JSONObject brandObject = new JSONObject();

                brandObject.put("id", car.getBrandid());
                brandObject.put("name", car.getBrand());

                modelObject.put("id", car.getModelid());
                modelObject.put("name", car.getModel());
                modelObject.put("brand", brandObject);

                carObject.put("id", car.getId());
                carObject.put("licenseNumber", car.getLicenseNumber());
                carObject.put("model", modelObject);

                carArray.put(carObject);
            }
        }catch(Exception e) {
            Log.e("ExceptionError", e.toString());
        }
        return jobj;
    }
}
