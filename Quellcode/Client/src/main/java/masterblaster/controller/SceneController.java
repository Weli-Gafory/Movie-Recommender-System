package masterblaster.controller;

import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import masterblaster.Main;

import java.io.File;
import java.io.IOException;
import java.net.URL;

//Der SceneController steuert den Wechsel von Szenen :)
public abstract class SceneController {

    @FXML
    protected MenuItem NutzerlisteMenu;

    @FXML
    protected MenuItem NutzeruebersichtMenu;

    @FXML
    protected MenuItem EinstellungenMenu;

    @FXML
    protected MenuItem ReportsMenu;

    @FXML
    protected MenuItem diskussionsgruppen;

    @FXML
    protected Menu filmAnlegen;

    protected ChangeListener<? super Scene> sceneSwitchListener;



    private void switchToSceneWithStage(String fxml) {
        try {
            Scene newScene = new Scene(new FXMLLoader(getClass().getResource(fxml)).load());
            Main.getCurrentStage().setScene(newScene);

            if (sceneSwitchListener != null) {
                Main.getCurrentStage().sceneProperty().removeListener(sceneSwitchListener);
                sceneSwitchListener = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void registerMenuButtonClicked() {
        switchToSceneWithStage("/fxml/Registrierung.fxml");
    }

    @FXML
    protected void registerNutzerMenuButtonClicked(){ switchToSceneWithStage("/fxml/Registrierung_Nutzer.fxml");}

    @FXML
    protected void databaseViewMenuButtonClicked() {
        switchToSceneWithStage("/fxml/TableView.fxml");
    }

    @FXML
    protected void loginMenuButtonClicked() {
        switchToSceneWithStage("/fxml/Login.fxml");
    }

    @FXML
    protected void filmManuellMenuButtonClicked() {
        switchToSceneWithStage("/fxml/filmManuellHinzufuegen.fxml");
    }

    @FXML
    protected void filmAutoMenuButtonClicked() {
        switchToSceneWithStage("/fxml/filmeAutomatisiertHinzufuegen.fxml");
    }

    @FXML
    protected void nutzerlisteMenuButtonClicked(){switchToSceneWithStage("/fxml/Nutzerliste.fxml"); }

    @FXML
    protected void nutzeruebersichtMenuButtonClicked(){switchToSceneWithStage("/fxml/NutzerUebersicht.fxml");}

    @FXML
    protected void reportsMenuButtonClicked(){switchToSceneWithStage("/fxml/FilmReportAnsicht.fxml");}

    @FXML
    protected void einstellungenMenuButtonClicked(){switchToSceneWithStage("/fxml/NutzerEinstellungen.fxml");}

    @FXML
    protected void watchlistClick(){switchToSceneWithStage("/fxml/Watchlist.fxml");}

    @FXML
    protected void geseheneFilmeClick(){switchToSceneWithStage("/fxml/GeseheneFilme.fxml");}

    @FXML
    protected void alleNutzerClick(){switchToSceneWithStage("/fxml/Nutzerliste.fxml");}

    @FXML
    protected void freundeslisteClick(){switchToSceneWithStage("/fxml/Freundesliste.fxml");}

   @FXML
   protected void nutzerBewertungenButtonClick(){switchToSceneWithStage("/fxml/DeineBewertungen.fxml");}

    @FXML
    protected void offeneFreundschaftsanfragenButtonClicked(){switchToSceneWithStage("/fxml/Freundschaftsanfragen.fxml");}

    @FXML
    protected void alleBewertungenButtonClicked(){switchToSceneWithStage("/fxml/BewertungenUebersicht.fxml");}

    @FXML
    protected void diskussionsgruppenMenuButtonClicked(){switchToSceneWithStage("/fxml/DiskussiongruppenUebersicht.fxml");}

    @FXML
    protected void offeneFilmeinladungenButtonClicked(){switchToSceneWithStage("/fxml/OffeneFilmeinladungen.fxml");}

    @FXML
    protected void messageButtonClicked(){switchToSceneWithStage("/fxml/Messages.fxml");}




    protected void setMenuSettings(){
        if (Main.isAdmin){
            NutzerlisteMenu.setVisible(false);
            NutzeruebersichtMenu.setVisible(false);
            diskussionsgruppen.setVisible(false);
            EinstellungenMenu.setVisible(false);
            ReportsMenu.setVisible(true);
            filmAnlegen.setVisible(true);
        } else {
            NutzerlisteMenu.setVisible(true);
            NutzeruebersichtMenu.setVisible(true);
            diskussionsgruppen.setVisible(true);
            EinstellungenMenu.setVisible(true);
            ReportsMenu.setVisible(false);
            filmAnlegen.setVisible(false);
        }
    }
}
