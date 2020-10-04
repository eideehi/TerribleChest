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

package net.eidee.minecraft.terrible_chest.inventory.container;

import java.util.PrimitiveIterator;
import java.util.stream.IntStream;
import javax.annotation.ParametersAreNonnullByDefault;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntListIterator;
import mcp.MethodsReturnNonnullByDefault;
import net.eidee.minecraft.terrible_chest.config.Config;
import net.eidee.minecraft.terrible_chest.inventory.ExtraInventoryData;
import net.eidee.minecraft.terrible_chest.inventory.TerribleChestInventory;
import net.eidee.minecraft.terrible_chest.item.ItemStackContainer;
import net.eidee.minecraft.terrible_chest.item.Items;
import net.eidee.minecraft.terrible_chest.item.TerribleChestItemHandler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemHandlerHelper;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class TerribleChestContainer
    extends Container
{
    public static final int EXTRA_INVENTORY_DATA_SIZE = 2;
    public static final int DATA_MAX_PAGE = 0;
    public static final int DATA_CURRENT_PAGE = 1;

    public static final int TYPE_LEFT_CLICK = 0;
    public static final int TYPE_RIGHT_CLICK = 1;

    public static final int TYPE_SWAP = 2;
    public static final int TYPE_ONE_BY_ONE = 3;

    public static final int TYPE_THROW_MAX_STACK = 1;

    public static final int TYPE_MOVE_THE_SAME = 2;

    private final InventoryPlayer playerInventory;
    private final TerribleChestInventory chestInventory;
    private final IInventory unlockInventory;
    private final int mainInventorySize;

    private final ExtraInventoryData extraInventoryData;
    private final int fieldCount;
    private final int extraInventoryDataSize;
    private final int[] dataCache;

    private int swapIndex;

    public TerribleChestContainer( InventoryPlayer InventoryPlayer )
    {
        this( InventoryPlayer, TerribleChestInventory.dummy(), ExtraInventoryData.dummy( EXTRA_INVENTORY_DATA_SIZE ) );
    }

    public TerribleChestContainer( InventoryPlayer inventoryPlayer,
                                   TerribleChestInventory inventory,
                                   ExtraInventoryData extraInventoryData )
    {
        this.chestInventory = inventory;
        this.playerInventory = inventoryPlayer;
        this.unlockInventory = new InventoryBasic( "", false, 1 );
        this.mainInventorySize = playerInventory.mainInventory.size() + chestInventory.getSizeInventory();
        this.extraInventoryData = extraInventoryData;
        this.fieldCount = this.chestInventory.getFieldCount();
        this.extraInventoryDataSize = this.extraInventoryData.getSize();
        this.dataCache = new int[ this.fieldCount + this.extraInventoryDataSize ];

        this.swapIndex = -1;

        this.chestInventory.openInventory( this.playerInventory.player );

        for ( int i = 0; i < 3; ++i )
        {
            for ( int j = 0; j < 9; ++j )
            {
                addSlotToContainer( new Slot( this.chestInventory, j + i * 9, 8 + j * 18, 34 + i * 18 ) );
            }
        }

        for ( int i = 0; i < 3; ++i )
        {
            for ( int j = 0; j < 9; ++j )
            {
                addSlotToContainer( new Slot( this.playerInventory, j + i * 9 + 9, 8 + j * 18, 100 + i * 18 ) );
            }
        }

        for ( int i = 0; i < 9; ++i )
        {
            addSlotToContainer( new Slot( this.playerInventory, i, 8 + i * 18, 158 ) );
        }

        addSlotToContainer( new Slot( unlockInventory, 0, 183, 35 ) );
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

    @SideOnly( Side.CLIENT )
    public int getMaxPage()
    {
        return extraInventoryData.get( DATA_MAX_PAGE );
    }

    @SideOnly( Side.CLIENT )
    public int getCurrentPage()
    {
        return extraInventoryData.get( DATA_CURRENT_PAGE );
    }

    @SideOnly( Side.CLIENT )
    public long getItemCount( int slot )
    {
        return Integer.toUnsignedLong( dataCache[ slot ] );
    }

    @SideOnly( Side.CLIENT )
    public IInventory getPlayerInventory()
    {
        return playerInventory;
    }

    @SideOnly( Side.CLIENT )
    public IInventory getChestInventory()
    {
        return chestInventory;
    }

    @SideOnly( Side.CLIENT )
    public int getSwapIndex()
    {
        return swapIndex;
    }

    @SideOnly( Side.CLIENT )
    public void resetSwapIndex()
    {
        swapIndex = -1;
    }

    public void changePage( int page )
    {
        swapIndex = -1;

        int currentPage = extraInventoryData.get( DATA_CURRENT_PAGE );
        int maxPage = extraInventoryData.get( DATA_MAX_PAGE );
        int nextPage = MathHelper.clamp( page, 0, maxPage - 1 );
        if ( nextPage != currentPage )
        {
            extraInventoryData.set( DATA_CURRENT_PAGE, nextPage );
        }
    }

    public void unlockMaxPage()
    {
        swapIndex = -1;

        ItemStack stack = unlockInventory.getStackInSlot( 0 );
        if ( !stack.isEmpty() && stack.getItem() == Items.DIAMOND_SPHERE )
        {
            int maxPage = extraInventoryData.get( DATA_MAX_PAGE );
            int nextMaxPage = MathHelper.clamp( maxPage + 1, 0, Config.maxPageLimit );
            if ( nextMaxPage > maxPage )
            {
                stack.shrink( 1 );
                extraInventoryData.set( DATA_MAX_PAGE, nextMaxPage );
            }
        }
    }

    @Override
    public void addListener( IContainerListener listener )
    {
        super.addListener( listener );
        listener.sendAllWindowProperties( this, chestInventory );
    }

    @Override
    public void detectAndSendChanges()
    {
        super.detectAndSendChanges();

        for ( int i = 0; i < dataCache.length; i++ )
        {
            int value;
            if ( i < fieldCount )
            {
                value = chestInventory.getField( i );
            }
            else
            {
                value = extraInventoryData.get( i - fieldCount );
            }

            if ( dataCache[ i ] != value )
            {
                for ( IContainerListener listener : listeners )
                {
                    listener.sendWindowProperty( this, i, value );
                    dataCache[ i ] = value;
                }
            }
        }
    }

    @SideOnly( Side.CLIENT )
    @Override
    public void updateProgressBar( int id, int data )
    {
        if ( id < 0 )
        {
            return;
        }

        if ( id < fieldCount )
        {
            chestInventory.setField( id, data );
            dataCache[ id ] = data;
        }
        else if ( id < dataCache.length )
        {
            extraInventoryData.set( id - fieldCount, data );
            dataCache[ id ] = data;
        }
    }

    @Override
    public boolean canInteractWith( EntityPlayer playerIn )
    {
        return chestInventory.isUsableByPlayer( playerIn );
    }

    @Override
    public void onContainerClosed( EntityPlayer playerIn )
    {
        super.onContainerClosed( playerIn );
        chestInventory.closeInventory( playerIn );

        ItemStack stack = unlockInventory.removeStackFromSlot( 0 );
        if ( !stack.isEmpty() && !playerIn.addItemStackToInventory( stack ) )
        {
            playerIn.dropItem( stack, true );
        }
    }

    @Override
    public boolean canMergeSlot( ItemStack stack, Slot slotIn )
    {
        return slotIn.slotNumber >= 27;
    }

    @Override
    public boolean canDragIntoSlot( Slot slotIn )
    {
        return slotIn.slotNumber >= 27;
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
                slot.putStack( stack.splitStack( slot.getSlotStackLimit() ) );
                result = true;
                if ( stack.isEmpty() )
                {
                    break;
                }
            }
        }
        return result;
    }

    @Override
    public ItemStack transferStackInSlot( EntityPlayer playerIn, int index )
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

    @SuppressWarnings( { "StatementWithEmptyBody", "UnnecessarySemicolon" } )
    @Override
    public ItemStack slotClick( int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player )
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

                            slot.putStack( grabbedStack.splitStack( 1 ) );
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
}
