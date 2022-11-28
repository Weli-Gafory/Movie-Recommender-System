module masterblaster {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;
    requires org.json;
    requires com.google.gson;
    requires java.desktop;
    //requires org.junit.jupiter.api;


    opens masterblaster to javafx.fxml;
    //opens com.example.client to com.google.gson;
    exports masterblaster;
    exports masterblaster.controller;
    opens masterblaster.controller to javafx.fxml;
    exports masterblaster.models;
    opens masterblaster.models to javafx.fxml;
    exports masterblaster.clientServer;
    opens masterblaster.clientServer to javafx.fxml;
    opens fxml to javafx.fxml;
}

