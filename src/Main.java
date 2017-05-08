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
import java.util.stream.Stream;

public class Main {

    static List<Obj> pers;
    static List<Obj> kart;
    static List<Obj> roues;
    static List<Obj> ailes;

    static Double[][] persArray;
    static Double[][] kartArray;
    static Double[][] rouesArray;
    static Double[][] ailesArray;

    static Double[] persAverage;
    static Double[] kartAverage;
    static Double[] rouesAverage;
    static Double[] ailesAverage;


    static Function<Stream<Obj>, Integer> numberOfGroup = stream -> stream.collect(Collectors.groupingBy(Function.identity())).size();
    static Function<Integer, BiFunction<Double, Stream<Obj>, List<Obj>>> groupByStat = stat -> (max, stream) -> stream.collect(Collectors.groupingBy(o -> o.stats[stat])).get(max);
    static Function<Integer, Comparator<Obj>> comparatorByStats = stats -> (o1, o2) -> o1.stats[stats] > o2.stats[stats] ? 1 : Objects.equals(o1.stats[stats], o2.stats[stats]) ? 0 : -1;
    static BiFunction<Integer, Stream<Obj>, Double> maxStats = (stat, stream) -> stream.mapToDouble(x -> x.stats[stat]).max().getAsDouble();
    static Function<Integer, ToDoubleFunction<Obj>> averageByStat = stat -> o -> o.stats[stat];
    static Stream<Integer> forAllStats = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11).parallelStream();


    public static void main(String[] args) {
        init();


        Model m = new Model("Test");
        IntVar pers = m.intVar("perso", 0, Main.pers.size() - 1);
        IntVar kart = m.intVar("kart", 0, Main.kart.size() - 1);
        IntVar roues = m.intVar("roues", 0, Main.roues.size() - 1);
        IntVar ailes = m.intVar("ailes", 0, Main.ailes.size() - 1);

        Solver solver = m.getSolver();
        if (solver.solve()) {
            Obj persRes = Main.pers.get(pers.getValue());
            Obj kartRes = Main.kart.get(kart.getValue());
            Obj rouesRes = Main.roues.get(roues.getValue());
            Obj ailesRes = Main.ailes.get(ailes.getValue());
            System.out.println(persRes);
            System.out.println(kartRes);
            System.out.println(rouesRes);
            System.out.println(ailesRes);
            for (int i = 0; i < 12; i++) {
                double res = persRes.stats[i] + kartRes.stats[i] + rouesRes.stats[i] + ailesRes.stats[i];
                System.out.println(Obj.GetChar(i) + " : " + res);
            }
        }

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
        pers = Arrays.asList(
                new Obj(ObjType.Pers, "Bébé Peach", new double[]{2.25, 2.5, 2.75, 2, 4, 2, 5, 4.5, 5, 5, 4.25, 4}),
                new Obj(ObjType.Pers, "Bébé Daisy", new double[]{2.25, 2.5, 2.75, 2, 4, 2, 5, 4.5, 5, 5, 4.25, 4}),
                new Obj(ObjType.Pers, "Bébé Harmonie", new double[]{2.25, 2.5, 2.75, 2, 4.25, 2, 4.75, 4.25, 4.75, 4.75, 3.75, 4}),
                new Obj(ObjType.Pers, "Lemmy", new double[]{2.25, 2.5, 2.75, 2, 4.25, 2, 4.75, 4.25, 4.75, 4.75, 3.75, 4}),
                new Obj(ObjType.Pers, "Bébé Mario", new double[]{2.5, 2.75, 3, 2.25, 4.25, 2.25, 4.5, 4, 4.5, 4.5, 4, 3.75}),
                new Obj(ObjType.Pers, "Bébé Luigi", new double[]{2.5, 2.75, 3, 2.25, 4.25, 2.25, 4.5, 4, 4.5, 4.5, 4, 3.75}),
                new Obj(ObjType.Pers, "Skelerex", new double[]{2.5, 2.75, 3, 2.25, 4.25, 2.25, 4.5, 4, 4.5, 4.5, 4, 3.75}),
                new Obj(ObjType.Pers, "Mii léger", new double[]{2.5, 2.75, 3, 2.25, 4.25, 2.25, 4.5, 4, 4.5, 4.5, 4, 3.75}),
                new Obj(ObjType.Pers, "Koopa", new double[]{2.75, 3, 3.25, 2.5, 4, 2.5, 4.5, 4, 4.5, 4.5, 4.25, 3.75}),
                new Obj(ObjType.Pers, "Laikitu", new double[]{2.75, 3, 3.25, 2.5, 4, 2.5, 4.5, 4, 4.5, 4.5, 4.25, 3.75}),
                new Obj(ObjType.Pers, "Bowser Jr", new double[]{2.75, 3, 3.25, 2.5, 4, 2.5, 4.5, 4, 4.5, 4.5, 4.25, 3.75}),
                new Obj(ObjType.Pers, "Toadette", new double[]{2.75, 3, 3.25, 2.5, 4.25, 2.5, 4.25, 3.75, 4.25, 4.25, 3.5, 3.75}),
                new Obj(ObjType.Pers, "Wendy", new double[]{2.75, 3, 3.25, 2.5, 4.25, 2.5, 4.25, 3.75, 4.25, 4.25, 3.5, 3.75}),
                new Obj(ObjType.Pers, "Isabelle", new double[]{2.75, 3, 3.25, 2.5, 4.25, 2.5, 4.25, 3.75, 4.25, 4.25, 3.5, 3.75}),
                new Obj(ObjType.Pers, "Toad", new double[]{3, 3.25, 3.5, 2.75, 4, 2.75, 4.25, 3.75, 4.25, 4.25, 4, 3.5}),
                new Obj(ObjType.Pers, "Maskass", new double[]{3, 3.25, 3.5, 2.75, 4, 2.75, 4.25, 3.75, 4.25, 4.25, 4, 3.5}),
                new Obj(ObjType.Pers, "Larry", new double[]{3, 3.25, 3.5, 2.75, 4, 2.75, 4.25, 3.75, 4.25, 4.25, 4, 3.5}),
                new Obj(ObjType.Pers, "Peach Chat", new double[]{3.25, 3.5, 3.75, 3, 4, 2.75, 4, 3.5, 4, 4, 3.75, 3.5}),
                new Obj(ObjType.Pers, "Inklink Femme", new double[]{3.25, 3.5, 3.75, 3, 4, 2.75, 4, 3.5, 4, 4, 3.75, 3.5}),
                new Obj(ObjType.Pers, "Villageoise", new double[]{3.25, 3.5, 3.75, 3, 4, 2.75, 4, 3.5, 4, 4, 3.75, 3.5}),
                new Obj(ObjType.Pers, "Peach", new double[]{3.5, 3.75, 4, 3.25, 3.75, 3, 3.75, 3.25, 3.75, 3.75, 3.75, 3.5}),
                new Obj(ObjType.Pers, "Daisy", new double[]{3.5, 3.75, 4, 3.25, 3.75, 3, 3.75, 3.25, 3.75, 3.75, 3.75, 3.5}),
                new Obj(ObjType.Pers, "Yoshi", new double[]{3.5, 3.75, 4, 3.25, 3.75, 3, 3.75, 3.25, 3.75, 3.75, 3.75, 3.5}),
                new Obj(ObjType.Pers, "Mario Tanuki", new double[]{3.5, 3.75, 4, 3.25, 3.75, 3.25, 3.75, 3.25, 3.75, 3.75, 3.25, 3.5}),
                new Obj(ObjType.Pers, "Inklink Homme", new double[]{3.5, 3.75, 4, 3.25, 3.75, 3, 3.75, 3.25, 3.75, 3.75, 3.75, 3.5}),
                new Obj(ObjType.Pers, "Villageois", new double[]{3.5, 3.75, 4, 3.25, 3.75, 3, 3.75, 3.25, 3.75, 3.75, 3.75, 3.5}),
                new Obj(ObjType.Pers, "Luigi", new double[]{3.75, 4, 4.25, 3.5, 3.5, 3.5, 3.75, 3.25, 3.75, 3.75, 3.25, 3.25}),
                new Obj(ObjType.Pers, "Iggy", new double[]{3.75, 4, 4.25, 3.5, 3.5, 3.5, 3.75, 3.25, 3.75, 3.75, 3.25, 3.25}),
                new Obj(ObjType.Pers, "Mario", new double[]{3.75, 4, 4.25, 3.5, 3.5, 3.5, 3.5, 3, 3.5, 3.5, 3.5, 3.25}),
                new Obj(ObjType.Pers, "Ludwig", new double[]{3.75, 4, 4.25, 3.5, 3.5, 3.5, 3.5, 3, 3.5, 3.5, 3.5, 3.25}),
                new Obj(ObjType.Pers, "Mii Moyen", new double[]{3.75, 4, 4.25, 3.5, 3.5, 3.5, 3.5, 3, 3.5, 3.5, 3.5, 3.25}),
                new Obj(ObjType.Pers, "Harmonie", new double[]{4, 4.25, 4.5, 3.75, 3.25, 3.75, 3.25, 2.75, 3.25, 3.25, 3.75, 3.25}),
                new Obj(ObjType.Pers, "Boo", new double[]{4, 4.25, 4.5, 3.75, 3.25, 3.75, 3.25, 2.75, 3.25, 3.25, 3.75, 3.25}),
                new Obj(ObjType.Pers, "Link", new double[]{4, 4.25, 4.5, 3.75, 3.25, 3.75, 3.25, 2.75, 3.25, 3.25, 3.75, 3.25}),
                new Obj(ObjType.Pers, "Waluigi", new double[]{4.5, 4.75, 5, 4.25, 3.25, 4, 3, 2.5, 3, 3, 3, 3}),
                new Obj(ObjType.Pers, "Donkey Kong", new double[]{4.5, 4.75, 5, 4.25, 3.25, 4, 3, 2.5, 3, 3, 3, 3}),
                new Obj(ObjType.Pers, "Roy", new double[]{4.5, 4.75, 5, 4.25, 3.25, 4, 3, 2.5, 3, 3, 3, 3}),
                new Obj(ObjType.Pers, "Wario", new double[]{4.75, 5, 5.25, 4.5, 3, 4.25, 2.75, 2.25, 2.75, 2.75, 3.25, 2.75}),
                new Obj(ObjType.Pers, "Bowser Squelette", new double[]{4.75, 5, 5.25, 4.5, 3, 4.25, 2.75, 2.25, 2.75, 2.75, 3.25, 2.75}),
                new Obj(ObjType.Pers, "Silver Mario", new double[]{4.25, 4.5, 4.75, 4, 3.25, 4.5, 3.25, 2.75, 3.25, 3.25, 3.25, 3}),
                new Obj(ObjType.Pers, "Peach Or Rose", new double[]{4.5, 4.75, 5, 4.25, 3.25, 4, 3, 2.5, 3, 3, 3, 3}),
                new Obj(ObjType.Pers, "Bowser", new double[]{4.75, 5, 5.25, 4.5, 3, 4.5, 2.5, 2, 2.5, 2.5, 3, 2.75}),
                new Obj(ObjType.Pers, "Morton", new double[]{4.75, 5, 5.25, 4.5, 3, 4.5, 2.5, 2, 2.5, 2.5, 3, 2.75}),
                new Obj(ObjType.Pers, "Mii Lourd", new double[]{4.75, 5, 5.25, 4.5, 3, 4.5, 2.5, 2, 2.5, 2.5, 3, 2.75})
        );

        kart = Arrays.asList(
                new Obj(ObjType.Kart, "Standard Kart", new double[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}),
                new Obj(ObjType.Kart, "Pipe Frame", new double[]{-0.5, 0, -0.5, -0.5, 0.5, -0.25, 0.5, 0.5, -0.25, 0.25, 0.25, 0.5}),
                new Obj(ObjType.Kart, "Mach 8", new double[]{0, 0, 0.25, 0.5, -0.25, 0.25, -0.25, 0, -0.25, 0.25, 0.25, 0}),
                new Obj(ObjType.Kart, "Steel Driver", new double[]{0.25, 0.5, -0.75, -0.25, -0.75, 0.5, -0.5, 0.75, -0.5, -0.5, 0, -0.5}),
                new Obj(ObjType.Kart, "Cat Cruiser", new double[]{-0.25, -0.25, 0.25, 0, 0.25, 0, 0.25, 0, 0.25, 0, 0, 0.25}),
                new Obj(ObjType.Kart, "Circuit Special", new double[]{0.5, -0.5, -0.25, 0.25, -0.75, 0.25, -0.5, -0.25, -0.75, -0.25, -0.5, -0.75}),
                new Obj(ObjType.Kart, "Tri-Speeder", new double[]{0.25, 0.5, -0.75, -0.25, -0.75, 0.5, -0.5, 0.75, -0.5, -0.5, 0, 0 - 0.5}),
                new Obj(ObjType.Kart, "Badwagon", new double[]{0.5, -0.25, -0.5, 0, -1, 0.5, -0.75, -0.25, -0.75, -0.5, 0.5, -1}),
                new Obj(ObjType.Kart, "Prancer", new double[]{0.25, 0, 0, 0, -0.5, -0.25, 0, 0.25, 0, -0.25, -0.25, -0.25}),
                new Obj(ObjType.Kart, "Biddybuggy", new double[]{-0.75, -0.5, -0.5, -0.25, 0.75, -0.5, 0.5, 0.5, 0.25, 0.5, 0.25, 0.75}),
                new Obj(ObjType.Kart, "Landship", new double[]{-0.5, 0.5, -0.25, -0.75, 0.5, -0.5, 0.25, 0.75, 0, -0.25, 0.75, 0.5}),
                new Obj(ObjType.Kart, "Sneeker", new double[]{0.25, -0.25, 0, 0, -0.5, 0, 0, 0, -0.25, 0, -0.75, -0.25}),
                new Obj(ObjType.Kart, "Sports Coupe", new double[]{0, 0, 0.25, 0.5, -0.25, 0.25, -0.25, 0, 0.25, 0.25, 0.25, 0}),
                new Obj(ObjType.Kart, "Gold Standard", new double[]{0.25, -0.25, 0, 0, -0.5, 0, 0, 0, -0.25, 0, -0.75, -0.25}),
                new Obj(ObjType.Kart, "GLA", new double[]{0.5, -0.25, -0.5, 0, -1, 0.5, -0.75, -0.25, -0.75, 0.5, 0.5, -1}),
                new Obj(ObjType.Kart, "W25 Silver Arrow", new double[]{-0.25, -0.25, 0, 0.25, 0.25, -0.25, 0.25, 0.25, 0, 0.25, 0.5, 0.25}),
                new Obj(ObjType.Kart, "300 SL Roadster", new double[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}),
                new Obj(ObjType.Kart, "Blue Falcon", new double[]{0.25, -0.25, 0, 0.25, -0.25, -0., -0.25, 0.25, -0.5, 0.5, 0, -0.25}),
                new Obj(ObjType.Kart, "Tanooki Kart", new double[]{-0.25, 0.25, 0, 0, -0.5, 0.25, 0.25, 0.5, 0, 0, 1, -0.25}),
                new Obj(ObjType.Kart, "B Dasher", new double[]{0.5, -0.5, -0.25, 0.25, -0.75, 0.25, -0.5, -0.25, -0.75, -0.25, -0.5, -0.75}),
                new Obj(ObjType.Kart, "P-Wing", new double[]{0.5, -0.5, -0.25, 0.25, -0.75, 0.25, -0.5, -0.25, -0.75, -0.25, -0.5, -0.75}),
                new Obj(ObjType.Kart, "Koopa Clown", new double[]{-0.25, 0.25, 0, 0, -0.5, 0.25, 0.25, 0.5, 0, 0, 1, -0.25}),
                new Obj(ObjType.Kart, "Standard Bike", new double[]{-0.25, -0.25, 0, 0.25, 0.25, -0.25, 0.25, 0.25, 0, 0.25, 0.5, 0.25}),
                new Obj(ObjType.Kart, "Comet", new double[]{0 - 0.25, -0.25, 0.25, 0, 0.25, 0, 0.25, 0, 0.25, 0, 0, 0.25}),
                new Obj(ObjType.Kart, "Sport Bike", new double[]{0.25, 0, 0, 0, -0.5, -0.25, 0, 0.25, 0, -0.25, -0.25, -0.25}),
                new Obj(ObjType.Kart, "The Duke", new double[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}),
                new Obj(ObjType.Kart, "Flame Rider", new double[]{-0.25, -0.25, 0, 0.25, 0.25, -0.25, 0.25, 0.25, 0, 0.25, 0.5, 0.25}),
                new Obj(ObjType.Kart, "Vamint", new double[]{-0.5, 0, -0.5, -0.5, 0.5, -0.25, 0.5, 0.5, -0.25, 0.25, 0.25, 0.5}),
                new Obj(ObjType.Kart, "Mr. Scooty", new double[]{-0.75, -0.5, -0.5, -0.25, 0.75, -0.5, 0.5, 0.5, 0.25, 0.5, 0.25, 0.75}),
                new Obj(ObjType.Kart, "Jet Bike", new double[]{0.25, 0, 0, 0, -0.5, -0.25, 0, 0.25, 0, -0.25, -0.25, -0.25}),
                new Obj(ObjType.Kart, "Yoshi Bike", new double[]{-0.25, -0.25, 0.25, 0, 0.25, 0, 0.25, 0, 0.25, 0, 0, 0.25}),
                new Obj(ObjType.Kart, "Master Cycle", new double[]{0.25, -0.25, 0, 0, -0.5, 0, 0, 0, -0.25, 0, -0.75, -0.25}),
                new Obj(ObjType.Kart, "City Tripper", new double[]{-0.5, 0, -0.5, -0.5, 0.5, -0.25, 0.5, 0.5, -0.25, 0.25, 0.25, 0.5}),
                new Obj(ObjType.Kart, "Standard ATV", new double[]{0.5, -0.25, -0.5, 0, -1, 0.5, -0.75, -0.25, -0.75, -0.5, 0.5, -1}),
                new Obj(ObjType.Kart, "Wild Wiggler", new double[]{-0.25, -0.25, 0, 0.25, 0.25, -0.25, 0.25, 0.25, 0, 0.5, 0.5, 0.25}),
                new Obj(ObjType.Kart, "Teddy Buggy", new double[]{-0.25, -0.25, 0.25, 0, 0.25, 0, 0.25, 0, 0.25, 0, 0, 0.25}),
                new Obj(ObjType.Kart, "Bone Rattler", new double[]{0.25, 0.5, -0.75, -0.25, -0.75, 0.5, -0.5, 0.75, -0.5, -0.5, 0, -0.}),
                new Obj(ObjType.Kart, "Inkstriker", new double[]{0, 0, 0.5, 0.5, -0.25, 0.25, -0.25, 0, -0.25, 0.25, 0.25, 0}),
                new Obj(ObjType.Kart, "Splat Buggy", new double[]{0.25, -0.25, 0, 0.25, -0.25, -0.5, -0.25, 0.25, -0.5, 0.5, 0, -0.25})
        );

        roues = Arrays.asList(
                new Obj(ObjType.Roue, "Standard", new double[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}),
                new Obj(ObjType.Roue, "Monster", new double[]{0, -0.25, -0.5, 0, -0.5, 0.5, -0.75, -0.5, -0.5, -0.75, 0.5, -0.25}),
                new Obj(ObjType.Roue, "Roller", new double[]{-0.5, 0, 0, -0.5, 0.5, -0.5, 0.25, 0.25, 0.25, 0.25, -0.25, 0.75}),
                new Obj(ObjType.Roue, "Slim", new double[]{0.25, -0.25, -0.25, 0.5, -0.5, 0, .25, 0.25, 0.25, 0, -1, -0.25}),
                new Obj(ObjType.Roue, "Slick", new double[]{0.5, -0.75, -0.75, 0.5, -0.75, 0.25, -0.25, -0.75, -0.5, -0.25, -1.25, -0.75}),
                new Obj(ObjType.Roue, "Metal", new double[]{0.5, 0, -0.25, -0.25, -1, 0.5, -0.25, -0.25, -0.75, -0.5, -0.75, -0.75}),
                new Obj(ObjType.Roue, "Button", new double[]{-0.25, -0.25, -0.25, 0, 0.25, -0.5, 0, 0, -0.25, 0.25, -0.5, 0.5}),
                new Obj(ObjType.Roue, "Off-Road", new double[]{0.25, 0.25, -0.5, 0, -0.25, 0.25, -0.5, -0.5, -0.5, -0.25, 0.25, -0.5}),
                new Obj(ObjType.Roue, "Sponge", new double[]{-0.25, -0.5, 0.25, -0.25, 0, -0.25, -0.25, -0.25, 0, 0, 0.25, 0.25}),
                new Obj(ObjType.Roue, "Wood", new double[]{0.25, -0.25, -0.25, 0.5, -0.5, 0, 0.25, 0.25, 0.25, 0, -1, -0.25}),
                new Obj(ObjType.Roue, "Cushion", new double[]{-0.25, -0.5, 0.25, -0.25, 0, -0.25, -0.25, 0.5, 0, -0.25, 0.25, 0.25}),
                new Obj(ObjType.Roue, "Blue Standard", new double[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}),
                new Obj(ObjType.Roue, "Hot Monster", new double[]{0, -0.25, -0.5, 0, -0.5, 0.5, -0.75, -0.5, -0.5, -0.75, 0.5, -0.2}),
                new Obj(ObjType.Roue, "Azure Roller", new double[]{-0.5, 0, 0, -0.5, 0.5, -0.5, 0.25, 0.25, 0.25, 0.25, -0.25, 0.75}),
                new Obj(ObjType.Roue, "Crimson Slim", new double[]{0.25, -0.25, -0.25, 0.5, -0.5, 0, 0.25, 0.25, 0.25, 0, -1, -0.25}),
                new Obj(ObjType.Roue, "Cyber Slick", new double[]{0.5, -0.75, -0.75, 0.5, -0.75, 0.25, -0.25, -0.75, -0.5, -0.25, -1.25, -0.75}),
                new Obj(ObjType.Roue, "Retro Off-Road", new double[]{0.25, 0.25, -0.5, 0, -0.25, 0.25, -0.5, -0.5, -0.25, -0.25, 0.25, -0.5}),
                new Obj(ObjType.Roue, "Gold Tires", new double[]{0.5, 0, -0.25, -0.25, -1, 0.5, -0.25, -0.25, -0.75, -0.5, -0.75, -0.75}),
                new Obj(ObjType.Roue, "GLA Tires", new double[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}),
                new Obj(ObjType.Roue, "Triforce Tires", new double[]{0.25, 0.25, -0.5, 0, -0.25, 0.25, -0.5, -0.5, -0.25, -0.25, 0.25, -0.5}),
                new Obj(ObjType.Roue, "Leaf Tires", new double[]{-0.25, -0.25, -0.25, 0, 0.25, -0.5, 0, 0, -0.25, 0.25, -0.5, 0.5})
        );

        ailes = Arrays.asList(
                new Obj(ObjType.Aile, "Super Glider", new double[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}),
                new Obj(ObjType.Aile, "Cloud Glider", new double[]{-0.25, 0, -0.25, 0.25, 0.25, -0.25, 0, 0, 0.25, 0, 0, 0.25}),
                new Obj(ObjType.Aile, "Wario Wing", new double[]{0, -0.25, 0, 0.25, 0, 0.25, 0, 0.25, 0, -0.25, -0.25, 0}),
                new Obj(ObjType.Aile, "Waddle Wing", new double[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}),
                new Obj(ObjType.Aile, "Peach Parasol", new double[]{-0.25, -0.25, -0.25, 0.25, 0.25, 0, 0, 0.25, 0.25, -0.25, -0.25, 0.25}),
                new Obj(ObjType.Aile, "Parachute", new double[]{-0.25, 0, -0.25, 0.25, 0.25, -0.25, 0, 0, 0.25, 0, 0, 0.25}),
                new Obj(ObjType.Aile, "Parafoil", new double[]{-0.25, -0.25, -0.25, 0.25, 0.25, 0, 0, 0.25, 0.25, -0.25, -0.25, 0.25}),
                new Obj(ObjType.Aile, "Flower Glider", new double[]{-0.25, 0, -0.25, 0.25, 0.25, -0.25, 0, 0, 0.25, 0, 0, 0.25}),
                new Obj(ObjType.Aile, "Bowser Kite", new double[]{-0.25, -0.25, -0.25, 0.25, 0.25, 0, 0, 0.25, 0.25, -0.25, -0.25, 0.25}),
                new Obj(ObjType.Aile, "Plane Glider", new double[]{0, -0.25, 0, 0.25, 0, 0.25, 0, 0.25, 0, -0.25, -0.25, 0}),
                new Obj(ObjType.Aile, "MKTV Parafoil", new double[]{-0.25, -0.25, -0.25, 0.25, 0.25, 0, 0, 0.25, 0.25, -0.25, -0.25, 0.25}),
                new Obj(ObjType.Aile, "Gold Glider", new double[]{0, -0.25, 0, 0.25, 0, 0.25, 0, 0.25, 0, -0.25, -0.25, 0}),
                new Obj(ObjType.Aile, "Hylian Kite", new double[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}),
                new Obj(ObjType.Aile, "Paper Glider", new double[]{-0.25, 0, -0.25, 0.25, 0.25, -0.25, 0, 0, 0.25, 0, 0, 0.25})
        );

        persArray = new Double[pers.size()][12];
        for (int i = 0; i < pers.size(); i++) {
            persArray[i] = pers.get(i).stats;
        }
        kartArray = new Double[kart.size()][12];
        for (int i = 0; i < kart.size(); i++) {
            kartArray[i] = kart.get(i).stats;
        }
        rouesArray = new Double[roues.size()][12];
        for (int i = 0; i < roues.size(); i++) {
            rouesArray[i] = roues.get(i).stats;
        }
        ailesArray = new Double[ailes.size()][12];
        for (int i = 0; i < ailes.size(); i++) {
            ailesArray[i] = ailes.get(i).stats;
        }

        persAverage = new Double[12];
        kartAverage = new Double[12];
        rouesAverage = new Double[12];
        ailesAverage = new Double[12];
        BiFunction<Integer, Stream<Obj>, Double> averageByList = (i, s) -> s.mapToDouble(averageByStat.apply(i)).average().getAsDouble();

        forAllStats.forEach(i -> {
            persAverage[i] = averageByList.apply(i, pers.parallelStream());
            kartAverage[i] = averageByList.apply(i, kart.parallelStream());
            rouesAverage[i] = averageByList.apply(i, roues.parallelStream());
            ailesAverage[i] = averageByList.apply(i, ailes.parallelStream());
        });
    }

}
