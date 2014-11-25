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
			this.interval = 200;
			this.peakWait = 100;
			this.peakWaitPhase = 0;
			this.peakSpeed = 1;
			this.baseSpeed = 1;

			this.pulse = pulse;
			this.resource = Gui_EventManager.blurOverlayResource;
			
			this.R = 255;
			this.G = 255;
			this.B = 255;
		}
		
		public void setRGB(int R, int G, int B)
		{
			this.R = R;
			this.G = G;
			this.B = B;
		}
		
		public int getRGBA(int alpha)
		{
			return EnviroUtils.getColorFromRGBA(this.R, this.G, this.B , alpha);
		}
		
		public void setPulseVar(int amplitude, int interval, int peakWait, int peakSpeed, int baseSpeed)
		{
			this.amplitude = amplitude > 111 ? 111 : amplitude ;
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
 

        alpha = (int)( overlay.amplitude - Math.sin( Math.toRadians(overlay.phase) ) *  overlay.amplitude );
        alpha = alpha*2;

        if(alpha >= 254) alpha = 254;
        else if(alpha <= 0) alpha = 0;
        //Moving up to peak
        if(overlay.phase <=  overlay.amplitude)	 
        {
        	if(overlay.phase >= (overlay.amplitude/2) && overlay.peakWaitPhase <= overlay.peakWait)
        	{
        		overlay.peakWaitPhase++;
        	}
        	else
        	{
        		overlay.phase += overlay.peakSpeed;
        	}
        	
        }
        else if(overlay.intervalPhase <= overlay.interval)
        {
        	overlay.intervalPhase++;
        }
        else  
        {
        	overlay.phase = 0;
        	overlay.peakWaitPhase = 0;
        	overlay.intervalPhase = 0;
        }
            
            // there's no drawPoint in java so draw a VERY short line
           // Minecraft.getMinecraft().fontRenderer.drawString(phase +" : "+ y, this.getDefaultPosX(), this.getDefaultPosY() -50 , 16777215);
            
        return alpha;
    }
}

