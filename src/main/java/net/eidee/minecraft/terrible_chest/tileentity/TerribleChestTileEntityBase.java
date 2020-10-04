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

package net.eidee.minecraft.terrible_chest.tileentity;

import java.util.Objects;
import java.util.Set;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.collect.Sets;

import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntSet;
import mcp.MethodsReturnNonnullByDefault;
import net.eidee.minecraft.terrible_chest.config.Config;
import net.eidee.minecraft.terrible_chest.inventory.ExtraInventoryData;
import net.eidee.minecraft.terrible_chest.inventory.TerribleChestInventory;
import net.eidee.minecraft.terrible_chest.inventory.container.TerribleChestContainer;
import net.eidee.minecraft.terrible_chest.item.ItemStackContainer;
import net.eidee.minecraft.terrible_chest.item.TerribleChestItemHandler;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public interface TerribleChestTileEntityBase
    extends ITickable
{
    TileEntity getTileEntity();

    @Nullable
    TerribleChestItemHandler getTerribleChestItemHandler();

    @Nullable
    TerribleChestInventory getTerribleChestInventory();

    ExtraInventoryData getExtraInventoryData();

    long getLastItemTransferTime();

    void setLastItemTransferTime( long time );

    @Nullable
    default TerribleChestContainer createContainer( InventoryPlayer inventoryPlayer )
    {
        TerribleChestInventory inventory = getTerribleChestInventory();
        ExtraInventoryData extraInventoryData = getExtraInventoryData();
        if ( inventory != null )
        {
            return new TerribleChestContainer( inventoryPlayer, inventory, extraInventoryData );
        }
        return null;
    }

    default Set< IItemHandler > getNeighborItemHandlers( EnumFacing... sides )
    {
        Set< IItemHandler > set = Sets.newHashSet();

        TileEntity tileEntity = getTileEntity();
        World world = Objects.requireNonNull( tileEntity.getWorld() );
        BlockPos pos = tileEntity.getPos();

        for ( EnumFacing direction : EnumFacing.HORIZONTALS )
        {
            TileEntity neighbor = world.getTileEntity( pos.offset( direction ) );
            if ( neighbor == null )
            {
                continue;
            }
            for ( EnumFacing side : sides )
            {
                IItemHandler handler = neighbor.getCapability( CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side );
                if ( handler != null )
                {
                    set.add( handler );
                }
            }
        }

        return set;
    }

    default void itemDeliver()
    {
        TerribleChestItemHandler handler = getTerribleChestItemHandler();
        if ( handler == null )
        {
            return;
        }

        for ( int i = 0; i < handler.getSlots(); i++ )
        {
            ItemStackContainer containerInSlot = handler.getContainerInSlot( i );
            if ( containerInSlot.isEmpty() )
            {
                continue;
            }

            int size = ( int )Math.max( 1, Math.min( containerInSlot.getCount(), Config.transferStackCount ) );
            ItemStack stackInSlot = ItemHandlerHelper.copyStackWithSize( containerInSlot.getStack(), size );
            for ( IItemHandler neighbor : getNeighborItemHandlers( EnumFacing.VALUES ) )
            {
                for ( int j = 0; j < neighbor.getSlots(); j++ )
                {
                    if ( ItemHandlerHelper.canItemStacksStack( stackInSlot, neighbor.getStackInSlot( j ) ) )
                    {
                        ItemStack result = neighbor.insertItem( j, stackInSlot.copy(), false );
                        int insertCount = stackInSlot.getCount() - result.getCount();
                        if ( insertCount > 0 )
                        {
                            handler.extractItem( i, insertCount, false );
                            return;
                        }
                    }
                }
            }
        }
    }

    default void itemCollection()
    {
        TerribleChestItemHandler handler = getTerribleChestItemHandler();
        if ( handler == null )
        {
            return;
        }

        for ( IItemHandler neighbor : getNeighborItemHandlers( EnumFacing.DOWN ) )
        {
            for ( int i = 0; i < neighbor.getSlots(); i++ )
            {
                ItemStack neighborStack = neighbor.getStackInSlot( i );
                if ( neighborStack.isEmpty() )
                {
                    continue;
                }

                int size = Math.max( 1, Math.min( neighborStack.getCount(), Config.transferStackCount ) );
                neighborStack = ItemHandlerHelper.copyStackWithSize( neighborStack, size );

                for ( int j = 0; j < handler.getSlots(); j++ )
                {
                    ItemStackContainer containerInSlot = handler.getContainerInSlot( j );
                    if ( ItemHandlerHelper.canItemStacksStack( neighborStack, containerInSlot.getStack() ) )
                    {
                        ItemStack result = handler.insertItem( j, neighborStack.copy(), false );
                        int insertCount = neighborStack.getCount() - result.getCount();
                        if ( insertCount > 0 )
                        {
                            neighbor.extractItem( i, insertCount, false );
                            return;
                        }
                    }
                }
            }
        }
    }

    @Override
    default void update()
    {
        if ( !Config.stopItemCollectionAndDeliver )
        {
            TileEntity tileEntity = getTileEntity();
            World world = Objects.requireNonNull( tileEntity.getWorld() );
            if ( !world.isRemote )
            {
                long time = world.getWorldTime();
                if ( ( time - getLastItemTransferTime() ) < Config.transferCooldown )
                {
                    return;
                }

                setLastItemTransferTime( time );

                if ( world.isBlockPowered( tileEntity.getPos() ) )
                {
                    itemDeliver();
                }
                else
                {
                    itemCollection();
                }
            }
        }
    }
}
