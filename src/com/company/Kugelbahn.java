package com.company;

public class Kugelbahn {
    //static double vx, vy, sx, sy, ax, ay;
    static double ax, ay;
    static double vel[] = new double[2];
    static double pos[] = new double[2];
    static double gravity[] = new double[2];
    static double wind[] = new double[2];

    private static double vrelX, vrelY; //Relative Geschwindigkeit der Kugel zu der Geraden

    static double aHang;
    static double aNormal;
    static double aReib;

    static double temp;
    static double dotP;         //Skalarprodukt von (Kugel zu Linie & Normalenvektor der Geraden) zum schauen, ob Kugel über oder unterhalb der Linie liegt
    static double normX, normY; //Richtungsvektor der Geraden
    static double normalizeNorm; //laenge des Normalenvektors zur Normierung
    static double richtungX, richtungY; //Vektor von Kugel zur Geraden

    static double vProjektionX; //Projektion vom Richtungsvektor der Kugel auf Gerade
    static double vProjektionY;
    static double skalar;       //Faktor zur Berechnung von Projektion
    static double vParallelX, vParallelY;   //paraleler Anteil
    static double tempvParallelX, tempvParallelY;
    static double normalizeProjektion;

    static double arx, ary;
    static double ahx, ahy;
    //static int rollOn; //Linie auf der die Kugel rollt

    static int closest;

    //static boolean rollen = false;
    //static boolean collision = true;

    static double d = Double.POSITIVE_INFINITY;
    static double heightLoss = 0.4; //Kugel fliegt auch Holzplatte

    //führt alle Berechnungnen in richitger Reihenfolge aus
    public static void calc(double t) {
        for(int i = 0; i<Screen.ball.length; i++) {
            calcAcceleration(i);
            calcVelocity(t, i);
            calcPosition(t, i);
            if(Screen.ball.length > 1) {
                for (int k = 0; k < Screen.ball.length; k++) {
                    for (int l = k + 1; l < Screen.ball.length; l++) {
                        collisionCheckBallBall(k, l);
                    }
                }
            }
            for(int j = 0; j<Screen.lines.length; j++) {
                collisionCheckBallLine(j, i);
            }
            updateVelDis(i);
        }

        //updateVelDis();
    }

    public static void calcVelocity(double t, int ballID) {
        Screen.ball[ballID].setVelX_new(Screen.ball[ballID].getVelX() + ax * t);

        Screen.ball[ballID].setVelY_new(Screen.ball[ballID].getVelY() + ay * t);

    }


    public static void calcPosition(double t, int ballID) {
        Screen.ball[ballID].setPosX_new(Screen.ball[ballID].getPosX() + Screen.ball[ballID].getVelX() * t + (0.5 * ax * Math.pow(t, 2)));

        Screen.ball[ballID].setPosY_new(Screen.ball[ballID].getPosY() + Screen.ball[ballID].getVelY() * t + (0.5 * ay * Math.pow(t, 2)));

    }

