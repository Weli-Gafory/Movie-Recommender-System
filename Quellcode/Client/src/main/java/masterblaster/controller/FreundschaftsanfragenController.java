package masterblaster.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import masterblaster.Main;
import masterblaster.models.Nutzer;
import org.json.JSONArray;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class FreundschaftsanfragenController extends SceneController implements Initializable {
    @FXML
    private TableView<Nutzer> freundschaftsanfragen;

    @FXML
    private TableColumn<Nutzer, String> vorname;

    @FXML
    private TableColumn<Nutzer, String> nachname;

    @FXML
    private Button accept;

    @FXML
    private Button deny;

    private ObservableList<Nutzer> friendsRequesting;

    @FXML
    private void acceptButtonClicked() {
        try {
            Main.getClient().acceptFriendRequest(Main.getClient().getNutzer(), freundschaftsanfragen.getSelectionModel().getSelectedItem());
            this.friendsRequesting.remove(freundschaftsanfragen.getSelectionModel().getSelectedItem());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void denyButtonClicked() {
        try {
            Main.getClient().declineFriendRequest(Main.getClient().getNutzer(), freundschaftsanfragen.getSelectionModel().getSelectedItem());
            this.friendsRequesting.remove(freundschaftsanfragen.getSelectionModel().getSelectedItem());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setMenuSettings();
        System.out.println("offeneFreunschaftsanfragen");
        vorname.setCellValueFactory(new PropertyValueFactory<>("name"));
        nachname.setCellValueFactory(new PropertyValueFactory<>("surname"));

        this.friendsRequesting = this.getFriendsRequesting();
        this.freundschaftsanfragen.setItems(this.friendsRequesting);

    }

    private ObservableList<Nutzer> getFriendsRequesting() {
        ObservableList<Nutzer> friendsRequesting = FXCollections.observableArrayList();
        try {
            ArrayList<Nutzer> requester = Main.getClient().getAllFriendRequests(Main.getClient().getNutzer());
            for (Nutzer request: requester) {
                friendsRequesting.add(request);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return friendsRequesting;
    }

    private void getPendingFriendRequests() throws IOException, InterruptedException {
        //get all pendingFriendrequests ....

        // TODO: 29.05.22 COOLLLLIN hier bitte was machen
    }
}



