package me.pxl.Asset.Assets;

import me.pxl.Backend.Generic.RenderAPI;

public abstract class AAnimation extends ATexture{

	public AAnimation(RenderAPI r) {
		super(r);
	}
	public AAnimation(DataAsset dsa) {
		super(dsa);
	}

	public abstract void setCurrTex(int currtxt);
}
