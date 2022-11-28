package masterblaster.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
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
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import masterblaster.Main;
import masterblaster.models.Einladung;
import masterblaster.models.Nutzer;
import masterblaster.models.Obermensch;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;


public class NutzerListeController extends SceneController implements Initializable {
    @FXML
    private TextField keyword;

    @FXML
    private TableColumn<Nutzer, String> vorname;

    @FXML
    private TableColumn <Nutzer, String> nachname;

    @FXML
    private Button SendMessage;

    @FXML
    private Button AddFriend;

    @FXML
    private Button details;

    @FXML
    private Button filmeinladungButton;


    @FXML
    private TableView<Nutzer> NutzerListeTableView;

    ObservableList<Nutzer> nutzers = FXCollections.observableArrayList();


    @FXML
    private void detailsButtonClicked(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/NutzerUebersicht.fxml"));
        Parent root;
        root = fxmlLoader.load();
        NutzerUebersichtController nutzerUebersichtController = fxmlLoader.getController();

        nutzerUebersichtController.passNutzerData(Main.getClient().getNutzer(), getSelectedNutzer());
        Scene addScene = new Scene(root);

        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(addScene);
        window.show();

    }

    private Nutzer getSelectedNutzer() {
        return NutzerListeTableView.getSelectionModel().getSelectedItem();
    }


    @FXML
    private void sendMessageButtonClicked(ActionEvent event) {
        try {
            JSONObject result = Main.getClient().startChat(Main.getClient().getNutzer(), getSelectedNutzer());
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Chat.fxml"));
            Parent root;
            root = loader.load();
            ChatController chatController = loader.getController();

            Scene addScene = new Scene(root);

            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
            window.setScene(addScene);
            chatController.passData((boolean) result.get("isRequester"), result.getInt("chattingPort"), getSelectedNutzer());
            window.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void filmeinladungButtonClicked(ActionEvent actionEvent){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Einladung.fxml"));
            Parent root;
            root = loader.load();
            EinladungController einladungController = loader.getController();

            Scene addScene = new Scene(root);

            Stage window = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            window.setScene(addScene);
            einladungController.passData(getSelectedNutzer());
            window.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void addFriendButtonClicked(){
        System.out.println("FriendRequest sent");
        try {
            Main.getClient().newFriendRequest(Main.getClient().getNutzer(), NutzerListeTableView.getSelectionModel().getSelectedItem());
            JOptionPane.showMessageDialog(null, "Freundschaftsanfrage wurde gesendet!", "InfoBox: " + "Erfolgreich", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setMenuSettings();

        System.out.println("Nutzerliste");
        vorname.setCellValueFactory(new PropertyValueFactory<>("name"));
        nachname.setCellValueFactory(new PropertyValueFactory<>("surname"));

        try {
            ArrayList<Nutzer> nutzerliste = Main.getClient().getAllUsers(Main.getClient().getNutzer());
            for (Nutzer n: nutzerliste) {
                nutzers.add(n);
            }

            NutzerListeTableView.setItems(nutzers);
            FilteredList<Nutzer> filteredList = new FilteredList<>(nutzers, b-> true);
            keyword.textProperty().addListener((observable, oldvalue,newValue)->{
                filteredList.setPredicate(nutzersearch -> {
                    if (newValue == null){
                        return true;
                    }
                    String searchword = newValue.toLowerCase();
                    if (nutzersearch.getName().toLowerCase().indexOf(searchword)>-1){
                        return true;
                    }
                    else {
                        return false;
                    }
                });
            });
            SortedList<Nutzer> sortedList = new SortedList<>(filteredList);
            sortedList.comparatorProperty().bind(NutzerListeTableView.comparatorProperty());
            NutzerListeTableView.setItems(sortedList);


        } catch (Exception e) {
            e.printStackTrace();
        }




    }
}

