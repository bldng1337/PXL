package me.pxl.Asset;

import com.google.gson.Gson;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.function.Consumer;
import java.util.function.Function;
import me.pxl.Asset.Asset.DataAsset;
import me.pxl.Asset.Asset.State;
import me.pxl.Backend.Generic.RenderAPI;
import me.pxl.Event.EventTarget;
import me.pxl.Event.Events.EPostRender;
import me.pxl.Utils.IOUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryUtil;

public abstract class AssetManager {

    abstract class AssetJob {

        abstract void run();

        abstract UUID getRefAsset();
    }

    class ReloadJob extends AssetJob {

        Asset a;
        UUID u;

        public ReloadJob(Asset a) {
            this.a = a;
            this.u = a.getUUID();
        }

        UUID getRefAsset() {
            return u;
        }

        void run() {
            String path = getrefs(m -> m.get(u));
            a.getAs().reload(Paths.get(path));
            System.out.println("Reloaded: " + path);
            getswap(a -> a.add(u));
        }
    }

    class LoadJob extends AssetJob {

        UUID u;

        public LoadJob(UUID u) {
            this.u = u;
        }

        UUID getRefAsset() {
            return u;
        }

        void run() {
            loadAsset(u);
        }
    }

    volatile BlockingQueue<AssetJob> jobqueue = new ArrayBlockingQueue<>(200);
    //Finalize Queue
    volatile Queue<UUID> finalq = new ArrayBlockingQueue<>(100); //

    //	BlockingQueue<Tuple<Asset,UUID>> aqueue=new ArrayBlockingQueue<>(100);//Reload Queue
    //	BlockingQueue<UUID> queue=new ArrayBlockingQueue<>(100);//Load queue

    volatile HashMap<UUID, Asset> assets = new HashMap<>();
    volatile HashMap<UUID, String> softref = new HashMap<>();

    protected Path p;
    Thread StreamThread;
    RenderAPI r;
    Path passets;

    public static final String CONFIG_FILE_ENDING = "PXL";
    Gson gson = new Gson();
    static AssetManager athis;

    public static AssetManager getAssetManager() {
        return athis;
    }

    public AssetManager(long mainwin, Path p, RenderAPI r) {
        this.r = r;
        this.p = p;
        athis = this;

        //StreamThread
        long win = GLFW.glfwCreateWindow(1, 1, "", MemoryUtil.NULL, mainwin);
        if (win == MemoryUtil.NULL) throw new RuntimeException(
            "Failed to create the GLFW window"
        );
        StreamJob sj = new StreamJob(win);
        StreamThread = new Thread(sj);
        StreamThread.setName("Stream-Thread");
        StreamThread.start();

        //Make AssetPath
        passets = p.resolve("Assets." + CONFIG_FILE_ENDING); //TODO: Why is this in the Main AssetManager?
    }

    public void softLoad(UUID... ul) {
        for (UUID u : ul) jobqueue.add(new LoadJob(u));
    }

    protected synchronized <T> T getswap(Function<Queue<UUID>, T> a) {
        return a.apply(finalq);
    }

    protected synchronized <T> T getassets(
        Function<HashMap<UUID, Asset>, T> a
    ) {
        return a.apply(assets);
    }

    protected synchronized void useassets(Consumer<HashMap<UUID, Asset>> a) {
        a.accept(assets);
    }

    protected synchronized <T> T getrefs(Function<HashMap<UUID, String>, T> a) {
        return a.apply(softref);
    }

    protected synchronized void userefs(Consumer<HashMap<UUID, String>> a) {
        a.accept(softref);
    }

    @SuppressWarnings("unchecked")
    public void registerAssetType(@SuppressWarnings("rawtypes") Class c) {
        atypes.add(c);
    }

    private List<Class<Asset>> atypes = new ArrayList<>();

