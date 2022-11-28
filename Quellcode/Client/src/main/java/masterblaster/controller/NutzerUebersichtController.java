package masterblaster.controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import masterblaster.Main;
import masterblaster.models.Nutzer;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.ResourceBundle;

public class NutzerUebersichtController extends SceneController implements Initializable {

    @FXML
    private Button Freundesliste;

    @FXML
    private Button Watchlist;


    @FXML
    private Button statistik;

    @FXML
    private Button GeseheneFilme;

    @FXML
    private Button AlleNutzer;

    @FXML
    private Button nutzerBewertungen;

    @FXML
    private Button offeneFreundschaftsanfragen;

    @FXML
    private Text info;

    @FXML
    private Circle profilePic;

    @FXML
    private Label pageTitle;
    @FXML
    private Text noMethodeSelected;

    private Nutzer nutzer;
    @FXML
    private ComboBox<String> Comb;

    public void passNutzerData(Nutzer nutzer, Nutzer fremdNutzer){
        this.nutzer = nutzer;
        Main.getClient().setFremdNutzer(fremdNutzer);
        pageTitle.setText("Nutzerkonto von " + fremdNutzer.getName() + " " + fremdNutzer.getSurname());
        profilePic.setFill(new ImagePattern(new Image(new ByteArrayInputStream(Base64.getDecoder().decode(fremdNutzer.getProfilepic())))));
        JSONObject settings = Main.getClient().getSettings(fremdNutzer);
        boolean isFriend= false;
        try {
            JSONArray friends = Main.getClient().getAllFriends(nutzer);
            ArrayList<Nutzer> freunde = new ArrayList<>();

            for (int i = 0; i< friends.length(); i++){
                freunde.add(new Nutzer(friends.getJSONObject(i)));

                if (new Nutzer(friends.getJSONObject(i)).getId() == fremdNutzer.getId()){
                    isFriend = true;
                    break;
                }
            }

        } catch (Exception e) {
            System.out.println(e);

        }

        setButtonSettings(isFriend,settings);
        //hier brauchen wir jetzt die jeweiligen Nutzereinstellungen von einem Nutzer
        //testen ob nutzer und fremdnutzer freunde sind!

    }

    private void setButtonSettings(boolean isFriend, JSONObject settings){

        offeneFreundschaftsanfragen.setVisible(false);
        statistik.setVisible(false);


        if (settings.getInt("friends") == 0){
            Freundesliste.setDisable(false);
        }
        else if (settings.getInt("friends") == 1 && isFriend) {
            Freundesliste.setDisable(false);
        }
        else {
            Freundesliste.setDisable(true);
        }


        if (settings.getInt("watchlist") == 0){
            Watchlist.setDisable(false);
        }
        else if (settings.getInt("watchlist") == 1 && isFriend) {
            Watchlist.setDisable(false);
        }
        else {
            Watchlist.setDisable(true);
        }


        if (settings.getInt("seenlist") == 0){
            GeseheneFilme.setDisable(false);
        }
        else if (settings.getInt("seenlist") == 1 && isFriend) {
            GeseheneFilme.setDisable(false);
        }
        else {
            GeseheneFilme.setDisable(true);
        }

        if (settings.getInt("bewertung") == 0){
            nutzerBewertungen.setDisable(false);
        }
        else if (settings.getInt("bewertung") == 1 && isFriend) {
            nutzerBewertungen.setDisable(false);
        }
        else {
            nutzerBewertungen.setDisable(true);
        }
    }



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Comb.setItems(FXCollections.observableArrayList("durch selbst gesehene Filme","durch Freunde gesehene Filme"));

        setMenuSettings();
        pageTitle.setText("Nutzerkonto von " + Main.getClient().getNutzer().getName() + " " + Main.getClient().getNutzer().getSurname());
        profilePic.setFill(new ImagePattern(new Image(new ByteArrayInputStream(Base64.getDecoder().decode(Main.getClient().getNutzer().getProfilepic())))));
    }

    @FXML
    private void nutzerstatistikenButtonClicked(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Nutzerstatistiken.fxml"));
        Parent root;
        root = loader.load();
        Scene addScene = new Scene(root);

        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(addScene);
        window.show();
    }
    @FXML
    void filmVorschlaegen(ActionEvent event) throws IOException {

        try {
            if (Comb.getValue().toString() != null) {
                String filmebasedonwas = Comb.getValue().toString();
                System.out.println(filmebasedonwas);

                if (filmebasedonwas.equals("durch selbst gesehene Filme")) {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/RecommendedMovies.fxml"));
                    Parent root;
                    root = loader.load();

                    Scene addScene = new Scene(root);

                    Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    window.setScene(addScene);
                    window.show();
                    System.out.println(Comb.getValue().toString());


                }
                if (Comb.getValue().equals("durch Freunde gesehene Filme")) {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/BasedOnFriendsSeenList.fxml"));
                    Parent root;
                    root = loader.load();
                    BaedOnMyFriendsSeenList recommended = loader.getController();


                    Scene addScene = new Scene(root);

                    Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    window.setScene(addScene);
                    window.show();
                    System.out.println(Comb.getValue().toString());


                }
            }
        }
        catch (Exception e){
            if(Comb.getValue()==null){
                noMethodeSelected.setText(" Bitte suchen Sie sich eine der beiden Recommendation Methoden aus");
                noMethodeSelected.setVisible(true);
                System.out.println(e.getMessage());
            }

        }


    }
}
