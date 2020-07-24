package com.example.chatfunctionalityusingsocket;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SharedPreferenceHelper {

    private SharedPreferences mPreferences;

    private static final String DEVICE_ID = "device_id";

    private static final String EMAIL_ID = "email";

    private static final String EMAIL_TOKEN = "email_token";

    private static final String EMAIL_VERIFIED = "email_verified";

    private static final String FIRSTNAME = "firstName";

    private static final String LASTNAME = "lastName";

    private static final String GENDER = "gender";

    private static final String IS_PROFILE_COMPLETED = "is_Profile_completed";

    private static final String IS_VERIFIED = "is_verified";

    private static final String IV_TOKEN = "iv_token";

    private static final String MESSAGE_COUNT = "messages_count";

    private static final String MOBILE_NUMBER = "mobile_number";

    private static final String NOTIFICATION_COUNT = "notification_count";

    private static final String POSTS_COUNT = "posts_count";

    private static final String USER_ID = "user_id";

    private static final String USERNAME = "username";

    private static final String IS_LOGGED_IN = "is_logged_in";

    private static final String MEM_TOKEN = "mem_token";

    private static final String PROFILE_IMAGE = "profile_image";

    private static final String PASSWORD = "password";

    private static final String IS_FORGOT_PASSWORD = "is_Forgot_password";

    private static final String LIFE_EVENTS_CLICK = "is_life_event_clicked";

    private static final String FIREBASE_TOKEN = "firebase_token";

    private static final String RELIGIOUS_ID = "religious_id";


    public static final String SUB_CATEGORY_EVENT_IMAGE = "sub_category_event_url";
    public static final String SUB_CATEGORY_EVENT_NAME = "sub_category_event_name";
    public static final String SUB_CATEGORY_EVENT_TYPE_ID = "sub_category_event_type_id";
    public static final String SUB_CATEGORY_EVENT_CATEGORY_TYPE_ID = "sub_category_event_category_id";

    private static final String CLOSE_CREATE_LIFE_EVENTS_CLICK = "is_close_create_life_event_clicked";


    public SharedPreferenceHelper(Context context) {
        mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void setDeviceId(String device_id){
        mPreferences.edit().putString(DEVICE_ID,device_id).apply();
    }

    public String getDeviceId(){
        return mPreferences.getString(DEVICE_ID,null);
    }

    public void setEmailId(String emailId){
        mPreferences.edit().putString(EMAIL_ID,emailId).apply();
    }

    public String getEmailId(){
        return mPreferences.getString(EMAIL_ID,null);
    }

    public void setEmailToken(String emailToken){
        mPreferences.edit().putString(EMAIL_TOKEN,emailToken).apply();
    }
    public String getEmailToken(){
        return mPreferences.getString(EMAIL_TOKEN,null);
    }

    public void setEmailVerified(Boolean isVerified){
        mPreferences.edit().putBoolean(EMAIL_VERIFIED,isVerified).apply();
    }

    public Boolean isEmailVerified(){
        return mPreferences.getBoolean(EMAIL_VERIFIED,false);
    }

    public void setFirstname(String firstname){
        mPreferences.edit().putString(FIRSTNAME,firstname).apply();
    }

    public String getFirstname(){
        return mPreferences.getString(FIRSTNAME,null);
    }

    public void setLastname(String lastname){
        mPreferences.edit().putString(LASTNAME,lastname).apply();
    }

    public String getLastname(){
        return mPreferences.getString(LASTNAME,null);
    }

    public void setGender(String gender){
        mPreferences.edit().putString(GENDER,gender).apply();
    }

    public String getGender(){
        return mPreferences.getString(GENDER,null);
    }

    public void setIsProfileCompleted(Boolean isProfileCompleted){
        mPreferences.edit().putBoolean(IS_PROFILE_COMPLETED,isProfileCompleted).apply();
    }

    public Boolean getIsProfileCompleted(){
        return mPreferences.getBoolean(IS_PROFILE_COMPLETED,false);
    }

    public void setIsVerified(Boolean isVerified){
        mPreferences.edit().putBoolean(IS_VERIFIED,isVerified).apply();
    }

    public Boolean getIsVerified(){
        return mPreferences.getBoolean(IS_VERIFIED,false);
    }

    public void setIvToken(String ivToken){
        mPreferences.edit().putString(IV_TOKEN,ivToken).apply();
    }

    public String getIvToken(){
        return mPreferences.getString(IV_TOKEN,null);
    }

    public void setMessageCount(String messageCount){
        mPreferences.edit().putString(MESSAGE_COUNT,messageCount).apply();
    }

    public String getMessageCount(){
        return mPreferences.getString(MESSAGE_COUNT,null);
    }

    public void setMobileNumber(String mobileNumber){
        mPreferences.edit().putString(MOBILE_NUMBER,mobileNumber).apply();
    }

    public String getMobileNumber(){
        return mPreferences.getString(MOBILE_NUMBER,null);
    }

    public void setNotificationCount(String notificationCount){
        mPreferences.edit().putString(NOTIFICATION_COUNT,notificationCount).apply();
    }

    public String getNotificationCount(){
        return mPreferences.getString(NOTIFICATION_COUNT,null);
    }

    public void setPostsCount(String postsCount){
        mPreferences.edit().putString(POSTS_COUNT,postsCount).apply();
    }

    public String getPostsCount(){
        return mPreferences.getString(POSTS_COUNT,null);
    }

    public void setUserId(String userId){
        mPreferences.edit().putString(USER_ID,userId).apply();
    }

    public String getUserId(){
        return mPreferences.getString(USER_ID,null);
    }

    public void setUsername(String username){
        mPreferences.edit().putString(USERNAME,username).apply();
    }

    public String getUsername(){
        return mPreferences.getString(USERNAME,null);
    }

    public void setIsLoggedIn(Boolean isLoggedIn){
        mPreferences.edit().putBoolean(IS_LOGGED_IN,isLoggedIn).apply();
    }

    public Boolean getIsLoggedIn(){
        return mPreferences.getBoolean(IS_LOGGED_IN,false);
    }

    public void setMemToken(String memToken){
        mPreferences.edit().putString(MEM_TOKEN,memToken).apply();
    }

    public String getMemToken(){
        return mPreferences.getString(MEM_TOKEN,null);
    }

    public void setProfileImage(String profileImage){
        mPreferences.edit().putString(PROFILE_IMAGE,profileImage).apply();
    }

    public String getProfileImage(){
        return mPreferences.getString(PROFILE_IMAGE,null);
    }

    public void setPassword(String password){
        mPreferences.edit().putString(PASSWORD,password).apply();
    }

    public String getPassword(){
        return mPreferences.getString(PASSWORD,null);
    }

    public void clearPreferences()
    {
        mPreferences.edit().clear().apply();
    }

    public void setIsForgotPassword(Boolean isForgotPassword)
    {
        mPreferences.edit().putBoolean(IS_FORGOT_PASSWORD,isForgotPassword).apply();
    }

    public Boolean getIsForgotPassword()
    {
        return mPreferences.getBoolean(IS_FORGOT_PASSWORD,false);
    }

    public void setIsLifeEventClicked(Boolean isLifeEventClicked)
    {
        mPreferences.edit().putBoolean(LIFE_EVENTS_CLICK,isLifeEventClicked).apply();
    }

    public Boolean getIsLifeEventClicked()
    {
        return mPreferences.getBoolean(LIFE_EVENTS_CLICK,false);
    }

    public void setSubCategoryEventImage(String subCategoryEventImage)
    {
        mPreferences.edit().putString(SUB_CATEGORY_EVENT_IMAGE,subCategoryEventImage).apply();
    }

    public String getSubCategoryEventImage()
    {
        return mPreferences.getString(SUB_CATEGORY_EVENT_IMAGE,null);
    }

    public void setSubCategoryEventTypeId(String subCategoryEventTypeId)
    {
        mPreferences.edit().putString(SUB_CATEGORY_EVENT_TYPE_ID,subCategoryEventTypeId).apply();
    }

    public String getSubCategoryEventTypeId()
    {
        return mPreferences.getString(SUB_CATEGORY_EVENT_TYPE_ID,null);
    }

    public void setSubCategoryEventCategoryTypeId(String subCategoryEventCategoryTypeId)
    {
        mPreferences.edit().putString(SUB_CATEGORY_EVENT_CATEGORY_TYPE_ID,subCategoryEventCategoryTypeId).apply();
    }

    public String getSubCategoryEventCategoryTypeId()
    {
        return mPreferences.getString(SUB_CATEGORY_EVENT_CATEGORY_TYPE_ID,null);
    }

    public void setSubCategoryEventName(String subCategoryEventName)
    {
        mPreferences.edit().putString(SUB_CATEGORY_EVENT_NAME,subCategoryEventName).apply();
    }

    public String getSubCategoryEventName()
    {
        return mPreferences.getString(SUB_CATEGORY_EVENT_NAME,null);
    }

    public void setCloseCreateLifeEventsClick(Boolean closeCreateLifeEventsClick)
    {
        mPreferences.edit().putBoolean(CLOSE_CREATE_LIFE_EVENTS_CLICK,closeCreateLifeEventsClick).apply();
    }

    public Boolean getCloseCreateLifeEventsClick()
    {
        return mPreferences.getBoolean(CLOSE_CREATE_LIFE_EVENTS_CLICK,false);
    }

    public void setReligiousId(String religiousId)
    {
        mPreferences.edit().putString(RELIGIOUS_ID,religiousId).apply();

    }

    public String getReligiousId()
    {
        return mPreferences.getString(RELIGIOUS_ID,null);
    }




    /*public void setFirebaseToken(String firebaseToken)
    {
        mPreferences.edit().putString(FIREBASE_TOKEN,firebaseToken).apply();
    }

    public String getFirebaseToken()
    {
        return mPreferences.getString(FIREBASE_TOKEN,null);
    }*/
}

