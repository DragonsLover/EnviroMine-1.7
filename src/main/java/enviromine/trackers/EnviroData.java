package enviromine.trackers;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.apache.logging.log4j.Level;

import cpw.mods.fml.common.registry.EntityRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.FoodStats;
import net.minecraft.util.MathHelper;
import net.minecraft.village.Village;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import enviromine.core.EM_Settings;
import enviromine.core.EnviroMine;
import enviromine.handlers.EM_PhysManager;
import enviromine.handlers.EM_StatusManager;
import enviromine.handlers.EnviroAchievements;
import enviromine.trackers.properties.BiomeProperties;
import enviromine.trackers.properties.BlockProperties;
import enviromine.trackers.properties.DimensionProperties;
import enviromine.trackers.properties.EntityProperties;
import enviromine.trackers.properties.ItemProperties;
import enviromine.utils.EnviroUtils;

public class EnviroData 
{
	public int solidBlocks;
	public Boolean nearLava;
	
	public float dayPercent;
	
	public NewEnviroDataTracker entityLiving;
	
	public boolean isDayTime;
	public boolean isRaining;
	public boolean isInShade;
	public boolean isHellWorld;
	
	public int validEntities = 0;
	
	public int lightLev;
	public int blockLightLev;
	

	private int i;
	private int j;
	private int k;
	
	public int range;
	
	Chunk chunk;
	BiomeGenBase biome;
	public BiomeProperties biomeProp;
	public DimensionProperties dimensionProp = null;
	
	public EnviroData(NewEnviroDataTracker entityLiving)
	{
		this.entityLiving = entityLiving;
		
		i = MathHelper.floor_double(entityLiving.trackedEntity.posX);
		j = MathHelper.floor_double(entityLiving.trackedEntity.posY);
		k = MathHelper.floor_double(entityLiving.trackedEntity.posZ);
		
		lightLev = 0;
		blockLightLev = 0;
		
		range = 5;
		
		isRaining = entityLiving.trackedEntity.worldObj.isRaining();
		isInShade = entityLiving.trackedEntity.worldObj.canBlockSeeTheSky( MathHelper.floor_double(entityLiving.trackedEntity.posX),  MathHelper.floor_double(entityLiving.trackedEntity.posY),  MathHelper.floor_double(entityLiving.trackedEntity.posZ));
		isDayTime = entityLiving.trackedEntity.worldObj.isDaytime();
		
		
		//Note: This is offset slightly so that heat peaks after noon.
		float scale = 1.25F; // Anything above 1 forces the maximum and minimum temperatures to plateau when they're reached noon
		dayPercent = MathHelper.clamp_float((float)(Math.sin(Math.toRadians(((entityLiving.trackedEntity.worldObj.getWorldTime()%24000L)/24000D)*360F - 30F))*0.5F + 0.5F)*scale, 0F, 1F);


	}
	
	
	
