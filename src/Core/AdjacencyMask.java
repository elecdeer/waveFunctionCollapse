package Core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Tile同士が隣接可能かどうかのMaskマップ
 */
public class AdjacencyMask{


	private boolean[] mask;

	public AdjacencyMask(int num){
		mask = new boolean[num];
		Arrays.fill(mask, true);

	}

	public boolean[] getMask(){
		return mask;
	}

	public void set(int tileID, boolean flag){
		mask[tileID] = flag;
	}

	/**
	 * このマスクと引数のマスクとでandを取ったマスクを新しく生成して返す
	 * @param adjacencyMask
	 * @return
	 */
	public AdjacencyMask createAndMask(AdjacencyMask adjacencyMask){
		AdjacencyMask andMask = new AdjacencyMask(mask.length);
		for(int i = 0; i < mask.length; i++){
			andMask.set(i, getMask()[i] & adjacencyMask.getMask()[i]);
		}
		return andMask;
	}

	/**
	 * trueの数を返す
	 * @return
	 */
	public int getEntropy(){
		int entropy = 0;
		for(int i = 0; i < getMask().length; i++){
			if(getMask()[i]) entropy++;
		}
		return entropy;
	}

	/**
	 * trueになっているタイルIDのリストを返す
	 * @return
	 */
	public List<Integer> getValidTileIDList(){
		List<Integer> list = new ArrayList<>();
		for(int i = 0; i < getMask().length; i++){
			if(getMask()[i]) list.add(i);
		}
		return list;
	}

	/**
	 * trueになっているタイルIDをランダムに1つ取り出す
	 * @return
	 */
	public int getRandomValidId(){
		List<Integer> list = getValidTileIDList();
		return list.get(Main.random.nextInt(list.size()));
	}
}
