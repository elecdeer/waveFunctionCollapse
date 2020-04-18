package Core;

import processing.core.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import static Core.Main.P5;

/**
 * Tile同士が隣接可能かどうかのMaskマップ
 */
public class AdjacencyMask{
	private boolean[] mask;
	private int[] weight;

	public AdjacencyMask(int num){
		mask = new boolean[num];
		weight = new int[num];
		Arrays.fill(weight, 1);

		fill(true);
	}

	public void fill(boolean b){
		Arrays.fill(mask, b);
	}

	public boolean[] getMask(){
		return mask;
	}

	public void set(int tileID, boolean flag){
		mask[tileID] = flag;
	}

	public void set(int tileID, boolean flag, int weightVal){
		set(tileID, flag);
		weight[tileID] = weightVal;
	}

	public static AdjacencyMask and(AdjacencyMask maskA, AdjacencyMask maskB){
		int length = maskA.mask.length;

		AdjacencyMask andMask = new AdjacencyMask(length);
		andMask.fill(true);
		for(int i = 0; i < length; i++){
			andMask.set(i, maskA.mask[i] & maskB.mask[i], Math.max(maskA.weight[i], maskB.weight[i]));
		}
		return andMask;
	}

	public static AdjacencyMask or(AdjacencyMask maskA, AdjacencyMask maskB){
		int length = maskA.mask.length;

		AdjacencyMask orMask = new AdjacencyMask(length);
		orMask.fill(false);
		for(int i = 0; i < length; i++){
			orMask.set(i, maskA.mask[i] | maskB.mask[i], Math.max(maskA.weight[i], maskB.weight[i]));
		}
		return orMask;
	}

	public static AdjacencyMask or(List<AdjacencyMask> maskList){

		AdjacencyMask orMask = maskList.get(0);

//		for(int i = 1; i < maskList.size(); i++){
//			orMask = or(orMask, maskList.get(i));
//		}

		orMask.fill(false);
		for(int i = 0; i < orMask.mask.length; i++){
			for(AdjacencyMask adjacencyMask : maskList){
				if(adjacencyMask.mask[i]){
					orMask.set(i, true, adjacencyMask.weight[i]);
					continue;
				}
			}
		}
		return orMask;
	}



	/**
	 * trueの数を返す
	 * @return
	 */
	public int getEntropy(){
		int entropy = 0;
		for(int i = 0; i < mask.length; i++){
			if(mask[i]) entropy += weight[i];
		}
		return entropy;
	}

	/**
	 * trueになっているタイルIDのリストを返す
	 * @return
	 */
	public List<Integer> getValidTileIDList(){
		List<Integer> list = new ArrayList<>();
		for(int i = 0; i < mask.length; i++){
			if(mask[i]) list.add(i);
		}
		return list;
	}

	/**
	 * trueになっているタイルIDをランダムに1つ取り出す
	 * @return
	 */
	public int getRandomValidId(){
		List<Integer> list = getValidTileIDList();

//		System.out.println(list);

		//重み付けランダム抽出

		int sumWeight = 0;
		for(int id : list){
			sumWeight += weight[id];
		}

		float rand = Main.random.nextFloat() * sumWeight;
		int area = 0;

		for(int id : list){
			area += weight[id];
			if(area >= rand){
				return id;
			}
		}

		return -1;

//		List<Integer> list = getValidTileIDList();
//		return list.get(Main.random.nextInt(list.size()));
	}

	/**
	 * 同一のマスクかどうか
	 * @param adjacencyMask
	 * @return
	 */
	public boolean isSameMask(AdjacencyMask adjacencyMask){
		for(int i = 0; i < mask.length; i++){
			if(mask[i] != adjacencyMask.mask[i]) return false;
		}
		return true;
	}


	public String toString(){
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < mask.length; i++){
			sb.append(mask[i] ? 'o' : 'x');
		}
		return sb.toString();
	}


}