	public void Run()
	{
		System.out.println("Running EnviroData");
		
		this.entityLiving.airQuality.resetNumbers();
		
		System.out.println("EMD-GetData");
		this.getData();
		
		System.out.println("EMD-GetSurroundingData");
		//Gets Blocks and MobList // Villager Assistance
		this.getSurroundingData(range);
		
		System.out.println("EMD-CheckEntitys");
		//Runs Inventory Checks
		this.checkEntity();
		
		this.entityLiving.airQuality.fixValueBounds();
		
	}
	
	
	private void getData()
	{

		if(entityLiving.trackedEntity.worldObj == null)
		{
			return;
		}
		
		
		// Null Checks
		if((chunk = entityLiving.trackedEntity.worldObj.getChunkFromBlockCoords(i, k)) == null) return;

		if((biome = chunk.getBiomeGenForWorldCoords(i & 15, k & 15, entityLiving.trackedEntity.worldObj.getWorldChunkManager())) == null) return;
	
		//Grabs custom Dimension Properties
		if(EM_Settings.dimensionProperties.containsKey(entityLiving.trackedEntity.worldObj.provider.dimensionId))
		{ 
			dimensionProp = EM_Settings.dimensionProperties.get(entityLiving.trackedEntity.worldObj.provider.dimensionId);
		}
		
		//Get biome properties
		if(BiomeProperties.base.hasProperty(biome))
		{
			biomeProp = BiomeProperties.base.getProperty(biome);
		}
		
		
		// gets light levels around player
		if(j > 0)
		{
			if(j >= 256)
			{
				lightLev = 15;
				blockLightLev = 15;
			} else
			{
				lightLev = chunk.getSavedLightValue(EnumSkyBlock.Sky, i & 0xf, j, k & 0xf);
				blockLightLev = chunk.getSavedLightValue(EnumSkyBlock.Block, i & 0xf, j, k & 0xf);
			}
		}
		
		if(entityLiving.trackedEntity.worldObj.provider.hasNoSky)
		{
			isDayTime = false;
		}
		

	}
	/**
	 * Grabs Block Data for all 
	 * @param entityLiving
	 * @param range
	 */
	private void getSurroundingData(int range)
	{
		float dist = 0;
		
		Chunk checkChunk;
		BiomeGenBase checkBiome;
	
		for(int x = -range; x <= range; x++)
		{
			for(int y = -range; y <= range; y++)
			{
				for(int z = -range; z <= range; z++)
				{
					checkChunk = entityLiving.trackedEntity.worldObj.getChunkFromBlockCoords((i + x), (k + z));
					checkBiome = checkChunk.getBiomeGenForWorldCoords((i + x) & 15, (k + z) & 15, entityLiving.trackedEntity.worldObj.getWorldChunkManager());
					BiomeProperties biomeOverride = null;
					
					if(y == 0)
					{

						if(BiomeProperties.base.hasProperty(checkBiome))
						{
							biomeOverride = BiomeProperties.base.getProperty(checkBiome);
						}

					}
						//TODO Move to just trackers
						dist = (float)entityLiving.trackedEntity.getDistance(i + x, j + y, k + z);

						Block block = Blocks.air;
						int meta = 0;
					
						block = entityLiving.trackedEntity.worldObj.getBlock(i + x, j + y, k + z);
						if(block != Blocks.air)
						{
							meta = entityLiving.trackedEntity.worldObj.getBlockMetadata(i + x, j + y, k + z);
						}
			
						if(BlockProperties.base.hasProperty(block, meta))
						{
							BlockProperties blockProps = BlockProperties.base.getProperty(block, meta);
							

							// Hard Coded
							entityLiving.airQuality.getSurroundingData(this, block, meta, range, blockProps, biomeOverride);
							//entityLiving.bodyTemp.LoopSurroundingData(entityLiving.trackedEntity);
							//entityLiving.sanity.LoopSurroundingData(entityLiving.trackedEntity);
							//entityLiving.hydration.LoopSurroundingData(entityLiving.trackedEntity);
							
							//TODO Than call rest and customs
							
						}
						
						if(block.getMaterial() == Material.lava)
						{
							nearLava = true;
						}
					
				}
			}
		}
	}
	
	public void GetNearByMobs()
	{
		/**
		 * Mob Data
		 */
		List mobList = entityLiving.trackedEntity.worldObj.getEntitiesWithinAABBExcludingEntity(entityLiving.trackedEntity, AxisAlignedBB.getBoundingBox(entityLiving.trackedEntity.posX - 2, entityLiving.trackedEntity.posY - 2, entityLiving.trackedEntity.posZ - 2, entityLiving.trackedEntity.posX + 3, entityLiving.trackedEntity.posY + 3, entityLiving.trackedEntity.posZ + 3));
		
		Iterator iterator = mobList.iterator();
		
		int validEntities = 0;
		
		EnviroDataTracker tracker = EM_StatusManager.lookupTracker(entityLiving.trackedEntity);
		
		if(tracker == null)
		{
			EnviroMine.logger.log(Level.ERROR, "Tracker updating as null! Crash imminent!");
		}
		
		while(iterator.hasNext())
		{
			Entity mob = (Entity)iterator.next();
			
			if(!(mob instanceof EntityLivingBase))
			{
				continue;
			}
			
			NewEnviroDataTracker mobTrack = EM_StatusManager.lookupTrackerNew((EntityLivingBase)mob);
			EntityProperties livingProps = null;
			
			if(EntityList.getEntityID(mob) > 0)
			{
				if(EM_Settings.livingProperties.containsKey(EntityList.getEntityID(mob)))
				{
					livingProps = EM_Settings.livingProperties.get(EntityList.getEntityID(mob));
				}
			} else if(EntityRegistry.instance().lookupModSpawn(mob.getClass(), false) != null)
			{
				if(EM_Settings.livingProperties.containsKey(EntityRegistry.instance().lookupModSpawn(mob.getClass(), false).getModEntityId() + 128))
				{
					livingProps = EM_Settings.livingProperties.get(EntityRegistry.instance().lookupModSpawn(mob.getClass(), false).getModEntityId() + 128);
				}
			}
			
			//TODO Send to MobfoundNear
			this.entityLiving.airQuality.mobFoundNear(this, mob, mobTrack, livingProps);
			
			if(mob instanceof EntityVillager && entityLiving.trackedEntity instanceof EntityPlayer && entityLiving.trackedEntity.canEntityBeSeen(mob) && EM_Settings.villageAssist)
			{
				VillagerAssistance(mob);
			}
			
		}
	}
	
