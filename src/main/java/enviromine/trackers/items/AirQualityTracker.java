package enviromine.trackers.items;

import enviromine.EnviroDamageSource;
import enviromine.trackers.EnviroData;
import enviromine.trackers.properties.EntityProperties;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.MathHelper;

public class AirQualityTracker extends EnviroTrackerItem{

	private float gasAirDiff = 0F;
	private boolean enableAirQ = true;
	
	public float test = 1;
	
	public AirQualityTracker()
	{
		super();
	}
	
	@Override
	public int getDefaultID() 
	{
		return 1;
	}
	
	@Override
	public float getStartValue() {
		return 100F;
	}

	@Override
	public String getName() {
		return "Air Quality";
	}
	
	public void setGasAirDiff(float diff)
	{
		gasAirDiff = diff;
	}

	@Override
	public void EntityPlayerAction(float[] enviroData, EntityLivingBase trackedEntity) 
	{

		// Air checks
		enviroData[0] += gasAirDiff;
		gasAirDiff = 0F;
		valueCur += enviroData[0];
		
		ItemStack helmet = trackedEntity.getEquipmentInSlot(4);
		if(helmet != null && !isCreative(trackedEntity))
		{
			if(helmet.hasTagCompound() && helmet.getTagCompound().hasKey("gasMaskFill"))
			{
				NBTTagCompound tag = helmet.getTagCompound();
				int gasMaskFill = tag.getInteger("gasMaskFill");
				
				if(gasMaskFill > 0 && valueCur <= 99F)
				{
					int airDrop = 100 - MathHelper.ceiling_float_int(valueCur);
					airDrop = gasMaskFill >= airDrop? airDrop : gasMaskFill;
					
					if(airDrop > 0)
					{
						valueCur += airDrop;
						tag.setInteger("gasMaskFill", (gasMaskFill - airDrop));
					}
				}
			}
		}
	}

	@Override
	public void EntityAnimalAction(float[] enviroData, EntityAnimal trackedEntity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void SpecialAction(float[] enviroData, EntityLivingBase trackedEntity) {

		
	}

	@Override
	public void Tick_SideEffects(EntityLivingBase trackedEntity) 
	{
		if(valueCur <= 0)
		{
			trackedEntity.attackEntityFrom(EnviroDamageSource.suffocate, 4.0F);
			
			trackedEntity.worldObj.playSoundAtEntity(trackedEntity, "enviromine:gag", 1f, 1f);
 		}
		
		if(valueCur <= 10F)
		{
			trackedEntity.addPotionEffect(new PotionEffect(Potion.digSlowdown.id, 200, 1));
			trackedEntity.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 200, 1));
		} else if(valueCur <= 25F)
		{
			trackedEntity.addPotionEffect(new PotionEffect(Potion.digSlowdown.id, 200, 0));
			trackedEntity.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 200, 0));
		}	
	}


	@Override
	public void LoopSurroundingData(EntityLivingBase trackedEntity) {
		// TODO Auto-generated method stub
		
	}





}