    public static void calcAcceleration(int ballID) {
        if (!Screen.ball[ballID].isRollen()) {
            ax = gravity[0] + wind[0];

            ay = gravity[1] + wind[1];

        } else if (Screen.ball[ballID].isRollen()) {

            double m = ((double) Screen.lines[Screen.ball[ballID].getRollOn()].getY1() - (double) Screen.lines[Screen.ball[ballID].getRollOn()].getY0()) / ((double) Screen.lines[Screen.ball[ballID].getRollOn()].getX1() - (double) Screen.lines[Screen.ball[ballID].getRollOn()].getX0());

            aHang = Math.abs(gravity[1] * Math.sin(Math.atan((m))));
            aNormal = Math.abs(gravity[1] * Math.cos(Math.atan((m))));
            aReib = Math.abs(aNormal) * 0.035;//Reifen auf schlechter Straße

            //Verhindert das Teilen durch 0, welches bei einer Steigung von 0 Auftritt fuer (vProjektionX/normalizeProjektion) berechnung
            if(m == 0){
                ahx = Math.abs(0 * aHang);
                arx = Math.abs(aReib);

                ahy = Math.abs(0 * aHang);
                ary = Math.abs(0 * aReib);

            }
            else{
                ahx = Math.abs((Screen.ball[ballID].getvProjektionX()/(vectorLength(Screen.ball[ballID].getvProjektionX(), Screen.ball[ballID].getvProjektionY())) * aHang));   //Hangabtriebsbeschleunigung
                arx = Math.abs((Screen.ball[ballID].getvProjektionX()/(vectorLength(Screen.ball[ballID].getvProjektionX(), Screen.ball[ballID].getvProjektionY())) * aReib));

                ahy = Math.abs((Screen.ball[ballID].getvProjektionY()/(vectorLength(Screen.ball[ballID].getvProjektionX(), Screen.ball[ballID].getvProjektionY())) * aHang));;
                ary = Math.abs((Screen.ball[ballID].getvProjektionY()/(vectorLength(Screen.ball[ballID].getvProjektionX(), Screen.ball[ballID].getvProjektionY())) * aReib));
            }

            if (m > 0 && Screen.ball[ballID].getVelX_new() < 0) {
                ax = ahx + arx;

                ay = ahy + ary;
            }

            else if (m > 0 && Screen.ball[ballID].getVelX_new() > 0) {
                ax = ahx - arx;

                ay = ahy - ary;
            }

            else if (m < 0 && Screen.ball[ballID].getVelX_new() > 0) {
                ax = -ahx - arx;

                ay = ahy + ary;
            }

            else if (m < 0 && Screen.ball[ballID].getVelX_new() <0){
                ax = -ahx + arx;

                ay = ahy - ary;
            }

            else if (m == 0 && Screen.ball[ballID].getVelX_new() < 0){
                ax = ahx + arx;

                ay = ahy - ary;
            }
            else if (m == 0 && Screen.ball[ballID].getVelX_new() > 0){
                ax = ahx - arx;

                ay = ahy - ary;
            }

            //Kugel fällt senkrecht auf Gerade
            else if (m == 0 && Screen.ball[ballID].getVelX_new() == 0){
                ax = ahx + arx;

                ay = 0;
            }
        }
    }

        public static void updateVelDis (int ballID) {

            Screen.ball[ballID].setVelX(Screen.ball[ballID].getVelX_new());
            Screen.ball[ballID].setVelY(Screen.ball[ballID].getVelY_new());

            Screen.ball[ballID].setPosX(Screen.ball[ballID].getPosX_new());
            Screen.ball[ballID].setPosY(Screen.ball[ballID].getPosY_new());

            Main.updateCoordinate();
        }

