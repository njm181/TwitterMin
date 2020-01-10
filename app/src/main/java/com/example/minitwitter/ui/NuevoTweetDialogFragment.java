package com.example.minitwitter.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.example.minitwitter.R;
import com.example.minitwitter.common.Constantes;
import com.example.minitwitter.common.SharedPreferencesManager;
import com.example.minitwitter.data.TweetViewModel;

public class NuevoTweetDialogFragment extends DialogFragment implements View.OnClickListener {

    private ImageView ivClose, ivAvatar;
    private Button btnTwittear;
    private EditText etMensaje;

    //este metodo es para cargar el estilo a pantalla completa del view del dialogo de  abajo
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //aplica el style personalizado en el documento style.xml al cuadro de dialogo
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle);

    }

    //Este metodo nos va a permitir cargar una vista personalizada de nuestro fragmento de dialogo, que sera el formulario para crear un tweet
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        //view es el que vamos a cargar con nuestro formulario
        //view = inflater.inflate: creamos una vista que va a ser introducida dentro del dialogo, para ello invocamos el layout
        //el segundo parametro es para decir que sea introducido dentro del WiewGroup container que recibimos como parametro
        //false: indica que este va a ser el layout que se cargue en la pantalla, que no pasa por otro como el recyclerview por ejemplo
        View view = inflater.inflate(R.layout.nuevo_tweet_full_dialog, container, false);

        //cargo los componentes visuales del nuevo_tweet_full_dialog.xml
        ivClose = view.findViewById(R.id.imageViewClose);
        ivAvatar = view.findViewById(R.id.imageViewAvatar);
        btnTwittear = view.findViewById(R.id.btnTwittear);
        etMensaje = view.findViewById(R.id.editTextMensaje);

        //eventos para el boton de twittear y cerrar
        btnTwittear.setOnClickListener(this);
        ivClose.setOnClickListener(this);


        //Seteamos la imagen del usuario de perfil
        String photoUrl = SharedPreferencesManager.getSomeStringValue(Constantes.PREF_PHOTOURL);

        if(!photoUrl.isEmpty()){
            //si la url no esta vacia le cargamos la foto al ivAvatar
            Glide.with(getActivity()).load(Constantes.API_MINITWITTER_FILES_URL + photoUrl).into(ivAvatar);
        }



        //una vez creada la vista la devolvemos para que se muetre en el dispositivo
        return view;
    }

    @Override
    public void onClick(View v) {
        //recibe evento click de dos elementos
        int id = v.getId();
        String mensaje = etMensaje.getText().toString();

        if(id == R.id.btnTwittear){

            if(mensaje.isEmpty()){
                Toast.makeText(getActivity(), "Debe escribir un texto en el mensaje", Toast.LENGTH_SHORT).show();
            }else {

                //insertamos el tweet, llamando al objeto que puede hacerlo
                TweetViewModel tweetViewModel = ViewModelProviders.of(getActivity()).get(TweetViewModel.class);

                //insercion
                tweetViewModel.insertTweet(mensaje);

                //cerrar dialogo donde se inserta el tweet
                getDialog().dismiss();
            }

        }else if (id == R.id.imageViewClose){
            if(!mensaje.isEmpty()){
                showDialogConfirm();
            }else{
                getDialog().dismiss();
            }

        }
    }

    private void showDialogConfirm() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // 2. Chain together various setter methods to set the dialog characteristics
        builder.setMessage("Desea realmente eliminar el Tweet? El mensaje se borrara")
                .setTitle("Cancelar Tweet");

        // Add the buttons
        builder.setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // si aceptar eliminar el tweet cerramos el cuadro de dialog que preguntaba y el cuadro de dialogo donde estaba twiteando
                dialog.dismiss();
                getDialog().dismiss();
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // si quiere seguir escribiendo, solo cerramos el cuadro de dialogo donde preguntaba que queria hacer
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();

        dialog.show(); //mostrar el cuadro de dialogo que pregunta si queremos o no eliminar el tweet

    }
}
