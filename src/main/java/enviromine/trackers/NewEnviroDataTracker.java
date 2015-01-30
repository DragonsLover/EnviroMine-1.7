package enviromine.trackers;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import enviromine.core.EM_Settings;
import enviromine.handlers.EM_StatusManager;
import enviromine.trackers.items.AirQualityTracker;
import enviromine.trackers.items.EnviroTrackerItem;

public class NewEnviroDataTracker
{

	public AirQualityTracker airQuality;
	public int updateTimer = 0;
	//public TemperatureTracker bodyTemp;
	//public float airTemp;
	//public HydrationTracker hydration;
	//public SanityTracker sanity;
    
	public int attackDelay = 1;
	public int curAttackTime = 0;
	
	public EntityLivingBase trackedEntity;
	public List<EnviroTrackerItem> trackers;

	
	public NewEnviroDataTracker(EntityLivingBase entity)
	{
		trackedEntity = entity;
		airQuality = new AirQualityTracker();
		//bodyTemp = new TemperatureTracker();
		//hydration = new HydrationTracker();
		//sanity = new SanityTracker();
	
	}
	
	public void updateData()
	{
		
	    
	    // set Previous values before changes
			//bodyTemp.prevValue = bodyTemp.valueCur;
			airQuality.prevValue = airQuality.valueCur;
			//hydration.prevValue = hydration.valueCur;
			//sanity.prevValue = sanity.valueCur;
		
		if(!CheckSetTracker()) return;
		
		if(!trackedEntity.worldObj.getChunkFromBlockCoords(MathHelper.floor_double(trackedEntity.posX), MathHelper.floor_double(trackedEntity.posZ)).isChunkLoaded)
		{
			return;
		}
		
		EnviroData enviroData = new EnviroData(this);
	    	enviroData.Run();

	    
		boolean isCreative = false;
		
		if(trackedEntity instanceof EntityPlayer)
		{
			if(((EntityPlayer)trackedEntity).capabilities.isCreativeMode)
			{
				isCreative = true;
			}
		}
		
		//TODO Call Overrides.. and updateTrackers
		this.airQuality.Overrides(enviroData, enviroData.biomeProp, enviroData.dimensionProp);
		this.airQuality.updateTracker(enviroData);

		


		//TODO Call Apply SideEffects
		if(curAttackTime >= attackDelay)
		{
			this.airQuality.applySideEffects(enviroData);		

			curAttackTime = 0;
		}
		else 
		{
			curAttackTime +=1;
		}
		
		
		if(trackedEntity instanceof EntityPlayer)
		{
			if(((EntityPlayer)trackedEntity).capabilities.isCreativeMode)
			{

				airQuality.valueCur = airQuality.prevValue;
			}
			
		}	

		//Dimension Overrides
		if(enviroData.dimensionProp != null && enviroData.dimensionProp.override)
		{
			if(!enviroData.dimensionProp.trackAirQuality && EM_Settings.enableAirQ) airQuality.valueCur = airQuality.prevValue;
		}
		
		
		//FIX FLOATING POINTS
		this.airQuality.fixFloatinfPointErrors();
		
		
		//SAVE THIS
		
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
