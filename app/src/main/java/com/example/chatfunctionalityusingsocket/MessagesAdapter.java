package com.example.chatfunctionalityusingsocket;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessagesViewHolder>{

    public Context mContext;
    public SharedPreferenceHelper mSharedPreferenceHelper;
    private List<MessagesListModel> mMessagesListModelList;

    Activity mActivity;

    boolean isImageFitToScreen;
    private MediaController mMediaController;
    private int pos = 1;



    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE MMM dd yyyy - hh:mm a");

    public MessagesAdapter(Context context, SharedPreferenceHelper sharedPreferenceHelper, List<MessagesListModel> messagesListModelList,Activity mActivity) {
        mContext = context;
        mSharedPreferenceHelper = sharedPreferenceHelper;
        mMessagesListModelList = messagesListModelList;
        mMediaController = new MediaController(mContext);

        this.mActivity=mActivity;
    }

    public void updateMessagesList(List<MessagesListModel> new_list)
    {
        mMessagesListModelList.clear();
        mMessagesListModelList.addAll(new_list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MessagesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.messages_item_layout, parent, false);
        return new MessagesViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MessagesViewHolder holder, int position) {

        MessagesListModel messagesListModel = mMessagesListModelList.get(position);


        if ((messagesListModel.getSenderId().equals(mSharedPreferenceHelper.getUserId()) || messagesListModel.getMessage_sender() == 1) && messagesListModel.getMessage_type().equals("text"))
        {

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.weight = 1.0f;
            params.gravity = Gravity.END;
            params.rightMargin = 20;
            holder.chat_id_layout.setLayoutParams(params);
            holder.chat_id_layout.setBackground(holder.itemView.getResources().getDrawable(R.drawable.chat_background_me));
            holder.mMessageView.setText(messagesListModel.message);
            holder.profile_pic_chat.setVisibility(View.GONE);
            holder.mMessageView.setVisibility(View.VISIBLE);
            holder.mMessageView.setPadding(0,0,10,0);
            holder.e_greeting_image_layout.setVisibility(View.GONE);
            holder.e_greeting_image_linear_layout.setVisibility(View.GONE);
            holder.video_linear_layout.setVisibility(View.GONE);
            //holder.cardview_layout.setVisibility(View.GONE);
            holder.message_layout.setVisibility(View.VISIBLE);
            holder.e_greeting_image.setVisibility(View.GONE);
            Date date = null;
            try {
                date = (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")).parse(messagesListModel.created_at.replaceAll("Z$", "+0000"));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            holder.message_time.setText(simpleDateFormat.format(date));
            holder.message_time.setGravity(Gravity.END);
            holder.mMessageView.setTextColor(holder.itemView.getResources().getColor(R.color.white));
            holder.mUsernameView.setText(messagesListModel.created_at);
            holder.typing_layout.setVisibility(View.GONE);

        }else if ((!messagesListModel.getSenderId().equals(mSharedPreferenceHelper.getUserId()) || messagesListModel.getMessage_sender() == 2) && messagesListModel.getMessage_type().equals("text")){

            try {
                holder.typing_layout.setVisibility(View.GONE);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.weight = 1.0f;
                params.gravity = Gravity.START;
                params.leftMargin = 20;
                holder.chat_id_layout.setLayoutParams(params);
                holder.e_greeting_image_layout.setVisibility(View.GONE);
                holder.e_greeting_image_linear_layout.setVisibility(View.GONE);
                holder.message_layout.setVisibility(View.VISIBLE);
                holder.chat_id_layout.setBackground(holder.itemView.getResources().getDrawable(R.drawable.chat_background_others));
                holder.mMessageView.setText(messagesListModel.message);
                Date date = (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")).parse(messagesListModel.created_at.replaceAll("Z$", "+0000"));
                holder.message_time.setText(simpleDateFormat.format(date));
                holder.mMessageView.setVisibility(View.VISIBLE);
                holder.mMessageView.setPadding(10,0,0,0);
                holder.e_greeting_image.setVisibility(View.GONE);
                //holder.cardview_layout.setVisibility(View.GONE);
                holder.message_time.setGravity(Gravity.START);
                holder.mMessageView.setTextColor(holder.itemView.getResources().getColor(R.color.white));
                holder.mUsernameView.setText(messagesListModel.created_at);
                holder.chat_id_layout.setGravity(Gravity.START);
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }else if ((messagesListModel.getSenderId().equals(mSharedPreferenceHelper.getUserId()) || messagesListModel.getMessage_sender() == 4) && messagesListModel.getMessage_type().equals("e_greeting"))
        {

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.weight = 1.0f;
            params.gravity = Gravity.END;
            holder.chat_id_layout.setLayoutParams(params);
            holder.chat_id_layout.setBackground(holder.itemView.getResources().getDrawable(R.drawable.chat_background_e_greeting));
            holder.mMessageView.setVisibility(View.GONE);
            holder.e_greeting_image.setVisibility(View.VISIBLE);
            holder.e_greeting_image_linear_layout.setVisibility(View.VISIBLE);
            holder.e_greeting_image_layout.setVisibility(View.VISIBLE);
            //holder.cardview_layout.setVisibility(View.VISIBLE);
            holder.message_layout.setVisibility(View.GONE);
            holder.profile_pic_chat.setVisibility(View.GONE);
            holder.video_linear_layout.setVisibility(View.GONE);

            String e_greeting_text = messagesListModel.getMessage();
            holder.e_greeting_text.setText(messagesListModel.getE_greeting_text());
            Glide.with(holder.e_greeting_image).load(e_greeting_text).placeholder(util.getProgressDrawable(mContext)).into(holder.e_greeting_image);

            Date date = null;
            try {
                date = (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")).parse(messagesListModel.created_at.replaceAll("Z$", "+0000"));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            holder.message_time.setText(simpleDateFormat.format(date));
            holder.message_time.setGravity(Gravity.END);
            holder.mMessageView.setTextColor(holder.itemView.getResources().getColor(R.color.white));
            holder.mUsernameView.setText(messagesListModel.created_at);
            holder.typing_layout.setVisibility(View.GONE);

        }else if ((!messagesListModel.getSenderId().equals(mSharedPreferenceHelper.getUserId()) || messagesListModel.getMessage_sender() == 5) && messagesListModel.getMessage_type().equals("e_greeting")){

            holder.typing_layout.setVisibility(View.GONE);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.weight = 1.0f;
            params.gravity = Gravity.START;
            holder.chat_id_layout.setLayoutParams(params);
            holder.chat_id_layout.setBackground(holder.itemView.getResources().getDrawable(R.drawable.chat_background_e_greeting));
            holder.e_greeting_image_linear_layout.setVisibility(View.VISIBLE);
            holder.e_greeting_image_layout.setVisibility(View.VISIBLE);
            //holder.cardview_layout.setVisibility(View.VISIBLE);
            holder.message_layout.setVisibility(View.GONE);
            holder.mMessageView.setVisibility(View.GONE);
            holder.video_linear_layout.setVisibility(View.GONE);

            holder.e_greeting_image.setVisibility(View.VISIBLE);

            String e_greeting_text = messagesListModel.getMessage();
            holder.e_greeting_text.setText(messagesListModel.getE_greeting_text());
            Glide.with(holder.e_greeting_image).load(e_greeting_text).placeholder(util.getProgressDrawable(mContext)).into(holder.e_greeting_image);

            Date date = null;
            try {
                date = (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")).parse(messagesListModel.created_at.replaceAll("Z$", "+0000"));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            holder.message_time.setText(simpleDateFormat.format(date));
            holder.message_time.setGravity(Gravity.START);
            holder.mMessageView.setTextColor(holder.itemView.getResources().getColor(R.color.white));
            holder.mUsernameView.setText(messagesListModel.created_at);
            holder.chat_id_layout.setGravity(Gravity.START);

        }else if ((messagesListModel.getSenderId().equals(mSharedPreferenceHelper.getUserId()) || messagesListModel.getMessage_sender() == 6) && messagesListModel.getMessage_type().equals("image"))
        {

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.weight = 1.0f;
            params.gravity = Gravity.END;
            holder.chat_id_layout.setLayoutParams(params);
            holder.chat_id_layout.setBackground(holder.itemView.getResources().getDrawable(R.drawable.chat_background_e_greeting));
            holder.mMessageView.setVisibility(View.GONE);
            holder.video_linear_layout.setVisibility(View.GONE);
            holder.e_greeting_image.setVisibility(View.VISIBLE);
            holder.e_greeting_image_linear_layout.setVisibility(View.VISIBLE);
            holder.e_greeting_image_layout.setVisibility(View.VISIBLE);
            //holder.cardview_layout.setVisibility(View.VISIBLE);
            holder.message_layout.setVisibility(View.GONE);
            holder.profile_pic_chat.setVisibility(View.GONE);

            String e_greeting_text = messagesListModel.getMessage();
            holder.e_greeting_text.setText("");
            Glide.with(holder.e_greeting_image).load(e_greeting_text).into(holder.e_greeting_image);

            Date date = null;
            try {
                date = (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")).parse(messagesListModel.created_at.replaceAll("Z$", "+0000"));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            holder.message_time.setText(simpleDateFormat.format(date));
            holder.message_time.setGravity(Gravity.END);
            holder.mMessageView.setTextColor(holder.itemView.getResources().getColor(R.color.white));
            holder.mUsernameView.setText(messagesListModel.created_at);
            holder.typing_layout.setVisibility(View.GONE);
        }else if ((!messagesListModel.getSenderId().equals(mSharedPreferenceHelper.getUserId()) || messagesListModel.getMessage_sender() == 7) && messagesListModel.getMessage_type().equals("image")){

            holder.typing_layout.setVisibility(View.GONE);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.weight = 1.0f;
            params.gravity = Gravity.START;
            holder.chat_id_layout.setLayoutParams(params);
            holder.chat_id_layout.setBackground(holder.itemView.getResources().getDrawable(R.drawable.chat_background_e_greeting));
            holder.e_greeting_image_linear_layout.setVisibility(View.VISIBLE);
            holder.e_greeting_image_layout.setVisibility(View.VISIBLE);
            //holder.cardview_layout.setVisibility(View.VISIBLE);
            holder.video_linear_layout.setVisibility(View.GONE);
            holder.message_layout.setVisibility(View.GONE);
            holder.mMessageView.setVisibility(View.GONE);

            holder.e_greeting_image.setVisibility(View.VISIBLE);

            String e_greeting_text = messagesListModel.getMessage();
            holder.e_greeting_text.setText("");
            Glide.with(holder.e_greeting_image).load(e_greeting_text).into(holder.e_greeting_image);
            Date date = null;
            try {
                date = (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")).parse(messagesListModel.created_at.replaceAll("Z$", "+0000"));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            holder.message_time.setText(simpleDateFormat.format(date));
            holder.message_time.setGravity(Gravity.START);
            holder.mMessageView.setTextColor(holder.itemView.getResources().getColor(R.color.white));
            holder.mUsernameView.setText(messagesListModel.created_at);
            holder.chat_id_layout.setGravity(Gravity.START);
        }else if ((messagesListModel.getSenderId().equals(mSharedPreferenceHelper.getUserId()) || messagesListModel.getMessage_sender() == 8) && messagesListModel.getMessage_type().equals("video")){

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.weight = 1.0f;
            params.gravity = Gravity.END;
            holder.chat_id_layout.setLayoutParams(params);
            holder.chat_id_layout.setBackground(holder.itemView.getResources().getDrawable(R.drawable.chat_background_e_greeting));
            holder.mMessageView.setVisibility(View.GONE);
            holder.e_greeting_image.setVisibility(View.GONE);
            holder.e_greeting_image_linear_layout.setVisibility(View.GONE);
            holder.e_greeting_image_layout.setVisibility(View.GONE);
            holder.video_linear_layout.setVisibility(View.VISIBLE);
            //holder.cardview_layout.setVisibility(View.VISIBLE);
            holder.message_layout.setVisibility(View.GONE);
            holder.profile_pic_chat.setVisibility(View.GONE);

            String video_url = messagesListModel.getMessage();

            RequestOptions requestOptions = new RequestOptions();
            Glide.with(mContext)
                    .load(video_url)
                    .apply(requestOptions)
                    .thumbnail(Glide.with(mContext).load(video_url))
                    .placeholder(util.getProgressDrawable(mContext))
                    .into(holder.video_view);

//            Uri video = Uri.parse(video_url);
//            holder.video_view.setVideoURI(video);
//            holder.video_view.setMediaController(mMediaController);



            Date date = null;
            try {
                date = (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")).parse(messagesListModel.created_at.replaceAll("Z$", "+0000"));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            holder.message_time.setText(simpleDateFormat.format(date));
            holder.message_time.setGravity(Gravity.END);
            holder.mMessageView.setTextColor(holder.itemView.getResources().getColor(R.color.white));
            holder.mUsernameView.setText(messagesListModel.created_at);
            holder.typing_layout.setVisibility(View.GONE);
        }else {

            holder.typing_layout.setVisibility(View.GONE);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.weight = 1.0f;
            params.gravity = Gravity.START;
            holder.chat_id_layout.setLayoutParams(params);
            holder.chat_id_layout.setBackground(holder.itemView.getResources().getDrawable(R.drawable.chat_background_e_greeting));
            holder.e_greeting_image_linear_layout.setVisibility(View.GONE);
            holder.e_greeting_image_layout.setVisibility(View.GONE);
            //holder.cardview_layout.setVisibility(View.VISIBLE);
            holder.video_linear_layout.setVisibility(View.VISIBLE);
            holder.message_layout.setVisibility(View.GONE);
            holder.mMessageView.setVisibility(View.GONE);

            holder.e_greeting_image.setVisibility(View.VISIBLE);

            String video_url = messagesListModel.getMessage();

            RequestOptions requestOptions = new RequestOptions();
            Glide.with(mContext)
                    .load(video_url)
                    .apply(requestOptions)
                    .thumbnail(Glide.with(mContext).load(video_url))
                    .placeholder(util.getProgressDrawable(mContext))
                    .into(holder.video_view);

            Date date = null;
            try {
                date = (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")).parse(messagesListModel.created_at.replaceAll("Z$", "+0000"));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            holder.message_time.setText(simpleDateFormat.format(date));
            holder.message_time.setGravity(Gravity.START);
            holder.mMessageView.setTextColor(holder.itemView.getResources().getColor(R.color.white));
            holder.mUsernameView.setText(messagesListModel.created_at);
            holder.chat_id_layout.setGravity(Gravity.START);

        }


        holder.e_greeting_image_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext,FullScreenImageActivity.class);
                intent.putExtra("e_greeting_image",messagesListModel.getMessage());
                intent.putExtra("e_greeting_text",messagesListModel.getE_greeting_text());
                mContext.startActivity(intent);
                mActivity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

            }
        });

        holder.video_linear_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(mContext,VideoViewActivity.class);
                intent.putExtra("video_path",messagesListModel.getMessage());
                mContext.startActivity(intent);
                mActivity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

    }


    @Override
    public int getItemCount() {
        return mMessagesListModelList.size();
        //return mMessages.size();
    }

    public class MessagesViewHolder extends RecyclerView.ViewHolder
    {
        private TextView mUsernameView,typing_text;
        private TextView mMessageView,username_me,message_me,message_time;
        CircleImageView profile_pic_chat;
        ImageView e_greeting_image;
        TextView e_greeting_text;
        CardView cardView_layout;
        LinearLayout chat_id_layout,typing_layout,chat_ui,message_layout,e_greeting_image_linear_layout;
        RelativeLayout e_greeting_image_layout,video_linear_layout;
        ImageView video_view;

        public MessagesViewHolder(@NonNull View itemView) {
            super(itemView);
            mUsernameView = (TextView) itemView.findViewById(R.id.username);
            mMessageView = (TextView) itemView.findViewById(R.id.message);
            chat_id_layout =  itemView.findViewById(R.id.chat_id_layout);
            profile_pic_chat = itemView.findViewById(R.id.profile_pic_chat);
            message_time = itemView.findViewById(R.id.message_time);
            typing_layout = itemView.findViewById(R.id.typing_layout);
            typing_text = itemView.findViewById(R.id.typing_text);
            chat_ui = itemView.findViewById(R.id.chat_ui);
            e_greeting_image = itemView.findViewById(R.id.e_greeting);
            e_greeting_text = itemView.findViewById(R.id.e_greeting_text);
            e_greeting_image_layout = itemView.findViewById(R.id.e_greeting_image_layout);
            message_layout = itemView.findViewById(R.id.message_layout);
            e_greeting_image_linear_layout = itemView.findViewById(R.id.e_greeting_image_linear_layout);
            video_linear_layout = itemView.findViewById(R.id.video_linear_layout);
            video_view = itemView.findViewById(R.id.video_view);
            //cardView_layout = itemView.findViewById(R.id.cardview_layout);

        }

        public void setMessage(String message) {
            if (null == mMessageView) return;
            mMessageView.setText(message);
        }

        public void setUsername(String username) {
            if (null == mUsernameView) return;
            mUsernameView.setText(username);
        }
    }
}
