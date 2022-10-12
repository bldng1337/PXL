package me.pxl.Asset.Assets;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.joml.Vector2f;

import me.pxl.Asset.AssetManager;
import me.pxl.Asset.Assets.ATileMap.DTileMap.Tiles;
import me.pxl.Backend.Generic.RenderAPI;
import me.pxl.Backend.Generic.Texture;
import me.pxl.Serialize.Serialization;
import me.pxl.Serialize.VirtualEnum;
import me.pxl.Serialize.JSON.JSONDeserializeAdapter;
import me.pxl.Utils.IOUtils;
/**
 * Class holding a TileMap as Texture
 * @author bldng
 *
 */
public class ATileMap extends ATexture{
	public static String[] ext={"tile"};	
	public class DTileMap extends ATexture.DTexture{

		public DTileMap(RenderAPI r) {
			super(r);
		}

		@Override
		protected void unload() {
			tl.forEach((a)->{AssetManager.getAssetManager().returnRef(a.t);});
		}
		
		@Override
		protected void load(Path f) {
			s=State.LOADING;
			try {
				init(f);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		
		@Override
		protected void finalizeloading() {
			s=State.LOADED;
			for(TileTexture t:tl) {
				finalizeRef(t.t);
			}
		}

		@Override
		protected void reload(Path f) {
			List<TileTexture> flist=tl;
			try {
				init(f);
			} catch (IOException e) {
				e.printStackTrace();
			}
			flist.forEach((a)->{AssetManager.getAssetManager().returnRef(a.t);});
		}

		@Override
		protected void swap() {
			
		}
		
		class TileTexture{
			ATexture t;
			List<Tiles> tl=new ArrayList<>();
		}
		class Tiles{
			Vector2f pos,size;
		}
		List<TileTexture> tl=new ArrayList<>();
		
		private void init(Path f) throws IOException {
			JSONDeserializeAdapter jdes=new JSONDeserializeAdapter(IOUtils.stringfromFile(f));
			jdes.begin();
			jdes.nextName();
			jdes.beginArray("Textures");
			while(jdes.hasnext()) {
				jdes.begin();
					tl.add(new TileTexture());
					while(jdes.hasnext()) {
						String name=jdes.nextName().toLowerCase();
						switch(name) {
						case "txt":
							jdes.begin("txt");
								jdes.nextName();
								tl.get(tl.size()-1).t=getLoadingRef(UUID.fromString(jdes.nextString(null, null)));
								//TODO:HANDLE Asset Data
								if(jdes.nextName().equals("AssetData")) {
									jdes.begin("AssetData");
										while(jdes.hasnext())
											jdes.skipnext();
									jdes.exit();
								}
							jdes.exit();
							break;
						case "tiles":
							jdes.beginArray("Tiles");
								while(jdes.hasnext()) {
									Tiles t=new Tiles();
									tl.get(tl.size()-1).tl.add(t);
									jdes.begin();
										while(jdes.hasnext()) {
											switch(jdes.nextName()) {
											case "Class":
												jdes.skipnext();
												break;
											case "pos":
												t.pos=jdes.nextVec2f("", t.pos);
												break;
											case "size":
												t.size=jdes.nextVec2f("", t.size);
												break;
											default:
												jdes.skipnext();
												break;
											}
										}
									jdes.exit();
								}
							jdes.endArray("Tiles");
							break;
						default:
							jdes.skipnext();
							break;
						}
					}
				jdes.exit();
			}
			jdes.endArray("Textures");
			jdes.exit();
		}
		
	}
	
	public List<ATexture> getSubtxt() {
		return ((DTileMap)getAs()).tl.stream().map((a)->a.t).collect(Collectors.toList());
	}
	
	public List<ATexture> getTiles() {
		DTileMap dtm=(DTileMap) this.getAs();
		return dtm.tl.get(subtxt).tl.stream().map((a)->{
			ATexture at=new ATexture(((DTileMap) this.getAs()).tl.get(subtxt).t.getAs());
			at.f=new float[] {a.pos.x/at.getT().getWidth(),a.pos.y/at.getT().getHeight(),(a.pos.x+a.size.x)/at.getT().getWidth(),(a.pos.y+a.size.y)/at.getT().getHeight()};
			return at;}).collect(Collectors.toList());
	}
	
	public void update() {
		DTileMap dtm=(DTileMap) this.getAs();
		if(subtxt>dtm.tl.size())
			subtxt=dtm.tl.size()-1;
		if(subtxt<0)
			subtxt=0;
		if(tile>dtm.tl.get(subtxt).tl.size())
			tile=dtm.tl.get(subtxt).tl.size()-1;
		if(tile<0)
			tile=0;
		Tiles a=dtm.tl.get(subtxt).tl.get(tile);
		Texture txt=dtm.tl.get(subtxt).t.getT();
		f=new float[] {
				a.pos.x/txt.getWidth(),
				a.pos.y/txt.getHeight(),
				(a.pos.x+a.size.x)/txt.getWidth(),
				(a.pos.y+a.size.y)/txt.getHeight()};
	}
	
	@VirtualEnum(Choices = "getSubtxt")
	@Serialization(MethodName = "update")
	public int subtxt=0;
	@Serialization(MethodName = "update")
	@VirtualEnum(Choices = "getTiles")
	public int tile=0;
	
	
	@Override
	public Texture getT() {
		return ((DTileMap) this.getAs()).tl.get(Math.min(((DTileMap) this.getAs()).tl.size()-1, subtxt)).t.getT();
	}

	public ATileMap(DataAsset as) {
		super(as);
	}
	public ATileMap(RenderAPI ra) {
		super(ra);
		this.as=new DTileMap(ra);
	}
	
	

}
