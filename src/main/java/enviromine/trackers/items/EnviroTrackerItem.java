package enviromine.trackers.items;

import enviromine.core.EM_Settings;
import enviromine.handlers.EM_PhysManager;
import enviromine.trackers.properties.BiomeProperties;
import enviromine.trackers.properties.BlockProperties;
import enviromine.trackers.properties.EntityProperties;
import enviromine.utils.EnviroUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.MathHelper;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;

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
	public float maxLimit;
	public float minLimit;
	
	private float riseValue;
	private float dropValue;
	
	private float MaxRiseValue;
	private float MaxDropValue;
	
	private float dropTimer;
	

	public EnviroTrackerItem()
	{
		id = getDefaultID();
		name = getName();
		setValues(getStartValue(), 100F, 0F);
	}
	
/**
 * Set Values of this Tracker Item 
 * <br><br>
 * <b>Example:</b> setValues(100F, 100F, 0F);
 * @param cur "Current Value"
 * @param max "Max Value"
 * @param min "Min Value"
 */
	public void setValues(float cur, float max, float min)
	{
		valueCur =  cur;
		valueMax = max;
		valueMin = min;
		prevValue = valueCur;
	}
	
	public abstract int getDefaultID();
	
	public abstract float getStartValue();
	
	public abstract String getName();
	
	/**
	 * This Returns data to EnviroData
	 * 
	 * @param trackedEntity
	 */
	public abstract void LoopSurroundingData(EntityLivingBase trackedEntity);
	
	public abstract void EntityPlayerAction(float[] enviroData, EntityLivingBase trackedEntity);
	
	public abstract void EntityAnimalAction(float[] enviroData, EntityAnimal trackedEntity);
	
	public abstract void SpecialAction(float[] enviroData, EntityLivingBase trackedEntity);
	
	public abstract void Tick_SideEffects(EntityLivingBase trackedEntity);
	
	public boolean isEnabledByDefault()
	{
		return true;
	}
	
	
	public void fixValueBounds()
	{
		if(this.valueCur >= this.valueMax)
		{
			this.valueCur = this.valueMax;
		}
		else if (this.valueCur <= this.valueMin)
		{
			this.valueCur = this.valueMin;
		}
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
