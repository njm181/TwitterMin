package com.example.minitwitter.common;

import android.app.Application;
import android.content.Context;

//Con esta clase podemos gestionar el acceso al Contexto en ciertos puntos de la aplicacion
public class MyApp extends Application {

    private static MyApp instance;

    //singleton
    //obtener la instancia de MyApp Application
    public static MyApp getInstance(){
        return instance;
    }
    //obtener el Context de la aplicacion
    public static Context getContext(){
        return instance;
    }

    //Este metodo se crea una sola vez cuando se abre la aplicacion
    @Override
    public void onCreate() {
        instance = this; //se instancia con singleton
        super.onCreate();
    }
    //Para que MyApp este vinculado a la creacion de la aplicacion en el AndroidManifest se agrega debajo de application:
    //manifest android:name=".common.MyApp"
}
