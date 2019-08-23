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
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import mcp.MethodsReturnNonnullByDefault;
import net.eidee.minecraft.terrible_chest.capability.Capabilities;
import net.eidee.minecraft.terrible_chest.capability.TerribleChestItemsCapability;
import net.eidee.minecraft.terrible_chest.capability.logic.TerribleChestItemSorters;
import net.eidee.minecraft.terrible_chest.capability.logic.TerribleChestItemsLogic;
import net.eidee.minecraft.terrible_chest.config.Config;
import net.eidee.minecraft.terrible_chest.inventory.ItemHandler;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.IIntArray;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public abstract class TerribleChestTileEntity< LOGIC extends TerribleChestItemsLogic, SELF extends TerribleChestTileEntity< LOGIC, SELF > >
    extends TileEntity
    implements INamedContainerProvider
{
    protected UUID ownerId;

    public TerribleChestTileEntity()
    {
        super( TileEntityTypes.TERRIBLE_CHEST );
    }

    protected abstract SELF getSelf();

    protected abstract LOGIC castLogic( TerribleChestItemsLogic logic );

    protected abstract TerribleChestInventoryWrapper.Factory< LOGIC, SELF > getInventoryWrapperFactory();

    protected LazyOptional< TerribleChestInventoryWrapper< LOGIC, SELF > > createInventoryWrapper()
    {
        World world = Objects.requireNonNull( getWorld() );
        PlayerEntity owner = world.getPlayerByUuid( ownerId );
        if ( owner != null )
        {
            return owner.getCapability( Capabilities.TERRIBLE_CHEST )
                        .map( TerribleChestItemsCapability::getLogic )
                        .map( this::castLogic )
                        .map( x -> getInventoryWrapperFactory().create( getSelf(), x ) );
        }
        return LazyOptional.empty();
    }

    protected boolean isUsableByPlayer( PlayerEntity player )
    {
        World world = Objects.requireNonNull( getWorld() );
        BlockPos pos = getPos();
        if ( world.getTileEntity( pos ) != this || !Objects.equals( player.getUniqueID(), ownerId ) )
        {
            return false;
        }
        return player.getDistanceSq( pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5 ) <= 64.0;
    }

    public void setOwnerId( UUID ownerId )
    {
        this.ownerId = ownerId;
    }

    public boolean isOwner( PlayerEntity player )
    {
        return Objects.equals( ownerId, player.getUniqueID() );
    }

    @Override
    public ITextComponent getDisplayName()
    {
        return new TranslationTextComponent( "container.terrible_chest.terrible_chest" );
    }

    @Override
    public void read( CompoundNBT compound )
    {
        super.read( compound );
        ownerId = compound.getUniqueId( "OwnerId" );
    }

    @Override
    public CompoundNBT write( CompoundNBT compound )
    {
        super.write( compound );
        if ( ownerId != null )
        {
            compound.putUniqueId( "OwnerId", ownerId );
        }
        return compound;
    }

    @Nonnull
    @Override
    public < T > LazyOptional< T > getCapability( @Nonnull Capability< T > cap, @Nullable Direction side )
    {
        if ( cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY )
        {
            World world = Objects.requireNonNull( getWorld() );
            PlayerEntity owner = world.getPlayerByUuid( ownerId );
            if ( owner != null )
            {
                return createInventoryWrapper().map( ItemHandler::new ).cast();
            }
        }
        return super.getCapability( cap, side );
    }

    public static abstract class TerribleChestInventoryWrapper< LOGIC extends TerribleChestItemsLogic, TE extends TerribleChestTileEntity< LOGIC, TE > >
        implements IInventory
    {
        protected final TE tileEntity;
        protected final LOGIC logic;
        protected final IInventory inventory;

        public TerribleChestInventoryWrapper( TE tileEntity, LOGIC logic )
        {
            this.tileEntity = tileEntity;
            this.logic = logic;
            this.inventory = logic.createInventory();
        }

        protected abstract IIntArray getData();

        public void swap( int index1, int index2 )
        {
            if ( index1 >= 0 && index2 >= 0 )
            {
                logic.swap( index1, index2 );
            }
        }

        public void sort( int sortType )
        {
            logic.sort( TerribleChestItemSorters.DEFAULT, 0, getSizeInventory() );
        }

        @Override
        public int getSizeInventory()
        {
            return inventory.getSizeInventory();
        }

        @Override
        public boolean isEmpty()
        {
            return inventory.isEmpty();
        }

        @Override
        public ItemStack getStackInSlot( int index )
        {
            return inventory.getStackInSlot( index );
        }

        @Override
        public ItemStack decrStackSize( int index, int count )
        {
            return inventory.decrStackSize( index, count );
        }

        @Override
        public ItemStack removeStackFromSlot( int index )
        {
            return inventory.removeStackFromSlot( index );
        }

        @Override
        public void setInventorySlotContents( int index, ItemStack stack )
        {
            inventory.setInventorySlotContents( index, stack );
        }

        @Override
        public void markDirty()
        {
            inventory.markDirty();
        }

        @Override
        public void clear()
        {
            inventory.clear();
        }

        @Override
        public boolean isUsableByPlayer( PlayerEntity player )
        {
            return tileEntity.isUsableByPlayer( player );
        }

        @Override
        public int getInventoryStackLimit()
        {
            return inventory.getInventoryStackLimit();
        }

        public interface Factory< LOGIC extends TerribleChestItemsLogic, TE extends TerribleChestTileEntity< LOGIC, TE > >
        {
            TerribleChestInventoryWrapper< LOGIC, TE > create( TE tileEntity, LOGIC logic );
        }
    }
}
