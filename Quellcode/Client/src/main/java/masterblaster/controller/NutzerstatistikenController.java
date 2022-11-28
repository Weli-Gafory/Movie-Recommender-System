package masterblaster.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import masterblaster.Main;
import masterblaster.models.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class NutzerstatistikenController extends SceneController implements Initializable {

    @FXML
    private Label gesamtdauer;

    @FXML
    private Button sender;
    @FXML
    private TableView<Actors> lieblingsschauspieler;

    @FXML
    private TableColumn<Actors, Integer> lieblingsschauspielerPlatz;

    @FXML
    private TableColumn<Actors, String> lieblingsschauspielerName;

    @FXML
    private TableView<Genre> lieblingskategorie;

    @FXML
    private TableColumn<Genre, Integer> lieblingskategoriePlatz;

    @FXML
    private TableColumn<Genre, String> lieblingskategorieName;

    @FXML
    private TableView<BestMovie> lieblingsfilm;

    @FXML
    private TableColumn<BestMovie, Integer> lieblingsfilmPlatz;

    @FXML
    private TableColumn<BestMovie, String> lieblingsfilmName;

    @FXML
    private DatePicker von;

    @FXML
    private DatePicker bis;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setMenuSettings();
        lieblingsschauspielerPlatz.setCellValueFactory(new PropertyValueFactory<>("counter"));
        lieblingsschauspielerName.setCellValueFactory(new PropertyValueFactory<>("name"));
        lieblingskategoriePlatz.setCellValueFactory(new PropertyValueFactory<>("counter"));
        lieblingskategorieName.setCellValueFactory(new PropertyValueFactory<>("name"));
        lieblingsfilmPlatz.setCellValueFactory(new PropertyValueFactory<>("bewertung"));
        lieblingsfilmName.setCellValueFactory(new PropertyValueFactory<>("name"));

        von.getEditor().setDisable(true);
        bis.getEditor().setDisable(true);
        von.getEditor().setOpacity(1);
        bis.getEditor().setOpacity(1);
        action(0,99999999);
    }

    public void action(Integer von, Integer bis){
        JSONArray data;
        try {
            data = Main.getClient().getUserStatistics(Main.getClient().getNutzer().getId(),von,bis);
            System.out.println(data.toString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        gesamtdauer.setText("Deine Watchtime beträgt " + getLength(data.getJSONArray(2)));
        gesamtdauer.setWrapText(true);


        ObservableList<Actors> actors = getAllActors(data.getJSONArray(0));
        ObservableList<Genre> genre = getAllGenre(data.getJSONArray(1));
        ObservableList<BestMovie> bestmovies = getBestMovies(data.getJSONArray(3));

        lieblingsschauspieler.setItems(actors);
        lieblingskategorie.setItems(genre);
        lieblingsfilm.setItems(bestmovies);
    }

    public ObservableList<Actors> getAllActors(JSONArray data){

        ObservableList<Actors> actors = FXCollections.observableArrayList();


        for (int i=0; i<data.length();i++) {
            JSONObject object = data.getJSONObject(i);

            String name = object.getString("name");
            int platzierung = i+1;

            Actors b = new Actors(name, platzierung);
            actors.add(b);
        }
        return actors;
    }

    public ObservableList<Genre> getAllGenre(JSONArray data){

        ObservableList<Genre> genre = FXCollections.observableArrayList();


        for (int i=0; i<data.length();i++) {
            JSONObject object = data.getJSONObject(i);

            String name = object.getString("name");
            int platzierung = i+1;

            Genre b = new Genre(name, platzierung);
            genre.add(b);
        }
        return genre;
    }

    public ObservableList<BestMovie> getBestMovies(JSONArray data){

        ObservableList<BestMovie> bestMovies = FXCollections.observableArrayList();


        for (int i=0; i<data.length();i++) {
            JSONObject object = data.getJSONObject(i);

            String name = object.getString("name");
            int platzierung = i+1;

            BestMovie b = new BestMovie(name, platzierung);
            bestMovies.add(b);
        }
        return bestMovies;
    }

    public String getLength(JSONArray data){

        Integer minutes = data.getJSONObject(0).getInt("filmlaengeGes");
        Integer hours=0;
        while(minutes>=60){
            minutes-=60;
            hours+=1;
        }
        String length = hours+" Stunden und " + minutes +" Minuten";
        return length;
    }
    @FXML
    private void senderButtonClicked(){
        Integer bis2;
        Integer von2;
        if(bis.getValue()==null){
            bis2=99999999;
        }
        else{
            String bis1 = bis.getValue().toString();
            bis2 = Integer.parseInt(bis1.substring(0,4)+bis1.substring(5,7)+bis1.substring(8,10));
        }
        if(von.getValue()==null){
            von2=0;
        }
        else{
            String von1 = von.getValue().toString();
            von2 = Integer.parseInt(von1.substring(0,4)+von1.substring(5,7)+von1.substring(8,10));
        }
        action(von2,bis2);
    }

    public void resetButtonClicked() {
        von.setValue(null);
        bis.setValue(null);
        action(0,0);
    }
    public class Actors {
        private String name;
        private int counter;
        public Actors(String name, Integer counter){
            super();
            this.name=name;
            this.counter=counter;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getCounter() {
            return counter;
        }

        public void setCounter(Integer counter) {
            this.counter = counter;
        }
    }
    public class BestMovie {
        private String name;
        private int bewertung;
        public BestMovie(String name, Integer bewertung){
            super();
            this.name=name;
            this.bewertung=bewertung;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getBewertung() {
            return bewertung;
        }

        public void setBewertung(Integer bewertung) {
            this.bewertung = bewertung;
        }
    }
    public class Genre {
        private String name;
        private int counter;
        public Genre(String name, Integer counter){
            super();
            this.name=name;
            this.counter=counter;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getCounter() {
            return counter;
        }

        public void setCounter(Integer counter) {
            this.counter = counter;
        }
    }
}
