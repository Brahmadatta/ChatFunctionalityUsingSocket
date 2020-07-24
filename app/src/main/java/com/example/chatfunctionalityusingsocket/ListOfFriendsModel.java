package com.example.chatfunctionalityusingsocket;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.google.gson.annotations.SerializedName;

public class ListOfFriendsModel implements Parcelable {

    @SerializedName("firendname")
    public String firendname;

    @SerializedName("firendpic")
    public String firendpic;

    @SerializedName("frienduserid")
    public String frienduserid;

    @SerializedName("live")
    public String live;

    @SerializedName("unreadMessagesCount")
    public int message_count;

    @SerializedName("lastMessage")
    public String last_unread_message;

    @SerializedName("messageType")
    public String messageType;

    public boolean typing_status;

    @SerializedName("lastMessageSend_Date")
    public String time;

    public int getMessage_count() {
        return message_count;
    }

    public void setMessage_count(int message_count) {
        this.message_count = message_count;
    }

    public boolean isTyping_status() {
        return typing_status;
    }

    public void setTyping_status(boolean typing_status) {
        this.typing_status = typing_status;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLast_unread_message() {
        return last_unread_message;
    }

    public void setLast_unread_message(String last_unread_message) {
        this.last_unread_message = last_unread_message;
    }

    public String getFirendname() {
        return firendname;
    }

    public void setFirendname(String firendname) {
        this.firendname = firendname;
    }

    public String getFirendpic() {
        return firendpic;
    }

    public void setFirendpic(String firendpic) {
        this.firendpic = firendpic;
    }

    public String getFrienduserid() {
        return frienduserid;
    }

    public void setFrienduserid(String frienduserid) {
        this.frienduserid = frienduserid;
    }

    public String getLive() {
        return live;
    }

    public void setLive(String live) {
        this.live = live;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public ListOfFriendsModel(String firendname, String firendpic, String frienduserid, String live, int message_count, String last_unread_message, boolean isTyping, String texting_time, String messageType) {
        this.firendname = firendname;
        this.firendpic = firendpic;
        this.frienduserid = frienduserid;
        this.live = live;
        this.message_count = message_count;
        this.last_unread_message = last_unread_message;
        this.typing_status = isTyping;
        this.time = texting_time;
        this.messageType = messageType;
    }

    public ListOfFriendsModel(Parcel in) {
        firendname = in.readString();
        firendpic = in.readString();
        frienduserid = in.readString();
        live = in.readString();
        message_count = in.readInt();
        last_unread_message = in.readString();
        typing_status = in.readBoolean();
        time = in.readString();
        messageType = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(firendname);
        parcel.writeString(firendpic);
        parcel.writeString(frienduserid);
        parcel.writeString(live);
        parcel.writeInt(message_count);
        parcel.writeString(last_unread_message);
        parcel.writeBoolean(typing_status);
        parcel.writeString(time);
        parcel.writeString(messageType);
    }

    public static DiffUtil.ItemCallback<ListOfFriendsModel> CALLBACK = new DiffUtil.ItemCallback<ListOfFriendsModel>() {
        @Override
        public boolean areItemsTheSame(@NonNull ListOfFriendsModel oldItem, @NonNull ListOfFriendsModel newItem) {
            return oldItem.frienduserid == newItem.frienduserid;
        }

        @Override
        public boolean areContentsTheSame(@NonNull ListOfFriendsModel oldItem, @NonNull ListOfFriendsModel newItem) {
            return true;
        }
    };

    public static final Creator<ListOfFriendsModel> CREATOR = new Creator<ListOfFriendsModel>() {
        @Override
        public ListOfFriendsModel createFromParcel(Parcel in) {
            return new ListOfFriendsModel(in);
        }

        @Override
        public ListOfFriendsModel[] newArray(int size) {
            return new ListOfFriendsModel[size];
        }
    };
}
