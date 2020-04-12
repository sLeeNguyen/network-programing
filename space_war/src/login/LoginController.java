package login;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;

import helpers.CheckAndAlert;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class LoginController implements Initializable {
	
	private final double ROOT_WIDTH = 1244D;
	private final double ROOT_HEIGHT = 740D;
	
	private final double CHILD_WIDTH = 370D;
	private final double CHILD_HEIGHT = 440D;
	
    @FXML
    private AnchorPane root;

    @FXML
    private AnchorPane subroot;

    @FXML
    private VBox signInVBox;

    @FXML
    private JFXTextField usernameSignInJFX;

    @FXML
    private JFXPasswordField passwordSignInJFX;

    @FXML
    private VBox signUpVBox;

    @FXML
    private JFXTextField usernameSignUpJFX;

    @FXML
    private JFXPasswordField passwordSignUpJFX;

    @FXML
    private JFXPasswordField confirmPassSignUpJFX;
    
    
    @Override
	public void initialize(URL location, ResourceBundle resources) {
    	setUpTitleBar();
    	setElementLayout();
    	setPaneVisible(signInVBox, true);
	}
    
    private void setUpTitleBar() {
		try {
			Node node = FXMLLoader.load(getClass().getResource("/helpers/fxml/title_bar.fxml"));
		
			root.getChildren().add(node);
			AnchorPane.setTopAnchor(node, 0D);
	    	AnchorPane.setLeftAnchor(node, 0D);
	    	AnchorPane.setRightAnchor(node, 0D);
	    	
		} catch (IOException e) {
			CheckAndAlert.alertErrorMessage("Xảy ra lỗi. Xin hãy thử lại!");
			e.printStackTrace();
		}
	}
    
    private void setPaneVisible(VBox pane, boolean hasVisible) {
    	TranslateTransition trans = new TranslateTransition();
    	trans.setDuration(Duration.seconds(1D));
    	trans.setNode(pane);
    	
    	if (hasVisible) {
    		trans.setToY(550);
    	} else {
    		
    		trans.setToY(-550);
    	}
    	
    	trans.play();
    }
    
    
    private void setElementLayout() {
    	signUpVBox.setLayoutX((ROOT_WIDTH - CHILD_WIDTH) / 2);
    	signUpVBox.setLayoutY(-CHILD_HEIGHT);
    	
    	signInVBox.setLayoutX((ROOT_WIDTH - CHILD_WIDTH) / 2);
    	signInVBox.setLayoutY(-CHILD_HEIGHT);
    }
    
    @FXML
    void onForgotPassword(MouseEvent event) {

    }

    @FXML
    void onOpenSignUpPane(MouseEvent event) {
    	setPaneVisible(signInVBox, false);
    	setPaneVisible(signUpVBox, true);
    }

    @FXML
    void onSignIn(ActionEvent event) {

    }

    @FXML
    void onSignUp(ActionEvent event) {
    	setPaneVisible(signInVBox, true);
    	setPaneVisible(signUpVBox, false);

    }
}
