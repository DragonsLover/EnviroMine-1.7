package enviromine.handlers;

import net.minecraft.client.Minecraft;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.RenderTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Type;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import enviromine.client.hud.HUDRegistry;
import enviromine.client.hud.SaveController;
import enviromine.core.EM_Settings;
import enviromine.gases.GasBuffer;
import enviromine.world.Earthquake;

public class EM_ServerScheduledTickHandler
{
	@SubscribeEvent
	public void tickEnd(TickEvent.WorldTickEvent tick)
	{
		
		if(tick.side.isServer())
		{
			Earthquake.updateEarthquakes();
			GasBuffer.update();
			
			if(EM_Settings.enablePhysics)
			{
				EM_PhysManager.updateSchedule();
			}
		}
	
	}
	
    private boolean ticked = false;
    private boolean firstload = true;

    @SubscribeEvent
	@SideOnly(Side.CLIENT)
    public void RenderTickEvent(RenderTickEvent event) 
    {
        if ((event.type == Type.RENDER || event.type == Type.CLIENT) && event.phase == Phase.END) 
        {
            Minecraft mc = Minecraft.getMinecraft();
            if (firstload && mc != null) 
            {
            	System.out.println("First Load...");
                if (!SaveController.loadConfig("config"))
                {
                	System.out.println("No Config Create one...");
                    HUDRegistry.checkForResize();
                    HUDRegistry.resetAllDefaults();
                    SaveController.saveConfig("config");
                }
                firstload = false;
            }
        }
    }
}
