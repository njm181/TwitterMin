package com.example.minitwitter.ui.tweets;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.minitwitter.R;
import com.example.minitwitter.common.Constantes;
import com.example.minitwitter.common.SharedPreferencesManager;
import com.example.minitwitter.data.TweetViewModel;
import com.example.minitwitter.retrofit.response.Like;
import com.example.minitwitter.retrofit.response.Tweet;

import java.util.List;

public class TweetRecyclerViewAdapter extends RecyclerView.Adapter<TweetRecyclerViewAdapter.ViewHolder> {

    private Context ctx;
    private List<Tweet> listaDeTweets;
    private String username; //del usuario que esta logueado
    private TweetViewModel tweetViewModel; //para usar los metodos del mismo


    public TweetRecyclerViewAdapter(Context contexto, List<Tweet> items) {
        listaDeTweets = items;
        ctx = contexto;
        //instancio al username del usuario logueado en la variable
        username = SharedPreferencesManager.getSomeStringValue(Constantes.PREF_USERNAME);
        //Cada vez que instanciamos un view model debemos usar ViewModelProviders, le pasamos el contexto y la clase del viewModel a usar
        tweetViewModel = ViewModelProviders.of((FragmentActivity) ctx).get(TweetViewModel.class);
    }

    //cargar layout donde vamos a generar un elemento de la lista
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_tweet, parent, false);
        return new ViewHolder(view);
    }

    //ir dibujando cada elemento en el RecyclerView
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        //si el elemento que nos marca, es distinto de null genera todo, sino, no hace nada
        if (listaDeTweets != null){

            holder.tweet = listaDeTweets.get(position);

        //seteo la info en los componentes
        holder.tvUsername.setText("@"+holder.tweet.getUser().getUsername());
        holder.tvMensaje.setText(holder.tweet.getMensaje());
        holder.tvLikesCount.setText(String.valueOf(holder.tweet.getLikes().size()));


        String photo = holder.tweet.getUser().getPhotoUrl();

        //si el usuario tiene foto cargada, la insertamos en el imageviewAvatar
        if (!photo.equals("")) {
            //cargamos la foto por defecto
            Glide.with(ctx)
                    .load("https://www.minitwitter.com/apiv1/uploads/photos/" + photo)
                    .into(holder.ivAvatar);
        }
            //Con esto conseguimos que si hacemos un Scroll rapido del recycler el adapter resetea el estilo de todos los
            //elementos que pinta, y solo va a pintar aquellos que tengan like
            Glide.with(ctx).load(R.drawable.ic_like).into(holder.ivLike);
            holder.tvLikesCount.setTextColor(ctx.getResources().getColor(android.R.color.black));
            holder.tvLikesCount.setTypeface(null, Typeface.NORMAL);


            //por defecto viene oculto
            holder.ivShowMenu.setVisibility(View.GONE);

            //solo lo mostraremos en caso del que el tweet que estamos recorriendo en ese momento el usuario que lo creo tenga el mismo
            //username que el usuario logueados. Solo ese usuario podra eliminarlo
            //hay que saber en que momento mostrar el ivshowMenu
            if(holder.tweet.getUser().getUsername().equals(username)){
                holder.ivShowMenu.setVisibility(View.VISIBLE);
            }


            //gestionar elemento visual showMenu, en la vista de fragment_tweet es la flchea gris del lado izq
            holder.ivShowMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tweetViewModel.openDialogTweetMenu(ctx, holder.tweet.getId());
                }
            });

            //Cuando se hace click sobre el Like, para marcar como favorito
            holder.ivLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //invocar al viewModel y le paso el identificador del elemento actual que se hizo click
                    tweetViewModel.likeTweet(holder.tweet.getId());
                }
            });





        //Otra cosa que debemos hacer es recorrer toda la lista de likes para saber si nosotros estamos en esa lista
        //y en ese caso pintar el corazon y el texto de numeros de likes
        for (Like like : holder.tweet.getLikes()) {
            if (like.getUsername().equals(username)) { //si el username del like es igual al usuario logueado entonces aplico los efectos
                Glide.with(ctx).load(R.drawable.ic_like_pink).into(holder.ivLike);
                holder.tvLikesCount.setTextColor(ctx.getResources().getColor(R.color.pink));
                holder.tvLikesCount.setTypeface(null, Typeface.BOLD);
                break;
            }
         }
        }

    }

    //actualzar el adapter con las modificaciones
    public void setData(List<Tweet> tweetList){
        this.listaDeTweets = tweetList;
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        //si la lista no esta vacia entonces devuelvo la cantidad de elementos
        if(listaDeTweets != null)
            return listaDeTweets.size();
        else return 0; //si esta vacia entonces devuelvo 0, esto es para evitar un error
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;//constraintlayout que posee todos los elementos de abajo
        public final ImageView ivAvatar;
        public final ImageView ivLike;
        public final TextView tvUsername;
        public final TextView tvMensaje;
        public final TextView tvLikesCount;

        public final ImageView ivShowMenu;

        public Tweet tweet;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            ivAvatar = view.findViewById(R.id.imageViewAvatar);
            ivLike = view.findViewById(R.id.imageViewLike);
            tvUsername = view.findViewById(R.id.textViewUsername);
            tvMensaje = view.findViewById(R.id.textViewMensaje);
            tvLikesCount = view.findViewById(R.id.textViewLikes);

            ivShowMenu = view.findViewById(R.id.imageViewShowMenu);


        }

        @Override
        public String toString() {
            return super.toString() + " '" + tvUsername.getText() + "'";
        }
    }
}
