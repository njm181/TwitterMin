package com.example.minitwitter.ui.profile;

import android.Manifest;
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
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.minitwitter.R;
import com.example.minitwitter.common.Constantes;
import com.example.minitwitter.data.ProfileViewModel;
import com.example.minitwitter.retrofit.request.RequestUserProfile;
import com.example.minitwitter.retrofit.response.ResponseUserProfile;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.listener.single.CompositePermissionListener;
import com.karumi.dexter.listener.single.DialogOnDeniedPermissionListener;
import com.karumi.dexter.listener.single.PermissionListener;

public class ProfileFragment extends Fragment {

    private ProfileViewModel profileViewModel;
    private ImageView ivAvatar;
    private EditText etUsername, etEmail, etPassword, etWebsite, etDescripcion;
    private Button btnGuardar, btnChangePassword;
    boolean loadingData = true;

    private PermissionListener allPermissionListener;

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        profileViewModel = ViewModelProviders.of(getActivity()).get(ProfileViewModel.class);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile_fragment, container, false);

        //obtengo las referencias a los componentes visuales de profile_fragment
        ivAvatar = view.findViewById(R.id.imageViewAvatar);
        etUsername = view.findViewById(R.id.editTextUsername);
        etEmail = view.findViewById(R.id.editTextEmail);
        etPassword = view.findViewById(R.id.editTextPassword);
        etWebsite = view.findViewById(R.id.editTextWebsite);
        etDescripcion = view.findViewById(R.id.editTextDescripcion);
        btnGuardar = view.findViewById(R.id.buttonSave);
        btnChangePassword = view.findViewById(R.id.buttonChangePassword);

        //eventos. Expresion lambda
        btnGuardar.setOnClickListener(v -> {
            String username = etUsername.getText().toString();
            String email = etEmail.getText().toString();
            String descripcion = etDescripcion.getText().toString();
            String website = etWebsite.getText().toString();

            if(username.isEmpty()){
                etUsername.setError("El nombre de usuario es requerido");
            }else if(email.isEmpty()){
                etEmail.setError("El email es requerido");
            }else{
                RequestUserProfile requestUserProfile = new RequestUserProfile(username, email, descripcion, website);
                profileViewModel.updateProfile(requestUserProfile);
                Toast.makeText(getActivity(), "Enviando informacion al servidor", Toast.LENGTH_SHORT).show();
                btnGuardar.setEnabled(false);
            }
        });

        btnChangePassword.setOnClickListener(v -> {
            Toast.makeText(getActivity(), "Click on save", Toast.LENGTH_SHORT).show();
        });


        ivAvatar.setOnClickListener(v -> {
            //invocar a la seleccion de la foto
            //invocar al metodo de comprobacion de permisos
            checkPermissions();
        });



        //viewModel
        profileViewModel.userProfile.observe(getActivity(), new Observer<ResponseUserProfile>() {
            @Override
            public void onChanged(ResponseUserProfile responseUserProfile) {
                loadingData = false;
                etUsername.setText(responseUserProfile.getUsername());
                etEmail.setText(responseUserProfile.getEmail());
                etWebsite.setText(responseUserProfile.getWebsite());
                etDescripcion.setText(responseUserProfile.getDescripcion());

                if(!responseUserProfile.getPhotoUrl().isEmpty()){
                    Glide.with(getActivity())
                            .load(Constantes.API_MINITWITTER_FILES_URL + responseUserProfile.getPhotoUrl())
                            .dontAnimate()//no recomendada con circleimageview
                            .diskCacheStrategy(DiskCacheStrategy.NONE)//no se utilice la memoria cache
                            .centerCrop()//imagen centrada en el circleimageview
                            .skipMemoryCache(true)
                            .into(ivAvatar);
                }
                if(!loadingData){
                    btnGuardar.setEnabled(true);
                    Toast.makeText(getActivity(), "Datos guardados correctament", Toast.LENGTH_SHORT).show();
                }
            }
        });


        return view;

    }
    //gestionar los permisos
    private void checkPermissions() {
        //cuadro de dialogo que muestre la info que el usuario sepa que dimos una serie de permisos
        PermissionListener dialogOnDeniedPermissionListener = DialogOnDeniedPermissionListener.Builder.withContext(getActivity())
                .withTitle("Permisos")
                .withMessage("Los permisos son necesarios para poder seleccionar una foto de perfil")
                .withButtonText("Aceptar")
                .withIcon(R.mipmap.ic_launcher)
                .build();

        allPermissionListener = new CompositePermissionListener((PermissionListener) getActivity(), dialogOnDeniedPermissionListener);

        Dexter.withActivity(getActivity())
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE) //que permisos necesito
                .withListener(allPermissionListener)
                .check();
    }




}
