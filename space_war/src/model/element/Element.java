package model.element;

import javafx.scene.image.ImageView;
import javafx.util.Pair;

public class Element extends ImageView {
	
	private final double ELEMENT_RADIUS;
	private final double WIDTH;
	private final double HEIGHT;
	
	private int type;
	private int id;
	private boolean dead = false;
	
	public Element(String url, double radius, double width, double height) {
		super(url);
		this.type = EType.NONE;
		this.ELEMENT_RADIUS = radius;
		this.WIDTH = width;
		this.HEIGHT = height;
	}
	
	public Element(String url, double radius, double width, double height, int id) {
		super(url);
		this.type = EType.NONE;
		this.ELEMENT_RADIUS = radius;
		this.WIDTH = width;
		this.HEIGHT = height;
		this.id = id;
	}
	
	public Element(String url, int type, double radius, double width, double height) {
		super(url);
		this.type = type;
		this.ELEMENT_RADIUS = radius;
		this.WIDTH = width;
		this.HEIGHT = height;
	}
	
	public Element(String url, int type, double radius, double width, double height, int id) {
		super(url);
		this.type = type;
		this.ELEMENT_RADIUS = radius;
		this.WIDTH = width;
		this.HEIGHT = height;
		this.id = id;
	}
	
	public int getType() {
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
	
	public int getID() {
		return id;
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
