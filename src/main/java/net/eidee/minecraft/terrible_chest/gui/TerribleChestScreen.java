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

import io.netty.util.collection.IntObjectHashMap;
import io.netty.util.collection.IntObjectMap;
import mcp.MethodsReturnNonnullByDefault;
import net.eidee.minecraft.terrible_chest.config.KeyBindings;
import net.eidee.minecraft.terrible_chest.inventory.container.TerribleChestContainer;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
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
    private static final IntObjectMap< KeyBinding > sortKeys;

    static
    {
        IntObjectHashMap< KeyBinding > map = new IntObjectHashMap<>();
        map.put( 0, KeyBindings.SORT_0 );
        map.put( 1, KeyBindings.SORT_1 );
        map.put( 2, KeyBindings.SORT_2 );
        map.put( 3, KeyBindings.SORT_3 );
        map.put( 4, KeyBindings.SORT_4 );
        map.put( 5, KeyBindings.SORT_5 );
        map.put( 6, KeyBindings.SORT_6 );
        map.put( 7, KeyBindings.SORT_7 );
        map.put( 8, KeyBindings.SORT_8 );
        map.put( 9, KeyBindings.SORT_9 );
        sortKeys = map;
    }

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
            if ( slotId >= 0 &&
                 slotId <
                 container.getChestInventory()
                          .getSizeInventory() )
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
            else if ( slotId >=
                      container.getChestInventory()
                               .getSizeInventory() && slotId < container.getTotalSlotSize() )
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
        else if ( type == ClickType.CLONE )
        {
            super.handleMouseClick( slotIn, slotId, 1, type );
            return;
        }
        super.handleMouseClick( slotIn, slotId, mouseButton, type );
    }

    @Override
    public boolean keyPressed( int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_ )
    {
        Iterable< IntObjectMap.PrimitiveEntry< KeyBinding > > entries = sortKeys.entries();
        for ( IntObjectMap.PrimitiveEntry< KeyBinding > entry : entries )
        {
            KeyBinding keyBinding = entry.value();
            if ( keyBinding.matchesKey( p_keyPressed_1_, p_keyPressed_2_ ) )
            {
                super.handleMouseClick( null, 0, entry.key(), ClickType.CLONE );
                return true;
            }
        }
        return super.keyPressed( p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_ );
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
