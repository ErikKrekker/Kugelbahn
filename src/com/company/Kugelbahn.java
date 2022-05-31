package com.company;

public class Kugelbahn {
    static double vx, vy, sx, sy, ax, ay;
    static double vel[] = new double[2];
    static double pos[] = new double[2];
    static double gravity[] = new double[2];
    static double wind[] = new double[2];
    static Line lines[] = new Line[3];

    private static double vrelX, vrelY; //Relative Geschwindigkeit der Kugel zu der Geraden

    static double aHang;
    static double aNormal;
    static double aReib;

    static double temp;
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

    static double arx, ary;
    static double ahx, ahy;
    static int rollOn; //Linie auf der die Kugel rollt

    static int closest;

    static boolean rollen = false;
    static boolean collision = true;

    static double d = Double.POSITIVE_INFINITY;
    static double heightLoss = 0.4; //Kugel fliegt auch Holzplatte

    //führt alle Berechnungnen in richitger Reihenfolge aus
    public static void calc(double t) {
        calcAcceleration();
        calcVelocity(t);
        calcPosition(t);

        for(int i = 0; i<Screen.lines.length; i++) {
            collisionCheck(i);
        }

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

            double m = ((double) Screen.lines[rollOn].getY1() - (double) Screen.lines[rollOn].getY0()) / ((double) Screen.lines[rollOn].getX1() - (double) Screen.lines[rollOn].getX0());

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
                ahx = Math.abs((vProjektionX/normalizeProjektion) * aHang);   //Hangabtriebsbeschleunigung
                arx = Math.abs((vProjektionX/normalizeProjektion) * aReib);

                ahy = Math.abs((vProjektionY/normalizeProjektion) * aHang);
                ary = Math.abs((vProjektionY/normalizeProjektion) * aReib);
            }

            if (m > 0 && vx < 0) {
                ax = ahx + arx;

                ay = ahy + ary;
            }

            else if (m > 0 && vx > 0) {
                ax = ahx - arx;

                ay = ahy - ary;
                System.out.println();
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
                System.out.println();
            }

