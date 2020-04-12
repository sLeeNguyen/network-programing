package application;
	
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import login.LoginController;
import view.ViewManager;


public class Main extends Application {
	
	@Override
	public void start(Stage primaryStage) {
		try {
			Parent loginUI = FXMLLoader.load(getClass().getResource("/login/ui.fxml"));
			Scene scene = new Scene(loginUI);		
			Stage stage = new Stage();
			stage.setScene(scene);
			stage.initStyle(StageStyle.UNDECORATED);
			stage.show();
			
//			ViewManager manager = new ViewManager();
//			Stage stage = manager.getMainStage();
//			stage.initStyle(StageStyle.UNDECORATED);
//			stage.show();
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
