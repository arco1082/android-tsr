package com.clickherelabs.texasscottishrite.model;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by armando_contreras on 7/24/16.
 */
@IgnoreExtraProperties
public class Brand {

    private String name;
    private String code;
    public Brand() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public Brand(String name, String code) {
        this.name = name;
        this.code = code;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("name", name);
        result.put("code", code);
        return result;
    }

    public String getName() {
        return this.name;
    }

    public String getCode() {
        return this.code;
    }

}
