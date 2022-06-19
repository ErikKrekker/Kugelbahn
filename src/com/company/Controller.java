package com.company;

public class Controller {
    //Variablen - Beschleunigen
    static double ax, ay;                               //Beschleunigungen -> werden zum updaten gebraucht
    static double[] gravity = new double[2];            //Gravitation
    static double[] wind = new double[2];               //Wind

    static double aHang;                                //Hangabtriebsbeschleunigung
    static double aNormal;                              //Normalenbeschleunigung
    static double aReib;                                //Reibung
    static double arx, ary;                             //Reibungsbeschleunigung zur Berechnung
    static double ahx, ahy;                             //Hangabtriebsbeschleunigung zur Berechnung
    static double a_magnet1, a_magnet2;                 //Beschleunigungen der beiden Magneten

    //Variablen - Kollisionserkennung
    static int closest;                                 //Linie, die am nah zur Kugel ist -> beim Handling genutzt
    static double dotP;                                 //Skalarprodukt von (Kugel zu Linie & Normalenvektor der Geraden) -> schaut ob Kugel ober oder unter der Linie liegt
    static double normX, normY;                         //Normalenvektor der Geraden (zeigt immer noch oben)
    static double normalizeNorm;                        //Normalisierung des Normalenvektors
    static double richtungX, richtungY;                 //Richtungsvektor von Kugel zur Geraden
    static double d_balls;                              //Abstand von 2 Kugeln zueinander
    static double d = Double.POSITIVE_INFINITY;         //Abstand zur aktuell betrachteten Linie

    //Variablen - Kollisionshandling
    static double skalar;                               //Skalar zur Projektion des Richtungsvektor der Kugel auf die Gerade
    static double vProjektionX, vProjektionY;           //Projektion vom Richtungsvektor der Kugel auf Gerade
    static double normalizeProjektion;                  //Normalisierung des Projektionsvektor
    static double tempvParallelX, tempvParallelY;       //Paralleler Anteil (zum Normalenvektor) der Projektion
    static double x_line, y_line;                       //Verbindungsvektor der beiden Magnetkugeln

    //Variablen - Einstellbar
    static double heightLoss = 0.38;                    //Verlust beim Aufprall -> Kugel fliegt auch Holzplatte -> http://docplayer.org/72996896-Die-hyperaktive-kugel-wann-hoert-sie-endlich-auf-zu-springen.html
    static double detectionThreshold = 0.6;             //"Buffer" zur Abstandserkennung Kugel mit Linie
    static double buffer = 0.25;                        //"Buffer" der Grenzen der Linie (wichitg bei senkrechten Linien)



    //Ablauf der Kugelbahn
    public static void orderOfOperation(double t) {
        for (int i = 0; i < Screen.ball.length; i++) {
            calcAcceleration(i);
            calcVelocity(t, i);
            calcPosition(t, i);

            if (Screen.ball.length > 1) {
                for (int k = 0; k < Screen.ball.length; k++) {
                    for (int l = k + 1; l < Screen.ball.length; l++) {
                        collisionCheckPointPoint(k, l);
                    }
                }
            }

            for (int j = 0; j < Screen.lines.length; j++) {
                collisionCheckPointLine(j, i);
            }
            updateVelocityPosition(i);
        }
    }

    // Methoden zur Berechnung von Geschwindigkeit / Position / Beschleunigung / Upadate der Werte

    // v = v0 + a*t
    public static void calcVelocity(double t, int ballID) {
        if (Screen.ball[ballID].isMovable()) {
            Screen.ball[ballID].setVelX_new(Screen.ball[ballID].getVelX() + ax * t);

            Screen.ball[ballID].setVelY_new(Screen.ball[ballID].getVelY() + ay * t);
        }
    }

    // s = s0 + v*t + 0.5*a*t^2
    public static void calcPosition(double t, int ballID) {
        if (Screen.ball[ballID].isMovable()) {
            Screen.ball[ballID].setPosX_new(Screen.ball[ballID].getPosX() + Screen.ball[ballID].getVelX() * t + (0.5 * ax * Math.pow(t, 2)));

            Screen.ball[ballID].setPosY_new(Screen.ball[ballID].getPosY() + Screen.ball[ballID].getVelY() * t + (0.5 * ay * Math.pow(t, 2)));
        }
    }

