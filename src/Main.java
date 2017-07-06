import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;

import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Main {

    public static final String newLine = System.getProperty("line.separator");
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
    static Function<Integer, ToDoubleFunction<Obj>> averageByStat = stat -> o -> o.stats[stat];

    public static void main(String[] args) {
        System.out.println("Initialisation ...");
        init();
        boolean quit = false;
        Scanner scan = new Scanner(System.in);
        MarioSolver solver = new MarioSolver();
        try {
            do {
                System.out.println("Menu");
                System.out.println("1 Ajouter contrainte");
                System.out.println("2 Résoudre");
                System.out.println("3 Voir les données");
                System.out.println("4 Réinitialiser le modèle");
                System.out.println("5 Aide");
                System.out.println("6 Quitter");
                int val = scan.nextInt();
                switch (val) {
                    case 1:
                        System.out.println("1 Contraindre \"set\"");
                        System.out.println("2 Contraindre \"stats\"");
                        System.out.println("3 Annuler");
                        int val1 = scan.nextInt();
                        switch (val1) {
                            case 1:
                                System.out.println("0 Personnage");
                                System.out.println("1 Kart");
                                System.out.println("2 Roues");
                                System.out.println("3 Ailes");
                                int type = scan.nextInt();
                                ObjType objType;
                                switch (type) {
                                    case 0:
                                        objType = ObjType.Pers;
                                        break;
                                    case 1:
                                        objType = ObjType.Kart;
                                        break;
                                    case 2:
                                        objType = ObjType.Roue;
                                        break;
                                    case 3:
                                        objType = ObjType.Aile;
                                        break;
                                    default:
                                        System.out.println("Option inconnu, KHAZAD AI-MENU !");
                                        continue;
                                }
                                System.out.println("1 Contraindre à une valeur");
                                System.out.println("2 Contraindre entre 2 valeurs");
                                System.out.println("3 Contraindre parmi plusieurs valeurs");
                                int v10 = scan.nextInt();
                                try {
                                    switch (v10) {
                                        case 1:
                                            System.out.println("Rentrez la valeur");
                                            solver.constraintElement(objType, scan.nextInt());
                                            break;
                                        case 2:
                                            System.out.println("Rentrez le minimum");
                                            int min = scan.nextInt();
                                            System.out.println("Rentrez le maximum");
                                            int max = scan.nextInt();
                                            solver.constraintElement(objType, min, max);
                                            break;
                                        case 3:
                                            System.out.println("Rentrez les valeurs séparés d'un espace");
                                            String[] tabValues = scan.next().split(" ");
                                            int[] intValues = Arrays.stream(tabValues).parallel().mapToInt(Integer::parseInt).toArray();
                                            solver.constraintElement(objType, intValues);
                                            break;
                                        default:
                                            System.out.println("Option inconnu, KHAZAD AI-MENU !");
                                            continue;
                                    }
                                } catch (IncoherentException e) {
                                    System.out.println("Valeur(s) hors limite");
                                } finally {
                                    break;
                                }
                            case 2:
                                try {
                                    System.out.println(Obj.GetAllStat());
                                    System.out.println("Rentrez la stat");
                                    int stat = scan.nextInt();
                                    System.out.println("Rentrez l'opérande");
                                    String op = scan.next();
                                    System.out.println("Rentrez l'objectif");
                                    double obj = scan.nextDouble();
                                    solver.addConstraint(stat, op, obj);
                                } catch (IncoherentException e) {
                                    System.out.println("Objectif hors limite");
                                } finally {
                                    break;
                                }
                            case 3:
                                break;
                            default:
                                System.out.println("Option inconnu, KHAZAD AI-MENU !");
                                break;
                        }
                        break;
                    case 2:
                        System.out.println("1 Afficher un seul solution");
                        System.out.println("2 Afficher toutes les solutions");
                        System.out.println("3 Annuler");
                        int val11 = scan.nextInt();
                        try {
                            switch (val11) {
                                case 1:
                                    printCombi(solver.findOne());
                                    break;
                                case 2:
                                    solver.findAll().forEach(Main::printCombi);
                                    break;
                                case 3:
                                    break;
                                default:
                                    System.out.println("Option inconnu, KHAZAD AI-MENU !");
                                    break;
                            }
                        } catch (IncoherentException e) {
                            System.out.println("Système incohérent !");
                        } finally {
                            break;
                        }
                    case 3:
                        seeData();
                        break;
                    case 4:
                        solver = new MarioSolver();
                        System.out.println("Ok");
                        break;
                    case 5:
                        System.out.println("Outil console réalisé par Valentin \"Valball\" Jubert");
                        System.out.println("L'outil permet de créer des \"sets\" pour Mario Kart 8 Deluxe");
                        System.out.println("Vous pouvez ajoutez autant de contraintes que vous voulez.");
                        System.out.println("Cependant prenez garde à ne pas rendre incohérent le modèle !");
                        break;
                    case 6:
                        quit = true;
                        scan.close();
                        break;
                    case 31415:
                        System.out.println("On sait s'amuser en Allemagne !");
                        pi();
                        break;
                    default:
                        System.out.println("Option inconnu, KHAZAD AI-MENU !");
                        break;
                }
            } while (!quit);
        } catch (InputMismatchException e) {
            System.out.println("La prochaine fois essaie de rentrer des chiffres !");
            System.exit(107);
        }
    }

    static void seeData() {
        System.out.println("Personnages ------------------------------");
        for (int i = 0; i < pers.size(); i++) {
            System.out.println(i + " " + findSame(pers.get(i)));
        }
        System.out.println("Kart ------------------------------");
        for (int i = 0; i < kart.size(); i++) {
            System.out.println(i + " " + findSame(kart.get(i)));
        }
        System.out.println("Roues ------------------------------");
        for (int i = 0; i < roues.size(); i++) {
            System.out.println(i + " " + findSame(roues.get(i)));
        }
        System.out.println("Ailes ------------------------------");
        for (int i = 0; i < ailes.size(); i++) {
            System.out.println(i + " " + findSame(ailes.get(i)));
        }
    }

    static List<Obj> findSame(Obj o) {
        return objs.stream().filter(x -> x.equals(o)).collect(Collectors.toList());
    }

    private static void printCombi(int pers, int kart, int roue, int aile) {
        Obj persRes = Main.pers.get(pers);
        Obj kartRes = Main.kart.get(kart);
        Obj rouesRes = Main.roues.get(roue);
        Obj ailesRes = Main.ailes.get(aile);
        System.out.println("Pers : " + findSame(persRes));
        System.out.println("Kart : " + findSame(kartRes));
        System.out.println("Roues : " + findSame(rouesRes));
        System.out.println("Ailes : " + findSame(ailesRes));
        for (int i = 0; i < 12; i++) {
            System.out.println(Obj.GetChar(i) + " : " + (persRes.stats[i] + kartRes.stats[i] + rouesRes.stats[i] + ailesRes.stats[i]));
        }
        System.out.println();
    }

    private static void printCombi(Solution s) {
        Obj persRes = Main.pers.get(s.pers);
        Obj kartRes = Main.kart.get(s.kart);
        Obj rouesRes = Main.roues.get(s.roue);
        Obj ailesRes = Main.ailes.get(s.aile);
        System.out.println("Pers : " + findSame(persRes));
        System.out.println("Kart : " + findSame(kartRes));
        System.out.println("Roues : " + findSame(rouesRes));
        System.out.println("Ailes : " + findSame(ailesRes));
        for (int i = 0; i < 12; i++) {
            System.out.println(Obj.GetChar(i) + " : " + (persRes.stats[i] + kartRes.stats[i] + rouesRes.stats[i] + ailesRes.stats[i]));
        }
        System.out.println();
    }

    private static void pi() {
        MarioSolver marioSolver = new MarioSolver();
        try {
            IntVar max = marioSolver.addConstraint(Obj.SpeedGround, ">=", 4);
            marioSolver.addConstraint(Obj.Weight, ">=", 4);
            marioSolver.addConstraint(Obj.Acceleration, ">=", 2.75);
            marioSolver.constraintElement(ObjType.Kart, 0, 7, 10, 13, 14, 2);
            marioSolver.constraintElement(ObjType.Pers, 13, 15);
            marioSolver.constraintElement(ObjType.Aile, 3);
            marioSolver.setObjective(Model.MAXIMIZE, max);
            List<Solution> solutionList = marioSolver.findAll();
            solutionList.forEach(Main::printCombi);
        } catch (IncoherentException e) {
            System.out.println("Système incohérent !");
        }
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
