package com.example.minitwitter.ui.tweets;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.minitwitter.R;
import com.example.minitwitter.common.Constantes;
import com.example.minitwitter.common.SharedPreferencesManager;
import com.example.minitwitter.ui.profile.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

public class DashboardActivity extends AppCompatActivity implements PermissionListener {

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
                    selectedFragment = new ProfileFragment();
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
            Glide.with(this).load(Constantes.API_MINITWITTER_FILES_URL + photoUrl)
                    .dontAnimate()//no recomendada con circleimageview
                    .diskCacheStrategy(DiskCacheStrategy.NONE)//no se utilice la memoria cache
                    .centerCrop()//imagen centrada en el circleimageview
                    .skipMemoryCache(true)
                    .into(ivAvatar);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode != RESULT_CANCELED){
            if(requestCode == Constantes.SELECT_PHOTO_GALLERY){
                if(data != null){
                    Uri imagenSelecciomada = data.getData(); //data trae la info de la imagen seleccionada

                    //ruta de la imagen
                }
            }
        }
    }

    //cuando el usuario acepta los permisos
    @Override
    public void onPermissionGranted(PermissionGrantedResponse response) {
        //invoco la seleccion de fotos de la galeria
        Intent seleccionarFoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);//llama a una accion, creamos peticion para abrir galeria
        //lanzar la peticion y espera un resultado, la foto que se selecciono, si la info que viene por respuesta es igual a la constante, quiere decir que es la respuesta a esta peticion
        startActivityForResult(seleccionarFoto, Constantes.SELECT_PHOTO_GALLERY);


    }
    //cuando el usuario los deniega
    @Override
    public void onPermissionDenied(PermissionDeniedResponse response) {
        Toast.makeText(this, "No se puede seleccionar la foto", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

    }
}
