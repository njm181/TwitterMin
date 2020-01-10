package com.example.minitwitter.data;

import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;

import com.example.minitwitter.common.MyApp;
import com.example.minitwitter.retrofit.AuthTwitterClient;
import com.example.minitwitter.retrofit.AuthTwitterService;
import com.example.minitwitter.retrofit.request.RequestCreateTweet;
import com.example.minitwitter.retrofit.response.Tweet;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//Aca traemos la conexion al webSerivice, retrofit
public class TweetRepository {

    private AuthTwitterService authTwitterService; //para las peticiones con token(getAllTweets)
    private AuthTwitterClient authTwitterClient; //para hacer las peticiones privadas con token
    private MutableLiveData<List<Tweet>> allTweets; //lista de datos dinamicos, que cambian.
    //MutableLiveData permite setear cambios y saber que lista de tweets es la que tenemos

    public TweetRepository() {
        //inicilizamos la conexion a retrofit
        authTwitterClient =  AuthTwitterClient.getInstance();
        authTwitterService = authTwitterClient.getAuthTwitterApiService();
        //carga todos los tweets, y si hay modificaciones un observer en el viewModel va a estar escuchando para aplicar cambios
        allTweets = getAllTweets();
    }

    //llenar la lista allTweets
    public MutableLiveData<List<Tweet>> getAllTweets(){

        if(allTweets == null){
            allTweets = new MutableLiveData<>();
        }

        //para obtener los datos, llamamos al codigo que nos conecta con la api y nos devuelve la lista de tweets
        //realizar la peticion para pedir todos los tweets, como requiere token usamos la clase AuthTwitterClient

        Call<List<Tweet>> call = authTwitterService.getAllTweets();//obtengo todos los tweets
        call.enqueue(new Callback<List<Tweet>>() { //Este metodo enqueue permite ejecutar la peticion a la api en segundo plano
            @Override
            public void onResponse(Call<List<Tweet>> call, Response<List<Tweet>> response) {
                if (response.isSuccessful()){
                    allTweets.setValue(response.body());//obtengo los tweets
                }else{
                    Toast.makeText(MyApp.getContext(), "Algo salio mal al obtener todos los tweets", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Tweet>> call, Throwable t) {
                Toast.makeText(MyApp.getContext(), "Error en la conexion", Toast.LENGTH_SHORT).show();
            }
        });

        //Actulizar la lista para devolver
        return allTweets;
    }

    //metodo para enviar por POST un nuevo Tweet y que se refresque la lista de tweets
    public void createTweet(String mensajeDelTweet){

        //cuerpo de la peticion
        RequestCreateTweet requestCreateTweet = new RequestCreateTweet(mensajeDelTweet);

        //invocamos la peticion que devuelve un objeto de tipo Call<Tweet>
        Call<Tweet> call = authTwitterService.createTweet(requestCreateTweet);
        
        //realizar la invocacion de la llamada a la api, para obtener la respuesta de la api
        call.enqueue(new Callback<Tweet>() {
            @Override
            public void onResponse(Call<Tweet> call, Response<Tweet> response) {
                
                if(response.isSuccessful()){
                    //si salio ok la respuesta, entonces tenemos que actualizar con la lista de tweets que nos llega nuestra lista de tweets
                    //recorrer toda la lista de allTweets, la clonamos y sobre la lista clonada agregamos el nuevo tweet y los que ya teniamos
                    List<Tweet> listaClonada = new ArrayList<>();
                    //agregamos en primer lugar el nuevo Tweet que nos llega del servidor por que siempre se muestra del ultimo creado al mas viejo
                    listaClonada.add(response.body());
                    for (int i=0; i<allTweets.getValue().size(); i ++){ // allTweets.getValue(): lista del MultableLiveData

                        //recorremos toda la lista allTweets y por cada elemento vamos a hacer una copia en la lista clonada
                        listaClonada.add( new Tweet(allTweets.getValue().get(i)));
                    }
                    //setear la nueva lista con el Tweet nuevo agredado a la lista vieja
                    allTweets.setValue(listaClonada);// el SetValue() comunica al observer del ViewModel y al del TweetListFragment que hay una nueva lista
                    //y automaticamente lo refresca

                }else{
                    Toast.makeText(MyApp.getContext(), "Algo salio mal, intente de nuevo", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Tweet> call, Throwable t) {
                Toast.makeText(MyApp.getContext(), "Error en la conexion, intente de nuevo", Toast.LENGTH_SHORT).show();

            }
        });

    }


    //metodo para enviar por POST un nuevo Tweet y que se refresque la lista de tweets
    public void likeTweet(final int idTweet){

        //invocamos la peticion que devuelve un objeto de tipo Call<Tweet>
        Call<Tweet> call = authTwitterService.likeTweet(idTweet);

        //realizar la invocacion de la llamada a la api, para obtener la respuesta de la api
        call.enqueue(new Callback<Tweet>() {
            @Override
            public void onResponse(Call<Tweet> call, Response<Tweet> response) {

                if(response.isSuccessful()){
                    //si salio ok la respuesta, entonces tenemos que actualizar con la lista de tweets que nos llega nuestra lista de tweets
                    //recorrer toda la lista de allTweets, la clonamos y sobre la lista clonada agregamos el nuevo tweet y los que ya teniamos
                    List<Tweet> listaClonada = new ArrayList<>();
                    //agregamos en primer lugar el nuevo Tweet que nos llega del servidor por que siempre se muestra del ultimo creado al mas viejo
                    listaClonada.add(response.body());
                    for (int i=0; i<allTweets.getValue().size(); i ++){ // allTweets.getValue(): lista del MultableLiveData

                        //mientras clonamos la lista, vamos buscando si el tweet que estamos recorriendo en este momento
                        //es igual al idTweet que pasamos(en el que hicimos like), entonces insertamos el elemento que llega del servidor
                        //y no el elemento actual. Cuando se pinte la lista en el adapter, este elemento va a ser nuevo, y el adapter lo va a pintar de rosa
                        if(allTweets.getValue().get(i).getId() == idTweet){
                            listaClonada.add(response.body());
                        }else{
                            //recorremos toda la lista allTweets y por cada elemento vamos a hacer una copia en la lista clonada
                            listaClonada.add( new Tweet(allTweets.getValue().get(i)));
                        }
                    }
                    //setear la nueva lista con el Tweet nuevo agredado a la lista vieja
                    allTweets.setValue(listaClonada);// el SetValue() comunica al observer del ViewModel y al del TweetListFragment que hay una nueva lista
                    //y automaticamente lo refresca

                }else{
                    Toast.makeText(MyApp.getContext(), "Algo salio mal, intente de nuevo", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Tweet> call, Throwable t) {
                Toast.makeText(MyApp.getContext(), "Error en la conexion, intente de nuevo", Toast.LENGTH_SHORT).show();

            }
        });

    }


}
