/*
 * MIT License
 *
 * Copyright (c) 2020 EideeHi
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

import static net.eidee.minecraft.terrible_chest.TerribleChestMod.MOD_ID;

import java.util.List;
import java.util.Objects;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import mcp.MethodsReturnNonnullByDefault;
import net.eidee.minecraft.terrible_chest.inventory.container.TerribleChestContainer;
import net.eidee.minecraft.terrible_chest.network.Networks;
import net.eidee.minecraft.terrible_chest.network.message.gui.ChangePage;
import net.eidee.minecraft.terrible_chest.network.message.gui.UnlockMaxPage;
import net.eidee.minecraft.terrible_chest.settings.Config;
import net.eidee.minecraft.terrible_chest.settings.KeyBindings;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
@OnlyIn( Dist.CLIENT )
public abstract class TerribleChestScreen
    extends ContainerScreen< TerribleChestContainer >
{
    private static final ResourceLocation MULTI_PAGE_TEXTURE;
    private static final ResourceLocation SINGLE_PAGE_TEXTURE;
    private static final KeyBinding[] SORT_KEYS;

    static
    {
        MULTI_PAGE_TEXTURE = new ResourceLocation( MOD_ID, "textures/gui/container/terrible_chest_multi_page.png" );
        SINGLE_PAGE_TEXTURE = new ResourceLocation( MOD_ID, "textures/gui/container/terrible_chest_single_page.png" );

        SORT_KEYS = new KeyBinding[] { KeyBindings.SORT_0,
                                       KeyBindings.SORT_1,
                                       KeyBindings.SORT_2,
                                       KeyBindings.SORT_3,
                                       KeyBindings.SORT_4,
                                       KeyBindings.SORT_5,
                                       KeyBindings.SORT_6,
                                       KeyBindings.SORT_7,
                                       KeyBindings.SORT_8,
                                       KeyBindings.SORT_9
        };
    }

    private TerribleChestScreen( TerribleChestContainer screenContainer, PlayerInventory inv, ITextComponent titleIn )
    {
        super( screenContainer, inv, titleIn );
    }

    public static TerribleChestScreen createScreen( TerribleChestContainer screenContainer,
                                                    PlayerInventory inv,
                                                    ITextComponent titleIn )
    {
        return Config.COMMON.useSinglePageMode.get() ? new SinglePage( screenContainer, inv, titleIn )
                                                     : new MultiPage( screenContainer, inv, titleIn );
    }

    @Override
    protected void handleMouseClick( @Nullable Slot slotIn, int slotId, int mouseButton, ClickType type )
    {
        if ( type == ClickType.CLONE )
        {
            super.handleMouseClick( null, slotId, 1, type );
            return;
        }

        if ( slotIn != null )
        {
            if ( type == ClickType.PICKUP )
            {
                if ( hasAltDown() )
                {
                    if ( Objects.equals( slotIn.inventory, container.getChestInventory() ) )
                    {
                        super.handleMouseClick( slotIn, slotId, TerribleChestContainer.TYPE_SWAP, type );
                        return;
                    }
                }
                else if ( hasControlDown() )
                {
                    if ( Objects.equals( slotIn.inventory, container.getChestInventory() ) ||
                         Objects.equals( slotIn.inventory, container.getPlayerInventory() ) )
                    {
                        super.handleMouseClick( slotIn, slotId, TerribleChestContainer.TYPE_ONE_BY_ONE, type );
                        return;
                    }
                }
            }
            else if ( type == ClickType.QUICK_MOVE )
            {
                if ( Objects.equals( slotIn.inventory, container.getChestInventory() ) ||
                     Objects.equals( slotIn.inventory, container.getPlayerInventory() ) )
                {
                    if ( hasControlDown() )
                    {
                        super.handleMouseClick( slotIn, slotId, TerribleChestContainer.TYPE_MOVE_THE_SAME, type );
                        return;
                    }
                }
            }
        }

        super.handleMouseClick( slotIn, slotId, mouseButton, type );
    }

    @Override
    protected void drawGuiContainerBackgroundLayer( MatrixStack matrixStack, float partialTicks, int x, int y )
    {
        int index = container.getSwapIndex();
        Slot slot = index >= 0 && index < container.inventorySlots.size() ? container.getSlot( index ) : null;
        if ( slot != null && Objects.equals( slot.inventory, container.getChestInventory() ) )
        {
            RenderSystem.disableDepthTest();
            int xPos = guiLeft + slot.xPos;
            int yPos = guiTop + slot.yPos;
            RenderSystem.colorMask( true, true, true, false );
            fillGradient( matrixStack, xPos, yPos, xPos + 16, yPos + 16, 0x80FF0000, 0x80FF0000 );
            RenderSystem.colorMask( true, true, true, true );
            RenderSystem.enableDepthTest();
        }
    }

    @Override
    public void render( MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks )
    {
        renderBackground( matrixStack );
        super.render( matrixStack, mouseX, mouseY, partialTicks );
        renderHoveredTooltip( matrixStack, mouseX, mouseY );
    }

    @Override
    public List< ITextComponent > getTooltipFromItem( ItemStack itemStack )
    {
        List< ITextComponent > tooltip = super.getTooltipFromItem( itemStack );

        Slot slot = getSlotUnderMouse();
        if ( slot != null && Objects.equals( slot.inventory, container.getChestInventory() ) )
        {
            long count = container.getItemCount( slot.getSlotIndex() );
            String text = I18n.format( "gui.terrible_chest.terrible_chest.count", count );
            tooltip.add( 1, new StringTextComponent( text ) );
        }

        return tooltip;
    }

    @Override
    public boolean keyPressed( int keyCode, int scanCode, int modifiers )
    {
        for ( int i = 0; i < SORT_KEYS.length; i++ )
        {
            if ( SORT_KEYS[ i ].matchesKey( keyCode, scanCode ) )
            {
                super.handleMouseClick( null, 0, i, ClickType.CLONE );
                return true;
            }
        }
        return super.keyPressed( keyCode, scanCode, modifiers );
    }

    private static class MultiPage
        extends TerribleChestScreen
    {
        private final int inventoryRows;

        public MultiPage( TerribleChestContainer screenContainer, PlayerInventory inv, ITextComponent titleIn )
        {
            super( screenContainer, inv, titleIn );
            this.inventoryRows = Config.COMMON.inventoryRows.get();
            this.ySize = 132 + this.inventoryRows * 18;
            this.playerInventoryTitleY = this.ySize - 93;
        }

        @Override
        protected void init()
        {
            super.init();

            addButton( new ImageButton( guiLeft + 121, guiTop + 6, 11, 11, 187, 118, 11, MULTI_PAGE_TEXTURE, button -> {
                Networks.getChannel().sendToServer( new ChangePage( container.getCurrentPage() - 10 ) );
            } ) );
            addButton( new ImageButton( guiLeft + 134, guiTop + 6, 7, 11, 183, 96, 11, MULTI_PAGE_TEXTURE, button -> {
                Networks.getChannel().sendToServer( new ChangePage( container.getCurrentPage() - 1 ) );
            } ) );
            addButton( new ImageButton( guiLeft + 149, guiTop + 6, 7, 11, 176, 96, 11, MULTI_PAGE_TEXTURE, button -> {
                Networks.getChannel().sendToServer( new ChangePage( container.getCurrentPage() + 1 ) );
            } ) );
            addButton( new ImageButton( guiLeft + 158, guiTop + 6, 11, 11, 176, 118, 11, MULTI_PAGE_TEXTURE, button -> {
                Networks.getChannel().sendToServer( new ChangePage( container.getCurrentPage() + 10 ) );
            } ) );
            addButton( new ImageButton( guiLeft + 181, guiTop + 8, 20, 20, 176, 56, 20, MULTI_PAGE_TEXTURE, button -> {
                Networks.getChannel().sendToServer( UnlockMaxPage.INSTANCE );
            } ) );
        }

        @Override
        protected void drawGuiContainerBackgroundLayer( MatrixStack matrixStack, float partialTicks, int x, int y )
        {
            RenderSystem.color4f( 1.0F, 1.0F, 1.0F, 1.0F );
            getMinecraft().getTextureManager().bindTexture( MULTI_PAGE_TEXTURE );

            blit( matrixStack, guiLeft, guiTop, 0, 0, xSize, inventoryRows * 18 + 35 );
            blit( matrixStack, guiLeft, guiTop + inventoryRows * 18 + 35, 0, 143, xSize, 97 );
            blit( matrixStack, guiLeft + xSize, guiTop + 3, 176, 0, 33, 56 );

            super.drawGuiContainerBackgroundLayer( matrixStack, partialTicks, x, y );
        }

        @Override
        protected void drawGuiContainerForegroundLayer( MatrixStack matrixStack, int x, int y )
        {
            super.drawGuiContainerForegroundLayer( matrixStack, x, y );

            String page = ( container.getCurrentPage() + 1 ) + " / ";
            int pageWidth = font.getStringWidth( page );
            String maxPage = Integer.toString( container.getMaxPage() );
            int maxPageWidth = font.getStringWidth( maxPage );
            font.drawString( matrixStack, page, 169.0F - maxPageWidth - pageWidth, 24.0F, 4210752 );
            font.drawString( matrixStack, maxPage, 169.0F - maxPageWidth, 24.0F, 4210752 );
        }
    }

    private static class SinglePage
        extends TerribleChestScreen
    {
        public SinglePage( TerribleChestContainer screenContainer, PlayerInventory inv, ITextComponent titleIn )
        {
            super( screenContainer, inv, titleIn );
            this.xSize = 356;
            this.ySize = 238;
            this.playerInventoryTitleX = 98;
            this.playerInventoryTitleY = this.ySize - 93;
        }

        @Override
        protected void drawGuiContainerBackgroundLayer( MatrixStack matrixStack, float partialTicks, int x, int y )
        {
            RenderSystem.color4f( 1.0F, 1.0F, 1.0F, 1.0F );
            getMinecraft().getTextureManager().bindTexture( SINGLE_PAGE_TEXTURE );
            blit( matrixStack, guiLeft, guiTop, getBlitOffset(), 0, 0, xSize, ySize, 256, 512 );

            super.drawGuiContainerBackgroundLayer( matrixStack, partialTicks, x, y );
        }
    }
}
