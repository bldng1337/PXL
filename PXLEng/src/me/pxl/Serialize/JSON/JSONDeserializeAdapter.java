package me.pxl.Serialize.JSON;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.UUID;

import org.joml.Vector2f;
import org.joml.Vector3f;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

import me.pxl.Asset.Asset;
import me.pxl.Asset.AssetManager;
import me.pxl.Serialize.Serialize;
import me.pxl.Serialize.SerializeationAdapter;

public class JSONDeserializeAdapter implements SerializeationAdapter{
	JsonReader jr;
	public JSONDeserializeAdapter(String s) {
		System.out.println(s);
		jr=JSONSerializeAdapter.gson.newJsonReader(new StringReader(s));
	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> List<T> nextDynamicList(String name,List<T> l, @SuppressWarnings("rawtypes")Class[] c) throws IOException {
		this.beginArray(name);
			while(this.hasnext())
				l.add((T) Serialize.deserializeclass(Object.class, this));
		this.endArray(name);
		return l;
	}

	@SuppressWarnings("unchecked")
	public <T> List<T> nextAssetList(String name, List<T> l, Class<?> c) throws IOException {
		this.beginArray(name);
		while(this.hasnext()) {
			
			l.add((T) this.nextAsset("", null, Object.class));
		}
		this.endArray(name);
		return l;
	}
	
	@Override
	public Asset nextAsset(String name, Asset a,Class<?> c) throws IOException {
		if(jr.peek().equals(JsonToken.NULL)) {
			jr.nextNull();
			return null;
		}
		return AssetManager.getAssetManager().getRef(UUID.fromString(jr.nextString()));
	}

	@Override
	public String nextString(String name, String o) throws IOException {
		return jr.nextString();
	}

	@Override
	public Vector2f nextVec2f(String name, Vector2f o) throws IOException {
		if(o==null)
			o=new Vector2f();
		jr.beginArray();
		o.set(jr.nextDouble(), jr.nextDouble());
		jr.endArray();
		return o;
	}

	@Override
	public Vector3f nextVec3f(String name, Vector3f o) throws IOException {
		if(o==null)
			o=new Vector3f();
		jr.beginArray();
		o.set(jr.nextDouble(), jr.nextDouble(), jr.nextDouble());
		jr.endArray();
		return o;
	}

	@Override
	public boolean nextBoolean(String name, boolean o) throws IOException {
		return jr.nextBoolean();
	}

	@Override
	public void beginArray(String name) throws IOException {
		jr.beginArray();
	}

	@Override
	public void exit() throws IOException {
		while(jr.hasNext())
			jr.skipValue();
		jr.endObject();
	}

	@Override
	public void endArray(String name) throws IOException {
		jr.endArray();
	}

	@Override
	public int nextInt(String name, int o) throws IOException {
		return jr.nextInt();
	}

	@Override
	public double nextDouble(String name, double o) throws IOException {
		return jr.nextDouble();
	}

	@Override
	public float nextFloat(String name, float o) throws IOException {
		return (float) jr.nextDouble();
	}

	@Override
	public long nextLong(String name, long o) throws IOException {
		return jr.nextLong();
	}

	@Override
	public boolean hasnext() throws IOException {
		return jr.hasNext();
	}

	@Override
	public String nextName() throws IOException {
		return jr.nextName();
	}

	@Override
	public void begin(String s) throws IOException {
		jr.beginObject();
	}

	@Override
	public Object nextEnum(String n,Object o, Object[] enumConstants) throws IOException {
		String name=jr.nextString();
		for(Object ob:enumConstants)
			if(ob.toString().equals(name))
				return ob;
		return o;
	}

	@Override
	public void begin() throws IOException {
		jr.beginObject();
	}

	@Override
	public Type getType() {
		return Type.DESERIALIZATION;
	}

	@Override
	public void skipnext() throws IOException {
		jr.skipValue();
	}

}
