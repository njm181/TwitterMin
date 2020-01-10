
package com.example.minitwitter.retrofit.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RequestLogin {

    /*
    * A traves de http://www.jsonschema2pojo.org/ pego el formato JSON que obtengo de la api con los datos que necesita
    * para iniciar sesion, lo pego en la pagina, completo el form con los datos, y descargo el zip, luego lo incluyo en
    * el proyecto el fichero .java
    * Esta pagina realiza la autogeneracion de la clase POGO que necesito para construir los objetos java que necesito
    * con los datos que traigo de la API
    * */

    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("password")
    @Expose
    private String password;

    /**
     * No args constructor for use in serialization
     * 
     */
    public RequestLogin() {
    }

    /**
     * 
     * @param password
     * @param email
     */
    public RequestLogin(String email, String password) {
        super();
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