    public static void calcAcceleration(int ballID) {
        //Beschleunigung freier Fall
        if (!Screen.ball[ballID].isRollen() && Screen.ball[ballID].isMovable() && !Screen.ball[ballID].isMagnet()) {
            ax = gravity[0] + wind[0];

            ay = gravity[1] + wind[1];

          //Beschleunigung Kugel rollt auf Linie inkl. Reibung ohne Wind
        } else if (Screen.ball[ballID].isRollen() && Screen.ball[ballID].isMovable() && !Screen.ball[ballID].isMagnet()) {
            double m = ((double) Screen.lines[Screen.ball[ballID].getRollOn()].getY1() - (double) Screen.lines[Screen.ball[ballID].getRollOn()].getY0()) / ((double) Screen.lines[Screen.ball[ballID].getRollOn()].getX1() - (double) Screen.lines[Screen.ball[ballID].getRollOn()].getX0());

            aHang = Math.abs(gravity[1] * Math.sin(Math.atan((m))));
            aNormal = Math.abs(gravity[1] * Math.cos(Math.atan((m))));
            aReib = Math.abs(aNormal) * 0.035;                          //Reifen auf schlechter Straße

            //Verhindert das Teilen durch 0, welches bei einer Steigung von 0 Auftritt bei der (vProjektionX/normalizeProjektion) berechnung
            if (m == 0) {
                ahx = Math.abs(0 * aHang);
                arx = Math.abs(aReib);

                ahy = Math.abs(0 * aHang);
                ary = Math.abs(0 * aReib);

            } else {
                ahx = Math.abs((Screen.ball[ballID].getvProjektionX() / (vectorLength(Screen.ball[ballID].getvProjektionX(), Screen.ball[ballID].getvProjektionY())) * aHang));
                arx = Math.abs((Screen.ball[ballID].getvProjektionX() / (vectorLength(Screen.ball[ballID].getvProjektionX(), Screen.ball[ballID].getvProjektionY())) * aReib));

                ahy = Math.abs((Screen.ball[ballID].getvProjektionY() / (vectorLength(Screen.ball[ballID].getvProjektionX(), Screen.ball[ballID].getvProjektionY())) * aHang));
                ary = Math.abs((Screen.ball[ballID].getvProjektionY() / (vectorLength(Screen.ball[ballID].getvProjektionX(), Screen.ball[ballID].getvProjektionY())) * aReib));
            }

            if (m > 0 && Screen.ball[ballID].getVelX_new() < 0) {
                ax = ahx + arx;

                ay = ahy + ary;
            } else if (m > 0 && Screen.ball[ballID].getVelX_new() > 0) {
                ax = ahx - arx;

                ay = ahy - ary;
            } else if (m < 0 && Screen.ball[ballID].getVelX_new() > 0) {
                ax = -ahx - arx;

                ay = ahy + ary;
            } else if (m < 0 && Screen.ball[ballID].getVelX_new() < 0) {
                ax = -ahx + arx;

                ay = ahy - ary;
            } else if (m == 0 && Screen.ball[ballID].getVelX_new() < 0) {
                ax = ahx + arx;

                ay = ahy - ary;
            } else if (m == 0 && Screen.ball[ballID].getVelX_new() > 0) {
                ax = ahx - arx;

                ay = ahy - ary;
            }

            //Kugel trifft senkrecht auf Linie
            else if (m == 0 && Screen.ball[ballID].getVelX_new() == 0) {
                ax = ahx + arx;

                ay = 0;
            }
          //Beschleunnigung der beiden Magneten aufeinadner zu
        } else if (Screen.ball[ballID].isMagnet() && Screen.ball[ballID].isMovable()) {

            double path_x = Screen.ball[2].getPosX() - Screen.ball[3].getPosX();
            double path_y = Screen.ball[2].getPosY() - Screen.ball[3].getPosY();

            double normPath = Math.sqrt(Math.pow(path_x, 2) + Math.pow(path_y, 2));

            magneticPull(2, 3);

            if (ballID == 2) {
                //ax = a_magnet1 * -1 * (path_x / normPath);
                //ay = a_magnet1 * -1 * (path_y / normPath);
                ax = 0;
                ay = 0;

            }

            if (ballID == 3) {
                ax = a_magnet2 * (path_x / normPath);
                ay = a_magnet2 * (path_y / normPath);
            }

        }
    }

    public static void updateVelocityPosition(int ballID) {

        if (Screen.ball[ballID].isMovable()) {
            Screen.ball[ballID].setVelX(Screen.ball[ballID].getVelX_new());
            Screen.ball[ballID].setVelY(Screen.ball[ballID].getVelY_new());

            Screen.ball[ballID].setPosX(Screen.ball[ballID].getPosX_new());
            Screen.ball[ballID].setPosY(Screen.ball[ballID].getPosY_new());
        }
        Main.updateCoordinate();
    }

