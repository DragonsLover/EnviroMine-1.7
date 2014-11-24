package enviromine.client.hud.items;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import enviromine.EnviroUtils;
import enviromine.client.Gui_EventManager;
import enviromine.client.gui.UI_Settings;
import enviromine.client.hud.HUDRegistry;
import enviromine.client.hud.HudItem;
import enviromine.client.hud.OverlayHandler;
import enviromine.client.hud.OverlayHandler.Overlay;
import enviromine.utils.Alignment;
import enviromine.utils.RenderAssist;

public class HudItemHydration extends HudItem	{

	@Override
	public String getName() {

		return "Hydration";
	}

	@Override
	public String getButtonLabel() {

		return "Hydration Bar";
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

		return (HUDRegistry.screenHeight - 15);
	}

	@Override
	public int getWidth() {

		return UI_Settings.minimalHud ? 0 : 64;
	}

	@Override
	public int getHeight() {

		return 8;
	}

	@Override
	public boolean isBlinking() {

		if(blink() && Gui_EventManager.tracker.hydration < 25)
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	@Override
	public int getDefaultID() {

		return 1;
	}

	@Override
	public void render() 
	{
		GL11.glPushMatrix();	
		int waterBar = MathHelper.ceiling_float_int((Gui_EventManager.tracker.hydration / 100) * this.getWidth());

		int frameBorder = 4;
		if(this.isBlinking())
			frameBorder = 5;
		
		if(waterBar > this.getWidth())
		{
			waterBar = this.getWidth();
		} else if(waterBar < 0)
		{
			waterBar = 0;
		}
		
		if(!UI_Settings.minimalHud)
		{
			int angle = 90;
			GL11.glPushMatrix();

			if(this.rotated)
			{
				GL11.glTranslatef(posX,posY, 0);
				GL11.glRotatef( angle, 0, 0, 1 );
				GL11.glTranslatef(-posX+(this.getHeight()/2),-posY -(this.getWidth()/2) -this.getHeight(), 0);
			}
			
			//Bar
			RenderAssist.drawTexturedModalRect(posX, posY, 0, 0, getWidth(), getHeight());
			RenderAssist.drawTexturedModalRect(posX, posY, 64, 0, waterBar, getHeight());
			
			//render status update
			RenderAssist.drawTexturedModalRect(posX + waterBar - 2, posY + 2, 16, 64, 4, 4);

			//Frame
			RenderAssist.drawTexturedModalRect(posX, posY, 0, getHeight() * frameBorder, getWidth(), getHeight());

			
			
			GL11.glPopMatrix();
		}
		
		if(UI_Settings.ShowGuiIcons == true)
		{
			int iconPosX = getIconPosX();
			// Render Icon
			RenderAssist.drawTexturedModalRect(iconPosX, posY - 4, 16, 80, 16, 16);
		}
		
		if(UI_Settings.ShowText == true)
		{
				//Render Text Frame
				RenderAssist.drawTexturedModalRect( getTextPosX(), posY, 64, getHeight() * 4, 32, getHeight());

				//Render Text
				RenderAssist.drawTexturedModalRect(getTextPosX(), posY, 64, getHeight() * 4, 32, getHeight());
				Minecraft.getMinecraft().fontRenderer.drawString(Gui_EventManager.tracker.hydration + "%", getTextPosX(), posY, 16777215);
		}
		GL11.glPopMatrix();
	}

	@Override
	public ResourceLocation BindResource() {
		// TODO Auto-generated method stub
		return Gui_EventManager.guiResource;
	}

	@Override
	public void renderScreenOverlay(int scaledwidth, int scaledheight) {
		if(Gui_EventManager.tracker.bodyTemp >= 39)
		{
			int grad = 0;
			if(Gui_EventManager.tracker.bodyTemp >= 41F)
			{
				grad = 210;
			} else
			{
				grad = (int)((1F - (Math.abs(3 - (Gui_EventManager.tracker.bodyTemp - 39)) / 3)) * 96);
			}
			EnviroUtils.drawScreenOverlay(scaledwidth, scaledheight, EnviroUtils.getColorFromRGBA(255, 255, 255, grad));
			
		} 
	}




}
