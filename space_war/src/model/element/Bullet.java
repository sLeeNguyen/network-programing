package model.element;

public class Bullet extends Element {
	public Bullet(String url, int type, double radius, double width, double height) {
		super(url, type, radius, width, height);
	}
	
	public Bullet(String url, int type, double posX, double posY, double radius, double width, double height) {
		super(url, type, radius, width, height);
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
