package ImGui.sequencer;

import java.util.List;

import imgui.ImColor;
import imgui.ImDrawList;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.ImVec2;
import imgui.ImVec4;

public class Sequencer {
	public static class Sequence{
		String Name;
		int startstamp,endstamp;
		public Sequence(String string, int i, int j) {
			Name=string;
			startstamp=i;
			endstamp=j;
		}
		int getTexturePreview(){return 0;}
	}
	private static int getColor(int i) {
		ImVec4 vec=ImGui.getStyle().getColor(i);
	    return ImColor.floatToColor(vec.x, vec.y, vec.z, vec.w);
	}
	//int where are you 
	//int zoom
	public static void beginSequence(List<Sequence> seqs,int currframe, float[] f) {
		ImGui.text("ed");
		{
			ImDrawList dw=ImGui.getWindowDrawList();
			ImVec2 canvas_pos = ImGui.getCursorScreenPos();
		    ImVec2 canvas_size = ImGui.getContentRegionAvail();
			ImGui.beginGroup();
			ImGui.dummy(canvas_size.x, 80);
			for(int i=0;i<90;i++) {
		    	final int size=20;
		    	dw.addRectFilled(canvas_pos.x+size*i, canvas_pos.y, canvas_pos.x+size+size*i, canvas_pos.y+size, getColor(i));
		    	dw.addText(canvas_pos.x+size*i+2, canvas_pos.y+size+5, 0xFF_FF_FF_FF, i+"");
		    }
			
			ImGui.endGroup();
		}
		ImGuiIO io=ImGui.getIO();
		
		final float maxzoom=0.1f;
		float minzoom=0.05f;
		float zoomlevel=f[0];
		zoomlevel+=io.getMouseWheel()/20f;
		if(zoomlevel<minzoom)
			zoomlevel=minzoom;
		
		float currpos=f[1];
		currpos=Math.max(currpos, -20);
		currpos-=ImGui.getMouseDragDeltaX();
		if(ImGui.isMouseReleased(0)) {
			currpos-=ImGui.getMouseDragDeltaX();
		}
		
	    ImGui.beginGroup();
	    ImDrawList dw=ImGui.getWindowDrawList();
		ImVec2 canvas_pos = ImGui.getCursorScreenPos();
	    ImVec2 canvas_size = ImGui.getContentRegionAvail();
	    dw.addRectFilled(canvas_pos.x, canvas_pos.y, canvas_pos.x+canvas_size.x, canvas_pos.y+canvas_size.y, getColor(13));
	    for(float x=1080;x<2000;x+=(Math.floor(x/100)+1)*100-x) {
//	    	System.out.print(x+" ");
	    }
//	    for(float x=0;x<(canvas_size.x);x+=10*zoomlevel) {
//	    	dw.addLine(x, canvas_pos.y+20, x, canvas_pos.y+canvas_size.y, getColor(0),3f);
//	    }
	    
	    
	    {
		    float stepsize=zoomlevel*1000;
		    
		    float offset=(float) ((Math.floor(currpos/stepsize)+1)*(stepsize)-currpos);
		    float pureoffset=(float) ((Math.floor(currpos/1000)+1)*(1000)-currpos);
		    for(float x=0;x<(canvas_size.x-offset)/stepsize;x+=1) {
		    	dw.addLine(offset+x*stepsize, canvas_pos.y+20, offset+x*stepsize, canvas_pos.y+canvas_size.y, getColor(0),3f);
		    	
		    	String text=((currpos+Math.floor(pureoffset))+x*1000)+"";
		    	dw.addText( (offset+x*stepsize-(ImGui.getFont().calcTextSizeA(ImGui.getFontSize(), 1000000f, 0f, text).x/2f)), canvas_pos.y, getColor(0), text);
		    }
	    }
	    
	    if(zoomlevel>0.4f)
	    {
	    	final float hardstepsize=100;
		    float stepsize=zoomlevel*hardstepsize;
		    
		    float offset=(float) ((Math.floor(currpos/stepsize)+1)*(stepsize)-currpos);
		    float pureoffset=(float) ((Math.floor(currpos/1000)+1)*(1000)-currpos);
		    for(float x=0;x<(canvas_size.x-offset)/stepsize;x+=1) {
		    	dw.addLine(offset+x*stepsize, canvas_pos.y+20, offset+x*stepsize, canvas_pos.y+canvas_size.y, getColor(0),2f);
		    	
		    	String text=((currpos+Math.floor(pureoffset))+x*hardstepsize)+"";
		    	dw.addText( (offset+x*stepsize-(ImGui.getFont().calcTextSizeA(ImGui.getFontSize(), 1000000f, 0f, text).x/2f)), canvas_pos.y, getColor(0), text);
		    }
	    }
	    
	    if(zoomlevel>2.8f)
	    {
	    	final float hardstepsize=10;
		    float stepsize=zoomlevel*hardstepsize;
		    
		    float offset=(float) ((Math.floor(currpos/stepsize)+1)*(stepsize)-currpos);
		    float pureoffset=(float) ((Math.floor(currpos/1000)+1)*(1000)-currpos);
		    for(float x=0;x<(canvas_size.x-offset)/stepsize;x+=1) {
		    	dw.addLine(offset+x*stepsize, canvas_pos.y+20, offset+x*stepsize, canvas_pos.y+canvas_size.y, getColor(0),0.5f);
		    	
		    	String text=((currpos+Math.floor(pureoffset))+x*hardstepsize)+"";
		    	dw.addText( (offset+x*stepsize-(ImGui.getFont().calcTextSizeA(ImGui.getFontSize(), 1000000f, 0f, text).x/2f)), canvas_pos.y, getColor(0), text);
		    }
	    }
	    int delta=25;
	    int height=20;
	    for(int i=0;i<seqs.size();i++) {
	    	Sequence s=seqs.get(i);
	    	if(!(s.startstamp>currpos||
	    		s.endstamp<currpos+canvas_size.x*zoomlevel))
	    		continue;
	    	dw.addRectFilled(canvas_pos.x+(s.startstamp*zoomlevel-currpos), canvas_pos.y+delta+height*i, canvas_pos.x+(s.endstamp*zoomlevel-currpos), canvas_pos.y+delta+height*i+height, getColor(0));
	    }
	    dw.addText(canvas_pos.x, canvas_pos.y-40, 0xFF_FF_FF_FF, "zoomlevel:"+zoomlevel+" currpos:"+currpos);
	    f[0]=zoomlevel;
	    f[1]=currpos+ImGui.getMouseDragDeltaX();
	}
	public static void endSequence(float[] f) {
		ImGui.endGroup();
	}
}
