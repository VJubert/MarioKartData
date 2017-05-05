import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {

    static List<Obj> pers;
    static List<Obj> kart;
    static List<Obj> roues;
    static List<Obj> ailes;

    static Function<Stream<Obj>, Integer> numberOfGroup = stream -> stream.collect(Collectors.groupingBy(Function.identity())).size();
    static Function<Integer, BiFunction<Double, Stream<Obj>, List<Obj>>> groupByStat = stat -> (max, stream) -> stream.collect(Collectors.groupingBy(o -> o.stats[stat])).get(max);
    static Function<Integer, Comparator<Obj>> comparatorByStats = stats -> (o1, o2) -> o1.stats[stats] > o2.stats[stats] ? 1 : Objects.equals(o1.stats[stats], o2.stats[stats]) ? 0 : -1;
    static BiFunction<Integer, Stream<Obj>, Double> maxStats = (stat, stream) -> stream.mapToDouble(x -> x.stats[stat]).max().getAsDouble();


    public static void main(String[] args) {
        init();

        int stat = Obj.SpeedGround;
        findMax(stat);

        Model m = new Model("Test");
        IntVar pers = m.intVar("perso", 0, Main.pers.size() - 1, false);
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
                new Obj(ObjType.Perso, "Bébé Peach", new double[]{2.25, 2.5, 2.75, 2, 4, 2, 5, 4.5, 5, 5, 4.25, 4}),
                new Obj(ObjType.Perso, "Bébé Daisy", new double[]{2.25, 2.5, 2.75, 2, 4, 2, 5, 4.5, 5, 5, 4.25, 4}),
                new Obj(ObjType.Perso, "Bébé Harmonie", new double[]{2.25, 2.5, 2.75, 2, 4.25, 2, 4.75, 4.25, 4.75, 4.75, 3.75, 4}),
                new Obj(ObjType.Perso, "Lemmy", new double[]{2.25, 2.5, 2.75, 2, 4.25, 2, 4.75, 4.25, 4.75, 4.75, 3.75, 4}),
                new Obj(ObjType.Perso, "Bébé Mario", new double[]{2.5, 2.75, 3, 2.25, 4.25, 2.25, 4.5, 4, 4.5, 4.5, 4, 3.75}),
                new Obj(ObjType.Perso, "Bébé Luigi", new double[]{2.5, 2.75, 3, 2.25, 4.25, 2.25, 4.5, 4, 4.5, 4.5, 4, 3.75}),
                new Obj(ObjType.Perso, "Skelerex", new double[]{2.5, 2.75, 3, 2.25, 4.25, 2.25, 4.5, 4, 4.5, 4.5, 4, 3.75}),
                new Obj(ObjType.Perso, "Mii léger", new double[]{2.5, 2.75, 3, 2.25, 4.25, 2.25, 4.5, 4, 4.5, 4.5, 4, 3.75}),
                new Obj(ObjType.Perso, "Koopa", new double[]{2.75, 3, 3.25, 2.5, 4, 2.5, 4.5, 4, 4.5, 4.5, 4.25, 3.75}),
                new Obj(ObjType.Perso, "Laikitu", new double[]{2.75, 3, 3.25, 2.5, 4, 2.5, 4.5, 4, 4.5, 4.5, 4.25, 3.75}),
                new Obj(ObjType.Perso, "Bowser Jr", new double[]{2.75, 3, 3.25, 2.5, 4, 2.5, 4.5, 4, 4.5, 4.5, 4.25, 3.75}),
                new Obj(ObjType.Perso, "Toadette", new double[]{2.75, 3, 3.25, 2.5, 4.25, 2.5, 4.25, 3.75, 4.25, 4.25, 3.5, 3.75}),
                new Obj(ObjType.Perso, "Wendy", new double[]{2.75, 3, 3.25, 2.5, 4.25, 2.5, 4.25, 3.75, 4.25, 4.25, 3.5, 3.75}),
                new Obj(ObjType.Perso, "Isabelle", new double[]{2.75, 3, 3.25, 2.5, 4.25, 2.5, 4.25, 3.75, 4.25, 4.25, 3.5, 3.75}),
                new Obj(ObjType.Perso, "Toad", new double[]{3, 3.25, 3.5, 2.75, 4, 2.75, 4.25, 3.75, 4.25, 4.25, 4, 3.5}),
                new Obj(ObjType.Perso, "Maskass", new double[]{3, 3.25, 3.5, 2.75, 4, 2.75, 4.25, 3.75, 4.25, 4.25, 4, 3.5}),
                new Obj(ObjType.Perso, "Larry", new double[]{3, 3.25, 3.5, 2.75, 4, 2.75, 4.25, 3.75, 4.25, 4.25, 4, 3.5}),
                new Obj(ObjType.Perso, "Peach Chat", new double[]{3.25, 3.5, 3.75, 3, 4, 2.75, 4, 3.5, 4, 4, 3.75, 3.5}),
                new Obj(ObjType.Perso, "Inklink Femme", new double[]{3.25, 3.5, 3.75, 3, 4, 2.75, 4, 3.5, 4, 4, 3.75, 3.5}),
                new Obj(ObjType.Perso, "Villageoise", new double[]{3.25, 3.5, 3.75, 3, 4, 2.75, 4, 3.5, 4, 4, 3.75, 3.5}),
                new Obj(ObjType.Perso, "Peach", new double[]{3.5, 3.75, 4, 3.25, 3.75, 3, 3.75, 3.25, 3.75, 3.75, 3.75, 3.5}),
                new Obj(ObjType.Perso, "Daisy", new double[]{3.5, 3.75, 4, 3.25, 3.75, 3, 3.75, 3.25, 3.75, 3.75, 3.75, 3.5}),
                new Obj(ObjType.Perso, "Yoshi", new double[]{3.5, 3.75, 4, 3.25, 3.75, 3, 3.75, 3.25, 3.75, 3.75, 3.75, 3.5}),
                new Obj(ObjType.Perso, "Mario Tanuki", new double[]{3.5, 3.75, 4, 3.25, 3.75, 3.25, 3.75, 3.25, 3.75, 3.75, 3.25, 3.5}),
                new Obj(ObjType.Perso, "Inklink Homme", new double[]{3.5, 3.75, 4, 3.25, 3.75, 3, 3.75, 3.25, 3.75, 3.75, 3.75, 3.5}),
                new Obj(ObjType.Perso, "Villageois", new double[]{3.5, 3.75, 4, 3.25, 3.75, 3, 3.75, 3.25, 3.75, 3.75, 3.75, 3.5}),
                new Obj(ObjType.Perso, "Luigi", new double[]{3.75, 4, 4.25, 3.5, 3.5, 3.5, 3.75, 3.25, 3.75, 3.75, 3.25, 3.25}),
                new Obj(ObjType.Perso, "Iggy", new double[]{3.75, 4, 4.25, 3.5, 3.5, 3.5, 3.75, 3.25, 3.75, 3.75, 3.25, 3.25}),
                new Obj(ObjType.Perso, "Mario", new double[]{3.75, 4, 4.25, 3.5, 3.5, 3.5, 3.5, 3, 3.5, 3.5, 3.5, 3.25}),
                new Obj(ObjType.Perso, "Ludwig", new double[]{3.75, 4, 4.25, 3.5, 3.5, 3.5, 3.5, 3, 3.5, 3.5, 3.5, 3.25}),
                new Obj(ObjType.Perso, "Mii Moyen", new double[]{3.75, 4, 4.25, 3.5, 3.5, 3.5, 3.5, 3, 3.5, 3.5, 3.5, 3.25}),
                new Obj(ObjType.Perso, "Harmonie", new double[]{4, 4.25, 4.5, 3.75, 3.25, 3.75, 3.25, 2.75, 3.25, 3.25, 3.75, 3.25}),
                new Obj(ObjType.Perso, "Boo", new double[]{4, 4.25, 4.5, 3.75, 3.25, 3.75, 3.25, 2.75, 3.25, 3.25, 3.75, 3.25}),
                new Obj(ObjType.Perso, "Link", new double[]{4, 4.25, 4.5, 3.75, 3.25, 3.75, 3.25, 2.75, 3.25, 3.25, 3.75, 3.25}),
                new Obj(ObjType.Perso, "Waluigi", new double[]{4.5, 4.75, 5, 4.25, 3.25, 4, 3, 2.5, 3, 3, 3, 3}),
                new Obj(ObjType.Perso, "Donkey Kong", new double[]{4.5, 4.75, 5, 4.25, 3.25, 4, 3, 2.5, 3, 3, 3, 3}),
                new Obj(ObjType.Perso, "Roy", new double[]{4.5, 4.75, 5, 4.25, 3.25, 4, 3, 2.5, 3, 3, 3, 3}),
                new Obj(ObjType.Perso, "Wario", new double[]{4.75, 5, 5.25, 4.5, 3, 4.25, 2.75, 2.25, 2.75, 2.75, 3.25, 2.75}),
                new Obj(ObjType.Perso, "Bowser Squelette", new double[]{4.75, 5, 5.25, 4.5, 3, 4.25, 2.75, 2.25, 2.75, 2.75, 3.25, 2.75}),
                new Obj(ObjType.Perso, "Silver Mario", new double[]{4.25, 4.5, 4.75, 4, 3.25, 4.5, 3.25, 2.75, 3.25, 3.25, 3.25, 3}),
                new Obj(ObjType.Perso, "Peach Or Rose", new double[]{4.5, 4.75, 5, 4.25, 3.25, 4, 3, 2.5, 3, 3, 3, 3}),
                new Obj(ObjType.Perso, "Bowser", new double[]{4.75, 5, 5.25, 4.5, 3, 4.5, 2.5, 2, 2.5, 2.5, 3, 2.75}),
                new Obj(ObjType.Perso, "Morton", new double[]{4.75, 5, 5.25, 4.5, 3, 4.5, 2.5, 2, 2.5, 2.5, 3, 2.75}),
                new Obj(ObjType.Perso, "Mii Lourd", new double[]{4.75, 5, 5.25, 4.5, 3, 4.5, 2.5, 2, 2.5, 2.5, 3, 2.75})
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
                new Obj(ObjType.Ailes, "Super Glider", new double[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}),
                new Obj(ObjType.Ailes, "Cloud Glider", new double[]{-0.25, 0, -0.25, 0.25, 0.25, -0.25, 0, 0, 0.25, 0, 0, 0.25}),
                new Obj(ObjType.Ailes, "Wario Wing", new double[]{0, -0.25, 0, 0.25, 0, 0.25, 0, 0.25, 0, -0.25, -0.25, 0}),
                new Obj(ObjType.Ailes, "Waddle Wing", new double[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}),
                new Obj(ObjType.Ailes, "Peach Parasol", new double[]{-0.25, -0.25, -0.25, 0.25, 0.25, 0, 0, 0.25, 0.25, -0.25, -0.25, 0.25}),
                new Obj(ObjType.Ailes, "Parachute", new double[]{-0.25, 0, -0.25, 0.25, 0.25, -0.25, 0, 0, 0.25, 0, 0, 0.25}),
                new Obj(ObjType.Ailes, "Parafoil", new double[]{-0.25, -0.25, -0.25, 0.25, 0.25, 0, 0, 0.25, 0.25, -0.25, -0.25, 0.25}),
                new Obj(ObjType.Ailes, "Flower Glider", new double[]{-0.25, 0, -0.25, 0.25, 0.25, -0.25, 0, 0, 0.25, 0, 0, 0.25}),
                new Obj(ObjType.Ailes, "Bowser Kite", new double[]{-0.25, -0.25, -0.25, 0.25, 0.25, 0, 0, 0.25, 0.25, -0.25, -0.25, 0.25}),
                new Obj(ObjType.Ailes, "Plane Glider", new double[]{0, -0.25, 0, 0.25, 0, 0.25, 0, 0.25, 0, -0.25, -0.25, 0}),
                new Obj(ObjType.Ailes, "MKTV Parafoil", new double[]{-0.25, -0.25, -0.25, 0.25, 0.25, 0, 0, 0.25, 0.25, -0.25, -0.25, 0.25}),
                new Obj(ObjType.Ailes, "Gold Glider", new double[]{0, -0.25, 0, 0.25, 0, 0.25, 0, 0.25, 0, -0.25, -0.25, 0}),
                new Obj(ObjType.Ailes, "Hylian Kite", new double[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}),
                new Obj(ObjType.Ailes, "Paper Glider", new double[]{-0.25, 0, -0.25, 0.25, 0.25, -0.25, 0, 0, 0.25, 0, 0, 0.25})
        );


    }

}
