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
	private boolean ignoreSamePattern = false;

	public BasicTileMapper(PImage sourceImg, int tileSize){
		this.sourceImg = sourceImg;
		this.tileSize = tileSize;
	}

	/**
	 * 同じパターンのタイルを完全に無視するかどうか
	 * trueにすると出現回数による重み付けがされなくなる
	 * @param set
	 */
	public void ignoreSamePattern(boolean set){
		ignoreSamePattern = set;
	}

	/**
	 * 水平反転を追加
	 */
	public void enableHorizonFlip(){
		horizonFlip = true;
	}

	/**
	 * 垂直反転を追加
	 */
	public void enableVerticalFlip(){
		verticalFlip = true;
	}

	/**
	 * 回転タイルを追加
	 */
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

				addTile(tileImg);

				if(rotation){
					addTile(imgLeftTurn(tileImg, 1));
					addTile(imgLeftTurn(tileImg, 2));
					addTile(imgLeftTurn(tileImg, 3));
				}

				if(horizonFlip){
					addTile(imgHorizonFlip(tileImg));
				}

				if(verticalFlip){
					addTile(imgVerticalFlip(tileImg));
				}

			}
		}

		tileList.forEach(tile -> {
			System.out.printf("%3d over:%3d\n", tile.id, tile.overlapWeight);
		});

		//隣接関係の構築
		tileList.forEach(tile -> tile.setUpAdjacency(tileList));
	}

	private int index = 0;

	private void addTile(PImage tileImg){
		Tile addTile = new Tile(tileImg, index);

		for(Tile tile: tileList){
			//重複している
			if(tile.isSamePattern(addTile)){
				if(! ignoreSamePattern){
					tile.countOverlap();
				}

				return;
			}
		}
		tileList.add(addTile);
		index++;
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
