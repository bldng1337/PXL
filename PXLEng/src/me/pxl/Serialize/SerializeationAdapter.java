package me.pxl.Serialize;

import java.io.IOException;
import java.util.List;

import org.joml.Vector2f;
import org.joml.Vector3f;

import me.pxl.Asset.Asset;

public interface SerializeationAdapter {
	enum Type{
		SERIALIZATION,DESERIALIZATION;
	}
	abstract Type getType();
	default <T> List<T> nextAssetList(String name,List<T> l, Class<?> assetType) throws IOException {
		return nextDynamicList(name, l, new Class[] {});
	}
	
	abstract <T> List<T> nextDynamicList(String name,List<T> l,@SuppressWarnings("rawtypes")Class[] c) throws IOException;
	
	abstract void skipnext() throws IOException;
	
	
	default void dragDrop(String name,Object o) throws IOException{}

	abstract Asset nextAsset(String name, Asset a,Class<?> c)throws IOException;

	abstract String nextString(String name, String o)throws IOException;

	abstract Vector2f nextVec2f(String name, Vector2f o)throws IOException;

	abstract Vector3f nextVec3f(String name, Vector3f o)throws IOException;

	abstract boolean nextBoolean(String name, boolean o)throws IOException;

	abstract void beginArray(String name)throws IOException;

	abstract void exit()throws IOException;

	abstract void endArray(String name)throws IOException;

	abstract int nextInt(String name, int o)throws IOException;

	abstract double nextDouble(String name,double o)throws IOException;

	abstract float nextFloat(String name, float o)throws IOException;

	abstract long nextLong(String name, long o)throws IOException;
	
	
	abstract void begin() throws IOException;
	
	abstract boolean hasnext()throws IOException;

	abstract String nextName()throws IOException;

	abstract void begin(String s) throws IOException;
	default int nextVirtualEnum(String name,int curr,List<?> l) throws IOException{
		return nextInt(name,curr);
	}

	default void nextumString(String string, String name) throws IOException {
		nextString(string, name);
	}

	abstract Object nextEnum(String name,Object o,Object[] enumConstants) throws IOException;

	default Object nextRange(String name, Number n, float min, float max,String type) throws IOException {
		switch(type) {
		case "java.lang.int":
		case "int":
			return nextInt(name, n.intValue());
		case "java.lang.float":
		case "float":
			return nextFloat(name, n.floatValue());
		case "java.lang.double":
		case "double":
			return nextDouble(name, n.doubleValue());
		case "java.lang.long":
		case "long":
			return nextLong(name, n.longValue());
		}
		return n;
	}

	default void nextDisplay(Object invoke) throws IOException {
		nextumString("Display:", invoke==null?"null":invoke.toString());
	}

	

}
