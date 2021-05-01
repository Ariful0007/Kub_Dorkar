package com.example.kub_dorkar.model;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.Exclude;

public class RequestItemUserId {

    @Exclude
    public String requestItemUserId;

    public  <T extends  RequestItemUserId> T withId(@NonNull final String id){
        this.requestItemUserId = id;
        return (T) this;
    }

}