	/**
	 * Checks Entity Type and Runs Proper Methods
	 */
	private void checkEntity()
	{
		if(entityLiving.trackedEntity instanceof EntityPlayer)
		{
			this.entityLiving.airQuality.ifEntityPlayer(this, (EntityPlayer)this.entityLiving.trackedEntity, biomeProp, dimensionProp);
			
			InventoryCheck((EntityPlayer)entityLiving.trackedEntity);
			
			GetNearByMobs();
			
		}else if(entityLiving.trackedEntity instanceof EntityAnimal)
		{
			if(EntityProperties.base.hasProperty(entityLiving.trackedEntity))
			{
				EntityProperties.base.getProperty(entityLiving.trackedEntity);
			}
			
			this.entityLiving.airQuality.ifEntityAnimal(this, (EntityAnimal)entityLiving.trackedEntity, null, biomeProp, dimensionProp);
		}
	}
	
	private void InventoryCheck(EntityPlayer player)
	{
		for(int slot = 0; slot < 9; slot++)
		{
			ItemStack stack = player.inventory.mainInventory[slot];
			
			if(stack == null)
			{
				continue;
			}
			
			float stackMult = 1F;
			
			if(stack.stackSize > 1)
			{
				stackMult = (stack.stackSize-1F)/63F + 1F;
			}
			
			if(EM_Settings.itemProperties.containsKey("" + Item.itemRegistry.getNameForObject(stack.getItem()) + "," + stack.getItemDamage()) || EM_Settings.itemProperties.containsKey("" + Item.itemRegistry.getNameForObject(stack.getItem())))
			{
				ItemProperties itemProps;
				
				if(EM_Settings.itemProperties.containsKey("" + Item.itemRegistry.getNameForObject(stack.getItem()) + "," + stack.getItemDamage()))
				{
					itemProps = EM_Settings.itemProperties.get("" + Item.itemRegistry.getNameForObject(stack.getItem()) + "," + stack.getItemDamage());
				} else
				{
					itemProps = EM_Settings.itemProperties.get("" + Item.itemRegistry.getNameForObject(stack.getItem()));
				}
				
				//TODO Call methods
				this.entityLiving.airQuality.inventorySearch(this, stack, stackMult, itemProps);
			}
			//TODO I dont think this ever gets called...
			else if(stack.getItem() instanceof ItemBlock)
			{
				ItemBlock itemBlock = (ItemBlock)stack.getItem();
				
				//TODO Call Trackers
			}

		}
	}
	

	
	private void VillagerAssistance(Entity mob)
	{

			EntityVillager villager = (EntityVillager)mob;
			Village village = entityLiving.trackedEntity.worldObj.villageCollectionObj.findNearestVillage(MathHelper.floor_double(villager.posX), MathHelper.floor_double(villager.posY), MathHelper.floor_double(villager.posZ), 32);
			
			long assistTime = villager.getEntityData().getLong("Enviro_Assist_Time");
			long worldTime = entityLiving.trackedEntity.worldObj.provider.getWorldTime();
			
			EntityProperties entityProp = EntityProperties.base.hasProperty((EntityLivingBase)mob) ? EntityProperties.base.getProperty((EntityLivingBase)mob) : null;
			
			if(village != null && village.getReputationForPlayer(entityLiving.trackedEntity.getCommandSenderName()) >= 5 && !villager.isChild() && Math.abs(worldTime - assistTime) > 24000)
			{

					//TODO 
				this.entityLiving.airQuality.villageAssist(this, villager, village, entityProp);
			}
	}
	
	
}
