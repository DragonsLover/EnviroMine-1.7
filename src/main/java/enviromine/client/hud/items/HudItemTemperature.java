package enviromine.client.hud.items;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import enviromine.EnviroUtils;
import enviromine.Utils.Alignment;
import enviromine.Utils.RenderAssist;
import enviromine.client.Gui_EventManager;
import enviromine.client.gui.UI_Settings;
import enviromine.client.hud.HUDRegistry;
import enviromine.client.hud.HudItem;
import enviromine.core.EM_Settings;

public class HudItemTemperature extends HudItem {

	@Override
	public String getName() {
		return "Temperature";
	}

	@Override
	public String getButtonLabel() {
		return "Temperature Bar";
	}

	@Override
	public Alignment getDefaultAlignment() {
		return Alignment.BOTTOMLEFT;
	}

	@Override
	public int getDefaultPosX() {
		return 8;
	}

	@Override
	public int getDefaultPosY() {
		return (HUDRegistry.screenHeight - 30);
	}

	@Override
	public int getWidth() {
		return 64;
	}

	@Override
	public int getHeight() {
		return 8;
	}

	@Override
	public int getDefaultID() {
		return 0;
	}
	
	@Override
	public boolean isBlinking()
	{
		if(blink() && Gui_EventManager.tracker.bodyTemp < 35 || blink() && Gui_EventManager.tracker.bodyTemp > 39)
		{
			return true;
		}
		else
		{
			return false;
		}

	}
	
	@Override
	public void render()
	{
		int heatBar = MathHelper.ceiling_float_int(((Gui_EventManager.tracker.bodyTemp + 50) / 150) * this.getWidth());
		int preheatBar = MathHelper.ceiling_float_int(((Gui_EventManager.tracker.airTemp + 50) / 150) * this.getWidth());
		int preheatIco = 16- MathHelper.ceiling_float_int(((Gui_EventManager.tracker.airTemp + 50) / 150) * 16);

		float dispHeat = new BigDecimal(String.valueOf(Gui_EventManager.tracker.bodyTemp)).setScale(2, RoundingMode.DOWN).floatValue();
		float FdispHeat = new BigDecimal(String.valueOf((Gui_EventManager.tracker.bodyTemp * 1.8) + 32)).setScale(2, RoundingMode.DOWN).floatValue();

		int frameBorder = 4;
		if(this.isBlinking())
			frameBorder = 5;

		
		if(heatBar > getWidth())heatBar = getWidth();
		else if(heatBar < 0) heatBar = 0;
		
		if(preheatBar > getWidth())	preheatBar = getWidth();
		else if(preheatBar < 0)	preheatBar = 0;
		
		if(preheatIco > 24)	preheatIco = 24; 
		else if(preheatIco < 0)	preheatIco = 0;
			
		if(!UI_Settings.minimalHud)
		{
			
			//heat Bar
			RenderAssist.drawTexturedModalRect(posX, posY, 0, 24, getWidth(), getHeight());
		
			//render status update
			RenderAssist.drawTexturedModalRect(posX + preheatBar - 4, posY, 32, 64, 8, 8);
			RenderAssist.drawTexturedModalRect(posX + heatBar - 2, posY + 2, 20, 64, 4, 4);

			//Frame
			RenderAssist.drawTexturedModalRect(posX, posY, 0, getHeight() * frameBorder, getWidth(), getHeight());
		}
		
		if(UI_Settings.ShowGuiIcons == true)
		{
			int iconPosX = getIconPosX();
			// Render Icon
			RenderAssist.drawTexturedModalRect(iconPosX, posY - 4, 0, 80, 16, 16);

			// Render Icon Overlay
			if(preheatIco >= 8)
			{
				RenderAssist.drawTexturedModalRect(iconPosX, posY - 4 + preheatIco, 16, 96 + preheatIco, 16, 16-preheatIco);
			} else
			{
				RenderAssist.drawTexturedModalRect(iconPosX, posY - 4 + preheatIco, 0, 96 + preheatIco, 16, 16-preheatIco);
			}
		}
		
		if(UI_Settings.ShowText == true)
		{
				//Render Text Frame
				RenderAssist.drawTexturedModalRect( getTextPosX(), posY, 64, getHeight() * 4, 32, getHeight());

				//Render Text
				if(UI_Settings.useFarenheit == true)
				{
					Minecraft.getMinecraft().fontRenderer.drawString( FdispHeat + "F", getTextPosX(), posY, 16777215);
				} else
				{
					Minecraft.getMinecraft().fontRenderer.drawString(dispHeat + "C", getTextPosX(), posY, 16777215);
				}
		}

	}

}
