package ru.veloportation.veloport.model.db;

import java.io.Serializable;

public class Courier  implements Serializable {
    public long id;
    public long idUser;
    public double latitude;
    public double longitude;
    public String login;
    public String pass;
    public int employment;

}
