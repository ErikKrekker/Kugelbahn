package com.company;

public class Kugelbahn {
    static double v0, s0, vx, vy, sx, sy, ax, ay;
    static double vel[] = new double[2];
    static double pos[] = new double[2];
    static double gravity[] = new double[2];
    static double wind[] = new double[2];
    static Line lines[] = new Line[3];

    static boolean falling = true;
    static boolean rollen = false;
    static double d = Double.POSITIVE_INFINITY;

    static double heightLoss = 0.47;

    static double temp;
    static boolean collision = true;
    static double m;    //Steigung der Geraden
    private static double vrelX, vrelY; //Relative Geschwindigkeit der Kugel zu der Geraden

    static double aHang;
    static double aNormal;
    static double aReib;

    static double dotP;         //Skalarprodukt von (Kugel zu Linie & Normalenvektor der Geraden) zum schauen, ob Kugel über oder unterhalb der Linie liegt
    static double normX, normY; //Richtungsvektor der Geraden
    static double normalizeNorm; //laenge des Normalenvektors zur Normierung
    static double richtungX, richtungY; //Vektor von Kugel zur Geraden
    static double normalizeRichtung;    //laenge des ^ Vektors

    static double vProjektionX; //Projektion vom Richtungsvektor der Kugel auf Gerade
    static double vProjektionY;
    static double skalar;       //Faktor zur Berechnung von Projektion
    static double vParallelX, vParallelY;   //paraleler Anteil
    static double tempvParallelX, tempvParallelY;
    static double normalizeProjektion;

    static double ballVekX; //Richtungsvektor der Kugel
    static double ballVekY; //

    static double angle;    //Auftreffwinkel Gerade und Kugel


    static Line line1 = new Line(5, 30, 20, 30);

    //führt alle Berechnungnen in richitger Reihenfolge aus
    public static void calc(double t) {

        calcAcceleration();
        calcVelocity(t);
        calcPosition(t);

        checkDirection();

        m = ((double) line1.getY1() - (double) line1.getY0()) / ((double) line1.getX1() - (double) line1.getX0());

        collisionCheck();
        updateVelDis();
    }

    public static void calcVelocity(double t) {
        vx = vel[0] + ax * t;

        vy = vel[1] + ay * t;
    }


    public static void calcPosition(double t) {
        sx = pos[0] + vel[0] * t + (0.5 * ax * Math.pow(t, 2));

        sy = pos[1] + vel[1] * t + (0.5 * ay * Math.pow(t, 2));

    }

    public static void calcAcceleration() {
        if (!rollen) {
            ax = gravity[0] + wind[0];

            ay = gravity[1] + wind[1];
        } else if (rollen) {
            aHang = Math.abs(gravity[1]) * Math.sin(Math.atan((m)));
            aNormal = Math.abs(gravity[1]) * Math.cos(Math.atan((m)));
            aReib = Math.abs(aNormal) * 0.15;
            System.out.println();
            if (aHang > aReib) {
                ax = (vProjektionX / normalizeProjektion) * aHang;

                ay = (vProjektionY / normalizeProjektion) * aHang;
            }
            else {
                ax = -(vProjektionX / normalizeProjektion) * aHang;

                ay = -(vProjektionY / normalizeProjektion) * aHang;

            }
        }
    }

        public static void updateVelDis () {

            vel[0] = vx;
            vel[1] = vy;

            pos[0] = sx;
            pos[1] = sy;

            Main.updateCoordinate();

        }

        public static void createLines () {

            lines[0] = new Line(24, 60, 30, 50);
            lines[1] = new Line(0, 17, 40, 30);
            lines[2] = new Line(0, Screen.width / Screen.scale, 44, 44);
        }


