package pi.parkinglot;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.simple.JSONObject;

import java.util.Arrays;
import java.util.List;

public class JWToken {
    private long id;
    private List<String> roles;
    private String accessToken;
    private String tokenType;
    private String username;

    JWToken(JSONObject data){
        this.id = (long) data.get("id");
        /*JSONArray rolesArray = (JSONArray) data.get("roles");
        for(int i=0; i<rolesArray.length(); i++){
            try {
                roles.add(rolesArray.getString(i));
            } catch (Exception e) {
                Log.e("ExceptionError", e.toString());
            }
        }*/
        this.accessToken = (String) data.get("accessToken");
        this.tokenType = (String) data.get("tokenType");
        this.username = (String) data.get("username");
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

    public String returnData(){
        return this.roles.toString() + " " + this.id + " " + this.accessToken + " " + this.tokenType + " " + this.username;
    }
}
