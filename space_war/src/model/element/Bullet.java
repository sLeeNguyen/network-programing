package model.element;

public class Bullet extends Element {
	
	private static final String LASER_BLUE_PATH = "/view/resources/lasers/laser_blue06.png";
	
	public Bullet(String url, String type, double radius, double width, double height) {
		super(LASER_BLUE_PATH, type, radius, width, height);
	}
	
	public Bullet(String url, String type, double posX, double posY, double radius, double width, double height) {
		super(LASER_BLUE_PATH, type, radius, width, height);
		setLayoutX(posX);
		setLayoutY(posY);
	}
	
	public void moveUp(double y) {
		setLayoutY(getLayoutY() - y); 
	}
	
	public void moveDown(double y) {
		setLayoutY(getLayoutY() + y); 
	}
}