    //Collision Handler Kugel mit Linie
    public static void collisionCheckPointLine(int lineID, int ballID) {

        calcDistancePointLine(lineID, ballID);
        checkDirectionPointLine(ballID);
        //Verhindert, dass Y0 < als Y1 wird -> Fehler handling beim Rotieren
        double m;
        m = ((double) Screen.lines[closest].getY1() - (double) Screen.lines[closest].getY0()) / ((double) Screen.lines[closest].getX1() - (double) Screen.lines[closest].getX0());

        if (m == Double.NEGATIVE_INFINITY) {
            int temp = Screen.lines[lineID].getY0();
            Screen.lines[lineID].setY0(Screen.lines[lineID].getY1());
            Screen.lines[lineID].setY1(temp);

            m = ((double) Screen.lines[closest].getY1() - (double) Screen.lines[closest].getY0()) / ((double) Screen.lines[closest].getX1() - (double) Screen.lines[closest].getX0());
        }

        //Kugel trifft von der linken Seite aus auf eine senkrechte Linie
        if (m == Double.POSITIVE_INFINITY && dotP > 0 && !Screen.ball[ballID].isCollision() && !Screen.ball[ballID].isRollen() && d <= 0.6 && hitbox(lineID, ballID, m)) {

            Screen.ball[ballID].setVelX_new(-1 * (Screen.ball[ballID].getVelX_new() * heightLoss));
            Screen.ball[ballID].setVelY_new(Screen.ball[ballID].getVelY_new() * heightLoss);
        }

        //Kugel trifft von der rechten Seite aus auf eine senkrechte Linie
        else if (m == Double.POSITIVE_INFINITY && dotP < 0 && Screen.ball[ballID].isCollision() && !Screen.ball[ballID].isRollen() && d <= 0.6 && hitbox(lineID, ballID, m)) {

            Screen.ball[ballID].setVelX_new(-1 * (Screen.ball[ballID].getVelX_new() * heightLoss));
            Screen.ball[ballID].setVelY_new(Screen.ball[ballID].getVelY_new() * heightLoss);
        }

        //Kugel trifft von oben auf die Linie, Normalenvektor der geraden & Richtungsvektor der Kugel gehen aufeinander zu, Kugel befindet sich zwischen Start & Endpunkt der Geraden
        else if (!Screen.ball[ballID].isRollen() && dotP < 0 && Screen.ball[ballID].isCollision() && d <= detectionThreshold && hitbox(closest, ballID, m)) {
            vectorZerlegungPointLine(closest, ballID);

            //Abspringen auf einer waagerechten Gerade
            if (m == 0 && vectorLength(tempvParallelX, tempvParallelY) > Screen.ball[ballID].getWeight() * Math.abs(gravity[1])) {
                Screen.ball[ballID].setVelX_new(Screen.ball[ballID].getVelX_new() * heightLoss);
                Screen.ball[ballID].setVelY_new(-1 * (Screen.ball[ballID].getVelY_new() * heightLoss));
            } else {
                bounceOrRoll(ballID, m);
            }
        }

        //Kugel trifft von unten auf die Linie
        else if (dotP > 0 && !Screen.ball[ballID].isCollision() && d <= detectionThreshold && hitbox(closest, ballID, m)) {
            calcReflectingVector(closest, ballID);
        }

        //Beim Rollen wird geschaut, ob die Kugel auf eine andere Linie trifft
        //Rollen wird kurzzeitig auf false gesetzt, damit der Collisioncheck wieder durchgeschaut werden kann
        else if (Screen.ball[ballID].isRollen() && lineID != Screen.ball[ballID].getRollOn() && d <= detectionThreshold && dotP < 0 && hitbox(closest, ballID, m)) {
            Screen.ball[ballID].setRollen(false);
            vectorZerlegungPointLine(closest, ballID);

            bounceOrRoll(ballID, m);
            fixClipping(Screen.ball[ballID].getRollOn(), ballID);

        }

        //Schaut, ob die Kugel noch am Rollen ist (entweder nicht auf der Linie oder ist zu weit weg)
        else if (Screen.ball[ballID].isRollen() && ((Screen.ball[ballID].getPosX() < Screen.lines[Screen.ball[ballID].getRollOn()].getX0()) ||Screen.ball[ballID].getPosX() > Screen.lines[Screen.ball[ballID].getRollOn()].getX1())) {
            Screen.ball[ballID].setRollen(false);
        }
    }

