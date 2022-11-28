package masterblaster.controller;

import javafx.beans.value.ObservableValue;
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
import masterblaster.models.Film;
import masterblaster.models.Nutzer;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.time.chrono.MinguoChronology;
import java.util.ResourceBundle;

public class FreundeslisteController extends SceneController implements Initializable {

    @FXML
    private Button back;

    @FXML
    private Button foreward;

    @FXML
    private TextField SearchField;

    @FXML
    private Button search;

    @FXML
    private TableView<Nutzer> friendTableView;

    @FXML
    private TableColumn<Nutzer, String> vorname;

    @FXML
    private TableColumn<Nutzer, String> nachname;

    @FXML
    private Button details;

    @FXML
    private Button deleteFriend;

    @FXML
    private Label pageTitle;

    private ObservableList<Nutzer> freunde = FXCollections.observableArrayList();

    private Nutzer nutzer;


    @FXML
    private void detailsButtonClicked(){

    }

    @FXML
    private void deleteFriendButtonClicked(){
        try {
            Main.getClient().deleteFriend(nutzer, friendTableView.getSelectionModel().getSelectedItem());
            freunde.remove(friendTableView.getSelectionModel().getSelectedItem());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void searchButtonClicked(){

    }

    @FXML
    private void backButtonClicked(){

    }

    @FXML
    private void forewardButtonClicked(){

    }


    private ObservableList<Nutzer> getFriendList() throws Exception {
        JSONArray jsonArray = Main.getClient().getAllFriends(nutzer);

        for (int i = 0; i< jsonArray.length(); i++){
            freunde.add(new Nutzer(jsonArray.getJSONObject(i)));
        }
        return freunde;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        if (Main.getClient().getFremdNutzer() != null){
            nutzer = Main.getClient().getFremdNutzer();
            Main.getClient().setFremdNutzer(null);
            showButtons(false);
        }
        else {
            nutzer = Main.getClient().getNutzer();
            showButtons(true);
        }
        setMenuSettings();
        System.out.println("Freundesliste");
        vorname.setCellValueFactory(new PropertyValueFactory<>("name"));
        nachname.setCellValueFactory(new PropertyValueFactory<>("surname"));

        try {
            freunde = getFriendList();
            friendTableView.setItems(freunde);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        pageTitle.setText("Freundesliste von: " + nutzer.getName() + " "  + nutzer.getSurname());

    }

    private void showButtons(boolean x){
        deleteFriend.setVisible(x);
        details.setVisible(x);
    }
}