        static double k;
        public static void collisionCheckBallLine (int i, int ballID) {

                calcDistancePointLine(i, ballID);
                checkDirection(ballID);

            //System.out.println("Kugel 1 ist am rollen" + Screen.ball[1].isRollen());
            double m = ((double) Screen.lines[closest].getY1() - (double) Screen.lines[closest].getY0()) / ((double) Screen.lines[closest].getX1() - (double) Screen.lines[closest].getX0());

            //System.out.println("Abstand" + d);
            //System.out.println("Steigung" + m);
            //System.out.println("NormalenVektor [" + normX + "] [" + normY + "]" );
            //System.out.println("Geschwindigkeit [" + Screen.ball[0].getVelX_new() + "] [" + Screen.ball[0].getVelY_new() + "]" );
            //System.out.println("Die nähste linie ist: " + closest);
            System.out.println("Projektionsvektor von  " +ballID +"    "+ Screen.ball[ballID].getvProjektionX() + "|" + Screen.ball[ballID].getvProjektionY() );
            //System.out.println(Screen.ball[1].getVelX() + " | " + Screen.ball[1].getVelY());
            //System.out.println(Screen.ball[0].getVelX() + " | " + Screen.ball[0].getVelY());
            System.out.println(Screen.ball[ballID].isRollen());

            //Kugel trifft von der linken Seite aus auf eine Wand
            /*
            if (dotP > 0 && !collision && !rollen && d_closest <= 0.6 && pos[1] >= Screen.lines[closest].getY0() && pos[1] <= Screen.lines[closest].getY1()){

                vx = -(vx * heightLoss);
                vy = vy * heightLoss;
            }

            //Kugel trifft von der rechten Seite aus auf eine Wand
            if (dotP < 0 && collision && !rollen && d_closest <= 0.6 && pos[1] >= Screen.lines[closest].getY0() && pos[1] <= Screen.lines[closest].getY1()){

                vx = -(vx * heightLoss);
                vy = vy * heightLoss;
            }
            */


            //Kugel befindest sich oberhalb der Geraden, Normalenvektor der geraden & Richtungsvektor der Kugel gehen aufeinander zu, Kugel befindet sich zwischen Start / Endpunkt der Geraden
            if (!Screen.ball[ballID].isRollen() && dotP < 0 && Screen.ball[ballID].isCollision() && d <= 0.6 && Screen.ball[ballID].getPosX() >= Screen.lines[closest].getX0() && Screen.ball[ballID].getPosX() <= Screen.lines[closest].getX1()) {
                vectorZerlegung_BallLine(closest, ballID);
                System.out.println("Ich sollte hier nicht sein");
                //Abspringen auf einer waagerechten Gerade
                if (m == 0 && vectorLength(tempvParallelX, tempvParallelY) >= 2){
                    Screen.ball[ballID].setVelX_new( Screen.ball[ballID].getVelX_new() * heightLoss );
                    Screen.ball[ballID].setVelY_new( -1*(Screen.ball[ballID].getVelY_new() * heightLoss) );
                }
                    else {
                        //Annahme: Kugelmasse = 200g -> Fg = 1,962 und die Absprungskraft muss größer als Fg sein
                         if (vectorLength(tempvParallelX, tempvParallelY) <= 2) {
                            System.out.println(vectorLength(tempvParallelX, tempvParallelY));
                             Screen.ball[ballID].setRollen(true);
                             Screen.ball[ballID].setRollOn(closest);
                             Screen.ball[ballID].setVelX_new(vProjektionX);
                             Screen.ball[ballID].setVelY_new(vProjektionY);
                            //System.out.println("Bin am rollen");
                            //fixClipping(Screen.ball[ballID].getRollOn(), ballID);
                        } else {
                            //https://math.stackexchange.com/questions/3301455/reflect-a-2d-vector-over-another-vector-without-using-normal-why-this-gives-the
                            calcReflectingVector(closest, ballID);
                        }
                }
            }

            //Kugel trifft von unten auf die Gerade
            else if (dotP > 0 && !Screen.ball[ballID].isCollision() && d <= 0.6 && Screen.ball[ballID].getPosX() >= Screen.lines[closest].getX0() && Screen.ball[ballID].getPosX() <= Screen.lines[closest].getX1()) {

                calcReflectingVector(closest, ballID);
            }

            //Während die Kugel am Rollen ist & andere Geraden nah genug kommen (außer die Gerade, auf der die Kugel rollt) wird ein neues Handling eingebracht
            // Rollen wird temporär auf false gesetzt, damit der Collisioncheck wieder durchgeführt werden kann
            else if(Screen.ball[ballID].isRollen() && i!=Screen.ball[ballID].getRollOn() && d < 0.6 && dotP <0 && Screen.ball[ballID].getPosX() >= Screen.lines[closest].getX0() && Screen.ball[ballID].getPosX() <= Screen.lines[closest].getX1()){
                Screen.ball[ballID].setRollen(false);
                System.out.println("pog");
                if (vectorLength(tempvParallelX, tempvParallelY) <= 2) {
                    Screen.ball[ballID].setRollen(true);
                    Screen.ball[ballID].setRollOn(closest);
                    Screen.ball[ballID].setVelX_new(vProjektionX);
                    Screen.ball[ballID].setVelY_new(vProjektionY);
                    //System.out.println("Bin am rollen" + rollOn);
                    //fixClipping(Screen.ball[ballID].getRollOn(), ballID);
                }
                else {
                    calcReflectingVector(i, ballID);
                }
            }

            //Schaut, ob die Kugel noch auf der Geraden rollt
            //calcDistancePointLine(Screen.ball[ballID].getRollOn(), ballID);

            if (Screen.ball[ballID].isRollen() && (Screen.ball[ballID].getPosX() <= Screen.lines[Screen.ball[ballID].getRollOn()].getX0() || Screen.ball[ballID].getPosX() >= Screen.lines[Screen.ball[ballID].getRollOn()].getX1())) {
                Screen.ball[ballID].setRollen(false);
                //System.out.println("fliege wieder");
            }
        }


