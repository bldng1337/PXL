package me.pxl.ECS;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.joml.Vector3f;

import me.pxl.Asset.Asset;
import me.pxl.Asset.AssetManager;
import me.pxl.Log.Logger;
import me.pxl.Serialize.Serialize;
import me.pxl.Serialize.JSON.JSONDeserializeAdapter;
import me.pxl.Serialize.JSON.JSONSerializeAdapter;
import me.pxl.Utils.Utils;

public class EntityManager {
//	Gson gson=new Gson();
	@SuppressWarnings("rawtypes")
	HashMap<Class, List<Component>> componentmap=new HashMap<>();
	List<Entity> entities=new ArrayList<>();
	Entity player;
	Entity controller;
	Vector3f transform;
	public EntityManager() {
		transform=new Vector3f(0);
	}
	
	public List<Entity> getEntities(String Name) {
		return this.entities.stream().filter(a->a.getName().equals(Name)).collect(Collectors.toList());
	}
	
	public void translate(float f,float g) {
		transform.set(f, g, transform.z);
	}
	
	public void ltranslate(int dx,int dy) {
		transform.add(dx, dy, 0);
	}
	@SuppressWarnings("rawtypes")
	ArrayList<Class> elist=new ArrayList<>();
	
	@SuppressWarnings("rawtypes")
	public ArrayList<Class> getElist() {
		return elist;
	}

	public void registerEntity(@SuppressWarnings("rawtypes") Class c) {
		if(!elist.contains(c))
			elist.add(c);
	}
	
	public void unregisterEntity(@SuppressWarnings("rawtypes") Class c) {
		elist.remove(c);
		//TODO: remove not existing entities
	}

	
	@SuppressWarnings("rawtypes")
	List<Class> cmplist=new ArrayList<>();
	
	//TODO: Build registering api something dont know
	public <T extends Component> void registerComponent(Class<T> c) {
		if(!cmplist.contains(c))
			cmplist.add(c);
	}
	
	public <T extends Component> T attachComponent(Entity e,T c) {
		if(!componentmap.containsKey(c.getClass()))
			componentmap.put(c.getClass(), new ArrayList<Component>());
		disattachComponent(c);
		componentmap.get(c.getClass()).add(c);
		e.complist.add(c);
		c.e=e;
		c.init();
		return c;
	}
	
