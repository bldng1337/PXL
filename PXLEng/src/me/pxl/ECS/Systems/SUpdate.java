package me.pxl.ECS.Systems;

import me.pxl.Engine;
import me.pxl.ECS.Component;
import me.pxl.ECS.Components.CUpdate;
import me.pxl.Log.Logger;

public class SUpdate extends System{

	@Override
	public int priority() {
		return -100;
	}

	@Override
	public boolean editor() {
		return false;
	}

	@Override
	public void init(Engine em) {}

	@Override
	public void update(Engine e) {
		for(Component c: e.em.getComponents(CUpdate.class)) {
			try {
				((CUpdate)c).getCall().call();
			}catch (Exception err) {
				Logger.log(()->"Error "+err.getMessage());
			}
		}
	}

}
