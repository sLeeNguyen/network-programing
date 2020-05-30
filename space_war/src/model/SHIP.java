package model;

public enum SHIP {
    BLUE1("/resources/shipchooser/playerShip1_blue.png", "/resources/lasers/laserBlue01.png"),
    BLUE2("/resources/shipchooser/playerShip2_blue.png", "/resources/lasers/laserBlue02.png"),
    BLUE3("/resources/shipchooser/playerShip3_blue.png", "/resources/lasers/laserBlue03.png"),
    
    GREEN1("/resources/shipchooser/playerShip1_green.png", "/resources/lasers/laserGreen01.png"),
    GREEN2("/resources/shipchooser/playerShip2_green.png", "/resources/lasers/laserGreen02.png"),
    GREEN3("/resources/shipchooser/playerShip3_green.png", "/resources/lasers/laserGreen03.png"),
    
    ORANGE1("/resources/shipchooser/playerShip1_orange.png", "/resources/lasers/laserRed01.png"),
    ORANGE2("/resources/shipchooser/playerShip2_orange.png", "/resources/lasers/laserRed02.png"),
    ORANGE3("/resources/shipchooser/playerShip3_orange.png", "/resources/lasers/laserRed03.png"),
    
    RED1("/resources/shipchooser/playerShip1_red.png", "/resources/lasers/laserRed01.png"),
    RED2("/resources/shipchooser/playerShip2_red.png", "/resources/lasers/laserRed02.png"),
    RED3("/resources/shipchooser/playerShip3_red.png", "/resources/lasers/laserRed03.png");
	
    String urlShip;
    String urlBullet;

    private SHIP(String urlShip, String urlBullet) {
        this.urlShip = urlShip;
        this.urlBullet = urlBullet;
    }

    public String getUrlShip() {
        return urlShip;
    }
    
    public String getUrlBullet() {
    	return urlBullet;
    }
}