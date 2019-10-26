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

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.collect.Lists;
import mcp.MethodsReturnNonnullByDefault;
import net.eidee.minecraft.terrible_chest.capability.Capabilities;
import net.eidee.minecraft.terrible_chest.config.Config;
import net.eidee.minecraft.terrible_chest.inventory.ItemHandler;
import net.eidee.minecraft.terrible_chest.inventory.TerribleChestInventory;
import net.eidee.minecraft.terrible_chest.inventory.container.TerribleChestContainer;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class TerribleChestTileEntity
    extends TileEntity
    implements ITickable
{
    public static final int DATA_PAGE = 27;
    public static final int DATA_MAX_PAGE = 28;
    public static final int DATA_SWAP_TARGET = 29;
    public static final int SWAP_EXEC_FLAG = 0b0100_0000_0000_0000_0000_0000_0000_0000;

    private UUID ownerId;
    private int page;
    /**
     * GUIでのスロット入れ替えに使う。NBT保存対象外
     */
    private int swapTarget = -1;

    private static List< TileEntity > getAllNeighborInventoryTileEntity( World world, BlockPos pos )
    {
        List< TileEntity > list = Lists.newArrayList();
        final EnumFacing[] directions = { EnumFacing.NORTH, EnumFacing.SOUTH, EnumFacing.WEST, EnumFacing.EAST };
        for ( EnumFacing direction : directions )
        {
            TileEntity tileEntity = world.getTileEntity( pos.offset( direction ) );
            if ( tileEntity instanceof IInventory )
            {
                list.add( tileEntity );
            }
        }
        return list;
    }

    private static Iterable< IItemHandler > getItemHandler( TileEntity tileEntity, EnumFacing side )
    {
        List< IItemHandler > list = Lists.newArrayListWithCapacity( 1 );
        IItemHandler capability = tileEntity.getCapability( CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side );
        if ( capability != null )
        {
            list.add( capability );
        }
        return list;
    }

    @Nullable
    private static ISidedInventory getSidedInventory( TileEntity tileEntity )
    {
        ISidedInventory sidedInventory = null;
        if ( tileEntity instanceof ISidedInventory )
        {
            sidedInventory = ( ISidedInventory )tileEntity;
        }
        return sidedInventory;
    }

    private static boolean canInsertItem( TileEntity tileEntity,
                                          EnumFacing side,
                                          ItemStack stackInSlot,
                                          ItemStack insertStack )
    {
        ISidedInventory sidedInventory = getSidedInventory( tileEntity );
        if ( sidedInventory != null )
        {
            for ( int slot : sidedInventory.getSlotsForFace( side ) )
            {
                if ( sidedInventory.getStackInSlot( slot ) == stackInSlot &&
                     !sidedInventory.canInsertItem( slot, insertStack, side ) )
                {
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean canExtractItem( TileEntity tileEntity,
                                           EnumFacing side,
                                           ItemStack stackInSlot )
    {
        ISidedInventory sidedInventory = getSidedInventory( tileEntity );
        if ( sidedInventory != null )
        {
            for ( int slot : sidedInventory.getSlotsForFace( side ) )
            {
                if ( sidedInventory.getStackInSlot( slot ) == stackInSlot &&
                     !sidedInventory.canExtractItem( slot, stackInSlot, side ) )
                {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isUsableByPlayer( EntityPlayer player )
    {
        World world = Objects.requireNonNull( getWorld() );
        BlockPos pos = getPos();
        if ( world.getTileEntity( pos ) != this ||
             !Objects.equals( player.getUniqueID(), ownerId ) )
        {
            return false;
        }
        return player.getDistanceSq( pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5 ) <= 64.0;
    }

    private void itemDeliver( World world, TerribleChestInventory inventory )
    {
        Inventory _inventory = new Inventory( this, inventory );
        for ( int i = 0; i < _inventory.getSizeInventory(); i++ )
        {
            ItemStack stack = _inventory.getStackInSlot( i );
            if ( stack.isEmpty() )
            {
                continue;
            }
            for ( TileEntity tileEntity : getAllNeighborInventoryTileEntity( world, getPos() ) )
            {
                final EnumFacing[] sides = { EnumFacing.UP, EnumFacing.NORTH };
                for ( EnumFacing side : sides )
                {
                    for ( IItemHandler itemHandler : getItemHandler( tileEntity, side ) )
                    {
                        for ( int j = 0; j < itemHandler.getSlots(); j++ )
                        {
                            ItemStack stackInSlot = itemHandler.getStackInSlot( j );
                            if ( stackInSlot.isEmpty() ||
                                 !canInsertItem( tileEntity, side, stackInSlot, stack ) )
                            {
                                continue;
                            }
                            ItemStack result = itemHandler.insertItem( j, stack, false );
                            if ( result != stack )
                            {
                                _inventory.decrStackSize( i, 1 );
                                return;
                            }
                        }
                    }
                }
            }
        }
    }

    private void itemCollection( World world, TerribleChestInventory inventory )
    {
        Inventory _inventory = new Inventory( this, inventory );
        for ( TileEntity tileEntity : getAllNeighborInventoryTileEntity( world, getPos() ) )
        {
            final EnumFacing side = EnumFacing.DOWN;
            for ( IItemHandler itemHandler : getItemHandler( tileEntity, side ) )
            {
                for ( int i = 0; i < itemHandler.getSlots(); i++ )
                {
                    ItemStack stackInSlot = itemHandler.getStackInSlot( i );
                    if ( !canExtractItem( tileEntity, side, stackInSlot ) )
                    {
                        continue;
                    }
                    for ( int j = 0; j < _inventory.getSizeInventory(); j++ )
                    {
                        ItemStack stack = _inventory.getStackInSlot( j );
                        int itemCount = _inventory.getItemCount( j );
                        if ( stack.isEmpty() ||
                             !ItemHandlerHelper.canItemStacksStack( stack, stackInSlot ) ||
                             itemCount == -1 )
                        {
                            continue;
                        }
                        _inventory.setItemCount( j, itemCount + 1 );
                        itemHandler.extractItem( i, 1, false );
                        return;
                    }
                }
            }
        }
    }

    @Nullable
    private TerribleChestInventory getTerribleChestInventory()
    {
        if ( ownerId != null )
        {
            World world = Objects.requireNonNull( getWorld() );
            EntityPlayer owner = world.getPlayerEntityByUUID( ownerId );
            if ( owner != null )
            {
                return owner.getCapability( Capabilities.TERRIBLE_CHEST, null );
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

    @Nullable
    public TerribleChestContainer createContainer( InventoryPlayer inventoryPlayer )
    {
        TerribleChestInventory capability = inventoryPlayer.player.getCapability( Capabilities.TERRIBLE_CHEST, null );
        if ( capability != null )
        {
            return new TerribleChestContainer( inventoryPlayer, new Inventory( this, capability ) );
        }
        return null;
    }

    @Override
    public ITextComponent getDisplayName()
    {
        return new TextComponentTranslation( "container.terrible_chest.terrible_chest" );
    }

    @Override
    public void update()
    {
        if ( !Config.stopItemCollectionAndDeliver )
        {
            World world = Objects.requireNonNull( getWorld() );
            if ( !world.isRemote )
            {
                TerribleChestInventory inventory = getTerribleChestInventory();
                if ( inventory != null )
                {
                    if ( world.isBlockPowered( getPos() ) )
                    {
                        itemDeliver( world, inventory );
                    }
                    else
                    {
                        itemCollection( world, inventory );
                    }
                }
            }
        }
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
            TerribleChestInventory inventory = getTerribleChestInventory();
            return inventory != null;
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
            TerribleChestInventory inventory = getTerribleChestInventory();
            if ( inventory != null )
            {
                return ( T )new ItemHandler( inventory );
            }
        }
        return super.getCapability( capability, facing );
    }

    private static class Inventory
        implements IInventory
    {
        private TerribleChestTileEntity tileEntity;
        private TerribleChestInventory inventory;

        Inventory( TerribleChestTileEntity tileEntity, TerribleChestInventory inventory )
        {
            this.tileEntity = tileEntity;
            this.inventory = inventory;
        }

        private int getOffset()
        {
            return tileEntity.page * 27;
        }

        private int getItemCount( int index )
        {
            return inventory.getItemCount( getOffset() + index );
        }

        private void setItemCount( int index, int count )
        {
            inventory.setItemCount( getOffset() + index, count );
        }

        private void swap( int index1, int index2 )
        {
            int offset = getOffset();
            inventory.swap( offset + index1, offset + index2 );
        }

        @Override
        public int getSizeInventory()
        {
            return 27;
        }

        @Override
        public boolean isEmpty()
        {
            int offset = getOffset();
            for ( int i = 0; i < 27; i++ )
            {
                if ( !inventory.getStackInSlot( offset + i ).isEmpty() )
                {
                    return false;
                }
            }
            return true;
        }

        @Override
        public ItemStack getStackInSlot( int index )
        {
            return inventory.getStackInSlot( getOffset() + index );
        }

        @Override
        public ItemStack decrStackSize( int index, int count )
        {
            return inventory.decrStackSize( getOffset() + index, count );
        }

        @Override
        public ItemStack removeStackFromSlot( int index )
        {
            return inventory.removeStackFromSlot( getOffset() + index );
        }

        @Override
        public void setInventorySlotContents( int index, ItemStack stack )
        {
            inventory.setInventorySlotContents( getOffset() + index, stack );
        }

        @Override
        public void markDirty()
        {
            inventory.markDirty();
        }

        @Override
        public boolean isUsableByPlayer( EntityPlayer player )
        {
            return tileEntity.isUsableByPlayer( player );
        }

        @Override
        public void clear()
        {
            int offset = getOffset();
            for ( int i = 0; i < 27; i++ )
            {
                inventory.removeStackFromSlot( offset + i );
            }
        }

        @Override
        public void openInventory( EntityPlayer player )
        {
        }

        @Override
        public void closeInventory( EntityPlayer player )
        {
        }

        @Override
        public int getInventoryStackLimit()
        {
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean isItemValidForSlot( int index, ItemStack stack )
        {
            return true;
        }

        @Override
        public int getField( int index )
        {
            if ( index >= 0 && index < 27 )
            {
                return getItemCount( index );
            }
            else if ( index == DATA_PAGE )
            {
                return tileEntity.page;
            }
            else if ( index == DATA_MAX_PAGE )
            {
                return inventory.getMaxPage();
            }
            else if ( index == DATA_SWAP_TARGET )
            {
                return tileEntity.swapTarget;
            }
            return 0;
        }

        @Override
        public void setField( int index, int value )
        {
            if ( index >= 0 && index < 27 )
            {
                setItemCount( index, value );
            }
            else if ( index == DATA_PAGE )
            {
                tileEntity.page = value;
            }
            else if ( index == DATA_MAX_PAGE )
            {
                inventory.setMaxPage( value );
            }
            else if ( index == DATA_SWAP_TARGET )
            {
                if ( ( value & SWAP_EXEC_FLAG ) != 0 )
                {
                    int _value = value - SWAP_EXEC_FLAG;
                    int index1 = _value >> 15;
                    int index2 = _value & 0x7FFF;
                    inventory.swap( index1, index2 );
                    tileEntity.swapTarget = -1;
                }
                else
                {
                    tileEntity.swapTarget = value;
                }
            }
        }

        @Override
        public int getFieldCount()
        {
            return 30;
        }

        @Override
        public String getName()
        {
            return "container.terrible_chest.terrible_chest";
        }

        @Override
        public boolean hasCustomName()
        {
            return false;
        }

        @Override
        public ITextComponent getDisplayName()
        {
            return new TextComponentTranslation( getName() );
        }
    }
}
