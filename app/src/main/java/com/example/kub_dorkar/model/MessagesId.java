package com.example.kub_dorkar.model;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.Exclude;

public class MessagesId {
    @Exclude
    public String MessageId;

    public  <T extends  MessagesId> T withId(@NonNull final String id){
        this.MessageId = id;
        return (T) this;
    }
}
