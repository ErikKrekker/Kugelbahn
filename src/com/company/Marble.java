package com.company;

public class Marble {

    int rollOn;                 //Linie, auf der die Marble rollt

    double velX;                //Geschwindigkeit der Marble bzw. v0
    double velY;

    double velX_new;            //neue Geschwindigkeit (wird in der Iterration berchnet)
    double velY_new;

    double posX;                //Position der Marble bzw. s0
    double posY;

    double posX_new;            //neue Position (wird in der Itteration berechnet)
    double posY_new;

    double vProjektionX;        //Projektionsvektor der Marble auf die Gerade
    double vProjektionY;

    double weight;              //Gewicht

    boolean rollen = false;     //Ob die Marble am rollen ist
    boolean collision = true;   //Ob die Marble mit einer Linie kollidieren wird -> checkDirectionPointLine methode
    boolean movable;            //Ob die "Marble" sich bewegen kann
    boolean magnet;             //Ob die Marble magnetisch ist

    public Marble(double posX, double posY, double velX, double velY, double weight, boolean movable, boolean magnet){
        this.velX = velX;
        this.velY = velY;
        this.posX = posX;
        this.posY = posY;
        this.weight = weight;
        this.movable = movable;
        this.magnet = magnet;
    }

    public int getRollOn() {
        return rollOn;
    }

    public double getVelX(){
        return velX;
    }

    public double getVelY(){
        return velY;
    }

    public double getVelX_new() {
        return velX_new;
    }

    public double getVelY_new() {
        return velY_new;
    }

    public double getPosX(){ return posX; }

    public double getPosY(){
        return posY;
    }

    public double getPosX_new() {
        return posX_new;
    }

    public double getPosY_new() {
        return posY_new;
    }

    public double getvProjektionX() { return vProjektionX; }

    public double getvProjektionY() { return vProjektionY; }

    public double getWeight() { return weight; }

    public boolean isRollen(){ return  rollen; }

    public boolean isCollision(){ return  collision; }

    public boolean isMovable() { return movable; }

    public boolean isMagnet() { return  magnet; }



    public void setRollOn(int rollOn) {
        this.rollOn = rollOn;
    }

    public void setVelX(double velX) {
        this.velX = velX;
    }

    public void setVelY(double velY) {
        this.velY = velY;
    }

    public void setVelX_new(double velX_new) {
        this.velX_new = velX_new;
    }

    public void setVelY_new(double velY_new) {
        this.velY_new = velY_new;
    }

    public void setPosX(double posX) {
        this.posX = posX;
    }

    public void setPosY(double posY) { this.posY = posY; }

    public void setPosX_new(double posX_new) {
        this.posX_new = posX_new;
    }

    public void setPosY_new(double posY_new) {
        this.posY_new = posY_new;
    }

    public void setvProjektionX(double vProjektionX){ this.vProjektionX = vProjektionX; }

    public void setvProjektionY(double vProjektionY){ this.vProjektionY = vProjektionY; }

    public void setRollen(boolean rollen) {
        this.rollen = rollen;
    }

    public void setCollision(boolean collision) {
        this.collision = collision;
    }

    public void setMovable(boolean movable){ this.movable = movable; }


    public void setWeight(double weight) {
        this.weight = weight;
    }

}

