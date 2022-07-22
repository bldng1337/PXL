//Game
//Date:09. Apr. 2022
//Author:BF
package Flappy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.joml.Vector2f;
import org.joml.Vector3f;

import me.pxl.Engine;
import me.pxl.Asset.Assets.AScene;
import me.pxl.Asset.Assets.AShader;
import me.pxl.Asset.Assets.ATexture;
import me.pxl.ECS.CAsset;
import me.pxl.ECS.Component;
import me.pxl.ECS.Entity;
import me.pxl.ECS.Components.CRender;
import me.pxl.ECS.Entities.EUpdate;
import me.pxl.Event.EventManager;
import me.pxl.Log.Timer;


public class Game extends EUpdate{
    
    Timer obstaclespawn;
    Engine e;
    Entity background;
    float pos;
    boolean gameOver=false;

    public float range=500f;
    @CAsset(Name="Game Over Texture")
    public ATexture Endtxt;
    @CAsset(Name="Obstacle")
    public AScene osc;
    @CAsset(Name="Background Shader")
    public AShader bsh;
    @CAsset(Name="Background Texture")
    public ATexture btxt;

    @Override
    public void onSetup() {
        super.onSetup();
        e=Engine.getEngine();
        obstaclespawn=new Timer();

        
    }
    boolean start=false;
    Entity player;
    
    public void onStartPlay(){
        player=e.em.getEntities("Bird").get(0);

        background=e.em.instanceEntity(Entity.class);
        background.pos.set(0, -(e.getFinalBuffer().getHeight()/2f), -0.5f);
        background.size.set(e.getFinalBuffer().getWidth(), e.getFinalBuffer().getHeight());
        CRender rcomp=e.em.attachComponent(background, CRender.class);
        rcomp.sh=e.getAssetManager().getRef(bsh.getUUID());
        rcomp.txt=e.getAssetManager().getRef(btxt.getUUID());
    }

    @Override
    protected void onUpdate() {
        if(gameOver)
            return;
        
        if(!start){
            onStartPlay();
            start=true;
        }
        background.pos.set(0, -(e.getFinalBuffer().getHeight()/2f), -0.5f);
        background.size.set(e.getFinalBuffer().getWidth(), e.getFinalBuffer().getHeight());
        e.em.translate(0, (e.getFinalBuffer().getHeight()/2f));
        if(obstaclespawn.getmillis()>2000){
            obstaclespawn.reset();
            try {
                e.em.loadScene(osc.getScene(), false, new Vector3f(2000f,(float)Math.random()*range-range/2,0));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        List<Entity> rmlist=new ArrayList<>();
        for(Component c:e.em.getComponents(CObstacle.class)){
            if(rectRect(player.pos.x,player.pos.y,player.size.x,player.size.y,c.getEntity().pos.x,c.getEntity().pos.y,c.getEntity().size.x,c.getEntity().size.y)){
                gameOver=true;
                EventManager.unregister(player);
                Entity ent=e.em.instanceEntity(Entity.class);
                e.em.attachComponent(ent, CRender.class).txt=e.getAssetManager().getRef(this.Endtxt.getUUID());
                Vector2f size=this.Endtxt.getT().getSize();
                size.mul(2);
                ent.pos.set(e.getFinalBuffer().getWidth()/2f-size.x/2, -size.y/2, 2);
                ent.size.set(size);
            }
            if(c.getEntity().getPos().x<-2000){
                rmlist.add(c.getEntity());
            }
            c.getEntity().getPos().sub(2, 0, 0);
        }
        pos+=0.2;
        if(pos>100)
            pos=0;
        bsh.getShader().setVal("time", pos/100f);
        rmlist.forEach(en->e.em.despawnEntity(en));
    }

    private boolean rectRect(float r1x, float r1y, float r1w, float r1h, float r2x, float r2y, float r2w, float r2h) {

        // are the sides of one rectangle touching the other?
      
        if (r1x + r1w >= r2x &&    // r1 right edge past r2 left
            r1x <= r2x + r2w &&    // r1 left edge past r2 right
            r1y + r1h >= r2y &&    // r1 top edge past r2 bottom
            r1y <= r2y + r2h) {    // r1 bottom edge past r2 top
              return true;
        }
        return false;
      }
}
