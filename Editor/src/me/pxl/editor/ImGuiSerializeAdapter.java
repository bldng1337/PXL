package me.pxl.editor;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

import org.joml.Vector2f;
import org.joml.Vector3f;

import imgui.ImGui;
import imgui.flag.ImGuiDragDropFlags;
import imgui.type.ImDouble;
import imgui.type.ImInt;
import imgui.type.ImLong;
import imgui.type.ImString;
import me.pxl.Asset.Asset;
import me.pxl.Asset.AssetManager;
import me.pxl.Asset.Assets.ATexture;
import me.pxl.Serialize.Serialize;
import me.pxl.Serialize.SerializeationAdapter;

public class ImGuiSerializeAdapter implements SerializeationAdapter{
	EditorMain edit;
	int id=0;
	enum State{
		VISIBLE(true),INVIS(false),VIRTUAL(true);
		private boolean b;
		State(boolean b) {
			this.b=b;
		}
		public boolean get() {
			return b;
		}
		
	}
	Deque<State> shouldout=new ArrayDeque<>();
	public ImGuiSerializeAdapter(EditorMain e) {
		edit=e;
		shouldout.push(State.VISIBLE);
	}

	@Override
	public Asset nextAsset(String name, Asset a,Class<?> c) throws IOException {
		if(!shouldout.peek().get())
			return a;
		try {
		ImGui.text(name);
		ImGui.sameLine();
		if(a instanceof ATexture) {
			ATexture txt=(ATexture)a;
			ImGui.image(txt.getT().getID(),70,70, txt.getTextureCoords()[0], txt.getTextureCoords()[1], txt.getTextureCoords()[2], txt.getTextureCoords()[3]);
		}else {
			ImGui.image(edit.fp.file.getID(), 70, 70);
		}
		if (ImGui.beginDragDropTarget()) {
			Path payload = ImGui.acceptDragDropPayload("FE_" + AssetManager.getAssetManager().getType(c).getSimpleName());
			if (payload != null) {
				if(a!=null)
					AssetManager.getAssetManager().returnRef(a.getUUID());
				a=AssetManager.getAssetManager()
						.getRef(AssetManager.getAssetManager().registerAsset(payload));
			}
			ImGui.endDragDropTarget();
		}
		ImGui.text(AssetManager.getAssetManager().getType(c).getSimpleName());
		} catch (SecurityException e) {
			e.printStackTrace();
		}
		return a;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> List<T> nextAssetList(String name, List<T> l, Class<?> c) throws IOException {
		if(!shouldout.peek().get())
			return l;
		int i=0;
		if(ImGui.treeNode(name)) {
			for(T t:l) {
				this.nextumString("", name+"["+i+++"]:");
				Serialize.serializeclass(t, this);
				ImGui.separator();
			}
			ImGui.image(edit.fp.file.getID(), 70, 70);
			if (ImGui.beginDragDropTarget()) {
				Path payload = ImGui.acceptDragDropPayload("FE_" + AssetManager.getAssetManager().getType(c).getSimpleName());
				if (payload != null) {
					l.add((T) AssetManager.getAssetManager()
							.getRef(AssetManager.getAssetManager().registerAsset(payload)));
				}
				ImGui.endDragDropTarget();
			}
			ImGui.treePop();
			ImGui.separator();
		}
		return l;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> List<T> nextDynamicList(String name, List<T> l, @SuppressWarnings("rawtypes")Class[] c) throws IOException {
		if(!shouldout.peek().get())
			return l;
		int i=0;
		if(ImGui.treeNode(name)) {
			for(T t:l) {
				this.nextumString("", name+"["+i+++"]:");
				Serialize.serializeclass(t, this);
				ImGui.separator();
			}
			if(ImGui.button("Add"))
				ImGui.openPopup("DL"+name+l.hashCode());
			if(ImGui.beginPopup("DL"+name+l.hashCode())) {
				for(@SuppressWarnings("rawtypes")Class cls:c) {
					if(ImGui.button(cls.getSimpleName()))
						try {
							l.add((T) cls.getConstructor().newInstance());
						} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
								| InvocationTargetException | NoSuchMethodException | SecurityException e) {
							e.printStackTrace();
						}
				}
				ImGui.endPopup();
			}
			ImGui.treePop();
			ImGui.separator();
		}
		return null;
	}
	
	@Override
	public void nextDisplay(Object invoke) throws IOException {
		display(invoke);
	}

	@Override
	public String nextString(String name, String o) throws IOException {
		if(!shouldout.peek().get())
			return o;
		ImString imstr = new ImString(o, 100);
		ImGui.inputText(name, imstr);
		return imstr.get();
	}

	@Override
	public Vector2f nextVec2f(String name, Vector2f v) throws IOException {
//		System.out.println(shouldout.peek());
//		System.out.println(name);
		if(!shouldout.peek().get())
			return v;
		if (v == null)
			v = new Vector2f();
		float[] fl = new float[] { v.x, v.y };
		ImGui.dragFloat2(name, fl);
		v.set(fl);
		return v;
	}

	@Override
	public Vector3f nextVec3f(String name, Vector3f v) throws IOException {
		if(!shouldout.peek().get())
			return v;
		if (v == null)
			v = new Vector3f();
		float[] fl = new float[] { v.x, v.y, v.z };
		ImGui.dragFloat3(name, fl);
		v.set(fl);
		return v;
	}
	@Override
	public void nextumString(String string, String name) throws IOException {
		if(!shouldout.peek().get())
			return;
		ImGui.text(string+name);
	}

	@Override
	public boolean nextBoolean(String name, boolean o) throws IOException {
		if(!shouldout.peek().get()) {
			return o;
		}
		if(ImGui.checkbox(name, o))
			o=!o;
		return o;
	}
	Object drag=null;
	@Override
	public void dragDrop(String name, Object o) throws IOException {
		if(ImGui.beginDragDropSource(ImGuiDragDropFlags.SourceAllowNullID)) {
			drag=o;
			ImGui.setDragDropPayload(name, o);
			this.display(o);
            ImGui.endDragDropSource();
		}
	}
	
	@Override
	public void beginArray(String name) throws IOException {
		if(!shouldout.peek().get()) {
			shouldout.push(State.INVIS);
			return;
		}
		if(ImGui.treeNode(name+id++,name))
			shouldout.push(State.VISIBLE);
		else
			shouldout.push(State.INVIS);
		ImGui.dummy(1, 1);
		
	}

	@Override
	public void exit() throws IOException {
		if(shouldout.isEmpty())
			return;
		
		if(shouldout.peek()!=State.VIRTUAL&&shouldout.pop().get()) {
			ImGui.treePop();
			ImGui.separator();
		}
	}
	@Override
	public void begin() throws IOException {
		shouldout.push(State.VIRTUAL);
	}
	
	@Override
	public void begin(String s) throws IOException {
		if(!shouldout.peek().get()) {
			shouldout.push(State.INVIS);
			return;
		}
		if(ImGui.treeNode(s+id++,s))
			shouldout.push(State.VISIBLE);
		else
			shouldout.push(State.INVIS);
		ImGui.dummy(1, 1);
	}

	@Override
	public void endArray(String name) throws IOException {
		if(shouldout.peek()!=State.VIRTUAL&&shouldout.pop().get()) {
			ImGui.treePop();
			ImGui.separator();
		}
	}

	@Override
	public int nextInt(String name, int o) throws IOException {
		if(!shouldout.peek().get())
			return o;
		ImInt i = new ImInt(o);
		ImGui.inputInt(name, i);
		return i.get();
	}

	@Override
	public double nextDouble(String name, double o) throws IOException {
		ImDouble i = new ImDouble(o);
		ImGui.inputDouble(name, i);
		return i.get();
	}

	@Override
	public float nextFloat(String name, float o) throws IOException {
		if(!shouldout.peek().get())
			return o;
		float[] fl=new float[] {o};
		ImGui.dragFloat(name, fl);
		return fl[0];
	}
	
	@Override
	public Object nextRange(String name, Number n, float min, float max, String type) throws IOException {
		float[] fl=new float[] {n.floatValue()};
		ImGui.sliderFloat(name, fl, min, max);
		return fl[0];
	}

	@Override
	public long nextLong(String name, long o) throws IOException {
		if(!shouldout.peek().get())
			return o;
		ImLong i=new ImLong(o);
		ImGui.inputScalar(name, 0, i);//TODO: dont know
		return i.get();
	}

	@Override
	public boolean hasnext() throws IOException {
		return false;
	}

	@Override
	public String nextName() throws IOException {
		return null;
	}

	@Override
	public Object nextEnum(String name,Object o, Object[] enumConstants) {
		if(!shouldout.peek().get())
			return o;
		ImInt imi=new ImInt(-1);
		String[] str=new String[enumConstants.length];
		for(int i=0;i<str.length;i++) {
			if(enumConstants[i].equals(o))
				imi.set(i);
			str[i]=enumConstants[i].toString();
		}
		if(ImGui.combo(name, imi, str))
			return enumConstants[imi.get()];
		return o;
	}
	
	@Override
	public int nextVirtualEnum(String name, int curr, List<?> l) throws IOException {
		if(!shouldout.peek().get())
			return curr;
		ImGui.text(name);
		display(l.get(curr));
		if(ImGui.beginPopupContextItem("VirtualEnum"+curr+":"+name+l.size())) {
			if(l.get(0).getClass().getSimpleName().equals("ATexture")) {
				try {
				@SuppressWarnings("unchecked")
				List<ATexture> at=(List<ATexture>) l;
				for(int i=0;i<l.size();i++) {
					ATexture t=at.get(i);
					ImGui.imageButton(t.getT().getID(), 
							100, 100,
							t.getTextureCoords()[0],t.getTextureCoords()[1],
							t.getTextureCoords()[2],t.getTextureCoords()[3]);
						
					if(ImGui.isItemHovered()&&ImGui.isMouseReleased(0)) {
						curr=i;
					}
				}
				}catch(Exception e) {
					e.printStackTrace();
					for(int i=0;i<l.size();i++) {
						ImGui.button(l.get(i).toString());
						if(ImGui.isItemHovered()&&ImGui.isMouseReleased(0)) {
							curr=i;
						}
					}
				}
			}else {
				for(int i=0;i<l.size();i++) {
					ImGui.button(l.get(i).toString());
					if(ImGui.isItemHovered()&&ImGui.isMouseReleased(0)) {
						curr=i;
					}
				}
			}
			ImGui.endPopup();
		}
		
		return curr;
	}
	
	@Override
	public Type getType() {
		return Type.SERIALIZATION;
	}

	@Override
	public void skipnext() throws IOException {
		ImGui.dummy(10, 10);
	}

	
	
	private<T> void display(T t) {
		if(t==null) {
			ImGui.text("null");
		}else
		if(t instanceof ATexture) {
			ATexture txt=(ATexture)t;
			ImGui.image(txt.getT().getID(),100,100, txt.getTextureCoords()[0], txt.getTextureCoords()[1], txt.getTextureCoords()[2], txt.getTextureCoords()[3]);
		}else {
			ImGui.text(t.toString());
		}
	}

}
