package enviromine.client.hud;

import java.util.ArrayList;
import java.util.List;

import enviromine.EnviroUtils;
import enviromine.client.Gui_EventManager;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

public class OverlayHandler 
{	

	protected static List<Overlay> OverlayList = new ArrayList<Overlay>();
	protected static List<Overlay> OverlayListActive = new ArrayList<Overlay>();

	public class Overlay
	{

		public int id;
		public int amplitude;
		public int phase;
		public int interval, intervalPhase;
		public int peakWait, peakWaitPhase;
		public int peakSpeed, baseSpeed;
		public int R, G, B;
		public Boolean pulse;
		public ResourceLocation resource;
		
		
		public Overlay (int id, Boolean pulse)
		{
			this.id = id;
			this.amplitude = 111;
			this.phase = 0;
			this.intervalPhase = 0;
			this.interval = 0;
			this.peakWait = 0;
			this.peakWaitPhase = 0;
			this.peakSpeed = 1;
			this.baseSpeed = 1;

			this.pulse = pulse;
			this.resource = Gui_EventManager.blurOverlayResource;
		}
		
		public void setRGB(int R, int G, int B)
		{
			this.R = R;
			this.G = G;
			this.B = B;
		}
		
		public int getRGBA(Overlay overlay, int alpha)
		{
			return EnviroUtils.getColorFromRGBA(overlay.R, overlay.G, overlay.B , alpha);
		}
		
		public void setPulseVar(int amplitude, int phase, int interval, int peakWait, int peakSpeed, int baseSpeed)
		{
			this.amplitude = amplitude;
			this.phase = phase;
			this.interval = interval;
			this.peakWait = peakWait;
			this.peakSpeed = peakSpeed;
			this.baseSpeed = baseSpeed;
		}
		
		public void setResource(ResourceLocation resource)
		{
			this.resource = resource;
		}
	}
	
    public static void registerOverlayItem(Overlay overlay) {

    	if (!OverlayList.contains(overlay)) 
        {
    		OverlayList.add(overlay);
        }
    }
    
	public static List<Overlay> getOverlayItemList() 
	{
        return OverlayList;
    }

    public static void enableOverlayItem(Overlay overlay) 
    {
        if (OverlayList.contains(overlay) && !OverlayListActive.contains(overlay)) 
        {
        	OverlayListActive.add(overlay);
        }
    }

    public static void disableOverlayItem(Overlay overlay) 
    {
    	OverlayListActive.remove(overlay);
    }

    public static List<Overlay> getActiveOverlayItemList() 
    {
        return OverlayListActive;
    }

    public static boolean isActiveHudItem(Overlay overlay) 
    {
        return getActiveOverlayItemList().contains(overlay);
    }
	
    public static Overlay getHudItemByID(int id) 
    {
        for (Overlay overlay : getOverlayItemList()) 
        {
            if (id == overlay.id)
                return overlay;
        }
        return null;
    }
	
	public int PulseWave(Overlay overlay)
    {
        int alpha;
 

        alpha = (int)( overlay.amplitude - Math.sin( Math.toRadians( overlay.phase) ) *  overlay.amplitude );
            
            if(alpha <=  overlay.amplitude)	 overlay.phase += overlay.peakSpeed;
            else  overlay.phase = 0;
            
            // there's no drawPoint in java so draw a VERY short line
           // Minecraft.getMinecraft().fontRenderer.drawString(phase +" : "+ y, this.getDefaultPosX(), this.getDefaultPosY() -50 , 16777215);
            
        return alpha;
    }
}

