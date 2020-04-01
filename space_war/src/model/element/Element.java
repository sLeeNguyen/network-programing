package model.element;

import javafx.scene.image.ImageView;

public class Element extends ImageView {

	private String type;
	
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
}
