package model.element;

import javafx.scene.image.ImageView;

public class Element extends ImageView {

	private String type;
	private boolean dead = false;
	
	public Element(String url) {
		super(url);
		this.type = null;
	}
	
	public Element(String url, String type) {
		super(url);
		this.type = type;
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
}
