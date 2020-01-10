package com.example.minitwitter.retrofit;


import com.example.minitwitter.common.Constantes;
import com.example.minitwitter.common.SharedPreferencesManager;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;


//Clase que va a obtener previamente al envio de la request al servidor toda la info de esa request
//y le va a adjuntar un header
public class AuthInterceptor implements Interceptor {



    //Este metodo va ser invocado, cada vez que en una request queramos interceptarla y aplicarle lo que hace el metodo
    @Override
    public Response intercept(Chain chain) throws IOException {//El objeto Chain permite enlanzar la request que recibimos y la que vamos a mandar finalmente
        //Rescatar el token del usuario que se loguea
        String token = SharedPreferencesManager.getSomeStringValue(Constantes.PREF_TOKEN);

        //Tomamos la request que queremos enviar y en su lugar vamos a enviar una request con mas informacion
        //A la request original le agregamos un header con el token. Bearer es un tipo de string
        Request request = chain.request().newBuilder().addHeader("Authorization", "Bearer "+token).build();
        return chain.proceed(request);//devuelve la request con el header agregado
    }
}
