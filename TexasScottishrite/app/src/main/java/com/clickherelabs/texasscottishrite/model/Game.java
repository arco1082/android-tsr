package com.clickherelabs.texasscottishrite.model;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by armando_contreras on 7/24/16.
 */
@IgnoreExtraProperties
public class Game {

    private String name;
    private String image_url;
    private String share_url;
    private String action_url;
    private String type;
    private String intent;

    public Game() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public Game(String name, String image_url, String action_url, String share_url,String type, String intent) {
        this.name = name;
        this.image_url = image_url;
        this.share_url = share_url;
        this.action_url = action_url;
        this.type = type;
        this.intent = intent;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("name", name);
        result.put("image_url", image_url);
        result.put("share_url", share_url);
        result.put("action_url", action_url);
        result.put("type", type);
        result.put("intent", intent);
        return result;
    }

    public String getName() {
        return this.name;
    }

    public String getImageUrl() {
        return this.image_url;
    }

    public String getShareUrl() {
        return this.share_url;
    }

    public String getType() {
        return this.type;
    }

    public String getIntent() {
        return this.intent;
    }

    public String getActionUrl() {
        return this.action_url;
    }
}