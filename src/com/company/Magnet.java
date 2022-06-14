package com.company;

public class Magnet {

    double posX;
    double posY;
    double weight;
    double length;

    double velx = 0;
    double vely = 0;

    public Magnet(double posX, double posY, double weight, double length){
        this.posX = posX;
        this.posY = posY;
        this.weight = weight;
        this.length = length;
    }

    public double getPosX() {
        return posX;
    }

    public double getPosY() {
        return posY;
    }

    public double getWeight() {
        return weight;
    }

    public  double getLength() {
        return  length;
    }

    public double getVelx() {
        return velx;
    }

    public double getVely() {
        return vely;
    }


    public void setVelx(double velx) {
        this.velx = velx;
    }

    public void setVely(double vely) {
        this.vely = vely;
    }

    public void setPosX(double posX) {
        this.posX = posX;
    }

    public void setPosY(double posY) {
        this.posY = posY;
    }
}
