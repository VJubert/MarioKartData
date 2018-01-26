import java.util.Arrays;
/**
 * Created by Valentin on 04/05/2017.
 */
public class Obj {
    public static final int SpeedGround = 0;
    public static final int SpeedWater = 1;
    public static final int SpeedAir = 2;
    public static final int SpeedNoGravity = 3;
    public static final int Acceleration = 4;
    public static final int Weight = 5;
    public static final int HandlingGround = 6;
    public static final int HandlingWater = 7;
    public static final int HandlingAir = 8;
    public static final int HandlingNoGravity = 9;
    public static final int Traction = 10;
    public static final int MiniTurbo = 11;


    ObjType type;
    String nom;
    Double[] stats;

//    Double SpeedGround;
//    Double SpeedWater;
//    Double SpeedAir;
//    Double SpeedNoGravity;
//    Double Acceleration;
//    Double Weight;
//    Double HandlingGround;
//    Double HandlingWater;
//    Double HandlingAir;
//    Double HandlingNoGravity;
//    Double Traction;
//    Double MiniTurbo;

    Obj(ObjType type, String nom, double[] stats) {
        this.type = type;
        this.nom = nom;
        if (stats.length != 12) {
            System.err.println(nom);
        }
//        SpeedGround=stats[0];
//        SpeedWater=stats[1];
//        SpeedAir=stats[2];
//        SpeedNoGravity=stats[3];
//        Acceleration=stats[4];
//        Weight=stats[5];
//        HandlingGround=stats[6];
//        HandlingWater=stats[7];
//        HandlingAir=stats[8];
//        HandlingNoGravity=stats[9];
//        Traction=stats[10];
//        MiniTurbo=stats[11];
        this.stats = new Double[stats.length];
        for (int i = 0; i < stats.length; i++) {
            this.stats[i] = stats[i];
        }
    }

    static String GetAllStat() {
        String newLine = System.getProperty("line.separator");
        return "0 SpeedGround" + newLine + "1 SpeedWater" + newLine + "2 SpeedAir" + newLine + "3 SpeedNoGravity" + newLine
                + "4 Acceleration" + newLine + "5 Weight" + newLine + "6 HandlingGround" + newLine + "7 HandlingWater" + newLine
                + "8 HandlingAir" + newLine + "9 HandlingNoGravity" + newLine + "10 Traction" + newLine + "11 MiniTurbo";
    }

    static String GetChar(int g) {
        switch (g) {
            case SpeedGround:
                return "SpeedGround";
            case SpeedWater:
                return "SpeedWater";
            case SpeedAir:
                return "SpeedAir";
            case SpeedNoGravity:
                return "SpeedNoGravity";
            case Acceleration:
                return "Acceleration";
            case Weight:
                return "Weight";
            case HandlingGround:
                return "HandlingGround";
            case HandlingWater:
                return "HandlingWater";
            case HandlingAir:
                return "HandlingAir";
            case HandlingNoGravity:
                return "HandlingNoGravity";
            case Traction:
                return "Traction";
            case MiniTurbo:
                return "MiniTurbo";
            default:
                return "Pourquoi ?";
        }
    }

    @Override
    public String toString() {
        return nom;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Obj obj = (Obj) o;

        if (type != obj.type) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        return Arrays.equals(stats, obj.stats);
//        return true;
    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + Arrays.hashCode(stats);
        return result;
    }
}
