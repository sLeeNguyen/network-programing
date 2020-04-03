package model.element;

public class Bullet extends Element {
	
	private static final String LASER_BLUE_PATH = "/view/resources/lasers/laser_blue06.png";
	public static final int HEIGHT = 37;
	public static final int WIDTH = 17;
	
	public Bullet(String url, String type) {
		super(LASER_BLUE_PATH, type);
	}
	
	public Bullet(String url, String type, double posX, double posY) {
		super(LASER_BLUE_PATH, type);
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
