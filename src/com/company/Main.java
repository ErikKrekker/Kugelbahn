package com.company;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


public class Main {
    static int x;
    static int y;
    public static JLabel currentCoordinate = new JLabel("Current Coordinate: [" +Screen.ball[0].getPosX()+ ", " + Screen.ball[0].getPosY() + "]");
    //y-richtung anpassen -> wie in echt
    public static void main(String[] args) {


        JFrame window = new JFrame();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.setTitle("Controller");

        //Start settings

        Controller.wind[0] = 0;
        Controller.wind[1] = 0;

        Controller.gravity[0] = 0;
        //Controller.gravity[1] = -9.81;
        Controller.gravity[1] = 10;


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
        JButton positionUpdate = new JButton("Update Marble");
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



    public static void programmupdate(JTextField posx_val,JTextField posy_val, JTextField velx_val,JTextField vely_val, JTextField windx_val,JTextField windy_val,  Screen field) {

        double positionX = Double.parseDouble(posx_val.getText());
        double positionY = Double.parseDouble(posy_val.getText());

        double velX = Double.parseDouble(velx_val.getText());
        double velY = Double.parseDouble(vely_val.getText());

        double windX = Double.parseDouble(windx_val.getText());
        double windY = Double.parseDouble(windy_val.getText());

        Screen.ball[0].setPosX(positionX);
        Screen.ball[0].setPosY(positionY);

        Screen.ball[0].setVelX(velX);
        Screen.ball[0].setVelY(velY);

        Controller.wind[0] = windX;
        Controller.wind[1] = windY;

        field.repaint();
        for(int i = 0; i < Screen.ball.length; i++) {
            Screen.ball[i].setRollen(false);

        }

    }


    public static void placeball(int posx_val, int posy_val, JLabel currentCoordinate, Screen field, JTextField posx_field, JTextField posy_field){

        Screen.ball[0].setPosX(posx_val);
        Screen.ball[0].setPosY(posy_val);

        posx_field.setText(Double.toString(Screen.ball[0].getPosX()));
        posy_field.setText(Double.toString(Screen.ball[0].getPosY()));

        currentCoordinate.setText("Current Coordinate: [" + (double) posx_val + ", " + (double) posy_val + "]");

        field.repaint();

    }

    public static void updateCoordinate(){
        //aktuelle Koordinaten als int
        int pos_x = (int)Screen.ball[0].getPosX();
        int pos_y = (int)Screen.ball[0].getPosY();
        currentCoordinate.setText("Current Coordinate: [" + pos_x + ", " + pos_y + "]");
    }

}