    //Kollisionserkennung Kugel mit Linie
    //Gerade in Normalenform
    // d = Kugel->Gerade * normVektor der Geraden
    public static void calcDistancePointLine(int lineID, int ballID) {

        //Vektor zwischen Kugel zur Geraden (auf den Startpunkt)
        richtungX = Screen.lines[lineID].getX0() - Screen.ball[ballID].getPosX_new();
        richtungY = Screen.lines[lineID].getY0() - Screen.ball[ballID].getPosY_new();

        //Normalenvektor der Geraden
        normX = (Screen.lines[lineID].getY1() - Screen.lines[lineID].getY0());
        normY = -1 * (Screen.lines[lineID].getX1() - Screen.lines[lineID].getX0());

        normalizeNorm = vectorLength(normX, normY);

        dotP = richtungX * normX + richtungY * normY;

        d = Math.abs(richtungX * normX + richtungY * normY) / Math.sqrt(Math.pow(normX, 2) + Math.pow(normY, 2)) - Screen.radius / Screen.scale;

        //Schaut ob eine Kugel nah genug and einer Linie ist, diese Nummer wird gespeichert und im collisionChecker abgefragt
        if (d < 1) {
            closest = lineID;
        }
    }

    //Start & Endpunkt spannen ein Rechteck auf und es wird geschaut, ob die Kugel sich darin befindet
    public static boolean hitbox(int lineID, int ballID, double m) {
        boolean inRange;

        if(m == Double.POSITIVE_INFINITY && Screen.lines[lineID].getY0() < Screen.lines[lineID].getY1()){
            inRange = Screen.ball[ballID].getPosY() >= (Screen.lines[lineID].getY0() - buffer) && Screen.ball[ballID].getPosY() <= (Screen.lines[lineID].getY1() + buffer);
            return inRange;
        }

        else if(m == Double.POSITIVE_INFINITY && Screen.lines[lineID].getY0() > Screen.lines[lineID].getY1()){
            inRange = Screen.ball[ballID].getPosY() <= (Screen.lines[lineID].getY0() - buffer) && Screen.ball[ballID].getPosY() >= (Screen.lines[lineID].getY1() + buffer);
            return inRange;
        }
        else
            inRange = Screen.ball[ballID].getPosX() >= (Screen.lines[lineID].getX0() - buffer) && Screen.ball[ballID].getPosX() <= (Screen.lines[lineID].getX1() + buffer);

        return inRange;
    }

    //Schaut ob Richtungsvektor der Kugel & Normale der Linie aufeinader zu laufen
    public static void checkDirectionPointLine(int ballID) {
        double dotP;

        //Relative Geschwindigkeit der Kugel zur Linie
        double vrelX = Screen.ball[ballID].getVelX_new() - 0;
        double vrelY = Screen.ball[ballID].getVelY_new() - 0;

        dotP = (vrelX / vectorLength(vrelX, vrelY)) * (normX / normalizeNorm) + (vrelY / vectorLength(vrelX, vrelY)) * (normY / normalizeNorm);

        if (dotP < 0)
            Screen.ball[ballID].setCollision(true);
        else if (dotP > 0)
            Screen.ball[ballID].setCollision(false);
    }

    //Kollisionshandling Kugel mit Linie
    //skalar = v * rvGerade / |rvGerade|
    public static void vectorZerlegungPointLine(int lineID, int ballID) {

        skalar = (Screen.ball[ballID].getVelX_new() * (Screen.lines[lineID].getX1() - Screen.lines[lineID].getX0()) + Screen.ball[ballID].getVelY_new() * (Screen.lines[lineID].getY1() - Screen.lines[lineID].getY0())) /
               (Math.pow((Screen.lines[lineID].getX1() - Screen.lines[lineID].getX0()), 2) + Math.pow((Screen.lines[lineID].getY1() - Screen.lines[lineID].getY0()), 2));

        //Projektion des Richtungsvektors der Kugel auf die Gerade
        vProjektionX = skalar * (Screen.lines[lineID].getX1() - Screen.lines[lineID].getX0());
        vProjektionY = skalar * (Screen.lines[lineID].getY1() - Screen.lines[lineID].getY0());
        Screen.ball[ballID].setvProjektionX(vProjektionX);
        Screen.ball[ballID].setvProjektionY(vProjektionY);

        normalizeProjektion = vectorLength(vProjektionX, vProjektionY);

        //Paralleler Anteil der Zerlegung -> Entscheidung zum Abspringen oder Rollen
        tempvParallelX = Screen.ball[ballID].getVelX_new() - vProjektionX;
        tempvParallelY = Screen.ball[ballID].getVelY_new() - vProjektionY;

    }

