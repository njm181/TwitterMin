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
import com.example.minitwitter.retrofit.request.RequestLogin;
import com.example.minitwitter.retrofit.response.ResponseAuth;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnLogin;
    TextView textViewGoSignUp;
    EditText etEmail, etPassword;
    MiniTwitterClient miniTwitterClient;//para definir el acceso a los servicios de la api
    MiniTwitterApiService miniTwitterApiService;//para definir el acceso a los servicios de la api

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Ocultar toolbar
        getSupportActionBar().hide();

        //Para la conexion a la api y usar los servicios
        retrofitInit();

        findViews();
        events();


    }

    private void retrofitInit() {
        miniTwitterClient = MiniTwitterClient.getInstance();//Para instanciar, y si ya estaba instanciada la devuelve
        miniTwitterApiService = miniTwitterClient.getMiniTwitterApiService();//devuelve una instancia del servicio
    }

    //Asocio los componentes a las variables
    private void findViews() {
        btnLogin = findViewById(R.id.btnSignUp);
        textViewGoSignUp = findViewById(R.id.textViewGoSignUp);
        etEmail = findViewById(R.id.editTextEmail);
        etPassword = findViewById(R.id.editTextPassword);
    }
    //Eventos de los componentes
    private void events() {
        btnLogin.setOnClickListener(this);
        textViewGoSignUp.setOnClickListener(this);
    }



    @Override
    public void onClick(View v) {
        //A este metodo llegan los clicks realizados en este Activity, que luego filtramos por id para saber a que elemento se clikeo
        int id = v.getId();
        
        switch (id){
            case R.id.btnSignUp:
                goToLogin();
                break;
            case R.id.textViewGoSignUp:
                goToSignUp();
                break;
        }

    }

    private void goToLogin() {
        final String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();

        if(email.isEmpty()){
            etEmail.setError("El email es requerido");
        }else if(password.isEmpty()){
            etPassword.setError("La password es requerida");
        }else{
            //creamos objeto de la peticion para enviar
            RequestLogin requestLogin = new RequestLogin(email, password);

            //la llamada
            Call<ResponseAuth> call = miniTwitterApiService.doLogin(requestLogin);
            //y sobre la llamada hacemos la peticion asincrona, y dentro creamos una clase anonima que es una clase Callback
            //de tipo ResponseAuth
            call.enqueue(new Callback<ResponseAuth>() {
                @Override
                public void onResponse(Call<ResponseAuth> call, Response<ResponseAuth> response) {
                    //respuesta en caso de que no haya problema de comunicacion con el servidor
                    //Si queremos verificar que la respuesta sea de tipo 200, osea OK
                    if(response.isSuccessful()){
                        Toast.makeText(MainActivity.this, "Sesion iniciada correctamente", Toast.LENGTH_LONG).show();

                        //Invocamos al SharedPreferences para obtener el token de la respuesta de la api y lo guardamos en el fichero para persistir
                        //y utilizar en futuras peticiones
                        SharedPreferencesManager.setSomeStringValue(Constantes.PREF_TOKEN, response.body().getToken());
                        SharedPreferencesManager.setSomeStringValue(Constantes.PREF_USERNAME, response.body().getUsername());
                        SharedPreferencesManager.setSomeStringValue(Constantes.PREF_EMAIL, response.body().getEmail());
                        SharedPreferencesManager.setSomeStringValue(Constantes.PREF_PHOTOURL, response.body().getPhotoUrl());
                        SharedPreferencesManager.setSomeStringValue(Constantes.PREF_CREATED, response.body().getCreated());
                        SharedPreferencesManager.setSomeBooleanValue(Constantes.PREF_ACTIVE, response.body().getActive());
                        //guardamos todas las propiedades que recibimos al loguear en preferencias


                        //pasar al dashboard o menu del usuario
                        Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
                        startActivity(intent);
                        //no permitir volver al MainActivity
                        //Destruimos el MainActivity
                        finish();
                    }else{
                        //tendriamos error
                        Toast.makeText(MainActivity.this, "Algo salio mal, revise sus datos de accesos", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseAuth> call, Throwable t) {
                    //en caso de que falle la comunicacion con el servidor
                    Toast.makeText(MainActivity.this, "Problemas de conexion, intentelo de nuevo", Toast.LENGTH_LONG).show();

                }
            });

        }

    }

    private void goToSignUp() {
        //ir a la pantalla de registro --> SignUpActivity
        Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
        startActivity(intent);
        finish();
    }
}
