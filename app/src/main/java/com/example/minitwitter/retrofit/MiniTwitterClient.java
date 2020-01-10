package com.example.minitwitter.retrofit;

import com.example.minitwitter.common.Constantes;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MiniTwitterClient {
    //clase cliente para generar la conexion a la Api
    //usando el patron singleton

    private static MiniTwitterClient instance = null;
    private MiniTwitterApiService miniTwitterApiService;
    private Retrofit retrofit;

    public MiniTwitterClient() {
        retrofit = new Retrofit.Builder()
                .baseUrl(Constantes.API_MINITWITTER_BASE_URL) //url de la api
                .addConverterFactory(GsonConverterFactory.create()) //conversor de objetos que voy a usar, .create() genera la instancia del conversor
                .build();//finaliza la creacion del objeto Retrofit

        //Una vez instanciado el Retrofit podemos instanciar el Servicio
        miniTwitterApiService = retrofit.create(MiniTwitterApiService.class);

        //Cada vez que instancia MiniTwitterClient ya instancio tambien retrofit y el servicio
    }

    //Metodo que devuelva una instancia del Client/ Singleton
    public static MiniTwitterClient getInstance(){
        if(instance == null){
            instance = new MiniTwitterClient();
        }
        return instance;
    }

    //nos permite consumir los servicios definidos de la api, devuelve el servicio instanciado en el constructor
    public MiniTwitterApiService getMiniTwitterApiService(){
        return miniTwitterApiService;
    }
}
