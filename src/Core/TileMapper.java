package Core;

import processing.core.*;

import java.util.List;

import static Core.Main.P5;

public abstract class TileMapper{

	public abstract void constructTileMap();

	public abstract List<Tile> toTileList();
}
