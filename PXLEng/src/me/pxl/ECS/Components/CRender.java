package me.pxl.ECS.Components;

import me.pxl.Asset.Assets.AShader;
import me.pxl.Asset.Assets.ATexture;
import me.pxl.ECS.CAsset;
import me.pxl.ECS.Component;

public class CRender extends Component{
//	public float rotation;
	@CAsset(Name = "Texture")
	public ATexture txt;
	@CAsset(Name = "Material")
	public AShader sh;

}
