package com.example.chatfunctionalityusingsocket;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MessageDetailActivity extends AppCompatActivity implements View.OnClickListener{

    @BindView(R.id.back_button_message_detail)
    ImageView back_button_message_detail;

    @BindView(R.id.userNameMessageDetail)
    TextView userNameMessageDetail;

    @BindView(R.id.messages_recyclerView)
    RecyclerView messages_recyclerView;

    @BindView(R.id.send_layout)
    LinearLayout send_layout;

    @BindView(R.id.input_message)
    EditText input_message;

    @BindView(R.id.typing_status)
    TextView typing_status;

    @BindView(R.id.send_image_or_video)
    ImageView send_image_or_video;

    @BindView(R.id.send_video)
    ImageView send_video;

    @BindView(R.id.e_greeting_gift_card_layout)
    LinearLayout e_greeting_gift_card_layout;


    public static Activity mActivity = null;

    Bundle mBundle;
    //private Socket mSocket;

    ProgressDialog dialog;
    private String pathToStoredVideo;

    MessagesViewModel mMessagesViewModel;

    private static final int MESSAGE_SENDER_ME = 1;
    private static final int MESSAGE_SENDER_OTHERS = 2;
    private static final int MESSAGE_TYPING = 3;
    private static final int E_GREETING_ME = 4;
    private static final int E_GREETING_OTHERS = 5;
    private static final int E_IMAGE_FILE_ME = 6;
    private static final int E_IMAGE_FILE_OTHERS = 7;
    private static final int E_VIDEO_FILE_ME = 8;
    private static final int E_VIDEO_FILE_OTHERS = 9;


    Typeface myTypeFace;

    //Image Upload
    File mPhotoFile;
    Uri photoURI;
    Uri capturedUri = null;
    Uri compressUri = null;
    Uri selectedUri;

    static final int REQUEST_TAKE_PHOTO = 1;
    static final int REQUEST_GALLERY_PHOTO = 2;
    static final int REQUEST_GALLERY_VIDEO = 3;
    private List<MessagesListModel> mMessagesListModelList = new ArrayList<MessagesListModel>();
    SharedPreferenceHelper mSharedPreferenceHelper;

    private Boolean isConnected = true;
    String username, greeting_message,friend_user_id,from_all_events,filepath,e_greeting_image_path,from_video_record;

    MessagesAdapter mMessagesAdapter;

    private Socket mSocket;
    TokenSessionManager mTokenSessionManager;
    String mCurrentPhotoPath;

    private BroadcastReceiver currentActivityReceiver;

    public static final String FILE_PROVIDER_AUTHORITY = ".silicompressor.provider";
    private static final int REQUEST_TAKE_CAMERA_PHOTO = 1;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_STORAGE = 1;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_STORAGE_VID = 2;
    private static final int REQUEST_TAKE_VIDEO = 200;
    private static final int TYPE_IMAGE = 1;
    private static final int TYPE_VIDEO = 2;


    {
        try {

            mSocket = IO.socket(AppConfig.CHAT_SERVER_URL);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private static final int TYPING_TIMER_LENGTH = 600;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_detail_new);


        ButterKnife.bind(this);

        mActivity = this;



        mMessagesViewModel = ViewModelProviders.of(this).get(MessagesViewModel.class);

        back_button_message_detail.setOnClickListener(this);
        send_layout.setOnClickListener(this);
        e_greeting_gift_card_layout.setOnClickListener(this);
        send_image_or_video.setOnClickListener(this);
        send_video.setOnClickListener(this);

        mSharedPreferenceHelper = new SharedPreferenceHelper(this);
        mTokenSessionManager = new TokenSessionManager(this);

        myTypeFace = ResourcesCompat.getFont(MessageDetailActivity.this, R.font.gil_regular);

        mMessagesAdapter = new MessagesAdapter(this, mSharedPreferenceHelper, new ArrayList<>(),this);
        scrollToBottom();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            // clear FLAG_TRANSLUCENT_STATUS flag:
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.text_color));
        }

        mBundle = getIntent().getExtras();

        //getting the friend userId from previous screen
        if (mBundle != null && (mBundle.getString("is_from_push") != null))
        {

            username = mBundle.getString("userName");
            friend_user_id = mBundle.getString("senderId");
            mTokenSessionManager.setChatTopId(friend_user_id);
            userNameMessageDetail.setText(username);

            Log.e("data_messageee",username + "\n" + friend_user_id);

        }else if(mBundle != null && mBundle.getString("from_all_events") != null){

            username = mBundle.getString("username");
            from_all_events = mBundle.getString("from_all_events");
            e_greeting_image_path = mBundle.getString("e_greeting_image_path");
            friend_user_id = mBundle.getString("friend_user_id");
            greeting_message = mBundle.getString("greeting_message");
            userNameMessageDetail.setText(username);
        }else if (mBundle != null && mBundle.getString("from_video_record") != null)
        {
            from_video_record = mBundle.getString("from_video_record");
            username = mBundle.getString("username");
            friend_user_id = mBundle.getString("friend_user_id");
            filepath = mBundle.getString("imageFile");
            userNameMessageDetail.setText(username);
        }
        else {
            username = mBundle.getString("username");
            friend_user_id = mBundle.getString("friend_user_id");
            userNameMessageDetail.setText(username);
        }


        onSocketConnect();


        if (from_all_events != null) {
            attemptSendEGreeting(e_greeting_image_path);
        }else if (from_video_record != null)
        {
//            attemptSendVideo(filepath);
            uploadVideoPathToServer(filepath);
        }

    }

    //Connecting the socket
    private void onSocketConnect() {
        mSocket.on("connection", onConnect);
        //mSocket.on(Socket.EVENT_CONNECT,onConnect);
        mSocket.on(Socket.EVENT_DISCONNECT, onDisconnected);
        mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectionerror);
        mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectionerror);
        mSocket.on("message_received_3", onNewMessage);
        mSocket.on("display", onTyping);
        //mSocket.on("displayStop", onStopTyping);
        mSocket.connect();
    }

    @Override
    protected void onResume() {
        super.onResume();


        currentActivityReceiver = new CurrentActivityReceiver(this);
        LocalBroadcastManager.getInstance(this).
                registerReceiver(currentActivityReceiver, CurrentActivityReceiver.CURRENT_ACTIVITY_RECEIVER_FILTER);

        if (mBundle.getString("is_from_push") != null) {
            mMessagesViewModel.getChatMessages(mSharedPreferenceHelper.getUsername(), mSharedPreferenceHelper.getUserId(), friend_user_id, username);

        } else {
            mMessagesViewModel.getChatMessages(mSharedPreferenceHelper.getUsername(), mSharedPreferenceHelper.getUserId(), friend_user_id, username);
        }


        input_message.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (null == username) return;
                if (!mSocket.connected()) return;

                //emitting the typing listener on typing the keyboard

                input_message.setTypeface(myTypeFace);
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("sender", mSharedPreferenceHelper.getUsername());
                    jsonObject.put("senderId", mSharedPreferenceHelper.getUserId());
                    jsonObject.put("receiverId", friend_user_id);
                    jsonObject.put("receiver", username);
                    jsonObject.put("typing", true);
                    jsonObject.put("message", input_message.getText().toString().trim());
                    mSocket.emit("typing", jsonObject);

                    //mSocket.emit("typing",jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        messages_recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mMessagesAdapter = new MessagesAdapter(this, mSharedPreferenceHelper, mMessagesListModelList, this);
        //mAdapter = new MessagesAdapter(mMessages,this);
        messages_recyclerView.setAdapter(mMessagesAdapter);
        scrollToBottom();
        observeViewModel();

    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).
                unregisterReceiver(currentActivityReceiver);
        currentActivityReceiver = null;
    }


    private void observeViewModel() {

        mMessagesViewModel.messages_list.observe(this, new Observer<List<MessagesListModel>>() {
            @Override
            public void onChanged(List<MessagesListModel> messagesListModels) {
                if (messagesListModels != null && messagesListModels instanceof List) {
                    mMessagesAdapter.updateMessagesList(messagesListModels);
                    scrollToBottom();
                }
            }
        });

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_button_message_detail:

                onBackPressed();


                break;

            case R.id.send_layout:


                if (input_message.getText().toString().isEmpty()) {
                    input_message.requestFocus();
                } else {
                    attemptSend();
                }

                break;

            case R.id.e_greeting_gift_card_layout:

                Intent intent = new Intent(this,EGreetingCardActivity.class);
                intent.putExtra("username",username);
                intent.putExtra("friend_user_id",friend_user_id);
                startActivity(intent);

                break;

            case R.id.send_image_or_video:

                requestPermissions(TYPE_IMAGE);

                break;
        }
    }
    private void requestPermissions(int mediaType) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (mediaType == TYPE_IMAGE) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_WRITE_STORAGE);
            } else if (mediaType == TYPE_VIDEO){
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_WRITE_STORAGE_VID);
            }

        } else {
            if (mediaType == TYPE_IMAGE) {
                // Want to compress an image
                selectImage();
            } else if (mediaType == TYPE_VIDEO) {
                // Want to compress a video
                dispatchTakeVideoIntent();
            }

        }
    }

    private void dispatchTakeVideoIntent() {
        showVideoDialog();
    }

    private void showVideoDialog() {


        AlertDialog.Builder videoDialog = new AlertDialog.Builder(this);
        videoDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Select video from gallery",
                "Record video from camera" };
        videoDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                chooseVideoFromGallary();
                                break;
                            case 1:
                                takeVideoFromCamera();
                                break;
                        }
                    }
                });
        videoDialog.show();
    }

    public void chooseVideoFromGallary() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);

        galleryIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        if (galleryIntent.resolveActivity(getPackageManager()) != null) {
            try {

                galleryIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 10);
                galleryIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
                capturedUri = FileProvider.getUriForFile(this,
                        getPackageName() + FILE_PROVIDER_AUTHORITY,
                        createMediaFile(TYPE_VIDEO));

                galleryIntent.putExtra(MediaStore.EXTRA_OUTPUT, capturedUri);
                //Log.d(LOG_TAG, "VideoUri: " + capturedUri.toString());
                startActivityForResult(galleryIntent, REQUEST_GALLERY_VIDEO);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    private void takeVideoFromCamera() {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        takeVideoIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
            try {

                takeVideoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 10);
                takeVideoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
                capturedUri = FileProvider.getUriForFile(this,
                        getPackageName() + FILE_PROVIDER_AUTHORITY,
                        createMediaFile(TYPE_VIDEO));

                takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, capturedUri);
                //Log.d(LOG_TAG, "VideoUri: " + capturedUri.toString());
                startActivityForResult(takeVideoIntent, REQUEST_TAKE_VIDEO);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }


    /**
     * Alert dialog for capture or select from galley
     */
    private void selectImage() {
        Intent[] intentArray = new Intent[0];
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
        Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
                // Error occurred while creating the File
            }
            if (photoFile != null) {
                photoURI = FileProvider.getUriForFile(this,
                        BuildConfig.APPLICATION_ID + ".provider",
                        photoFile);
                mPhotoFile = photoFile;
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            }
        }
        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {


            takeVideoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 30);
            takeVideoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
            String fName = "VideoFileName.mp4";
            File f = new File(fName);

            takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
        }
        contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
        contentSelectionIntent.setType("*/*");
        contentSelectionIntent.putExtra(Intent.EXTRA_MIME_TYPES, new String[] {"image/*", "video/*"});
        intentArray = new Intent[]{takePictureIntent,takeVideoIntent};
        chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
        chooserIntent.putExtra(Intent.EXTRA_TITLE, "Choose an action");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
        startActivityForResult(chooserIntent, REQUEST_GALLERY_VIDEO);
    }


    private void attemptSendEGreeting(String e_greeting_image_path) {


        if (!mSocket.connected()) return;

        try {

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("sender", mSharedPreferenceHelper.getUsername());
            jsonObject.put("senderId", mSharedPreferenceHelper.getUserId());
            jsonObject.put("receiverId", friend_user_id);
            jsonObject.put("receiver", username);
            jsonObject.put("message_type", "e_greeting");
            jsonObject.put("e_greeting_text", greeting_message);
            jsonObject.put("message", e_greeting_image_path);

            mSocket.emit("send_message", jsonObject);
            input_message.setText("");
            addMessageGreeting(mSharedPreferenceHelper.getUsername(), e_greeting_image_path, mSharedPreferenceHelper.getUserId(), E_GREETING_ME,e_greeting_image_path);


            mSocket.emit("message_received_3", e_greeting_image_path);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if (isConnected) {
                        mSocket.emit("user_connected", mSharedPreferenceHelper.getUserId());
                        isConnected = true;
                    }
                }
            });
        }
    };

    private Emitter.Listener onDisconnected = args -> isConnected = false;

    private Emitter.Listener onConnectionerror = args -> {

    };

    //emitting the new message by the user
    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

            try {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject data_received = (JSONObject) args[0];
                        String username, message, senderId, receiverId, receiver, e_greeting_text,message_type;
                        try {

                            username = data_received.getString("sender");
                            message = data_received.getString("message");
                            senderId = data_received.getString("senderId");
                            receiverId = data_received.getString("receiverId");
                            receiver = data_received.getString("receiver");
                            e_greeting_text = data_received.getString("e_greeting_text");
                            message_type = data_received.getString("message_type");

                            if (senderId.equals(friend_user_id) && receiverId.equals(mSharedPreferenceHelper.getUserId()) && (message_type.equals("text"))) {
                                addMessage(username, message, friend_user_id, MESSAGE_SENDER_OTHERS);
                                typing_status.setVisibility(View.GONE);
                                //removeTyping(username);
                            } else if (senderId.equals(friend_user_id) && receiverId.equals(mSharedPreferenceHelper.getUserId()) && message_type.equals("image")){

                                addMessageImage(username, message, friend_user_id, E_IMAGE_FILE_OTHERS, e_greeting_text);
                                typing_status.setVisibility(View.GONE);

                            }else if (senderId.equals(friend_user_id) && receiverId.equals(mSharedPreferenceHelper.getUserId()) && message_type.equals("e_greeting")){
                                addMessageGreeting(username, message, friend_user_id, E_GREETING_OTHERS, e_greeting_text);
                                typing_status.setVisibility(View.GONE);
                            }else if (senderId.equals(friend_user_id) && receiverId.equals(mSharedPreferenceHelper.getUserId()) && message_type.equals("video"))
                            {
                                addMessageVideo(username, message, friend_user_id, E_VIDEO_FILE_OTHERS, e_greeting_text);
                                typing_status.setVisibility(View.GONE);
                            }


                        } catch (Exception e) {
                            e.printStackTrace();
                            return;
                        }

                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    //Typing Listener
    private Emitter.Listener onTyping = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            try {

                runOnUiThread(new Runnable() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void run() {
                        JSONObject data = (JSONObject) args[0];
                        String sender, senderId, receiverId, receiver, typing;
                        try {

                            Log.e("dataaatyping", data.toString());
                            sender = data.getString("sender");
                            senderId = data.getString("senderId");
                            receiverId = data.getString("receiverId");
                            receiver = data.getString("receiver");
                            typing = data.getString("typing");

                            if (receiverId.equals(mSharedPreferenceHelper.getUserId()) && senderId.equals(friend_user_id)) {
                                //addTyping(sender, senderId);
                                typing_status.setVisibility(View.VISIBLE);
                                typing_status.setText("Typing...");

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        typing_status.setVisibility(View.GONE);
                                    }
                                }, TYPING_TIMER_LENGTH);
                            }

                        } catch (JSONException e) {
                            Log.e("Typing", e.getMessage());
                            return;
                        }
                        //addTyping(username);
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    //Sending the message by the logged in user
    private void attemptSend() {

        if (!mSocket.connected()) return;

        try {

            String message = input_message.getText().toString().trim();


            //mSocket.emit("send_message", message);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("sender", mSharedPreferenceHelper.getUsername());
            jsonObject.put("senderId", mSharedPreferenceHelper.getUserId());
            jsonObject.put("receiverId", friend_user_id);
            jsonObject.put("receiver", username);
            jsonObject.put("message_type", "text");
            jsonObject.put("e_greeting_text", "");
            jsonObject.put("message", input_message.getText().toString());

            mSocket.emit("send_message", jsonObject);
            input_message.setText("");
            addMessage(mSharedPreferenceHelper.getUsername(), message, mSharedPreferenceHelper.getUserId(), MESSAGE_SENDER_ME);


            mSocket.emit("message_received_3", message);


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void scrollToBottom() {
        messages_recyclerView.scrollToPosition(mMessagesAdapter.getItemCount() - 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_GALLERY_VIDEO){

            if( data != null) {
                selectedUri = data.getData();

                String mime_type = getMimeType(this,selectedUri);

                try {


                    if(mime_type.startsWith("image")) {

                        try {
                            InputStream is = getContentResolver().openInputStream(data.getData());
                            Bitmap bitmap = BitmapFactory.decodeStream(new ByteArrayInputStream(getBytes(is)));
                            uploadImage(getBitmapToBytes(bitmap));
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                    else if(mime_type.startsWith("video")) {
                        //It's a video

                        if (data != null && data.getData() != null) {
                            //create destination directory
                            File f = new File( Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES) + "/Silicompressor/videos");
                            if (f.mkdirs() || f.isDirectory()) {
                                //compress and output new video specs
                                Uri videoContentUri = data.getData();
                                new VideoCompressAsyncTask(this).execute("false", videoContentUri.toString(), f.getPath());
                            }

                        }
                    }

                }catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
            else {

                try {
                    InputStream is = getContentResolver().openInputStream(photoURI);
                    Bitmap bitmap = BitmapFactory.decodeStream(new ByteArrayInputStream(getBytes(is)));
                    uploadImage(getBitmapToBytes(bitmap));
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        }else {

            if (data != null && data.getData() != null) {
                //create destination directory
                File f = new File( Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES) + "/Silicompressor/videos");
                if (f.mkdirs() || f.isDirectory()) {
                    //compress and output new video specs
                    Uri videoContentUri = data.getData();
                    new VideoCompressAsyncTask(this).execute("false", videoContentUri.toString(), f.getPath());
                }

            }
        }
    }

    private byte[] getBitmapToBytes(Bitmap bmp) {

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 20, stream);
        return stream.toByteArray();
    }

    public byte[] getBytes(InputStream is) throws IOException {
        ByteArrayOutputStream byteBuff = new ByteArrayOutputStream();

        int buffSize = 1024;
        byte[] buff = new byte[buffSize];

        int len = 0;
        while ((len = is.read(buff)) != -1) {
            byteBuff.write(buff, 0, len);
        }

        return byteBuff.toByteArray();
    }


    private void uploadImage(byte[] imageBytes) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false); // if you want user to wait for some process to finish,
        builder.setView(R.layout.loading_dialogue);
        AlertDialog dialog = builder.create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitInterface retrofitInterface = retrofit.create(RetrofitInterface.class);

        RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), imageBytes);

        MultipartBody.Part imageFile = MultipartBody.Part.createFormData("chatfile", "image.jpg", requestFile);
        MultipartBody.Part userId = MultipartBody.Part.createFormData("userId",mSharedPreferenceHelper.getUserId());
        MultipartBody.Part mem = MultipartBody.Part.createFormData("mem_token",mSharedPreferenceHelper.getMemToken());
        MultipartBody.Part file_type = MultipartBody.Part.createFormData("filetype","image");
        MultipartBody.Part client = MultipartBody.Part.createFormData("clientId",AppConfig.CLIENT_ID);

        dialog.show();
        Call<ChatUploadImageResponse> call = retrofitInterface.uploadImageFromChat(imageFile,client,userId,mem,file_type);
        //mProgressBar.setVisibility(View.VISIBLE);
        call.enqueue(new Callback<ChatUploadImageResponse>() {
            @Override
            public void onResponse(Call<ChatUploadImageResponse> call, Response<ChatUploadImageResponse> response) {
                dialog.dismiss();
                if (response.isSuccessful())
                {

                    if (response.body() != null) {
                        String filepath = response.body().getFile();
                        attemptSendImage(filepath);
                    }

                }
            }

            @Override
            public void onFailure(Call<ChatUploadImageResponse> call, Throwable t) {
                dialog.dismiss();

            }
        });
    }

    class VideoCompressAsyncTask extends AsyncTask<String, String, String> {

        Context mContext;
        Uri videoContentUri;

        public VideoCompressAsyncTask(Context context) {
            mContext = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            dialog = ProgressDialog.show(MessageDetailActivity.this, "", "Compressing, Please Wait...", true);
            dialog.setMax(100);
            dialog.show();
        }

        @Override
        protected String doInBackground(String... paths) {
            String filePath = null;
            try {

                //This bellow is just a temporary solution to test that method call works
                boolean b = Boolean.parseBoolean(paths[0]);
                if (b) {
                    filePath = SiliCompressor.with(mContext).compressVideo(paths[1], paths[2]);
                } else {
                    videoContentUri = Uri.parse(paths[1]);
                    // Example using the bitrate and video size parameters
                    /*filePath = SiliCompressor.with(mContext).compressVideo(
                            videoContentUri,
                            paths[2],
                            1280,
                            720,
                            1500000);*/
                    filePath = SiliCompressor.with(mContext).compressVideo(
                            videoContentUri,
                            paths[2]);
                }


            } catch (URISyntaxException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return filePath;

        }


        @Override
        protected void onPostExecute(String compressedFilePath) {
            super.onPostExecute(compressedFilePath);

            dialog.dismiss();

            if (compressedFilePath == null){

                File f = new File(Environment.getExternalStorageDirectory().toString());
                for (File temp : f.listFiles()) {
                    if (temp.getName().equals("VideoFileName.mp4")) {
                        f = temp;
                        break;
                    }
                }

                uploadVideoToServer(f);
            }else {
                File imageFile = new File(compressedFilePath);
                float length = imageFile.length() / 1024f; // Size in KB
                String value;
                if (length >= 1024)
                    value = length / 1024f + " MB";
                else
                    value = length + " KB";

                uploadVideoToServer(imageFile);
            }
        }
    }


    private void uploadVideoPathToServer(String pathToStoredVideo) {

        File videoFile = new File(pathToStoredVideo);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false); // if you want user to wait for some process to finish,
        builder.setView(R.layout.loading_dialogue);
        AlertDialog dialog = builder.create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitInterface retrofitInterface = retrofit.create(RetrofitInterface.class);

        RequestBody requestFile = RequestBody.create(videoFile,MediaType.parse("video/*"));

        MultipartBody.Part imageFile = MultipartBody.Part.createFormData("chatfile", videoFile.getName(), requestFile);
        MultipartBody.Part userId = MultipartBody.Part.createFormData("userId",mSharedPreferenceHelper.getUserId());
        MultipartBody.Part mem = MultipartBody.Part.createFormData("mem_token",mSharedPreferenceHelper.getMemToken());
        MultipartBody.Part file_type = MultipartBody.Part.createFormData("filetype","video");
        MultipartBody.Part client = MultipartBody.Part.createFormData("clientId",AppConfig.CLIENT_ID);

        dialog.show();
        Call<ChatUploadImageResponse> call = retrofitInterface.uploadVideoFromChat(imageFile,client,userId,mem,file_type);
        //mProgressBar.setVisibility(View.VISIBLE);
        call.enqueue(new Callback<ChatUploadImageResponse>() {
            @Override
            public void onResponse(Call<ChatUploadImageResponse> call, Response<ChatUploadImageResponse> response) {
                dialog.dismiss();
                if (response.isSuccessful())
                {

                    if (response.body() != null) {

                        String filepath = response.body().getFile();
                        attemptSendVideo(filepath);
                    }

                }else {

                }
            }

            @Override
            public void onFailure(Call<ChatUploadImageResponse> call, Throwable t) {
                dialog.dismiss();

            }
        });

    }

    public String getMimeType(Context context, Uri uri) {
        String mimeType = null;
        if (ContentResolver.SCHEME_CONTENT.equals(uri.getScheme())) {
            ContentResolver cr = context.getContentResolver();
            mimeType = cr.getType(uri);
        } else {
            String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri
                    .toString());
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                    fileExtension.toLowerCase());
        }
        return mimeType;
    }

    private void uploadVideoToServer(File video_base64) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false); // if you want user to wait for some process to finish,
        builder.setView(R.layout.loading_dialogue);
        AlertDialog dialog = builder.create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitInterface retrofitInterface = retrofit.create(RetrofitInterface.class);

        RequestBody requestFile = RequestBody.create(video_base64,MediaType.parse("video/*"));

        MultipartBody.Part imageFile = MultipartBody.Part.createFormData("chatfile", video_base64.getName(), requestFile);
        MultipartBody.Part userId = MultipartBody.Part.createFormData("userId",mSharedPreferenceHelper.getUserId());
        MultipartBody.Part mem = MultipartBody.Part.createFormData("mem_token",mSharedPreferenceHelper.getMemToken());
        MultipartBody.Part file_type = MultipartBody.Part.createFormData("filetype","video");
        MultipartBody.Part client = MultipartBody.Part.createFormData("clientId",AppConfig.CLIENT_ID);

        //mProfileViewModel.getImageUploadResponse(client,mem,userId,imageFile);

        dialog.show();
        Call<ChatUploadImageResponse> call = retrofitInterface.uploadVideoFromChat(imageFile,client,userId,mem,file_type);
        //mProgressBar.setVisibility(View.VISIBLE);
        call.enqueue(new Callback<ChatUploadImageResponse>() {
            @Override
            public void onResponse(Call<ChatUploadImageResponse> call, Response<ChatUploadImageResponse> response) {
                dialog.dismiss();
                if (response.isSuccessful())
                {

                    if (response.body() != null) {

                        String filepath = response.body().getFile();
                        attemptSendVideo(filepath);


                    }

                }else {

                }
            }

            @Override
            public void onFailure(Call<ChatUploadImageResponse> call, Throwable t) {
                dialog.dismiss();

            }
        });



    }

    private void attemptSendVideo(String filepath) {

        if (!mSocket.connected()) return;

        try {


            //mSocket.emit("send_message", message);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("sender", mSharedPreferenceHelper.getUsername());
            jsonObject.put("senderId", mSharedPreferenceHelper.getUserId());
            jsonObject.put("receiverId", friend_user_id);
            jsonObject.put("receiver", username);
            jsonObject.put("message_type", "video");
            jsonObject.put("e_greeting_text", "");
            jsonObject.put("message", filepath);

            mSocket.emit("send_message", jsonObject);
            input_message.setText("");
            addMessageVideo(mSharedPreferenceHelper.getUsername(), filepath, mSharedPreferenceHelper.getUserId(), E_VIDEO_FILE_ME,filepath);


            mSocket.emit("message_received_3", filepath);

            // perform the sending message attempt.


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void attemptSendImage(String filepath) {

        if (!mSocket.connected()) return;

        try {

            //mSocket.emit("send_message", message);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("sender", mSharedPreferenceHelper.getUsername());
            jsonObject.put("senderId", mSharedPreferenceHelper.getUserId());
            jsonObject.put("receiverId", friend_user_id);
            jsonObject.put("receiver", username);
            jsonObject.put("message_type", "image");
            jsonObject.put("e_greeting_text", "");
            jsonObject.put("message", filepath);

            mSocket.emit("send_message", jsonObject);
            input_message.setText("");
            addMessageImage(mSharedPreferenceHelper.getUsername(), filepath, mSharedPreferenceHelper.getUserId(), E_IMAGE_FILE_ME,"");


            mSocket.emit("message_received_3", filepath);

            // perform the sending message attempt.


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void addMessage(String username, String message, String userid, int messageSenderOthers) {


        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault());
        String currentDateandTime = sdf.format(new Date());

        if (message != null) {
            //mMessages.add(new Message(message,username,messageSenderOthers));
            mMessagesListModelList.add(new MessagesListModel(message, userid, currentDateandTime, messageSenderOthers, "text", ""));
            if (mMessagesAdapter != null) {
                mMessagesAdapter.notifyDataSetChanged();
                scrollToBottom();
            }
        }
    }

    private void addMessageGreeting(String username, String message, String userId, int eGreetingMe, String e_greeting_image_path) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault());
        String currentDateandTime = sdf.format(new Date());

        if (message != null) {
            //mMessages.add(new Message(message,username,messageSenderOthers));
            mMessagesListModelList.add(new MessagesListModel(message, userId, currentDateandTime, eGreetingMe, "e_greeting", e_greeting_image_path));
            if (mMessagesAdapter != null) {
                mMessagesAdapter.notifyDataSetChanged();
                scrollToBottom();
            }
        }
    }

    private void addMessageImage(String username, String message, String userId, int eImageFileMe, String filepath) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault());
        String currentDateandTime = sdf.format(new Date());

        if (message != null) {
            //mMessages.add(new Message(message,username,messageSenderOthers));
            mMessagesListModelList.add(new MessagesListModel(message, userId, currentDateandTime, eImageFileMe, "image", ""));
            if (mMessagesAdapter != null) {
                mMessagesAdapter.notifyDataSetChanged();
                scrollToBottom();
            }
        }

    }

    private void addMessageVideo(String username, String filepath, String userId, int eVideoFileMe, String filepath1) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault());
        String currentDateandTime = sdf.format(new Date());

        if (filepath != null) {
            //mMessages.add(new Message(message,username,messageSenderOthers));
            mMessagesListModelList.add(new MessagesListModel(filepath, userId, currentDateandTime, eVideoFileMe, "video", ""));
            if (mMessagesAdapter != null) {
                mMessagesAdapter.notifyDataSetChanged();
                scrollToBottom();
            }
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    //disconnecting socket on backPress of the chat screen.
    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (mBundle.getString("is_from_push") != null)
        {
            mSocket.disconnect();

            mSocket.off("connection", onConnect);
            mSocket.off(Socket.EVENT_DISCONNECT, onDisconnected);
            mSocket.off(Socket.EVENT_CONNECT_ERROR, onConnectionerror);
            mSocket.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectionerror);
            mSocket.off("message_received_3", onNewMessage);
            mSocket.off("typing", onTyping);
            Intent intent = new Intent(MessageDetailActivity.this, HomeScreenActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

        }else if (mBundle.getString("from_all_events") != null) {

            Intent intent = new Intent(MessageDetailActivity.this, HomeScreenActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

        }else {
            mSocket.disconnect();

            mSocket.off("connection", onConnect);
            mSocket.off(Socket.EVENT_DISCONNECT, onDisconnected);
            mSocket.off(Socket.EVENT_CONNECT_ERROR, onConnectionerror);
            mSocket.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectionerror);
            mSocket.off("message_received_3", onNewMessage);
            mSocket.off("typing", onTyping);
            //mSocket.off("displayStop", onStopTyping);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //dispatchTakePictureIntent();
                    selectImage();
                } else {
                    Toast.makeText(this, "You need to enable the permission for External Storage Write" +
                            " to test out this library.", Toast.LENGTH_LONG).show();
                    return;
                }
                break;
            }
            case MY_PERMISSIONS_REQUEST_WRITE_STORAGE_VID: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    dispatchTakeVideoIntent();
                } else {
                    Toast.makeText(this, "You need to enable the permission for External Storage Write" +
                            " to test out this library.", Toast.LENGTH_LONG).show();
                    return;
                }
                break;
            }
        }
    }


    private File createMediaFile(int type) throws IOException {

        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = (type == REQUEST_GALLERY_VIDEO) ? "JPEG_" + timeStamp + "_" : "VID_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                type == REQUEST_GALLERY_VIDEO ? Environment.DIRECTORY_PICTURES : Environment.DIRECTORY_MOVIES);
        File file = File.createTempFile(
                fileName,  /* prefix */
                type == REQUEST_GALLERY_VIDEO ? ".jpg" : ".mp4",         /* suffix */
                storageDir      /* directory */
        );

        // Get the path of the file created
        mCurrentPhotoPath = file.getAbsolutePath();
        return file;
    }

    /**
     * Create file with current timestamp name
     *
     * @throws IOException
     */
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String mFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File mFile = File.createTempFile(mFileName, ".jpg", storageDir);
        return mFile;
    }

}