package enviromine.trackers.items;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityAnimal;

public class HydrationTracker extends EnviroTrackerItem{

	@Override
	public int getDefaultID() {
		// TODO Auto-generated method stub
		return 3;
	}

	@Override
	public float getStartValue() {
		// TODO Auto-generated method stub
		return 100;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Hydration";
	}

	@Override
	public void LoopSurroundingData(EntityLivingBase trackedEntity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void EntityPlayerAction(float[] enviroData,
			EntityLivingBase trackedEntity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void EntityAnimalAction(float[] enviroData,
			EntityAnimal trackedEntity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void SpecialAction(float[] enviroData, EntityLivingBase trackedEntity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void Tick_SideEffects(EntityLivingBase trackedEntity) {
		// TODO Auto-generated method stub
		
	}

}