    @SuppressWarnings("unchecked")
    public Class<Asset> getType(Path p) {
        for (Class<Asset> c : atypes) {
            try {
                String[] as = (String[]) c.getField("ext").get(null);
                for (String s : as) {
                    if (p.getFileName().toString().endsWith(s)) {
                        @SuppressWarnings("rawtypes")
                        Class e = c;
                        while (
                            !e
                                .getSuperclass()
                                .getSimpleName()
                                .equals(Asset.class.getSimpleName())
                        ) e = e.getSuperclass();
                        return e;
                    }
                }
            } catch (
                NullPointerException
                | SecurityException
                | IllegalArgumentException
                | IllegalAccessException
                | NoSuchFieldException e
            ) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public Class<?> getType(Class<?> e) {
        while (
            !e
                .getSuperclass()
                .getSimpleName()
                .equals(Asset.class.getSimpleName())
        ) e = e.getSuperclass();
        return e;
    }

    private Class<Asset> getAssetType(Path p) {
        for (Class<Asset> c : atypes) {
            try {
                String[] as = (String[]) c.getField("ext").get(null);
                for (String s : as) {
                    if (p.getFileName().toString().endsWith(s)) {
                        return c;
                    }
                }
            } catch (
                NullPointerException
                | SecurityException
                | IllegalArgumentException
                | IllegalAccessException
                | NoSuchFieldException e
            ) {
                e.printStackTrace();
            }
        }
        return null;
    }

    protected Asset search(UUID u) {
        String path = getrefs(m -> m.get(u));
        Path p = Paths.get(path);
        if (p == null) {
            System.out.println("Couldnt find Asset " + u);
            System.exit(1);
        }
        Class<Asset> c = getAssetType(p);
        if (c == null) {
            System.out.println("Couldnt find AssetType for " + p);
            return null;
        }
        Asset a = null;
        try {
            a = (Asset) c.getConstructor(RenderAPI.class).newInstance(r);
        } catch (
            InstantiationException
            | IllegalAccessException
            | IllegalArgumentException
            | InvocationTargetException
            | SecurityException
            | NoSuchMethodException e
        ) {
            e.printStackTrace();
            return null;
        }
        a.getAs().load(p);
        return a;
    }

    public void reload(UUID u) {
        if (isAssetLoaded(u)) {
            //			if(!aqueue.contains(t)) {
            //TODO: reimplement contains? can lead to more reloads of an asset as is needed
            System.out.println("Reloading: " + getrefs(a -> a.get(u)));
            jobqueue.add(new ReloadJob(getassets(m -> m.get(u))));
            //			}
            return;
        }
    }

    protected synchronized void loadAsset(UUID u) {
        if (getassets(map -> map.containsKey(u))) return;
        Asset a = search(u);
        if (a != null) {
            useassets(map -> {
                map.put(u, a);
            });
            System.out.println("Streamed " + u);
            return;
        }
        System.out.println("Couldnt find Asset Type: " + u);
    }

    //TODO:Dont like it that registerAsset is in the Assetmanager used in production
    public UUID registerAsset(Path p) {
        Path rp = this.p.resolve(p).normalize();
        if (softref.containsValue(rp.toString())) for (Entry<
            UUID,
            String
        > a : getRefs())
            if (a.getValue().equals(rp.toString())) return a.getKey();
        UUID u;
        while (softref.containsKey(u = UUID.randomUUID()));
        if (!softref.containsValue(rp.toString())) {
            softref.put(u, rp.toString());
            IOUtils.write(gson.toJson(softref), passets);
        }
        return u;
    }

    public Set<Entry<UUID, Asset>> getAssets() {
        return getassets(map -> map.entrySet());
    }

    public Set<Entry<UUID, String>> getRefs() {
        return this.getrefs(m -> m.entrySet());
    }

    @SuppressWarnings("unchecked")
    protected <T extends Asset> T getLoadingRef(UUID u) {
        if (!isAssetLoaded(u)) loadAsset(u);
        Asset t = getassets(map -> map.get(u));
        t.getAs().u = u;
        t.getAs().references++;
        try {
            return (T) t
                .getClass()
                .getConstructor(DataAsset.class)
                .newInstance((DataAsset) t.getAs());
        } catch (
            InstantiationException
            | IllegalAccessException
            | IllegalArgumentException
            | InvocationTargetException
            | NoSuchMethodException
            | SecurityException e
        ) {
            e.printStackTrace();
            return null;
        }
    }

    protected void finalizeRef(Asset t) {
        if (t.getAs().s == State.LOADING) {
            t.update();
            t.getAs().finalizeloading();
            t.finalizeloading();
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends Asset> T getRef(UUID u) {
        if (!isAssetLoaded(u)) loadAsset(u);
        Asset t = getassets(map -> map.get(u));
        t.getAs().u = u;
        if (t.getAs().s == State.LOADING) {
            t.update();
            t.getAs().finalizeloading();
            t.finalizeloading();
        }
        t.getAs().references++;
        try {
            return (T) t
                .getClass()
                .getConstructor(DataAsset.class)
                .newInstance((DataAsset) t.getAs());
        } catch (
            InstantiationException
            | IllegalAccessException
            | IllegalArgumentException
            | InvocationTargetException
            | NoSuchMethodException
            | SecurityException e
        ) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean isAssetLoaded(UUID u) {
        return getassets(m -> m.containsKey(u));
    }

    public void waitforAsset(UUID... ul) {
        for (UUID u : ul)
            while (!this.isAssetLoaded(u)) try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
    }

    public void returnRef(UUID u) {
        Asset t = getassets(map -> map.get(u));
        t.getAs().references--;
        if (t.getAs().references <= 0) {
            t.getAs().unload();
            useassets(m -> m.remove(u));
        }
    }

    class StreamJob implements Runnable {

        private final long winid;

        public StreamJob(long win) {
            winid = win;
        }

        @Override
        public void run() {
            GLFW.glfwMakeContextCurrent(winid);
            GL.createCapabilities();
            System.out.println("Started Streaming");
            while (true) {
                try {
                    jobqueue.take().run();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @EventTarget
    public void onPostRender(EPostRender pr) {
        while (!getswap(a -> a.isEmpty())) {
            UUID u = getswap(b -> b.poll());
            Asset a = getassets(c -> c.get(u));
            if (a != null) {
                a.getAs().swap();
                a.update();
            }
        }
    }

    @SuppressWarnings("deprecation")
    public void destroy() {
        StreamThread.suspend();
        for (Entry<UUID, Asset> s : this.getAssets())
            s.getValue().getAs().unload();
    }

    public Path getPath() {
        return p;
    }

    public String getPath(UUID key) {
        return getrefs(a -> a.get(key));
    }

    public void returnRef(Asset sc) {
        this.returnRef(sc.getUUID());
    }

    public UUID registerAsset(String string) {
        return registerAsset(Paths.get(string));
    }
}
