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

package net.eidee.minecraft.terrible_chest.tileentity;

import java.util.Objects;
import java.util.UUID;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import mcp.MethodsReturnNonnullByDefault;
import net.eidee.minecraft.terrible_chest.capability.Capabilities;
import net.eidee.minecraft.terrible_chest.capability.TerribleChestCapability;
import net.eidee.minecraft.terrible_chest.config.Config;
import net.eidee.minecraft.terrible_chest.inventory.ExtraInventoryData;
import net.eidee.minecraft.terrible_chest.inventory.TerribleChestInventory;
import net.eidee.minecraft.terrible_chest.inventory.container.TerribleChestContainer;
import net.eidee.minecraft.terrible_chest.item.TerribleChestItemHandler;

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
public class TerribleChestTileEntity
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
                return tmpCapability != null ? tmpCapability.getMaxPage() : 0;
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
                if ( tmpCapability != null )
                {
                    tmpCapability.setMaxPage( value );
                }
            }
        }
    };

    private UUID ownerId;
    private int page;
    private long lastTransferTime;

    private TerribleChestCapability tmpCapability;

    @Nullable
    private TerribleChestCapability getTerribleChestCapability()
    {
        if ( ownerId != null )
        {
            Objects.requireNonNull( getWorld() );
            EntityPlayer player = getWorld().getPlayerEntityByUUID( ownerId );
            if ( player != null )
            {
                return player.getCapability( Capabilities.TERRIBLE_CHEST, null );
            }
        }
        return null;
    }

    public void setOwnerId( UUID ownerId )
    {
        this.ownerId = ownerId;
    }

    public boolean isOwner( EntityPlayer player )
    {
        return Objects.equals( ownerId, player.getUniqueID() );
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
        TerribleChestCapability capability = getTerribleChestCapability();
        if ( capability != null )
        {
            return TerribleChestItemHandler.create( capability.getContainers(), () -> page * 27, () -> 27 );
        }
        return null;
    }

    @Nullable
    @Override
    public TerribleChestInventory getTerribleChestInventory()
    {
        TerribleChestCapability capability = getTerribleChestCapability();
        if ( capability != null )
        {
            TerribleChestTileEntity self = this;
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
                    if ( world.getTileEntity( pos ) != self || !Objects.equals( player.getUniqueID(), ownerId ) )
                    {
                        return false;
                    }
                    return player.getDistanceSq( pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5 ) <= 64.0;
                }

                @Override
                public void openInventory( EntityPlayer player )
                {
                    self.tmpCapability = capability;
                }

                @Override
                public void closeInventory( EntityPlayer player )
                {
                    self.tmpCapability = null;
                }
            };
        }
        return null;
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
        ownerId = compound.getUniqueId( "OwnerId" );
        page = compound.getInteger( "Page" );
    }

    @Override
    public NBTTagCompound writeToNBT( NBTTagCompound compound )
    {
        super.writeToNBT( compound );
        if ( ownerId != null )
        {
            compound.setUniqueId( "OwnerId", ownerId );
        }
        compound.setInteger( "Page", page );
        return compound;
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
