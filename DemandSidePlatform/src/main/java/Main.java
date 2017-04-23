import heart.State;

import java.time.LocalDateTime;


public class Main {
    /*
    just for tests
     */
    public static void main(String[] args) {
        HeartService heartService = new HeartService();
        heartService.getModel();
        heartService.printModelData();
        heartService.printCurrent();
//        LocalDateTime start = LocalDateTime.now();
        State state = heartService.createDefaultState();
        heartService.runWithStartingState(state);
        heartService.printCurrent();
//        LocalDateTime stop = LocalDateTime.now();
//        long diffInMilli = java.time.Duration.between(start, stop).toMillis();
//        long diffInSeconds = java.time.Duration.between(start, stop).getSeconds();
//        long diffInMinutes = java.time.Duration.between(start, stop).toMinutes();
//
//        System.out.println("Work took full " + diffInMinutes + " minutes!");
//        System.out.println("Work took full " + diffInSeconds + " seconds!");
//        System.out.println("Work took full " + diffInMilli + " miliseconds!");
    }

}
