package main.grid;

import org.newdawn.slick.*;

public class NormalGridSquare implements GridSquare{
	
	private Image tile_image;
	
	SpriteSheet sheet = new SpriteSheet("img/tileset/tile2.png", width,height);
	private Image normal_tile_image = sheet.getSprite(3,0);
	
	// private Image normalTileImage = new Image ("/res/img/tileset/WoodBlock.png");
	
	public NormalGridSquare() throws SlickException {
		this.tile_image = normal_tile_image;
		
	}
	
	public Image getImage()
	{
		return tile_image;
	}


}
