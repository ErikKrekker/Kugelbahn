package com.company;
import java.awt.Color;

import javax.swing.*;
import java.awt.*;

public class Screen extends JPanel implements Runnable {

    static double FPS = 60;

    public static int width = 750;          //Bildschirmgroese in px
    public static int height = 750;

    static int degree = 18;

    static int diameter = 10;               //Durchmesser der Kugel
    static int radius = diameter/2;         //Radius der Kugel
    static int scale = 10;                  //Skalierungsfaktor 10px = 1m

    static boolean outOfBounds = false;     //Ob die Kugel aus dem Bild geflogen ist
    Thread thread;

    //!!ACHTUNG BEI LINIENERSTELLUNG!!
    //x0 muss immer kleiner als x1 sein

    static Line lines[] = {
            new Line(5,8,35,22),
            new Line(35,25,65,35),
            new Line(0, 15,5,45),
            new Line(10, 45,30,30),
            new Line(0, 50,40,60),
            new Line(40, 65,75,45),
            new Line(0, 72,70,72),
            new Line(70, 0,70,72),
    };

    static Marble ball[] = {
            new Marble(62, 24.3, 0, 0, 1.7, true, false),
            new Marble(22, 11.5, 0, 0, 1.7, true, false),
            new Marble(54, 30.8, 0, 0, 2, false, true),
            new Marble(58, 32.1, 0, 0, 2, false, true),
    };

    public Screen() {

    }

    //startet die "run" methode
    public void startThread(){

            thread = new Thread(this);
            thread.start();

    }
    public void pauseThread(){
        thread.stop();
    }


    @Override
    public void run() {

        //Zeitmessung in nanoSek
        double drawInterval = 1000000000/FPS;
        double nextDrawTime = System.nanoTime() + drawInterval;

        while(!outOfBounds){

            update();
            //Startet paintComponent methode
            repaint();

            try {
                double remainingTime = nextDrawTime - System.nanoTime();
                remainingTime = remainingTime/1000000;

                if(remainingTime < 0){
                    remainingTime = 0;
                }

                Thread.sleep((long) remainingTime);

                nextDrawTime += drawInterval;

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    //startet Berechnungen mit Zeitintervall delta_t = 1/60 s
    public void update(){
        checkBounds();
        if(!outOfBounds){
            Controller.orderOfOperation(1/FPS);
        }else if(outOfBounds){
            outOfBounds = false;
            JOptionPane.showMessageDialog(null, "Durchlauf beendet" +"\n" + "'Update Marble' drücken zur Vorbereitung eines weiteren Durchlaufes");
            thread.stop();
        }

    }

    //Zeichnet alle Elemente neu
    public void paintComponent(Graphics g){

        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D)g;
        //Zeichnen der Linien
        g2.setColor(Color.white);
        g2.drawLine(lines[0].getX0() * scale, lines[0].getY0() * scale, lines[0].getX1() * scale, lines[0].getY1() * scale);

        g2.drawLine(lines[1].getX0() * scale, lines[1].getY0() * scale, lines[1].getX1() * scale, lines[1].getY1() * scale);

        g2.drawLine(lines[2].getX0() * scale, lines[2].getY0() * scale, lines[2].getX1() * scale, lines[2].getY1() * scale);

        g2.drawLine(lines[3].getX0() * scale, lines[3].getY0() * scale, lines[3].getX1() * scale, lines[3].getY1() * scale);

        g2.drawLine(lines[4].getX0() * scale, lines[4].getY0() * scale, lines[4].getX1() * scale, lines[4].getY1() * scale);

        g2.drawLine(lines[5].getX0() * scale, lines[5].getY0() * scale, lines[5].getX1() * scale, lines[5].getY1() * scale);

        g2.drawLine(lines[6].getX0() * scale, lines[6].getY0() * scale, lines[6].getX1() * scale, lines[6].getY1() * scale);

        g2.drawLine(lines[7].getX0() * scale, lines[7].getY0() * scale, lines[7].getX1() * scale, lines[7].getY1() * scale);






        //Line2D line = new Line2D.Double(10 * scale,30 * scale,10 * scale,30 * scale);
        //g2.draw(line);
        //AffineTransform at = AffineTransform.getRotateInstance(Math.toRadians(degree), line.getX1(), line.getY1());
        //g2.draw(at.createTransformedShape(line));

        //Zeichnen der Kugeln mit Geschwindigkeitsvektor
        g2.setColor(Color.green);
        g2.fillOval((int)((ball[0].getPosX()*scale - radius)) ,(int)((ball[0].getPosY()*scale - radius)), diameter, diameter);
        g2.drawLine((int)(ball[0].getPosX() * scale), (int)(ball[0].getPosY() * scale), (int)((ball[0].getPosX() + ball[0].getVelX()) * scale), (int)((ball[0].getPosY() + ball[0].getVelY()) * scale));

        g2.setColor(Color.MAGENTA);
        g2.fillOval((int)((ball[1].getPosX()*scale - radius)) ,(int)((ball[1].getPosY()*scale - radius)), diameter, diameter);
        g2.drawLine((int)(ball[1].getPosX() * scale), (int)(ball[1].getPosY() * scale), (int)((ball[1].getPosX() + ball[1].getVelX()) * scale), (int)((ball[1].getPosY() + ball[1].getVelY()) * scale));


        g2.setColor(Color.white);
        g2.fillOval((int)((ball[2].getPosX()*scale - radius)) ,(int)((ball[2].getPosY()*scale - radius)), diameter, diameter);
        g2.drawLine((int)(ball[2].getPosX() * scale), (int)(ball[2].getPosY() * scale), (int)((ball[2].getPosX() + ball[2].getVelX()) * scale), (int)((ball[2].getPosY() + ball[2].getVelY()) * scale));

        g2.setColor(Color.gray);
        g2.fillOval((int)((ball[3].getPosX()*scale - radius)) ,(int)((ball[3].getPosY()*scale - radius)), diameter, diameter);
        g2.drawLine((int)(ball[3].getPosX() * scale), (int)(ball[3].getPosY() * scale), (int)((ball[3].getPosX() + ball[3].getVelX()) * scale), (int)((ball[3].getPosY() + ball[3].getVelY()) * scale));
        g2.dispose();


    }

    //schaut, ob die Kugeln an den Seiten bzw. unten aus dem Bild fliegt
    public void checkBounds(){

        if((int)(ball[0].getPosX()*scale - radius) >= width || (int)(ball[0].getPosX()*scale + radius) < 0 || (int)(ball[0].getPosY()*scale - radius) >= height){
            outOfBounds = true;
            System.out.println("--------ENDE---------");
        }

    }
}