        public static void calcDistancePointLine (int i, int ballID){

            //Vektor zwischen Ball zur Geraden
            richtungX = Screen.lines[i].getX0() - Screen.ball[ballID].getPosX();
            richtungY = Screen.lines[i].getY0() - Screen.ball[ballID].getPosY();

            //Normalen vektor der Geraden
            normX = (Screen.lines[i].getY1() - Screen.lines[i].getY0());
            normY = -1 * (Screen.lines[i].getX1() - Screen.lines[i].getX0());

            normalizeNorm = vectorLength(normX, normY);

            dotP = richtungX * normX + richtungY * normY;

            d = Math.abs(richtungX * normX + richtungY * normY ) / Math.sqrt(Math.pow(normX, 2) + Math.pow(normY, 2)) - Screen.radius/Screen.scale  ;
            System.out.println(d + "auf" + i);
            //Schaut ob eine Linie nah genug dran ist, dessen nummer wird gespeichert und wird im collisionChecker geprüft
            if(d < 1){
                    closest = i;
                }
            /*
            if (dotP > 0)
                System.out.println("Der Punkt liegt unter der Linie");
            else if (dotP < 0)
                System.out.println("Der Punkt liegt über der Linie");
            else
                System.out.println("Der Punkt liegt genau auf der Linie");
            */
        }
        static double d_balls;
        public static void calcDistancePointPoint(int ball1, int ball2){
            d_balls = Math.sqrt( Math.pow(Screen.ball[ball2].getPosX_new() - Screen.ball[ball1].getPosX_new(),2) + Math.pow(Screen.ball[ball2].getPosY() - Screen.ball[ball1].getPosY(),2) );
            //System.out.println(d_balls);

        }

        public static void collisionCheckBallBall(int ball1, int ball2){
            calcDistancePointPoint(ball1, ball2);
            vectorZerlegung_BallBall(ball1, ball2);

            if(d_balls <= (Screen.diameter/Screen.scale) ) {

                System.out.println("Elastischer Stoß");
                double m = Screen.ball[ball1].getWeight() + Screen.ball[ball2].getWeight();

                double vx1_new = 2 * ( (Screen.ball[ball1].getWeight() * Screen.ball[ball1].getVelX_new() + Screen.ball[ball2].getWeight() * Screen.ball[ball2].getVelX_new() ) / m ) - Screen.ball[ball1].getVelX_new();

                double vy1_new = 2 * ( (Screen.ball[ball1].getWeight() * Screen.ball[ball1].getVelY_new() + Screen.ball[ball2].getWeight() * Screen.ball[ball2].getVelY_new() ) / m ) - Screen.ball[ball1].getVelY_new();

                double vx2_new = 2 * ( (Screen.ball[ball1].getWeight() * Screen.ball[ball1].getVelX_new() + Screen.ball[ball2].getWeight() * Screen.ball[ball2].getVelX_new() ) / m ) - Screen.ball[ball2].getVelX_new();

                double vy2_new = 2 * ( (Screen.ball[ball1].getWeight() * Screen.ball[ball1].getVelY_new() + Screen.ball[ball2].getWeight() * Screen.ball[ball2].getVelY_new() ) / m ) - Screen.ball[ball2].getVelY_new();

                Screen.ball[ball1].setVelX_new(vx1_new);
                Screen.ball[ball1].setVelY_new(vy1_new);

                Screen.ball[ball2].setVelX_new(vx2_new);
                Screen.ball[ball2].setVelY_new(vy2_new);

                double offset = Math.abs(d_balls - Screen.diameter/Screen.scale) / 2;

                Screen.ball[ball1].setPosX_new( Screen.ball[ball1].getPosX_new() + offset * x_line );
                Screen.ball[ball1].setPosY_new( Screen.ball[ball1].getPosY_new() + offset * y_line );


                updateVelDis(ball1);
                updateVelDis(ball2);

                //System.out.println();
                ballBounce = false;

                Screen.ball[ball1].setRollen(false);
                Screen.ball[ball2].setRollen(false);

                for(int j = 0; j<Screen.lines.length; j++) {
                    collisionCheckBallLine(j, ball1);
                    collisionCheckBallLine(j, ball2);
                }

            }
        }


