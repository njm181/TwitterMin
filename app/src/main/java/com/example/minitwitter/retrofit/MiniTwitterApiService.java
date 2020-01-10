package com.example.minitwitter.retrofit;

import com.example.minitwitter.retrofit.request.RequestLogin;
import com.example.minitwitter.retrofit.request.RequestSignUp;
import com.example.minitwitter.retrofit.response.ResponseAuth;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface MiniTwitterApiService {
    //retrofit convierte la api en una interfaz
    //una interfaz por cada api a la cual conectarse
    //la va a consumir la api a traves de este servicio

    //peticion de auth de logueo, por donde envio parametros
    @POST("auth/login")
    Call<ResponseAuth> doLogin(@Body RequestLogin requestLogin);
    //@Body RequestLogin envia la informacion de la peticion en el cuerpo de la misma
    //Y la respuesta a la peticion va a venir en un objeto de tipo
    //ResponseAuth. Como las peticiones de retrofit son asincronas
    //y eso abre un nuevo hilo de ejecucion, de manera que eso genera
    //un tiempo indeterminado que haya que esperar.
    //Para decir qu eestamos haciendo una llamada y esperamos la respuesta
    //Usamos el Call de tipo <ResponseAuth>


    @POST("auth/signup")
    Call<ResponseAuth> doSignUp(@Body RequestSignUp requestSignUp);

}
