import javafx.application.Application;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {

        ChatServer.getInstance().start();

        HotelDatabase.initializeData();
        primaryStage.setTitle("Ain Shams Hotel Management System");
        primaryStage.setMinWidth(1000);
        primaryStage.setMinHeight(680);
        SceneManager.init(primaryStage);
        SceneManager.showLogin();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
