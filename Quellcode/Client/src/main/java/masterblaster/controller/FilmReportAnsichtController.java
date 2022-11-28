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
import masterblaster.models.Film;
import masterblaster.models.Report;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.ResourceBundle;


public class FilmReportAnsichtController extends SceneController implements Initializable {
    @FXML
    private TableView<Report> filmTableView;
    @FXML
    private TableColumn<Report,Integer> id;

    @FXML
    private TableColumn <Report,String>titel;

    @FXML
    private TableColumn <Report,String> beschreibung;

    @FXML
    private Button foreward;

    @FXML
    private Button back;

    ObservableList<Report> reports = FXCollections.observableArrayList();


    @FXML
    private void forewardButtonClicked(){

    }

    @FXML
    private void backButtonClicked(){

    }

    @FXML
    private void moderiertButtonClicked(){
        try {
            Main.getClient().deleteReport(getSelectedReport());
            reports.remove(filmTableView.getSelectionModel().getSelectedItem());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setMenuSettings();
        System.out.println("Reports");
       // id.setCellValueFactory(new PropertyValueFactory<>("id"));
        titel.setCellValueFactory(new PropertyValueFactory<>("titel"));
        beschreibung.setCellValueFactory(new PropertyValueFactory<>("beschreibung"));
        try {
            ObservableList<Report> reports = getAllReports();
            filmTableView.setItems(reports);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private ObservableList<Report> getAllReports() throws Exception {
        System.out.println("test");
        JSONArray jsonArray = Main.getClient().getAllReports();


        for (int i = 0; i <jsonArray.length() ; i++) {

            JSONObject object = jsonArray.getJSONObject(i);

            // TODO: 17.05.2022 Filmbanner hinzufuegen
            Integer id = object.getInt("idNo");
            String titel = object.getString("name");
            String beschreibung = object.getString("report");
            Report report = new Report(id, titel, beschreibung);
            reports.add(report);
        }
        return reports;
    }

    private Report getSelectedReport(){
        return filmTableView.getSelectionModel().getSelectedItem();
    }
}