    //Entscheidung ob abspringen oder rollen
    public static void bounceOrRoll(int ballID, double m) {

        // Zum Abspringen muss die Kinetische Energie größer als die Gewichtskraft sein
        if(m == Double.POSITIVE_INFINITY){
            Screen.ball[ballID].setVelX_new(-1*Screen.ball[ballID].getVelX_new());
        }
        else if (vectorLength(tempvParallelX, tempvParallelY) <= Screen.ball[ballID].getWeight() * gravity[1]) {

            Screen.ball[ballID].setRollen(true);
            Screen.ball[ballID].setRollOn(closest);
            Screen.ball[ballID].setVelX_new(vProjektionX);
            Screen.ball[ballID].setVelY_new(vProjektionY);
            fixClipping(Screen.ball[ballID].getRollOn(), ballID);
        }
        else {
            //https://math.stackexchange.com/questions/3301455/reflect-a-2d-vector-over-another-vector-without-using-normal-why-this-gives-the
            calcReflectingVector(closest, ballID);
        }
    }

    //Berechnet den Absprungsvektor nach dem Einfallswinkel = Ausfallswinkel konzept
    // skalar = v * rvGerade / |rvGerade|
    // v_neu = -v + (2*skalar*rvGerade)
    public static void calcReflectingVector(int lineID, int ballID) {
        //Faktor für die Projektion des Richtungsvektor der Kugel auf die Linie
        double k = (Screen.ball[ballID].getVelX_new() * (Screen.lines[lineID].getX1() - Screen.lines[lineID].getX0()) + Screen.ball[ballID].getVelY_new() * (Screen.lines[lineID].getY1() - Screen.lines[lineID].getY0()))
                / (Math.pow(Screen.lines[lineID].getX1() - Screen.lines[lineID].getX0(), 2) + Math.pow(Screen.lines[lineID].getY1() - Screen.lines[lineID].getY0(), 2));

        Screen.ball[ballID].setVelX_new(heightLoss * (-Screen.ball[ballID].getVelX_new() + (2 * k * (Screen.lines[lineID].getX1() - Screen.lines[lineID].getX0()))));
        Screen.ball[ballID].setVelY_new(heightLoss * (-Screen.ball[ballID].getVelY_new() + (2 * k * (Screen.lines[lineID].getY1() - Screen.lines[lineID].getY0()))));
    }

