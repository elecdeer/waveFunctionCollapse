package Core;

import processing.core.*;

import java.util.List;
import java.util.stream.Collectors;

import static Core.Main.P5;

public class Mass{
	private int massX;
	private int massY;
	private AdjacencyMask wave;
	private int entropy;
	private Tile collapsedTile;

	public Mass(int tileNum, int x, int y){
		this.massX = x;
		this.massY = y;
		this.wave = new AdjacencyMask(tileNum);
		entropy = wave.getEntropy();
	}

	public int getMassX(){
		return massX;
	}

	public int getMassY(){
		return massY;
	}

	public AdjacencyMask getWave(){
		return wave;
	}

	public int getEntropy(){
		return entropy;
	}

	/**
	 * Entropyの再計算
	 */
	public void calcEntropy(){
		entropy = wave.getEntropy();
	}

	public Tile getCollapsedTile(){
		return collapsedTile;
	}

	/**
	 * waveを制約する
	 * @return 変化があればtrue
	 */
	public boolean restrictWave(AdjacencyMask restrictWave){
//		System.out.println("restrictWave");
//		System.out.println(restrictWave.toString());
//		System.out.println("wave");
//		System.out.println(wave.toString());


		AdjacencyMask nextWave = AdjacencyMask.and(wave, restrictWave);

//		System.out.println("andWave");
//		System.out.println(nextWave.toString());

		if(wave.isSameMask(nextWave)){
			return false;
		}else{
			wave = nextWave;
			return true;
		}
	}

	/**
	 * waveを収束させ、一つのタイルに確定する
	 * @param tileList
	 */
	public void collapseWave(List<Tile> tileList){
		collapsedTile = tileList.get(wave.getRandomValidId());
	}

	/**
	 * dir方向に隣接可能なwaveを取得
	 * @param dir
	 */
	public AdjacencyMask composePropagationWave(int dir, List<Tile> tileList){
		AdjacencyMask mask = new AdjacencyMask(wave.getMask().length);
		mask.fill(false);

		for(int tileID : wave.getValidTileIDList()){
			mask = AdjacencyMask.or(mask, tileList.get(tileID).getAdjacency(dir));
		}

		return mask;

//		return AdjacencyMask.or(wave.getValidTileIDList().stream().map(id -> tileList.get(id).getAdjacency(dir)).collect(Collectors.toList()));
	}


	public void draw(int x, int y, int tileSize){
		if(isCollapsed()){
			collapsedTile.draw(x, y);
		}
	}

	/**
	 * 確定済みかどうか
	 * @return
	 */
	public boolean isCollapsed(){
		return collapsedTile != null;
	}

}
