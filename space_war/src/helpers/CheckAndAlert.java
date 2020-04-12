package helpers;

import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class CheckAndAlert {
	   public static final void alertErrorMessage(String msg) {
	        Alert alert = new Alert(Alert.AlertType.ERROR);
	        alert.setTitle("Error");
	        alert.setHeaderText(null);
	        alert.setContentText(msg);
	        alert.showAndWait();
	    }

	    public static final void alertSuccessMessage(String msg) {
	        Alert alert = new Alert(Alert.AlertType.INFORMATION);
	        alert.setTitle("Success");
	        alert.setHeaderText(null);
	        alert.setContentText(msg);
	        alert.showAndWait();
	    }

	    public static final boolean alertConfirmMessage(String msg) {
	        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
	        alert.setTitle("Confirm");
	        alert.setHeaderText(null);
	        alert.setContentText(msg);

	        Optional<ButtonType> result = alert.showAndWait();

	        return result.get() == ButtonType.OK;
	    }
}
