package enviromine.trackers;

import java.util.Iterator;
import java.util.List;

import org.apache.logging.log4j.Level;

import cpw.mods.fml.common.registry.EntityRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
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
import enviromine.trackers.items.EnviroDataManager;
import enviromine.trackers.properties.BiomeProperties;
import enviromine.trackers.properties.BlockProperties;
import enviromine.trackers.properties.DimensionProperties;
import enviromine.trackers.properties.EntityProperties;
import enviromine.trackers.properties.ItemProperties;

public class EnviroData 
{
	public int solidBlocks;
	public Boolean nearLava;
	
	public int lightLev;
	public int blockLightLev;
	public float dayPercent;
	
	EnviroDataManager entityLiving;
	
	public boolean isDayTime;
	public boolean isRaining;
	public boolean isShade;
	

	public EnviroData(EnviroDataManager entityLiving)
	{
		this.entityLiving = entityLiving;
		
		isDayTime = entityLiving.trackedEntity.worldObj.isDaytime();
		
		if(entityLiving.trackedEntity.worldObj.provider.hasNoSky)
		{
			isDayTime = false;
		}

		isRaining = entityLiving.trackedEntity.worldObj.isRaining();
		isShade = entityLiving.trackedEntity.worldObj.canBlockSeeTheSky( MathHelper.floor_double(entityLiving.trackedEntity.posX),  MathHelper.floor_double(entityLiving.trackedEntity.posY),  MathHelper.floor_double(entityLiving.trackedEntity.posZ));
		
		//Note: This is offset slightly so that heat peaks after noon.
		float scale = 1.25F; // Anything above 1 forces the maximum and minimum temperatures to plateau when they're reached
		dayPercent = MathHelper.clamp_float((float)(Math.sin(Math.toRadians(((entityLiving.trackedEntity.worldObj.getWorldTime()%24000L)/24000D)*360F - 30F))*0.5F + 0.5F)*scale, 0F, 1F);
		
		Run();
	}
	
	
	
