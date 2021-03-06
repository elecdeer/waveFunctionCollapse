package Core;

import processing.core.*;

import java.util.List;

import static Core.Main.P5;

public class Tile{
	public int id;
	public int overlapWeight = 1;

	private PImage img;


	private AdjacencyMask[] adjacency;


	public Tile(PImage tileImg, int id){
		this.id = id;
		this.img = tileImg;
	}

	public void countOverlap(){
		overlapWeight++;
	}

	public void setUpAdjacency(List<Tile> tileList){
		//上下左右
		adjacency = new AdjacencyMask[4];
		for(int i = 0; i < adjacency.length; i++){
			adjacency[i] = new AdjacencyMask(tileList.size());
		}

		tileList.forEach(tile -> {
			int weight = tile.overlapWeight;
//
			adjacency[0].set(tile.id, canAdjacencyTop(tile), weight);
			adjacency[1].set(tile.id, canAdjacencyBottom(tile), weight);
			adjacency[2].set(tile.id, canAdjacencyLeft(tile), weight);
			adjacency[3].set(tile.id, canAdjacencyRight(tile), weight);
		});

	}


	public void draw(int x, int y){
//		P5.color(img.pixels[0]);
//		P5.rect(x, y, img.width, img.height);

//		int scale = 12;
//		P5.copy(img, 0, 0, 1, 1, x*scale, y*scale, scale, scale);

		P5.image(img, x, y);
	}

	public AdjacencyMask getAdjacency(int dir){
		return adjacency[dir];
	}

	public int getPixel(int x, int y, int dx, int dy){
		int getX = x + dx;
		int getY = y + dy;
//		System.out.printf("tile%d (%d,%d): %x\n", id, getX, getY, img.pixels[getX + getY*img.width]);

		if(getX < 0 || img.width <= getX || getY < 0 || img.height <= getY){
			throw new IndexOutOfBoundsException();
		}

		return img.pixels[getX + getY*img.width];
	}


	public boolean isDifferentColor(int colorA, int colorB){
		return colorA != colorB;
	}


	public boolean canAdjacencyRight(Tile targetTile){

		for(int i = 0; i < img.width - 1; i++){
			for(int j = 0; j < img.height; j++){
				int colorA = getPixel(i, j, 1, 0);
				int colorB = targetTile.getPixel(i, j, 0, 0);

				if(isDifferentColor(colorA, colorB)) return false;
			}
		}
		return true;
	}


	public boolean canAdjacencyLeft(Tile targetTile){
		for(int i = 0; i < img.width - 1; i++){
			for(int j = 0; j < img.height; j++){
				int colorA = getPixel(i, j, 0, 0);
				int colorB = targetTile.getPixel(i, j, 1, 0);

				if(isDifferentColor(colorA, colorB)) return false;
			}
		}
		return true;
	}

	public boolean canAdjacencyTop(Tile targetTile){
		for(int i = 0; i < img.width; i++){
			for(int j = 0; j < img.height - 1; j++){
				int colorA = getPixel(i, j, 0, 0);
				int colorB = targetTile.getPixel(i, j, 0, 1);

				if(isDifferentColor(colorA, colorB)) return false;
			}
		}
		return true;
	}

	public boolean canAdjacencyBottom(Tile targetTile){
		for(int i = 0; i < img.width; i++){
			for(int j = 0; j < img.height - 1; j++){
				int colorA = getPixel(i, j, 0, 1);
				int colorB = targetTile.getPixel(i, j, 0, 0);

				if(isDifferentColor(colorA, colorB)) return false;
			}
		}
		return true;
	}


	public boolean isSamePattern(Tile targetTile){
		for(int i = 0; i < img.width; i++){
			for(int j = 0; j < img.height; j++){
				int colorA = getPixel(i, j, 0, 0);
				int colorB = targetTile.getPixel(i, j, 0, 0);

				if(isDifferentColor(colorA, colorB)) return false;
			}
		}
		return true;
	}
}
