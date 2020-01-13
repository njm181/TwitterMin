package com.example.minitwitter.data;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.minitwitter.retrofit.request.RequestUserProfile;
import com.example.minitwitter.retrofit.response.ResponseUserProfile;

public class ProfileViewModel extends AndroidViewModel {

    private ProfileRepository profileRepository;
    public LiveData<ResponseUserProfile> userProfile; //si es publica ya la puede usar el observer

    public ProfileViewModel(@NonNull Application application) {
        super(application);
        profileRepository = new ProfileRepository(); //instancio el repository para poder usar el objeto de forma global
        userProfile = profileRepository.getProfile(); //obtengo el perfil del usuario autenticado
    }

    public void updateProfile(RequestUserProfile requestUserProfile){
        profileRepository.updateProfile(requestUserProfile);
    }

    //subir la foto
    public void uploadPhoto(String photo){
        profileRepository.uploadPhoto(photo);
    }


}
