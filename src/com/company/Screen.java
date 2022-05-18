package com.company;
import java.awt.Color;

import javax.swing.*;
import java.awt.*;

public class Screen extends JPanel implements Runnable {

    static double FPS = 60;

    public static int width = 750;
    public static int height = 750;

    static int diameter = 10;
    static int radius = diameter/2;
    static int scale = 10;

    static boolean outOfBounds = false;
    Thread thread;

    static Line lines[] = {
            //new Line(5,30,50,70),
            //new Line(20,30,50,20)
            //new Line(0, Screen.width/ Screen.scale,45,45)
    };

    public Screen() {

    }

    //führt die run methode aus
    public void startThread(){


            thread = new Thread(this);
            thread.start();

    }
    public void pauseThread(){
        thread.stop();
    }


    @Override
    public void run() {

        //Zeitmessung in nanoSek für Präzision
        double drawInterval = 1000000000/FPS;
        double nextDrawTime = System.nanoTime() + drawInterval;

        while(outOfBounds == false){

            update();
            //Startet paintComponent methode
            //Kollision erkennung
            //Handling -> weiter rollen oder abspringen -> neuer RIchtungsvektor
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

    //startet Berechnungen mit vergangener Zeit
    public void update(){
        checkBounds();
        if(outOfBounds == false){
            Kugelbahn.calc(1/FPS);
        }else if(outOfBounds == true){
            outOfBounds = false;
            JOptionPane.showMessageDialog(null, "Durchlauf beendet" +"\n" + "'Update Kugel' drücken zur Vorbereitung eines weiteren Durchlaufes");
            thread.stop();
        }

    }

    //Zeichnet die Kugelpostion neu
    public void paintComponent(Graphics g){

        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D)g;
        /*
        g2.setColor(Color.white);

        g2.setColor(Color.white);
        g2.drawLine(lines[0].getX0() * scale, lines[0].getY0() * scale, lines[0].getX1() * scale, lines[0].getY1() * scale);

        g2.setColor(Color.blue);
        //g2.drawLine(lines[1].getX0() * scale, lines[1].getY0() * scale, lines[1].getX1() * scale, lines[1].getY1() * scale);

        g2.setColor(Color.cyan);
        //g2.drawLine(lines[2].getX0() * scale, lines[2].getY0() * scale, lines[2].getX1() * scale, lines[2].getY1() * scale);
        */

        g2.drawLine(Kugelbahn.line1.getX0() * scale, Kugelbahn.line1.getY0() * scale, Kugelbahn.line1.getX1() * scale, Kugelbahn.line1.getY1() * scale );
        g2.setColor(Color.blue);
        //Zeichnen der Kugel
        //g2.fillOval((int)(Kugelbahn.pos[0]*scale - (diameter/2)) ,(int)((Kugelbahn.pos[1]*scale + (diameter/2))*-1), diameter, diameter);
        g2.fillOval((int)(Kugelbahn.pos[0]*scale - radius) ,(int)((Kugelbahn.pos[1]*scale - radius)), diameter, diameter);
        g2.dispose();
    }

    //schaut, ob die Kugel an den Seiten bzw. unten aus dem Bild fliegt
    public void checkBounds(){

        if((int)(Kugelbahn.pos[0]*scale - radius) >= width || (int)(Kugelbahn.pos[0]*scale + radius) < 0 || (int)(Kugelbahn.pos[1]*scale - radius) >= height){
            outOfBounds = true;
            System.out.println("--------ENDE---------");
        }

    }
}
