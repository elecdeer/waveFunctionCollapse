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

	/**
	 * このマスクと引数のマスクとでandを取ったマスクを新しく生成して返す
	 * @param adjacencyMask
	 * @return
	 */
	public AdjacencyMask createAndMask(AdjacencyMask adjacencyMask){
		AdjacencyMask andMask = new AdjacencyMask(mask.length);
		for(int i = 0; i < mask.length; i++){
			andMask.set(i, mask[i] & adjacencyMask.mask[i]);
		}
		return andMask;
	}

	/**
	 * このマスクと引数のマスクとでorを取ったマスクを新しく生成して返す
	 * @param adjacencyMask
	 * @return
	 */
	public AdjacencyMask createOrMask(AdjacencyMask adjacencyMask){
		AdjacencyMask orMask = new AdjacencyMask(mask.length);
		orMask.fill(false);
		for(int i = 0; i < mask.length; i++){
			orMask.set(i, mask[i] | adjacencyMask.mask[i]);
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
			if(mask[i]) entropy++;
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
		return list.get(Main.random.nextInt(list.size()));
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
