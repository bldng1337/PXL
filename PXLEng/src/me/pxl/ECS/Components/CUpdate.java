package me.pxl.ECS.Components;

import me.pxl.ECS.Component;
import me.pxl.Utils.Utils.Call;

public class CUpdate extends Component{
	private Call c;
	
	public void setCall(Call c) {
		this.c=c;
	}
	public Call getCall() {
		return c;
	}
	
	@Override
	public boolean serializable() {
		return false;
	}
}
