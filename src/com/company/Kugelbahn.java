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

        collisionCheck();
        updateVelDis();
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
                ax = (vProjektionX/normalizeProjektion) * aHang;

                ay = (vProjektionY/normalizeProjektion) * aHang;
            }
            else{
                ax = 0;
                ay = 0;
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

    static double normalize;
    static double bounceX, bounceY;
    static double normVekNormalize;
    public static void collisionCheck(){

        calcDistancePointLine(1);
        //System.out.println("Abstand: " +d);
        //System.out.println("Kreuzprodukt: " +crossP);
        //Kugel trifft von oben auf die Gerade
        if(crossP < 0 &&falling && d <= 0.5 && pos[0] >= line1.getX0() && pos[0] <= line1.getX1()){
            calcAngle();
            vectorZerlegung();
            if(vectorLength(vParallelX, vParallelY) <= 0.1){
                rollen = true;
                pos[0] = line1.getX0();
                pos[1] = line1.getY0();
                System.out.println("Bin am rollen");
            }
            else{
                vy = -(vy * heightLoss);
                System.out.println("BONKKK");
            }
        }

        //Kugel trifft von unten auf die Gerade
        else if (crossP > 0 && !falling && d<= 0.4 && pos[0] >= line1.getX0() && pos[0] <= line1.getX1()){
            if(vectorLength(vParallelX, vParallelY) <= 0.1)
                rollen = true;
            System.out.println("BONKKK2");
            calcAngle();
            vectorZerlegung();
        }

        else if(crossP == 0 && !falling && d<= 0.4 && pos[0] >= line1.getX0() && pos[0] <= line1.getX1()){
            if(vectorLength(vParallelX, vParallelY) <= 0.1)
                rollen = true;
            System.out.println("BONKKK2");
            calcAngle();
            vectorZerlegung();
        }
    }
    static double crossP;
    static double normX, normY; //Richtungsvektor der Geraden
    static double richtungX, richtungY;
    static double normalizeRichtung;
    static Line line1 = new Line(20, 30, 24, 30);

    public static void calcDistancePointLine(int i){

        //Vektor zwischen Ball zur Geraden
        richtungX = line1.getX0() - pos[0];
        richtungY = line1.getY0() - pos[1];

        normX = (line1.getY1() - line1.getY0());
        normY = -1*(line1.getX1() - line1.getX0());

        normalizeRichtung = vectorLength(richtungX, richtungY);

        crossP = richtungX * normX + richtungY * normY;

        d = Math.abs((richtungX * normX + richtungY * normY) / Math.sqrt(Math.pow(normX, 2) + Math.pow(normY, 2)) + Screen.radius/Screen.scale);

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

        ballVekX = -1*(vx - vel[0]);
        ballVekY = -1*(vy - vel[1]);

        angle = Math.acos((ballVekX * normX + ballVekY * normY) / (vectorLength(ballVekX, ballVekY) * vectorLength(normX, normY)));

        System.out.println("Winkel: "+ angle);
        /*
        lineVekX = lines[1].getX1() - lines[1].getX0();
        lineVekY = lines[1].getY1() - lines[1].getY0();

        oben = Math.abs(ballVekX * lineVekX + ballVekY * lineVekY);

        unten = Math.sqrt(Math.pow(ballVekX, 2) + Math.pow(ballVekY, 2)) * Math.sqrt(Math.pow(lineVekX, 2) + Math.pow(lineVekY, 2));

        //Winkel wird in Bogenmaß angegeben
        angle = Math.acos(oben/unten);
        */
    }

    static double vProjektionX;
    static double vProjektionY;
    static double skalar;
    static double vParallelX, vParallelY;
    static double normalizeProjektion;
    public static void vectorZerlegung(){

        skalar = ((-ballVekX) * (line1.getX1() - line1.getX0()) + (-ballVekY) * (line1.getY1() - line1.getY0())) / (Math.pow((line1.getX1() - line1.getX0()), 2) + Math.pow((line1.getY1() - line1.getY0()), 2));

        //Projektion des Richtungsvektors auf den Richtungsvektor der Geraden
        vProjektionX = skalar * (line1.getX1() - line1.getX0());
        vProjektionY = skalar * (line1.getY1() - line1.getY0());

        normalizeProjektion = vectorLength(vProjektionX, vProjektionY);

        //Paralleler Anteil der Zerlegung -> Wird für Entscheidung ob rollen oder springen gebraucht
        vParallelX = -(ballVekX) - vProjektionX;
        vParallelY = -(ballVekY) - vProjektionY;
        System.out.println();
    }

    public static double vectorLength(double x, double y){

        return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
    }


    public static void checkMovement(){
            if(sy < pos[1])
                falling = false;
            else if (sy > pos[1])
                falling = true;
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