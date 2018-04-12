package com.example.dfrank.med_manager2;

import android.net.Uri;

/**
 * Created by dfrank on 4/1/18.
 */

public class User {
    public String name;
    public String email;
    private Uri photoUrl;

    public User(){}
    public User(String name, String email){
        this.name = name;
        this.email = email;
    }
    public User(String name, String email, Uri photoUrl){
        this.name = name;
        this.email = email;
        this.photoUrl = photoUrl;
    }
}