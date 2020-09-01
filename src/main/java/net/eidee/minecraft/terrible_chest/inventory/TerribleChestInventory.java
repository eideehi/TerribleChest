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

package net.eidee.minecraft.terrible_chest.inventory;

import javax.annotation.ParametersAreNonnullByDefault;

import mcp.MethodsReturnNonnullByDefault;
import net.eidee.minecraft.terrible_chest.item.ItemStackContainer;
import net.eidee.minecraft.terrible_chest.item.TerribleChestItemHandler;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIntArray;
import net.minecraftforge.items.ItemHandlerHelper;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public interface TerribleChestInventory
    extends IInventory
{
    static TerribleChestInventory dummy( int length )
    {
        TerribleChestItemHandler itemHandler = TerribleChestItemHandler.dummy( length );
        return new TerribleChestInventory()
        {
            @Override
            public TerribleChestItemHandler getItemHandler()
            {
                return itemHandler;
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
        };
    }

    TerribleChestItemHandler getItemHandler();

    default ItemStackContainer getContainerInSlot( int index )
    {
        return getItemHandler().getContainerInSlot( index );
    }

    default IIntArray getItemCountAccessor()
    {
        return new IIntArray()
        {
            @Override
            public int get( int index )
            {
                if ( index >= 0 && index < getSizeInventory() )
                {
                    return ( int )getContainerInSlot( index ).getCount();
                }
                return 0;
            }

            @Override
            public void set( int index, int value )
            {
                if ( index >= 0 && index < getSizeInventory() )
                {
                    getContainerInSlot( index ).setCount( Integer.toUnsignedLong( value ) );
                }
            }

            @Override
            public int size()
            {
                return getSizeInventory();
            }
        };
    }

    @Override
    default int getSizeInventory()
    {
        return getItemHandler().getSlots();
    }

    @Override
    default boolean isEmpty()
    {
        for ( int i = 0; i < getSizeInventory(); i++ )
        {
            if ( !getContainerInSlot( i ).isEmpty() )
            {
                return false;
            }
        }
        return true;
    }

    @Override
    default ItemStack getStackInSlot( int index )
    {
        return getContainerInSlot( index ).getStack().copy();
    }

    @Override
    default ItemStack decrStackSize( int index, int count )
    {
        return getItemHandler().extractItem( index, count, false );
    }

    @Override
    default ItemStack removeStackFromSlot( int index )
    {
        ItemStackContainer container = getItemHandler().removeContainerInSlot( index );
        if ( container.isEmpty() )
        {
            return ItemStack.EMPTY;
        }
        else
        {
            ItemStack stack = container.getStack();
            int size = ( int )Math.min( container.getCount(), stack.getMaxStackSize() );
            return ItemHandlerHelper.copyStackWithSize( stack, size );
        }
    }

    @Override
    default void setInventorySlotContents( int index, ItemStack stack )
    {
        getItemHandler().setStackInSlot( index, stack );
    }

    @Override
    default int getInventoryStackLimit()
    {
        return getItemHandler().getSlotLimit( 0 );
    }

    @Override
    default void clear()
    {
        for ( int i = 0; i < getSizeInventory(); i++ )
        {
            getItemHandler().removeContainerInSlot( i );
        }
    }
}
