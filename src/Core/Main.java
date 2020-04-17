package Core;

import processing.core.*;
import processing.opengl.PGraphicsOpenGL;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Main extends PApplet{
	public static final int TILE_SIZE = 3;
	public static PApplet P5;

	public static Random random = new Random(5555);

	//チューニングできそうなところ
	//マスク取る部分をbit演算?
	//composePropagationWaveのvalidIDリスト取る部分で、パターンが重複しているのは省く

	@Override
	public void settings(){
		P5 = this;

		size(600, 600, P2D);
	}

	private List<Tile> tileList;

	@Override
	public void setup(){
//		random.
		scale(12);

		((PGraphicsOpenGL) g).textureSampling(2);

		background(0);

		setupTileMap("resources/MoreFlowers.png", TILE_SIZE);

		frameRate(3000);

	}

	private void setupTileMap(String imgName, int tileSize){
		System.out.println("setupTileMap");
		PImage sourceImg = loadImage(imgName);

		tileList = new ArrayList<>();

		//元画像からのタイル切り出し
		for(int y = 0; y < sourceImg.height - 2; y++){
			for(int x = 0; x < sourceImg.width - 2; x++){
				Tile tile = new Tile(sourceImg, tileList.size(), tileSize, x, y);
				tileList.add(tile);

//				tile.draw(x*(tileSize+1), y*(tileSize+1));
			}
		}

		//隣接関係の構築
		tileList.forEach(tile -> tile.setUpAdjacency(tileList));

//		System.out.println(tileList.get(0).getAdjacency(3).getValidTileIDList());

//		int x = 0;
//		for(int i : tileList.get(0).getAdjacency(3).getValidTileIDList()){
//			tileList.get(i).draw(5 + (x++)*5, 100);
//		}

		//		List<AdjacencyMask> maskList

		setupGenerate();

		System.out.printf("tileNum: %d\n", tileList.size());
	}

	private Mass[][] massGrid;
	private List<Mass> massList;


	public static final int MASS_NUM = 50;

	private void setupGenerate(){
		massGrid = new Mass[MASS_NUM][MASS_NUM];
		massList = new ArrayList<>();

		for(int x = 0; x < MASS_NUM; x++){
			for(int y = 0; y < MASS_NUM; y++){
				Mass mass = new Mass(tileList.size(), x, y);
				massGrid[x][y] = mass;
				massList.add(mass);
			}
		}
	}

	@Override
	public void draw(){

		scale(12);
		background(0);
		if(step < MASS_NUM*MASS_NUM){
			generateStep();
		}else{

		}

		for(int x = 0; x < MASS_NUM; x++){
			for(int y = 0; y < MASS_NUM; y++){
				massGrid[x][y].draw(x, y, TILE_SIZE);
			}
		}

	}


	private int step = 0;
	private int propagationCount = 0;

	public void generateStep(){
		System.out.printf("### %d generateStep\n", step++);


		//エントロピー最小のマスを選ぶ
		//最小エントロピーなマスの算出
		int minEntropy = Integer.MAX_VALUE;

		List<Mass> targetMassList = massList
				.stream()
				.filter(mass -> ! mass.isCollapsed())
				.collect(Collectors.toList());

		for(Mass mass : targetMassList){
			minEntropy = min(minEntropy, mass.getEntropy());
		}

		int finalMinEntropy = minEntropy;
		List<Mass> selections = targetMassList
				.stream()
				.filter(mass -> mass.getEntropy() == finalMinEntropy)
				.collect(Collectors.toList());

		//ランダムに一つ選ぶ
		Mass selectedMass = selections.get(random.nextInt(selections.size()));


		//そのマスのタイルを確定
		selectedMass.collapseWave(tileList);

//		System.out.printf("selectedMass (%d,%d) -> %d\n", selectedMass.getMassX(), selectedMass.getMassY(), selectedMass.getCollapsedTile().id);

		//周囲に伝搬
//		propagation(selectedMass.getMassX(), selectedMass.getMassY(), selectedMass.getWave());

		propagationCount = 0;

		int x = selectedMass.getMassX();
		int y = selectedMass.getMassY();
		propagation(x  , y-1, selectedMass.getCollapsedTile().getAdjacency(0));
		propagation(x  , y+1, selectedMass.getCollapsedTile().getAdjacency(1));
		propagation(x-1, y,   selectedMass.getCollapsedTile().getAdjacency(2));
		propagation(x+1, y,   selectedMass.getCollapsedTile().getAdjacency(3));

		System.out.println("propagationCount = " + propagationCount);

		//Entropyの再計算
		for(Mass mass : targetMassList){
			mass.calcEntropy();
		}
	}

	/**
	 * 再帰的にwaveを伝搬する
	 * @param x
	 * @param y
	 * @param wave
	 */
	public void propagation(int x, int y, AdjacencyMask wave){
		propagationCount++;
		if(x < 0 || MASS_NUM <= x || y < 0 || MASS_NUM <= y){
			return;
		}

		Mass mass = massGrid[x][y];

		if(mass.isCollapsed()) return;

//		System.out.printf("propagation(%d,%d)\n", x, y);
//		System.out.println(wave.toString());



		boolean changed = mass.restrictWave(wave);
		//変化があったら周囲に伝搬
		if(changed){
			propagation(x  , y-1, mass.composePropagationWave(0, tileList));
			propagation(x  , y+1, mass.composePropagationWave(1, tileList));
			propagation(x-1, y,   mass.composePropagationWave(2, tileList));
			propagation(x+1, y,   mass.composePropagationWave(3, tileList));
		}
	}





	public static void main(String[] args){
		PApplet.main("Core.Main");
	}
}

