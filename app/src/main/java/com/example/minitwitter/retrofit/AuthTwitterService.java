package com.example.minitwitter.retrofit;

import com.example.minitwitter.retrofit.request.RequestCreateTweet;
import com.example.minitwitter.retrofit.request.RequestUserProfile;
import com.example.minitwitter.retrofit.response.ResponseUploadPhoto;
import com.example.minitwitter.retrofit.response.ResponseUserProfile;
import com.example.minitwitter.retrofit.response.Tweet;
import com.example.minitwitter.retrofit.response.TweetDeleted;

import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
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

    //peticion para realizar la eliminacion de un tweet
    @DELETE("tweets/{idTweet}")
    Call<TweetDeleted> deleteTweet(@Path("idTweet") int idTweet);


    //Peticiones relacionadas con la info de los usuarios
    @GET("users/profile")
    Call<ResponseUserProfile> getProfile();

    //Para modficar un dato que ya existe se usa put
    @PUT("users/profile")
    Call<ResponseUserProfile> updateProfile(@Body RequestUserProfile requestUserProfile);


    //subir foto de perfil
    @Multipart //para decir que enviaremos un fichero por partes
    @POST("users/uploadprofilephoto")
    Call<ResponseUploadPhoto> uploadProfilePhoto(@Part("file\"; filename=\"photo.jpeg\" ")RequestBody file);



}
