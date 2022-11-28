package masterblaster.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import masterblaster.Main;
import masterblaster.models.Film;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.Base64;
import java.util.ResourceBundle;

public class EditFilmController extends SceneController implements Initializable {


    @FXML
    private TextField kategorie;

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

    private Film film;

    @FXML
    Text info;

    @FXML
    private Button editFilmBanner;

    private Path source, target;


    @FXML
    private void editFilmBannerButtonClicked() {
        FileChooser fileChooser = new FileChooser();
        configureFileChooser(fileChooser);
        File file = fileChooser.showOpenDialog(Main.getCurrentStage());

        if (file != null) {
            this.source = file.toPath();
            String fileName = file.getName();
            this.target = Path.of("./.banner/" + name.getText() + fileName.substring(fileName.lastIndexOf(".")));
        }
    }

    private static void configureFileChooser(FileChooser fileChooser) {
        fileChooser.setTitle("Filmbanner auswählen");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PNG", "*.png"),
                new FileChooser.ExtensionFilter("JPG", "*.jpg")
        );
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        erscheinungsdatum.getEditor().setDisable(true);
        erscheinungsdatum.getEditor().setOpacity(1);
        setMenuSettings();
    }

    @FXML
    private boolean editFilm() {
        this.updateFilm(this.film);

        try {
            Main.getClient().updateFilmAction(this.film);
            info.setText("Film geändert!");
            info.setVisible(true);
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            info.setText("Da ist etwas schief gelaufen, bitte versuch' es erneut!");
            info.setVisible(true);
            return false;
        }

    }

    public void passFilmData(Film film) {
        this.film = film;
        name.setText(film.getName());
        kategorie.setText(film.getKategorie());
        erscheinungsdatum.setValue(LocalDate.parse(film.getErscheinungsdatum()));
        regisseur.setText(film.getRegisseur());
        drehbuchautor.setText(film.getDrehbuchautor());
        cast.setText(film.getCast());
        filmlaenge.setText(film.getFilmlaenge());
        this.target = Path.of(film.getFilmbanner());

    }

    private void updateFilm(Film film) {
        String base64Filmbanner = null;
        try {
            if (this.source != null)
                base64Filmbanner = Base64.getEncoder().encodeToString(Files.readAllBytes(this.source));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        film.setName(name.getText());
        film.setKategorie(kategorie.getText());
        film.setErscheinungsdatum(erscheinungsdatum.getValue().toString());
        film.setRegisseur(regisseur.getText());
        film.setDrehbuchautor(drehbuchautor.getText());
        film.setCast(cast.getText());
        film.setFilmlaenge(filmlaenge.getText());
        film.setFilmbanner(base64Filmbanner);

    }
}
