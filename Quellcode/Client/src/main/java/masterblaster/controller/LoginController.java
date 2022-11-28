package masterblaster.controller;

import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import masterblaster.Main;
import masterblaster.models.Nutzer;
import masterblaster.models.Systemadministrator;
import org.json.JSONObject;

import javax.swing.*;

import java.awt.*;

import static javax.swing.JOptionPane.showMessageDialog;

public class LoginController extends SceneController {

    @FXML
    private TextField EMailAdresse;

    @FXML
    private PasswordField Passwort;

    @FXML
    private Text LoginFail;

    @FXML
    private Text EmailText;

    @FXML
    private Text PasswortText;

    @FXML
    private Toggle AdminToggle;
    @FXML
    private Toggle NutzerToggle;

    @FXML
    private Button LoginButton;

    @FXML
    private PasswordField ZweiFaktorCode;

    @FXML
    private Button LoginZweiFaktorButton;

    @FXML
    private Text ZweiFaktorNachricht;

    private String twoFACode;

    //Aufheben fürs "echte Login"
    @FXML
    protected void loginButtonClick() {
        login(EMailAdresse.getText(), Passwort.getText(), AdminToggle.isSelected());
    }
    @FXML
    protected void loginZweiFaktorButtonClick() throws Exception {
        String temp1 = ZweiFaktorCode.getText();
        if (temp1.equals(twoFACode)) {
            System.out.println(Main.getClient().sumOpenInvitations(Main.getClient().getNutzer().getId()));
            if (Main.getClient().sumOpenInvitations(Main.getClient().getNutzer().getId()) != 0){
                int anzahlEinladungen = Main.getClient().sumOpenInvitations(Main.getClient().getNutzer().getId());
                JOptionPane.showMessageDialog(null, "Du hast " + anzahlEinladungen +  " offene Einladungen,\n" +
                        "für mehr Informationen, öffne die Nutzerübersicht!", "Offene Einladungen: ", JOptionPane.INFORMATION_MESSAGE);
            }
            databaseViewMenuButtonClicked();
        }
            //hier kann die Nachricht rein!
        else {
            LoginFail.setText("Der eingegebene Code ist falsch, \n bitte versuch' es erneut!");
            LoginFail.setVisible(true);
        }

    }



    private void login(String mail, String pw, boolean isAdminLogin) {
        JSONObject credentials = new JSONObject()
                .put("mail", mail)
                .put("password", pw);

        try {
            if(isAdminLogin) {
                Systemadministrator user = Main.getClient().loginAction(credentials);
                Main.getClient().setMensch(user);
                Main.setIsAdmin(true);
                databaseViewMenuButtonClicked();
                System.out.println("Seht her! Ein Admin!");

            } else {
                Nutzer user = Main.getClient().loginUserAction(credentials);
                LoginButton.setDisable(true);
                this.twoFACode = Main.getClient().new2faRequest(user);
                Main.getClient().setMensch(user);
                Main.getClient().setNutzer(user);
                Main.setIsAdmin(false);
                zweiFaktor();
                System.out.println("es ist kein Admin Kollegen!");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            LoginFail.setVisible(true);
        }
    }

    private void zweiFaktor(){
        EmailText.setVisible(false);
        PasswortText.setVisible(false);
        EMailAdresse.setVisible(false);
        Passwort.setVisible(false);
        LoginButton.setVisible(false);
        LoginFail.setVisible(false);

        Group g = new Group();
        g.getChildren().add((Node) NutzerToggle);
        g.getChildren().add((Node) AdminToggle);
        g.setVisible(false);

        ZweiFaktorCode.setVisible(true);
        LoginZweiFaktorButton.setVisible(true);
        ZweiFaktorNachricht.setText("Bitte geben Sie den Code ein, der an " + EMailAdresse.getText() + " gesendet wurde!");
        ZweiFaktorNachricht.setVisible(true);

    }

    private boolean isAdminLogin(){
        if (AdminToggle.isSelected()){
            return true;
        }
        else{
            return false;
        }
    }

    private boolean isNutzerLogin(){
        if (NutzerToggle.isSelected()){
            return true;
        }
        else {
            return false;
        }
    }

}
