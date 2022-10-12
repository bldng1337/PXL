package me.pxl.Asset.Assets;

import me.pxl.Backend.Generic.RenderAPI;
/**
 * Class Holding an Animation Asset
 * @author bldng
 *
 */
public abstract class AAnimation extends ATexture{

	public AAnimation(RenderAPI r) {
		super(r);
	}
	public AAnimation(DataAsset dsa) {
		super(dsa);
	}

	public abstract void setCurrTex(int currtxt);
}
