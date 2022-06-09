package com.company;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

//Github Test weil es Error gesagt hat obwohl da kein Error mehr war

public class Main {
    static int x;
    static int y;
    public static JLabel currentCoordinate = new JLabel("Current Coordinate: [" + Kugelbahn.pos[0] + ", " + Kugelbahn.pos[1] + "]");
    public static JLabel geschwindigkeit_val = new JLabel("Geschwindigkeit : ");
    //y-richtung anpassen -> wie in echt
    public static void main(String[] args) {


        JFrame window = new JFrame();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.setTitle("Kugelbahn");

        //Start settings
        Kugelbahn.pos[0] = 10;
        Kugelbahn.pos[1] = 10;

        Kugelbahn.vel[0] = 0;
        Kugelbahn.vel[1] = 0;

        Kugelbahn.wind[0] = 0;
        Kugelbahn.wind[1] = 0;

        Kugelbahn.gravity[0] = 0;
        //Kugelbahn.gravity[1] = -9.81;
        Kugelbahn.gravity[1] = 10;


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
        posx_val.setText(String.valueOf( Kugelbahn.pos[0]));
        screen.add(posx_val);

        JLabel posy = new JLabel("Position Y: ");
        posy.setBounds(780, 60, 80, 25);
        screen.add(posy);

        JTextField posy_val = new JTextField();
        posy_val.setBounds(890, 60, 80, 25);
        posy_val.setText(String.valueOf( Kugelbahn.pos[1]));
        screen.add(posy_val);

        JLabel velx = new JLabel("Geschw X: ");
        velx.setBounds(780, 100, 80, 25);
        screen.add(velx);

        JTextField velx_val = new JTextField();
        velx_val.setBounds(890, 100, 80, 25);
        velx_val.setText(String.valueOf( Kugelbahn.vel[0]));
        screen.add(velx_val);

        JLabel vely = new JLabel("Geschw Y: ");
        vely.setBounds(780, 140, 80, 25);
        screen.add(vely);

        JTextField vely_val = new JTextField();
        vely_val.setBounds(890, 140, 80, 25);
        vely_val.setText(String.valueOf( Kugelbahn.vel[1]));
        screen.add(vely_val);

        JLabel windx = new JLabel("Wind X: ");
        windx.setBounds(780, 180, 80, 25);
        screen.add(windx);

        JTextField windx_val = new JTextField();
        windx_val.setBounds(890, 180, 80, 25);
        windx_val.setText(String.valueOf(Kugelbahn.wind[0]));
        screen.add(windx_val);

        JLabel windy = new JLabel("Wind Y: ");
        windy.setBounds(780, 220, 80, 25);
        screen.add(windy);

        JTextField windy_val = new JTextField();
        windy_val.setBounds(890, 220, 80, 25);
        windy_val.setText(String.valueOf(Kugelbahn.wind[1]));
        screen.add(windy_val);


        geschwindigkeit_val.setBounds(780, 420, 180, 25);
        screen.add(geschwindigkeit_val);

        JLabel rotation_val = new JLabel("Rotation : ");
        rotation_val.setBounds(780, 460, 120, 25);
        screen.add(rotation_val);



        currentCoordinate.setBounds(780, 540, 190, 25);
        screen.add(currentCoordinate);

        Screen field = new Screen();


        JSlider lineTilt = new JSlider(JSlider.HORIZONTAL,0,360,0);
        lineTilt.setBounds(780, 500, 180, 25);
        lineTilt.addChangeListener(e -> tilt(field,rotation_val, lineTilt));
        screen.add(lineTilt);


        field.setBounds(0, 0, Screen.width, Screen.height);
        field.setBackground(Color.pink);
        field.setDoubleBuffered(true);
        field.setFocusable(true);
        field.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                x = e.getX() / Screen.scale;
                y = (e.getY() / Screen.scale);
                //y = (e.getY() / Screen.scale) * -1;

                placeball(x,y,currentCoordinate,field,posx_val,posy_val);
            }
        });

        screen.add(field);
        JButton positionUpdate = new JButton("Update Kugel");
        positionUpdate.setBounds(780, 300, 190, 25);
        positionUpdate.addActionListener(e -> programmupdate(posx_val,posy_val,velx_val,vely_val,windx_val,windy_val,field));
        screen.add(positionUpdate);

        JButton start = new JButton("GO!");
        start.setBounds(780, 260, 80, 25);
        start.addActionListener(e -> programmstart(field, start, positionUpdate));
        screen.add(start);

        JButton pause = new JButton("PAUSE");
        pause.setBounds(890, 260, 80, 25);
        pause.addActionListener(e -> programmpause(field, start, positionUpdate));
        screen.add(pause);



        window.add(screen);

        window.pack();

        window.setLocationRelativeTo(null);
        window.setVisible(true);

    }

    public static void tilt(Screen field, JLabel rotation_val, JSlider tilt_val){

        int rotation_pos = tilt_val.getValue();

        rotation_val.setText("Rotation : " + Double.toString(tilt_val.getValue()));
        Screen.degree = tilt_val.getValue();
        field.repaint();

    }

    public static void dragline(){



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

    public static void showvel(){

        geschwindigkeit_val.setText("Geschwindigkeit : " + Math.round(Kugelbahn.vel[0]) + " , " + Math.round(Kugelbahn.vel[1]));

    }

    public static void programmupdate(JTextField posx_val,JTextField posy_val, JTextField velx_val,JTextField vely_val, JTextField windx_val,JTextField windy_val,  Screen field) {

        double positionX = Double.parseDouble(posx_val.getText());
        double positionY = Double.parseDouble(posy_val.getText());

        double velX = Double.parseDouble(velx_val.getText());
        double velY = Double.parseDouble(vely_val.getText());

        double windX = Double.parseDouble(windx_val.getText());
        double windY = Double.parseDouble(windy_val.getText());

        Kugelbahn.rollen = false;

        Kugelbahn.pos[0] = positionX;
        Kugelbahn.pos[1] = positionY;

        Kugelbahn.vel[0] = velX;
        Kugelbahn.vel[1] = velY;

        Kugelbahn.wind[0] = windX;
        Kugelbahn.wind[1] = windY;

        field.repaint();

        Kugelbahn.rollen = false;
    }


    public static void placeball(int posx_val, int posy_val, JLabel currentCoordinate, Screen field, JTextField posx_field, JTextField posy_field){

        double positionX = posx_val;
        double positionY = posy_val;

        Kugelbahn.pos[0] = positionX;
        Kugelbahn.pos[1] = positionY;

        posx_field.setText(Double.toString(Kugelbahn.pos[0]));
        posy_field.setText(Double.toString(Kugelbahn.pos[1]));

        currentCoordinate.setText("Current Coordinate: [" + positionX + ", " + positionY + "]");

        field.repaint();


    }

    public static void updateCoordinate(){
        //aktuelle Koordinaten als int
       currentCoordinate.setText("Current Coordinate: [" + Math.round(Kugelbahn.pos[0]) + ", " + Math.round(Kugelbahn.pos[1]) + "]");
    }

}