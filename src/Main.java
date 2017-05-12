import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.variables.IntVar;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Main {

    static List<Obj> objs;
    static List<Obj> pers;
    static List<Obj> kart;
    static List<Obj> roues;
    static List<Obj> ailes;

    static int[][] persArray;
    static int[][] kartArray;
    static int[][] rouesArray;
    static int[][] ailesArray;

    static Double[] persAverage;
    static Double[] kartAverage;
    static Double[] rouesAverage;
    static Double[] ailesAverage;


    static IntVar persCs;
    static IntVar kartCs;
    static IntVar rouesCs;
    static IntVar ailesCs;

    static Function<Stream<Obj>, Integer> numberOfGroup = stream -> stream.collect(Collectors.groupingBy(Function.identity())).size();
    static Function<Integer, BiFunction<Double, Stream<Obj>, List<Obj>>> groupByStat = stat -> (max, stream) -> stream.collect(Collectors.groupingBy(o -> o.stats[stat])).get(max);
    static Function<Integer, Comparator<Obj>> comparatorByStats = stats -> (o1, o2) -> o1.stats[stats] > o2.stats[stats] ? 1 : Objects.equals(o1.stats[stats], o2.stats[stats]) ? 0 : -1;
    static BiFunction<Integer, Stream<Obj>, Double> maxStats = (stat, stream) -> stream.mapToDouble(x -> x.stats[stat]).max().getAsDouble();
    static Function<Integer, ToDoubleFunction<Obj>> averageByStat = stat -> o -> o.stats[stat];


    public static void main(String[] args) {
        init();
        showBaseChoice();

        Model m = new Model("MKData");
        persCs = m.intVar("perso", 0, Main.pers.size() - 1);
        kartCs = m.intVar("kart", 0, Main.kart.size() - 1);
        rouesCs = m.intVar("roues", 0, Main.roues.size() - 1);
        ailesCs = m.intVar("ailes", 0, Main.ailes.size() - 1);

//        addConstraint(m, Obj.Weight, ">=", (int) (3.5 * 4));
        IntVar res = addConstraint(m, Obj.SpeedGround, ">=", (int) (5.75 * 4));
//        addConstraint(m,Obj.SpeedAir,">=", (int) (5*4));
//        addConstraint(m, Obj.SpeedNoGravity, ">=", (int) (4 * 4));
//        addConstraint(m, Obj.MiniTurbo, ">=", (int) (3 * 4));
//        addConstraint(m, Obj.Acceleration, ">=", (int) (3 * 4));
//
        m.setObjective(Model.MAXIMIZE, res);

//        System.out.println(m.getSolver().findAllSolutions().size());
        printSolutions(m, true);
    }

    static void printSolutions(Model m) {
        printSolutions(m, false);
    }

    static List<Obj> findSame(ObjType type, Obj o) {
        return objs.stream().filter(x -> x.type.equals(type)).filter(x -> x.equals(o)).collect(Collectors.toList());
    }

    static void printSolutions(Model m, boolean all) {
        Solver solver = m.getSolver();
        if (all) {
            solver.findAllSolutions().stream().forEach(solution -> {
                printCombi(solution.getIntVal(persCs), solution.getIntVal(kartCs), solution.getIntVal(rouesCs), solution.getIntVal(ailesCs));
            });
        } else {
            while (solver.solve()) {
                printCombi(persCs.getValue(), kartCs.getValue(), rouesCs.getValue(), ailesCs.getValue());
            }
        }
    }

    private static void printCombi(int pers, int kart, int roue, int aile) {
        Obj persRes = Main.pers.get(pers);
        Obj kartRes = Main.kart.get(kart);
        Obj rouesRes = Main.roues.get(roue);
        Obj ailesRes = Main.ailes.get(aile);
        System.out.println("Pers : " + findSame(ObjType.Pers, persRes));
        System.out.println("Kart : " + findSame(ObjType.Kart, kartRes));
        System.out.println("Roues : " + findSame(ObjType.Roue, rouesRes));
        System.out.println("Ailes : " + findSame(ObjType.Aile, ailesRes));
        for (int i = 0; i < 12; i++) {
            System.out.println(Obj.GetChar(i) + " : " + (persRes.stats[i] + kartRes.stats[i] + rouesRes.stats[i] + ailesRes.stats[i]));
        }
        System.out.println();
    }

    private static void showBaseChoice() {
        Obj p = objs.parallelStream().filter(x -> x.nom.equals("Morton")).findFirst().get();
        Obj k = objs.parallelStream().filter(x -> x.nom.equals("Or")).findFirst().get();
        Obj r = objs.parallelStream().filter(x -> x.nom.equals("Roller")).findFirst().get();
        Obj a = objs.parallelStream().filter(x -> x.nom.equals("Aile fleurie")).findFirst().get();

        IntStream.range(0, 12).forEachOrdered(x -> System.out.println(Obj.GetChar(x) + " : " + (p.stats[x] + k.stats[x] + r.stats[x] + a.stats[x])));
        System.out.println("Fin Base");
        System.out.println();
    }

    static IntVar addConstraint(Model m, int stat, String op, Integer result) {
        IntVar resPers = m.intVar(-24, 24);
        IntVar resKart = m.intVar(-24, 24);
        IntVar resRoues = m.intVar(-24, 24);
        IntVar resAiles = m.intVar(-24, 24);
        m.element(resPers, persArray[stat], persCs).post();
        m.element(resKart, kartArray[stat], kartCs).post();
        m.element(resRoues, rouesArray[stat], rouesCs).post();
        m.element(resAiles, ailesArray[stat], ailesCs).post();
        IntVar res;
        if (result == null)
            res = m.intVar(-24, 24);
        else
            res = m.intVar(result);
        m.scalar(new IntVar[]{resPers, resKart, resRoues, resAiles}, new int[]{1, 1, 1, 1}, op, res).post();
        return res;

    }

    public static void findMax(int stat) {
        double maxMiniPerso = maxStats.apply(stat, pers.parallelStream());
        double maxMiniKart = maxStats.apply(stat, kart.parallelStream());
        double maxMiniRoues = maxStats.apply(stat, roues.parallelStream());
        double maxMiniAiles = maxStats.apply(stat, ailes.parallelStream());

        groupByStat.apply(stat).apply(maxMiniPerso, pers.stream()).forEach(System.out::println);
        groupByStat.apply(stat).apply(maxMiniKart, kart.stream()).forEach(System.out::println);
        groupByStat.apply(stat).apply(maxMiniRoues, roues.stream()).forEach(System.out::println);
        groupByStat.apply(stat).apply(maxMiniAiles, ailes.stream()).forEach(System.out::println);
        System.out.println(maxMiniAiles + maxMiniKart + maxMiniPerso + maxMiniRoues);
    }

    public static void init() {
        objs = Arrays.asList(
                new Obj(ObjType.Pers, "Bébé Peach", new double[]{2.25, 2.5, 2.75, 2, 4, 2, 5, 4.5, 5, 5, 4.25, 4}),
                new Obj(ObjType.Pers, "Bébé Daisy", new double[]{2.25, 2.5, 2.75, 2, 4, 2, 5, 4.5, 5, 5, 4.25, 4}),
                new Obj(ObjType.Pers, "Bébé Harmonie", new double[]{2.25, 2.5, 2.75, 2, 4.25, 2, 4.75, 4.25, 4.75, 4.75, 3.75, 4}),
                new Obj(ObjType.Pers, "Lemmy", new double[]{2.25, 2.5, 2.75, 2, 4.25, 2, 4.75, 4.25, 4.75, 4.75, 3.75, 4}),
                new Obj(ObjType.Pers, "Bébé Mario", new double[]{2.5, 2.75, 3, 2.25, 4.25, 2.25, 4.5, 4, 4.5, 4.5, 4, 3.75}),
                new Obj(ObjType.Pers, "Bébé Luigi", new double[]{2.5, 2.75, 3, 2.25, 4.25, 2.25, 4.5, 4, 4.5, 4.5, 4, 3.75}),
                new Obj(ObjType.Pers, "Skelerex", new double[]{2.5, 2.75, 3, 2.25, 4.25, 2.25, 4.5, 4, 4.5, 4.5, 4, 3.75}),
//                new Obj(ObjType.Pers, "Mii léger", new double[]{2.5, 2.75, 3, 2.25, 4.25, 2.25, 4.5, 4, 4.5, 4.5, 4, 3.75}),
                new Obj(ObjType.Pers, "Koopa", new double[]{2.75, 3, 3.25, 2.5, 4, 2.5, 4.5, 4, 4.5, 4.5, 4.25, 3.75}),
                new Obj(ObjType.Pers, "Lakitu", new double[]{2.75, 3, 3.25, 2.5, 4, 2.5, 4.5, 4, 4.5, 4.5, 4.25, 3.75}),
                new Obj(ObjType.Pers, "Bowser Jr", new double[]{2.75, 3, 3.25, 2.5, 4, 2.5, 4.5, 4, 4.5, 4.5, 4.25, 3.75}),
                new Obj(ObjType.Pers, "Toadette", new double[]{2.75, 3, 3.25, 2.5, 4.25, 2.5, 4.25, 3.75, 4.25, 4.25, 3.5, 3.75}),
                new Obj(ObjType.Pers, "Wendy", new double[]{2.75, 3, 3.25, 2.5, 4.25, 2.5, 4.25, 3.75, 4.25, 4.25, 3.5, 3.75}),
                new Obj(ObjType.Pers, "Marie", new double[]{2.75, 3, 3.25, 2.5, 4.25, 2.5, 4.25, 3.75, 4.25, 4.25, 3.5, 3.75}),
                new Obj(ObjType.Pers, "Toad", new double[]{3, 3.25, 3.5, 2.75, 4, 2.75, 4.25, 3.75, 4.25, 4.25, 4, 3.5}),
                new Obj(ObjType.Pers, "Maskas", new double[]{3, 3.25, 3.5, 2.75, 4, 2.75, 4.25, 3.75, 4.25, 4.25, 4, 3.5}),
                new Obj(ObjType.Pers, "Larry", new double[]{3, 3.25, 3.5, 2.75, 4, 2.75, 4.25, 3.75, 4.25, 4.25, 4, 3.5}),
                new Obj(ObjType.Pers, "Peach Chat", new double[]{3.25, 3.5, 3.75, 3, 4, 2.75, 4, 3.5, 4, 4, 3.75, 3.5}),
                new Obj(ObjType.Pers, "Inklink Fille", new double[]{3.25, 3.5, 3.75, 3, 4, 2.75, 4, 3.5, 4, 4, 3.75, 3.5}),
                new Obj(ObjType.Pers, "Villageoise", new double[]{3.25, 3.5, 3.75, 3, 4, 2.75, 4, 3.5, 4, 4, 3.75, 3.5}),
                new Obj(ObjType.Pers, "Peach", new double[]{3.5, 3.75, 4, 3.25, 3.75, 3, 3.75, 3.25, 3.75, 3.75, 3.75, 3.5}),
                new Obj(ObjType.Pers, "Daisy", new double[]{3.5, 3.75, 4, 3.25, 3.75, 3, 3.75, 3.25, 3.75, 3.75, 3.75, 3.5}),
                new Obj(ObjType.Pers, "Yoshi", new double[]{3.5, 3.75, 4, 3.25, 3.75, 3, 3.75, 3.25, 3.75, 3.75, 3.75, 3.5}),
                new Obj(ObjType.Pers, "Mario Tanuki", new double[]{3.5, 3.75, 4, 3.25, 3.75, 3.25, 3.75, 3.25, 3.75, 3.75, 3.25, 3.5}),
                new Obj(ObjType.Pers, "Inklink Garcon", new double[]{3.5, 3.75, 4, 3.25, 3.75, 3, 3.75, 3.25, 3.75, 3.75, 3.75, 3.5}),
                new Obj(ObjType.Pers, "Villageois", new double[]{3.5, 3.75, 4, 3.25, 3.75, 3, 3.75, 3.25, 3.75, 3.75, 3.75, 3.5}),
                new Obj(ObjType.Pers, "Luigi", new double[]{3.75, 4, 4.25, 3.5, 3.5, 3.5, 3.75, 3.25, 3.75, 3.75, 3.25, 3.25}),
                new Obj(ObjType.Pers, "Iggy", new double[]{3.75, 4, 4.25, 3.5, 3.5, 3.5, 3.75, 3.25, 3.75, 3.75, 3.25, 3.25}),
                new Obj(ObjType.Pers, "Mario", new double[]{3.75, 4, 4.25, 3.5, 3.5, 3.5, 3.5, 3, 3.5, 3.5, 3.5, 3.25}),
                new Obj(ObjType.Pers, "Ludwig", new double[]{3.75, 4, 4.25, 3.5, 3.5, 3.5, 3.5, 3, 3.5, 3.5, 3.5, 3.25}),
//                new Obj(ObjType.Pers, "Mii Moyen", new double[]{3.75, 4, 4.25, 3.5, 3.5, 3.5, 3.5, 3, 3.5, 3.5, 3.5, 3.25}),
                new Obj(ObjType.Pers, "Harmonie", new double[]{4, 4.25, 4.5, 3.75, 3.25, 3.75, 3.25, 2.75, 3.25, 3.25, 3.75, 3.25}),
                new Obj(ObjType.Pers, "Boo", new double[]{4, 4.25, 4.5, 3.75, 3.25, 3.75, 3.25, 2.75, 3.25, 3.25, 3.75, 3.25}),
                new Obj(ObjType.Pers, "Link", new double[]{4, 4.25, 4.5, 3.75, 3.25, 3.75, 3.25, 2.75, 3.25, 3.25, 3.75, 3.25}),
                new Obj(ObjType.Pers, "Waluigi", new double[]{4.5, 4.75, 5, 4.25, 3.25, 4, 3, 2.5, 3, 3, 3, 3}),
                new Obj(ObjType.Pers, "Donkey Kong", new double[]{4.5, 4.75, 5, 4.25, 3.25, 4, 3, 2.5, 3, 3, 3, 3}),
                new Obj(ObjType.Pers, "Roy", new double[]{4.5, 4.75, 5, 4.25, 3.25, 4, 3, 2.5, 3, 3, 3, 3}),
                new Obj(ObjType.Pers, "Wario", new double[]{4.75, 5, 5.25, 4.5, 3, 4.25, 2.75, 2.25, 2.75, 2.75, 3.25, 2.75}),
                new Obj(ObjType.Pers, "Bowser Squelette", new double[]{4.75, 5, 5.25, 4.5, 3, 4.25, 2.75, 2.25, 2.75, 2.75, 3.25, 2.75}),
                new Obj(ObjType.Pers, "Silver Mario", new double[]{4.25, 4.5, 4.75, 4, 3.25, 4.5, 3.25, 2.75, 3.25, 3.25, 3.25, 3}),
                new Obj(ObjType.Pers, "Peach d'Or Rose", new double[]{4.25, 4.5, 4.75, 4, 3.25, 4.5, 3.25, 2.75, 3.25, 3.25, 3.25, 3}),
                new Obj(ObjType.Pers, "Bowser", new double[]{4.75, 5, 5.25, 4.5, 3, 4.5, 2.5, 2, 2.5, 2.5, 3, 2.75}),
                new Obj(ObjType.Pers, "Morton", new double[]{4.75, 5, 5.25, 4.5, 3, 4.5, 2.5, 2, 2.5, 2.5, 3, 2.75}),
                //new Obj(ObjType.Pers, "Mii Lourd", new double[]{4.75, 5, 5.25, 4.5, 3, 4.5, 2.5, 2, 2.5, 2.5, 3, 2.75})
                new Obj(ObjType.Kart, "Kart Standard", new double[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}),
                new Obj(ObjType.Kart, "Retro", new double[]{-0.5, 0, -0.5, -0.5, 0.5, -0.25, 0.5, 0.5, -0.25, 0.25, 0.25, 0.5}),
                new Obj(ObjType.Kart, "Proto 8", new double[]{0, 0, 0.25, 0.5, -0.25, 0.25, -0.25, 0, -0.25, 0.25, 0.25, 0}),
                new Obj(ObjType.Kart, "Nautomobile", new double[]{0.25, 0.5, -0.75, -0.25, -0.75, 0.5, -0.5, 0.75, -0.5, -0.5, 0, -0.5}),
                new Obj(ObjType.Kart, "Chabriolet", new double[]{-0.25, -0.25, 0.25, 0, 0.25, 0, 0.25, 0, 0.25, 0, 0, 0.25}),
                new Obj(ObjType.Kart, "Mach-celebre", new double[]{0.5, -0.5, -0.25, 0.25, -0.75, 0.25, -0.5, -0.25, -0.75, -0.25, -0.5, -0.75}),
                new Obj(ObjType.Kart, "Tubul R3", new double[]{0.25, 0.5, -0.75, -0.25, -0.75, 0.5, -0.5, 0.75, -0.5, -0.5, 0, 0 - 0.5}),
                new Obj(ObjType.Kart, "Beat-bolide", new double[]{0.5, -0.25, -0.5, 0, -1, 0.5, -0.75, -0.25, -0.75, -0.5, 0.5, -1}),
                new Obj(ObjType.Kart, "Cavalkart", new double[]{0.25, 0, 0, 0, -0.5, -0.25, 0, 0.25, 0, -0.25, -0.25, -0.25}),
                new Obj(ObjType.Kart, "Paracoccinelly", new double[]{-0.75, -0.5, -0.5, -0.25, 0.75, -0.5, 0.5, 0.5, 0.25, 0.5, 0.25, 0.75}),
                new Obj(ObjType.Kart, "Caravéloce", new double[]{-0.5, 0.5, -0.25, -0.75, 0.5, -0.5, 0.25, 0.75, 0, -0.25, 0.75, 0.5}),
                new Obj(ObjType.Kart, "Sneakart", new double[]{0.25, -0.25, 0, 0, -0.5, 0, 0, 0, -0.25, 0, -0.75, -0.25}),
                new Obj(ObjType.Kart, "Propulsar", new double[]{0, 0, 0.25, 0.5, -0.25, 0.25, -0.25, 0, 0.25, 0.25, 0.25, 0}),
                new Obj(ObjType.Kart, "Or", new double[]{0.25, -0.25, 0, 0, -0.5, 0, 0, 0, -0.25, 0, -0.75, -0.25}),
                new Obj(ObjType.Kart, "GLA", new double[]{0.5, -0.25, -0.5, 0, -1, 0.5, -0.75, -0.25, -0.75, 0.5, 0.5, -1}),
                new Obj(ObjType.Kart, "W25 Silver Arrow", new double[]{-0.25, -0.25, 0, 0.25, 0.25, -0.25, 0.25, 0.25, 0, 0.25, 0.5, 0.25}),
                new Obj(ObjType.Kart, "300 SL Roadster", new double[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}),
                new Obj(ObjType.Kart, "Blue Falcon", new double[]{0.25, -0.25, 0, 0.25, -0.25, -0., -0.25, 0.25, -0.5, 0.5, 0, -0.25}),
                new Obj(ObjType.Kart, "Tanooki Kart", new double[]{-0.25, 0.25, 0, 0, -0.5, 0.25, 0.25, 0.5, 0, 0, 1, -0.25}),
                new Obj(ObjType.Kart, "Intrépide", new double[]{0.5, -0.5, -0.25, 0.25, -0.75, 0.25, -0.5, -0.25, -0.75, -0.25, -0.5, -0.75}),
                new Obj(ObjType.Kart, "Autorhino", new double[]{-0.5, 0.25, -0.25, -0.75, 0.5, -0.5, 0.25, 0.75, 0, -0.25, 0.75, 0.5}),
                new Obj(ObjType.Kart, "Magikart", new double[]{0.5, -0.5, -0.25, 0.25, -0.75, 0.25, -0.5, -0.25, -0.75, -0.25, -0.5, -0.75}),
                new Obj(ObjType.Kart, "Koopa Clown", new double[]{-0.25, 0.25, 0, 0, -0.5, 0.25, 0.25, 0.5, 0, 0, 1, -0.25}),
                new Obj(ObjType.Kart, "Moto Standard", new double[]{-0.25, -0.25, 0, 0.25, 0.25, -0.25, 0.25, 0.25, 0, 0.25, 0.5, 0.25}),
                new Obj(ObjType.Kart, "Meteore", new double[]{0 - 0.25, -0.25, 0.25, 0, 0.25, 0, 0.25, 0, 0.25, 0, 0, 0.25}),
                new Obj(ObjType.Kart, "Sport GP", new double[]{0.25, 0, 0, 0, -0.5, -0.25, 0, 0.25, 0, -0.25, -0.25, -0.25}),
                new Obj(ObjType.Kart, "Cybertombe", new double[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}),
                new Obj(ObjType.Kart, "Flamboyante", new double[]{-0.25, -0.25, 0, 0.25, 0.25, -0.25, 0.25, 0.25, 0, 0.25, 0.5, 0.25}),
                new Obj(ObjType.Kart, "Mécabécane", new double[]{-0.5, 0, -0.5, -0.5, 0.5, -0.25, 0.5, 0.5, -0.25, 0.25, 0.25, 0.5}),
                new Obj(ObjType.Kart, "Scootinette", new double[]{-0.75, -0.5, -0.5, -0.25, 0.75, -0.5, 0.5, 0.5, 0.25, 0.5, 0.25, 0.75}),
                new Obj(ObjType.Kart, "Epervier", new double[]{0.25, 0, 0, 0, -0.5, -0.25, 0, 0.25, 0, -0.25, -0.25, -0.25}),
                new Obj(ObjType.Kart, "Yoshimoto", new double[]{-0.25, -0.25, 0.25, 0, 0.25, 0, 0.25, 0, 0.25, 0, 0, 0.25}),
                new Obj(ObjType.Kart, "Master-becane", new double[]{0.25, -0.25, 0, 0, -0.5, 0, 0, 0, -0.25, 0, -0.75, -0.25}),
                new Obj(ObjType.Kart, "City Tripper", new double[]{-0.5, 0, -0.5, -0.5, 0.5, -0.25, 0.5, 0.5, -0.25, 0.25, 0.25, 0.5}),
                new Obj(ObjType.Kart, "Quad Standard", new double[]{0.5, -0.25, -0.5, 0, -1, 0.5, -0.75, -0.25, -0.75, -0.5, 0.5, -1}),
                new Obj(ObjType.Kart, "Quad Wiggler", new double[]{-0.25, -0.25, 0, 0.25, 0.25, -0.25, 0.25, 0.25, 0, 0.5, 0.5, 0.25}),
                new Obj(ObjType.Kart, "Quad Nounours", new double[]{-0.25, -0.25, 0.25, 0, 0.25, 0, 0.25, 0, 0.25, 0, 0, 0.25}),
                new Obj(ObjType.Kart, "Bone Rattler", new double[]{0.25, 0.5, -0.75, -0.25, -0.75, 0.5, -0.5, 0.75, -0.5, -0.5, 0, -0.}),
                new Obj(ObjType.Kart, "Inkstriker", new double[]{0, 0, 0.5, 0.5, -0.25, 0.25, -0.25, 0, -0.25, 0.25, 0.25, 0}),
                new Obj(ObjType.Kart, "Splat Buggy", new double[]{0.25, -0.25, 0, 0.25, -0.25, -0.5, -0.25, 0.25, -0.5, 0.5, 0, -0.25}),
                new Obj(ObjType.Roue, "Roue Standard", new double[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}),
                new Obj(ObjType.Roue, "Mastodonte", new double[]{0, -0.25, -0.5, 0, -0.5, 0.5, -0.75, -0.5, -0.5, -0.75, 0.5, -0.25}),
                new Obj(ObjType.Roue, "Roller", new double[]{-0.5, 0, 0, -0.5, 0.5, -0.5, 0.25, 0.25, 0.25, 0.25, -0.25, 0.75}),
                new Obj(ObjType.Roue, "Classique", new double[]{0.25, -0.25, -0.25, 0.5, -0.5, 0, .25, 0.25, 0.25, 0, -1, -0.25}),
                new Obj(ObjType.Roue, "Lisse", new double[]{0.5, -0.75, -0.75, 0.5, -0.75, 0.25, -0.25, -0.75, -0.5, -0.25, -1.25, -0.75}),
                new Obj(ObjType.Roue, "Metal", new double[]{0.5, 0, -0.25, -0.25, -1, 0.5, -0.25, -0.25, -0.75, -0.5, -0.75, -0.75}),
                new Obj(ObjType.Roue, "Bouton", new double[]{-0.25, -0.25, -0.25, 0, 0.25, -0.5, 0, 0, -0.25, 0.25, -0.5, 0.5}),
                new Obj(ObjType.Roue, "Hors-piste", new double[]{0.25, 0.25, -0.5, 0, -0.25, 0.25, -0.5, -0.5, -0.5, -0.25, 0.25, -0.5}),
                new Obj(ObjType.Roue, "Eponge", new double[]{-0.25, -0.5, 0.25, -0.25, 0, -0.25, -0.25, -0.25, 0, 0, 0.25, 0.25}),
                new Obj(ObjType.Roue, "Bois", new double[]{0.25, -0.25, -0.25, 0.5, -0.5, 0, 0.25, 0.25, 0.25, 0, -1, -0.25}),
                new Obj(ObjType.Roue, "Coussin", new double[]{-0.25, -0.5, 0.25, -0.25, 0, -0.25, -0.25, 0.5, 0, -0.25, 0.25, 0.25}),
                new Obj(ObjType.Roue, "Standard Bleu", new double[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}),
                new Obj(ObjType.Roue, "Masto-flamme", new double[]{0, -0.25, -0.5, 0, -0.5, 0.5, -0.75, -0.5, -0.5, -0.75, 0.5, -0.2}),
                new Obj(ObjType.Roue, "Roller Azure", new double[]{-0.5, 0, 0, -0.5, 0.5, -0.5, 0.25, 0.25, 0.25, 0.25, -0.25, 0.75}),
                new Obj(ObjType.Roue, "Classique rouge", new double[]{0.25, -0.25, -0.25, 0.5, -0.5, 0, 0.25, 0.25, 0.25, 0, -1, -0.25}),
                new Obj(ObjType.Roue, "Cyber-lisse", new double[]{0.5, -0.75, -0.75, 0.5, -0.75, 0.25, -0.25, -0.75, -0.5, -0.25, -1.25, -0.75}),
                new Obj(ObjType.Roue, "Hors-piste retro", new double[]{0.25, 0.25, -0.5, 0, -0.25, 0.25, -0.5, -0.5, -0.25, -0.25, 0.25, -0.5}),
                new Obj(ObjType.Roue, "Or", new double[]{0.5, 0, -0.25, -0.25, -1, 0.5, -0.25, -0.25, -0.75, -0.5, -0.75, -0.75}),
                new Obj(ObjType.Roue, "Roues GLA", new double[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}),
                new Obj(ObjType.Roue, "Roues Triforce", new double[]{0.25, 0.25, -0.5, 0, -0.25, 0.25, -0.5, -0.5, -0.25, -0.25, 0.25, -0.5}),
                new Obj(ObjType.Roue, "Roues Feuilles", new double[]{-0.25, -0.25, -0.25, 0, 0.25, -0.5, 0, 0, -0.25, 0.25, -0.5, 0.5}),
                new Obj(ObjType.Aile, "Standard", new double[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}),
                new Obj(ObjType.Aile, "Ailes nuages", new double[]{-0.25, 0, -0.25, 0.25, 0.25, -0.25, 0, 0, 0.25, 0, 0, 0.25}),
                new Obj(ObjType.Aile, "Aile Wario", new double[]{0, -0.25, 0, 0.25, 0, 0.25, 0, 0.25, 0, -0.25, -0.25, 0}),
                new Obj(ObjType.Aile, "Dendinaille", new double[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}),
                new Obj(ObjType.Aile, "Ombrelle Peach", new double[]{-0.25, -0.25, -0.25, 0.25, 0.25, 0, 0, 0.25, 0.25, -0.25, -0.25, 0.25}),
                new Obj(ObjType.Aile, "Parachute", new double[]{-0.25, 0, -0.25, 0.25, 0.25, -0.25, 0, 0, 0.25, 0, 0, 0.25}),
                new Obj(ObjType.Aile, "Parapente", new double[]{-0.25, -0.25, -0.25, 0.25, 0.25, 0, 0, 0.25, 0.25, -0.25, -0.25, 0.25}),
                new Obj(ObjType.Aile, "Aile fleurie", new double[]{-0.25, 0, -0.25, 0.25, 0.25, -0.25, 0, 0, 0.25, 0, 0, 0.25}),
                new Obj(ObjType.Aile, "Bowser-volant", new double[]{-0.25, -0.25, -0.25, 0.25, 0.25, 0, 0, 0.25, 0.25, -0.25, -0.25, 0.25}),
                new Obj(ObjType.Aile, "Planeur", new double[]{0, -0.25, 0, 0.25, 0, 0.25, 0, 0.25, 0, -0.25, -0.25, 0}),
                new Obj(ObjType.Aile, "Parapente MKTV", new double[]{-0.25, -0.25, -0.25, 0.25, 0.25, 0, 0, 0.25, 0.25, -0.25, -0.25, 0.25}),
                new Obj(ObjType.Aile, "Or", new double[]{0, -0.25, 0, 0.25, 0, 0.25, 0, 0.25, 0, -0.25, -0.25, 0}),
                new Obj(ObjType.Aile, "Voile Hylienne", new double[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}),
                new Obj(ObjType.Aile, "Avion en papier", new double[]{-0.25, 0, -0.25, 0.25, 0.25, -0.25, 0, 0, 0.25, 0, 0, 0.25})
        );

        pers = objs.stream().filter(obj -> obj.type.equals(ObjType.Pers)).distinct().collect(Collectors.toList());
        kart = objs.stream().filter(obj -> obj.type.equals(ObjType.Kart)).distinct().collect(Collectors.toList());
        roues = objs.stream().filter(obj -> obj.type.equals(ObjType.Roue)).distinct().collect(Collectors.toList());
        ailes = objs.stream().filter(obj -> obj.type.equals(ObjType.Aile)).distinct().collect(Collectors.toList());


        persArray = new int[12][pers.size()];
        kartArray = new int[12][kart.size()];
        rouesArray = new int[12][roues.size()];
        ailesArray = new int[12][ailes.size()];

        persAverage = new Double[12];
        kartAverage = new Double[12];
        rouesAverage = new Double[12];
        ailesAverage = new Double[12];
        BiFunction<Integer, Stream<Obj>, Double> averageByList = (i, s) -> s.mapToDouble(averageByStat.apply(i)).average().getAsDouble();

        IntStream.range(0, 12).forEachOrdered(i -> {
            for (int j = 0; j < pers.size(); j++) {
                persArray[i][j] = (int) (pers.get(j).stats[i] * 4);
            }
            for (int j = 0; j < kart.size(); j++) {
                kartArray[i][j] = (int) (kart.get(j).stats[i] * 4);
            }
            for (int j = 0; j < roues.size(); j++) {
                rouesArray[i][j] = (int) (roues.get(j).stats[i] * 4);
            }
            for (int j = 0; j < ailes.size(); j++) {
                ailesArray[i][j] = (int) (ailes.get(j).stats[i] * 4);
            }
            persAverage[i] = averageByList.apply(i, pers.parallelStream());
            kartAverage[i] = averageByList.apply(i, kart.parallelStream());
            rouesAverage[i] = averageByList.apply(i, roues.parallelStream());
            ailesAverage[i] = averageByList.apply(i, ailes.parallelStream());
        });
    }

}
