package com.company;

public class Kugel {

    double velX;
    double velY;

    double velX_new;
    double velY_new;

    double posX;
    double posY;

    double posX_new;
    double posY_new;

    boolean rollen = false;
    boolean collision = true;

    int rollOn;

    double vProjektionX;
    double vProjektionY;

    double weight;

    public Kugel(double posX, double posY, double velX, double velY, double weight){
        this.velX = velX;
        this.velY = velY;
        this.posX = posX;
        this.posY = posY;
        this.weight = weight;
    }

    public double getVelX(){
        return velX;
    }

    public double getVelY(){
        return velY;
    }

    public double getPosX(){
        return posX;
    }

    public double getPosY(){
        return posY;
    }

    public boolean isRollen(){ return  rollen; }

    public boolean isCollision(){ return  collision; }

    public double getVelX_new() {
        return velX_new;
    }

    public double getVelY_new() {
        return velY_new;
    }

    public double getPosX_new() {
        return posX_new;
    }

    public double getPosY_new() {
        return posY_new;
    }

    public int getRollOn() {
        return rollOn;
    }

    public double getvProjektionX() { return vProjektionX; }

    public double getvProjektionY() { return vProjektionY; }

    public double getWeight() { return weight; }




    public void setVelX(double velX) {
        this.velX = velX;
    }

    public void setVelY(double velY) {
        this.velY = velY;
    }

    public void setPosX(double posX) {
        this.posX = posX;
    }

    public void setPosY(double posY) {
        this.posY = posY;
    }

    public void setRollen(boolean rollen) {
        this.rollen = rollen;
    }

    public void setCollision(boolean collision) {
        this.collision = collision;
    }

    public void setPosX_new(double posX_new) {
        this.posX_new = posX_new;
    }

    public void setPosY_new(double posY_new) {
        this.posY_new = posY_new;
    }

    public void setVelX_new(double velX_new) {
        this.velX_new = velX_new;
    }

    public void setVelY_new(double velY_new) {
        this.velY_new = velY_new;
    }

    public void setRollOn(int rollOn) {
        this.rollOn = rollOn;
    }

    public void setvProjektionX(double vProjektionX){ this.vProjektionX = vProjektionX; }

    public void setvProjektionY(double vProjektionY){ this.vProjektionY = vProjektionY; }

}