	private void Run()
	{
		
		this.getData();
		
		//Gets Blocks and MobList // Villager Assistance
		this.getSurroundingData(5);
		
		//Runs Inventory Checks
		this.EntityPlayerCheck();
		
	}
	
	
	private void getData()
	{
		int i = MathHelper.floor_double(entityLiving.trackedEntity.posX);
		int j = MathHelper.floor_double(entityLiving.trackedEntity.posY);
		int k = MathHelper.floor_double(entityLiving.trackedEntity.posZ);
		
		if(entityLiving.trackedEntity.worldObj == null)
		{
			return;
		}
		
		Chunk chunk = entityLiving.trackedEntity.worldObj.getChunkFromBlockCoords(i, k);
		
		if(chunk == null)
		{
			return;
		}
		
		BiomeGenBase biome = chunk.getBiomeGenForWorldCoords(i & 15, k & 15, entityLiving.trackedEntity.worldObj.getWorldChunkManager());
		
		if(biome == null)
		{
			return;
		}
		
		DimensionProperties dimensionProp = null;
		if(EM_Settings.dimensionProperties.containsKey(entityLiving.trackedEntity.worldObj.provider.dimensionId))
		{ 
			dimensionProp = EM_Settings.dimensionProperties.get(entityLiving.trackedEntity.worldObj.provider.dimensionId);
		}
		

		if(entityLiving.trackedEntity.worldObj.provider.hasNoSky)
		{
			isDayTime = false;
		}
		
		int lightLev = 0;
		int blockLightLev = 0;
		
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
		

	}
	/**
	 * Grabs Block Data for all 
	 * @param entityLiving
	 * @param range
	 */
	private void getSurroundingData(int range)
	{
		int i = MathHelper.floor_double(entityLiving.trackedEntity.posX);
		int j = MathHelper.floor_double(entityLiving.trackedEntity.posY);
		int k = MathHelper.floor_double(entityLiving.trackedEntity.posZ);
		
		float dist = 0;
		
		Chunk checkChunk;
		BiomeGenBase checkBiome;
	
		for(int x = -range; x <= range; x++)
		{
			for(int y = -range; y <= range; y++)
			{
				for(int z = -range; z <= range; z++)
				{
					if(y == 0)
					{
						checkChunk = entityLiving.trackedEntity.worldObj.getChunkFromBlockCoords((i + x), (k + z));
						checkBiome = checkChunk.getBiomeGenForWorldCoords((i + x) & 15, (k + z) & 15, entityLiving.trackedEntity.worldObj.getWorldChunkManager());
						
						BiomeProperties biomeOverride = null;
						if(EM_Settings.biomeProperties.containsKey(checkBiome.biomeID))
						{
							biomeOverride = EM_Settings.biomeProperties.get(checkBiome.biomeID);
						}
						
						if(!EM_PhysManager.blockNotSolid(entityLiving.trackedEntity.worldObj, x + i, y + j, z + k, false))
						{
							solidBlocks += 1;
						}
						
						dist = (float)entityLiving.trackedEntity.getDistance(i + x, j + y, k + z);

						Block block = Blocks.air;
						int meta = 0;
						
						block = entityLiving.trackedEntity.worldObj.getBlock(i + x, j + y, k + z);
						
						if(block != Blocks.air)
						{
							meta = entityLiving.trackedEntity.worldObj.getBlockMetadata(i + x, j + y, k + z);
						}
			
						if(EM_Settings.blockProperties.containsKey("" + Block.blockRegistry.getNameForObject(block) + "," + meta) || EM_Settings.blockProperties.containsKey("" + Block.blockRegistry.getNameForObject(block)))
						{
							BlockProperties blockProps = null;
							
							if(EM_Settings.blockProperties.containsKey("" + Block.blockRegistry.getNameForObject(block) + "," + meta))
							{
								blockProps = EM_Settings.blockProperties.get("" + Block.blockRegistry.getNameForObject(block) + "," + meta);
							} else
							{
								blockProps = EM_Settings.blockProperties.get("" + Block.blockRegistry.getNameForObject(block));
							}
							
							entityLiving.airQuality.LoopSurroundingData(entityLiving.trackedEntity);
							
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
		
		
		
		/**
		 * Mob Data
		 */
	List mobList = entityLiving.trackedEntity.worldObj.getEntitiesWithinAABBExcludingEntity(entityLiving.trackedEntity, AxisAlignedBB.getBoundingBox(entityLiving.trackedEntity.posX - 2, entityLiving.trackedEntity.posY - 2, entityLiving.trackedEntity.posZ - 2, entityLiving.trackedEntity.posX + 3, entityLiving.trackedEntity.posY + 3, entityLiving.trackedEntity.posZ + 3));
		
		Iterator iterator = mobList.iterator();
		
		float avgEntityTemp = 0.0F;
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
			
			EnviroDataTracker mobTrack = EM_StatusManager.lookupTracker((EntityLivingBase)mob);
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
			
		}
	
	}
	
	private void EntityPlayerCheck()
	{
		if(entityLiving.trackedEntity instanceof EntityPlayer)
		{
			InventoryCheck((EntityPlayer)entityLiving.trackedEntity);
			
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
				
				//TODO Call Trackers
			}
			else if(stack.getItem() instanceof ItemBlock)
			{
				ItemBlock itemBlock = (ItemBlock)stack.getItem();
				
				//TODO Call Trackers
			}

		}
	}
	
	
	private void EntityMobAction()
	{
		
	}
	
	private void VillagerAssistance(EntityLivingBase mob)
	{
		if(mob instanceof EntityVillager && entityLiving.trackedEntity instanceof EntityPlayer && entityLiving.trackedEntity.canEntityBeSeen(mob) && EM_Settings.villageAssist)
		{
			EntityVillager villager = (EntityVillager)mob;
			Village village = entityLiving.trackedEntity.worldObj.villageCollectionObj.findNearestVillage(MathHelper.floor_double(villager.posX), MathHelper.floor_double(villager.posY), MathHelper.floor_double(villager.posZ), 32);
			
			long assistTime = villager.getEntityData().getLong("Enviro_Assist_Time");
			long worldTime = entityLiving.trackedEntity.worldObj.provider.getWorldTime();
			
			if(village != null && village.getReputationForPlayer(entityLiving.trackedEntity.getCommandSenderName()) >= 5 && !villager.isChild() && Math.abs(worldTime - assistTime) > 24000)
			{

					//TODO 
			}
		}
	}
	
	
}
