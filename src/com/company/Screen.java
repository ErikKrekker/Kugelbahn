package com.company;
import java.awt.Color;

import javax.swing.*;
import java.awt.*;

public class Screen extends JPanel implements Runnable {

    public static Graphics2D g2;

    static double FPS = 60;

    public static int width = 750;          //Bildschirmgroese in px
    public static int height = 750;


    static int diameter = 10;               //Durchmesser der Kugel
    static int radius = diameter/2;         //Radius der Kugel
    static int scale = 10;                  //Skalierungsfaktor 10px = 1m

    static boolean outOfBounds = false;     //Ob die Kugel aus dem Bild geflogen ist
    Thread thread;

    //!!ACHTUNG BEI LINIENERSTELLUNG!!
    //x0 muss immer kleiner als x1 sein

    static Line lines[] = Main.defaultLineSettings();

    static double[][] startvalues = {
            {26, 70, 0, 0, 1.7},
            {22, 27, 0, 0, 1.7},
            {54, 30.8, 0, 0, 2},
            {58, 32.1, 0, 0, 2}
    };

    static Marble ball[] = Main.defaultBallSettings();

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
            Main.start.setEnabled(true);
            Main.positionUpdate.setEnabled(true);
            thread.stop();

        }

    }

    //Zeichnet alle Elemente neu
    public void paintComponent(Graphics g){

        super.paintComponent(g);

        g2 = (Graphics2D)g;
        //Zeichnen der Linien
        for (int i = 0; i < lines.length; i++){
            if(i == Main.linechoice.getSelectedIndex() && Main.linemovement.isSelected()){
                g2.setColor((Color.red));
            }else{
                g2.setColor(Color.white);
            }
            g2.drawLine(lines[i].getX0() * scale, lines[i].getY0() * scale, lines[i].getX1() * scale, lines[i].getY1() * scale);
        }

        //Zeichnen der Kugeln mit Geschwindigkeitsvektor
        g2.setColor(new Color(46, 46, 191));
        g2.fillOval((int)((ball[0].getPosX()*scale - radius)) ,(int)((ball[0].getPosY()*scale - radius)), diameter, diameter);
        g2.drawLine((int)(ball[0].getPosX() * scale), (int)(ball[0].getPosY() * scale), (int)((ball[0].getPosX() + ball[0].getVelX()) * scale), (int)((ball[0].getPosY() + ball[0].getVelY()) * scale));

        g2.setColor(new Color(236, 233, 84));
        g2.fillOval((int)((ball[1].getPosX()*scale - radius)) ,(int)((ball[1].getPosY()*scale - radius)), diameter, diameter);
        g2.drawLine((int)(ball[1].getPosX() * scale), (int)(ball[1].getPosY() * scale), (int)((ball[1].getPosX() + ball[1].getVelX()) * scale), (int)((ball[1].getPosY() + ball[1].getVelY()) * scale));


        g2.setColor(Color.gray);
        g2.fillOval((int)((ball[2].getPosX()*scale - radius)) ,(int)((ball[2].getPosY()*scale - radius)), diameter, diameter);
        g2.drawLine((int)(ball[2].getPosX() * scale), (int)(ball[2].getPosY() * scale), (int)((ball[2].getPosX() + ball[2].getVelX()) * scale), (int)((ball[2].getPosY() + ball[2].getVelY()) * scale));

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
