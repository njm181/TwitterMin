package com.example.minitwitter.ui;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.minitwitter.R;
import com.example.minitwitter.common.Constantes;
import com.example.minitwitter.common.SharedPreferencesManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class DashboardActivity extends AppCompatActivity {

    private FloatingActionButton fab;
    private ImageView ivAvatar;


    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            Fragment selectedFragment = null;

            switch (menuItem.getItemId()){
                case R.id.navigation_home:
                    selectedFragment = TweetListFragment.newInstance(Constantes.TWEET_LIST_ALL);
                    System.out.println("Estas en HOME");
                    fab.show();
                    break;
                case R.id.navigation_tweets_like:
                    selectedFragment = TweetListFragment.newInstance(Constantes.TWEET_LIST_FAVS);
                    System.out.println("Estas en FAVORITOS");
                    fab.hide();
                    break;
                case R.id.navigation_profile:
                    //
                    fab.hide();
                    break;
            }
            if(selectedFragment != null){
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainer, selectedFragment)
                        .commit();
                return true; //aplicar cambio
            }

            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        fab = findViewById(R.id.fab);
        ivAvatar = findViewById(R.id.imageViewToolbarPhoto);

        getSupportActionBar().hide();


        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(navListener);;

        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragmentContainer, TweetListFragment.newInstance(Constantes.TWEET_LIST_ALL))
                .commit();


        //vista principal
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragmentContainer, TweetListFragment.newInstance(Constantes.TWEET_LIST_ALL))//cuando queramos instanciar el fragmento
                //y le decimos que queremos toda la lista de tweets. TWEET_LIST_ALL=1
                .commit();


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NuevoTweetDialogFragment dialog = new NuevoTweetDialogFragment();
                //mostrar el dialogFragment, donde paso el gestor de fragmentos y una etiqueta para identifcar mi fragmento
                dialog.show(getSupportFragmentManager(), "NuevoTweetDialogFragment");
            }
        });


        //Seteamos la imagen del usuario de perfil
        String photoUrl = SharedPreferencesManager.getSomeStringValue(Constantes.PREF_PHOTOURL);
        if(!photoUrl.isEmpty()){
            //si la url no esta vacia le cargamos la foto al ivAvatar
            Glide.with(this).load(Constantes.API_MINITWITTER_FILES_URL + photoUrl).into(ivAvatar);
        }

    }


}
