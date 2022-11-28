package masterblaster.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import masterblaster.Main;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.ResourceBundle;

public class NutzerEinstellungenController extends SceneController implements Initializable {
    @FXML
    private ToggleGroup freundesliste;

    @FXML
    private Toggle niemand1;

    @FXML
    private Toggle freunde1;

    @FXML
    private Toggle alle1;

    @FXML
    private Toggle niemand2;


    @FXML
    private Toggle freunde2;

    @FXML
    private Toggle alle2;

    @FXML
    private Toggle niemand3;

    @FXML
    private Toggle freunde3;

    @FXML
    private Toggle alle3;

    @FXML
    private Toggle niemand4;

    @FXML
    private Toggle freunde4;

    @FXML
    private Toggle alle4;

    @FXML
    private ToggleGroup watchlist;

    @FXML
    private ToggleGroup geseheneFilme;

    @FXML
    private ToggleGroup bewertungen;

    @FXML
    private Button speichern;


    @FXML
    private void speichernButtonClicked(){
        System.out.println(getFreundeslisteSettings());
        System.out.println(getWatchlisteSettings());
        System.out.println(getGeseheneFilmelisteSettings());
        System.out.println(getBewertungenlisteSettings());

        try {
            Main.getClient().setSettings(Main.getClient().getNutzer(),getFreundeslisteSettings(),getWatchlisteSettings(),getGeseheneFilmelisteSettings(),getBewertungenlisteSettings());
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private int getFreundeslisteSettings(){
        if (freundesliste.getSelectedToggle() == niemand1) {
            return 2;
        } else if (freundesliste.getSelectedToggle() == freunde1) {
            return 1;
        }else {
            return 0;
        }
    }

    private int getWatchlisteSettings(){
        if (watchlist.getSelectedToggle() == niemand2) {
            return 2;
        } else if (watchlist.getSelectedToggle() == freunde2) {
            return 1;
        }else {
            return 0;
        }
    }

    private int getGeseheneFilmelisteSettings(){
        if (geseheneFilme.getSelectedToggle() == niemand3) {
            return 2;
        } else if (geseheneFilme.getSelectedToggle() == freunde3) {
            return 1;
        }else {
            return 0;
        }
    }

    private int getBewertungenlisteSettings(){
        if (bewertungen.getSelectedToggle() == niemand4) {
            return 2;
        } else if (bewertungen.getSelectedToggle() == freunde4) {
            return 1;
        }else {
            return 0;
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setMenuSettings();
        Main.getClient().getSettings(Main.getClient().getNutzer());
        JSONObject temp = Main.getClient().getSettings(Main.getClient().getNutzer());
        setWatchListSettings(temp.getInt("watchlist"));
        setFreundeslisteSettings(temp.getInt("friends"));
        setBewertungenSettings(temp.getInt("bewertung"));
        setSeenlistSettings(temp.getInt("seenlist"));
        System.out.println(temp);
    }
    private void setWatchListSettings(int a){
        if(a==2){
            watchlist.selectToggle(niemand2);
        } else if (a==1) {
            watchlist.selectToggle(freunde2);
        }
        else{
            watchlist.selectToggle(alle2);
        }
    }

    private void setFreundeslisteSettings(int b){
        if(b==2){
            freundesliste.selectToggle(niemand1);
        }
        else if(b==1){
            freundesliste.selectToggle(freunde1);
        }
        else{
            freundesliste.selectToggle(alle1);
        }
    }

    private void setBewertungenSettings(int c){
        if(c==2){
            bewertungen.selectToggle(niemand4);
        }
        else if(c==1){
            bewertungen.selectToggle(freunde4);
        }
        else{
            bewertungen.selectToggle(alle4);
        }
    }

    public void setSeenlistSettings(int d){
        if(d==2){
            geseheneFilme.selectToggle(niemand3);
        }
        else if(d==1){
            geseheneFilme.selectToggle(freunde3);
        }
        else{
            geseheneFilme.selectToggle(alle3);
        }
    }
}