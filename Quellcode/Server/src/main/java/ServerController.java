import clientServer.ClientHandler;
import clientServer.DB;
import org.eclipse.jetty.server.Server;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ServerController {

    static Server server = new Server(1999);

    public static void main(String[] args) throws SQLException {
        DB.main(args);
        createBeispieldaten();

        server.setHandler(new ClientHandler());
        try {
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void createBeispieldaten() {
        copyPicture("/banner/platzhalter.png", ".banner/platzhalter.png", false);
        System.out.println("copied filmbanner platzhalter");
        copyPicture("/profilepics/platzhalter.png", "./.profilepics/platzhalter.png", true);
        createSomeUsers();
    }

    private static void createSomeUsers() {
        try {
            if (!DB.checkNutzer("angela@merkel.de")) {
                copyBeispielBilder();
                DB.addNutzer("Angela", "Merkel", "angela@merkel.de", "angela@merkel.de", "1959-02-31", "./.profilepics/merkel.jpg");
                DB.addNutzer("Theo", "Waigel", "theo@waigel.de", "theo@waigel.de", "1959-02-31", "./.profilepics/theowaigol.jpg");
                DB.addNutzer("Volker", "Bouffier", "volker@bouffier.de", "volker@bouffier.de", "1959-02-31", "./.profilepics/volkerbuff.jpg");
                DB.addNutzer("Heidi", "Klum", "heidi@klum.de", "heidi@klum.de", "1933-04-14", "./.profilepics/heidischneidi.png");
                DB.addNutzer("Olaf", "Scholz", "olaf@scholz.de", "olaf@scholz.de", "1985-12-12", "./.profilepics/daddy.png");
                DB.addNutzer("Sky", "du Mont", "sky@dumont.de", "sky@dumont.de", "1947-12-12", "./.profilepics/dumont.png");
                DB.addNutzer("Agnes", "Strack-Zimmermann", "agnes@zimmer.de", "agnes@zimmer.de", "1947-12-12", "./.profilepics/zimmer.png");

                System.out.println("Beispieluser erfolgreich angelegt");
            }
            if(!DB.checkNutzer("beispiel@user.de")){
                DB.addNutzer("Beispiel","User","beispiel@user.de","beispiel@user.de","01-01-2000","./.profilepics/daddy.png");
            }

        } catch (Exception ex) {
            System.out.println("Server or Database not yet startet");
        }
    }

    private static void copyBeispielBilder() {
        copyPicture("/profilepics/merkel.jpg", "./.profilepics/merkel.jpg", true);
        copyPicture("/profilepics/theowaigol.jpg", "./.profilepics/theowaigol.jpg", true);
        copyPicture("/profilepics/volkerbuff.jpg", "./.profilepics/volkerbuff.jpg", true);
        copyPicture("/profilepics/heidischneidi.png", "./.profilepics/heidischneidi.png", true);
        copyPicture("/profilepics/daddy.png", "./.profilepics/daddy.png", true);
        copyPicture("/profilepics/dumont.png", "./.profilepics/dumont.png", true);
        copyPicture("/profilepics/zimmer.png", "./.profilepics/zimmer.png", true);
    }

    private static void copyPicture(String source, String target, boolean isProfilPic) {
        try {
            Files.createDirectories(Path.of(isProfilPic ? "./.profilepics/" : "./.banner/"));
            URI ressourceURI = ServerController.class.getResource(source).toURI();

            if ("jar".equals(ressourceURI.getScheme())) {
                Map<String, String> env = new HashMap<>();
                String[] array = ressourceURI.toString().split("!");
                FileSystem fs = FileSystems.newFileSystem(URI.create(array[0]), env);
                Path sourcePath = fs.getPath(array[1]);
                Path targetPath = Path.of(target);
                Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
                fs.close();
                return;
            }

            Path sourcePath = Path.of(ressourceURI);
            Path targetPath = Path.of(target);
            Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }
    }

}
