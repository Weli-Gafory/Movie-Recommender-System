package masterblaster.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import masterblaster.Main;
import masterblaster.models.Nutzer;
import masterblaster.models.RecommendedMovies;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class RecommendedMoviesController extends SceneController implements Initializable {
    @FXML
    private TableColumn<RecommendedMovies, String> aehnlichkeitsScore;

    @FXML
    private TableColumn<RecommendedMovies, String> categorie;

    @FXML
    private TableView<RecommendedMovies> filmTableView;
    @FXML
    private AnchorPane mainPanel;

    @FXML
    private TableColumn<RecommendedMovies, String> name;


    @FXML
    private TableColumn<RecommendedMovies, String> regisseur;
     //ObservableList<RecommendedMovies> recommendedMovies= FXCollections.observableArrayList();

    private Nutzer nutzer;
    @FXML
    private Label anzahl;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.filmTableView.getItems().clear();
        nutzer = Main.getClient().getNutzer();
        System.out.println(nutzer.getId());
        System.out.println("Recommendedmovies controller initialized");

        setMenuSettings();
        aehnlichkeitsScore.setCellValueFactory(new PropertyValueFactory<>("aehnlichkeitsScore"));
        categorie.setCellValueFactory(new PropertyValueFactory<>("kategory"));
        name.setCellValueFactory(new PropertyValueFactory<>("name"));
        regisseur.setCellValueFactory(new PropertyValueFactory<>("director"));
        try {
            ObservableList<RecommendedMovies> recommendedMovies=getFilms();
            filmTableView.setItems(recommendedMovies);
            anzahl.setText(String.valueOf(recommendedMovies.size()));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }



    }


    private ObservableList<RecommendedMovies> getFilms() throws IOException, InterruptedException {

        JSONArray jsonArray = Main.getClient().getRecommendedMovedBasedOnMySeenList(nutzer);

        ObservableList<RecommendedMovies> recommendedMovies = FXCollections.observableArrayList();


        for (int i = 0; i < jsonArray.length(); i++) {

            JSONObject object = jsonArray.getJSONObject(i);

            String name = object.getString("name");
            String kategorie = object.getString("category");
            String regisseur = object.getString("regisseur");
            String aehnlichkeitsScore = object.getString("aenlichkeit");
            RecommendedMovies recommendedMovie = new RecommendedMovies(name,kategorie,regisseur,aehnlichkeitsScore);
            recommendedMovies.add(recommendedMovie);
        }
        return recommendedMovies;

       }

}
