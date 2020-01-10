package com.example.minitwitter.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.minitwitter.R;
import com.example.minitwitter.common.Constantes;
import com.example.minitwitter.common.SharedPreferencesManager;
import com.example.minitwitter.retrofit.MiniTwitterApiService;
import com.example.minitwitter.retrofit.MiniTwitterClient;
import com.example.minitwitter.retrofit.request.RequestSignUp;
import com.example.minitwitter.retrofit.response.ResponseAuth;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnSignUp;
    TextView tvLogin;
    EditText etUsername, etEmail, etPassword;
    MiniTwitterClient miniTwitterClient;//para definir el acceso a los servicios de la api
    MiniTwitterApiService miniTwitterApiService;//para definir el acceso a los servicios de la api

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //Ocultar toolbar
        getSupportActionBar().hide();

        retrofitInt();

        findViews();
        findEvents();


    }

    private void retrofitInt() {
        miniTwitterClient = MiniTwitterClient.getInstance();
        miniTwitterApiService = miniTwitterClient.getMiniTwitterApiService();
    }

    private void findViews() {
        btnSignUp = findViewById(R.id.btnSignUp);
        tvLogin = findViewById(R.id.textViewGoSignUp);
        etUsername = findViewById(R.id.editTextUsername);
        etEmail = findViewById(R.id.editTextEmail);
        etPassword = findViewById(R.id.editTextPassword);
    }
    private void findEvents() {
        btnSignUp.setOnClickListener(this);
        tvLogin.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        //A este metodo llegan los clicks realizados en este Activity, que luego filtramos por id para saber a que elemento se clikeo
        int id = v.getId();

        switch (id){
            case R.id.btnSignUp:
                goToSignUp();
                break;
            case R.id.textViewGoSignUp:
                backToLogin();
                break;
        }

    }

    private void goToSignUp() {
        //comprobacion de que esta todo correcto
        String username = etUsername.getText().toString();
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();

        if(username.isEmpty()){
            etUsername.setError("Usuario requerido");
        }else if(email.isEmpty()){
            etEmail.setError("Email es requerido");
        }else if(password.isEmpty() || password.length()<4){
            etPassword.setError("Password es requerido y debe tener al menos 4 caracteres");
        }else{
            //si cumple con lo anterior hacemos la llamada a retrofit
            String code = "UDEMYANDROID";//codigo para poder tener acceso a la api
            RequestSignUp requestSignUp = new RequestSignUp(username, email, password, code);
            Call<ResponseAuth> call = miniTwitterApiService.doSignUp(requestSignUp);//paso la request y devuelve objeto Call de tipo ResponseAuth que atrapo en call
            //llamada asincrona al servicio
            call.enqueue(new Callback<ResponseAuth>() {
                @Override
                public void onResponse(Call<ResponseAuth> call, Response<ResponseAuth> response) {
                    if(response.isSuccessful()){
                        Toast.makeText(SignUpActivity.this, "Registro exitoso, acabas de iniciar sesion", Toast.LENGTH_SHORT).show();
                        //Si sale Ok la peticion podemos pasar al DashboardActivity con la sesion iniciada, ya que seria
                        //Registro + Login, recibimos el token para iniciar sesion sin pasar por el Login

                        //Invocamos al SharedPreferences para obtener el token de la respuesta de la api y lo guardamos en el fichero para persistir
                        //y utilizar en futuras peticiones
                        SharedPreferencesManager.setSomeStringValue(Constantes.PREF_TOKEN, response.body().getToken());
                        SharedPreferencesManager.setSomeStringValue(Constantes.PREF_USERNAME, response.body().getUsername());
                        SharedPreferencesManager.setSomeStringValue(Constantes.PREF_EMAIL, response.body().getEmail());
                        SharedPreferencesManager.setSomeStringValue(Constantes.PREF_PHOTOURL, response.body().getPhotoUrl());
                        SharedPreferencesManager.setSomeStringValue(Constantes.PREF_CREATED, response.body().getCreated());
                        SharedPreferencesManager.setSomeBooleanValue(Constantes.PREF_ACTIVE, response.body().getActive());
                        //guardamos todas las propiedades que recibimos al loguear en preferencias

                        Intent intent = new Intent(SignUpActivity.this, DashboardActivity.class);
                        startActivity(intent);
                        finish();
                    }else{
                        Toast.makeText(SignUpActivity.this, "Algo salio mal, revise los datos de registro", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseAuth> call, Throwable t) {
                    Toast.makeText(SignUpActivity.this, "Error en la conexion, intentelo de nuevo", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void backToLogin() {
        //ir a la pantalla inicial--> MainActivity
        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}
