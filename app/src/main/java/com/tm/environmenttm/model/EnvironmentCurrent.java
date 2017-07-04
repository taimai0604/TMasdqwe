package com.tm.environmenttm.model;

import java.io.Serializable;

/**
 * Created by taima on 06/27/2017.
 */

public class EnvironmentCurrent implements Serializable {
    private int pa;//ap suat
    private int height;//do cao
    private int la;// do sang analog
    private int ld;// do sang diagital
    private int h;//do am
    private int t;//nhiet do C
    private int f;//nhiet do F
    private int k;//nhieu do K
    private int hi;//heat index
    private int dp;//diem suong

    public EnvironmentCurrent(){

    }

    public EnvironmentCurrent(int pa, int height, int la, int ld, int h, int t, int f, int k, int hi, int dp) {
        this.pa = pa;
        this.height = height;
        this.la = la;
        this.ld = ld;
        this.h = h;
        this.t = t;
        this.f = f;
        this.k = k;
        this.hi = hi;
        this.dp = dp;
    }

    public int getPa() {
        return pa;
    }

    public void setPa(int pa) {
        this.pa = pa;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getLa() {
        return la;
    }

    public void setLa(int la) {
        this.la = la;
    }

    public int getLd() {
        return ld;
    }

    public void setLd(int ld) {
        this.ld = ld;
    }

    public int getH() {
        return h;
    }

    public void setH(int h) {
        this.h = h;
    }

    public int getT() {
        return t;
    }

    public void setT(int t) {
        this.t = t;
    }

    public int getF() {
        return f;
    }

    public void setF(int f) {
        this.f = f;
    }

    public int getK() {
        return k;
    }

    public void setK(int k) {
        this.k = k;
    }

    public int getHi() {
        return hi;
    }

    public void setHi(int hi) {
        this.hi = hi;
    }

    public int getDp() {
        return dp;
    }

    public void setDp(int dp) {
        this.dp = dp;
    }
}
