import java.util.Timer;
import java.util.TimerTask;

public class TestPublisher implements Publisher {
    private static TestPublisher instance;

    private TestPublisher() {
    }

    public static TestPublisher getInstance() {
        if (instance == null) {
            instance = new TestPublisher();
        }
        return instance;
    }

    @Override
    public void start(Publish publish) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                publish.publish("Hello");
            }
        }, 0, 6L * 1000);
        System.out.println("Process launched");
    }
}