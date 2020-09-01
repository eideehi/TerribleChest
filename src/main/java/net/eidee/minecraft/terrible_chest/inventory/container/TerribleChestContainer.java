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

package net.eidee.minecraft.terrible_chest.inventory.container;

import java.util.Comparator;
import java.util.Objects;
import java.util.PrimitiveIterator;
import java.util.stream.IntStream;
import javax.annotation.ParametersAreNonnullByDefault;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntListIterator;
import mcp.MethodsReturnNonnullByDefault;
import net.eidee.minecraft.terrible_chest.inventory.TerribleChestInventory;
import net.eidee.minecraft.terrible_chest.item.ItemStackContainer;
import net.eidee.minecraft.terrible_chest.item.Items;
import net.eidee.minecraft.terrible_chest.item.TerribleChestItemHandler;
import net.eidee.minecraft.terrible_chest.settings.Config;
import net.eidee.minecraft.terrible_chest.util.TerribleChestItemSorters;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IntArray;
import net.minecraft.util.IntReferenceHolder;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.ItemHandlerHelper;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class TerribleChestContainer
    extends Container
{
    public static final int DATA_MAX_PAGE = 0;
    public static final int DATA_CURRENT_PAGE = 1;

    public static final int TYPE_LEFT_CLICK = 0;
    public static final int TYPE_RIGHT_CLICK = 1;

    public static final int TYPE_SWAP = 2;
    public static final int TYPE_ONE_BY_ONE = 3;

    public static final int TYPE_THROW_MAX_STACK = 1;

    public static final int TYPE_MOVE_THE_SAME = 2;

    private final PlayerInventory playerInventory;
    private final TerribleChestInventory chestInventory;
    private final IInventory unlockInventory;
    private final IIntArray chestData;
    private final IIntArray itemCounts;
    private final int mainInventorySize;

    private int swapIndex;

    public TerribleChestContainer( int id,
                                   PlayerInventory playerInventory,
                                   TerribleChestInventory chestInventory,
                                   IIntArray chestData )
    {
        super( ContainerTypes.TERRIBLE_CHEST, id );
        this.playerInventory = playerInventory;
        this.chestInventory = chestInventory;
        this.unlockInventory = new Inventory( 1 );
        this.chestData = chestData;
        this.itemCounts = chestInventory.getItemCountAccessor();
        this.mainInventorySize = playerInventory.mainInventory.size() + chestInventory.getSizeInventory();
        this.swapIndex = -1;

        if ( Config.COMMON.useSinglePageMode.get() )
        {
            slotInitializeSinglePage();
        }
        else
        {
            slotInitializeMultiPage();
        }

        this.trackIntArray( this.chestData );

        for ( int i = 0; i < this.itemCounts.size(); ++i )
        {
            final int index = i;
            this.trackInt( new IntReferenceHolder()
            {
                private int lastKnownPage = -1;

                @Override
                public int get()
                {
                    return itemCounts.get( index );
                }

                @Override
                public void set( int value )
                {
                    itemCounts.set( index, value );
                }

                @Override
                public boolean isDirty()
                {
                    if ( super.isDirty() )
                    {
                        return true;
                    }
                    else
                    {
                        int page = chestData.get( DATA_CURRENT_PAGE );
                        boolean flag = page != this.lastKnownPage;
                        this.lastKnownPage = page;
                        return flag;
                    }
                }
            } );
        }
    }

    public static TerribleChestContainer createContainer( int id, PlayerInventory playerInventory )
    {
        int slots = 133;
        if ( !Config.COMMON.useSinglePageMode.get() )
        {
            slots = Config.COMMON.inventoryRows.get() * 9;
        }
        return new TerribleChestContainer( id,
                                           playerInventory,
                                           TerribleChestInventory.dummy( slots ),
                                           new IntArray( 2 ) );
    }

    private void slotInitializeMultiPage()
    {
        int rows = Config.COMMON.inventoryRows.get();
        int offset = ( rows - 4 ) * 18;

        for ( int i = 0; i < rows; ++i )
        {
            for ( int j = 0; j < 9; ++j )
            {
                addSlot( new Slot( chestInventory, j + i * 9, 8 + j * 18, 36 + i * 18 ) );
            }
        }

        for ( int i = 0; i < 3; ++i )
        {
            for ( int j = 0; j < 9; ++j )
            {
                addSlot( new Slot( playerInventory, j + i * 9 + 9, 8 + j * 18, 122 + i * 18 + offset ) );
            }
        }

        for ( int i = 0; i < 9; ++i )
        {
            addSlot( new Slot( playerInventory, i, 8 + i * 18, 180 + offset ) );
        }

        addSlot( new Slot( unlockInventory, 0, 183, 35 ) );
    }

    private void slotInitializeSinglePage()
    {
        for ( int i = 0; i < 7; ++i )
        {
            for ( int j = 0; j < 19; ++j )
            {
                addSlot( new Slot( chestInventory, j + i * 19, 8 + j * 18, 17 + i * 18 ) );
            }
        }

        for ( int i = 0; i < 3; ++i )
        {
            for ( int j = 0; j < 9; ++j )
            {
                addSlot( new Slot( playerInventory, j + i * 9 + 9, 98 + j * 18, 156 + i * 18 ) );
            }
        }

        for ( int i = 0; i < 9; ++i )
        {
            addSlot( new Slot( playerInventory, i, 98 + i * 18, 214 ) );
        }
    }

    private void swap( int index1, int index2 )
    {
        TerribleChestItemHandler itemHandler = chestInventory.getItemHandler();
        ItemStackContainer container1 = itemHandler.removeContainerInSlot( index1 );
        ItemStackContainer container2 = itemHandler.removeContainerInSlot( index2 );
        if ( !container2.isEmpty() )
        {
            itemHandler.setContainerInSlot( index1, container2 );
        }
        if ( !container1.isEmpty() )
        {
            itemHandler.setContainerInSlot( index2, container1 );
        }
    }

    private void sort( Comparator< ItemStackContainer > comparator, int index1, int index2 )
    {
        if ( index1 >= 0 && index2 >= 0 && index1 < index2 )
        {
            TerribleChestItemHandler itemHandler = chestInventory.getItemHandler();

            int length = index2 - index1;
            int h = length;
            boolean loop = false;
            while ( h > 1 || loop )
            {
                if ( h > 1 )
                {
                    h = h * 10 / 13;
                }
                loop = false;
                for ( int i = 0; i < length - h; i++ )
                {
                    int j = index1 + i;
                    int k = j + h;

                    ItemStackContainer container1 = itemHandler.getContainerInSlot( j );
                    ItemStackContainer container2 = itemHandler.getContainerInSlot( k );

                    boolean swap = container1.isEmpty() && !container2.isEmpty();
                    if ( !container1.isEmpty() && !container2.isEmpty() )
                    {
                        if ( ItemHandlerHelper.canItemStacksStack( container1.getStack(), container2.getStack() ) )
                        {
                            long freeSpace = itemHandler.getSlotFreeSpace( j );
                            if ( freeSpace > 0 )
                            {
                                long size = Math.min( container2.getCount(), freeSpace );
                                container1.growCount( size );
                                container2.shrinkCount( size );
                                loop = true;
                            }
                        }
                        swap = comparator.compare( container1, container2 ) > 0;
                    }

                    if ( swap )
                    {
                        swap( j, k );
                        loop = true;
                    }
                }
            }
        }
    }

    @Override
    protected boolean mergeItemStack( ItemStack stack, int startIndex, int endIndex, boolean reverseDirection )
    {
        PrimitiveIterator.OfInt it;
        {
            IntStream stream = IntStream.range( startIndex, endIndex );
            if ( reverseDirection )
            {
                stream = stream.map( i -> endIndex - i + startIndex - 1 );
            }
            it = stream.filter( i -> i >= 0 && i < inventorySlots.size() ).iterator();
        }
        boolean result = false;
        IntList emptySlots = new IntArrayList( endIndex - startIndex );
        while ( it.hasNext() )
        {
            if ( stack.isEmpty() )
            {
                break;
            }
            int index = it.nextInt();
            Slot slot = getSlot( index );
            ItemStack stackInSlot = slot.getStack();
            if ( stackInSlot.isEmpty() )
            {
                emptySlots.add( index );
                continue;
            }
            if ( ItemHandlerHelper.canItemStacksStack( stack, stackInSlot ) )
            {
                if ( index < chestInventory.getSizeInventory() )
                {
                    long freeSpace = chestInventory.getItemHandler().getSlotFreeSpace( index );
                    if ( freeSpace > 0 )
                    {
                        int size = ( int )Math.min( stack.getCount(), freeSpace );
                        chestInventory.getContainerInSlot( index ).growCount( size );
                        stack.shrink( size );
                        result = true;
                    }
                }
                else
                {
                    int count = stackInSlot.getCount();
                    int freeSpace = Math.min( stackInSlot.getMaxStackSize(), slot.getSlotStackLimit() ) - count;
                    if ( freeSpace > 0 )
                    {
                        int size = Math.min( stack.getCount(), freeSpace );
                        stackInSlot.grow( size );
                        stack.shrink( size );
                        result = true;
                    }
                }
            }
        }
        if ( !stack.isEmpty() )
        {
            IntListIterator emptiesIt = emptySlots.iterator();
            while ( emptiesIt.hasNext() )
            {
                int emptySlot = emptiesIt.nextInt();
                Slot slot = getSlot( emptySlot );
                slot.putStack( stack.split( slot.getSlotStackLimit() ) );
                result = true;
                if ( stack.isEmpty() )
                {
                    break;
                }
            }
        }
        return result;
    }

    @OnlyIn( Dist.CLIENT )
    public int getMaxPage()
    {
        return chestData.get( DATA_MAX_PAGE );
    }

    @OnlyIn( Dist.CLIENT )
    public int getCurrentPage()
    {
        return chestData.get( DATA_CURRENT_PAGE );
    }

    @OnlyIn( Dist.CLIENT )
    public long getItemCount( int slot )
    {
        return Integer.toUnsignedLong( itemCounts.get( slot ) );
    }

    @OnlyIn( Dist.CLIENT )
    public IInventory getPlayerInventory()
    {
        return playerInventory;
    }

    @OnlyIn( Dist.CLIENT )
    public IInventory getChestInventory()
    {
        return chestInventory;
    }

    @OnlyIn( Dist.CLIENT )
    public int getSwapIndex()
    {
        return swapIndex;
    }

    public void changePage( int page )
    {
        int currentPage = chestData.get( DATA_CURRENT_PAGE );
        int maxPage = chestData.get( DATA_MAX_PAGE );
        int nextPage = MathHelper.clamp( page, 0, maxPage - 1 );
        if ( nextPage != currentPage )
        {
            chestData.set( DATA_CURRENT_PAGE, nextPage );
        }
    }

    public void unlockMaxPage()
    {
        ItemStack stack = unlockInventory.getStackInSlot( 0 );
        if ( !stack.isEmpty() && stack.getItem() == Items.DIAMOND_SPHERE )
        {
            int maxPage = chestData.get( DATA_MAX_PAGE );
            int nextMaxPage = MathHelper.clamp( maxPage + 1, 0, Config.COMMON.maxPageLimit.get() );
            if ( nextMaxPage > maxPage )
            {
                stack.shrink( 1 );
                chestData.set( DATA_MAX_PAGE, nextMaxPage );
            }
        }
    }

    @Override
    public boolean canInteractWith( PlayerEntity playerIn )
    {
        return chestInventory.isUsableByPlayer( playerIn );
    }

    @Override
    public void onContainerClosed( PlayerEntity playerIn )
    {
        super.onContainerClosed( playerIn );

        ItemStack stack = unlockInventory.removeStackFromSlot( 0 );
        if ( !stack.isEmpty() && !playerIn.addItemStackToInventory( stack ) )
        {
            playerIn.dropItem( stack, true );
        }
    }

    @Override
    public ItemStack slotClick( int slotId, int dragType, ClickType clickTypeIn, PlayerEntity player )
    {
        Slot slot = ( slotId >= 0 && slotId < mainInventorySize ) ? getSlot( slotId ) : null;
        if ( slot == null )
        {
            return super.slotClick( slotId, dragType, clickTypeIn, player );
        }
        if ( slotId < chestInventory.getSizeInventory() )
        {

            swapIndex = ( clickTypeIn == ClickType.PICKUP ) ? swapIndex : -1;

            if ( clickTypeIn == ClickType.PICKUP )
            {
                ItemStack grabbedStack = playerInventory.getItemStack().copy();
                ItemStack stackInSlot = slot.getStack();

                if ( swapIndex != -1 )
                {
                    dragType = TYPE_SWAP;
                }

                if ( dragType == TYPE_LEFT_CLICK )
                {
                    if ( grabbedStack.isEmpty() )
                    {
                        if ( !stackInSlot.isEmpty() )
                        {
                            ItemStack result = slot.decrStackSize( stackInSlot.getMaxStackSize() );
                            playerInventory.setItemStack( result );
                            return result;
                        }
                    }
                    else
                    {
                        if ( stackInSlot.isEmpty() )
                        {
                            slot.putStack( grabbedStack );
                            playerInventory.setItemStack( ItemStack.EMPTY );
                            return ItemStack.EMPTY;
                        }
                        else
                        {
                            if ( ItemHandlerHelper.canItemStacksStack( grabbedStack, stackInSlot ) )
                            {
                                if ( mergeItemStack( grabbedStack, slotId, slotId + 1, false ) )
                                {
                                    playerInventory.setItemStack( grabbedStack );
                                    return grabbedStack;
                                }
                            }
                        }
                    }
                }

                else if ( dragType == TYPE_RIGHT_CLICK )
                {
                    if ( grabbedStack.isEmpty() )
                    {
                        if ( !stackInSlot.isEmpty() )
                        {

                            long count = chestInventory.getContainerInSlot( slotId ).getCount();
                            int size = ( int )Math.min( count, stackInSlot.getMaxStackSize() ) / 2;
                            ItemStack result = slot.decrStackSize( size );
                            playerInventory.setItemStack( result );
                            return result;
                        }
                    }
                    else
                    {
                        if ( stackInSlot.isEmpty() )
                        {

                            slot.putStack( grabbedStack.split( 1 ) );
                            playerInventory.setItemStack( grabbedStack );
                            return grabbedStack;
                        }
                        else if ( ItemHandlerHelper.canItemStacksStack( grabbedStack, stackInSlot ) )
                        {
                            if ( chestInventory.getItemHandler().getSlotFreeSpace( slotId ) > 0 )
                            {
                                grabbedStack.shrink( 1 );
                                chestInventory.getContainerInSlot( slotId ).growCount( 1 );
                                playerInventory.setItemStack( grabbedStack );
                                return grabbedStack;
                            }
                        }
                    }
                }

                else if ( dragType == TYPE_SWAP )
                {
                    if ( swapIndex >= 0 )
                    {
                        swap( swapIndex, slotId );
                        swapIndex = -1;
                    }
                    else
                    {
                        swapIndex = slotId;
                    }
                }

                else if ( dragType == TYPE_ONE_BY_ONE )
                {
                    if ( !stackInSlot.isEmpty() )
                    {
                        ItemStack copy = ItemHandlerHelper.copyStackWithSize( stackInSlot, 1 );
                        if ( mergeItemStack( copy, chestInventory.getSizeInventory(), mainInventorySize, true ) )
                        {
                            chestInventory.getContainerInSlot( slotId ).shrinkCount( 1 );
                            if ( chestInventory.getContainerInSlot( slotId ).isEmpty() )
                            {
                                slot.putStack( ItemStack.EMPTY );
                            }
                        }
                    }
                }

            }

            else if ( clickTypeIn == ClickType.THROW )
            {
                ItemStack grabbedStack = playerInventory.getItemStack();
                if ( grabbedStack.isEmpty() )
                {
                    ItemStack stackInSlot = slot.getStack();
                    if ( !stackInSlot.isEmpty() )
                    {
                        ItemStackContainer container = chestInventory.getContainerInSlot( slotId );

                        int stackSize = ( dragType == TYPE_THROW_MAX_STACK ) ? stackInSlot.getMaxStackSize() : 1;
                        int dropSize = ( int )Math.min( stackSize, container.getCount() );
                        player.dropItem( ItemHandlerHelper.copyStackWithSize( stackInSlot, dropSize ), false );

                        container.shrinkCount( dropSize );
                        if ( container.isEmpty() )
                        {
                            slot.putStack( ItemStack.EMPTY );
                        }
                    }
                }
            }

            else if ( clickTypeIn == ClickType.QUICK_MOVE )
            {
                if ( dragType == TYPE_MOVE_THE_SAME )
                {
                    ItemStack stackInSlot = slot.getStack().copy();
                    if ( !stackInSlot.isEmpty() )
                    {
                        for ( int i = 0; i < chestInventory.getSizeInventory(); i++ )
                        {
                            ItemStack stackInChest = getSlot( i ).getStack();
                            if ( ItemHandlerHelper.canItemStacksStack( stackInSlot, stackInChest ) )
                            {
                                while ( !transferStackInSlot( player, i ).isEmpty() )
                                {
                                    ;
                                }
                            }
                        }
                    }
                }
                else
                {
                    transferStackInSlot( player, slotId );
                }
            }

            else if ( clickTypeIn == ClickType.CLONE )
            {
                Comparator< ItemStackContainer > comparator = TerribleChestItemSorters.DEFAULT_1;
                if ( dragType == 2 )
                {
                    comparator = TerribleChestItemSorters.DEFAULT_2;
                }
                else if ( dragType == 3 )
                {
                    comparator = TerribleChestItemSorters.DEFAULT_3;
                }
                sort( comparator, 0, chestInventory.getSizeInventory() );
            }

            return ItemStack.EMPTY;
        }

        else
        {
            swapIndex = -1;

            if ( clickTypeIn == ClickType.PICKUP )
            {
                if ( dragType == TYPE_ONE_BY_ONE )
                {
                    ItemStack stackInSlot = slot.getStack();
                    if ( !stackInSlot.isEmpty() )
                    {
                        ItemStack copy = ItemHandlerHelper.copyStackWithSize( stackInSlot, 1 );
                        if ( mergeItemStack( copy, 0, chestInventory.getSizeInventory(), false ) )
                        {
                            stackInSlot.shrink( 1 );
                        }
                    }
                }
            }

            else if ( clickTypeIn == ClickType.QUICK_MOVE )
            {
                if ( dragType == TYPE_MOVE_THE_SAME )
                {
                    ItemStack stackInSlot = slot.getStack().copy();
                    if ( !stackInSlot.isEmpty() )
                    {
                        for ( int i = chestInventory.getSizeInventory(); i < mainInventorySize; i++ )
                        {
                            ItemStack stackInPlayer = getSlot( i ).getStack();
                            if ( ItemHandlerHelper.canItemStacksStack( stackInSlot, stackInPlayer ) )
                            {
                                transferStackInSlot( player, i );
                            }
                        }
                    }
                    return ItemStack.EMPTY;
                }
            }
        }

        return super.slotClick( slotId, dragType, clickTypeIn, player );
    }

    @Override
    public ItemStack transferStackInSlot( PlayerEntity playerIn, int index )
    {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = ( index >= 0 && index < inventorySlots.size() ) ? inventorySlots.get( index ) : null;
        if ( slot != null && slot.getHasStack() )
        {
            ItemStack stackInSlot = slot.getStack();
            stack = stackInSlot.copy();
            if ( index < chestInventory.getSizeInventory() )
            {
                ItemStackContainer container = chestInventory.getContainerInSlot( index );
                int size = ( int )Math.min( container.getCount(), stackInSlot.getMaxStackSize() );
                ItemStack copy = ItemHandlerHelper.copyStackWithSize( stackInSlot, size );
                if ( !mergeItemStack( copy, chestInventory.getSizeInventory(), mainInventorySize, true ) )
                {
                    return ItemStack.EMPTY;
                }
                container.shrinkCount( size - copy.getCount() );
                if ( container.isEmpty() )
                {
                    stackInSlot.setCount( 0 );
                }
            }
            else if ( index < mainInventorySize )
            {
                if ( !mergeItemStack( stackInSlot, 0, chestInventory.getSizeInventory(), false ) )
                {
                    return ItemStack.EMPTY;
                }
            }
            else if ( !mergeItemStack( stackInSlot, chestInventory.getSizeInventory(), mainInventorySize, true ) )
            {
                return ItemStack.EMPTY;
            }

            if ( stackInSlot.isEmpty() )
            {
                slot.putStack( ItemStack.EMPTY );
            }
            else
            {
                slot.onSlotChanged();
            }
        }

        return stack;
    }

    @Override
    public boolean canDragIntoSlot( Slot slotIn )
    {
        return Objects.equals( slotIn.inventory, playerInventory );
    }

    @Override
    public boolean canMergeSlot( ItemStack stack, Slot slotIn )
    {
        return Objects.equals( slotIn.inventory, playerInventory );
    }
}
