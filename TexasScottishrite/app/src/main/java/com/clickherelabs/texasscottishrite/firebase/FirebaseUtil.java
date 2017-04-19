package com.clickherelabs.texasscottishrite.firebase;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by armando_contreras on 7/24/16.
 */
public class FirebaseUtil {
    public static DatabaseReference getBaseRef() {
        return FirebaseDatabase.getInstance().getReference();
    }

    public static String getProjectPath() {
        return "brands/";
    }

    public static String getGamesPath() {
        return "games/";
    }

    public static String getVideoListsPath() {
        return "videolists/";
    }

    public static DatabaseReference getBrandByCode(String code) {
        return getBrandsRef().child(code);
    }

    public static DatabaseReference getGamesByBrand(String code) {
        return getGamesRef().child(code);
    }

    public static DatabaseReference getPostByBrand(String code) {
        return getPostsRef().child(code);
    }

    public static DatabaseReference getVideoListsByBrand(String code) {
        return getVideoListsRef().child(code);
    }

    public static DatabaseReference getBrandsRef() {
        return getBaseRef().child("brands");
    }

    public static DatabaseReference getGamesRef() {
        return getBaseRef().child("games");
    }

    public static DatabaseReference getPostsRef() {
        return getBaseRef().child("posts");
    }

    public static DatabaseReference getVideoListsRef() {
        return getBaseRef().child("videolists");
    }

}