        public static void vectorZerlegung_BallLine(int j, int ballID) {

            skalar = (Screen.ball[ballID].getVelX_new() * (Screen.lines[j].getX1() - Screen.lines[j].getX0()) + Screen.ball[ballID].getVelY_new() * (Screen.lines[j].getY1() - Screen.lines[j].getY0())) /
                    (Math.pow((Screen.lines[j].getX1() - Screen.lines[j].getX0()), 2) + Math.pow((Screen.lines[j].getY1() - Screen.lines[j].getY0()), 2));

            //Projektion des Richtungsvektors der Kugel auf die Gerade
            vProjektionX = skalar * (Screen.lines[j].getX1() - Screen.lines[j].getX0());
            vProjektionY = skalar * (Screen.lines[j].getY1() - Screen.lines[j].getY0());
            Screen.ball[ballID].setvProjektionX(vProjektionX);
            Screen.ball[ballID].setvProjektionY(vProjektionY);
            System.out.println();
            normalizeProjektion = vectorLength(vProjektionX, vProjektionY);

            //Paralleler Anteil der Zerlegung -> Wird für Entscheidung ob rollen oder springen gebraucht
            tempvParallelX = Screen.ball[ballID].getVelX_new() - vProjektionX;
            tempvParallelY = Screen.ball[ballID].getVelY_new() - vProjektionY;

        }
        static boolean ballBounce = true;
        static double x_line, y_line;
        public static void vectorZerlegung_BallBall(int ball_a, int ball_b){
            //Vektor zerlegung für Kugel 1
            //System.out.println();
            x_line = Screen.ball[ball_a].getPosX() - Screen.ball[ball_b].getPosX();
            y_line = Screen.ball[ball_a].getPosY() - Screen.ball[ball_b].getPosY();
            double skalar1 =  (Screen.ball[ball_a].getVelX_new() * x_line + Screen.ball[ball_a].getVelY_new() * y_line ) / ( Math.pow(x_line, 2) + Math.pow(y_line,2 ) );
            double projA_x = skalar1 * x_line;
            double projA_y = skalar1 * y_line;
            //double parallelA_x = Screen.ball[ball_a].getVelX_new() - projA_x;
            //double parallelA_y = Screen.ball[ball_a].getVelY_new() - projA_y;

            //Vektor Zerlegung für Kugel 2
            double skalar2 =  (Screen.ball[ball_b].getVelX_new() * x_line + Screen.ball[ball_b].getVelY_new() * y_line ) / ( Math.pow(x_line, 2) + Math.pow(y_line,2 ) );
            double projB_x = skalar2 * x_line;
            double projB_y = skalar2 * y_line;
            //double parallelB_x = Screen.ball[ball_b].getVelX_new() - projB_x;
            //double parallelB_y = Screen.ball[ball_b].getVelY_new() - projB_y;

            /*
            double dotP = projA_x * projB_x + projA_y * projB_y;
            if(dotP > 0)
                ballBounce = true;
            else
                ballBounce = false;
            */

        }

        public static double vectorLength ( double x, double y){

            return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
        }

        //Schaut ob richtungsvektor Kugel & Geraden aufeinader zu laufen
        public static void checkDirection (int ballID) {
            vrelX = Screen.ball[ballID].getVelX_new() - 0;
            vrelY = Screen.ball[ballID].getVelY_new() - 0;

            temp = (vrelX / vectorLength(vrelX, vrelY)) * (normX / normalizeNorm) + (vrelY / vectorLength(vrelX, vrelY)) * (normY / normalizeNorm);

            if (temp < 0)
                Screen.ball[ballID].setCollision(true);
            else if (temp > 0)
                Screen.ball[ballID].setCollision(false);
            else
                Screen.ball[ballID].setRollen(true);
        }

        public static void calcReflectingVector(int l, int ballID){
            //Faktor für die Projektion des Richtungsvektor der Kugel auf die Gerade
            k = (Screen.ball[ballID].getVelX_new() * (Screen.lines[l].getX1() - Screen.lines[l].getX0()) + Screen.ball[ballID].getVelY_new() * (Screen.lines[l].getY1() - Screen.lines[l].getY0())) / (Math.pow(Screen.lines[l].getX1() - Screen.lines[l].getX0(), 2) + Math.pow(Screen.lines[l].getY1() - Screen.lines[l].getY0(), 2));

            Screen.ball[ballID].setVelX_new( heightLoss * (-Screen.ball[ballID].getVelX_new() + (2*k*(Screen.lines[l].getX1() - Screen.lines[l].getX0()))) );
            Screen.ball[ballID].setVelY_new( heightLoss * (-Screen.ball[ballID].getVelY_new() + (2*k*(Screen.lines[l].getY1() - Screen.lines[l].getY0()))) );
        }

        static double fix;
        public static void fixClipping(int rollOn, int ballID){
            double m = ((double) Screen.lines[rollOn].getY1() - (double) Screen.lines[rollOn].getY0()) / ((double) Screen.lines[rollOn].getX1() - (double) Screen.lines[rollOn].getX0());
            //Schauen ob Kugel ober oder unter Linie liegt

            fix = Math.abs(d - 0.5);
            if(dotP <0 && d > 0.5) {
                Screen.ball[ballID].setVelX_new(Screen.ball[ballID].getVelX_new() - (Math.sin(Math.atan(m)) * fix));
                Screen.ball[ballID].setVelY_new(Screen.ball[ballID].getVelY_new() - (Math.cos(Math.atan(m)) * fix));
            }
        }
    }
