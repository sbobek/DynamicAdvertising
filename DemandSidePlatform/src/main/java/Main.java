import heart.State;


public class Main {
    public static void main(String[] args) {
        HeartService heartService = new HeartService();
        heartService.getModel();
//        heartService.printModelData();
//        heartService.printCurrent();
        State state = heartService.createDefaultState();
        heartService.runWithStartingState(state);
        heartService.printCurrent();
    }

}
