package masterblaster.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import masterblaster.Main;
import masterblaster.models.Systemadministrator;

public class RegistrierungController extends SceneController {

    @FXML
    private TextField Vorname;

    @FXML
    private TextField Nachname;

    @FXML
    private TextField EMailAdresse;

    @FXML
    private PasswordField Passwort;

    @FXML
    private Button RegistrierungsButton;

    @FXML
    private Text info;

    @FXML
    private void registrierungButtonClick() {
        registerUser(Vorname.getText(), Nachname.getText(), EMailAdresse.getText(), Passwort.getText());
    }

    private void registerUser(String vn, String nn, String mail, String pw) {
        Systemadministrator sysAdmin = new Systemadministrator(vn, nn, mail, pw);

        try {
            Systemadministrator user = Main.getClient().registerAction(sysAdmin);

            loginMenuButtonClicked(); // LoginMenuButton ist der Button im Menu der zur Login Sicht leitet
            info.setText("Nutzer erfolgreich registriert!");
            info.setVisible(true);

        } catch (Exception e) {
            System.out.println(e.getMessage());
            info.setText("Nutzer existiert bereits, bitte andere Email verwenden!");
            info.setVisible(true);
        }
    }
}
