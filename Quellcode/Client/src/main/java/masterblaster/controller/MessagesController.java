package masterblaster.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import masterblaster.Main;
import masterblaster.models.Message;
import org.json.JSONArray;

import java.net.URL;
import java.util.ArrayList;
import java.util.NavigableMap;
import java.util.ResourceBundle;

public class MessagesController extends SceneController implements Initializable {


    @FXML
    private TableView<Message> messageTableView;

    @FXML
    private TableColumn<Message, String> nachricht;

    @FXML
    private Button delete;

    private ObservableList<Message> messages;



    @FXML
    private void deleteButtonClicked(){
        try {
            Main.getClient().deleteMessage(messageTableView.getSelectionModel().getSelectedItem());
            messages.remove(messageTableView.getSelectionModel().getSelectedItem());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ObservableList<Message> getMessages()  {
        ObservableList<Message> messages = FXCollections.observableArrayList();
        try {
            ArrayList<Message> requester = Main.getClient().getAllMessages(Main.getClient().getNutzer());
            for (Message request: requester){
                messages.add(request);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return messages;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setMenuSettings();
        System.out.println("Offene Nachrichten");

        nachricht.setCellValueFactory(new PropertyValueFactory<>("text"));

        this.messages = this.getMessages();
        this.messageTableView.setItems(this.messages);

    }
}
