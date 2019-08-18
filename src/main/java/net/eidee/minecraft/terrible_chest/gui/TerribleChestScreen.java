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
import java.util.Objects;
import javax.annotation.ParametersAreNonnullByDefault;

import com.mojang.blaze3d.platform.GlStateManager;
import mcp.MethodsReturnNonnullByDefault;
import net.eidee.minecraft.terrible_chest.inventory.container.TerribleChestContainer;
import net.eidee.minecraft.terrible_chest.network.Networks;
import net.eidee.minecraft.terrible_chest.network.message.gui.ChangePage;
import net.eidee.minecraft.terrible_chest.network.message.gui.UnlockMaxPage;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
@OnlyIn( Dist.CLIENT )
public class TerribleChestScreen
    extends ContainerScreen< TerribleChestContainer >
{
    private static final ResourceLocation GUI_TEXTURE;

    static
    {
        GUI_TEXTURE = new ResourceLocation( MOD_ID, "textures/gui/container/terrible_chest.png" );
    }

    public TerribleChestScreen( TerribleChestContainer screenContainer,
                                PlayerInventory inv,
                                ITextComponent titleIn )
    {
        super( screenContainer, inv, titleIn );
        xSize = 209;
        ySize = 182;
    }

    @Override
    protected void init()
    {
        super.init();
        addButton( new ImageButton( guiLeft + 138, guiTop + 5, 7, 11, 7, 182, 11, GUI_TEXTURE, button -> {
            int page = container.getPage();
            Networks.TERRIBLE_CHEST.sendToServer( new ChangePage( page - 1 ) );
        } ) );
        addButton( new ImageButton( guiLeft + 161, guiTop + 5, 7, 11, 0, 182, 11, GUI_TEXTURE, button -> {
            int page = container.getPage();
            Networks.TERRIBLE_CHEST.sendToServer( new ChangePage( page + 1 ) );
        } ) );
        addButton( new ImageButton( guiLeft + 181, guiTop + 8, 20, 20, 14, 182, 20, GUI_TEXTURE, button -> {
            Networks.TERRIBLE_CHEST.sendToServer( new UnlockMaxPage() );
        } ) );
    }

    @Override
    protected void handleMouseClick( Slot slotIn, int slotId, int mouseButton, ClickType type )
    {
        if ( type == ClickType.PICKUP && mouseButton == 0 )
        {
            if ( slotId >= 0 && slotId < 27 )
            {
                if ( hasAltDown() )
                {
                    super.handleMouseClick( slotIn, slotId, 2, type );
                    return;
                }
                else if ( hasControlDown() )
                {
                    super.handleMouseClick( slotIn, slotId, 3, type );
                    return;
                }
            }
            else if ( slotId >= 27 && slotId < 63 )
            {
                if ( hasControlDown() )
                {
                    super.handleMouseClick( slotIn, slotId, 3, type );
                    return;
                }
            }
        }
        else if ( type == ClickType.QUICK_MOVE && mouseButton == 0 )
        {
            if ( slotId >= 0 && slotId < 63 )
            {
                if ( hasControlDown() )
                {
                    super.handleMouseClick( slotIn, slotId, 2, type );
                    return;
                }
            }
        }
        super.handleMouseClick( slotIn, slotId, mouseButton, type );
    }

    @Override
    public void render( int p_render_1_, int p_render_2_, float p_render_3_ )
    {
        this.renderBackground();
        super.render( p_render_1_, p_render_2_, p_render_3_ );
        this.renderHoveredToolTip( p_render_1_, p_render_2_ );
    }

    @Override
    protected void drawGuiContainerForegroundLayer( int mouseX, int mouseY )
    {
        font.drawString( getTitle().getFormattedText(), 8.0F, 22.0F, 4210752 );
        font.drawString( playerInventory.getDisplayName().getFormattedText(), 8.0F, ySize - 93F, 4210752 );

        String page = ( container.getPage() + 1 ) + " / ";
        int pageWidth = font.getStringWidth( page );
        String maxPage = Integer.toString( container.getMaxPage() );
        int maxPageWidth = font.getStringWidth( maxPage );
        font.drawString( page, 166 - maxPageWidth - pageWidth, 22.0F, 4210752 );
        font.drawString( maxPage, 166 - maxPageWidth, 22.0F, 4210752 );
    }

    @Override
    protected void drawGuiContainerBackgroundLayer( float partialTicks, int mouseX, int mouseY )
    {
        GlStateManager.color4f( 1.0F, 1.0F, 1.0F, 1.0F );
        Objects.requireNonNull( minecraft ).getTextureManager().bindTexture( GUI_TEXTURE );
        blit( guiLeft, guiTop, 0, 0, xSize, ySize );

        int swapTarget = container.getSwapTarget();
        if ( swapTarget != -1 )
        {
            int slotId = swapTarget - ( container.getPage() * 27 );
            if ( slotId >= 0 && slotId < 27 )
            {
                Slot slot = container.getSlot( slotId );
                GlStateManager.disableLighting();
                GlStateManager.disableDepthTest();
                int x = guiLeft + slot.xPos;
                int y = guiTop + slot.yPos;
                GlStateManager.colorMask( true, true, true, false );
                fillGradient( x, y, x + 16, y + 16, 0x80FF0000, 0x80FF0000 );
                GlStateManager.colorMask( true, true, true, true );
                GlStateManager.enableLighting();
                GlStateManager.enableDepthTest();
            }
        }
    }

    @Override
    public void renderTooltip( List< String > textLines, int x, int y, FontRenderer font )
    {
        Slot slot = getSlotUnderMouse();
        if ( slot != null && Objects.equals( slot.inventory, container.getChestInventory() ) )
        {
            long count = container.getItemCount( slot.getSlotIndex() ) & 0xFFFFFFFFL;
            textLines.add( 1, I18n.format( "gui." + MOD_ID + ".terrible_chest.count", count ) );
        }
        super.renderTooltip( textLines, x, y, font );
    }
}
