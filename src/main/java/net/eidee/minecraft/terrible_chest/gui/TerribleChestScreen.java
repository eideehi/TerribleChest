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

import java.util.List;
import java.util.Objects;
import javax.annotation.ParametersAreNonnullByDefault;

import mcp.MethodsReturnNonnullByDefault;
import net.eidee.minecraft.terrible_chest.inventory.container.TerribleChestContainer;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
@OnlyIn( Dist.CLIENT )
public abstract class TerribleChestScreen
    extends ContainerScreen< TerribleChestContainer >
{
    public TerribleChestScreen( TerribleChestContainer container,
                                PlayerInventory playerInventory,
                                ITextComponent title )
    {
        super( container, playerInventory, title );
    }

    @Override
    protected void handleMouseClick( Slot slotIn, int slotId, int mouseButton, ClickType type )
    {
        if ( type == ClickType.PICKUP && mouseButton == 0 )
        {
            if ( slotId >= 0 && slotId < container.getChestInventory().getSizeInventory() )
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
            else if ( slotId >= container.getChestInventory().getSizeInventory() &&
                      slotId < container.getTotalSlotSize() )
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
            if ( slotId >= 0 && slotId < container.getTotalSlotSize() )
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
    public void renderTooltip( List< String > textLines, int x, int y, FontRenderer font )
    {
        Slot slot = getSlotUnderMouse();
        if ( slot != null && Objects.equals( slot.inventory, container.getChestInventory() ) )
        {
            long count = container.getData( slot.getSlotIndex() ) & 0xFFFFFFFFL;
            textLines.add( 1, I18n.format( "gui.terrible_chest.terrible_chest.count", count ) );
        }
        super.renderTooltip( textLines, x, y, font );
    }
}
