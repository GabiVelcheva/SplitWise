package splitWise.server;

public class SplitWiseServerMain {
    public static void main(String[] args) throws InterruptedException {
        final SplitWiseServer[] server = {null};

        Thread gabi = new Thread(() -> {
            server[0] = new SplitWiseServer();
            server[0].startSplitWiseServer();
        });
        gabi.start();
        Thread.sleep(400000);
        server[0].stop();
        gabi.interrupt();
        return;
    }
}
