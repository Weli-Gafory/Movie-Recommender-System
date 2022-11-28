package masterblaster;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import masterblaster.clientServer.Client;
import masterblaster.models.Nutzer;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class Main extends Application {

    public static Client client = new Client();

    public static boolean isAdmin;

    private static Stage currentStage;

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws IOException {
        // Login
        Parent masterLoader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml")).load();

        currentStage = stage;
        currentStage.setScene(new Scene(masterLoader));
        currentStage.setTitle("Bestimmt kein imdb-Klon");
        currentStage.show();
    }

    public static Stage getCurrentStage() {
        return currentStage;
    }

    public static Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public static void setIsAdmin(boolean bol) {
        isAdmin = bol;
    }
}