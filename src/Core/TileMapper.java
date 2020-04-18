package Core;

import processing.core.*;

import java.util.List;

import static Core.Main.P5;

public abstract class TileMapper{

	/**
	 * タイルマップの構築
	 */
	public abstract void constructTileMap();

	/**
	 * 構築したタイルマップを取り出す
	 * @return
	 */
	public abstract List<Tile> toTileList();
}
