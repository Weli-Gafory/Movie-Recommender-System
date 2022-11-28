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
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import masterblaster.Main;
import masterblaster.models.Bewertung;
import masterblaster.models.Nutzer;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class DeineBewertungenController extends SceneController implements Initializable {

    @FXML
    private TextField SearchField;

    @FXML
    private Button search;

    @FXML
    private Label pageTitel;

    @FXML
    private TableView<Bewertung> filmTableView;

    @FXML
    private TableColumn<Bewertung ,String> titel;

    @FXML
    private TableColumn <Bewertung , Integer> bewertung;

    @FXML
    private TableColumn <Bewertung ,String> text;

    @FXML
    private Button foreward;

    @FXML
    private Button back;

    @FXML
    private Button details;

    @FXML
    private Button bearbeiten;

    @FXML
    private Button filter;

    @FXML
    private Nutzer nutzer;


    @FXML
    private void forewardButtonClicked(){

    }

    @FXML
    private void backButtonClicked(){

    }

    @FXML
    private void detailsButtonClicked(){

    }

    @FXML
    private void bearbeitenButtonClicked(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/EditBewertung.fxml"));
        Parent root;
        root = loader.load();
        EditDeineBewertungenController editDeineBewertungenController = loader.getController();

        editDeineBewertungenController.passBewertungData(getSelectedBewertung());

        Scene addScene = new Scene(root);

        Stage window = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        window.setScene(addScene);
        window.show();

    }

    private Bewertung getSelectedBewertung(){
        return filmTableView.getSelectionModel().getSelectedItem();
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setMenuSettings();
        titel.setCellValueFactory(new PropertyValueFactory<>("titel"));
        bewertung.setCellValueFactory(new PropertyValueFactory<>("bewertung"));
        text.setCellValueFactory(new PropertyValueFactory<>("text"));


        ObservableList<Bewertung> bewertung = null;
        try {
            bewertung = getAllUserBewertungen();
            filmTableView.setItems(bewertung);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        pageTitel.setText("Bewertungen von " + nutzer.getName() + " " + nutzer.getSurname());
    }

    public void showButtons(boolean x){
        bearbeiten.setVisible(x);
    }

    private ObservableList<Bewertung> getAllUserBewertungen() throws IOException, InterruptedException {
        if (Main.getClient().getFremdNutzer() != null){
            nutzer = Main.getClient().getFremdNutzer();
            Main.getClient().setFremdNutzer(null);
            showButtons(false);
        }

        else {
            nutzer = Main.getClient().getNutzer();
            showButtons(true);
        }

        JSONArray jsonArray = null;
        try {
            jsonArray = Main.getClient().getAllUserReviews(nutzer);
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
}
