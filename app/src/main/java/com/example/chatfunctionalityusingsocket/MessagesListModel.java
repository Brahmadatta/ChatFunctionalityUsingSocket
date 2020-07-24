package com.example.chatfunctionalityusingsocket;

import com.google.gson.annotations.SerializedName;

public class MessagesListModel {

    @SerializedName("message")
    public String message;

    @SerializedName("senderId")
    public String senderId;

    @SerializedName("created_at")
    public String created_at;

    @SerializedName("message_type")
    public String message_type;

    @SerializedName("e_greeting_text")
    public String e_greeting_text;

    private int message_sender;


    public MessagesListModel(String message, String senderId, String created_at, int message_sender,String message_type,String e_greeting_text) {
        this.message = message;
        this.senderId = senderId;
        this.created_at = created_at;
        this.message_sender = message_sender;
        this.message_type = message_type;
        this.e_greeting_text = e_greeting_text;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public int getMessage_sender() {
        return message_sender;
    }

    public String getMessage_type() {
        return message_type;
    }


    public String getE_greeting_text() {
        return e_greeting_text;
    }



    public static class Builder {
        private String mMessage;
        private String sender;
        private String time;
        private int type;

        public Builder message(String message) {
            mMessage = message;
            return this;
        }
    }
}
