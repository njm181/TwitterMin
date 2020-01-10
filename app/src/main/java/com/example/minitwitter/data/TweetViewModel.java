package com.example.minitwitter.data;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.minitwitter.retrofit.response.Tweet;

import java.util.List;

public class TweetViewModel extends AndroidViewModel {

    //acceso al webservice(api) osea al repository
    private TweetRepository tweetRepository;
    private LiveData<List<Tweet>> tweets;

    public TweetViewModel(@NonNull Application application) {
        super(application);

        //instanciamos el repository
        tweetRepository = new TweetRepository();
        //inicilizar la lista de tweets
        tweets = tweetRepository.getAllTweets();
    }


    //get de mi lista LiveData<List<Tweet>> tweets, para devolver al observer de este viewModel
    public LiveData<List<Tweet>> getTweets(){return tweets;} //actualizar solo con la nueva informacion que tengamos en local

    //metodo independiente que llama al servidor y trae los tweets, es la diferencia con el metodo de arriba, este metodo esta continuamente
    //buscando la data nueva del servidor
    public LiveData<List<Tweet>> getNewTweets(){
        //obtener los tweets del servidor
        tweets = tweetRepository.getAllTweets();//esto esta hecho en el constructor
        //lo hago aca para que pueda ser llamado desde TweetListFragment
        return tweets;
    }

    //para que se pueda invocar el metodo que esta en el repository que inserta el tweet desde el NuevoTweetDialogFragment
    public void insertTweet(String mensaje){tweetRepository.createTweet(mensaje);}

    //metodo para dar like, idTweet del tweet marcado como favorito
    public void likeTweet(int idTweet){
       tweetRepository.likeTweet(idTweet);
    }
}
