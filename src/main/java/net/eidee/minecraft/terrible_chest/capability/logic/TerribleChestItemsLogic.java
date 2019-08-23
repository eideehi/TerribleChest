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

package net.eidee.minecraft.terrible_chest.capability.logic;

import java.util.Comparator;
import javax.annotation.ParametersAreNonnullByDefault;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import mcp.MethodsReturnNonnullByDefault;
import net.eidee.minecraft.terrible_chest.capability.TerribleChestItem;
import net.eidee.minecraft.terrible_chest.config.Config;
import net.eidee.minecraft.terrible_chest.util.IntUtil;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.items.ItemHandlerHelper;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public abstract class TerribleChestItemsLogic
{
    protected Int2ObjectMap< TerribleChestItem > items;

    public TerribleChestItemsLogic( Int2ObjectMap< TerribleChestItem > items )
    {
        this.items = items;
    }

    protected abstract int getInventorySize();

    public IInventory createInventory()
    {
        return new IInventory()
        {
            @Override
            public int getSizeInventory()
            {
                return getInventorySize();
            }

            @Override
            public boolean isEmpty()
            {
                for ( int i = 0; i < getSizeInventory(); i++ )
                {
                    if ( items.getOrDefault( i, TerribleChestItem.EMPTY ).isNotEmpty() )
                    {
                        return false;
                    }
                }
                return true;
            }

            @Override
            public ItemStack getStackInSlot( int index )
            {
                TerribleChestItem item = items.getOrDefault( index, TerribleChestItem.EMPTY );
                if ( item.isNotEmpty() )
                {
                    int itemCount = item.getCount();
                    int size = IntUtil.minUnsigned( getInventoryStackLimit(), itemCount );
                    return ItemHandlerHelper.copyStackWithSize( item.getStack(), size );
                }
                return ItemStack.EMPTY;
            }

            @Override
            public ItemStack decrStackSize( int index, int count )
            {
                if ( count > 0 && isValidIndex( index ) )
                {
                    TerribleChestItem item = items.getOrDefault( index, TerribleChestItem.EMPTY );
                    if ( item.isNotEmpty() )
                    {
                        ItemStack stack = item.getStack();
                        int itemCount = item.getCount();
                        int _count = IntUtil.minUnsigned( count, itemCount );
                        if ( _count == itemCount )
                        {
                            items.remove( index );
                        }
                        item.setCount( itemCount - _count );
                        markDirty();
                        return ItemHandlerHelper.copyStackWithSize( stack, _count );
                    }
                }
                return ItemStack.EMPTY;
            }

            @Override
            public ItemStack removeStackFromSlot( int index )
            {
                if ( isValidIndex( index ) )
                {
                    TerribleChestItem item = items.remove( index );
                    if ( item != null )
                    {
                        ItemStack stack = item.getStack();
                        int size = IntUtil.minUnsigned( stack.getMaxStackSize(), item.getCount() );
                        markDirty();
                        return ItemHandlerHelper.copyStackWithSize( stack, size );
                    }
                }
                return ItemStack.EMPTY;
            }

            @Override
            public void setInventorySlotContents( int index, ItemStack stack )
            {
                if ( isValidIndex( index ) )
                {
                    if ( stack.isEmpty() )
                    {
                        TerribleChestItem item = items.remove( index );
                        if ( item != null )
                        {
                            markDirty();
                        }
                    }
                    else
                    {
                        TerribleChestItem item = new TerribleChestItem( stack );
                        items.put( index, item );
                        markDirty();
                    }
                }
            }

            @Override
            public void markDirty()
            {
            }

            @Override
            public boolean isUsableByPlayer( PlayerEntity player )
            {
                return true;
            }

            @Override
            public void clear()
            {
                items.clear();
            }

            @Override
            public int getInventoryStackLimit()
            {
                return ( int )Math.min( Integer.MAX_VALUE, Config.COMMON.slotStackLimit.get() );
            }
        };
    }

    public boolean isValidIndex( int index )
    {
        return index >= 0 && index < getInventorySize();
    }

    public int getItemCount( int index )
    {
        return items.getOrDefault( index, TerribleChestItem.EMPTY ).getCount();
    }

    public void setItemCount( int index, int count )
    {
        TerribleChestItem item = items.getOrDefault( index, TerribleChestItem.EMPTY );
        if ( item.isNotEmpty() )
        {
            int _count = ( int )Math.min( count, Config.COMMON.slotStackLimit.get() );
            item.setCount( _count );
        }
    }

    public void swap( int index1, int index2 )
    {
        TerribleChestItem item1 = items.remove( index1 );
        TerribleChestItem item2 = items.remove( index2 );
        if ( item2 != null && item2.isNotEmpty() )
        {
            items.put( index1, item2 );
        }
        if ( item1 != null && item1.isNotEmpty() )
        {
            items.put( index2, item1 );
        }
    }

    public void sort( Comparator< TerribleChestItem > comparator, int index1, int index2 )
    {
        if ( index1 >= 0 && index2 >= 0 && index1 < index2 && index2 <= getInventorySize() )
        {
            int length = index2 - index1;
            int h = length;
            boolean swap = false;
            while ( h > 1 || swap )
            {
                if ( h > 1 )
                {
                    h = h * 10 / 13;
                }
                swap = false;
                for ( int i = 0; i < length - h; i++ )
                {
                    int j = index1 + i;
                    int k = j + h;

                    TerribleChestItem item1 = items.getOrDefault( j, TerribleChestItem.EMPTY );
                    TerribleChestItem item2 = items.getOrDefault( k, TerribleChestItem.EMPTY );

                    if ( ItemHandlerHelper.canItemStacksStack( item1.getStack(), item2.getStack() ) )
                    {
                        long slotStackLimit = Config.COMMON.slotStackLimit.get();
                        int item1Count = item1.getCount();
                        int item2Count = item2.getCount();
                        if ( Long.compareUnsigned( item1Count, slotStackLimit ) < 0 )
                        {
                            int limit = ( int )( slotStackLimit - item1Count );
                            int size = IntUtil.minUnsigned( item2Count, limit );
                            item1.setCount( item1Count + size );
                            item2.setCount( item2Count - size );
                            swap = true;
                        }
                    }

                    if ( ( item1.isNotEmpty() && item2.isNotEmpty() ) || ( item1.isEmpty() && item2.isNotEmpty() ) )
                    {
                        if ( item1.isEmpty() || comparator.compare( item1, item2 ) > 0 )
                        {
                            swap( j, k );
                            swap = true;
                        }
                    }
                }
            }
        }
    }

    public CompoundNBT writeToNBT( CompoundNBT nbt )
    {
        return nbt;
    }

    public void readFromNBT( CompoundNBT nbt )
    {
    }

    public interface Factory< T extends TerribleChestItemsLogic >
    {
        T create( Int2ObjectMap< TerribleChestItem > items );
    }
}
