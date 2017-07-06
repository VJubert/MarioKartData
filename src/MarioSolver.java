import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Valentin on 29/06/2017.
 */
public class MarioSolver {
    private static final String DefaultName = "MarioKartData";
    private static int instanceId = 0;
    private Model model;
    private IntVar persConstraint;
    private IntVar kartConstraint;
    private IntVar roueConstraint;
    private IntVar aileConstraint;

    private List<Constraint> constraintList;

    public MarioSolver() {
        model = new Model(DefaultName + instanceId);
        instanceId++;
        constraintList = new ArrayList<>();
    }

    public IntVar addConstraint(int stat, String operator, int objective) throws IncoherentException {
        return addConstraint(stat, operator, Double.valueOf(objective));
    }

    public IntVar addConstraint(int stat, String operator, Double objective) throws IncoherentException {
        IntVar result;
        if (objective == null) {
            result = model.intVar(-24, 24);
        } else {
            if (objective > 6 || objective < -6) {
                throw new IncoherentException();
            } else {
                result = model.intVar((int) (objective * 4));
            }
        }
        constraintList.add(new Constraint(stat, operator, result));
        return result;
    }

    /**
     * Constraint a part of "set" with a single value
     *
     * @param type  The part of "set" you want to constraint
     * @param value The value according the main list of the part you decide
     * @throws IncoherentException Throw if the value are below 0 or above the max of the list
     */
    public void constraintElement(ObjType type, int value) throws IncoherentException {
        if (value < 0)
            throw new IncoherentException();
        switch (type) {
            case Roue:
                if (value >= Main.roues.size())
                    throw new IncoherentException();
                roueConstraint = model.intVar("roue", value);
                break;
            case Pers:
                if (value >= Main.pers.size())
                    throw new IncoherentException();
                persConstraint = model.intVar("pers", value);
                break;
            case Kart:
                if (value >= Main.kart.size())
                    throw new IncoherentException();
                kartConstraint = model.intVar("kart", value);
                break;
            case Aile:
                if (value >= Main.ailes.size())
                    throw new IncoherentException();
                aileConstraint = model.intVar("aile", value);
                break;
        }
    }

    /**
     * Constraint a part of "set" between 2 values
     *
     * @param type The part of "set" you want to constraint
     * @param min  The min value for this part
     * @param max  The max value for this part
     * @throws IncoherentException Throw if the min are below 0 or above the max, or the max are above the list
     */
    public void constraintElement(ObjType type, int min, int max) throws IncoherentException {
        if (min < 0 || max < min)
            throw new IncoherentException();
        switch (type) {
            case Roue:
                if (max >= Main.roues.size())
                    throw new IncoherentException();
                roueConstraint = model.intVar("roue", min, max);
                break;
            case Pers:
                if (max >= Main.pers.size())
                    throw new IncoherentException();
                persConstraint = model.intVar("pers", min, max);
                break;
            case Kart:
                if (max >= Main.kart.size())
                    throw new IncoherentException();
                kartConstraint = model.intVar("kart", min, max);
                break;
            case Aile:
                if (max >= Main.ailes.size())
                    throw new IncoherentException();
                aileConstraint = model.intVar("aile", min, max);
                break;
        }
    }

    /**
     * Constraint a part of "set" with set
     *
     * @param type   The part of "set" you want to constraint
     * @param values Values you want for this part
     * @throws IncoherentException Throw if there is values below 0 or above the max of list
     */
    public void constraintElement(ObjType type, int... values) throws IncoherentException {

        switch (type) {
            case Roue:
                for (int value : values) {
                    if (value < 0 || value >= Main.roues.size())
                        throw new IncoherentException();
                }
                roueConstraint = model.intVar("roue", values);
                break;
            case Pers:
                for (int value : values) {
                    if (value < 0 || value >= Main.pers.size())
                        throw new IncoherentException();
                }
                persConstraint = model.intVar("pers", values);
                break;
            case Kart:
                for (int value : values) {
                    if (value < 0 || value >= Main.kart.size())
                        throw new IncoherentException();
                }
                kartConstraint = model.intVar("kart", values);
                break;
            case Aile:
                for (int value : values) {
                    if (value < 0 || value >= Main.ailes.size())
                        throw new IncoherentException();
                }
                aileConstraint = model.intVar("aile", values);
                break;
        }
    }

    public void setObjective(boolean max, IntVar var) {
        model.setObjective(max, var);
    }

    public Solution findOne() throws IncoherentException {
        initSolve();
        org.chocosolver.solver.Solution s = model.getSolver().findSolution();
        if (s == null)
            throw new IncoherentException();
        else
            return new Solution(s.getIntVal(persConstraint), s.getIntVal(kartConstraint), s.getIntVal(roueConstraint), s.getIntVal(aileConstraint));
    }

    public List<Solution> findAll() throws IncoherentException {
        initSolve();
        List<org.chocosolver.solver.Solution> solutionList = model.getSolver().findAllSolutions();
        if (solutionList.size() == 0)
            throw new IncoherentException();
        else {
            return solutionList.parallelStream().map(sol -> new Solution(sol.getIntVal(persConstraint), sol.getIntVal(kartConstraint), sol.getIntVal(roueConstraint), sol.getIntVal(aileConstraint))).collect(Collectors.toList());
        }
    }

    private void initSolve() {
        if (persConstraint == null)
            persConstraint = model.intVar("pers", 0, Main.pers.size() - 1);
        if (kartConstraint == null)
            kartConstraint = model.intVar("kart", 0, Main.kart.size() - 1);
        if (roueConstraint == null)
            roueConstraint = model.intVar("roue", 0, Main.roues.size() - 1);
        if (aileConstraint == null)
            aileConstraint = model.intVar("aile", 0, Main.ailes.size() - 1);

        constraintList.forEach(x -> {
            IntVar resultPers = model.intVar(-24, 24);
            IntVar resultKart = model.intVar(-24, 24);
            IntVar resultRoue = model.intVar(-24, 24);
            IntVar resultAile = model.intVar(-24, 24);
            model.element(resultPers, Main.persArray[x.stat], persConstraint).post();
            model.element(resultKart, Main.kartArray[x.stat], kartConstraint).post();
            model.element(resultRoue, Main.rouesArray[x.stat], roueConstraint).post();
            model.element(resultAile, Main.ailesArray[x.stat], aileConstraint).post();
            model.scalar(new IntVar[]{resultPers, resultKart, resultRoue, resultAile}, new int[]{1, 1, 1, 1}, x.operator, x.result).post();
        });
    }

    private class Constraint {
        int stat;
        String operator;
        IntVar result;

        public Constraint(int stat, String operator, IntVar result) {
            this.stat = stat;
            this.operator = operator;
            this.result = result;
        }
    }
}
