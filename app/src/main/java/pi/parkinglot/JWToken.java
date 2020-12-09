package pi.parkinglot;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JWToken implements Parcelable {

    public static final Creator<JWToken> CREATOR = new Creator<JWToken>() {
        @Override
        public JWToken createFromParcel(Parcel in) {
            return new JWToken(in);
        }

        @Override
        public JWToken[] newArray(int size) {
            return new JWToken[size];
        }
    };

    private long id;
    private List<String> roles;
    private String accessToken;
    private String tokenType;
    private String username;

    public JWToken(JSONObject data){
        roles = new ArrayList<>();
        try {
            this.id = ((Number) data.get("id")).longValue();
            JSONArray rolesArray = (JSONArray) data.get("roles");
            for(int i=0; i<rolesArray.length(); i++){
                this.roles.add((String) rolesArray.get(i));
            }
            this.accessToken = (String) data.get("accessToken");
            this.tokenType = (String) data.get("tokenType");
            this.username = (String) data.get("username");
        } catch (Exception e){
            Log.e("ExceptionError", e.toString());
        }
    }

    public JWToken(Parcel in) {
        id = in.readLong();
        roles = in.createStringArrayList();
        accessToken = in.readString();
        tokenType = in.readString();
        username = in.readString();
    }


    public long getId() {
        return id;
    }

    public List<String> getRoles() {
        return roles;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public String getUsername() {
        return username;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeStringList(roles);
        dest.writeString(accessToken);
        dest.writeString(tokenType);
        dest.writeString(username);
    }
}
