package masterblaster.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import masterblaster.Main;
import masterblaster.models.Einladung;
import masterblaster.models.Nutzer;

import java.net.URL;
import java.nio.channels.ScatteringByteChannel;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class OffeneFilmeinladungenController extends SceneController implements Initializable {

    @FXML
    private TableView<Einladung> filmeinladungen;
    @FXML
    private TableColumn<Einladung, String> vorname;
    @FXML
    private TableColumn<Einladung, String> nachname;
    @FXML
    private TableColumn<Einladung, String> filmname;
    @FXML
    private TableColumn<Einladung, String> datum;
    @FXML
    private TableColumn<Einladung, String> uhrzeit;
    @FXML
    private TableColumn<Einladung, String> nachricht;
    @FXML
    private Button accept;
    @FXML
    private Button deny;

    private ObservableList<Einladung> pendingInvitations;



    @FXML
    private void acceptButtonClicked(){
        try {
            Main.getClient().acceptInvitation(filmeinladungen.getSelectionModel().getSelectedItem());
            this.pendingInvitations.remove(filmeinladungen.getSelectionModel().getSelectedItem());
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @FXML
    private void denyButtonClicked(){
        try {
            Main.getClient().declineInvitation(filmeinladungen.getSelectionModel().getSelectedItem());
            this.pendingInvitations.remove(filmeinladungen.getSelectionModel().getSelectedItem());
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    private ObservableList<Einladung> getPendingInvitations(){
        ObservableList<Einladung> pendingInvitations = FXCollections.observableArrayList();
        try{
            ArrayList<Einladung> requester = Main.getClient().getAllInvitations(Main.getClient().getNutzer());
            for (Einladung request: requester){
                pendingInvitations.add(request);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return pendingInvitations;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setMenuSettings();
        System.out.println("Offene Einladungen");
        vorname.setCellValueFactory(new PropertyValueFactory<>("vorname"));
        nachname.setCellValueFactory(new PropertyValueFactory<>("nachname"));
        filmname.setCellValueFactory(new PropertyValueFactory<>("filmName"));
        datum.setCellValueFactory(new PropertyValueFactory<>("datum"));
        uhrzeit.setCellValueFactory(new PropertyValueFactory<>("uhrzeit"));
        nachricht.setCellValueFactory(new PropertyValueFactory<>("text"));

        this.pendingInvitations = this.getPendingInvitations();
        this.filmeinladungen.setItems(this.pendingInvitations);


    }
}
