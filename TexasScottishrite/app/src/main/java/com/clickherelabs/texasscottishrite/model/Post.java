package com.clickherelabs.texasscottishrite.model;

/**
 * Created by armando_contreras on 7/28/16.
 */

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class Post {

    private String title;
    private String image_url;
    private String action_url;
    private String type;
    private String intent;
    private String theme;
    private Object disabledOnDate;
    private Object enabledOnDate;
    private String team;

    public Post() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public Post(String title, String image_url, String action_url, String type, String intent, String theme, Object disabledOnDate, Object enabledOnDate, String team) {
        this.title = title;
        this.image_url = image_url;
        this.action_url = action_url;
        this.type = type;
        this.intent = intent;
        this.theme = theme;
        this.disabledOnDate = disabledOnDate;
        this.enabledOnDate = enabledOnDate;
        this.team = team;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("title", title);
        result.put("image_url", image_url);
        result.put("action_url", action_url);
        result.put("type", type);
        result.put("intent", intent);
        result.put("theme", theme);
        result.put("disabledOnDate", disabledOnDate);
        result.put("enabledOnDate", enabledOnDate);
        result.put("team", team);
        return result;
    }

    public String getTitle() {
        return this.title;
    }

    public String getImageUrl() {
        return this.image_url;
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

    public String getTheme() {
        return this.theme;
    }

    public Object getEnabledOndate() {
        return this.enabledOnDate;
    }

    public Object getDisabledOnDate() {
        return this.disabledOnDate;
    }

    public String getTeam() {
        return this.team;
    }
}