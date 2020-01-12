package com.example.minitwitter.ui;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.minitwitter.R;
import com.example.minitwitter.common.Constantes;
import com.example.minitwitter.data.TweetViewModel;
import com.example.minitwitter.retrofit.response.Tweet;

import java.util.List;


public class TweetListFragment extends Fragment {

    private int tweetListType = 1;
    private RecyclerView recyclerView;
    private TweetRecyclerViewAdapter adapter;
    private List<Tweet> tweetList;
    private TweetViewModel tweetViewModel; //para conectar todos los componentes, asi podemos invocar y obtener la lista de tweets
    private SwipeRefreshLayout swipeRefreshLayout; //referencia al objeto padre del layout fragment_tweet_list que contiene al recyclerview


    public TweetListFragment() {
    }

    //crea una instancia del frafmento y le pasa un parametro
    public static TweetListFragment newInstance(int tweetListType) {
        TweetListFragment fragment = new TweetListFragment(); //crea instancia del fragmento
        Bundle args = new Bundle();
        //Cuando pasamos parametros a un fragmento o activity pasamos una variable de tipo String que define el nombre de la variable
        //y el segundo el valor de la variable
        args.putInt(Constantes.TWEET_LIST_TYPE, tweetListType); //le pasa un parametro a traves de args
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //inicializamos el viewModel, pasando el contexto actual y la clase donde esta mi ViewModel
        tweetViewModel = ViewModelProviders.of(getActivity()).get(TweetViewModel.class);

        if (getArguments() != null) {
            //recibimos el parametro que vamos a guardar en tweetListType y para rescatarla hacemos referencia a la misma constante que hemos utilizado
            tweetListType = getArguments().getInt(Constantes.TWEET_LIST_TYPE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tweet_list, container, false);//el layout padre es el swipeRefreshLayout

        // Set the adapter
        Context context = view.getContext();
        recyclerView = view.findViewById(R.id.list);//referencia al recyclerview
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout); //referencia a la vista del Swipe
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorAzul));//personalizamos el swipe

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() { //cuando quiera recargar la vista se lanza este metodo
            @Override
            public void onRefresh() {
                //que se ha finalizado el refrescar la lista. Activamos el que se esta refrescando
                swipeRefreshLayout.setRefreshing(true);

                //El fragmento esta dispuesto a aceptar la carga de dos tipos de listas diferentes que comparten el diseño
                //dependiendo el valor que posea la constante que le otorga el usuario al seleccionar un boton de nav view
                //muestra la lista de todos los tweets o solo de los favoritos
                if (tweetListType == Constantes.TWEET_LIST_ALL) {
                    //queremos refrescar la lista de tweets
                    loadNewData();//las siguientes veces cuando refresquemos llama a este metodo
                }else if(tweetListType == Constantes.TWEET_LIST_FAVS){
                    loadNewFavData();
                }



            }
        });


        recyclerView.setLayoutManager(new LinearLayoutManager(context));



        //getActivity(): Context donde se inserto el fragmento
        //tweetList: respuesta del servidor para obtener la lista de tweets
        adapter = new TweetRecyclerViewAdapter(getActivity(),tweetList);
        recyclerView.setAdapter(adapter);


        if (tweetListType == Constantes.TWEET_LIST_ALL) {
            loadTweetData();//la primera vez se usa este metodo para obtener la lista de tweets del server, la siguiente vez llama a loadNewData()
        }else if(tweetListType == Constantes.TWEET_LIST_FAVS){
            loadFavTweetData();
        }


        return view;
    }

    //nos trae nuesvos tweets del servidor y ademas filtra los favoritos
    private void loadNewFavData() {
        tweetViewModel.getNewFavsTweets().observe(getActivity(), new Observer<List<Tweet>>() {
            @Override
            public void onChanged(List<Tweet> tweets) {
                tweetList = tweets;//guardamos la lista de tweets que recibimos

                //este metodo es llamado por wl swipe, entonces paramos el spinner
                swipeRefreshLayout.setRefreshing(false);
                adapter.setData(tweetList); //actualizamos la vista

                //elimino este observer una vez utilizado, ya que se usa cada vez que el usuario hace swipe
                //asi no interrumpe al observer de loadFavTweetData
                tweetViewModel.getNewFavsTweets().removeObserver(this);


            }
        });

    }

    private void loadFavTweetData() {
        //lista de favoritos que viene del viewModel - patron MVVM
        tweetViewModel.getFavTweets().observe(getActivity(), new Observer<List<Tweet>>() {
            @Override
            public void onChanged(List<Tweet> tweets) {
                //sobre la lista de tweets que recibi la guarda en la lista que dibuja el adapter
                tweetList = tweets;
                adapter.setData(tweetList);
            }
        });
    }


    private void loadTweetData() {
        //pendiente de los cambios que se produzcan en el modelo, y el modelo a su vez del repository
        //observar cuando recibimos la lista de tweets
        tweetViewModel.getTweets().observe(getActivity(), new Observer<List<Tweet>>() {
            @Override
            public void onChanged(List<Tweet> tweets) { //si cambio algo en la lista entonces hace lo siguiente
                tweetList = tweets; //igualamos la lista vieja a la actualizada
                //le decimos al adapter que hay cambios que debe mostrar
                adapter.setData(tweetList);
            }
        });

    }

    private void loadNewData() {
        //traemos toda la lista nueva del servidor
        tweetViewModel.getNewTweets().observe(getActivity(), new Observer<List<Tweet>>() {
            @Override
            public void onChanged(List<Tweet> tweets) { //si cambio algo en la lista entonces hace lo siguiente
                tweetList = tweets; //igualamos la lista vieja a la actualizada

                //desactivamos por que ya hemos recibido esa lista de tweets
                swipeRefreshLayout.setRefreshing(false);

                //le decimos al adapter que hay cambios que debe mostrar
                adapter.setData(tweetList);

                //el problema es que si dejamos ambos observer pendientes de la lista de tweets del viewModel se van a disparar los dos
                //en caso de que se inserte un nuevo tweet. Lo que podemos hacer: una vez que actualicemos del servidor la nueva lista de tweets
                //con el swipe podemos desactivar este observer para que no se vuelva a lanzar cuando creamos un nuevo tweet
                //eliminamos el observer creado al principio del metodo y no se va a volver a lanzar a menos que lo volvamos a crear
                //usando el swipe
                tweetViewModel.getNewTweets().removeObserver(this);
            }
        });

    }


}
