import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLClassLoader;
import java.nio.file.FileSystems;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Set;
import java.util.stream.Collectors;

import me.pxl.Engine;
import me.pxl.Asset.DAssetManager;
import me.pxl.ECS.Component;
import me.pxl.ECS.Entity;
import me.pxl.ECS.Entities.EUpdate;
import me.pxl.Event.EventTarget;


//init
//Date:02. Apr. 2022
//Author:BF
public class Init {
    WatchService watchService;
    URLClassLoader ucl;
    public void init(URLClassLoader ucl){
        isAssignableFrom(Entity.class,EUpdate.class);
        this.ucl=ucl;
        for(Package p:this.getClass().getClassLoader().getDefinedPackages())
            try {
                for(Class c:findAllClassesUsingClassLoader(ucl,p.getName())){
                    if(isAssignableFrom(Entity.class,c)){
                        Engine.getEngine().em.registerEntity(c);
                    }else
                    if(Component.class.isAssignableFrom(c)){
                        Engine.getEngine().em.registerComponent(c);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            
            try {
                watchService = FileSystems.getDefault().newWatchService();
                ((DAssetManager)Engine.getEngine().getAssetManager()).getPath().register(watchService, StandardWatchEventKinds.ENTRY_CREATE,StandardWatchEventKinds.ENTRY_DELETE,StandardWatchEventKinds.ENTRY_MODIFY);
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    private boolean isAssignableFrom(Class a,Class b){
        if(a.getSimpleName().equals(b.getSimpleName()))
            return true;
        Class c=b.getSuperclass();
        if(c==null)
            return false;
        do {
            if(c.getSimpleName().equals(a.getSimpleName())){
                return true;
            }
        } while((c=c.getSuperclass())!=null);
        return false;
    }

    public void destroy(){
        for(Package p:this.getClass().getClassLoader().getDefinedPackages())
            try {
                for(Class c:findAllClassesUsingClassLoader(ucl,p.getName())){
                    if(c.isAssignableFrom(Entity.class)){
                        Engine.getEngine().em.unregisterEntity(c);
                    }else
                    if(c.isAssignableFrom(Component.class)){
                        // Engine.getEngine().em.unr(c);
                        //TODO: unregister Component
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                watchService.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    @EventTarget
    public void update(EUpdate u){
        System.out.println("dd");
        WatchKey k=null;
            while((k=watchService.poll())!=null){
                k.pollEvents();
                for(WatchEvent w:k.pollEvents()){
                    System.out.println(w.kind()+" "+w.context());
                }
            }
    }

    public Set<Class> findAllClassesUsingClassLoader(URLClassLoader ucl,String packageName) throws IOException {
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(ucl.findResource(packageName.replace(".", "/")).openStream()));
            BufferedReader folder = new BufferedReader(new InputStreamReader(ucl.findResource(packageName.replace(".", "/")).openStream()))){
            Set<Class> curr=reader.lines()
            .filter(line -> line.endsWith(".class"))
            .map(line -> getClass(ucl,line, packageName))
            .collect(Collectors.toSet());
            for(String s: folder.lines()
                                .filter((a)->!a.contains("."))
                                .collect(Collectors.toList())){
                curr.addAll(findAllClassesUsingClassLoader(ucl, packageName+(packageName.isEmpty()?"":".")+s));
            }
            return curr;
        }catch(Exception e){
            System.out.println(packageName+" "+ucl);
            e.printStackTrace();
        }
        return Set.of();
    }
 
    private Class getClass(URLClassLoader ucl,String className, String packageName) {
        try {
            return ucl.loadClass(packageName + (packageName.isEmpty()?"":".")
            + className.substring(0, className.lastIndexOf('.')));
        } catch (ClassNotFoundException e) {}
        return null;
    }
}
