package me.pxl.Serialize;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.joml.Vector2f;
import org.joml.Vector3f;

import me.pxl.Asset.Asset;
import me.pxl.ECS.CAsset;
import me.pxl.Log.Logger;
import me.pxl.Utils.Utils;
import me.pxl.Utils.Utils.SaveBiFunction;
import me.pxl.Utils.Utils.SaveCall;

public class Serialize {
	
	
	public static void serializeclass(Object ob,SerializeationAdapter s) {
		try {
			s.begin();
			if(ob.getClass().getAnnotation(Display.class)!=null) {
				try {
					s.nextDisplay(ob.getClass().getMethod(ob.getClass().getAnnotation(Display.class).method()).invoke(ob));
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
						| NoSuchMethodException | SecurityException e) {
					e.printStackTrace();
				}
			}
			if(ob.getClass().getAnnotation(DragDrop.class) != null) {
				DragDrop d=ob.getClass().getAnnotation(DragDrop.class);
				try {
					Object o=ob.getClass().getMethod(d.getter()).invoke(ob);
					s.dragDrop(d.Name(), o);
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
						| NoSuchMethodException | SecurityException e) {
					e.printStackTrace();
				}
			}
			s.nextumString("Class", ob.getClass().getName());
			for (Field f : ob.getClass().getFields()) {
//				System.out.println(f.getName());
				try {
					f.set(ob, parseField(ob,f, s));
					if(f.isAnnotationPresent(Serialization.class)) {
						Serialization ser=f.getAnnotation(Serialization.class);
						if(!ser.MethodName().isEmpty())
							ob.getClass().getMethod(ser.MethodName()).invoke(ob);
					}
				} catch (Exception ex) {
					ex.printStackTrace();
					s.exit();
				}
			}
			s.exit();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void serializeclass(Object ob,SerializeationAdapter s,SaveCall<IOException> extra) {
		try {
			s.begin();
			s.nextumString("Class", ob.getClass().getName());
			if(ob.getClass().getAnnotation(DragDrop.class) != null) {
				DragDrop d=ob.getClass().getAnnotation(DragDrop.class);
				try {
					Object o=ob.getClass().getMethod(d.getter()).invoke(ob);
					s.dragDrop(d.Name(), o);
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
						| NoSuchMethodException | SecurityException e) {
					e.printStackTrace();
				}
			}
			for (Field f : ob.getClass().getFields()) {
				try {
					f.set(ob, parseField(ob,f, s));
					if(f.isAnnotationPresent(Serialization.class)) {
						Serialization ser=f.getAnnotation(Serialization.class);
						if(!ser.MethodName().isEmpty())
							ob.getClass().getMethod(ser.MethodName()).invoke(ob);
					}
				} catch (Exception ex) {
					ex.printStackTrace();
					s.exit();
				}
			}
			extra.call();
			s.exit();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static <T> T deserializeclass(Class<T> o,SerializeationAdapter s) {
		return deserializeclass(o,s,(a,b)->false);
	}
	
	public static <T> T deserializeclass(Class<T> o,SerializeationAdapter s,SaveBiFunction<T, String,Boolean,IOException> custom) {
		String name="";
		try {
			s.begin("Deserialize");
			String str=s.nextName();
			
//			System.out.println(str);
			if(str.equals("Display:")) {
				s.skipnext();
				str=s.nextName();
			}
			if(!str.equals("Class"))
				return null;
			T t=instantiate(s.nextString("Class",""),o);
			while(s.hasnext()) {
				name=s.nextName();
				if(custom.apply(t, name)) {
					continue;
				}
				try {
					Field f=t.getClass().getField(name);
					if(f!=null) {
						f.set(t, parseField(t, f, s));
						if(f.isAnnotationPresent(Serialization.class)) {
							Serialization ser=f.getAnnotation(Serialization.class);
							if(!ser.MethodName().isEmpty())
								t.getClass().getMethod(ser.MethodName()).invoke(t);
						}
						continue;
					}
				}catch(NoSuchFieldException e) {
					e.printStackTrace();
				}
				
				
			}
			s.exit();
			return t;
		} catch (IOException | SecurityException | IllegalArgumentException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private static <T> T instantiate(final String className, final Class<T> type){
	    try{
	        return type.cast(Utils.forName(className).getConstructor().newInstance());
	    } catch(Exception e){
	    	Logger.log(()->"Failed instantiating Class "+className+":"+e.getMessage());
	    	return null;
	    }
	}
	
	
	@SuppressWarnings("unchecked")
	private static Object parseField(Object ob,Field f,SerializeationAdapter s) throws IOException, IllegalArgumentException, IllegalAccessException, NoSuchMethodException, SecurityException, InvocationTargetException {
		Object o=f.get(ob);
//		if(ob.getClass().getAnnotation(DragDrop.class) != null) {
//			DragDrop d=ob.getClass().getAnnotation(DragDrop.class);
//			try {
//				Object o=ob.getClass().getMethod(d.getter()).invoke(ob);
//				s.dragDrop(d.Name(), o);
//			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
//					| NoSuchMethodException | SecurityException e) {
//				e.printStackTrace();
//			}
//		}else
		if(f.isAnnotationPresent(Range.class)) {
			Number n=(Number) o;
			Range r=f.getDeclaredAnnotation(Range.class);
			o=s.nextRange(f.getName(),n,r.min(),r.max(),o.getClass().getTypeName().toLowerCase());
		}else
		if (f.isAnnotationPresent(DynamicList.class)) {
//			s.beginArray(f.getName()+"[]");
			System.out.println(f.getName());
			if(f.isAnnotationPresent(CAsset.class)) {
				s.nextAssetList(f.getName(), (List<? extends Asset>) o,f.getAnnotation(DynamicList.class).AssetType());
			}else {
				s.nextDynamicList(f.getName(),(List<?>) o, (Class[]) ob.getClass().getDeclaredMethod(f.getAnnotation(DynamicList.class).Elements()).invoke(ob));
			}
//			s.endArray(f.getName()+"[]");
		}else
		if (f.isAnnotationPresent(CAsset.class)) {
			s.begin(f.getName());
				s.nextName();
				o=s.nextAsset(f.getName(),(Asset) o,f.getType());
				initObject(o,s);
			s.exit();
		}else if(f.isAnnotationPresent(VirtualEnum.class)){
			String Choices=f.getAnnotation(VirtualEnum.class).Choices();
			if(Choices.isEmpty()) {
				o=s.nextInt(f.getName(), (int) o);
			}else {
				Method m=ob.getClass().getMethod(Choices);
				List<?> l=(List<?>) m.invoke(ob);
				o=s.nextVirtualEnum(f.getName(),(int) o,l);
			}
		} else if(o!=null&&o.getClass().isEnum()){
			o=s.nextEnum(f.getName(),o,o.getClass().getEnumConstants());
		}else {
			switch (f.getGenericType().getTypeName().toLowerCase()) {
			case "java.lang.string": 
			{
				o=s.nextString(f.getName(),(String)o);
			}break;
			case "org.joml.vector2f":
			{
				o=s.nextVec2f(f.getName(),(Vector2f)o);
			}break;
			case "org.joml.vector3f": {
				o=s.nextVec3f(f.getName(),(Vector3f)o);
			}break;
			case "boolean": {
				o=s.nextBoolean(f.getName(),(boolean)o);
			}break;
			case "boolean[]": {
				s.beginArray(f.getName());
				for (int i = 0; i < ((boolean[]) o).length; i++)
					((boolean[]) o)[i] = s.nextBoolean(i+"", ((boolean[]) o)[i]);
				s.endArray(f.getName());
			}
				break;
			case "int": {
				o=s.nextInt(f.getName(),(int)o);
			}
				break;
			case "int[]": {
				s.beginArray(f.getName());
				for (int i = 0; i < ((int[]) o).length; i++)
					((int[]) o)[i] = s.nextInt(i+"", ((int[]) o)[i]);
				s.endArray(f.getName());
			}
				break;
			case "double": {
				o=s.nextDouble(f.getName(),(double) o);
			}
				break;
			case "double[]": {
				s.beginArray(f.getName());
				for (int i = 0; i < ((double[]) o).length; i++)
					((double[]) o)[i] = s.nextDouble(i+"",((double[]) o)[i]);
				s.endArray(f.getName());
			}
				break;
			case "float": {
				o=s.nextFloat(f.getName(),(float)o);
			}
				break;
			case "float[]": {
				s.beginArray(f.getName());
				for (int i = 0; i < ((float[]) o).length; i++) {
					((float[]) o)[i]=s.nextFloat(i+"", ((float[]) o)[i]);
				}
				s.endArray(f.getName());
			}
				break;
			case "long":
				o=s.nextLong(f.getName(),(long)o);
				break;
			case "long[]":
				s.beginArray(f.getName());
				for (int i = 0; i < ((long[]) o).length; i++) {
					((long[]) o)[i]=s.nextLong(i+"", ((long[]) o)[i]);
				}
				s.endArray(f.getName());
				break;
			default:
				//TODO impl
				break;
			}
			}
		return o;
	}

	private static void initObject(Object o, SerializeationAdapter s) throws IOException {
		s.nextName();
		s.begin("AssetData");
		
		if(o!=null) {
			switch(s.getType()) {
			case DESERIALIZATION:
				while(s.hasnext()) {
					String Name=s.nextName();
					try {
						Field f=o.getClass().getField(Name);
						f.set(o, parseField(o, f, s));
						if(f.isAnnotationPresent(Serialization.class)) {
							Serialization ser=f.getAnnotation(Serialization.class);
							if(!ser.MethodName().isEmpty())
								o.getClass().getMethod(ser.MethodName()).invoke(o);
						}
					} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
						e.printStackTrace();
					}
				}
				break;
			case SERIALIZATION:
				for(Field f:o.getClass().getFields()) {
					try {
						f.set(o, parseField(o, f, s));
						if(f.isAnnotationPresent(Serialization.class)) {
							Serialization ser=f.getAnnotation(Serialization.class);
							if(!ser.MethodName().isEmpty())
								o.getClass().getMethod(ser.MethodName()).invoke(o);
						}
					} catch (IllegalArgumentException | IllegalAccessException | IOException | NoSuchMethodException | SecurityException | InvocationTargetException e) {
						e.printStackTrace();
					}
				}
				break;
			}
			
		}
			
		s.exit();
		
	}
	

}
