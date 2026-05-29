import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.File;
import java.net.URL;

public class SceneManager {
    private static Stage stage;

    public static void init(Stage primaryStage) {
        stage = primaryStage;
    }

    private static void addCss(Scene scene) {
        try {
            URL resource = SceneManager.class.getResource("style.css");
            if (resource != null) {
                scene.getStylesheets().add(resource.toExternalForm());
                return;
            }
            File cssFile = new File("style.css");
            if (cssFile.exists()) {
                scene.getStylesheets().add(cssFile.toURI().toString());
                return;
            }
            File cssFile2 = new File("src/main/java/style.css");
            if (cssFile2.exists()) {
                scene.getStylesheets().add(cssFile2.toURI().toString());
            }
        } catch (Exception e) {
            System.out.println("[SceneManager] style.css not found, running without stylesheet.");
        }
    }

    public static void showLogin() {
        LoginScreen screen = new LoginScreen();
        Scene scene = new Scene(screen.getRoot(), 1100, 720);
        addCss(scene);
        stage.setScene(scene);
        stage.show();
    }

    public static void showGuestDashboard(Guest guest) {
        GuestDashboard screen = new GuestDashboard(guest);
        Scene scene = new Scene(screen.getRoot(), 1100, 720);
        addCss(scene);
        stage.setScene(scene);
    }

    public static void showAdminDashboard(Admin admin) {
        AdminDashboard screen = new AdminDashboard(admin);
        Scene scene = new Scene(screen.getRoot(), 1100, 720);
        addCss(scene);
        stage.setScene(scene);
    }

    public static void showReceptionistDashboard(Receptionist rec) {
        ReceptionistDashboard screen = new ReceptionistDashboard(rec);
        Scene scene = new Scene(screen.getRoot(), 1100, 720);
        addCss(scene);
        stage.setScene(scene);
    }

    public static Stage getStage() { return stage; }
}
