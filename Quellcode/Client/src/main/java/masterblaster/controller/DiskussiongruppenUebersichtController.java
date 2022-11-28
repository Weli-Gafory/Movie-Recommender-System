package masterblaster.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Pair;
import masterblaster.Main;
import masterblaster.models.Nutzer;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;


public class DiskussiongruppenUebersichtController extends SceneController implements Initializable {
    @FXML
    private TextField searchfield;

    @FXML
    private TableColumn<Nutzer, String> groupName;

    @FXML
    private TableColumn <Nutzer, String> isMember;

    @FXML
    private Button joinGroup;

    @FXML
    private Button joinGroupChat;

    @FXML
    private Button create;


    @FXML
    private TableView<Diskussionsgruppe> GroupListTableView;
    private ListChangeListener selectionChangeListener = (ListChangeListener<Diskussionsgruppe>) c ->
            disableButtons(
                    getSelectedGroup() != null ? getSelectedGroup().isMemberBool : true,
                    getSelectedGroup() != null ? !getSelectedGroup().isMemberBool : true
            );

    ObservableList<Diskussionsgruppe> groups = FXCollections.observableArrayList();

    @FXML
    private void createGroupButtonClicked(ActionEvent event) throws IOException {

        Dialog<Pair<String, Boolean>> newGroupDialog = new Dialog<>();

        newGroupDialog.setTitle("Neue Gruppe erstellen");
        newGroupDialog.setHeaderText(null);
        newGroupDialog.setContentText("Wie soll die neue Gruppe heißen?");
        newGroupDialog.initStyle(StageStyle.UTILITY);
        newGroupDialog.setGraphic(null);
        newGroupDialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        Node okButton = newGroupDialog.getDialogPane().lookupButton(ButtonType.OK);
        okButton.setDisable(true);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20,150,10,20));

        TextField groupName = new TextField();
        groupName.setPromptText("Gruppenname");
        groupName.textProperty().addListener((observable, oldValue, newValue) -> okButton.setDisable(newValue.isEmpty()));

        ToggleGroup groupPolicy = new ToggleGroup();

        ToggleButton publicGroup = new ToggleButton("Öffentlich");
        publicGroup.setToggleGroup(groupPolicy);

        ToggleButton privateGroup = new ToggleButton("Privat");
        privateGroup.setToggleGroup(groupPolicy);

        groupPolicy.selectToggle(publicGroup);

        grid.add(new Label("Wie soll ihre Gruppe heißen?"), 0,0);
        grid.add(groupName, 1,0);

        grid.add(new Label("Ist ihre Gruppe öffentlich oder Privat?"), 0,1);
        grid.add(publicGroup,1,1);
        grid.add(privateGroup,2,1);

        newGroupDialog.getDialogPane().setContent(grid);
        Platform.runLater(() -> groupName.requestFocus());

        newGroupDialog.setResultConverter(clickedButton -> clickedButton == ButtonType.OK ?
                new Pair<>(groupName.getText(), groupPolicy.getSelectedToggle() == privateGroup) : null);

        Optional<Pair<String, Boolean>> result = newGroupDialog.showAndWait();
        result.ifPresent(pair ->
        {
            try {
                Main.getClient().createDiscussionGroup(pair.getKey(), pair.getValue(), Main.getClient().getNutzer().getId());
                this.loadDiscussionGroups();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e.getMessage(), "Fehlgeschlagen!", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private Diskussionsgruppe getSelectedGroup() {
        return GroupListTableView.getSelectionModel().getSelectedItem();
    }

    @FXML
    private void joinGroupButtonClicked(ActionEvent event) {
        try {
            Main.getClient().joinDiscussionGroup(getSelectedGroup().getId(), Main.getClient().getNutzer());
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setGraphic(null);
            alert.setTitle(null);
            alert.setHeaderText(null);
            alert.setContentText("Du bist der Diskussionsgrupper erfolgreich beigetreten!");
            alert.initStyle(StageStyle.UTILITY);
            alert.showAndWait();
            this.loadDiscussionGroups();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void joinGroupChatButtonClicked(ActionEvent event){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Chat.fxml"));
            Parent root;
            root = loader.load();
            ChatController chatController = loader.getController();

            Scene addScene = new Scene(root);

            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
            window.setScene(addScene);
            chatController.passData(Main.getClient().startGroupChat(getSelectedGroup().getId()), getSelectedGroup().getName());
            window.show();

        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    private void disableButtons(boolean joinGroup, boolean joinChat) {
        this.joinGroup.setDisable(joinGroup);
        this.joinGroupChat.setDisable(joinChat);
    }

    @Override
    protected void setMenuSettings() {
        super.setMenuSettings();
        disableButtons(true,true);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setMenuSettings();

        System.out.println("Nutzerliste");
        groupName.setCellValueFactory(new PropertyValueFactory<>("name"));
        isMember.setCellValueFactory(new PropertyValueFactory<>("isMember"));

        this.loadDiscussionGroups();
    }

    private void loadDiscussionGroups() {
        this.searchfield.clear();
        this.GroupListTableView.getSelectionModel().getSelectedItems().removeListener(this.selectionChangeListener);
        this.groups = FXCollections.observableArrayList();
        try {
            JSONArray discussionGroupList = Main.getClient().getDiscussionGroups(Main.getClient().getNutzer().getId());
            for (int i = 0; i < discussionGroupList.length(); i++) {
                this.groups.add(new Diskussionsgruppe(discussionGroupList.getJSONObject(i).getInt("id"), discussionGroupList.getJSONObject(i).getString("name"), discussionGroupList.getJSONObject(i).getBoolean("isMember")));
            }

            FilteredList<Diskussionsgruppe> filteredList = new FilteredList<>(groups, b-> true);
            this.searchfield.textProperty().addListener((observable, oldvalue,newValue)->{
                GroupListTableView.getSelectionModel().clearSelection();
                filteredList.setPredicate(group -> {
                    if (newValue == null || group.getName().toLowerCase().contains(newValue.toLowerCase()))
                        return true;
                    return false;
                });
            });
            GroupListTableView.setItems(filteredList);

        } catch (Exception e) {
            e.printStackTrace();
        }
        GroupListTableView.getSelectionModel().getSelectedItems().addListener(this.selectionChangeListener);
    }

    public class Diskussionsgruppe {
        private final Integer id;
        private String name;
        private String isMember;
        private boolean isMemberBool;
        public Diskussionsgruppe(Integer id, String name, boolean isMember) {
            this.id = id;
            this.name = name;
            this.setIsMember(isMember);
            this.isMemberBool = isMember;
        }
        public String getName() {return this.name;}
        public String getIsMember() {return this.isMember;}
        public void setIsMember(boolean isMember) {this.isMember = isMember ? "Ja":"Nein";}
        public boolean getIsMemberBool() {return this.isMemberBool;}
        public Integer getId() {return id;}
    }
}



