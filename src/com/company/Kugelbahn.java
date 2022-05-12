package com.company;

public class Kugelbahn {
    static double v0, s0, vx, vy, sx, sy, ax ,ay, t, m, b, y, x;
    static double vel[] = new double[2];
    static double pos[] = new double[2];
    static double gravity[] = new double[2];
    static double wind[] = new double[2];
    static Line lines[] = new Line[3];

    static boolean falling = true;
    static boolean rollen = false;
    static double d = Double.POSITIVE_INFINITY;

    static double heightLoss = 0.78;

    //führt alle Berechnungnen in richitger Reihenfolge aus
    public static void calc(double t){

        calcAcceleration();
        calcVelocity(t);
        calcPosition(t);
        //Prüft ob die Kugel am fallen ist
        //checkMovementChange();
        checkMovement();

        updateVelDis();

        calcAngle();
        collisionCheck();
    }

    public static void calcVelocity(double t)
    {
        vx = vel[0] + ax*t;

        vy = vel[1] + ay*t;
    }

    public static void calcPosition(double t)
    {
        sx = pos[0] + vel[0]*t + (0.5*ax*Math.pow(t, 2));

        sy = pos[1] + vel[1]*t + (0.5*ay*Math.pow(t, 2));
    }
    static double aHang;
    static double aNormal;
    static double aReib;
    public static void calcAcceleration(){
        if(rollen == false) {
            ax = gravity[0] + wind[0];

            ay = gravity[1] + wind[1];
        }
        else if (rollen == true){
            aHang = Math.abs(gravity[1]) * Math.sin(angle);
            aNormal = Math.abs(gravity[1] * Math.cos(angle));
            aReib = Math.abs(aNormal) * 0.5;

            if(aHang > aReib){
                ax = Math.cos(angle) * aHang;

                ay = Math.sin(angle) * aHang;
            }
        }
    }

    public static void updateVelDis(){

        vel[0] = vx;
        vel[1] = vy;

        pos[0] = sx;
        pos[1] = sy;
    }

    public static void createLines(){

        lines[0] = new Line(24,60,30,50);
        lines[1] = new Line(0,17,40,30);
        lines[2] = new Line(0, Screen.width/ Screen.scale,44,44);
    }

    //Sprunghöhenverlust
    //http://docplayer.org/72996896-Die-hyperaktive-kugel-wann-hoert-sie-endlich-auf-zu-springen.html (Abschnitt 3.5.3)

    public static void collisionCheck(){

        calcDistancePointLine(1);
        System.out.println(d);
        //Kugel trifft von oben auf die Gerade
        if(crossP < 0 && d <= 0.4 && pos[0] >= line1.getX0() && pos[0] <= line1.getX1()){
            System.out.println("BONKKK");
            vel[1] = -(vel[1] * heightLoss);
            vel[0] = 0;
        }

        //Kugel trifft von unten auf die Gerade
        else if (crossP > 0 && d<= 0.4 && pos[0] >= line1.getX0() && pos[0] <= line1.getX1()){
            System.out.println("BONKKKKK2");
            vel[1] = -(vel[1] * heightLoss);
            vel[0] = 0;
        }
        /*
        System.out.println(d);
        for(int i = 0;i < lines.length; i++){
            schnittPunktX(i);

            if(d <= 0.4 && d >= -0.1 && falling == true && pos[0] >= lines[i].getX0() && pos[0] <= lines[i].getX1()){
                System.out.println("Bonk1");
                vel[1] = -(vel[1] * heightLoss);
                if(m<0)
                    vel[0] = - (vel[1] / Math.tan(angle));
                else if(m>0)
                    vel[0] = (vel[1] / Math.tan(angle));
            }
            //Verhindert, dass Kugel von unten durch die Line fliegt
            else if(d >= -0.4 && d <= 0.1 && falling == true && pos[0] >= lines[i].getX0() && pos[0] <= lines[i].getX1()){
                System.out.println("Bonk2");
                vel[0] = -(vel[0] * heightLoss);
            }
            //verhindert, dass Kugel direkt nach Aufprall durch die Linie fliegt / stecken bleibt
            else if(d >= -0.4 && d <= 0.1 && falling == false && pos[0] >= lines[i].getX0() && pos[0] <= lines[i].getX1()){
                System.out.println("Bonk3");
                pos[0] = pos[0] + Screen.radius/Screen.scale;
                pos[1] = pos[1] - Screen.radius/Screen.scale;
                vel[1] = -(vel[1] * heightLoss);
                if(m<0)
                    vel[0] = -(vel[1] / Math.tan(angle));
                else if(m>0)
                    vel[0] = (vel[1] / Math.tan(angle));
            }

        }*/
    }
    static double crossP;
    static Line line1 = new Line(25, 25, 15, 5);
    public static void calcDistancePointLine(int i){
        double richtungX = pos[0] - line1.getX0();
        double richtungY = pos[1] - line1.getY0();

        double normX = -1*(line1.getY1() - line1.getY0());
        double normY = (line1.getX1() - line1.getX0());

        crossP = richtungX * normX + richtungY * normY;

        d = Math.abs((richtungX * normX + richtungY * normY) / Math.sqrt(Math.pow(normX, 2) + Math.pow(normY, 2)) + Screen.radius/Screen.scale);

        System.out.println(crossP);

        if(crossP > 0)
            System.out.println("Der Punkt liegt unter der Linie");
        else if(crossP < 0)
            System.out.println("Der Punkt liegt über der Linie");
        else
            System.out.println("Der Punkt liegt genau auf der Linie");

    }

    static double ballVekX;
    static double ballVekY;
    static double lineVekX;
    static double lineVekY;
    static double oben, unten;
    static double angle;
    //Berechnet Winkel zw Gerade & Senkrechte der Kugel
    public static void calcAngle(){

        ballVekX = pos[0] - pos[0];
        ballVekY = (Screen.height/ Screen.scale) + pos[1];

        lineVekX = lines[1].getX1() - lines[1].getX0();
        lineVekY = lines[1].getY1() - lines[1].getY0();

        oben = Math.abs(ballVekX * lineVekX + ballVekY * lineVekY);

        unten = Math.sqrt(Math.pow(ballVekX, 2) + Math.pow(ballVekY, 2)) * Math.sqrt(Math.pow(lineVekX, 2) + Math.pow(lineVekY, 2));

        //Winkel wird in Bogenmaß angegeben
        angle = Math.acos(oben/unten);
    }


    public static void checkMovement(){
        if(rollen == false){
            if(sy < pos[1])
                falling = true;
            else if (sy > pos[1])
                falling = false;
            else if(count >= 60)
                rollen = true;
        }

    }
    static double changeX;
    static double changeY;
    static int count = 0;
    public static void checkMovementChange(){
        changeX = Math.abs(pos[0] - sx);
        changeY = Math.abs(pos[1] - sy);

        if(changeX <= 0.05 && changeY <= 0.05)
            count++;
        else
            count = 0;
    }
}



/*
            //alte Berechnung Abstand
            xtemp = pos[0] - line1.getX0();
            ytemp = -pos[1] - line1.getY0();
            //Kreuzprodukt
            crossx = ytemp * line1.getX1() - xtemp * line1.getY1();
            crossy = xtemp * line1.getY1() - ytemp * line1.getX1();
            oben = Math.sqrt(Math.pow(crossx, 2) + Math.pow(crossy, 2));
            unten = Math.sqrt(Math.pow(line1.getX1(), 2) + Math.pow(line1.getY1(), 2));
            d = (oben / unten) - ((Screen.diameter/2)/Screen.scale);
        */