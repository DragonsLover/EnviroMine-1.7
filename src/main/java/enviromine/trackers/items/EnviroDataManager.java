package enviromine.trackers.items;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import enviromine.core.EM_Settings;
import enviromine.handlers.EM_StatusManager;
import enviromine.trackers.EnviroData;

public class EnviroDataManager
{

	public AirQualityTracker airQuality;
	public TemperatureTracker bodyTemp;
	public float airTemp;
	public HydrationTracker hydration;
	public SanityTracker sanity;
    
	public EntityLivingBase trackedEntity;
	public List<EnviroTrackerItem> trackers;

	
		float prevBodyTemp;
		float prevAirQuality; 
		float prevHydration; 
		float prevSanity; 
		
	
	public EnviroDataManager(EntityLivingBase entity)
	{
		trackedEntity = entity;
		airQuality = new AirQualityTracker();
		bodyTemp = new TemperatureTracker();
		hydration = new HydrationTracker();
		sanity = new SanityTracker();
	
	}
	
	public void updateData()
	{
		
	   // EnviroData enviroData = new EnviroData(this);
	    
		bodyTemp.prevValue = bodyTemp.valueCur;
		airQuality.prevValue = airQuality.valueCur;
		hydration.prevValue = hydration.valueCur;
		sanity.prevValue = sanity.valueCur;
		
		if(!CheckSetTracker()) return;
		
		int i = MathHelper.floor_double(trackedEntity.posX);
		int k = MathHelper.floor_double(trackedEntity.posZ);
		
		if(!trackedEntity.worldObj.getChunkFromBlockCoords(i, k).isChunkLoaded)
		{
			return;
		}
		
		float[] enviroData = EM_StatusManager.getSurroundingData(trackedEntity, 5);
		boolean isCreative = false;
		
		if(trackedEntity instanceof EntityPlayer)
		{
			if(((EntityPlayer)trackedEntity).capabilities.isCreativeMode)
			{
				isCreative = true;
			}
		}
		
		
		
	}

	
	/**
	 * Checks for a Tracker for the Entity.
	 * Returns false if player is dead, null
	 * if (entity = Player) changes entity to a player entity
	 * @return
	 */
	private boolean CheckSetTracker()
	{
		if(trackedEntity == null)
		{
			//EM_StatusManager.removeTracker(this);
			return false;
		}
		
		if(trackedEntity.isDead)
		{
			if(trackedEntity instanceof EntityPlayer)
			{
				EntityPlayer player = EM_StatusManager.findPlayer(trackedEntity.getCommandSenderName());
				
				if(player == null)
				{
					//EM_StatusManager.saveAndRemoveTracker(this);
					return false;
				} else
				{
					trackedEntity = player;
					//this.loadNBTTags();
				}
			} else
			{
				//EM_StatusManager.removeTracker(this);
				return false;
			}
		}
		
		if(!(trackedEntity instanceof EntityPlayer) && !EM_Settings.trackNonPlayer || (EM_Settings.enableAirQ == false && EM_Settings.enableBodyTemp == false && EM_Settings.enableHydrate == false && EM_Settings.enableSanity == false))
		{
			//EM_StatusManager.saveAndRemoveTracker(this);
			return false;
		}
		
		return true;
		
	}
	
}
