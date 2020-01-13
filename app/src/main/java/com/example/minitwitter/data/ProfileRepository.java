package com.example.minitwitter.data;

import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;

import com.example.minitwitter.common.Constantes;
import com.example.minitwitter.common.MyApp;
import com.example.minitwitter.common.SharedPreferencesManager;
import com.example.minitwitter.retrofit.AuthTwitterClient;
import com.example.minitwitter.retrofit.AuthTwitterService;
import com.example.minitwitter.retrofit.request.RequestUserProfile;
import com.example.minitwitter.retrofit.response.ResponseUploadPhoto;
import com.example.minitwitter.retrofit.response.ResponseUserProfile;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//Aca traemos la conexion al webSerivice, retrofit
public class ProfileRepository {

    private AuthTwitterService authTwitterService; //para las peticiones con token(getAllTweets)
    private AuthTwitterClient authTwitterClient; //para hacer las peticiones privadas con token
    private MutableLiveData<ResponseUserProfile> userProfile; //lista de datos dinamicos, que cambian.
    //MutableLiveData permite setear cambios y saber que lista de tweets es la que tenemos

    private MutableLiveData<String> photoProfile;


    public ProfileRepository() {
        //inicilizamos la conexion a retrofit
        authTwitterClient = AuthTwitterClient.getInstance();
        authTwitterService = authTwitterClient.getAuthTwitterApiService();
        userProfile = getProfile();
        if(photoProfile == null){
            photoProfile = new MutableLiveData<>();
        }

    }

    //obtener los datos del usuario logueado
    public MutableLiveData<ResponseUserProfile> getProfile() {

        if (userProfile == null) {
            userProfile = new MutableLiveData<>();
        }

        Call<ResponseUserProfile> call = authTwitterService.getProfile();
        call.enqueue(new Callback<ResponseUserProfile>() {
            @Override
            public void onResponse(Call<ResponseUserProfile> call, Response<ResponseUserProfile> response) {
                if(response.isSuccessful()){
                    userProfile.setValue(response.body());
                }else{
                    Toast.makeText(MyApp.getContext(), "Algo salio mal", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseUserProfile> call, Throwable t) {
                Toast.makeText(MyApp.getContext(), "Error en la conexion", Toast.LENGTH_SHORT).show();
            }
        });


        return userProfile;
    }

    //actualizar los datos del usuario logueado
    public void updateProfile(RequestUserProfile requestUserProfile){
        Call<ResponseUserProfile> call = authTwitterService.updateProfile(requestUserProfile);
        call.enqueue(new Callback<ResponseUserProfile>() {
            @Override
            public void onResponse(Call<ResponseUserProfile> call, Response<ResponseUserProfile> response) {
                if(response.isSuccessful()){
                    userProfile.setValue(response.body());//almaceno la info que llega del server para ser devuelta
                }else{
                    Toast.makeText(MyApp.getContext(), "Algo salio mal", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseUserProfile> call, Throwable t) {
                Toast.makeText(MyApp.getContext(), "Error en la conexion", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void uploadPhoto(String photoPath){//photoPath: url de la foto en nuestro dispositivo.

        File file = new File(photoPath);

        RequestBody requestBody  = RequestBody.create(MediaType.parse("image/jpg"), file);

        Call<ResponseUploadPhoto> call = authTwitterService.uploadProfilePhoto(requestBody);

        call.enqueue(new Callback<ResponseUploadPhoto>() {
            @Override
            public void onResponse(Call<ResponseUploadPhoto> call, Response<ResponseUploadPhoto> response) {
                if(response.isSuccessful()){
                    //invoco al SharedPreferences para guardar la nueva ruta de la foto
                    SharedPreferencesManager.setSomeStringValue(Constantes.PREF_PHOTOURL, response.body().getFilename());
                    photoProfile.setValue(response.body().getFilename()); //le indicamos que tenemos un nuevo valor al MutableLiveData
                }else{
                    Toast.makeText(MyApp.getContext(), "Algo salio mal", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseUploadPhoto> call, Throwable t) {
                Toast.makeText(MyApp.getContext(), "Error en la conexion", Toast.LENGTH_SHORT).show();

            }
        });

    }

}
