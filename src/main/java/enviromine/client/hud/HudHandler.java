package enviromine.client.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.EntityLivingBase;

public class HudHandler {

    private ScaledResolution res = null;
    
    private Minecraft mc = Minecraft.getMinecraft();
    
    public void RenderHud()
    {

    	HUDRegistry.checkForResize();
	
    	for (HudItem huditem : HUDRegistry.getHudItemList()) 
    	{
    		if (mc.playerController.isInCreativeMode() && !huditem.isRenderedInCreative()) 
    		{
    			continue;
    		}
    		if (mc.thePlayer.ridingEntity instanceof EntityLivingBase) 
    		{
    			if (huditem.shouldDrawOnMount()) 
    			{
    				huditem.fixBounds();
    				huditem.render();
    			}
    		} else 
    		{
    			if (huditem.shouldDrawAsPlayer()) 
    			{
    				huditem.fixBounds();
    				huditem.render();
    			}
    		}
    	}
    }
    
}
