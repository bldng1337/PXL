package me.pxl.Serialize.JSON;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

import org.joml.Vector2f;
import org.joml.Vector3f;

import com.google.gson.Gson;
import com.google.gson.stream.JsonWriter;

import me.pxl.Asset.Asset;
import me.pxl.Serialize.Serialize;
import me.pxl.Serialize.SerializeationAdapter;

public class JSONSerializeAdapter implements SerializeationAdapter {
	static Gson gson=new Gson();
	JsonWriter jw;
	StringWriter w;
	public JSONSerializeAdapter() throws IOException {
		w= new StringWriter();
		jw=gson.newJsonWriter(w);
		jw.setSerializeNulls(true);
	}
	
	public String get() {
		return w.toString();
	}

	@Override
	public Asset nextAsset(String name, Asset a,Class<?> c) throws IOException {
		if(!name.isEmpty())
			jw.name(name);
		if(a==null)
			jw.nullValue();
		else
			jw.value(a.getUUID().toString());
		return a;
	}

	@Override
	public String nextString(String name, String o)throws IOException {
		jw.name(name);
		jw.value(o);
		return o;
	}

	@Override
	public Vector2f nextVec2f(String name, Vector2f o)throws IOException {
		jw.name(name);
		jw.beginArray();
		jw.value(o.x);
		jw.value(o.y);
		jw.endArray();
		return o;
	}

	@Override
	public Vector3f nextVec3f(String name, Vector3f o) throws IOException {
		jw.name(name);
		jw.beginArray();
		jw.value(o.x);
		jw.value(o.y);
		jw.value(o.z);
		jw.endArray();
		return o;
	}

	@Override
	public boolean nextBoolean(String name, boolean o) throws IOException {
		jw.name(name);
		jw.value(o);
		return o;
	}

	@Override
	public void beginArray(String name) throws IOException {
		jw.name(name);
		jw.beginArray();
	}

	@Override
	public void exit() throws IOException {
		jw.endObject();
	}

	@Override
	public void endArray(String name) throws IOException {
		jw.endArray();
	}

	@Override
	public int nextInt(String name, int o) throws IOException {
		jw.name(name);
		jw.value(o);
		return o;
	}

	@Override
	public double nextDouble(String name, double o) throws IOException {
		jw.name(name);
		jw.value(o);
		return o;
	}

	@Override
	public float nextFloat(String name, float o) throws IOException {
		jw.name(name);
		jw.value(o);
		return o;
	}

	@Override
	public long nextLong(String name, long o) throws IOException {
		jw.name(name);
		jw.value(o);
		return o;
	}

	@Override
	public boolean hasnext() {
		return false;
	}

	@Override
	public String nextName() {
		return "";
	}
	public void begin() throws IOException {
		jw.beginObject();
	}
	@Override
	public void begin(String s) throws IOException {
		jw.name(s);
		jw.beginObject();
	}

	@Override
	public Object nextEnum(String name,Object o, Object[] enumConstants) throws IOException {
		jw.name(name);
		if(o!=null) {
			jw.value(o.toString());
		}else {
			jw.value("");
		}
		return o;
	}

	@Override
	public Type getType() {
		return Type.SERIALIZATION;
	}

	@Override
	public void skipnext() throws IOException {
		jw.nullValue();
	}

	@Override
	public <T> List<T> nextDynamicList(String name, List<T> l, @SuppressWarnings("rawtypes")Class[] c) throws IOException {
		this.beginArray(name);
		for(T t:l)
			Serialize.serializeclass(t, this);
		this.endArray(name);
		return null;
	}
	
	@Override
	public <T> List<T> nextAssetList(String name, List<T> l, Class<?> assetType) throws IOException {
		this.beginArray(name);
		for(T t:l) {
			System.out.println(t);
			this.nextAsset("", (Asset) t, Object.class);
			Serialize.serializeclass(t, this);
		} 
		
		this.endArray(name);
		return l;
	}

}
