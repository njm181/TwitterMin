package com.example.minitwitter.common;

//Cuando registramos y logueamos, recibimos una informacion que debemos almacenar, datos del usuario y el token.
//El token es un String unico que nos permite realizar las peteciones privadas, hau ciertas peticiones que se permiten hacer si solo tengo
//token, en la api se ve un candado, todas las peticiones que lo tengan necesitan del token

import android.content.Context;
import android.content.SharedPreferences;

//Existe una forma de guardar variables, como de sesion por ejemplo, incluso si cerramos la app, se almacenan en el dispositivo
//y que se pueden almacenar en cualquier sitio(activity, fragment, etc) y recuperarlas en cualquier otro sitio(activity, fragment, etc)
//SharedPreferenes --> Estos ficheros se guardan en ficheros que gestiona la aplicacion en el dispositivos, se puede usar un unico fichero o varios
//eso depende de como lo declaremos: https://developer.android.com/training/data-storage/shared-preferences
public class SharedPreferencesManager {


    private static final String APP_SETTINGS_FILE = "APP_SETTINS";

    private SharedPreferencesManager(){

    }

    //metodo que nos va a permitir obtener una referencia al SharedPreferences, osea al objeto que gestiona las preferencias
    private static SharedPreferences getSharedPreferences(){
        //name: nombre del fichero donde se van a almacenar las variables
       return MyApp.getContext()
               .getSharedPreferences(APP_SETTINGS_FILE, Context.MODE_PRIVATE);
    }

    //guardar preferencias
    public static void setSomeStringValue(String dataLabel, String dataValue){
        //dataLabel: variable que hace referencia al dato que quiero guardar
        //dataValue: el valor de ese dato, ya que se guardan siempre como CLAVE-VALOR
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(dataLabel, dataValue);
        //realizar escritura para la persistencia
        editor.commit();
    }

    //saber si el usuario esta activo o no
    public static void setSomeBooleanValue(String dataLabel, boolean dataValue){
        //dataLabel: variable que hace referencia al dato que quiero guardar
        //dataValue: el valor de ese dato, ya que se guardan siempre como CLAVE-VALOR
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putBoolean(dataLabel, dataValue);
        //realizar escritura para la persistencia
        editor.commit();
    }

    //metodos para obtener valores de tipo String o Boolean del fichero, donde nos pasan la CLAVE que tiene esa variable
    //y nos retorna el valor
    public static String getSomeStringValue(String dataLabel){
        return getSharedPreferences().getString(dataLabel, null);
    }

    public static boolean getSomeBooleanValue(String dataLabel){
        return getSharedPreferences().getBoolean(dataLabel, false);
    }


}
