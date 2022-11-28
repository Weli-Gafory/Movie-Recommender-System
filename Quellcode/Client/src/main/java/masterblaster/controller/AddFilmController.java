package masterblaster.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import masterblaster.Main;
import masterblaster.models.Film;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Base64;
import java.util.ResourceBundle;

public class AddFilmController extends SceneController implements Initializable {

    @FXML
    private TextField Kategorie;

    @FXML
    private TextField cast;

    @FXML
    private TextField drehbuchautor;

    @FXML
    private DatePicker erscheinungsdatum;

    @FXML
    private TextField filmlaenge;

    @FXML
    private TextField name;

    @FXML
    private TextField regisseur;

    @FXML
    private Button send;

    @FXML
    private Text info;

    @FXML
    private Button fileChooserButton;

    private Path source, target = Path.of("./.banner/platzhalter.png");

    @FXML
    private void startFileChooser(){
        FileChooser fileChooser = new FileChooser();
        configureFileChooser(fileChooser);
        File file = fileChooser.showOpenDialog(Main.getCurrentStage());

        if (file != null){
            this.source = file.toPath();
            String fileName = file.getName();
            this.target = Path.of("./.banner/" + name.getText() + fileName.substring(fileName.lastIndexOf(".")));
        }
    }

    private static void configureFileChooser(FileChooser fileChooser){
        fileChooser.setTitle("Filmbanner auswählen");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PNG", "*.png"),
                new FileChooser.ExtensionFilter("JPG", "*.jpg")
        );
    }

    @FXML
    private void AddFilm(ActionEvent event) {
        try {
            String base64Filmbanner = "";
            if (this.source != null)
                base64Filmbanner = Base64.getEncoder().encodeToString(Files.readAllBytes(this.source));

            Film film = new Film(
                    name.getText(),
                    Kategorie.getText(),
                    erscheinungsdatum.getValue().toString(),
                    regisseur.getText(),
                    drehbuchautor.getText(),
                    cast.getText(),
                    filmlaenge.getText(),
                    base64Filmbanner
            );

            Main.getClient().addNewFilmAction(film);

            this.clearFields();
            info.setText("Film hinzugefügt!");
            info.setVisible(true);

        } catch (Exception e) {
            System.out.println(e.getMessage());
            info.setText("Da ist etwas schiefgelaufen, Bitte versuch' es erneut!");
        }

    }

    private void clearFields() {
        name.clear();
        Kategorie.clear();
        erscheinungsdatum.getEditor().clear();
        regisseur.clear();
        drehbuchautor.clear();
        cast.clear();
        filmlaenge.clear();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setMenuSettings();
        erscheinungsdatum.getEditor().setDisable(true);
        erscheinungsdatum.getEditor().setOpacity(1);
    }
}
