package com.company;

public class Kugelbahn {
    static double vx, vy, sx, sy, ax, ay;
    static double vel[] = new double[2];
    static double pos[] = new double[2];
    static double gravity[] = new double[2];
    static double wind[] = new double[2];
    static Line lines[] = new Line[3];

    static boolean rollen = false;
    static double d = Double.POSITIVE_INFINITY;

    static double temp;
    static boolean collision = true;
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

    static double angle;    //Auftreffwinkel Gerade und Kugel


    static Line line1 = new Line(10, 30, 10, 10);
    static double m = ((double) line1.getY1() - (double) line1.getY0()) / ((double) line1.getX1() - (double) line1.getX0()); //Steigung der Geraden

    static double heightLoss = 0.4; //Kugel fliegt auch Holzplatte

    //führt alle Berechnungnen in richitger Reihenfolge aus
    public static void calc(double t) {
        calcAcceleration();
        calcVelocity(t);
        calcPosition(t);

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
    static double arx, ary;
    static double ahx, ahy;
    public static void calcAcceleration() {
        if (!rollen) {
            ax = gravity[0] + wind[0];

            ay = gravity[1] + wind[1];

        } else if (rollen) {
            aHang = Math.abs(gravity[1] * Math.sin(Math.atan((m))));
            aNormal = Math.abs(gravity[1] * Math.cos(Math.atan((m))));
            aReib = Math.abs(aNormal) * 0.035;                                //Reifen auf schlechter Straße
            ahx = Math.abs(vProjektionX * aHang);   //Hangabtriebsbeschleunigung
            ahy = Math.abs(vProjektionY * aHang);
            arx = Math.abs(vProjektionX * aReib);   //Reibungsbeschleunigung
            ary = Math.abs(vProjektionY * aReib);

            if (m > 0 && vx < 0) {
                ax = ahx + arx;

                ay = ahy + ary;
            }

            else if (m > 0 && vx > 0) {
                ax = ahx - arx;

                ay = ahy - ary;
            }

            else if (m < 0 && vx > 0) {
                ax = -ahx - arx;

                ay = ahy + ary;
            }

            else if (m < 0 && vx <0){
                ax = -ahx + arx;

                ay = ahy - ary;
            }

            else if (m == 0 && vx < 0){
                ax = ahx + arx;

                ay = ahy - ary;
            }

            else if (m == 0 && vx > 0){
                ax = ahx - arx;

                ay = ahy - ary;
            }

            //Kugel fällt auf eine waagerechte Linie
            else if(m == 0 & vy >= 0){
                ax = ahx - arx;

                ay = ahy - ary;
            }

        }
    }

        public static void updateVelDis () {

            vel[0] = vx;
            vel[1] = vy;

            pos[0] = sx;
            pos[1] = sy;

            Main.updateCoordinate();
            Main.showvel();

        }

        public static void createLines () {

            lines[0] = new Line(24, 60, 30, 50);
            lines[1] = new Line(0, 17, 40, 30);
            lines[2] = new Line(0, Screen.width / Screen.scale, 44, 44);
        }

        static double k;
        public static void collisionCheck () {

            calcDistancePointLine(1);
            checkDirection();
            //System.out.println("Abstand" + d);
            //System.out.println("Steigung" + m);
            //System.out.println("NormalenVektor [" + normX + "] [" + normY + "]" );
            System.out.println("Geschwindigkeit [" + vel[0] + "] [" + vel[1] + "]" );

            //Kugel trifft von der linken Seite aus auf eine Wand
            if (dotP > 0 && !collision && d <= 0.6 && m == Double.POSITIVE_INFINITY && pos[1] >= line1.getY0() && pos[1] <= line1.getY1()){

                vx = -(vx * heightLoss);
                vy = vy * heightLoss;
            }

            //Kugel trifft von der rechten Seite aus auf eine Wand
            if (dotP < 0 && collision && d <= 0.6 && m == Double.POSITIVE_INFINITY && pos[1] >= line1.getY0() && pos[1] <= line1.getY1()){

                vx = -(vx * heightLoss);
                vy = vy * heightLoss;
            }



            //Kugel trifft von oben auf die Gerade
            if (dotP < 0 && collision && !rollen && d <= 0.6 && pos[0] >= line1.getX0() && pos[0] <= line1.getX1()) {
                vectorZerlegung();

                if (m == 0 && vectorLength(tempvParallelX, tempvParallelY) >= 2){
                        vx = vx * heightLoss;
                        vy = -(vy * heightLoss);
                }
                    else {
                        //Annahme: Kugelmasse = 200g -> Fg = 1,962 und die Absprungskraft muss größer als Fg sein
                         if (vectorLength(tempvParallelX, tempvParallelY) <= 2) {
                             vectorZerlegung();
                            System.out.println(vectorLength(tempvParallelX, tempvParallelY));
                            rollen = true;
                            vx = vProjektionX;
                            vy = vProjektionY;
                            System.out.println("Bin am rollen");
                        } else {
                            //https://math.stackexchange.com/questions/3301455/reflect-a-2d-vector-over-another-vector-without-using-normal-why-this-gives-the
                            calcReflectingVector();
                        }
                }
            }

            //Kugel trifft von unten auf die Gerade
            else if (dotP > 0 && !collision && d <= 0.6 && pos[0] >= line1.getX0() && pos[0] <= line1.getX1()) {

                calcReflectingVector();

            }

            else if (rollen && (pos[0] <= line1.getX0() || pos[0] >= line1.getX1())) {
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

        /*
        //Berechnet Winkel zw Normalenvektor der Geraden & Richtungsvektor der Kugel
        public static void calcAngle () {

            ballVekX = -1 * (vx - vel[0]);
            ballVekY = -1 * (vy - vel[1]);

            angle = Math.acos((ballVekX * normX + ballVekY * normY) / (vectorLength(ballVekX, ballVekY) * vectorLength(normX, normY)));

        }
        */


        public static void vectorZerlegung () {
          

            skalar = (vx * (line1.getX1() - line1.getX0()) + vy * (line1.getY1() - line1.getY0())) /
                    (Math.pow((line1.getX1() - line1.getX0()), 2) + Math.pow((line1.getY1() - line1.getY0()), 2));

            //Projektion des Richtungsvektors der Kugel auf die Gerade
            vProjektionX = skalar * (line1.getX1() - line1.getX0());
            vProjektionY = skalar * (line1.getY1() - line1.getY0());

            normalizeProjektion = vectorLength(vProjektionX, vProjektionY);

            //Paralleler Anteil der Zerlegung -> Wird für Entscheidung ob rollen oder springen gebraucht
            tempvParallelX = vx - vProjektionX;
            tempvParallelY = vy - vProjektionY;
            System.out.println();

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
            else
                rollen = true;
        }

        public static void calcReflectingVector(){
            //Faktor für die Projektion des Richtungsvektor der Kugel auf die Gerade
            k = (vx * (line1.getX1() - line1.getX0()) + vy * (line1.getY1() - line1.getY0())) / (Math.pow(line1.getX1() - line1.getX0(), 2) + Math.pow(line1.getY1() - line1.getY0(), 2));

            vx = heightLoss * (-vx + (2*k*(line1.getX1() - line1.getX0())));
            vy = heightLoss * (-vy + (2*k*(line1.getY1() - line1.getY0())));

        }
    }
