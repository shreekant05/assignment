package com.shreekant.assignment;

import com.shreekant.assignment.pojo.UserList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by shreekant on 09/05/21.
 */

interface APIInterface {

    @GET("/api/users?")
    Call<UserList> doGetUserList(@Query("page") String page, @Query("per_page") String count);

}
