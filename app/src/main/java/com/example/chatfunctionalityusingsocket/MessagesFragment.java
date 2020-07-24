package com.example.chatfunctionalityusingsocket;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;
import com.memorease.model.ListOfFriendsModel;
import com.memorease.rest.AppController;
import com.memorease.utils.AppConfig;
import com.memorease.utils.SharedPreferenceHelper;
import com.memorease.viewmodel.MessagesViewModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class MessagesFragment extends Fragment implements MessagesFragmentAdapter.ItemListener, View.OnClickListener,SwipeRefreshLayout.OnRefreshListener{

    @BindView(R.id.messages_recyclerView)
    RecyclerView messages_recyclerView;

    @BindView(R.id.no_friends)
    TextView no_friends;

    @BindView(R.id.swipeToRefreshMessages)
    SwipeRefreshLayout swipeToRefreshMessages;

    @BindView(R.id.shimmer_view_container)
    ShimmerFrameLayout shimmer_view_container;

    //MessagesFragmentAdapter mMessagesFragmentAdapter;
    MessagesFragmentAdapter mMessagesFragmentAdapter = new MessagesFragmentAdapter(new ArrayList<>(),this,getActivity());

    MessagesViewModel mMessagesViewModel;

    AllEventsViewModel mAllEventsViewModel;


    List<ListOfFriendsModel> mListOfFriendsModelList;
    //PagedList<ListOfFriendsModel> mListOfFriendsModelList;



    boolean isConnected;

    private Socket mSocket;

    SharedPreferenceHelper mSharedPreferenceHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_messages, container, false);

        ButterKnife.bind(this,view);

        mSharedPreferenceHelper = new SharedPreferenceHelper(getActivity());

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());

        swipeToRefreshMessages.setOnRefreshListener(this);

        //mMessagesFragmentAdapter = new MessagesFragmentAdapter(getActivity(),this);

        messages_recyclerView.setLayoutManager(linearLayoutManager);
        messages_recyclerView.setHasFixedSize(true);
        messages_recyclerView.setAdapter(mMessagesFragmentAdapter);
        //Connecting Socket
        AppController appController = (AppController) getActivity().getApplication();
        mSocket = appController.getSocket();
        mSocket.connect();
        mSocket.on(Socket.EVENT_DISCONNECT,onDisconnected);

        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mMessagesViewModel = ViewModelProviders.of(this).get(MessagesViewModel.class);

        mAllEventsViewModel = new AllEventsViewModel(AppController.create(getActivity()));
        messages_recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener(){
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int topRowVerticalPosition =
                        (recyclerView == null || recyclerView.getChildCount() == 0) ? 0 : recyclerView.getChildAt(0).getTop();
                swipeToRefreshMessages.setEnabled(topRowVerticalPosition >= 0);

            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });

        observeViewModel();

    }

    @Override
    public void onResume() {
        super.onResume();

        //Connecting socket while coming back from one to one chat
        AppController appController = (AppController) getActivity().getApplication();
        mSocket = appController.getSocket();
        mSocket.connect();
        if (!mSocket.connected())
        {
            mSocket.on(Socket.EVENT_DISCONNECT,onDisconnected);
            mSocket.on("message_received_3",onNewMessage);
            mSocket.on("display", onTyping);
            mSocket.on("stopTyping", onStopTyping);
        }else {
            mSocket.on(Socket.EVENT_DISCONNECT,onDisconnected);
            mSocket.on("message_received_3",onNewMessage);
            mSocket.on("display", onTyping);
            mSocket.on("stopTyping", onStopTyping);
        }
        shimmer_view_container.startShimmerAnimation();

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                swipeToRefreshMessages.post(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            mMessagesViewModel.getListOfChatUsers(mSharedPreferenceHelper.getUserId(),mSharedPreferenceHelper.getMemToken(), AppConfig.CLIENT_ID);

                        }catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }, 1000);

    }


    @Override
    public void onPause() {
        super.onPause();
        shimmer_view_container.stopShimmerAnimation();
    }


    private void observeViewModel() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(false); // if you want user to wait for some process to finish,
        builder.setView(R.layout.loading_dialogue);
        AlertDialog dialog = builder.create();

        swipeToRefreshMessages.setRefreshing(true);

        //Getting the chat list from the server.
        mMessagesViewModel.friends_list.observe(getActivity(), new Observer<List<ListOfFriendsModel>>() {
            @Override
            public void onChanged(List<ListOfFriendsModel> listOfFriendsModels) {

                shimmer_view_container.stopShimmerAnimation();
                shimmer_view_container.setVisibility(View.GONE);
                swipeToRefreshMessages.setRefreshing(false);

                if (listOfFriendsModels != null && listOfFriendsModels instanceof List)
                {
                    if (listOfFriendsModels.size() == 0){

                        messages_recyclerView.setVisibility(View.GONE);
                        no_friends.setVisibility(View.VISIBLE);

                    }else {
                        no_friends.setVisibility(View.GONE);
                        messages_recyclerView.setVisibility(View.VISIBLE);
                        mListOfFriendsModelList = listOfFriendsModels;
                        mMessagesFragmentAdapter.updateChatUserList(listOfFriendsModels);
                        runLayoutAnimation(messages_recyclerView);
                    }
                }
            }
        });

        mMessagesViewModel.message.observe(getActivity(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (s != null && s instanceof String)
                {
                    Toast.makeText(getActivity(), ""+s, Toast.LENGTH_SHORT).show();
                }
            }
        });

        mMessagesViewModel.loading.observe(getActivity(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isLoading) {
                if(isLoading != null && isLoading instanceof Boolean)
                {

                    if (isLoading)
                    {
                        shimmer_view_container.setVisibility(View.VISIBLE);
                        shimmer_view_container.startShimmerAnimation();
                    }else {
                        shimmer_view_container.stopShimmerAnimation();
                    }
                }
            }
        });

        mMessagesViewModel.message_error.observe(getActivity(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (Objects.requireNonNull(s).contains("Unable to resolve host")) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            ToastMessageManager.showWarningToastMessage(Objects.requireNonNull(getActivity()),getString(R.string.internet_check_text));
                            shimmer_view_container.setVisibility(View.GONE);
                            swipeToRefreshMessages.setRefreshing(false);
                            messages_recyclerView.setVisibility(View.GONE);
                            no_friends.setText("Please enable Internet");
                            no_friends.setGravity(Gravity.CENTER);
                        }
                    });
                }
            }
        });

    }


    private Emitter.Listener onDisconnected = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

            isConnected = false;
        }
    };

    //Showing "Typing.." indicator while user is typing
    private Emitter.Listener onTyping = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            try {
                getActivity().runOnUiThread(new Runnable() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void run() {
                        JSONObject data = (JSONObject) args[0];
                        String sender, senderId, receiverId, receiver, typing;
                        try {
                            sender = data.getString("sender");
                            senderId = data.getString("senderId");
                            receiverId = data.getString("receiverId");
                            receiver = data.getString("receiver");
                            typing = data.getString("typing");

                            try {
                                for (int i = 0; i < mListOfFriendsModelList.size(); i++) {

                                    if (receiverId.equals(mSharedPreferenceHelper.getUserId()) && senderId.equals(mListOfFriendsModelList.get(i).frienduserid)) {

                                        mListOfFriendsModelList.get(i).setTyping_status(true);
                                        mMessagesFragmentAdapter.notifyDataSetChanged();

                                        int finalI = i;
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                mListOfFriendsModelList.get(finalI).setTyping_status(false);
                                                mMessagesFragmentAdapter.notifyDataSetChanged();
                                            }
                                        },1000);
                                    }

                                }
                            }catch (Exception e)
                            {
                                e.printStackTrace();
                            }


                        } catch (JSONException e) {
                            return;
                        }
                    }
                });
            }catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    };

    private Emitter.Listener onStopTyping = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            try {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject data = (JSONObject) args[0];
                        String username;
                        try {
                            username = data.getString("sender");
                        } catch (JSONException e) {
                            Log.e("stop_typing", e.getMessage());
                            return;
                        }

                    }
                });
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    };

    //Receiving message from the users to the chat screen.
    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

            if (getActivity() != null) {

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject data_received = (JSONObject) args[0];
                        String sender, message, senderId, receiverId, receiver,message_type;
                        try {

                            sender = data_received.getString("sender");
                            message = data_received.getString("message");
                            senderId = data_received.getString("senderId");
                            receiverId = data_received.getString("receiverId");
                            receiver = data_received.getString("receiver");
                            message_type = data_received.getString("message_type");


                            for (int i = 0; i < mListOfFriendsModelList.size(); i++) {

                                if (receiverId.equals(mSharedPreferenceHelper.getUserId()) && senderId.equals(mListOfFriendsModelList.get(i).frienduserid)) {

                                    Log.e("news_value", data_received.getString("message").toString());


                                    int count = mListOfFriendsModelList.get(i).getMessage_count();

                                    int newcount = count++;

                                    mListOfFriendsModelList.get(i).setMessage_count(count);

                                    mListOfFriendsModelList.get(i).setLast_unread_message(message);

                                    removeItemPosition(i,sender,senderId,mListOfFriendsModelList.get(i).message_count,mListOfFriendsModelList.get(i).last_unread_message,mListOfFriendsModelList.get(i).firendpic,message_type);

                                }

                            }


                        } catch (Exception e) {
                            e.printStackTrace();
                            return;
                        }

                    }
                });
            }
        }
    };

    //Removing the chat position on getting new message
    private void removeItemPosition(int i, String sender, String senderId,int messagecount,String lastMessage,String userImage,String message_type) {
        mListOfFriendsModelList.remove(i); // Removes item from the list
        mMessagesFragmentAdapter.notifyItemRemoved(i);
        addItemOnTop(i,sender,senderId,messagecount,lastMessage,userImage,message_type);
    }

    //After removing it adding the chat position to the top
    private void addItemOnTop(int i, String sender, String senderId,int messageCount,String last_message,String userimage,String message_type) {

        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        String current_time = sdf.format(new Date());

        mListOfFriendsModelList.add(0,new ListOfFriendsModel(sender,userimage,senderId,"true",messageCount,last_message,false,current_time,message_type));
        mMessagesFragmentAdapter.notifyItemInserted(0);
        messages_recyclerView.smoothScrollToPosition(0);
        mMessagesFragmentAdapter.notifyDataSetChanged();
        //showOnRecyclerView();
        mMessagesFragmentAdapter.updateChatUserList(mListOfFriendsModelList);
    }

    //disconnecting socket on Destroy
    @Override
    public void onDestroy() {
        super.onDestroy();
        mSocket.disconnect();
        mSocket.off("message_received_3", onNewMessage);
        mSocket.off(Socket.EVENT_DISCONNECT, onDisconnected);
        mSocket.off("typing", onTyping);
        mSocket.off("stopTyping", onStopTyping);

    }


    @Override
    public void onClick(View view) {

    }

    //Going to next screen on clicking the chat
    @Override
    public void onItemClick(String friend_id, String username, String unread_message) {

        //disconnecting the chat socket
        mSocket.disconnect();
        mSocket.off("message_received_3", onNewMessage);
        mSocket.off(Socket.EVENT_DISCONNECT, onDisconnected);
        mSocket.off("typing", onTyping);
        mSocket.off("stopTyping", onStopTyping);


        for (int i = 0; i < mListOfFriendsModelList.size(); i++) {


            if (mListOfFriendsModelList.get(i).frienduserid.equals(friend_id)) {




                mListOfFriendsModelList.get(i).setMessage_count(0);

                mListOfFriendsModelList.get(i).setLast_unread_message("");

                mMessagesFragmentAdapter.notifyDataSetChanged();

            }

        }
        Intent intent = new Intent(getActivity(), MessageDetailActivity.class);
        intent.putExtra("username", username);
        intent.putExtra("friend_user_id", friend_id);
        startActivity(intent);
    }

    @Override
    public void onRefresh() {
        mMessagesViewModel.getListOfChatUsers(mSharedPreferenceHelper.getUserId(),mSharedPreferenceHelper.getMemToken(), AppConfig.CLIENT_ID);

    }

    private void runLayoutAnimation(final RecyclerView recyclerView) {
        final Context context = recyclerView.getContext();
        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_for_items);

        recyclerView.setLayoutAnimation(controller);
        mMessagesFragmentAdapter.notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }

}
