package masterblaster.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import masterblaster.Main;
import masterblaster.models.Nutzer;

import javax.swing.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Base64;

public class RegistrierungNutzerController extends SceneController {
    
    @FXML
    private TextField Vorname;

    @FXML
    private TextField Nachname;
    @FXML
    private TextField EMailAdresse;

    @FXML
    private PasswordField Passwort;

    @FXML
    private DatePicker Geburtsdatum;

    @FXML
    private Button fileChooserButton;

    private Path source, target = Path.of("./.profilepics/platzhalter.png");

    @FXML
    private void startFileChooser(){
        FileChooser fileChooser = new FileChooser();
        configureFileChooser(fileChooser);
        File file = fileChooser.showOpenDialog(Main.getCurrentStage());

        if (file != null){
            this.source = file.toPath();
            String fileName = file.getName();
            this.target = Path.of("./.profilepics/" + EMailAdresse.getText() + fileName.substring(fileName.lastIndexOf(".")));
        }
    }

    private void configureFileChooser(FileChooser fileChooser){
        fileChooser.setTitle("Profilbild auswählen");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PNG","*.png"),
                new FileChooser.ExtensionFilter("JPG","*.jpg")
        );
    }

    @FXML
    private void registrierungButtonClick(){
        registerNutzer(Vorname.getText(), Nachname.getText(), EMailAdresse.getText(), Passwort.getText(), Geburtsdatum.getValue().toString());
    }

    private void registerNutzer(String vn, String nn, String mail, String pw, String gb){
        try {
            String base64Image = null;

            if (this.source != null)
                base64Image = Base64.getEncoder().encodeToString(Files.readAllBytes(this.source));

            Main.getClient().registerUserAction(new Nutzer(vn,nn,mail,pw,gb, base64Image));
            loginMenuButtonClicked();
            // FIXME: 17.05.22 hier ist ein Funny Bug, siehe Notion
        } catch (Exception e) {
            System.out.println(e.getMessage());
            JOptionPane.showMessageDialog(null, "Die Registrierung hat nicht geklappt!", "InfoBox: " + "Fehlgeschlagen", JOptionPane.ERROR_MESSAGE);
        }

    }
}
