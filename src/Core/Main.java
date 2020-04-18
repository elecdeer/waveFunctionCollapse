package Core;

import processing.core.*;
import processing.opengl.PGraphicsOpenGL;

import java.util.*;
import java.util.stream.Collectors;

public class Main extends PApplet{

	public static final int TILE_SIZE = 3;
	public static final int MASS_NUM = 100;
	public static PApplet P5;

	public static Random random = new Random();

	//チューニングできそうなところ
	//マスク取る部分をbit演算?

	@Override
	public void settings(){
		P5 = this;

		size(600, 600, P2D);
	}

	private List<Tile> tileList;

	@Override
	public void setup(){
//		random.
		scale(6);

		((PGraphicsOpenGL) g).textureSampling(2);

		background(20);

		setupTileMap("resources/Mazelike.png", TILE_SIZE);

		frameRate(1000);

	}

	private void setupTileMap(String imgName, int tileSize){
		System.out.println("setupTileMap");
		PImage sourceImg = loadImage(imgName);

		image(sourceImg, 0, 0);

		BasicTileMapper tileMapper = new BasicTileMapper(sourceImg, tileSize);

		tileMapper.ignoreSamePattern(false);
		tileMapper.enableHorizonFlip();
		tileMapper.enableVerticalFlip();
		tileMapper.enableRotation();

		tileMapper.constructTileMap();

		tileList = tileMapper.toTileList();


		setupGenerate();

		System.out.printf("tileNum: %d\n", tileList.size());
	}

	private Mass[][] massGrid;
	private List<Mass> massList;



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

		scale(6);

		if(step == 0){
			delay(1000);
		}
		if(step < MASS_NUM*MASS_NUM){
			generateStep();
		}else{

		}

		if(step % 20 == 0){
			background(20);
			for(int x = 0; x < MASS_NUM; x++){
				for(int y = 0; y < MASS_NUM; y++){
					massGrid[x][y].draw(x, y, TILE_SIZE);
				}
			}
		}



	}


	private int step = 0;
	private int propagationCount = 0;

	private LinkedList<PropagationTask> taskQueue;

	public void generateStep(){
		System.out.printf("### %d generateStep\n", step++);


		//最小エントロピーなマスの算出
		int minEntropy = Integer.MAX_VALUE;

		for(Mass mass : massList){
			minEntropy = min(minEntropy, mass.getEntropy());
		}

		int finalMinEntropy = minEntropy;
		List<Mass> selections = massList
				.stream()
				.filter(mass -> mass.getEntropy() == finalMinEntropy)
				.collect(Collectors.toList());

		//ランダムに一つ選ぶ
		Mass selectedMass = selections.get(random.nextInt(selections.size()));


		//そのマスのタイルを確定
		selectedMass.collapseWave(tileList);

		massList.remove(selectedMass);

		System.out.printf("selectedMass (%d,%d) -> %d\n", selectedMass.getMassX(), selectedMass.getMassY(), selectedMass.getCollapsedTile().id);

//		propagation(selectedMass.getMassX(), selectedMass.getMassY(), selectedMass.getWave());

		propagationCount = 0;

		//周囲に伝搬
		int x = selectedMass.getMassX();
		int y = selectedMass.getMassY();

		taskQueue = new LinkedList<>();

		offerTask(x, y - 1, selectedMass.getCollapsedTile().getAdjacency(0));
		offerTask(x  , y+1, selectedMass.getCollapsedTile().getAdjacency(1));
		offerTask(x-1, y,   selectedMass.getCollapsedTile().getAdjacency(2));
		offerTask(x+1, y,   selectedMass.getCollapsedTile().getAdjacency(3));

		while(! taskQueue.isEmpty()){
			PropagationTask task = taskQueue.poll();
			propagation(task.x, task.y, task.wave);
		}

		System.out.println("propagationCount = " + propagationCount);

		//Entropyの再計算
		for(Mass mass : massList){
			mass.calcEntropy();
		}
	}

	private void offerTask(int x, int y, AdjacencyMask wave){
		for(PropagationTask task : taskQueue){
			//すでに同じマスのタスクがある
			if(task.isSamePosTask(x, y)){
				task.compositeTask(wave);
				return;
			}
		}

		taskQueue.offer(new PropagationTask(x, y, wave));
	}

	private class PropagationTask{
		public final int x;
		public final int y;
		public AdjacencyMask wave;

		public PropagationTask(int x, int y, AdjacencyMask wave){
			this.x = x;
			this.y = y;
			this.wave = wave;
		}

		public boolean isSamePosTask(int posX, int posY){
			return x == posX && y == posY;
		}

		public void compositeTask(AdjacencyMask compositeWave){
			this.wave = AdjacencyMask.and(wave, compositeWave);
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
			offerTask(x  , y-1, mass.composePropagationWave(0, tileList));
			offerTask(x  , y+1, mass.composePropagationWave(1, tileList));
			offerTask(x-1, y,   mass.composePropagationWave(2, tileList));
			offerTask(x+1, y,   mass.composePropagationWave(3, tileList));
		}
	}





	public static void main(String[] args){
		PApplet.main("Core.Main");
	}
}

