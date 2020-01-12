package com.example.minitwitter.data;

import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;

import com.example.minitwitter.common.Constantes;
import com.example.minitwitter.common.MyApp;
import com.example.minitwitter.common.SharedPreferencesManager;
import com.example.minitwitter.retrofit.AuthTwitterClient;
import com.example.minitwitter.retrofit.AuthTwitterService;
import com.example.minitwitter.retrofit.request.RequestCreateTweet;
import com.example.minitwitter.retrofit.response.Like;
import com.example.minitwitter.retrofit.response.Tweet;
import com.example.minitwitter.retrofit.response.TweetDeleted;

import java.util.ArrayList;
import java.util.Iterator;
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

    //lista para obtener de la lista actual los tweets favoritos
    private MutableLiveData<List<Tweet>> favTweets;
    //para saber que usuario es el que estamos comprobando y saber si ese usuario es el que esta logueado
    //y por lo tanto es el que marco ese tweet como favorito
    private String username;
    //El usuario es un dato que guardamos en el login con el fichero con sharedPreferences


    public TweetRepository() {
        //inicilizamos la conexion a retrofit
        authTwitterClient =  AuthTwitterClient.getInstance();
        authTwitterService = authTwitterClient.getAuthTwitterApiService();
        //carga todos los tweets, y si hay modificaciones un observer en el viewModel va a estar escuchando para aplicar cambios
        allTweets = getAllTweets();
        //Aca rescatamos el username del usuario logueado y lo vamos a necesitar para recorrer la lista de tweets
        username = SharedPreferencesManager.getSomeStringValue(Constantes.PREF_USERNAME);
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


    public void deleteTweet(final int idTweet){
        Call<TweetDeleted> call = authTwitterService.deleteTweet(idTweet);

        call.enqueue(new Callback<TweetDeleted>() {
            @Override
            public void onResponse(Call<TweetDeleted> call, Response<TweetDeleted> response) {
                if(response.isSuccessful()){
                    //deberia realizar una copia de la lista de todos los tweets de allTweets excepto el que acabo de eliminar
                    List<Tweet> listaClonada = new ArrayList<>();

                    for (int i = 0; i< allTweets.getValue().size();i++){

                        //si el tweet que estamos analizando ahora es distinto del tweet que acabo de eliminar, entonces debe quedarse en la lista
                        if(allTweets.getValue().get(i).getId() != idTweet){
                            listaClonada.add(new Tweet(allTweets.getValue().get(i)));
                        }
                    }
                    //se avisa a los observadores pendientes que hay una nueva lista de tweets
                    allTweets.setValue(listaClonada);
                    //actualizo la lista de favoritos por que talvez estaba marcado como fav
                    getFavsTweets();
                }else{
                    Toast.makeText(MyApp.getContext(), "Algo salio mal, intente de nuevo", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TweetDeleted> call, Throwable t) {
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


                    //cuando likeamos un tweet se debe refrescar la lista de favoritos, ya que hay un favorito nuevo
                    getFavsTweets();//para que vuelva a recorrer la lista de todos los tweets e introduzca el nuevo favorito

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

    //devolver la lista de tweets favoritos
    public MutableLiveData<List<Tweet>> getFavsTweets() {
        //si es la primera vez que lo llamo, lo instancia
        if(favTweets == null){
            favTweets = new MutableLiveData<>();
        }

        //1. obtener la lista de todos los tweets que tengo y recorrerla
        //para saber cuales son los tweets likeados por el usuario logueado y cuales no
        //los que si los voy a guardar en la lista de favoritos

        List<Tweet> newFavList = new ArrayList<>();//aca guardo solo los favoritos

        //recorrido de la lista de tweets
        //para iterar sobre la lista de allTweets
        //sobre la lista completa de tweets voy a obtener con el getValue un objeto de tipo List<Tweet>
        //y con el iterator obtengo el iterador que permite recorrrer esa lista
        Iterator itTweets = allTweets.getValue().iterator();

        //mientras tengamos elementos en la lista de tweets
        while (itTweets.hasNext()){
            Tweet current = (Tweet) itTweets.next(); //obtengo el tweet actual
            //ese tweet tiene una lista de usuarios que le dieron like
            //hay que hacer otro iterator de los likes
            Iterator itLikes = current.getLikes().iterator();

            //por defecto no encontramos al usuario logueado en esa lista de likes
            boolean encontrado = false;

            //recorro la lista de likes
            while (itLikes.hasNext() && !encontrado){
                Like likeActual = (Like)itLikes.next(); //like actual
                if(likeActual.getUsername().equals(username)){
                    encontrado = true;
                    newFavList.add(current);
                }

            }

        }

        //comunicar a cualquier observador que este pendiente de esta lista que hay
        //un nuevo conjunto de datos
        favTweets.setValue(newFavList);


        return favTweets;
    }


    }
