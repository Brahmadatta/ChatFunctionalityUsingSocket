package com.example.chatfunctionalityusingsocket;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.memorease.model.ChatAppUsersListResponse;
import com.memorease.model.GetMessageParams;
import com.memorease.model.GetMessagesResponse;
import com.memorease.model.MemoreaseApiService;
import com.memorease.model.PeopleYouMayKnowParams;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class MessagesViewModel extends AndroidViewModel {

    public MutableLiveData<String> status = new MutableLiveData<String>();

    public MutableLiveData<String> message = new MutableLiveData<String>();
    public MutableLiveData<String> message_error = new MutableLiveData<String>();

    public MutableLiveData<String> count = new MutableLiveData<String>();

    public MutableLiveData<Boolean> loading = new MutableLiveData<Boolean>();

    public MutableLiveData<List<ListOfFriendsModel>> friends_list = new MutableLiveData<List<ListOfFriendsModel>>();

    public MemoreaseApiService mMemoreaseApiService = new MemoreaseApiService();

    public CompositeDisposable mCompositeDisposable = new CompositeDisposable();


    //Messages List
    public MutableLiveData<String> status_messages_list = new MutableLiveData<String>();

    public MutableLiveData<List<MessagesListModel>> messages_list = new MutableLiveData<List<MessagesListModel>>();

    public MessagesViewModel(@NonNull Application application) {
        super(application);
    }




    public void getListOfChatUsers(String userId, String memToken, String clientId) {

        PeopleYouMayKnowParams peopleYouMayKnowParams = new PeopleYouMayKnowParams();

        peopleYouMayKnowParams.setUserId(userId);
        peopleYouMayKnowParams.setMem_token(memToken);
        peopleYouMayKnowParams.setClientId(clientId);

        loading.setValue(true);

        mCompositeDisposable.add(
                mMemoreaseApiService.getChatAppUserListResponse(peopleYouMayKnowParams)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<ChatAppUsersListResponse>() {
                            @Override
                            public void onSuccess(ChatAppUsersListResponse chatAppUsersListResponse) {

                                loading.setValue(false);

                                if (chatAppUsersListResponse.status.equals("Ok"))
                                {
                                    friends_list.setValue(chatAppUsersListResponse.getListOfFriendsModelList());
                                    //message.setValue(chatAppUsersListResponse.message);
                                    count.setValue(chatAppUsersListResponse.count);

                                }
                            }

                            @Override
                            public void onError(Throwable e) {

                                message_error.setValue(e.toString());
                                loading.setValue(false);
                            }
                        })
        );


    }


    @Override
    protected void onCleared() {
        super.onCleared();
        mCompositeDisposable.clear();
    }

    public void getChatMessages(String username, String userId, String friend_user_id, String others_name) {

        GetMessageParams getMessageParams = new GetMessageParams();
        getMessageParams.setSender(username);
        getMessageParams.setSenderId(userId);
        getMessageParams.setReceiverId(friend_user_id);
        getMessageParams.setReceiver(others_name);

        mCompositeDisposable.add(
                mMemoreaseApiService.getMessagesList(getMessageParams)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<GetMessagesResponse>() {
                            @Override
                            public void onSuccess(GetMessagesResponse getMessagesResponse) {

                                try {
                                    if (getMessagesResponse.status.equals("Ok"))
                                    {
                                        messages_list.setValue(getMessagesResponse.getMessagesListModelList());
                                    }
                                }catch (Exception e){
                                    messages_list.setValue(new ArrayList<>());
                                }


                                /*if (getMessagesResponse.status.equals("Ok"))
                                {
                                    messages_list.setValue(getMessagesResponse.getMessagesListModelList());
                                }else {
                                    messages_list.setValue(new ArrayList<>());
                                }*/
                            }

                            @Override
                            public void onError(Throwable e) {

                            }
                        })
        );

    }
}
