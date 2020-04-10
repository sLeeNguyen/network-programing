package model.element;

import javafx.scene.image.ImageView;
import javafx.util.Pair;

public class Element extends ImageView {
	
	private final double ELEMENT_RADIUS;
	private final double WIDTH;
	private final double HEIGHT;
	
	private String type;
	private boolean dead = false;
	
	public Element(String url, double radius, double width, double height) {
		super(url);
		this.type = null;
		this.ELEMENT_RADIUS = radius;
		this.WIDTH = width;
		this.HEIGHT = height;
	}
	
	public Element(String url, String type, double radius, double width, double height) {
		super(url);
		this.type = type;
		this.ELEMENT_RADIUS = radius;
		this.WIDTH = width;
		this.HEIGHT = height;
	}
	
	public String getType() {
		return type;
	}
	
	public boolean isDead() {
		return dead;
	}
	
	public void dead(boolean dead) {
		this.dead = dead;
	}
	
	public double getRadius() {
		return ELEMENT_RADIUS;
	}
	
	public Pair<Double, Double> getCoordinate() {
		double x = getLayoutX() + WIDTH / 2;
    	double y = getLayoutY() + HEIGHT / 2;
		return new Pair<Double, Double>(x, y);
	}

	public double getWidth() {
		return WIDTH;
	}
	
	public double getHeight() {
		return HEIGHT;
	}
}
