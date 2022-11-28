package masterblaster.controller;


import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.text.Text;
import masterblaster.Main;
import masterblaster.models.Film;

import org.json.JSONArray;

import java.io.File;
import java.io.FileWriter;
import java.net.URL;
import java.util.ResourceBundle;
import javax.swing.*;
import javax.swing.filechooser.FileSystemView;


public class FilmStatistikAnsichtController extends SceneController implements Initializable {

    private Film film;
    @FXML
    private Text titleName;

    @FXML
    private Text avgBewertung;

    @FXML
    private Text anzahlBewertung;

    @FXML
    private Text anzahlGesehen;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setMenuSettings();
    }


    public void passFilmData(Film film, String prev){
        this.film = film;
        titleName.setText(film.getName());
        try{
            showStats(film.getId());
        } catch(Exception e){
            System.out.println(e);
        }

    }

    @FXML
    private void downloadButtonClicked(){
        JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        jfc.setDialogTitle("Choose a directory to save your file: ");

        int rueckgabeWert = jfc.showOpenDialog(null);

        // Abfrage, ob auf "Öffnen" geklickt wurde
        if(rueckgabeWert == JFileChooser.APPROVE_OPTION) {
            String filename = jfc.getSelectedFile().getName();
            if (filename.contains(".")){
                JOptionPane.showMessageDialog(null, "Der Dateiname ist fehlerhaft! Bitte versuche es erneut.", "InfoBox: " + "Fehlgeschlagen!", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                filename = jfc.getCurrentDirectory() + "\\" + filename + ".txt";
                File file = new File(filename);
                if (!file.createNewFile()) {  //Falls Datei schon existiert
                    JOptionPane.showMessageDialog(null, "Die Datei existiert bereits und wurde deswegen ersetzt!", "InfoBox: " + "Erfolgreich!", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "Statistik wurde gedownloadet", "InfoBox: " + "Erfolgreich", JOptionPane.INFORMATION_MESSAGE);
                }

                FileWriter writer = new FileWriter(filename);
                writer.write("Statistik von " + film.getName() + "\n" +
                        "Durchschnittliche Bewertung: " + avgBewertung.getText() + "\n" +
                        "Anzahl der Bewertungen: " + anzahlBewertung.getText() + "\n" +
                        "Anzahl Gesehen: " + anzahlGesehen.getText());
                writer.close();
            } catch(Exception e){
                JOptionPane.showMessageDialog(null, "Irgendwas ist falsch gelaufen! Bitte versuche es erneut.", "InfoBox: " + "Fehlgeschlagen!", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    @FXML
    private void zurueckButtonClicked(){
        databaseViewMenuButtonClicked();
    }

    @FXML
    private void zuruecksetzenButtonClicked(){
        try{
            Main.getClient().resetFilmstatistik(film.getId());
            showStats(film.getId());
            JOptionPane.showMessageDialog(null, "Statistik wurde zurückgesetzt", "InfoBox: " + "Erfolgreich", JOptionPane.INFORMATION_MESSAGE);
        } catch(Exception ex){
            JOptionPane.showMessageDialog(null, "Statistik konnte nicht zurückgesetzt werden!", "InfoBox: " + "Fehlgeschlagen!", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showStats(int filmID) throws Exception{
        JSONArray stats = Main.getClient().getFilmstatistik(film.getId());
        avgBewertung.setText(stats.getJSONObject(0).getString("avgBewertung"));
        anzahlBewertung.setText(stats.getJSONObject(0).getString("anzahlBewertung"));
        anzahlGesehen.setText(stats.getJSONObject(0).getString("anzahlGesehen"));

    }
}
