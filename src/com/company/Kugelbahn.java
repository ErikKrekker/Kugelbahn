package com.company;

public class Kugelbahn {
    static double v0, s0, vx, vy, sx, sy, ax ,ay;
    static double vel[] = new double[2];
    static double pos[] = new double[2];
    static double gravity[] = new double[2];
    static double wind[] = new double[2];


    static boolean rollen = false;
    static boolean collision = true;
    static double d = Double.POSITIVE_INFINITY;

    static double heightLoss = 0.37;

    //führt alle Berechnungnen in richitger Reihenfolge aus

    public static void calc(double t){
        calcAcceleration();
        calcVelocity(t);
        calcPosition(t);

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
    static double mClosest;

    public static void calcClosestM(){
        mClosest = ((double)Screen.lines[closest].getY1() - (double)Screen.lines[closest].getY0()) / ((double)Screen.lines[closest].getX1() - (double)Screen.lines[closest].getX0());
    }

    public static void calcAcceleration(){
        if(!rollen) {
            ax = gravity[0] + wind[0];

            ay = gravity[1] + wind[1];
        }
        else if (rollen){
            aHang = Math.abs(gravity[1]) * Math.sin(Math.atan((mClosest )));
            aNormal = Math.abs(gravity[1]) * Math.cos(Math.atan((mClosest )));
            aReib = Math.abs(aNormal) * 0.15;
            System.out.println();
            if(aHang > aReib){
                ax = (vProjektionX/normalizeProjektion) * aHang;

                ay = (vProjektionY/normalizeProjektion) * aHang;
            }

            else if(aHang <= aReib){
                ax = -(vProjektionX/normalizeProjektion) * aHang;

                ay = -(vProjektionY/normalizeProjektion) * aHang;
            }
        }
    }

    public static void updateVelDis(){

        vel[0] = vx;
        vel[1] = vy;

        pos[0] = sx;
        pos[1] = sy;

        Main.updateCoordinate();

    }

    static Line line1 = new Line(5, 25, 10, 30);
    static double m;


    static int closest = 0;

    public static void collisionCheck(){

        for(int i = 0; i< Screen.lines.length; i++) {
            //m = ((double)Screen.lines[i].getY1() - (double)Screen.lines[i].getY0()) / ((double)Screen.lines[i].getX1() - (double)Screen.lines[i].getX0());
                calcDistancePointLine(i);
                checkClosestLine(i);
        }

        calcDistancePointLine(closest);
        checkDirection(closest);
        calcAngle(closest);
        vectorZerlegung(closest);
        //System.out.println("closest line:" + closest);
        //System.out.println(calcDistancePointLine(closest));
        //Kugel trifft von oben auf die Gerade
        if(crossP < 0  && collision && !rollen && calcDistancePointLine(closest) <= 0.55 && pos[0] >= Screen.lines[closest].getX0() && pos[0] <= Screen.lines[closest].getX1()){

            System.out.println();

            calcClosestM();
            calcAngle(closest);
            vectorZerlegung(closest);
            System.out.println("Erkannt");
            if(vectorLength(vParallelX, vParallelY) <= 1){
                System.out.println(vectorLength(vParallelX, vParallelY));
                rollen = true;
                System.out.println("Bin am rollen");
                if(d <= 0.2){
                    sy = sy + Screen.radius/Screen.scale;
                }
                vx = 0;
                vy = 0;

            }
            else{
                if(m == Double.POSITIVE_INFINITY || m == Double.NEGATIVE_INFINITY)
                    vx = -(heightLoss * (vx - 2 * (vx * (normX / normalizeNorm) + vy * (normY / normalizeNorm)) * (normY / normalizeNorm)));
                else{
                    vx = -(heightLoss * (vx - 2 * (vx * (normX / normalizeNorm) + vy * (normY / normalizeNorm)) * (normY / normalizeNorm)));
                    vy = -(heightLoss * (vy - 2 * (vx * (normX / normalizeNorm) + vy * (normY / normalizeNorm)) * (normY / normalizeNorm)));
                    System.out.println("Abprall");
                }
            }
        }

        //Kugel trifft von unten auf die Gerade
        else if (crossP > 0 && !collision && !rollen && calcDistancePointLine(closest)<= 0.55 && pos[0] >= Screen.lines[closest].getX0()  && pos[0] <= Screen.lines[closest].getX1()) {
            System.out.println("BONKKK2");
            calcAngle(closest);
            //vectorZerlegung(closest);
            if(m == Double.POSITIVE_INFINITY || m == Double.NEGATIVE_INFINITY)
                vx = -(heightLoss * (vx - 2 * (vx * (-normX / normalizeNorm) + vy * (-normY / normalizeNorm)) * (-normY / normalizeNorm)));
            else{
                vx = (heightLoss * (vx - 2 * (vx * (-normX / normalizeNorm) + vy * (-normY / normalizeNorm)) * (normY / normalizeNorm)));
                vy = -(heightLoss * (vy - 2 * (vx * (-normX / normalizeNorm) + vy * (-normY / normalizeNorm)) * (normY / normalizeNorm)));
            }
        }

        else if(rollen && (pos[0] <= Screen.lines[closest].getX0() || pos[0] >= Screen.lines[closest].getX1())){
            rollen = false;
            System.out.println("fliege wieder");
        }


    }

    static double crossP;
    static double normX, normY; //Richtungsvektor der Geraden
    static double normalizeNorm;
    static double richtungX, richtungY;
    static double normalizeRichtung;

    static double dold = calcDistancePointLine(0);

    public static void checkClosestLine(int i){
        //Schaut, ob es einen Abstand gibt, bei welchem die Kugel näher dran liegt
        if(dold > d)
            closest = i;

    }
    public static double calcDistancePointLine(int i){

        //Vektor zwischen Ball zur Geraden
        richtungX = Screen.lines[i].getX0() - pos[0];
        richtungY = Screen.lines[i].getY0() - pos[1];

        //Normalen vektor der Geraden
        normX = (Screen.lines[i].getY1() - Screen.lines[i].getY0());
        normY = -1*(Screen.lines[i].getX1() - Screen.lines[i].getX0());

        normalizeRichtung = vectorLength(richtungX, richtungY);
        normalizeNorm = vectorLength(normX, normY);

        crossP = richtungX * normX + richtungY * normY;

        d = Math.abs(((richtungX * normX + richtungY * normY) / Math.sqrt(Math.pow(normX, 2) + Math.pow(normY, 2))) - Screen.radius/Screen.scale);

        return d;
        /*
        if(crossP > 0)
            System.out.println("Der Punkt liegt unter der Linie");
        else if(crossP < 0)
            System.out.println("Der Punkt liegt über der Linie");
        else
            System.out.println("Der Punkt liegt genau auf der Linie");
        */
    }

    static double ballVekX;
    static double ballVekY;

    static double angle;
    //Berechnet Winkel zw Normalenvektor der Geraden & Richtungsvektor der Kugel
    public static void calcAngle(int i){

        ballVekX = -1*(vx - vel[0]);
        ballVekY = -1*(vy - vel[1]);

        angle = Math.acos((ballVekX * normX + ballVekY * normY) / (vectorLength(ballVekX, ballVekY) * vectorLength(normX, normY)));

        //System.out.println("Winkel: "+ angle);
    }

    static double vProjektionX;
    static double vProjektionY;
    static double skalar;
    static double vParallelX, vParallelY;
    static double tempvParallelX, tempvParallelY;
    static double normalizeProjektion;

    public static void vectorZerlegung(int i){

        skalar = ((-ballVekX) * (Screen.lines[i].getX1() - Screen.lines[i].getX0()) + (-ballVekY) * (Screen.lines[i].getY1() - Screen.lines[i].getY0())) / ((Screen.lines[i].getX1() - Screen.lines[i].getX0()) * (Screen.lines[i].getX1() - Screen.lines[i].getX0()) + (Screen.lines[i].getY1() - Screen.lines[i].getY0()) * (Screen.lines[i].getY1() - Screen.lines[i].getY0()));

        //skalar = ((-vx) * (Screen.lines[i].getX1() - Screen.lines[i].getX0()) + (-vy) * (Screen.lines[i].getY1() - Screen.lines[i].getY0())) / (Math.pow((Screen.lines[i].getX1() - Screen.lines[i].getX0()), 2) + Math.pow((Screen.lines[i].getY1() - Screen.lines[i].getY0()), 2));

        //Projektion des Richtungsvektors auf den Richtungsvektor der Geraden
        vProjektionX = skalar * (Screen.lines[i].getX1() - Screen.lines[i].getX0());
        vProjektionY = skalar * (Screen.lines[i].getY1() - Screen.lines[i].getY0());

        normalizeProjektion = vectorLength(vProjektionX, vProjektionY);

        //Paralleler Anteil der Zerlegung -> Wird für Entscheidung ob rollen oder springen gebraucht
        tempvParallelX = (-ballVekX) - vProjektionX;
        tempvParallelY = (-ballVekY) - vProjektionY;

        vParallelX = vel[0] * (tempvParallelX/(vectorLength(tempvParallelX, tempvParallelY)));
        vParallelY = vel[1] * (tempvParallelY/(vectorLength(tempvParallelX, tempvParallelY)));

        System.out.println();
    }

    public static double vectorLength(double x, double y){

        return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
    }

    static double vrelX, vrelY;
    static double temp;

    //0 ist der vektor vb (geschw der Linie) ist also 0

    public static void checkDirection(int i){
        vrelX = vx - 0;
        vrelY = vy - 0;

        temp = (vrelX/vectorLength(vrelX, vrelY)) * (normX/normalizeNorm) + (vrelY/vectorLength(vrelX, vrelY)) * (normY/normalizeNorm);

        if(temp < 0)
            collision = true;
        else if(temp > 0)
            collision = false;
        else if(temp == 0)
            collision = true;
    }
}
