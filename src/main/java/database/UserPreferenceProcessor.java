package database;
import com.example.ood_cw_new.HomePageController;
import javafx.application.Platform;

import java.util.concurrent.*;

public class UserPreferenceProcessor {


    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    DatabaseConnection databaseConnection = new DatabaseConnection();

    public void startBatchProcessing(String username) {
        Runnable task = () -> processPreferencesProcess(username);

        // Schedule the task to run every 5 minutes
        scheduler.scheduleAtFixedRate(task, 60, 120, TimeUnit.SECONDS);
    }

    public void stopBatchProcessing() {
        scheduler.shutdown();
    }

    private void processPreferencesProcess(String username) {
        databaseConnection.fetchingPreferences(username);
        gridThreadMethod();
    }

    public void gridThreadMethod(){
        Thread checkFetchThread = new Thread(() -> {
            while (!databaseConnection.isFetchingPreferencesComplete()) {
                try {
                    // Poll every 100ms to check if fetching is complete
                    Thread.sleep(100);
                    System.out.println("sleeping");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            // Once data fetching is complete, update the UI
            Platform.runLater(() -> {
                databaseConnection.populatePreference();
            });
        });

        checkFetchThread.start(); // Start the polling thread
    }

//    public static void main(String[] args) {
//        UserPreferenceProcessor processor = new UserPreferenceProcessor();
//        processor.startBatchProcessing();
//
//    }


}
