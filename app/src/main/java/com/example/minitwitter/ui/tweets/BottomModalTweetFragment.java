package com.example.minitwitter.ui.tweets;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;

import com.example.minitwitter.R;
import com.example.minitwitter.common.Constantes;
import com.example.minitwitter.data.TweetViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.navigation.NavigationView;

//fragmento de tipo dialogo, es un menu inferior que permite hacer acciones. Es como un BottomSheet
public class BottomModalTweetFragment extends BottomSheetDialogFragment {

    private TweetViewModel tweetViewModel;
    private int idTweetEliminar;

    public static BottomModalTweetFragment newInstance(int idTweet) {//el que invoque este metodo debe pasar el id del tweet

        BottomModalTweetFragment fragment = new BottomModalTweetFragment();
        Bundle args = new Bundle();//para pasar argumentos al fragmento de arriba
        args.putInt(Constantes.ARG_TWEET_ID, idTweet);//argumento de tipo int el que paso
        //ARG_TWEET_ID: es una referencia string a la variable idTweet que paso por fragmento
        fragment.setArguments(args);//le seteo los argumentos al fragmento para poder usar el idTweet con el valor con el que viene, sino da 0
        return fragment;
    }

    //para rescartar el parametro args.putInt(Constantes.ARG_TWEET_ID, idTweet)
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //el metodo onCreate permite rescatar mediante el getArguments() los parametros que hayan podido llegar al fragmento
        if(getArguments() != null){
            idTweetEliminar = getArguments().getInt(Constantes.ARG_TWEET_ID); //ARG_TWEET_ID: clave o etiqueta que recupera el valor que se le asigno en el constructor
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        //vista que retorna
        View view = inflater.inflate(R.layout.bottom_modal_tweet_fragment, container, false);

        //gestionamos los evento click, primero capturo el elemento de navagacion que tendra los elementos a clickear
        final NavigationView nav = view.findViewById(R.id.navigation_view_bottom_tweet);
        nav.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {//para gestionar los eventos click sobre este menu de navagacion
                int id = menuItem.getItemId();

                if(id == R.id.action_delete_tweet){
                    //si es igual, invocamos al viewModel para que elimine el tweet
                    tweetViewModel.deleteTweet(idTweetEliminar);//el id del tweet para eliminar lo obtengo del newInstance()
                    getDialog().dismiss();//getDialog(): es para obtener referencia al cuadro de dialogo que se abrio
                    return true;
                }
                return false;
            }
        });


        return view;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        tweetViewModel = ViewModelProviders.of(getActivity()).get(TweetViewModel.class);
    }

}