	public void disattachComponent(Component c) {
		if(c.e==null)
			return;
		c.e.complist.remove(c);
		c.e=null;
		c.onDestroy();
		componentmap.get(c.getClass()).remove(c);
	}
	
	
	public <T extends Component> T attachComponent(Entity e,Class<T> c) {
		T t=(T) instanceComponent(c);
		attachComponent(e,t);
		return t;
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends Component> T instanceComponent(Class<T> e) {
		try {
			return (T) e.getConstructors()[0].newInstance();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| SecurityException e1) {
			e1.printStackTrace();
			System.exit(1);
		}
		return null;
	}
	
	public Entity instanceEntity(Class<Entity> e) {
		try {
			Entity ie=(Entity) e.getConstructors()[0].newInstance();
			entities.add(ie);
			ie.onSetup();
			return ie;
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| SecurityException e1) {
			e1.printStackTrace();
			System.exit(1);
		}
		return null;
	}
	public Entity loadEntity(JSONDeserializeAdapter jr, Vector3f offset) throws IOException {
		Entity entt=Serialize.deserializeclass(Entity.class, jr,(ent,name)->{
			if(name.equals("Component")) {
				ent.pos.add(offset);
				try {
					jr.beginArray("Component");
						while(jr.hasnext())
							this.attachComponent(ent, Serialize.deserializeclass(Component.class, jr));
					jr.endArray("Component");
				} catch (IOException e) {
					e.printStackTrace();
				}
				return true;
			}
			return false;
		});
		entt.onSetup();
		entities.add(entt);
		return entt;
	}
	
	public void despawnEntity(Entity e) {
		entities.remove(e);
		for(Component c:e.complist) {
			for(Field f:c.getClass().getFields()) {
				if(f.isAnnotationPresent(CAsset.class)) {
					try {
						if(f.get(c)!=null)
							AssetManager.getAssetManager().returnRef(((Asset)f.get(c)).getUUID());
					} catch (IllegalArgumentException | IllegalAccessException ex) {
						ex.printStackTrace();
					}
				}
			}
			c.onDestroy();
			this.componentmap.get(c.getClass()).remove(c);
		}
	}
	
	public String saveScene() throws IOException {
		JSONSerializeAdapter js=new JSONSerializeAdapter();
		js.begin();
		js.nextVec3f("transform", transform);
		js.beginArray("Entities");
		for(Entity e:this.entities)
			saveEntity(e,js);
		js.endArray("Entities");
		js.exit();
		return js.get();
	}
	@SuppressWarnings("rawtypes")
	public void loadScene(String scene,boolean overwrite,Vector3f offset) throws IOException {
		HashMap<Class, List<Component>> oldmap=componentmap;
		HashMap<Class, List<Component>> map=overwrite?new HashMap<>():componentmap;
		List<Entity> ent=overwrite?new ArrayList<>():entities;
		componentmap=map;
		entities=ent;
		JSONDeserializeAdapter jdes=new JSONDeserializeAdapter(scene);
		jdes.begin();
		while(jdes.hasnext())
			switch(jdes.nextName()) {
			case "transform":
				if(overwrite)
					transform=jdes.nextVec3f("transform", transform);
				else
					jdes.nextVec3f("transform", null);
				break;
			case "Entities":
				jdes.beginArray("Entities");
					while(jdes.hasnext()) {
						loadEntity(jdes,offset);
					}
				jdes.endArray("Entities");
				break;
			}
		jdes.exit();
		if(overwrite) {
			for(Entry<Class, List<Component>> s:oldmap.entrySet()) {
				for(Component c:s.getValue())
				for(Field f:c.getClass().getFields()) {
					if(f.isAnnotationPresent(CAsset.class)) {
						try {
							if(f.get(c)!=null)
							AssetManager.getAssetManager().returnRef(((Asset)f.get(c)).getUUID());
						} catch (IllegalArgumentException | IllegalAccessException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
		
	}
	
	public Entity loadEntity(String s) throws IOException {
		JSONDeserializeAdapter jdes=new JSONDeserializeAdapter(s);
		return loadEntity(jdes,new Vector3f());
	}
	
	public <T> T instantiate(final String className, final Class<T> type){
	    try{
	        return type.cast(Utils.forName(className).getConstructors()[0].newInstance());
	    } catch(Exception e){
	    	Logger.log(()->"Failed instantiating Class "+className+":"+e.getMessage());
	    	return null;
	    }
	}
	
	
	
	public void saveEntity(Entity e,JSONSerializeAdapter js) throws IOException {
			Serialize.serializeclass(e, js,()->{
				try {
					js.beginArray("Component");
					for(Component c:e.complist) {
						if(!c.serializable())
							continue;
						Serialize.serializeclass(c, js);
					}
					js.endArray("Component");
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			});
	}
	
	public String saveEntity(Entity e) throws IOException {
		JSONSerializeAdapter js=new JSONSerializeAdapter();
		saveEntity(e, js);
		return js.get();
	}
	
	public List<Component> getComponents(@SuppressWarnings("rawtypes")Class component) {
		if(!componentmap.containsKey(component))
			componentmap.put(component, new ArrayList<Component>());
		return componentmap.get(component);
	}

	public List<Entity> getEntities() {
		return this.entities;
	}
	@SuppressWarnings("rawtypes")
	public void clear() {
		for(Entry<Class, List<Component>> s: componentmap.entrySet()) {
			for(Component c:s.getValue())
				for(Field f:c.getClass().getFields()) {
					if(f.isAnnotationPresent(CAsset.class)) {
						System.out.println(f.getName());
						try {
							if(f.get(c)!=null)
							AssetManager.getAssetManager().returnRef(((Asset)f.get(c)).getUUID());
						} catch (IllegalArgumentException | IllegalAccessException e) {
							e.printStackTrace();
						}
					}
				}
		}
		for(Entity e:entities) {
			for(Field f:e.getClass().getFields()) {
				if(f.isAnnotationPresent(CAsset.class)) {
					try {
						Asset a=(Asset) f.get(e);
						if(a!=null)
							AssetManager.getAssetManager().returnRef(a);
					} catch (IllegalArgumentException | IllegalAccessException e1) {
						e1.printStackTrace();
					}
					
				}
			}
		}
		entities.clear();
		componentmap.clear();
	}

	public Vector3f getTranslation() {
		return transform;
	}
	@SuppressWarnings("rawtypes")
	public List<Class> getRegisteredComponents() {
		return cmplist;
	}
	
	
}
