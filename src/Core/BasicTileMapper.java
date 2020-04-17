package Core;

import processing.core.*;

import java.util.ArrayList;
import java.util.List;

import static Core.Main.P5;
import static processing.core.PConstants.RGB;

public class BasicTileMapper extends TileMapper{

	private PImage sourceImg;
	private int tileSize;
	private List<Tile> tileList;

	private boolean horizonFlip = false;
	private boolean verticalFlip = false;
	private boolean rotation = false;

	public BasicTileMapper(PImage sourceImg, int tileSize){
		this.sourceImg = sourceImg;
		this.tileSize = tileSize;
	}

	public void enableHorizonFlip(){
		horizonFlip = true;
	}

	public void enableVerticalFlip(){
		verticalFlip = true;
	}

	public void enableRotation(){
		rotation = true;
	}




	@Override
	public void constructTileMap(){

		tileList = new ArrayList<>();

		//元画像からのタイル切り出し
		for(int y = 0; y < sourceImg.height - 2; y++){
			for(int x = 0; x < sourceImg.width - 2; x++){
				PImage tileImg = P5.createImage(tileSize, tileSize, RGB);
				tileImg.copy(sourceImg, x, y, tileSize, tileSize, 0, 0, tileSize, tileSize);

				tileList.add(createTile(tileImg));

				if(rotation){
					tileList.add(createTile(imgLeftTurn(tileImg, 1)));
					tileList.add(createTile(imgLeftTurn(tileImg, 2)));
					tileList.add(createTile(imgLeftTurn(tileImg, 3)));
				}

				if(horizonFlip){
					tileList.add(createTile(imgHorizonFlip(tileImg)));
				}

				if(verticalFlip){
					tileList.add(createTile(imgVerticalFlip(tileImg)));
				}

			}
		}


		//隣接関係の構築
		tileList.forEach(tile -> tile.setUpAdjacency(tileList));
	}

	private int index = 0;
	private Tile createTile(PImage tileImg){
		return new Tile(tileImg, index++);
	}

	public PImage imgHorizonFlip(PImage sourceImg){
		PImage img = P5.createImage(sourceImg.width, sourceImg.height, RGB);
		sourceImg.loadPixels();
		img.loadPixels();
		for(int x = 0; x < img.width; x++){
			for(int y = 0; y < img.height; y++){
				img.pixels[x + img.width*y] = sourceImg.pixels[(img.width - x - 1) + img.width*y];
			}
		}
		img.updatePixels();

		return img;
	}

	public PImage imgVerticalFlip(PImage sourceImg){
		PImage img = P5.createImage(sourceImg.width, sourceImg.height, RGB);
		sourceImg.loadPixels();
		img.loadPixels();

		for(int x = 0; x < img.width; x++){
			for(int y = 0; y < img.height; y++){
				img.pixels[x + img.width*y] = sourceImg.pixels[x + img.width*(img.height - y - 1)];
			}
		}
		img.updatePixels();

		return img;
	}


	public PImage imgLeftTurn(PImage sourceImg, int num){
		PImage img = P5.createImage(sourceImg.width, sourceImg.height, RGB);
		sourceImg.loadPixels();
		img.loadPixels();

		switch(num % 4){
		case 0:
			break;
		case 1:
			for(int x = 0; x < img.width; x++){
				for(int y = 0; y < img.height; y++){
					img.pixels[x + img.width*y] = sourceImg.pixels[(img.width - y - 1) + img.width*x];
				}
			}
			break;
		case 2:
			for(int x = 0; x < img.width; x++){
				for(int y = 0; y < img.height; y++){
					img.pixels[x + img.width*y] = sourceImg.pixels[(img.width - x - 1) + img.width*(img.height - y - 1)];
				}
			}
			break;
		case 3:
			for(int x = 0; x < img.width; x++){
				for(int y = 0; y < img.height; y++){
					img.pixels[x + img.width*y] = sourceImg.pixels[y + img.width*(img.width - x - 1)];
				}
			}
			break;

		}

		img.updatePixels();

		return img;
	}

	@Override
	public List<Tile> toTileList(){
		return tileList;
	}
}