    //Kugel rollt immer genau auf der Linie
    public static void fixClipping(int rollOn, int ballID) {
        double fix;
        //Steigung
        double m = ((double) Screen.lines[rollOn].getY1() - (double) Screen.lines[rollOn].getY0()) / ((double) Screen.lines[rollOn].getX1() - (double) Screen.lines[rollOn].getX0());
        calcDistancePointLine(rollOn, ballID);
        //Unterschiedliche Szenarios, wie die Kugel neu positiniert werden muss
        if(Screen.ball[ballID].isRollen()) {
            //Wenn die Kugel oberhalb der Linie liegt
            if (dotP < 0 && m > 0 && d < 0.5) {
                calcDistancePointLine(rollOn, ballID);
                fix = Math.abs(d - 0.5);
                Screen.ball[ballID].setPosX_new(Screen.ball[ballID].getPosX_new() + (fix * Math.abs((normX / normalizeNorm))));
                Screen.ball[ballID].setPosY_new(Screen.ball[ballID].getPosY_new() - (fix * Math.abs((normY / normalizeNorm))));
                updateVelocityPosition(ballID);
            } else if (dotP < 0 && m < 0 && d < 0.5) {
                calcDistancePointLine(rollOn, ballID);
                fix = Math.abs(d - 0.5);
                Screen.ball[ballID].setPosX_new(Screen.ball[ballID].getPosX_new() - (fix * Math.abs((normX / normalizeNorm))));
                Screen.ball[ballID].setPosY_new(Screen.ball[ballID].getPosY_new() - (fix * Math.abs((normY / normalizeNorm))));
                updateVelocityPosition(ballID);
            } else if (dotP < 0 && m > 0 && d > 0.5) {
                calcDistancePointLine(rollOn, ballID);
                fix = Math.abs(d - 0.5);
                Screen.ball[ballID].setPosX_new(Screen.ball[ballID].getPosX_new() - (fix * Math.abs((normX / normalizeNorm))));
                Screen.ball[ballID].setPosY_new(Screen.ball[ballID].getPosY_new() + (fix * Math.abs((normY / normalizeNorm))));
                updateVelocityPosition(ballID);
            } else if (dotP < 0 && m < 0 && d > 0.5) {
                calcDistancePointLine(rollOn, ballID);
                fix = Math.abs(d - 0.5);
                Screen.ball[ballID].setPosX_new(Screen.ball[ballID].getPosX_new() + (fix * Math.abs((normX / normalizeNorm))));
                Screen.ball[ballID].setPosY_new(Screen.ball[ballID].getPosY_new() + (fix * Math.abs((normY / normalizeNorm))));
                updateVelocityPosition(ballID);
            } else if (dotP < 0 && m == 0 && d > 0.5) {
                calcDistancePointLine(rollOn, ballID);
                fix = Math.abs(d - 0.5);
                Screen.ball[ballID].setPosY_new(Screen.ball[ballID].getPosY_new() + (fix * Math.abs((normY / normalizeNorm))));
                updateVelocityPosition(ballID);
                calcDistancePointLine(rollOn, ballID);
            } else if (dotP < 0 && m == 0 && d < 0.5) {
                calcDistancePointLine(rollOn, ballID);
                fix = Math.abs(d - 0.5);
                Screen.ball[ballID].setPosY_new(Screen.ball[ballID].getPosY_new() - (fix * Math.abs((normY / normalizeNorm))));
                updateVelocityPosition(ballID);
            }


            //Wenn die Kugel unerhalb der Linie liegt
            if (dotP > 0 && m > 0 && d < 0.5) {
                calcDistancePointLine(rollOn, ballID);
                fix = Math.abs(d - 0.5);
                Screen.ball[ballID].setPosX_new(Screen.ball[ballID].getPosX_new() - (fix * Math.abs((normX / normalizeNorm))));
                Screen.ball[ballID].setPosY_new(Screen.ball[ballID].getPosY_new() + (fix * Math.abs((normY / normalizeNorm))));
                updateVelocityPosition(ballID);
            } else if (dotP > 0 && m < 0 && d < 0.5) {
                calcDistancePointLine(rollOn, ballID);
                fix = Math.abs(d - 0.5);
                Screen.ball[ballID].setPosX_new(Screen.ball[ballID].getPosX_new() + (fix * Math.abs((normX / normalizeNorm))));
                Screen.ball[ballID].setPosY_new(Screen.ball[ballID].getPosY_new() + (fix * Math.abs((normY / normalizeNorm))));
                updateVelocityPosition(ballID);
            } else if (dotP > 0 && m > 0 && d > 0.5) {
                calcDistancePointLine(rollOn, ballID);
                fix = Math.abs(d - 0.5);
                Screen.ball[ballID].setPosX_new(Screen.ball[ballID].getPosX_new() + (fix * Math.abs((normX / normalizeNorm))));
                Screen.ball[ballID].setPosY_new(Screen.ball[ballID].getPosY_new() - (fix * Math.abs((normY / normalizeNorm))));
                updateVelocityPosition(ballID);
            } else if (dotP > 0 && m < 0 && d > 0.5) {
                calcDistancePointLine(rollOn, ballID);
                fix = Math.abs(d - 0.5);
                Screen.ball[ballID].setPosX_new(Screen.ball[ballID].getPosX_new() - (fix * Math.abs((normX / normalizeNorm))));
                Screen.ball[ballID].setPosY_new(Screen.ball[ballID].getPosY_new() - (fix * Math.abs((normY / normalizeNorm))));
                updateVelocityPosition(ballID);
            } else if (dotP > 0 && m == 0 && d > 0.5) {
                calcDistancePointLine(rollOn, ballID);
                fix = Math.abs(d - 0.5);
                Screen.ball[ballID].setPosY_new(Screen.ball[ballID].getPosY_new() - (fix * Math.abs((normY / normalizeNorm))));
                updateVelocityPosition(ballID);
                calcDistancePointLine(rollOn, ballID);
            } else if (dotP > 0 && m == 0 && d < 0.5) {
                calcDistancePointLine(rollOn, ballID);
                fix = Math.abs(d - 0.5);
                Screen.ball[ballID].setPosY_new(Screen.ball[ballID].getPosY_new() + (fix * Math.abs((normY / normalizeNorm))));
                updateVelocityPosition(ballID);
            }
        }

    }

