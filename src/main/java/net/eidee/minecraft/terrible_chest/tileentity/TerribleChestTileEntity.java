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

import static net.eidee.minecraft.terrible_chest.TerribleChest.MOD_ID;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.collect.Lists;
import mcp.MethodsReturnNonnullByDefault;
import net.eidee.minecraft.terrible_chest.capability.Capabilities;
import net.eidee.minecraft.terrible_chest.inventory.TerribleChestInventory;
import net.eidee.minecraft.terrible_chest.inventory.container.TerribleChestContainer;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.ISidedInventoryProvider;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.IIntArray;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class TerribleChestTileEntity
    extends TileEntity
    implements INamedContainerProvider,
               ITickableTileEntity
{
    public static final int DATA_PAGE = 27;
    public static final int DATA_MAX_PAGE = 28;
    public static final int DATA_SWAP_TARGET = 29;
    //public static final int DATA_SORT_TYPE = 30;
    public static final int SWAP_EXEC_FLAG = 0b0100_0000_0000_0000_0000_0000_0000_0000;

    private UUID ownerId;
    private int page;
    private IIntArray data;
    private Inventory inventory;
    /**
     * GUIでのスロット入れ替えに使う。NBT保存対象外
     */
    private int swapTarget = -1;

    {
        data = new IIntArray()
        {
            @Override
            public int get( int index )
            {
                if ( index >= 0 && index < 27 )
                {
                    return inventory != null ? inventory.getItemCount( index ) : 0;
                }
                else if ( index == DATA_PAGE )
                {
                    return page;
                }
                else if ( index == DATA_MAX_PAGE )
                {
                    return inventory != null ? inventory.getMaxPage() : 0;
                }
                else if ( index == DATA_SWAP_TARGET )
                {
                    return swapTarget;
                }
                /*
                else if ( index == DATA_SORT_TYPE )
                {
                    return -1;
                }
                 */
                return 0;
            }

            @Override
            public void set( int index, int value )
            {
                if ( index >= 0 && index < 27 )
                {
                    if ( inventory != null )
                    {
                        inventory.setItemCount( index, value );
                    }
                }
                else if ( index == DATA_PAGE )
                {
                    page = value;
                }
                else if ( index == DATA_MAX_PAGE && inventory != null )
                {
                    inventory.setMaxPage( value );
                }
                else if ( index == DATA_SWAP_TARGET && inventory != null )
                {
                    if ( ( value & SWAP_EXEC_FLAG ) != 0 )
                    {
                        int _value = value - SWAP_EXEC_FLAG;
                        int index1 = _value >> 15;
                        int index2 = _value & 0x7FFF;
                        inventory.inventory.swap( index1, index2 );
                        swapTarget = -1;
                    }
                    else
                    {
                        swapTarget = value;
                    }
                }
                /*
                else if ( index == DATA_SORT_TYPE && inventory != null )
                {
                    InventorySorter.DEFAULT.sort( inventory );
                }
                 */
            }

            @Override
            public int size()
            {
                return 30;
            }
        };
    }

    public TerribleChestTileEntity()
    {
        super( TileEntityTypes.TERRIBLE_CHEST );
    }


    private static List< TileEntity > getAllNeighborInventoryTileEntity( World world, BlockPos pos )
    {
        List< TileEntity > list = Lists.newArrayList();
        final Direction[] directions = { Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST };
        for ( Direction direction : directions )
        {
            TileEntity tileEntity = world.getTileEntity( pos.offset( direction ) );
            if ( tileEntity instanceof IInventory )
            {
                list.add( tileEntity );
            }
        }
        return list;
    }

    private static Iterable< IItemHandler > getItemHandler( TileEntity tileEntity, Direction side )
    {
        List< IItemHandler > list = Lists.newArrayListWithCapacity( 1 );
        tileEntity.getCapability( CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side ).ifPresent( list::add );
        return list;
    }

    @Nullable
    private static ISidedInventory getSidedInventory( TileEntity tileEntity )
    {
        ISidedInventory sidedInventory = null;
        if ( tileEntity instanceof ISidedInventoryProvider )
        {
            ISidedInventoryProvider sidedInventoryProvider = ( ISidedInventoryProvider )tileEntity;
            sidedInventory = sidedInventoryProvider.createInventory( tileEntity.getBlockState(),
                                                                     Objects.requireNonNull( tileEntity.getWorld() ),
                                                                     tileEntity.getPos() );
        }
        else if ( tileEntity instanceof ISidedInventory )
        {
            sidedInventory = ( ISidedInventory )tileEntity;
        }
        return sidedInventory;
    }

    private static boolean canInsertItem( TileEntity tileEntity,
                                          Direction side,
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
                                           Direction side,
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

    private boolean isUsableByPlayer( PlayerEntity player )
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

    private void setupInventory( PlayerEntity player )
    {
        this.inventory = null;
        player.getCapability( Capabilities.TERRIBLE_CHEST )
              .ifPresent( terribleChest -> this.inventory = new Inventory( this, terribleChest ) );
    }

    private void resetInventory()
    {
        this.inventory = null;
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
                final Direction[] sides = { Direction.UP, Direction.NORTH };
                for ( Direction side : sides )
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
            final Direction side = Direction.DOWN;
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
        return new TranslationTextComponent( "container." + MOD_ID + ".terrible_chest" );
    }

    @Nullable
    @Override
    public Container createMenu( int id, PlayerInventory playerInventory, PlayerEntity player )
    {
        setupInventory( player );
        return new TerribleChestContainer( id, playerInventory, inventory, data );
    }

    @Override
    public void tick()
    {
        World world = Objects.requireNonNull( getWorld() );
        if ( !world.isRemote() && ownerId != null )
        {
            PlayerEntity owner = world.getPlayerByUuid( ownerId );
            if ( owner != null )
            {
                boolean isBlockPowered = world.isBlockPowered( getPos() );
                owner.getCapability( Capabilities.TERRIBLE_CHEST )
                     .ifPresent( inventory -> {
                         if ( isBlockPowered )
                         {
                             itemDeliver( world, inventory );
                         }
                         else
                         {
                             itemCollection( world, inventory );
                         }
                     } );
            }
        }
    }

    @Override
    public void read( CompoundNBT compound )
    {
        super.read( compound );
        ownerId = compound.getUniqueId( "OwnerId" );
        page = compound.getInt( "Page" );
    }

    @Override
    public CompoundNBT write( CompoundNBT compound )
    {
        super.write( compound );
        if ( ownerId != null )
        {
            compound.putUniqueId( "OwnerId", ownerId );
        }
        compound.putInt( "Page", page );
        return compound;
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

        private int getMaxPage()
        {
            return inventory.getMaxPage();
        }

        private void setItemCount( int index, int count )
        {
            inventory.setItemCount( getOffset() + index, count );
        }

        private void setMaxPage( int maxPage )
        {
            inventory.setMaxPage( maxPage );
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
        public boolean isUsableByPlayer( PlayerEntity player )
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
        public void closeInventory( PlayerEntity player )
        {
            tileEntity.resetInventory();
        }
    }
/*
    private abstract static class InventorySorter
    {
        private static final Comparator< ItemStack > ModId = ( stack1, stack2 ) -> {
            ResourceLocation registryName1 = stack1.getItem().getRegistryName();
            ResourceLocation registryName2 = stack2.getItem().getRegistryName();
            if ( registryName1 != null && registryName2 != null )
            {
                return registryName1.getNamespace().compareTo( registryName2.getNamespace() );
            }
            else if ( registryName1 == null && registryName2 == null )
            {
                return 0;
            }
            return registryName1 == null ? 1 : -1;
        };

        private static final Comparator< ItemStack > ItemName = ( stack1, stack2 ) -> {
            ResourceLocation registryName1 = stack1.getItem().getRegistryName();
            ResourceLocation registryName2 = stack2.getItem().getRegistryName();
            if ( registryName1 != null && registryName2 != null )
            {
                return registryName1.getPath().compareTo( registryName2.getPath() );
            }
            else if ( registryName1 == null && registryName2 == null )
            {
                return 0;
            }
            return registryName1 == null ? 1 : -1;
        };

        private static final Comparator< ItemStack > StackSize = ( stack1, stack2 ) -> {
            return Integer.compare( stack1.getCount(), stack2.getCount() );
        };

        private boolean preCompare( ItemStack stack1, ItemStack stack2 )
        {
            return !stack1.isEmpty() && !stack2.isEmpty() ||
                   stack1.isEmpty() && !stack2.isEmpty();
        }

        public abstract Comparator< ItemStack > getComparator();

        final void sort( Inventory inventory )
        {
            int size = inventory.getSizeInventory();
            for ( int i = 0; i < size; i++ )
            {
                for ( int j = size - 1; j > i; j-- )
                {
                    ItemStack stack1 = inventory.getStackInSlot( i );
                    ItemStack stack2 = inventory.getStackInSlot( j );
                    if ( ItemHandlerHelper.canItemStacksStack( stack1, stack2 ) )
                    {
                        int itemCount1 = inventory.getItemCount( i );
                        int itemCount2 = inventory.getItemCount( j );
                        // TODO: オーバーフロー防止
                        inventory.setItemCount( i, itemCount1 + itemCount2 );
                        inventory.removeStackFromSlot( j );
                    }
                }
            }

            Comparator< ItemStack > comparator = getComparator();
            boolean swap;
            for ( int i = 0; i < size - 1; i++ )
            {
                swap = false;
                for ( int j = 1; j < size - ( i + 1 ); j++ )
                {
                    ItemStack stack1 = inventory.getStackInSlot( j - 1 );
                    ItemStack stack2 = inventory.getStackInSlot( j );
                    if ( preCompare( stack1, stack2 ) )
                    {
                        if ( stack1.isEmpty() )
                        {
                            swap = true;
                            inventory.swap( j - 1, j );
                        }
                        else if ( comparator.compare( stack1, stack2 ) > 0 )
                        {
                            swap = true;
                            inventory.swap( j - 1, j );
                        }
                    }
                }
                if ( !swap )
                {
                    break;
                }
            }
        }

        static InventorySorter DEFAULT = new InventorySorter()
        {
            @Override
            public Comparator< ItemStack > getComparator()
            {
                return ItemName.thenComparing( ModId ).thenComparing( StackSize );
            }
        };
    }
 */
}
