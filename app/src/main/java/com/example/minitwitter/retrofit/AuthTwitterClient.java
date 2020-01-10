package com.example.minitwitter.retrofit;

import com.example.minitwitter.common.Constantes;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AuthTwitterClient {
    //clase cliente para generar la conexion a la Api con el token
    //para las peteciones privadas
    //usando el patron singleton

    private static AuthTwitterClient instance = null;
    private AuthTwitterService miniTwitterApiService;
    private Retrofit retrofit;

    public AuthTwitterClient() {

        //previo a la realizacion de la peticion, vamos a utilizar una clase que permite asociarle un interceptor
        //Con esto podremos adjuntarle el token al header de la peticion
        //Incluir en el header de la peticion el TOKEN que autoriza al usuario

        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();
        okHttpClientBuilder.addInterceptor(new AuthInterceptor());//configuramos el builder, donde agregamos al interceptor
        OkHttpClient clientConElToken = okHttpClientBuilder.build();//con esto fabricamos un cliente que permite asociarle info a traves de la clase
        //AuthInterceptor para usar en la peticion

        retrofit = new Retrofit.Builder()
                .baseUrl(Constantes.API_MINITWITTER_BASE_URL) //url de la api
                .addConverterFactory(GsonConverterFactory.create()) //conversor de objetos que voy a usar, .create() genera la instancia del conversor
                .client(clientConElToken)//aca notificamos que lo vamos a usar en todas las peticiones de esa clase
                .build();//finaliza la creacion del objeto Retrofit

        //Una vez instanciado el Retrofit podemos instanciar el Servicio
        miniTwitterApiService = retrofit.create(AuthTwitterService.class);

        //Cada vez que instancia MiniTwitterClient ya instancio tambien retrofit y el servicio
    }

    //Metodo que devuelva una instancia del Client/ Singleton
    public static AuthTwitterClient getInstance(){
        if(instance == null){
            instance = new AuthTwitterClient();
        }
        return instance;
    }

    //nos permite consumir los servicios definidos de la api, devuelve el servicio instanciado en el constructor
    public AuthTwitterService getAuthTwitterApiService(){
        return miniTwitterApiService;
    }
}
