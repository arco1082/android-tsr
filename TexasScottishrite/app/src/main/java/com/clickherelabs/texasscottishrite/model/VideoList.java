package com.clickherelabs.texasscottishrite.model;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by armando_contreras on 7/28/16.
 */
@IgnoreExtraProperties
public class VideoList {

    private String title;
    private String description;
    private String mediaUrl;
    private String theme;

    public VideoList() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public VideoList(String title, String description, String mediaUrl, String theme) {
        this.title = title;
        this.description = description;
        this.mediaUrl = mediaUrl;
        this.theme = theme;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("title", title);
        result.put("description", description);
        result.put("mediaUrl", mediaUrl);
        result.put("theme", theme);
        return result;
    }

    public String getTitle() {
        return this.title;
    }

    public String getDescription() {
        return this.description;
    }

    public String getMediaUrl() {
        return this.mediaUrl;
    }

    public String getTheme() {
        return this.theme;
    }


}