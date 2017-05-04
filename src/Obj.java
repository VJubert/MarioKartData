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

    Obj(ObjType type, String nom, double[] stats) {
        this.type = type;
        this.nom = nom;
        if (stats.length != 12) {
            System.err.println(nom);
        }
        this.stats = new Double[stats.length];
        for (int i = 0; i < stats.length; i++) {
            this.stats[i] = stats[i];
        }
    }

    @Override
    public String toString() {
        return "Obj{" +
                "type=" + type +
                ", nom='" + nom + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Obj obj = (Obj) o;

        return Arrays.equals(stats, obj.stats);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(stats);
    }
}
