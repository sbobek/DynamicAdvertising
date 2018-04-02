package statistics;

/**
 * Created by Daniel Tyka on 2018-03-30.
 */
public class Budget {

    private final double startingBudget;
    private double availableBudget;

    public Budget(double startingBudget) {
        this.startingBudget = startingBudget;
        availableBudget = startingBudget;
    }

    public synchronized double reserveMoney(double requestedAmount){
        double reservedBudget = Math.min(availableBudget, requestedAmount);
        availableBudget -= reservedBudget;
        return reservedBudget;
    }

    public synchronized void returnMoney(double reservedBudget){
        availableBudget += reservedBudget;
    }

    public synchronized void reset(){
        availableBudget = startingBudget;
    }

    public double getSpentBudget() {
        return startingBudget - availableBudget;
    }

    public double getStartingBudget() {
        return startingBudget;
    }

    public double getAvailableBudget() {
        return availableBudget;
    }
}