        public static void collisionCheck () {

            calcDistancePointLine(1);

            //Kugel trifft von oben auf die Gerade
            if (dotP < 0 && collision && !rollen && d <= 0.5 && pos[0] >= line1.getX0() && pos[0] <= line1.getX1()) {
                calcAngle();
                vectorZerlegung();

                if (vectorLength(vParallelX, vParallelY) <= 0.5) {
                    System.out.println(vectorLength(vParallelX, vParallelY));
                    rollen = true;
                    vx = (vProjektionX / normalizeProjektion);
                    vy = (vProjektionY / normalizeProjektion);
                    System.out.println("Bin am rollen");
                } else {
                    vy = -(heightLoss * (vy + 2 * (vy * (normX / normalizeNorm) + vx * (normY / normalizeNorm)) * (normX / normalizeNorm)));
                    vx = -(heightLoss * (vx - 2 * (vy * (normX / normalizeNorm) + vx * (normY / normalizeNorm)) * (normY / normalizeNorm)));

                }
            }
            //Kugel trifft von unten auf die Gerade
            else if (dotP > 0 && !collision && d <= 0.5 && pos[0] >= line1.getX0() && pos[0] <= line1.getX1()) {

                vy = -(heightLoss * (vy + 2 * (vy * (normX / normalizeNorm) + vx * (normY / normalizeNorm)) * (normX / normalizeNorm)));
                vx = -(heightLoss * (vx - 2 * (vy * (normX / normalizeNorm) + vx * (normY / normalizeNorm)) * (normY / normalizeNorm)));
            } else if (rollen && (pos[0] <= line1.getX0() || pos[0] >= line1.getX1())) {
                rollen = false;
                System.out.println("fliege wieder");
            }
        }


        public static void calcDistancePointLine ( int i){

            //Vektor zwischen Ball zur Geraden
            richtungX = line1.getX0() - pos[0];
            richtungY = line1.getY0() - pos[1];

            //Normalen vektor der Geraden
            normX = (line1.getY1() - line1.getY0());
            normY = -1 * (line1.getX1() - line1.getX0());

            normalizeRichtung = vectorLength(richtungX, richtungY);
            normalizeNorm = vectorLength(normX, normY);

            dotP = richtungX * normX + richtungY * normY;

            d = Math.abs(((richtungX * normX + richtungY * normY) / Math.sqrt(Math.pow(normX, 2) + Math.pow(normY, 2))) - Screen.radius / Screen.scale);

            if (dotP > 0)
                System.out.println("Der Punkt liegt unter der Linie");
            else if (dotP < 0)
                System.out.println("Der Punkt liegt über der Linie");
            else
                System.out.println("Der Punkt liegt genau auf der Linie");

        }

        //Berechnet Winkel zw Normalenvektor der Geraden & Richtungsvektor der Kugel
        public static void calcAngle () {

            ballVekX = -1 * (vx - vel[0]);
            ballVekY = -1 * (vy - vel[1]);

            angle = Math.acos((ballVekX * normX + ballVekY * normY) / (vectorLength(ballVekX, ballVekY) * vectorLength(normX, normY)));

            System.out.println("Winkel: " + angle);
        }

        public static void vectorZerlegung () {

            skalar = ((-ballVekX) * (line1.getX1() - line1.getX0()) + (-ballVekY) * (line1.getY1() - line1.getY0())) / (Math.pow((line1.getX1() - line1.getX0()), 2) + Math.pow((line1.getY1() - line1.getY0()), 2));

            //Projektion des Richtungsvektors auf den Richtungsvektor der Geraden
            vProjektionX = skalar * (line1.getX1() - line1.getX0());
            vProjektionY = skalar * (line1.getY1() - line1.getY0());

            normalizeProjektion = vectorLength(vProjektionX, vProjektionY);

            //Paralleler Anteil der Zerlegung -> Wird für Entscheidung ob rollen oder springen gebraucht
            tempvParallelX = -(ballVekX) - vProjektionX;
            tempvParallelY = -(ballVekY) - vProjektionY;

            vParallelX = vel[0] * (tempvParallelX / (vectorLength(tempvParallelX, tempvParallelY)));
            vParallelY = vel[1] * (tempvParallelY / (vectorLength(tempvParallelX, tempvParallelY)));
        }

        public static double vectorLength ( double x, double y){

            return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
        }

        //Schaut ob richtungsvektor Kugel & Geraden aufeinader zu laufen
        public static void checkDirection () {
            vrelX = vx - 0;
            vrelY = vy - 0;

            temp = (vrelX / vectorLength(vrelX, vrelY)) * (normX / normalizeNorm) + (vrelY / vectorLength(vrelX, vrelY)) * (normY / normalizeNorm);

            if (temp < 0)
                collision = true;
            else if (temp > 0)
                collision = false;
            else if (temp == 0)
                collision = true;
        }
    }
