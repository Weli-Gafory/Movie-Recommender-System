package masterblaster.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import masterblaster.Main;
import masterblaster.models.Nutzer;

import java.util.Locale;

public class EinladungController extends SceneController{

    @FXML
    private TextField filmTitle;

    @FXML
    private TextField time;

    @FXML
    private DatePicker date;

    @FXML
    private TextArea message;

    @FXML
    private Button sender;

    @FXML
    private Label pageTitle;

    @FXML
    private Label subTitle;

    private Nutzer eingeladenerNutzer;

    @FXML
    private void senderButtonClicked(ActionEvent actionEvent){
        saveNewEinladung(filmTitle.getText(),time.getText(),date.getValue().toString(),message.getText());
        System.out.println("Einladung wurde gespeichert");
        nutzerlisteMenuButtonClicked();
    }

    private void saveNewEinladung(String filmTitle, String time, String date, String message){
        try{
            Main.getClient().addNewEinladung(Main.getClient().getNutzer(), eingeladenerNutzer, filmTitle, message, date,time);

        } catch (Exception e){
            throw new RuntimeException(e);
        }

    }

    public void passData(Nutzer eingeladenerNutzer){
        this.eingeladenerNutzer = eingeladenerNutzer;
        pageTitle.setText("\"DU MÖCHTEST " + eingeladenerNutzer.getName().toUpperCase(Locale.ROOT) + " " + eingeladenerNutzer.getSurname().toUpperCase(Locale.ROOT)+ " ZU EINEM FILM EINLADEN?\"");
        subTitle.setText(eingeladenerNutzer.getName().toUpperCase(Locale.ROOT) + " " + eingeladenerNutzer.getSurname().toUpperCase(Locale.ROOT)+" WIRD PER SYSTEM UND PER MAIL BENACHRICHTIGT");
    }
}
