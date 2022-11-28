package masterblaster.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.json.JSONObject;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class FilterController extends SceneController implements Initializable {

    @FXML
    private TextField name;

    @FXML
    private TextField kategorie;

    @FXML
    private DatePicker erscheinungsdatum;


    @FXML
    private TextField cast;


    @FXML
    private Button abbrechen;

    @FXML
    private Button uebernehmen;

    private FilmueberSichtController filmueberSichtController;

    @FXML
    private void abbrechenButtonClicked(){
        Stage stage = (Stage) abbrechen.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void uebernehmenButtonClicked(){
        JSONObject filter = new JSONObject()
                .put("Titel", name.getText())
                .put("kategorie", kategorie.getText())
                .put("erscheinungsdatum", erscheinungsdatum.getValue() != null ? erscheinungsdatum.getValue().toString() : "")
                .put("cast",cast.getText());

        filmueberSichtController.filter(filter);

        Stage stage = (Stage) uebernehmen.getScene().getWindow();
        stage.close();
    }

    public void passData(FilmueberSichtController filmueberSichtController, JSONObject oldFilterValues){
        name.setText(oldFilterValues.getString("Titel"));
        kategorie.setText(oldFilterValues.getString("kategorie"));
        erscheinungsdatum.setValue(!oldFilterValues.getString("erscheinungsdatum").isEmpty() ? LocalDate.parse(oldFilterValues.getString("erscheinungsdatum")):null);
        cast.setText(oldFilterValues.getString("cast"));

        this.filmueberSichtController = filmueberSichtController;
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }
}
