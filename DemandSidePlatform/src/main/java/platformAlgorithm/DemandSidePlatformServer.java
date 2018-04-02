package platformAlgorithm;

import staticData.Environment;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by Vulpes on 2016-12-03.
 */
public class DemandSidePlatformServer {
    public static final String RESET_CMD = "reset";
    public static final String SHUTDOWN_CMD = "shutdown";

    public static boolean shutdown = false;
    public static boolean reset = false;

    private static int starterData(String[] args) {
        if (args.length < 2) {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Welcome to DSP! Type: DSP_MODEL_PATH DSP_ID PORT_NUMBER BUDGET BIDDING_ALGORITHM_CLASS TARGET_TAGS\n " +
                    "using 'NONE' as DSP_MODEL_PATH defaults to standard example model, using 'NONE' as algorithm defaults to HeartDroid");
            String command = scanner.nextLine();

            Environment.setModel(command.split(" ")[0]);
            Environment.setDsId(command.split(" ")[1]);
            Environment.setPortNo(Integer.parseInt(command.split(" ")[2]));
            Environment.setBudget(Double.parseDouble(command.split(" ")[3]));
            Environment.setBiddingAlgorithm(command.split(" ")[4]);
            Environment.setTargetTags(command.split(" ")[5]);
        } else {
            int i = 0;

            while (i < args.length) {
                switch (args[i]) {
                    case "-id":
                        i++;
                        if (!(i < args.length))
                            return -1;
                        Environment.setDsId(args[i]);
                        break;
                    case "-port":
                        i++;
                        if (!(i < args.length))
                            return -1;
                        Environment.setPortNo(Integer.parseInt(args[i]));
                        break;
                    case "-model":
                        i++;
                        if (!(i < args.length))
                            return -1;
                        Environment.setModel(args[i]);
                        break;
                    case "-budget":
                        i++;
                        if (!(i < args.length))
                            return -1;
                        Environment.setBudget(Double.parseDouble(args[i]));
                        break;
                    case "-algorithm":
                        i++;
                        if (!(i < args.length))
                            return -1;
                        Environment.setBiddingAlgorithm(args[i]);
                        break;
                    case "-adClass":
                        i++;
                        if (!(i < args.length))
                            return -1;
                        Environment.setAdClassResolver(args[i]);
                        break;
                    case "-winEstimation":
                        i++;
                        if (!(i < args.length))
                            return -1;
                        Environment.setWinChanceEstimator(args[i]);
                        break;
                    case "-winLearningRate":
                        i++;
                        if (!(i < args.length))
                            return -1;
                        Environment.setWinLearningRate(Double.valueOf(args[i]));
                        break;
                    case "-winLambdaRegularization":
                        i++;
                        if (!(i < args.length))
                            return -1;
                        Environment.setWinLambdaRegularization(Double.valueOf(args[i]));
                        break;
                    case "-tags":
                        i++;
                        if (!(i < args.length))
                            return -1;
                        Environment.setTargetTags(args[i]);
                        break;
                    case "-parent":
                        i++;
                        if (!(i < args.length))
                            return -1;
                        Environment.setParentPort(Integer.parseInt(args[i]));
                        break;
                    case "-statistics":
                        i++;
                        if (!(i < args.length))
                            return -1;
                        Environment.setStatisticsFile(args[i]);
                        break;
                }
                i++;
            }
        }
        return 0;
    }

    public static void main(String[] args) throws Exception {
        if (starterData(args) == -1) {
            System.err.println("Some of arguments were wrong!");
            return;
        }

        BiddingAlgorithm.getInstance().initialize();

        System.out.println(Environment.getPortNo());
        ServerSocket serverSocket = new ServerSocket(Environment.getPortNo());
        serverSocket.setSoTimeout(50);
        System.out.println("DSP Sever opened");
        notifyParent("up");

        ExecutorService connectionHandlers = Executors.newCachedThreadPool();

        while (!shutdown) {
            if(reset){
                connectionHandlers.shutdown();
                connectionHandlers.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
                BiddingAlgorithm.getInstance().reset();
                notifyParent("reset done");
                connectionHandlers = Executors.newCachedThreadPool();
                reset = false;
            }
            try {
                Socket conversation = serverSocket.accept();
                connectionHandlers.submit(new DSPIncomingConnectionHandler(conversation));
            }catch (SocketTimeoutException e){}
        }

        connectionHandlers.shutdown();
        connectionHandlers.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        notifyParent("down");
    }

    public static void notifyParent(String message){
        if(Environment.getParentPort() != null){
            try {
                Socket notice = new Socket("localhost", Environment.getParentPort());
                PrintWriter out = new PrintWriter(notice.getOutputStream(), true);
                out.println("["+Environment.getDsId()+"] "+message);
                out.flush();
                notice.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
