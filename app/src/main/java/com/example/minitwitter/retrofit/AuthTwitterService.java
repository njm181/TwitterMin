package com.example.minitwitter.retrofit;

import com.example.minitwitter.retrofit.request.RequestCreateTweet;
import com.example.minitwitter.retrofit.response.Tweet;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface AuthTwitterService {
    //En este servicio se generan las peticiones que necesiten el toekn de autorizacion
    //La diferencia es que para acceder a este servicio utilizaremos un cliente diferente, un cliente que incluira el token
    //la conexion retrofit incluira el token por defecto

    @GET("tweets/all")
    Call<List<Tweet>> getAllTweets();

    @POST("tweets/create")
    Call<Tweet> createTweet(@Body RequestCreateTweet requestCreateTweet);//parametro de los datos que enviamos al server

    @POST("tweets/like/{idTweet}")
    Call<Tweet> likeTweet(@Path("idTweet") int idTweet);//peticion para pasar like, por parametro el id del tweet al cual se likeo
}
