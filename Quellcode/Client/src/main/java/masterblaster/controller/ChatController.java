package masterblaster.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import masterblaster.Main;
import masterblaster.chat.ChatTransponder;
import masterblaster.chat.ChatRequester;
import masterblaster.models.Nutzer;

import java.net.URL;
import java.util.ResourceBundle;

public class ChatController extends SceneController implements Initializable {
    private Label latestMessage;

    @FXML
    private Button refresh;

    @FXML
    private VBox root;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private AnchorPane scrollElements;

    @FXML
    private TextField messageInput;

    @FXML
    private Button sendMessage;

    @FXML
    private Label pageTitle;

    private ChatTransponder chatter;
    private boolean isGroupChat = false;
    private String lastOwnMessage = null;

    @FXML
    private void sendMessageButtonClicked() {
        lastOwnMessage = messageInput.getText();
        createOutgoingLabel(messageInput.getText());
        messageInput.clear();
    }

    public void createIncomingLabel(String message) {
        if (isGroupChat && lastOwnMessage != null && lastOwnMessage.equals(message))
            return;

        // Label wird erzeugt, die einkommende Nachricht wird hier übergeben
        Label incLabel = new Label(message);

        //"Aussehen" des Labels wird hier festgelegt.
        incLabel.setMaxWidth(500);
        incLabel.setWrapText(true);
        incLabel.setTextFill(Color.BLACK);
        CornerRadii radi = new CornerRadii(20);
        incLabel.setBackground(new Background(new BackgroundFill(Color.LIGHTGREY, radi, Insets.EMPTY)));
        incLabel.setPadding(new Insets(10));
        incLabel.setEffect(new DropShadow(1.0, Color.DARKGREY));

        if (latestMessage == null) {
            incLabel.setLayoutY(20);

        } else {
            incLabel.setLayoutY(20 + latestMessage.getLayoutY() + latestMessage.getHeight());
        }
        incLabel.setLayoutX(20);
        latestMessage = incLabel;
        scrollElements.getChildren().add(incLabel);

    }

    public void createOutgoingLabel(String message) {
        if (!isGroupChat)
            this.chatter.sendMessage(message);
        else {
            lastOwnMessage = Main.getClient().getNutzer().getName() +" "+ Main.getClient().getNutzer().getSurname() + ": " + message;
            this.chatter.sendMessage(lastOwnMessage);
        }

        Label outLabel = new Label(message);
        outLabel.setAlignment(Pos.CENTER_LEFT);
        outLabel.setContentDisplay(ContentDisplay.RIGHT);
        outLabel.setMaxWidth(500);
        outLabel.setWrapText(true);
        outLabel.setTextFill(Color.WHITESMOKE);
        CornerRadii radi = new CornerRadii(20);
        Color outColor = Color.rgb(69, 123, 251);
        outLabel.setBackground(new Background(new BackgroundFill(outColor, radi, Insets.EMPTY)));
        outLabel.setPadding(new Insets(10));
        outLabel.setEffect(new DropShadow(1.0, Color.DARKGREY));


        if (latestMessage == null) {
            outLabel.setLayoutY(20);
        } else {
            outLabel.setLayoutY(20 + latestMessage.getLayoutY() + latestMessage.getHeight());
        }
        outLabel.setLayoutX(500);
        latestMessage = outLabel;
        scrollElements.getChildren().add(outLabel);

    }

    public void passData(Boolean isRequester, Integer port, Nutzer counterPart) {

        pageTitle.setText("Dein Chat mit: " + counterPart.getName() + " " + counterPart.getSurname());
        if (isRequester) {
            ChatRequester requester = new ChatRequester(this);
            new Thread(() -> {
                this.chatter = requester.getRequesterTransponder(port);
                this.disableInput(false);
            }).start();
        } else {
            this.chatter = new ChatTransponder(port, this);
        }
        ChangeListener<? super Scene> listener = new ChangeListener<Scene>() {
            @Override
            public void changed(ObservableValue<? extends Scene> observable, Scene oldValue, Scene newValue) {
                System.out.println("Scene gonna get changed");
                close();

            }
        };
        this.sceneSwitchListener = listener;
        Main.getCurrentStage().sceneProperty().addListener(listener);
    }

    public void passData(Integer groupChatPort, String groupName) {
        pageTitle.setText(groupName);
        this.isGroupChat = true;
        this.chatter = new ChatTransponder(groupChatPort, this);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setMenuSettings();
        this.scrollPane.vvalueProperty().bind(scrollElements.heightProperty());
        this.scrollElements.setPadding(new Insets(10));
        this.disableInput(true);
    }

    public void close() {
        System.out.println("ChatPartner DISCONNECTED");
        if (this.chatter != null)
            this.chatter.closeConnection();
        this.disableInput(true);
    }

    public void disableInput(Boolean x) {
        this.sendMessage.setDisable(x);
        this.messageInput.setDisable(x);
    }
}
