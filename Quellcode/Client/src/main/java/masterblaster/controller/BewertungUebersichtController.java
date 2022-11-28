package masterblaster.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import masterblaster.Main;
import masterblaster.models.Bewertung;
import masterblaster.models.Film;
import masterblaster.models.Nutzer;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class BewertungUebersichtController extends SceneController implements Initializable {

    @FXML
    private TableView <Bewertung> bewertungTableView;


    @FXML
    private TableColumn <Bewertung ,String> titel;

    @FXML
    private TableColumn <Bewertung , Integer> bewertung;

    @FXML
    private TableColumn <Bewertung ,String> text;

    ObservableList<Bewertung> bewertungsListe;

    @FXML
    private Button zuNutzer;

    private Film film;

    private Nutzer nutzer;



    @FXML
    private void forewardButtonClicked() throws IOException, InterruptedException {


    }

    private ObservableList<Bewertung> getAllBewertungen() throws IOException, InterruptedException {
        JSONArray jsonArray = null;
        try {
            jsonArray = Main.getClient().getAllReviews(film);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        ObservableList<Bewertung> bewertungen = FXCollections.observableArrayList();


        for (int i=0; i<jsonArray.length();i++) {
            JSONObject object = jsonArray.getJSONObject(i);

            // TODO: 17.05.2022 Filmbanner hinzufuegen
            Integer id = object.getInt("id");
            String title = object.getString("titel");
            String text = object.getString("comment");
            Integer bewertung = object.getInt("score");

            Bewertung b = new Bewertung(id, text,title,bewertung);
            bewertungen.add(b);
        }
        return bewertungen;



    }

    @FXML
    private void backButtonClicked(){

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setMenuSettings();
        titel.setCellValueFactory(new PropertyValueFactory<>("titel"));
        bewertung.setCellValueFactory(new PropertyValueFactory<>("bewertung"));
        text.setCellValueFactory(new PropertyValueFactory<>("text"));

    }

    public void passFilm(Film film) {
        this.film = film;
        try {
            bewertungsListe = getAllBewertungen();
            bewertungTableView.setItems(bewertungsListe);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    @FXML
    private void zuNutzerButtonClicked(ActionEvent event) throws Exception {


        try {
            Nutzer userFromReview = Main.getClient().getUserFromReview(this.bewertungTableView.getSelectionModel().getSelectedItem());

            if (userFromReview.getId() != Main.getClient().getNutzer().getId()){
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/NutzerUebersicht.fxml"));
                Parent root;
                root = fxmlLoader.load();
                NutzerUebersichtController nutzerUebersichtController = fxmlLoader.getController();

                nutzerUebersichtController.passNutzerData(Main.getClient().getNutzer(), Main.getClient().getUserFromReview(getSelectedBewertung()));
                Scene addScene = new Scene(root);

                Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
                window.setScene(addScene);
                window.show();
            }
            else {
                nutzeruebersichtMenuButtonClicked();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Bewertung getSelectedBewertung() {
        return bewertungTableView.getSelectionModel().getSelectedItem();
    }


}
