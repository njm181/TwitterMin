
package com.example.minitwitter.retrofit.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RequestSignUp {

    /*
     * A traves de http://www.jsonschema2pojo.org/ pego el formato JSON que obtengo de la api con los datos que necesita
     * para registrarme, lo pego en la pagina, completo el form con los datos, y descargo el zip, luego lo incluyo en
     * el proyecto el fichero .java
     * Esta pagina realiza la autogeneracion de la clase POGO que necesito para construir los objetos java que necesito
     * con los datos que traigo de la API
     * */

    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("password")
    @Expose
    private String password;
    @SerializedName("code")
    @Expose
    private String code;

    /**
     * No args constructor for use in serialization
     * 
     */
    public RequestSignUp() {
    }

    /**
     * 
     * @param password
     * @param code
     * @param email
     * @param username
     */
    public RequestSignUp(String username, String email, String password, String code) {
        super();
        this.username = username;
        this.email = email;
        this.password = password;
        this.code = code;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

}
