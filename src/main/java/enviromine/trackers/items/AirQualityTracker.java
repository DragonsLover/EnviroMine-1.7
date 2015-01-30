package enviromine.trackers.items;

import enviromine.EnviroDamageSource;
import enviromine.core.EM_Settings;
import enviromine.trackers.EnviroData;
import enviromine.trackers.NewEnviroDataTracker;
import enviromine.trackers.properties.ArmorProperties;
import enviromine.trackers.properties.BiomeProperties;
import enviromine.trackers.properties.BlockProperties;
import enviromine.trackers.properties.DimensionProperties;
import enviromine.trackers.properties.EntityProperties;
import enviromine.trackers.properties.ItemProperties;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFlower;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.MathHelper;
import net.minecraft.village.Village;
import net.minecraftforge.common.EnumPlantType;

public class AirQualityTracker extends EnviroTrackerItem {

	private float gasAirDiff = 0F;
	private boolean enableAirQ = true;
	private float leaves = 0F;

		
	@Override
	public int getDefaultID() {
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

	@Override
	public void getSurroundingData(EnviroData enviroData, Block block, int Meta, int searchRange, BlockProperties blockProp, BiomeProperties biomeProp) 
	{

		if(blockProp != null)
		{
			if(blockProp.air > 0F)
			{
				leaves += (blockProp.air/0.1F);
			}
			else if (this.valueDiff >= blockProp.air && blockProp.air < 0 && this.valueDiff <=0)
			{
				this.valueDiff = blockProp.air;
			}
			
		}
		

	}

	@Override
	public void mobFoundNear(EnviroData enviroData, Entity mob, NewEnviroDataTracker mobTrack, EntityProperties livingProp) 
	{
		if(livingProp != null && enviroData.entityLiving.trackedEntity.canEntityBeSeen(mob))
		{
			if(livingProp.ambAir > 0F) // if Mob can drain your air
			{
				leaves += (livingProp.ambAir/0.1F);
			} else if(this.valueDiff >= livingProp.ambAir && livingProp.ambAir < 0 && this.valueDiff <= 0)
			{
				this.valueDiff = livingProp.ambAir;
			}
		}
	}

	@Override
	public void wearingArmor(EnviroData enviroData, int armorSlot, ItemStack armorPiece, NBTTagList enchTags, ArmorProperties props) 
	{
		
		if(armorSlot == 4) // helmet
		{
			if(props.air > 0F)
			{
				leaves += (props.air/0.1F);
			} else if(this.valueDiff >= props.air && props.air < 0 && this.valueDiff <= 0)
			{
				this.valueDiff = props.air;
			}
		}
		else
		{
			if((this.valueDiff <= props.air && props.air > 0F) || (this.valueDiff >= props.air && props.air < 0 && this.valueDiff <= 0))
			{
				this.valueDiff = props.air;
			}
		}
	}

	@Override
	public void armorEnchants(EnviroData enviroData, int armorSlot, int enID, int enLV) 
	{
		if(enID == Enchantment.respiration.effectId && armorSlot == 4)
		{
			leaves += 3F * enLV;
		}
		
	}

	@Override
	public void inventorySearch(EnviroData enviroData, ItemStack itemstack,	float stackMult, ItemProperties itemProp) 
	{
		if(itemProp.ambAir > 0F)
		{
			leaves += (itemProp.ambAir/0.1F) * stackMult;
			
			System.out.println("Found Leaves" + leaves);
		} else if(this.valueDiff >= itemProp.ambAir * stackMult && itemProp.ambAir < 0 && this.valueDiff <= 0)
		{
			this.valueDiff = itemProp.ambAir * stackMult;
		}
	}

	@Override
	public void ifEntityPlayer(EnviroData enviroData, EntityPlayer trackedEntity, BiomeProperties biomeProp, DimensionProperties dimProp) 
	{
		AirQuilityChecks(enviroData, (EntityLivingBase)trackedEntity, null, biomeProp, dimProp);
	}

	@Override
	public void ifEntityAnimal(EnviroData enviroData, EntityAnimal trackedEntity, EntityProperties entityProp,	BiomeProperties biomeProp, DimensionProperties dimensionProp) 
	{
		
	}
	
	@Override
	public void ifEntityMob(EnviroData enviroData, EntityMob trackedEntity,	EntityProperties entityProp, BiomeProperties biomeProp,	DimensionProperties dimensionProp) 
	{
		
	}
	
	public void AirQuilityChecks(EnviroData enviroData, EntityLivingBase trackedEntity, EntityProperties entityProp, BiomeProperties biomeProp, DimensionProperties dimensionProp)
	{
		if(enviroData.lightLev > 1 && !trackedEntity.worldObj.provider.hasNoSky) // if Hell World
		{
			this.valueDiff = 2F;
		}
		
		if(dimensionProp != null && trackedEntity.posY > dimensionProp.sealevel * 0.75 && !trackedEntity.worldObj.provider.hasNoSky)
		{
			this.valueDiff = 2F;
		}
		
		
		this.valueDiff += (leaves * 0.1F);
		
		if(this.valueDiff < 0)
		{
			this.valueDiff *= enviroData.solidBlocks/Math.pow(enviroData.range*2, 3);
		}
	}
	
	@Override
	public void Overrides(EnviroData enviroData, BiomeProperties biomeProp,	DimensionProperties dimensionProp) 
	{
		if(dimensionProp != null && dimensionProp.override)
		{   
			this.valueDiff = this.valueDiff * (float) dimensionProp.airMulti + dimensionProp.airRate;
		}
		
		if(biomeProp != null && biomeProp.biomeOveride)
		{
			this.valueDiff = this.valueDiff * (float) biomeProp.airRate;
		}
		
		this.valueDiff *= (float)EM_Settings.airMult;

	}

	@Override
	public void villageAssist(EnviroData enviroData, EntityVillager villager, Village village, EntityProperties entityProp) 
	{
	
	}

	@Override
	public void updateTracker(EnviroData enviroData) 
	{
		EntityLivingBase trackedEntity = enviroData.entityLiving.trackedEntity;
		boolean isCreative = false;
		
		this.prevValue = this.valueCur;
		
		// Air checks
		this.valueDiff += gasAirDiff;
		gasAirDiff = 0F;
		this.valueCur += this.valueDiff;
		
		ItemStack helmet = trackedEntity.getEquipmentInSlot(4);
		if(helmet != null && !isCreative)
		{
			if(helmet.hasTagCompound() && helmet.getTagCompound().hasKey("gasMaskFill"))
			{
				NBTTagCompound tag = helmet.getTagCompound();
				int gasMaskFill = tag.getInteger("gasMaskFill");
				
				if(gasMaskFill > 0 && this.valueCur <= 99F)
				{
					int airDrop = 100 - MathHelper.ceiling_float_int(this.valueCur);
					airDrop = gasMaskFill >= airDrop? airDrop : gasMaskFill;
					
					if(airDrop > 0)
					{
						this.valueCur += airDrop;
						tag.setInteger("gasMaskFill", (gasMaskFill - airDrop));
					}
				}
			}
		}
		
		
		if(EntityList.getEntityID(trackedEntity) > 0)
		{
			if(EM_Settings.livingProperties.containsKey(EntityList.getEntityID(trackedEntity)))
			{
				EntityProperties livingProps = EM_Settings.livingProperties.get(EntityList.getEntityID(trackedEntity));
				enableAirQ = livingProps.airQ;
			}
		}
		
		//Reset Disabled Values
		if(!EM_Settings.enableAirQ || !enableAirQ)
		{
			this.valueCur = this.valueMax;
		}
		
		
		if(trackedEntity instanceof EntityPlayer)
		{
			if(((EntityPlayer)trackedEntity).capabilities.isCreativeMode)
			{
				this.valueCur = this.prevValue;
			}
			
		}

	}

	@Override
	public void applySideEffects(EnviroData enviroData) 
	{
		EntityLivingBase trackedEntity = enviroData.entityLiving.trackedEntity;
		
		if(this.valueCur <= 0)
		{
			trackedEntity.attackEntityFrom(EnviroDamageSource.suffocate, 4.0F);
			
			trackedEntity.worldObj.playSoundAtEntity(trackedEntity, "enviromine:gag", 1f, 1f);
 		}
		
		if(this.valueCur <= 10F)
		{
			trackedEntity.addPotionEffect(new PotionEffect(Potion.digSlowdown.id, 200, 1));
			trackedEntity.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 200, 1));
		} else if(this.valueCur <= 25F)
		{
			trackedEntity.addPotionEffect(new PotionEffect(Potion.digSlowdown.id, 200, 0));
			trackedEntity.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 200, 0));
		}
		
	}

	@Override
	public void resetNumbers()
	{
		super.resetNumbers();
		this.leaves = 0F;
		this.gasAirDiff = 0;
	}








}
