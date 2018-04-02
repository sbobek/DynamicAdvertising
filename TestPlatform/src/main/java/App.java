import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class App {
    private static final String PROJECT_ROOT_DIR = Paths.get(System.getProperty("user.dir"), "/..").normalize().toString();
    private static final File DSP_PROJECT_ROOT = Paths.get(PROJECT_ROOT_DIR, "DemandSidePlatform").toFile();
    private static final File ADX_PROJECT_ROOT = Paths.get(PROJECT_ROOT_DIR, "AdExchange").toFile();
    private static final File SSP_PROJECT_ROOT = Paths.get(PROJECT_ROOT_DIR, "SupplySidePlatform").toFile();

    private static final Path LOG_FOLDER = Paths.get(PROJECT_ROOT_DIR, "log");
    private static final String DSP_STATISTICS_FILE = "dsp%d.log";
    private static final int PARENT_PORT = 8999;

    private static List<Process> processes = new LinkedList<>();
    private static ServerSocket parentServer;

    public static void main(String[] args) throws IOException, InterruptedException {
        final int NUMBER_OF_DSP = 2;

        Runtime.getRuntime().addShutdownHook(new Thread(() -> processes.stream().filter(Process::isAlive).forEach(Process::destroyForcibly)));
        compileProject();

        startParentServer();
        for(int i=1; i<=NUMBER_OF_DSP; i++){
            runDSP(i);
        }
        waitForNotification(NUMBER_OF_DSP); // DSP UP

        runADX(NUMBER_OF_DSP);
        waitForNotification(); // ADX UP

        runSSP();
        waitForNotification(); // SSP UP

        waitForNotification(); // SSP DONE
        resetDSP(NUMBER_OF_DSP);


        shutdown(NUMBER_OF_DSP);
        closeParentServer();
        awaitTermination();
    }

    private static void shutdown(int numberOfDSP) throws IOException {
        sendMessage(9000, "shutdown"); // ADX
        for(int i = 1; i<= numberOfDSP; i++){
            sendMessage(9000 + i, "shutdown"); // DSP
        }
        waitForNotification(numberOfDSP + 1); // ADX + DSP
    }

    private static void resetDSP(int numberOfDSP) throws IOException {
        for(int i = 1; i<= numberOfDSP; i++){
            sendMessage(9000 + i, "reset");
        }
        waitForNotification(numberOfDSP);
    }

    private static void sendMessage(int port, String message) throws IOException {
        Socket socket = new Socket("localhost", port);
        PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
        out.print(message);
        out.flush();
        out.close();
        socket.close();
    }

    private static void startParentServer() throws IOException {
        parentServer = new ServerSocket(PARENT_PORT);
    }

    private static void waitForNotification(int n) throws IOException {
        if(parentServer != null) {
            for(int i = 0; i < n; i++) {
                Socket s = parentServer.accept();
                BufferedReader r = new BufferedReader(new InputStreamReader(s.getInputStream()));
                String str = r.readLine();
                System.out.println(str);
                s.close();
            }
        }
    }

    private static void waitForNotification() throws IOException {
        waitForNotification(1);
    }

    private static void closeParentServer() throws IOException {
        parentServer.close();
        parentServer = null;
    }


    private static void awaitTermination() throws InterruptedException {
        for(Process p: processes){
            p.waitFor();
        }
    }

    private static void compileProject() throws IOException, InterruptedException {
        Process p = new ProcessBuilder().command(Arrays.asList("mvn", "install"))
                .directory(new File(PROJECT_ROOT_DIR))
                .redirectOutput(ProcessBuilder.Redirect.INHERIT)
                .redirectErrorStream(true)
                .start();
        processes.add(p);
        p.waitFor();
        processes.clear();
    }

    private static Process runDSP(int number) throws IOException {
        String args = getDSPArgs(number, "DSP"+number, Integer.toString(9000+number), "../DemandSidePlatform/src/main/resources/WeekDayModel.hmr", "FISHING,FISH", String.valueOf(40));
        List<String> cmd = getRunCmd(args);
        System.out.println(cmd.stream().collect(Collectors.joining(" ")));

        Process p = new ProcessBuilder().command(cmd)
                .directory(DSP_PROJECT_ROOT)
                .redirectOutput(ProcessBuilder.Redirect.INHERIT)
                .redirectErrorStream(true)
                .start();
        processes.add(p);
        return p;
    }

    private static Process runADX(int numberOfDSP) throws IOException {
        String args = getADXArgs(numberOfDSP);
        List<String> cmd = getRunCmd(args);
        System.out.println(cmd.stream().collect(Collectors.joining(" ")));

        Process p = new ProcessBuilder().command(cmd)
                .directory(ADX_PROJECT_ROOT)
//                .redirectOutput(ProcessBuilder.Redirect.INHERIT)
                .redirectErrorStream(true)
                .start();
        processes.add(p);
        return p;
    }

    private static Process runSSP() throws IOException {
        String args = getSSPArgs("FISH,FISHING,TOOLS,NETS,HOOKS,BOATS");
        List<String> cmd = getRunCmd(args);
        System.out.println(cmd.stream().collect(Collectors.joining(" ")));

        Process p = new ProcessBuilder().command(cmd)
                .directory(SSP_PROJECT_ROOT)
//                .redirectOutput(ProcessBuilder.Redirect.INHERIT)
                .redirectErrorStream(true)
                .start();
        processes.add(p);
        return p;
    }

    private static String getParentArg(){
        return parentServer!=null ? " -parent 8999" : "";
    }

    private static List<String> getRunCmd(String args){
        return Arrays.asList("mvn", "exec:java", "-Dexec.classpathScope=compile", "-Dexec.args=" +args);
    }

    private static String getDSPArgs(int number, String id, String port, String model, String tags, String budget){
        String statisticsFile = LOG_FOLDER.resolve(String.format(DSP_STATISTICS_FILE, number)).toString();
        return "-id " + id + " -port " + port + " -model " + model + " -statistics " + statisticsFile + " -budget " + budget + " -tags " + tags + getParentArg();
    }

    private static String getADXArgs(int numberOfDSP){
        return "-port 9000" + getParentArg() + " -dsp " + IntStream.range(0,numberOfDSP)
                                                                   .map(x -> x + 9001)
                                                                   .mapToObj(String::valueOf)
                                                                   .collect(Collectors.joining(" "));
    }

    private static String getSSPArgs(String tags){
        return "-port 9000 -tags " + tags + " -random 10" + getParentArg();
    }
}
