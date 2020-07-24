package com.example.chatfunctionalityusingsocket;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MessagesFragmentAdapter extends RecyclerView.Adapter<MessagesFragmentAdapter.MessagesViewHolder> {


    List<ListOfFriendsModel> mListOfFriendsModelList;

    List<ListOfFriendsModel> countList;
    private ItemListener mListener;
    public Context mContext;

    public int message_count;
    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a");
    SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY/MM/dd");


    public MessagesFragmentAdapter(List<ListOfFriendsModel> listOfFriendsModelList, ItemListener listener, Context context) {
        mListOfFriendsModelList = listOfFriendsModelList;
        mListener = listener;
        mContext = context;
    }

    public void updateChatUserList(List<ListOfFriendsModel> new_list) {
        mListOfFriendsModelList.clear();
        mListOfFriendsModelList.addAll(new_list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MessagesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_view_layout, parent, false);
        return new MessagesViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MessagesViewHolder holder, int position) {

        Calendar now = Calendar.getInstance();
        ListOfFriendsModel listOfFriendsModel = mListOfFriendsModelList.get(position);

        holder.userName.setText(listOfFriendsModel.firendname);
        Glide.with(holder.friend_pic).load(listOfFriendsModel.firendpic).into(holder.friend_pic);

        holder.friend_user_id.setText(listOfFriendsModel.frienduserid);

        try {
            if (listOfFriendsModel.getMessage_count() == 0) {
                try{
                    holder.message_count.setText("");


                    holder.last_unread_message.setText(listOfFriendsModel.getLast_unread_message());

                    holder.message_count.setBackgroundColor(holder.itemView.getContext().getResources().getColor(R.color.white));
                    @SuppressLint("SimpleDateFormat") Date date = (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")).parse(listOfFriendsModel.time.replaceAll("Z$", "+0000"));
                    holder.text_time.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.black));
                    assert date != null;
                    if (now.get(Calendar.DATE) == date.getDate()){
                        holder.text_time.setText(simpleDateFormat.format(date));
                    }else if (now.get(Calendar.DATE) - date.getDate() == 1  ){
                        holder.text_time.setText("Yesterday");
                    } else{
                        holder.text_time.setText(dateFormat.format(date));
                    }
                }catch (Exception e)
                {
                    e.printStackTrace();
                }


                // holder.message_count.setVisibility(View.GONE);
            } else {
                try {
                    holder.message_count.setVisibility(View.VISIBLE);
                    holder.message_count.setText(String.valueOf(listOfFriendsModel.getMessage_count()));


                    holder.last_unread_message.setText(listOfFriendsModel.getLast_unread_message());


                    holder.message_count.setBackground(holder.itemView.getContext().getResources().getDrawable(R.drawable.unread_message_drawable_background));
                    @SuppressLint("SimpleDateFormat") Date date = (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")).parse(listOfFriendsModel.time.replaceAll("Z$", "+0000"));
                    if (now.get(Calendar.DATE) == date.getDate()){
                        holder.text_time.setText(simpleDateFormat.format(date));
                    }else if (now.get(Calendar.DATE) - date.getDate() == 1  ){
                        holder.text_time.setText("Yesterday");
                    } else{
                        holder.text_time.setText(dateFormat.format(date));
                    }
                    //holder.text_time.setText(listOfFriendsModel.getTime());
                    holder.text_time.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.black));

                }catch (Exception e)
                {
                    e.printStackTrace();
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }







        if (listOfFriendsModel.isTyping_status())
        {
            holder.last_unread_message_e_card.setVisibility(View.GONE);
            holder.last_unread_message_image.setVisibility(View.GONE);
            holder.last_unread_message_video.setVisibility(View.GONE);
            holder.last_unread_message.setVisibility(View.GONE);
            holder.typing_status_messages.setVisibility(View.VISIBLE);
            holder.typing_status_messages.setText("Typing...");
            holder.typing_status_messages.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.text_color));

        }else if (listOfFriendsModel.getMessageType().equals("e_greeting")){
            holder.last_unread_message.setVisibility(View.GONE);
            holder.typing_status_messages.setVisibility(View.GONE);
            holder.last_unread_message_image.setVisibility(View.GONE);
            holder.last_unread_message_video.setVisibility(View.GONE);
            holder.last_unread_message_e_card.setVisibility(View.VISIBLE);
        }else if (listOfFriendsModel.getMessageType().equals("image")){
            holder.last_unread_message.setVisibility(View.GONE);
            holder.typing_status_messages.setVisibility(View.GONE);
            holder.last_unread_message_image.setVisibility(View.VISIBLE);
            holder.last_unread_message_e_card.setVisibility(View.GONE);
            holder.last_unread_message_video.setVisibility(View.GONE);
        }else if (listOfFriendsModel.getMessageType().equals("video")){
            holder.last_unread_message.setVisibility(View.GONE);
            holder.typing_status_messages.setVisibility(View.GONE);
            holder.last_unread_message_image.setVisibility(View.GONE);
            holder.last_unread_message_e_card.setVisibility(View.GONE);
            holder.last_unread_message_video.setVisibility(View.VISIBLE);
        }
        else {
            holder.last_unread_message.setVisibility(View.VISIBLE);
            holder.typing_status_messages.setVisibility(View.GONE);
            holder.last_unread_message_e_card.setVisibility(View.GONE);
            holder.last_unread_message_video.setVisibility(View.GONE);
            holder.last_unread_message_image.setVisibility(View.GONE);
        }


    }

    @Override
    public int getItemCount() {
        return mListOfFriendsModelList.size();
    }

    public class MessagesViewHolder extends RecyclerView.ViewHolder {

        TextView messageTime, userName, friend_user_id, message_count, last_unread_message,typing_status_messages,text_time;
        ImageView friend_pic;
        LinearLayout linearLayout,last_unread_message_e_card,last_unread_message_image,last_unread_message_video;

        public MessagesViewHolder(@NonNull View itemView) {
            super(itemView);
            messageTime = itemView.findViewById(R.id.messageTime);
            userName = itemView.findViewById(R.id.userName);
            friend_pic = itemView.findViewById(R.id.friend_pic);
            friend_user_id = itemView.findViewById(R.id.friend_user_id);
            message_count = itemView.findViewById(R.id.message_count);
            last_unread_message = itemView.findViewById(R.id.last_unread_message);
            linearLayout = itemView.findViewById(R.id.linearLayout);
            typing_status_messages = itemView.findViewById(R.id.typing_status_messages);
            text_time = itemView.findViewById(R.id.text_time);
            last_unread_message_e_card = itemView.findViewById(R.id.last_unread_message_e_card);
            last_unread_message_image = itemView.findViewById(R.id.last_unread_message_image);
            last_unread_message_video = itemView.findViewById(R.id.last_unread_message_video);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onItemClick(friend_user_id.getText().toString(), userName.getText().toString(), last_unread_message.getText().toString());
                }
            });
        }
    }

    public interface ItemListener {
        void onItemClick(String friend_id, String username, String unread_message);
    }
}

