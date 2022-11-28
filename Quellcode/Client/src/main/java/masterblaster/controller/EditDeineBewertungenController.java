package masterblaster.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import masterblaster.Main;
import masterblaster.models.Bewertung;

import java.net.URL;
import java.util.ResourceBundle;

public class EditDeineBewertungenController extends SceneController implements Initializable {

    private Bewertung bewertung;

    @FXML
    private TextField title;

    @FXML
    private TextArea text;

    @FXML
    private Slider rating;

    @FXML
    private Button save;

    @FXML
    private Button deleteButton;

    

    public void passBewertungData(Bewertung bewertung){
        this.bewertung = bewertung;
        title.setText(bewertung.getTitel());
        text.setText(bewertung.getText());
        rating.setValue(bewertung.getBewertung());

    }

    private void updateBewertung(Bewertung bewertung){
        bewertung.setTitel(title.getText());
        bewertung.setText(text.getText());
        bewertung.setBewertung((int) rating.getValue());
    }

    @FXML
    private void saveButtonClicked(){
        this.updateBewertung(this.bewertung);
        try{
            Main.getClient().editReview(bewertung.getBewertungId(), title.getText(), ((int) rating.getValue()), text.getText());
            nutzerBewertungenButtonClick();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void deleteButtonClicked() throws Exception {
        Main.getClient().deleteReview(bewertung);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setMenuSettings();
    }
}
