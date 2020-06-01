package application;
	
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


public class Main extends Application {
	
	@Override
	public void start(Stage primaryStage) {
		try {
			Parent loginUI = FXMLLoader.load(getClass().getResource("/login/ui.fxml"));
			Scene scene = new Scene(loginUI);		
			primaryStage = new Stage();
			primaryStage.setScene(scene);
			primaryStage.initStyle(StageStyle.UNDECORATED);
			primaryStage.show();
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
