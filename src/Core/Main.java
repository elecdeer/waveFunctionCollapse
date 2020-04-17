package Core;

import processing.core.*;
import processing.opengl.PGraphicsOpenGL;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main extends PApplet{
	public static PApplet P5;

	public static Random random = new Random();

	@Override
	public void settings(){
		P5 = this;

		size(800, 800, P2D);
	}



	@Override
	public void setup(){
		scale(4);

		((PGraphicsOpenGL) g).textureSampling(2);

		background(0);

		setupTileMap("resources/Mazelike.png", 3);
	}

	private void setupTileMap(String imgName, int tileSize){
		System.out.println("setupTileMap");
		PImage sourceImg = loadImage(imgName);

		List<Tile> tileList = new ArrayList<>();

		for(int y = 0; y < sourceImg.height - 2; y++){
			for(int x = 0; x < sourceImg.width - 2; x++){
				Tile tile = new Tile(sourceImg, tileList.size(), tileSize, x, y);
				tileList.add(tile);

				tile.draw(x*(tileSize+1), y*(tileSize+1));
			}
		}

		tileList.forEach(tile -> tile.setUpAdjacency(tileList));

//		System.out.println(tileList.get(0).getAdjacency(3).getValidTileIDList());

//		int x = 0;
//		for(int i : tileList.get(0).getAdjacency(3).getValidTileIDList()){
//			tileList.get(i).draw(5 + (x++)*5, 100);
//		}

		//		List<AdjacencyMask> maskList
	}

	@Override
	public void draw(){

	}

	public static void main(String[] args){
		PApplet.main("Core.Main");
	}
}

