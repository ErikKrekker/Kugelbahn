package com.company;
public class Line {

    int x0;
    int x1;

    int y0;
    int y1;

    double m;

    public Line(int x0, int y0, int x1, int y1){
        this.x0 = x0;
        this.x1 = x1;
        this.y0 = y0;
        this.y1 = y1;
    }

    public int getX0(){
        return x0;
    }

    public int getX1(){
        return x1;
    }

    public int getY0(){
        return y0;
    }

    public int getY1(){
        return y1;
    }

    public void setM(double m) { this.m = m; }

    public double getM() { return m; }

}