//public class MessagesFragmentAdapter extends PagedListAdapter<ListOfFriendsModel, MessagesFragmentAdapter.MessagesViewHolder> {
//
//
//    List<ListOfFriendsModel> mListOfFriendsModelList;
//
//    ListOfFriendsModel chatList;
//    private ItemListener mListener;
//    public Context mContext;
//    private SharedPreferenceHelper mSharedPreferenceHelper;
//
//    public int message_count;
//    @SuppressLint("SimpleDateFormat")
//    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a");
//    SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY/MM/dd");
//
//    public MessagesFragmentAdapter(Context context, ItemListener listener) {
//        super(ListOfFriendsModel.CALLBACK);
//        mContext = context;
//        mListener = listener;
//    }
//
////    public MessagesFragmentAdapter(List<ListOfFriendsModel> listOfFriendsModelList, ItemListener listener, Context context) {
////        mListOfFriendsModelList = listOfFriendsModelList;
////        mListener = listener;
////        mContext = context;
////    }
//
//    public void updateChatUserList(List<ListOfFriendsModel> new_list) {
//        mListOfFriendsModelList.clear();
//        mListOfFriendsModelList.addAll(new_list);
//        notifyDataSetChanged();
//    }
//
//    @NonNull
//    @Override
//    public MessagesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_view_layout, parent, false);
//        return new MessagesViewHolder(view);
//    }
//
//    @SuppressLint("SetTextI18n")
//    @Override
//    public void onBindViewHolder(@NonNull MessagesViewHolder holder, int position) {
//
//        chatList = getItem(position);
//
//        Calendar now = Calendar.getInstance();
//        //ListOfFriendsModel listOfFriendsModel = mListOfFriendsModelList.get(position);
//
//        holder.userName.setText(chatList.firendname);
//        Glide.with(holder.friend_pic).load(chatList.firendpic).into(holder.friend_pic);
//
//        holder.friend_user_id.setText(chatList.frienduserid);
//
//        try {
//            if (chatList.getMessage_count() == 0) {
//                try{
//                    holder.message_count.setText("");
//                    holder.last_unread_message.setText(chatList.getLast_unread_message());
//                    holder.message_count.setBackgroundColor(holder.itemView.getContext().getResources().getColor(R.color.white));
//                    @SuppressLint("SimpleDateFormat") Date date = (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")).parse(chatList.time.replaceAll("Z$", "+0000"));
//                    holder.text_time.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.black));
//                    assert date != null;
//                    if (now.get(Calendar.DATE) == date.getDate()){
//                        holder.text_time.setText(simpleDateFormat.format(date));
//                    }else if (now.get(Calendar.DATE) - date.getDate() == 1  ){
//                        holder.text_time.setText("Yesterday");
//                    } else{
//                        holder.text_time.setText(dateFormat.format(date));
//                    }
//                }catch (Exception e)
//                {
//                    e.printStackTrace();
//                }
//
//
//               // holder.message_count.setVisibility(View.GONE);
//            } else {
//                try {
//                    holder.message_count.setVisibility(View.VISIBLE);
//                    holder.message_count.setText(String.valueOf(chatList.getMessage_count()));
//                    holder.last_unread_message.setText(chatList.getLast_unread_message());
//                    holder.message_count.setBackground(holder.itemView.getContext().getResources().getDrawable(R.drawable.unread_message_drawable_background));
//                    @SuppressLint("SimpleDateFormat") Date date = (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")).parse(chatList.time.replaceAll("Z$", "+0000"));
//                    if (date != null) {
//                        if (now.get(Calendar.DATE) == date.getDate()){
//                            holder.text_time.setText(simpleDateFormat.format(date));
//                        }else if (now.get(Calendar.DATE) - date.getDate() == 1  ){
//                            holder.text_time.setText("Yesterday");
//                        } else{
//                            holder.text_time.setText(dateFormat.format(date));
//                        }
//                    }
//                    //holder.text_time.setText(chatList.getTime());
//                    holder.text_time.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.black));
//
//                }catch (Exception e)
//                {
//                    e.printStackTrace();
//                }
//
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//
//
//
//
//
//
//        if (chatList.isTyping_status())
//        {
//            holder.last_unread_message.setVisibility(View.GONE);
//            holder.typing_status_messages.setVisibility(View.VISIBLE);
//            holder.typing_status_messages.setText("Typing...");
//            holder.typing_status_messages.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.text_color));
//
//        }else {
//            holder.last_unread_message.setVisibility(View.VISIBLE);
//            holder.typing_status_messages.setVisibility(View.GONE);
//        }
//
//
//    }
//
//    public class MessagesViewHolder extends RecyclerView.ViewHolder {
//
//        TextView messageTime, lastText, userName, friend_user_id, message_count, last_unread_message,typing_status_messages,text_time;
//        ImageView friend_pic;
//        LinearLayout linearLayout;
//
//        public MessagesViewHolder(@NonNull View itemView) {
//            super(itemView);
//            messageTime = itemView.findViewById(R.id.messageTime);
//            lastText = itemView.findViewById(R.id.lastText);
//            userName = itemView.findViewById(R.id.userName);
//            friend_pic = itemView.findViewById(R.id.friend_pic);
//            friend_user_id = itemView.findViewById(R.id.friend_user_id);
//            message_count = itemView.findViewById(R.id.message_count);
//            last_unread_message = itemView.findViewById(R.id.last_unread_message);
//            linearLayout = itemView.findViewById(R.id.linearLayout);
//            typing_status_messages = itemView.findViewById(R.id.typing_status_messages);
//            text_time = itemView.findViewById(R.id.text_time);
//
//
//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    mListener.onItemClick(friend_user_id.getText().toString(), userName.getText().toString(), last_unread_message.getText().toString());
//                }
//            });
//        }
//    }
//
//    public interface ItemListener {
//        void onItemClick(String friend_id, String username, String unread_message);
//    }
//}


