
package com.example.minitwitter.retrofit.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
//cuerpo de la peticion que vamos a enviar al server cuando creamos un tweet, y debemos tener en Response el tipo de objeto de respuesta a la request
public class RequestCreateTweet {

    @SerializedName("mensaje")
    @Expose
    private String mensaje;

    /**
     * No args constructor for use in serialization
     * 
     */
    public RequestCreateTweet() {
    }

    /**
     * 
     * @param mensaje
     */
    public RequestCreateTweet(String mensaje) {
        super();
        this.mensaje = mensaje;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

}
