package com.addu.chatobal;

import androidx.annotation.NonNull;

public class Chats {
   public String username,message;
    public Chats(String username,String message){
        this.username=username;
        this.message=message;
    }
    public Chats(){}

    @NonNull
    @Override
    public String toString() {
        return this.username+":"+this.message;
    }
}
