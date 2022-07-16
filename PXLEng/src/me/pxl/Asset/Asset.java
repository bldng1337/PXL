package me.pxl.Asset;

import java.nio.file.Path;
import java.util.UUID;

import me.pxl.Backend.Generic.RenderAPI;

public abstract class Asset{
	public static enum State{
		LOADED,LOADING;
	}
	public static abstract class DataAsset{
		public DataAsset(RenderAPI r) {
			this.r=r;
		}
		protected RenderAPI r;
		int references=0;
		protected State s;
		protected UUID u;
		
		protected abstract void unload();
		protected void load(Path f) {
			s=State.LOADING;
		}
		protected abstract void reload(Path f);
		protected abstract void swap();
		protected void finalizeloading() {
			s=State.LOADED;
		}
		
	}
	protected DataAsset as;
	
	public Asset(RenderAPI r) {}
	
	public Asset(DataAsset as) {
		this.as=as;
		finalizeloading();
	}
	protected <T extends Asset> T getLoadingRef(UUID u) {
		return AssetManager.getAssetManager().getLoadingRef(u);
	}
	protected void finalizeRef(Asset t) {
		AssetManager.getAssetManager().finalizeRef(t);
	}
	
	public State getState() {
		return getAs().s;
	}
	
	public int getReferences() {
		return getAs().references;
	}
	
	public UUID getUUID() {
		return getAs().u;
	}

	public void finalizeloading() {}
	protected void update() {}

	public DataAsset getAs() {
		return as;
	}
	
}
