package enviromine.trackers.items;

import java.math.BigDecimal;
import java.math.RoundingMode;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.village.Village;
import enviromine.trackers.EnviroData;
import enviromine.trackers.NewEnviroDataTracker;
import enviromine.trackers.properties.ArmorProperties;
import enviromine.trackers.properties.BiomeProperties;
import enviromine.trackers.properties.BlockProperties;
import enviromine.trackers.properties.DimensionProperties;
import enviromine.trackers.properties.EntityProperties;
import enviromine.trackers.properties.ItemProperties;

public abstract class EnviroTrackerItem 
{
	private int id;
	public String name;
	
	public float valueCur;
	public float valueMax;
	public float valueMin;
	public float prevValue;
	
	/**
	 * Max Limit set a temporary maximum limit of value below valueMax
	 */
	protected float maxLimit;
	protected float minLimit;
	
	protected float valueDiff;
	
	private float MaxRiseValue;
	private float MaxDropValue;
	
	private float dropTimer;
	

	public EnviroTrackerItem()
	{
		id = getDefaultID();
		name = getName();
		setDefaultValues(getStartValue(), 100F, 0F);
	}
	
/**
 * Set Values of this Tracker Item 
 * <br><br>
 * <b>Example:</b> setValues(100F, 100F, 0F);
 * @param cur "Current Value"
 * @param max "Max Value"
 * @param min "Min Value"
 */
	public void setDefaultValues(float cur, float max, float min)
	{
		valueCur =  cur;
		valueMax = max;
		valueMin = min;
		
		maxLimit = max;
		minLimit = min;
		
		prevValue = valueCur;
		
		valueDiff = 0;
	}
	
	/**
	 * Set a Temporary Max limit which can be higher or lower than its default max value.
	 * @param limit
	 */
	public void setTempMaxLimit(float limit)
	{
		this.maxLimit = limit;
	}
	
	public abstract int getDefaultID();
	
	public abstract float getStartValue();
	
	public abstract String getName();
	
	/**
	 * This searches x Range around player for block data & biome data, <br> 
	 * and passes each block in a loop
	 * 
	 * @param trackedEntity
	 */
	public abstract void getSurroundingData(EnviroData enviroData, Block block, int Meta, int searchRange, BlockProperties blockProp, BiomeProperties biomeProp);
	
	/**
	 * This search in a range around the player for any mob and passes <br> 
	 * in a loop to check and set tracker data for each mob type around player.
	 * 
	 * @param enviroData
	 * @param mob
	 * @param mobTrack
	 * @param livingProp
	 */
	public abstract void mobFoundNear(EnviroData enviroData, Entity mob, NewEnviroDataTracker mobTrack, EntityProperties livingProp);
	
	/**
	 * This loops each armor slot on player/entity checking for armor.<br>
	 * When armor is found it will pass it into wearingArmor check and set tracker data
	 *  
	 * @param enviroData
	 * @param armorSlot
	 * @param armorPiece
	 * @param enchTags
	 * @param props
	 */
	public abstract void wearingArmor(EnviroData enviroData, int armorSlot, ItemStack armorPiece, NBTTagList enchTags, ArmorProperties props);
	/**
	 * If armor is found it will pass it into armorEnchants to <br>
	 * check and set tracker data
	 * @param enviroData
	 * @param armorSlot
	 * @param enID
	 * @param enLV
	 */
	public abstract void armorEnchants(EnviroData enviroData, int armorSlot, int enID, int enLV);
	
	/**
	 * This searches your HotBar for items and passes and ItemStack for checking against tracker data
	 * 
	 * @param enviroData
	 * @param itemstack
	 * @param stackMult
	 * @param itemProp
	 */
	public abstract void inventorySearch(EnviroData enviroData, ItemStack itemstack,float stackMult, ItemProperties itemProp);
	
	/**
	 * If Player Check against Tracker data and Do Somthing
	 * 
	 * @param enviroData
	 * @param trackedEntity
	 * @param biomeProp
	 * @param dimensionProp
	 */
	public abstract void ifEntityPlayer(EnviroData enviroData, EntityPlayer trackedEntity, BiomeProperties biomeProp, DimensionProperties dimensionProp);

	/**
	 * If Animal Check against Tracker data and Do Somthing
	 * 
	 * @param enviroData
	 * @param trackedEntity
	 * @param biomeProp
	 * @param dimensionProp
	 */

	public abstract void ifEntityAnimal(EnviroData enviroData, EntityAnimal trackedEntity, EntityProperties entityProp, BiomeProperties biomeProp, DimensionProperties dimensionProp);
	
	/**
	 * If Mob Check against Tracker data and Do Somthing
	 * 
	 * @param enviroData
	 * @param trackedEntity
	 * @param biomeProp
	 * @param dimensionProp
	 */
	
	public abstract void ifEntityMob(EnviroData enviroData, EntityMob trackedEntity, EntityProperties entityProp, BiomeProperties biomeProp, DimensionProperties dimensionProp);
	
	/**
	 * If Villager was found and in Village, and Villager is able to help do something. Check against tracker
	 * 
	 * @param enviroData
	 * @param villager
	 * @param village
	 * @param entityProp
	 */
	public abstract void villageAssist(EnviroData enviroData, EntityVillager villager, Village village, EntityProperties entityProp);
	
	/**
	 * Overrides are used to make last minute changes before update tracker is called... ie: Biomes/Dimension/config Multipliers
	 * 
	 * @param enviroData
	 * @param biomeProp
	 * @param dimensionProp
	 */
	public abstract void Overrides(EnviroData enviroData, BiomeProperties biomeProp, DimensionProperties dimensionProp);
	
	/**
	 * Updates Tracker data from all Envirodata gathered.
	 *  
	 * @param enviroData
	 */
	public abstract void updateTracker(EnviroData enviroData);
	
	/**
	 * Applies Side Effects to entity if certain things happen
	 * 
	 * @param enviroData
	 */
	public abstract void applySideEffects(EnviroData enviroData);
	
	//public abstract void ifSideEffectActive(EnviroData enviroData);
	
	
	public boolean isEnabledByDefault()
	{
		return true;
	}
	
	
	public void fixValueBounds()
	{
		if(this.valueCur >= this.maxLimit)
		{
			this.valueCur = this.valueMax;
		}
		else if(this.valueCur <= this.minLimit)
		{
			this.valueCur = this.valueMin;
		}
		else if(this.valueCur >= this.valueMax)
		{
			this.valueCur = this.valueMax;
		}
		else if (this.valueCur <= this.valueMin)
		{
			this.valueCur = this.valueMin;
		}
	}
	
	public void fixFloatinfPointErrors()
	{
		this.valueCur = new BigDecimal(String.valueOf(this.valueCur)).setScale(2, RoundingMode.HALF_UP).floatValue();
	}
	
	public void resetNumbers()
	{
		this.valueDiff = 0F;
	}
	
	public void saveToNBT() 
	{
		
	}
	
	public void readFromNBT()
	{
		
	}
	
	public boolean isCreative(EntityLivingBase trackedEntity)
	{
		if(trackedEntity instanceof EntityPlayer)
		{
			if(((EntityPlayer)trackedEntity).capabilities.isCreativeMode)
			{
				return true;
			}
		}
		return false;
	}

		
	
}
