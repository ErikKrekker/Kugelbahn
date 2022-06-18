package com.company;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


public class Main {
    static Screen field;
    static JButton positionUpdate;
    static JButton start;
    static JComboBox ballchoice;
    static JComboBox linechoice;
    static JCheckBox linemovement;
    static int x;
    static int y;
    public static JLabel currentCoordinate = new JLabel(" Kugel Position: [" +Screen.ball[0].getPosX()+ ", " + newY(Screen.ball[0].getPosY()) + "]");
    //y-richtung anpassen -> wie in echt
    public static void main(String[] args) {


        JFrame window = new JFrame();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.setTitle("Kugelbahn");

        //Start settings

        Controller.wind[0] = 0;
        Controller.wind[1] = 0;

        Controller.gravity[0] = 0;
        Controller.gravity[1] = 9.81;


        JPanel screen = new JPanel();
        screen.setLayout(null);
        window.setPreferredSize(new Dimension(1000, 800));
        //  window.setDoubleBuffered(true);
        window.setFocusable(true);


        JLabel posx = new JLabel("Position X: ");
        posx.setBounds(780, 20, 80, 25);
        screen.add(posx);

        JTextField posx_val = new JTextField();
        posx_val.setBounds(890, 20, 80, 25);
        posx_val.setText(String.valueOf(Screen.ball[0].getPosX()));
        screen.add(posx_val);

        JLabel posy = new JLabel("Position Y: ");
        posy.setBounds(780, 60, 80, 25);
        screen.add(posy);

        JTextField posy_val = new JTextField();
        posy_val.setBounds(890, 60, 80, 25);
        posy_val.setText(String.valueOf(Screen.ball[0].getPosY()));
        screen.add(posy_val);

        JLabel velx = new JLabel("Geschw X: ");
        velx.setBounds(780, 100, 80, 25);
        screen.add(velx);

        JTextField velx_val = new JTextField();
        velx_val.setBounds(890, 100, 80, 25);
        velx_val.setText(String.valueOf(Screen.ball[0].getVelX()));
        screen.add(velx_val);

        JLabel vely = new JLabel("Geschw Y: ");
        vely.setBounds(780, 140, 80, 25);
        screen.add(vely);

        JTextField vely_val = new JTextField();
        vely_val.setBounds(890, 140, 80, 25);
        vely_val.setText(String.valueOf(Screen.ball[0].getVelY()));
        screen.add(vely_val);

        JLabel windx = new JLabel("Wind X: ");
        windx.setBounds(780, 180, 80, 25);
        screen.add(windx);

        JTextField windx_val = new JTextField();
        windx_val.setBounds(890, 180, 80, 25);
        windx_val.setText(String.valueOf(Controller.wind[0]));
        screen.add(windx_val);

        JLabel windy = new JLabel("Wind Y: ");
        windy.setBounds(780, 220, 80, 25);
        screen.add(windy);

        JTextField windy_val = new JTextField();
        windy_val.setBounds(890, 220, 80, 25);
        windy_val.setText(String.valueOf(Controller.wind[1]));
        screen.add(windy_val);

        String[] ballselect = {"Green Ball","Pink Ball"};

        ballchoice = new JComboBox(ballselect);
        ballchoice.setBounds(810, 345, 120, 25);
        screen.add(ballchoice);

        currentCoordinate.setBounds(800, 540, 190, 25);
        screen.add(currentCoordinate);

        field = new Screen();

        JLabel linedisclaimer = new JLabel("Linke Seite = Linke Maustaste");
        linedisclaimer.setBounds(793, 400, 220, 25);
        screen.add(linedisclaimer);

        JLabel linedisclaimer2 = new JLabel("Rechte Seite = Rechte Maustaste");
        linedisclaimer2.setBounds(781, 420, 220, 25);
        screen.add(linedisclaimer2);
        String[] lineselect = {"Linie 1","Linie 2", "Linie 3", "Linie 4", "Linie 5","Linie 6"};

        linechoice = new JComboBox(lineselect);
        linechoice.setBounds(810, 460, 120, 25);
        linechoice.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                field.repaint();
            }
        });
        screen.add(linechoice);

        linemovement = new JCheckBox("<-- Linien Verschieben");
        linemovement.setBounds(781, 500, 180, 25);
        linemovement.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                field.repaint();
            }
        });
        screen.add(linemovement);

        field.setBounds(0, 0, Screen.width, Screen.height);
        field.setBackground(Color.PINK);
        field.setDoubleBuffered(true);
        field.setFocusable(true);
        field.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                x = e.getX() / Screen.scale;
                y = (e.getY() / Screen.scale);
                //y = (e.getY() / Screen.scale) * -1;
                if (linemovement.isSelected()){
                    placeline(x,y,field,e);
                }else{

                    placeball(x,y,currentCoordinate,field,posx_val,posy_val);

                }


            }
        });

        screen.add(field);
        positionUpdate = new JButton("Update Kugel");
        positionUpdate.setBounds(780, 300, 190, 25);
        positionUpdate.addActionListener(e -> programmupdate(posx_val,posy_val,velx_val,vely_val,windx_val,windy_val,field));
        screen.add(positionUpdate);

        start = new JButton("GO!");
        start.setBounds(780, 260, 80, 25);
        start.addActionListener(e -> programmstart(field, start, positionUpdate));
        screen.add(start);

        JButton pause = new JButton("PAUSE");
        pause.setBounds(890, 260, 80, 25);
        pause.addActionListener(e -> programmpause(field, start, positionUpdate));
        screen.add(pause);

        JButton resetWorld = new JButton("Alles zurücksetzen");
        resetWorld.setBounds(780, 700, 190, 25);
        resetWorld.addActionListener(e -> programmreset(field, start, positionUpdate,posx_val,posy_val,velx_val,vely_val,windx_val,windy_val ));
        screen.add(resetWorld);



        window.add(screen);

        window.pack();

        window.setLocationRelativeTo(null);
        window.setVisible(true);

    }

    public static void programmstart(Screen field, JButton start,JButton update) {

        field.startThread();

        start.setEnabled(false);
        update.setEnabled(false);

    }

    public static void programmpause(Screen field, JButton start, JButton update) {

        field.pauseThread();

        start.setEnabled(true);
        update.setEnabled(true);
    }



    public static void programmupdate(JTextField posx_val,JTextField posy_val, JTextField velx_val,JTextField vely_val, JTextField windx_val,JTextField windy_val,  Screen field) {

        double positionX = Double.parseDouble(posx_val.getText());
        double positionY = Double.parseDouble(posy_val.getText());

        double velX = Double.parseDouble(velx_val.getText());
        double velY = Double.parseDouble(vely_val.getText());

        double windX = Double.parseDouble(windx_val.getText());
        double windY = Double.parseDouble(windy_val.getText());

        Screen.ball[ballchoice.getSelectedIndex()].setPosX(positionX);
        Screen.ball[ballchoice.getSelectedIndex()].setPosY(newY(positionY));

        Screen.ball[ballchoice.getSelectedIndex()].setVelX(velX);
        Screen.ball[ballchoice.getSelectedIndex()].setVelY(-1*velY);

        Controller.wind[0] = windX;
        Controller.wind[1] = -1*windY;

        field.repaint();
        for(int i = 0; i < Screen.ball.length; i++) {
            Screen.ball[i].setRollen(false);
        }
        Screen.ball[2] = resetMagnet1();
        Screen.ball[3] = resetMagnet2();

    }

    public static void programmreset(Screen field, JButton start, JButton update,JTextField posx_val,JTextField posy_val, JTextField velx_val,JTextField vely_val, JTextField windx_val,JTextField windy_val){

        for (int i = 0; i < Screen.ball.length; i++){

            for(int j = 0; j <= Screen.startvalues.length; j++) {

                switch (j){
                    case 0 :
                        Screen.ball[i].setPosX(Screen.startvalues[i][j]);
                    case 1 :
                        Screen.ball[i].setPosY(Screen.startvalues[i][j]);
                    case 2 :
                        Screen.ball[i].setVelX(Screen.startvalues[i][j]);
                    case 3 :
                        Screen.ball[i].setVelY(Screen.startvalues[i][j]);
                    case 4 :
                        Screen.ball[i].setWeight(Screen.startvalues[i][j]);
                }
                programmpause(field, start, update);
                programmupdate(posx_val,posy_val,velx_val, vely_val,windx_val, windy_val, field);

            }

        }

    }

    public static void placeball(int posx_val, int posy_val, JLabel currentCoordinate, Screen field, JTextField posx_field, JTextField posy_field){

        Screen.ball[ballchoice.getSelectedIndex()].setPosX(posx_val);
        Screen.ball[ballchoice.getSelectedIndex()].setPosY(posy_val);
        Screen.ball[ballchoice.getSelectedIndex()].setRollen(false);

        posx_field.setText(Double.toString(Screen.ball[ballchoice.getSelectedIndex()].getPosX()));
        posy_field.setText(Double.toString(newY(Screen.ball[ballchoice.getSelectedIndex()].getPosY())));

        currentCoordinate.setText("Kugel Position: [" + (double) posx_val + ", " + newY((double) posy_val) + "]");

        field.repaint();

    }

    public static void placeline(int posx_val, int posy_val, Screen field, MouseEvent e) {

        if(e.getButton() == MouseEvent.BUTTON1){
            if(posx_val > Screen.lines[linechoice.getSelectedIndex()].getX1()) {
                Screen.lines[linechoice.getSelectedIndex()].setP1(Screen.lines[linechoice.getSelectedIndex()].getX1(), posy_val);
            }
            else{
                Screen.lines[linechoice.getSelectedIndex()].setP1(posx_val, posy_val);
            }
        }else if(e.getButton() == MouseEvent.BUTTON3){
            if(posx_val < Screen.lines[linechoice.getSelectedIndex()].getX0()) {
                Screen.lines[linechoice.getSelectedIndex()].setP2(Screen.lines[linechoice.getSelectedIndex()].getX0(), posy_val);
            }
            else{
                Screen.lines[linechoice.getSelectedIndex()].setP2(posx_val, posy_val);
            }
        }
        field.repaint();
    }


    public static void updateCoordinate(){
        //aktuelle Koordinaten als int
        int pos_x = (int)Screen.ball[ballchoice.getSelectedIndex()].getPosX();
        int pos_y = newY((int)Screen.ball[ballchoice.getSelectedIndex()].getPosY());
        currentCoordinate.setText("Current Coordinate: [" + pos_x + ", " + pos_y + "]");
    }

    public static Line[] defaultLineSettings(){
        Line[] lines ={
                new Line(5,8,35,22),
                new Line(35,25,65,35),
                new Line(0, 7,5,45),
                new Line(10, 45,30,30),
                new Line(6, 50,40,60),
                new Line(40, 65,75,45),
                new Line(0, 72,70,72),
                new Line(0, 51,0,72),
        };
        return lines;
    }

    public static Marble[] defaultBallSettings(){
        Marble ball[] = {
                new Marble(26, 59, 0, 0, 1.7, true, false),
                new Marble(22, 11.5, 0, 0, 1.7, true, false),
                resetMagnet1(),
                resetMagnet2()
        };
        return ball;
    }

    public static Marble resetMagnet1(){
        return new Marble(54, 44.2, 0, 0, 2, false, true);
    }

    public static Marble resetMagnet2(){
        return new Marble(58, 42.9, 0, 0, 2, false, true);
    }

    public static int newY(int y){
        return 75 - y;
    }

    public static double newY(double y){
        return 75 - y;
    }
}