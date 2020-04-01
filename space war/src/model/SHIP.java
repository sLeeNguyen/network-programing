package model;

public enum SHIP {
    BLUE("/view/resources/shipchooser/ship_blue.png"),
    GREEN("/view/resources/shipchooser/ship_green.png"),
    ORANGE("/view/resources/shipchooser/ship_orange.png"),
    RED("/view/resources/shipchooser/ship_red.png");

    String urlShip;

    private SHIP(String urlShip) {
        this.urlShip = urlShip;
    }

    public String getUrlShip() {
        return urlShip;
    }
}