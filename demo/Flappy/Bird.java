package Flappy;

import me.pxl.ECS.Entities.EUpdate;
import me.pxl.Event.EventManager;
import me.pxl.Event.EventTarget;
import me.pxl.Event.Events.EKey;
import me.pxl.Event.Events.EKey.Action;
import me.pxl.Log.Timer;

//Bird
//Date:09. Apr. 2022
//Author:BF
public class Bird extends EUpdate{
    
    public float jumpheight=6f;
    private float my; 
    Timer t;

    @Override
    public void onSetup() {
        EventManager.register(this);
        t=new Timer();
        super.onSetup();
        my=0;
    }

    @EventTarget
    public void onKey(EKey k){
        switch(k.getKey().toLowerCase()){
            case "w":
                if(t.getmillis()>200f&&k.getAction().equals(Action.PRESS)){
                    my=jumpheight;
                    t.reset();
                }
                break;
        }
    }

    @Override
    protected void onUpdate() {
        this.getPos().sub(0, my, 0);
        my-=0.981f/6;
    }


}
