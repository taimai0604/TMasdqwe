package com.tm.environmenttm.model;

/**
 * Created by MY on 6/22/2017.
 */
public class AddressGeo {
    private double la;
    private double ln;
    private String address;
    public AddressGeo(double la, double ln, String address){
        this.la =la;
        this.ln=ln;
        this.address=address;
    }

    public AddressGeo() {
        
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setLa(double la) {
        this.la = la;
    }

    public void setLn(double ln) {
        this.ln = ln;
    }

    public double getLa() {
        return la;
    }

    public double getLn() {
        return ln;
    }

    public String getAddress() {
        return address;
    }
}
