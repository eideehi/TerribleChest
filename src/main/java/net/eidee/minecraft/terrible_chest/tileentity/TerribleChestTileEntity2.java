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
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import mcp.MethodsReturnNonnullByDefault;
import net.eidee.minecraft.terrible_chest.config.Config;
import net.eidee.minecraft.terrible_chest.inventory.ExtraInventoryData;
import net.eidee.minecraft.terrible_chest.inventory.TerribleChestInventory;
import net.eidee.minecraft.terrible_chest.inventory.container.TerribleChestContainer;
import net.eidee.minecraft.terrible_chest.item.ItemStackContainer;
import net.eidee.minecraft.terrible_chest.item.TerribleChestItemHandler;
import net.eidee.minecraft.terrible_chest.util.ItemStackContainerUtil;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class TerribleChestTileEntity2
    extends TileEntity
    implements TerribleChestTileEntityBase
{
    private final ExtraInventoryData extraInventoryData = new ExtraInventoryData()
    {
        @Override
        public int getSize()
        {
            return TerribleChestContainer.EXTRA_INVENTORY_DATA_SIZE;
        }

        @Override
        public int get( int index )
        {
            if ( index == TerribleChestContainer.DATA_CURRENT_PAGE )
            {
                return page;
            }
            else if ( index == TerribleChestContainer.DATA_MAX_PAGE )
            {
                return maxPage;
            }
            return 0;
        }

        @Override
        public void set( int index, int value )
        {
            if ( index == TerribleChestContainer.DATA_CURRENT_PAGE )
            {
                page = value;
            }
            else if ( index == TerribleChestContainer.DATA_MAX_PAGE )
            {
                maxPage = value;
            }
        }
    };

    private Int2ObjectMap< ItemStackContainer > containers;
    private int maxPage;
    private int page;
    private long lastTransferTime;

    public TerribleChestTileEntity2()
    {
        this.containers = ItemStackContainerUtil.newContainers();
        this.maxPage = 1;
        this.page = 0;
        this.lastTransferTime = 0;
    }

    public void readTerribleChestData( NBTTagCompound compound )
    {
        ItemStackContainerUtil.loadAllItems( compound, containers );
        maxPage = compound.getInteger( "MaxPage" );
        page = compound.getInteger( "Page" );
    }

    public NBTTagCompound writeTerribleChestData( NBTTagCompound compound )
    {
        ItemStackContainerUtil.saveAllItems( compound, containers );
        compound.setInteger( "MaxPage", maxPage );
        compound.setInteger( "Page", page );
        return compound;
    }

    @Override
    public TileEntity getTileEntity()
    {
        return this;
    }

    @Nullable
    @Override
    public TerribleChestItemHandler getTerribleChestItemHandler()
    {
        return TerribleChestItemHandler.create( containers, () -> page * 27, () -> 27 );
    }

    @Nullable
    @Override
    public TerribleChestInventory getTerribleChestInventory()
    {
        TerribleChestItemHandler handler = Objects.requireNonNull( getTerribleChestItemHandler() );
        return new TerribleChestInventory()
        {
            @Override
            public TerribleChestItemHandler getItemHandler()
            {
                return handler;
            }

            @Override
            public void markDirty()
            {
            }

            @Override
            public boolean isUsableByPlayer( EntityPlayer player )
            {
                World world = Objects.requireNonNull( getWorld() );
                BlockPos pos = getPos();
                if ( world.getTileEntity( pos ) != getTileEntity() )
                {
                    return false;
                }
                return player.getDistanceSq( pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5 ) <= 64.0;
            }
        };
    }

    @Override
    public ExtraInventoryData getExtraInventoryData()
    {
        return extraInventoryData;
    }

    @Override
    public long getLastItemTransferTime()
    {
        return lastTransferTime;
    }

    @Override
    public void setLastItemTransferTime( long time )
    {
        lastTransferTime = time;
    }

    @Override
    public void readFromNBT( NBTTagCompound compound )
    {
        super.readFromNBT( compound );
        readTerribleChestData( compound );
    }

    @Override
    public NBTTagCompound writeToNBT( NBTTagCompound compound )
    {
        super.writeToNBT( compound );
        return writeTerribleChestData( compound );
    }

    @Override
    public boolean hasCapability( Capability< ? > capability, @Nullable EnumFacing facing )
    {
        if ( capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && Config.useItemHandlerCapability )
        {
            return getTerribleChestItemHandler() != null;
        }
        return super.hasCapability( capability, facing );
    }

    @SuppressWarnings( "unchecked" )
    @Nullable
    @Override
    public < T > T getCapability( Capability< T > capability, @Nullable EnumFacing facing )
    {
        if ( capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && Config.useItemHandlerCapability )
        {
            TerribleChestItemHandler handler = getTerribleChestItemHandler();
            if ( handler != null )
            {
                return ( T )handler;
            }
        }
        return super.getCapability( capability, facing );
    }
}