    //Collision Handler Kugel mit Kugel
    public static void collisionCheckPointPoint(int ball_a, int ball_b) {

        calcDistancePointPoint(ball_a, ball_b);
        vectorZerlegungPointPoint(ball_a, ball_b);

        //Kugel befinden sich genau nebeneinander oder ineinander
        if (d_balls <= (Screen.diameter / Screen.scale)) {

            //aktiviert die Magnete
            if (Screen.ball[ball_a].isMagnet() && !Screen.ball[ball_a].isMovable() || Screen.ball[ball_b].isMagnet() && !Screen.ball[ball_b].isMovable()) {
                Screen.ball[2].setMovable(true);
                Screen.ball[3].setMovable(true);
                Screen.ball[2].setRollOn(0);
                Screen.ball[3].setRollOn(0);


                Screen.ball[2].setPosX_new(Screen.ball[2].getPosX());
                Screen.ball[2].setPosY_new(Screen.ball[2].getPosY());
                Screen.ball[3].setPosX_new(Screen.ball[3].getPosX());
                Screen.ball[3].setPosY_new(Screen.ball[3].getPosY());
            }

            if (Screen.ball[ball_a].isMagnet() && Screen.ball[ball_b].isMagnet()) {
                fixClipping(0, 2);
                fixClipping(0, 3);
                //deaktiviert Magnete nach dem Zusammenstoss
                if (Screen.ball[2].getPosX() < 53 || Screen.ball[3].getPosX() < 53 ) {

                    Screen.ball[2].setMovable(false);
                    Screen.ball[3].setMovable(false);

                    Screen.ball[2].setVelX(0);
                    Screen.ball[2].setVelY(0);

                    Screen.ball[2].setVelX_new(0);
                    Screen.ball[2].setVelY_new(0);


                    Screen.ball[3].setVelX(0);
                    Screen.ball[3].setVelY(0);

                    Screen.ball[3].setVelX_new(0);
                    Screen.ball[3].setVelY_new(0);



                  //Vollkommen unelastischer Stoss bei den Magneten
                } else {
                    unelastischerStoss(2, 3);
                }
              //elastischer Stoss
            } else {
                calcDistancePointPoint(ball_a, ball_b);
                elastischerStoss(ball_a, ball_b);
                double normLine = vectorLength(x_line, y_line);
                double offset = Math.abs(d_balls - Screen.diameter / Screen.scale);

                //Versetzt die Kugeln entlangt der Beruehrnormalen so, dass diese nicht ineinander liegen & knapp aus der Kollisionserkennung liegt (die linkere Kugel wird ermittelt und versetz)
                if(d_balls <= Screen.diameter / Screen.scale) {
                    if (Screen.ball[ball_a].getPosX_new() < Screen.ball[ball_b].getPosX_new()) {
                        Screen.ball[ball_a].setPosX_new(Screen.ball[ball_a].getPosX_new() - (offset * Math.abs((x_line / normLine))) - 0.01);
                    } else if (Screen.ball[ball_a].getPosX_new() > Screen.ball[ball_b].getPosX_new()) {
                        Screen.ball[ball_b].setPosX_new(Screen.ball[ball_b].getPosX_new() - (offset * Math.abs((x_line / normLine))) - 0.01);
                    }
                }
            }

            updateVelocityPosition(ball_a);
            updateVelocityPosition(ball_b);

            //Kollisionscheck, damit die Kugel nicht durch die Linien fliegen
            for (int j = 0; j < Screen.lines.length; j++) {
                collisionCheckPointLine(j, ball_a);
                collisionCheckPointLine(j, ball_b);
            }
        }
    }


    //Kollisionserkennung Kugel mit Kugel

    public static void calcDistancePointPoint(int ball_a, int ball_b) {
        d_balls = Math.sqrt(Math.pow(Screen.ball[ball_b].getPosX() - Screen.ball[ball_a].getPosX(), 2) + Math.pow(Screen.ball[ball_b].getPosY() - Screen.ball[ball_a].getPosY(), 2));
    }

    //Kollisionshandling Kugel mit Kugel
    //skalar = v * rvBeruernormale / |Beruehrnromale|
    public static void vectorZerlegungPointPoint(int ball_a, int ball_b) {
        //Vektor zerlegung Kugel 1 mit Linie, die die beiden Kugeln verbindet
        x_line = Screen.ball[ball_a].getPosX() - Screen.ball[ball_b].getPosX();
        y_line = Screen.ball[ball_a].getPosY() - Screen.ball[ball_b].getPosY();
        double skalar1 = (Screen.ball[ball_a].getVelX_new() * x_line + Screen.ball[ball_a].getVelY_new() * y_line) / (Math.pow(x_line, 2) + Math.pow(y_line, 2));
        double projA_x = skalar1 * x_line;
        double projA_y = skalar1 * y_line;

        double a = projA_x * x_line + projA_y * y_line;
        //double parallelA_x = Screen.ball[ball_a].getVelX_new() - projA_x;
        //double parallelA_y = Screen.ball[ball_a].getVelY_new() - projA_y;

        //Vektor Zerlegung Kugel 2
        x_line = Screen.ball[ball_b].getPosX() - Screen.ball[ball_a].getPosX();
        y_line = Screen.ball[ball_b].getPosY() - Screen.ball[ball_a].getPosY();
        double skalar2 = (Screen.ball[ball_b].getVelX_new() * x_line + Screen.ball[ball_b].getVelY_new() * y_line) / (Math.pow(x_line, 2) + Math.pow(y_line, 2));
        double projB_x = skalar2 * x_line;
        double projB_y = skalar2 * y_line;

        double b = projB_x * x_line + projB_y * y_line;
        //double parallelB_x = Screen.ball[ball_b].getVelX_new() - projB_x;
        //double parallelB_y = Screen.ball[ball_b].getVelY_new() - projB_y;
    }