            //Kugel fällt senkrecht auf Gerade
            else if (m == 0 && vx == 0){
                ax = ahx + arx;

                ay = 0;
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

        static double k;
        public static void collisionCheck (int i) {

                calcDistancePointLine(i);
                checkDirection();

            double m = ((double) Screen.lines[closest].getY1() - (double) Screen.lines[closest].getY0()) / ((double) Screen.lines[closest].getX1() - (double) Screen.lines[closest].getX0());

            //System.out.println("Abstand" + d);
            //System.out.println("Steigung" + m);
            //System.out.println("NormalenVektor [" + normX + "] [" + normY + "]" );
            //System.out.println("Geschwindigkeit [" + vx + "] [" + vy + "]" );
            //System.out.println("Die nähste linie ist: " + closest);


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
            if (dotP < 0 && collision && !rollen && d <= 0.6 && pos[0] >= Screen.lines[closest].getX0() && pos[0] <= Screen.lines[closest].getX1()) {
                vectorZerlegung(closest);

                //Abspringen auf einer waagerechten Gerade
                if (m == 0 && vectorLength(tempvParallelX, tempvParallelY) >= 2){
                    vx = vx * heightLoss;
                    vy = -(vy * heightLoss);
                }
                    else {
                        //Annahme: Kugelmasse = 200g -> Fg = 1,962 und die Absprungskraft muss größer als Fg sein
                         if (vectorLength(tempvParallelX, tempvParallelY) <= 2) {
                            System.out.println(vectorLength(tempvParallelX, tempvParallelY));
                            rollen = true;
                            rollOn = closest;
                            vx = vProjektionX;
                            vy = vProjektionY;
                            System.out.println("Bin am rollen");
                        } else {
                            //https://math.stackexchange.com/questions/3301455/reflect-a-2d-vector-over-another-vector-without-using-normal-why-this-gives-the
                            calcReflectingVector(closest);
                        }
                }
            }

            //Kugel trifft von unten auf die Gerade
            else if (dotP > 0 && !collision && d <= 0.6 && pos[0] >= Screen.lines[closest].getX0() && pos[0] <= Screen.lines[closest].getX1()) {

                calcReflectingVector(closest);
            }

            //Während die Kugel am Rollen ist & andere Geraden nah genug kommen (außer die Gerade, auf der die Kugel rollt) wird ein neues Handling eingebracht
            // Rollen wird temporär auf false gesetzt, damit der Collisioncheck wieder durchgeführt werden kann
            else if(rollen && d < 0.6 && i!=rollOn && dotP <0){
                rollen = false;
                vectorZerlegung(i);
                if (vectorLength(tempvParallelX, tempvParallelY) <= 2) {
                    rollen = true;
                    rollOn = closest;
                    vx = vProjektionX;
                    vy = vProjektionY;
                    System.out.println("Bin am rollen");
                }
                else {
                    calcReflectingVector(i);
                }
            }

            //Schaut, ob die Kugel noch auf der Geraden rollt
            else if (rollen && (pos[0] <= Screen.lines[rollOn].getX0() || pos[0] >= Screen.lines[rollOn].getX1())) {
                rollen = false;
                System.out.println("fliege wieder");
            }
        }


        public static void calcDistancePointLine (int i){

            //Vektor zwischen Ball zur Geraden
            richtungX = Screen.lines[i].getX0() - pos[0];
            richtungY = Screen.lines[i].getY0() - pos[1];

            //Normalen vektor der Geraden
            normX = (Screen.lines[i].getY1() - Screen.lines[i].getY0());
            normY = -1 * (Screen.lines[i].getX1() - Screen.lines[i].getX0());

            normalizeNorm = vectorLength(normX, normY);

            dotP = richtungX * normX + richtungY * normY;

            d = Math.abs(((richtungX * normX + richtungY * normY) / Math.sqrt(Math.pow(normX, 2) + Math.pow(normY, 2))) - (Screen.radius / Screen.scale));

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

        public static void vectorZerlegung (int j) {

            skalar = (vx * (Screen.lines[j].getX1() - Screen.lines[j].getX0()) + vy * (Screen.lines[j].getY1() - Screen.lines[j].getY0())) /
                    (Math.pow((Screen.lines[j].getX1() - Screen.lines[j].getX0()), 2) + Math.pow((Screen.lines[j].getY1() - Screen.lines[j].getY0()), 2));

            //Projektion des Richtungsvektors der Kugel auf die Gerade
            vProjektionX = skalar * (Screen.lines[j].getX1() - Screen.lines[j].getX0());
            vProjektionY = skalar * (Screen.lines[j].getY1() - Screen.lines[j].getY0());

            normalizeProjektion = vectorLength(vProjektionX, vProjektionY);

            //Paralleler Anteil der Zerlegung -> Wird für Entscheidung ob rollen oder springen gebraucht
            tempvParallelX = vx - vProjektionX;
            tempvParallelY = vy - vProjektionY;

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

        public static void calcReflectingVector(int l){
            //Faktor für die Projektion des Richtungsvektor der Kugel auf die Gerade
            k = (vx * (Screen.lines[l].getX1() - Screen.lines[l].getX0()) + vy * (Screen.lines[l].getY1() - Screen.lines[l].getY0())) / (Math.pow(Screen.lines[l].getX1() - Screen.lines[l].getX0(), 2) + Math.pow(Screen.lines[l].getY1() - Screen.lines[l].getY0(), 2));

            vx = heightLoss * (-vx + (2*k*(Screen.lines[l].getX1() - Screen.lines[l].getX0())));
            vy = heightLoss * (-vy + (2*k*(Screen.lines[l].getY1() - Screen.lines[l].getY0())));
            System.out.println();
        }
    }
