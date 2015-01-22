package enviromine.trackers;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;

import enviromine.core.EnviroMine;
import enviromine.trackers.items.EnviroTrackerItem;

public class RegisterTracker {
	
    protected static List<EnviroTrackerItem> TrackerItemList = new ArrayList<EnviroTrackerItem>();
    protected static boolean initialLoadComplete = false;
    protected static List<EnviroTrackerItem> TrackerItemListActive = new ArrayList<EnviroTrackerItem>();
    
    private static Logger log = EnviroMine.logger;

	   public static void registerTrackerItem(EnviroTrackerItem trackerItem) {
	        if (trackerItem.getDefaultID() <= 25 && initialLoadComplete)
	        {
	            log.info("Rejecting " + trackerItem.getName() + " due to invalid ID.");
	        }
	        if (!TrackerItemList.contains(trackerItem)) 
	        {
	            TrackerItemList.add(trackerItem);
	            //System.out.println(TrackerItem.getName() +":"+ TrackerItem.isEnabledByDefault());
	            if (trackerItem.isEnabledByDefault()) 
	            {
	                enableTrackerItem(trackerItem);
	            }
	        }
	    }

	    public static List<EnviroTrackerItem> getTrackerItemList() {
	        return TrackerItemList;
	    }

	    public static void enableTrackerItem(EnviroTrackerItem trackerItem) 
	    {
	        if (TrackerItemList.contains(trackerItem) && !TrackerItemListActive.contains(trackerItem)) 
	        {
	        	//System.out.println(TrackerItem.getName() +" is Active now");
	            TrackerItemListActive.add(trackerItem);
	        }
	    }

	    public static void disableTrackerItem(EnviroTrackerItem trackerItem) 
	    {
	        TrackerItemListActive.remove(trackerItem);
	    }

	    public static List<EnviroTrackerItem> getActiveTrackerItemList() 
	    {
	        return TrackerItemListActive;
	    }

	    public static boolean isActiveTrackerItem(EnviroTrackerItem trackerItem) 
	    {
	        return getActiveTrackerItemList().contains(trackerItem);
	    }


	    public static EnviroTrackerItem getTrackerItemByID(int id) 
	    {
	        for (EnviroTrackerItem Trackeritem : getTrackerItemList()) 
	        {
	            if (id == Trackeritem.getDefaultID())
	                return Trackeritem;
	        }
	        return null;
	    }
}