    //Magnetische Anziehung
    // B = µ0 * µr * (I/(2 * pi * r))
    // F = l * I * B
    // a = F / m
    public static void magneticPull(int magnet1, int magnet2) {
        double feldKonstante = 4 * Math.PI * Math.pow(10, -7);                      //Naturkonstante bei Magnetismus
        double magnetesierbarkeit = 200000;                                         //Speziellegierung
        double r = vectorLength(Screen.ball[magnet1].getPosX() - Screen.ball[magnet2].getPosX(),Screen.ball[magnet1].getPosY() - Screen.ball[magnet2].getPosY() );
        double I = 80;                                                              //Stromstarke in Ampere

        double B = feldKonstante * magnetesierbarkeit * (I / (2 * Math.PI * r));    //Magnetische Flussdichte in Tesla -> Staerke des Magnetfeldes
        double F = I * B * (Screen.diameter / Screen.scale);                        //Magnetische Anziehungskraft

        a_magnet1 = F / Screen.ball[magnet1].getWeight();
        a_magnet2 = F / Screen.ball[magnet2].getWeight();
    }

    //v_neu = (2*(m1 * v1 + m2 * v2) / m1 + m2) - v
    public static void elastischerStoss(int ball_a, int ball_b) {

        double mass = Screen.ball[ball_a].getWeight() + Screen.ball[ball_b].getWeight();

        double vx1_new = 2 * (Screen.ball[ball_a].getWeight() * Screen.ball[ball_a].getVelX_new() + Screen.ball[ball_b].getWeight() * Screen.ball[ball_b].getVelX_new()) / mass - Screen.ball[ball_a].getVelX_new();

        double vy1_new = 2 * (Screen.ball[ball_a].getWeight() * Screen.ball[ball_a].getVelY_new() + Screen.ball[ball_b].getWeight() * Screen.ball[ball_b].getVelY_new()) / mass - Screen.ball[ball_a].getVelY_new();

        double vx2_new = 2 * (Screen.ball[ball_a].getWeight() * Screen.ball[ball_a].getVelX_new() + Screen.ball[ball_b].getWeight() * Screen.ball[ball_b].getVelX_new()) / mass - Screen.ball[ball_b].getVelX_new();

        double vy2_new = 2 * (Screen.ball[ball_a].getWeight() * Screen.ball[ball_a].getVelY_new() + Screen.ball[ball_b].getWeight() * Screen.ball[ball_b].getVelY_new()) / mass - Screen.ball[ball_b].getVelY_new();


        Screen.ball[ball_a].setVelX_new(vx1_new);
        Screen.ball[ball_a].setVelY_new(vy1_new);



        Screen.ball[ball_b].setVelX_new(vx2_new);
        Screen.ball[ball_b].setVelY_new(vy2_new);

        Screen.ball[ball_a].setRollen(false);
        Screen.ball[ball_b].setRollen(false);
        //Kugel muss nicht am rollen sein beim Zusammmsentreffen -> Rollen wird auf false gesetzt, beim Check wird dann geschaut ob die Kugel wieder rollen kann
        for (int j = 0; j < Screen.lines.length; j++) {
            collisionCheckPointLine(j, ball_b);
            collisionCheckPointLine(j, ball_a);
        }
        fixClipping(Screen.ball[ball_a].getRollOn(), ball_a);
        updateVelocityPosition(ball_a);

        fixClipping(Screen.ball[ball_b].getRollOn(), ball_b);
        updateVelocityPosition(ball_b);
    }

    //v_neu = (m1 * v1 + m2 * v2) / m1 + m2
    //Vollkommen unelastischer Stoss -> beide Kugeln bewegen sich in die gleiche Richtung weiter
    public static void unelastischerStoss(int ball_a, int ball_b) {

        double mass = Screen.ball[ball_a].getWeight() + Screen.ball[ball_b].getWeight();

        double vx = (Screen.ball[ball_a].getWeight() * Screen.ball[ball_a].getVelX_new() + Screen.ball[ball_b].getWeight() * Screen.ball[ball_b].getVelX_new()) / mass;
        double vy = (Screen.ball[ball_a].getWeight() * Screen.ball[ball_a].getVelY_new() + Screen.ball[ball_b].getWeight() * Screen.ball[ball_b].getVelY_new()) / mass;


        Screen.ball[ball_a].setVelX_new(vx);
        Screen.ball[ball_a].setVelY_new(vy);
        updateVelocityPosition(ball_a);


        Screen.ball[ball_b].setVelX_new(vx);
        Screen.ball[ball_b].setVelY_new(vy);
        updateVelocityPosition(ball_b);
    }

    public static double vectorLength(double x, double y) {

        return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
    }
}
