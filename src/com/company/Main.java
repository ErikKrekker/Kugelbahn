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
    //y-richtung anpassen -> wie in echt
    public static void main(String[] args) {

        //Hallo Larissa
        // HEHEHEHHEHE HALLO ERIK

        Kugelbahn.createLines();

        JFrame window = new JFrame();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.setTitle("Kugelbahn");

        //Start settings
        Kugelbahn.pos[0] = 8;
        Kugelbahn.pos[1] = 13;

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



        JLabel velx = new JLabel("Geschw X: ");
        velx.setBounds(780, 20, 80, 25);
        screen.add(velx);

        JTextField velx_val = new JTextField();
        velx_val.setBounds(890, 20, 80, 25);
        velx_val.setText(String.valueOf( Kugelbahn.vel[0]));
        screen.add(velx_val);

        JLabel vely = new JLabel("Geschw Y: ");
        vely.setBounds(780, 60, 80, 25);
        screen.add(vely);

        JTextField vely_val = new JTextField();
        vely_val.setBounds(890, 60, 80, 25);
        vely_val.setText(String.valueOf( Kugelbahn.vel[1]));
        screen.add(vely_val);

        JLabel windx = new JLabel("Wind X: ");
        windx.setBounds(780, 140, 80, 25);
        screen.add(windx);

        JTextField windx_val = new JTextField();
        windx_val.setBounds(890, 140, 80, 25);
        windx_val.setText(String.valueOf(Kugelbahn.wind[0]));
        screen.add(windx_val);

        JLabel windy = new JLabel("Wind Y: ");
        windy.setBounds(780, 180, 80, 25);
        screen.add(windy);

        JTextField windy_val = new JTextField();
        windy_val.setBounds(890, 180, 80, 25);
        windy_val.setText(String.valueOf(Kugelbahn.wind[1]));
        screen.add(windy_val);

        Screen field = new Screen();

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

                programmupdate(x,y, velx_val,vely_val,windx_val,windy_val,field);
                System.out.println(x);
                System.out.println(y);
            }


        });
        screen.add(field);

        JButton start = new JButton("GO!");
        start.setBounds(780, 220, 80, 25);
        start.addActionListener(e -> programmstart(field));
        screen.add(start);

        JButton pause = new JButton("PAUSE");
        pause.setBounds(890, 220, 80, 25);
        pause.addActionListener(e -> programmpause(field));
        screen.add(pause);

        JButton positionUpdate = new JButton("Update Kugel");
        positionUpdate.setBounds(780, 260, 190, 25);
        positionUpdate.addActionListener(e -> programmupdate(x,y,velx_val,vely_val,windx_val,windy_val,field));
        screen.add(positionUpdate);

        window.add(screen);

        window.pack();

        window.setLocationRelativeTo(null);
        window.setVisible(true);


    }

    public static void programmstart(Screen field) {

        field.startThread();
    }

    public static void programmpause(Screen field) {

        field.pauseThread();
    }



    public static void programmupdate(int posx_val, int posy_val,JTextField velx_val,JTextField vely_val, JTextField windx_val,JTextField windy_val,  Screen field) {

        double poitionX = posx_val;
        double poitionY = posy_val;

        double velX = Double.parseDouble(velx_val.getText());
        double velY = Double.parseDouble(vely_val.getText());

        double windX = Double.parseDouble(windx_val.getText());
        double windY = Double.parseDouble(windy_val.getText());


        Kugelbahn.pos[0] = poitionX;
        Kugelbahn.pos[1] = poitionY;
        Kugelbahn.vel[0] = velX;
        Kugelbahn.vel[1] = velY;

        Kugelbahn.wind[0] = windX;
        Kugelbahn.wind[1] = windY;

        field.repaint();
    }

}