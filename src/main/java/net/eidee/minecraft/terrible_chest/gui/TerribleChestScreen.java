/*
 * MIT License
 *
 * Copyright (c) 2019 EideeHi
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package net.eidee.minecraft.terrible_chest.gui;

import static net.eidee.minecraft.terrible_chest.TerribleChest.MOD_ID;

import java.util.List;
import javax.annotation.ParametersAreNonnullByDefault;

import mcp.MethodsReturnNonnullByDefault;
import net.eidee.minecraft.terrible_chest.inventory.container.TerribleChestContainer;
import net.eidee.minecraft.terrible_chest.network.Networks;
import net.eidee.minecraft.terrible_chest.network.message.gui.ChangePage;
import net.eidee.minecraft.terrible_chest.network.message.gui.UnlockMaxPage;
import net.eidee.minecraft.terrible_chest.tileentity.TerribleChestTileEntity;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiButtonImage;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
@SideOnly( Side.CLIENT )
public class TerribleChestScreen
    extends GuiContainer
{
    private static final ResourceLocation GUI_TEXTURE;

    static
    {
        GUI_TEXTURE = new ResourceLocation( MOD_ID, "textures/gui/container/terrible_chest.png" );
    }

    private TerribleChestContainer container;

    public TerribleChestScreen( TerribleChestContainer screenContainer )
    {
        super( screenContainer );
        this.container = screenContainer;
        xSize = 209;
        ySize = 182;
    }

    @Override
    protected void actionPerformed( GuiButton button )
    {
        container.resetSwapIndex();

        switch ( button.id )
        {
            default:
                break;

            case 0:
                Networks.TERRIBLE_CHEST.sendToServer( new ChangePage( container.getCurrentPage() - 1 ) );
                break;

            case 1:
                Networks.TERRIBLE_CHEST.sendToServer( new ChangePage( container.getCurrentPage() + 1 ) );
                break;

            case 2:
                Networks.TERRIBLE_CHEST.sendToServer( new UnlockMaxPage() );
                break;
        }
    }

    @Override
    public void initGui()
    {
        super.initGui();
        addButton( new GuiButtonImage( 0, guiLeft + 138, guiTop + 5, 7, 11, 7, 182, 11, GUI_TEXTURE ) );
        addButton( new GuiButtonImage( 1, guiLeft + 161, guiTop + 5, 7, 11, 0, 182, 11, GUI_TEXTURE ) );
        addButton( new GuiButtonImage( 2, guiLeft + 181, guiTop + 8, 20, 20, 14, 182, 20, GUI_TEXTURE ) );
    }

    @Override
    protected void handleMouseClick( Slot slotIn, int slotId, int mouseButton, ClickType type )
    {
        if ( type == ClickType.PICKUP && mouseButton == 0 )
        {
            if ( slotId >= 0 && slotId < 27 )
            {
                if ( isAltKeyDown() )
                {
                    super.handleMouseClick( slotIn, slotId, TerribleChestContainer.TYPE_SWAP, type );
                    return;
                }
                else if ( isCtrlKeyDown() )
                {
                    super.handleMouseClick( slotIn, slotId, TerribleChestContainer.TYPE_ONE_BY_ONE, type );
                    return;
                }
            }
            else if ( slotId >= 27 && slotId < 63 )
            {
                if ( isCtrlKeyDown() )
                {
                    super.handleMouseClick( slotIn, slotId, TerribleChestContainer.TYPE_ONE_BY_ONE, type );
                    return;
                }
            }
        }
        else if ( type == ClickType.QUICK_MOVE && mouseButton == 0 )
        {
            if ( slotId >= 0 && slotId < 63 )
            {
                if ( isCtrlKeyDown() )
                {
                    super.handleMouseClick( slotIn, slotId, TerribleChestContainer.TYPE_MOVE_THE_SAME, type );
                    return;
                }
            }
        }
        super.handleMouseClick( slotIn, slotId, mouseButton, type );
    }

    @Override
    public void drawScreen( int mouseX, int mouseY, float partialTicks )
    {
        this.drawDefaultBackground();
        super.drawScreen( mouseX, mouseY, partialTicks );
        this.renderHoveredToolTip( mouseX, mouseY );
    }

    @Override
    protected void drawGuiContainerForegroundLayer( int mouseX, int mouseY )
    {
        String chestName = container.getChestInventory().getDisplayName().getFormattedText();
        String playerInventoryName = container.getPlayerInventory().getDisplayName().getFormattedText();

        fontRenderer.drawString( chestName, 8, 22, 4210752 );
        fontRenderer.drawString( playerInventoryName, 8, ySize - 93, 4210752 );

        String page = ( container.getCurrentPage() + 1 ) + " / ";
        int pageWidth = fontRenderer.getStringWidth( page );
        String maxPage = Integer.toString( container.getMaxPage() );
        int maxPageWidth = fontRenderer.getStringWidth( maxPage );
        fontRenderer.drawString( page, 166 - maxPageWidth - pageWidth, 22, 4210752 );
        fontRenderer.drawString( maxPage, 166 - maxPageWidth, 22, 4210752 );
    }

    @Override
    protected void drawGuiContainerBackgroundLayer( float partialTicks, int mouseX, int mouseY )
    {
        GlStateManager.color( 1.0F, 1.0F, 1.0F, 1.0F );
        mc.getTextureManager().bindTexture( GUI_TEXTURE );
        drawTexturedModalRect( guiLeft, guiTop, 0, 0, xSize, ySize );

        int swapTarget = container.getSwapIndex();
        if ( swapTarget >= 0 && swapTarget < 27 )
        {
            Slot slot = inventorySlots.getSlot( swapTarget );
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            int x = guiLeft + slot.xPos;
            int y = guiTop + slot.yPos;
            GlStateManager.colorMask( true, true, true, false );
            drawGradientRect( x, y, x + 16, y + 16, 0x80FF0000, 0x80FF0000 );
            GlStateManager.colorMask( true, true, true, true );
            GlStateManager.enableLighting();
            GlStateManager.enableDepth();
        }
    }

    @Override
    protected void drawHoveringText( List< String > textLines, int x, int y, FontRenderer font )
    {
        Slot slot = getSlotUnderMouse();
        if ( slot != null )
        {
            if ( slot.slotNumber >= 0 && slot.slotNumber < 27 )
            {
                long count = container.getItemCount( slot.getSlotIndex() );
                textLines.add( 1, I18n.format( "gui." + MOD_ID + ".terrible_chest.count", count ) );
            }
        }
        super.drawHoveringText( textLines, x, y, font );
    }
}
