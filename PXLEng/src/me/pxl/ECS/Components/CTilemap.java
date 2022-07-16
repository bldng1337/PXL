package me.pxl.ECS.Components;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.joml.Vector2f;

import me.pxl.Asset.Assets.ATexture;
import me.pxl.ECS.CAsset;
import me.pxl.ECS.Component;
import me.pxl.Serialize.Display;
import me.pxl.Serialize.DragDrop;
import me.pxl.Serialize.DynamicList;
import me.pxl.Serialize.Serialization;

public class CTilemap extends Component{
	
	@Serialization(MethodName = "update")
	public Vector2f numtiles=new Vector2f(20f);
	
	public int[][] tiles=new int[1][1];
	@DynamicList(Elements = "getTiles")
	public List<Tile> l=new ArrayList<Tile>();
	
	public void update() {
//		System.out.println();
//		for(int x=0;x<tiles.length;x++) {
//			for(int y=0;y<tiles[0].length;y++) {
//				System.out.print(tiles[x][y]);
//				System.out.print(" ");
//			}
//			System.out.println();
//		}
		if(numtiles.x==tiles.length&&numtiles.y==tiles[0].length)
			return;
		int[][] t=new int[(int) numtiles.x][(int) numtiles.y];
		for(int x=0;x<Math.min(numtiles.x, tiles.length);x++) {
			for(int y=0;y<Math.min(numtiles.y, tiles[0].length);y++) {
				if(l.isEmpty())
					t[x][y]=-1;
				else
					t[x][y]=tiles[x][y];
			}
		}
		tiles=t;
	}
	public int[][] getMap(){
		return tiles;
	}
	
	public Class<?>[] getTiles() {
		return new Class[] {StandardTile.class,RandomTile.class};
	}
	public abstract static class Tile{
		
		public ATexture getTexture(int x,int y) {
			return getTexture();
		}
		
		public abstract ATexture getTexture();
		
		public Tile get() {
			return this;
		}
		
		public ATexture preview() {
			return getTexture();
		}
	}
	@Display(method = "preview")
	@DragDrop(getter = "get",Name = "CTile")
	public static class StandardTile extends Tile{
		@CAsset(Name = "Texture")
		public ATexture txt;
		
		@Override
		public ATexture getTexture() {
			return txt;
		}
	}
	@Display(method = "preview")
	@DragDrop(getter = "get",Name = "CTile")
	public static class RandomTile extends Tile {
		
		@CAsset(Name = "Textures")
		@DynamicList(AssetType = ATexture.class)
		public List<ATexture> l=new ArrayList<ATexture>();
		static Random r=new Random();
		@Override
		public ATexture getTexture(int x,int y) {
			if(l.isEmpty())
				return null;
			if(l.size()==1)
				return l.get(0);
			return l.get((int) Math.round((Math.sin(x+y+x+y+x*y*x*y*123124.234234)*0.5+0.5)*(l.size()-1)));
		}
		
		@Override
		public ATexture getTexture() {
			if(l.isEmpty())
				return null;
			return l.get(0);
		}
		
	}
}
