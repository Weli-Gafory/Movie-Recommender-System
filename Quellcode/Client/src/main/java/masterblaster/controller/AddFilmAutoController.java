package masterblaster.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import masterblaster.Main;
import masterblaster.models.Systemadministrator;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AddFilmAutoController extends SceneController implements Initializable {

    @FXML
    private TextField Kategorie;

    @FXML
    private DatePicker ZeitraumVon;

    @FXML
    private DatePicker ZeitraumBis;

    @FXML
    private Button AddMovie;

    @FXML
    private Text info;

    @FXML
    private void AddMovieButtonClick() {
        JSONObject automateFilmRequestData = new JSONObject();
        automateFilmRequestData.put("kategorie", Kategorie.getText() != null ? Kategorie.getText() : "");
        automateFilmRequestData.put("von", ZeitraumVon.getValue() != null ? ZeitraumVon.getValue() : "");
        automateFilmRequestData.put("bis", ZeitraumBis.getValue() != null ? ZeitraumBis.getValue() : "");

        try {
            Main.getClient().addFilmAutoAction(automateFilmRequestData);
            info.setText("Der Scraping-Vorgang wird im Hintergrund durchgeführt.\nSie können dieses Fenster verlassen.");
            info.setVisible(true);

        } catch (Exception e) {
            System.out.println(e.getMessage());
            info.setText("Scraping-Vorgang fehlgeschlagen!");
            info.setVisible(true);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setMenuSettings();
        ZeitraumVon.getEditor().setDisable(true);
        ZeitraumBis.getEditor().setDisable(true);
        ZeitraumVon.getEditor().setOpacity(1);
        ZeitraumBis.getEditor().setOpacity(1);
    }
}
