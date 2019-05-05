package utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;

/**
 * @author Cap_Sub
 */
public class FxmlUtils {

    public static Parent LoadFxml(String fxmlName) throws IOException {
        return FXMLLoader.load(FxmlUtils.class.getResource("/fxml/" + fxmlName));
    }

}
