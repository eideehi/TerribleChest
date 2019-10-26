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

package net.eidee.minecraft.terrible_chest.inventory;

import javax.annotation.ParametersAreNonnullByDefault;

import mcp.MethodsReturnNonnullByDefault;
import net.eidee.minecraft.terrible_chest.util.IntUtil;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ItemHandler
    implements IItemHandler
{
    private TerribleChestInventory inventory;

    public ItemHandler( TerribleChestInventory inventory )
    {
        this.inventory = inventory;
    }

    @Override
    public int getSlots()
    {
        return inventory.getSizeInventory();
    }

    @Override
    public ItemStack getStackInSlot( int slot )
    {
        return inventory.getStackInSlot( slot );
    }

    @Override
    public ItemStack insertItem( int slot, ItemStack stack, boolean simulate )
    {
        if ( stack.isEmpty() )
        {
            return ItemStack.EMPTY;
        }

        ItemStack stackInSlot = inventory.getStackInSlot( slot );

        int m;
        int slotLimit = getSlotLimit( slot );
        if ( !stackInSlot.isEmpty() )
        {
            int count = IntUtil.minUnsigned( inventory.getItemCount( slot ), slotLimit );
            if ( count >= slotLimit )
            {
                return stack;
            }

            if ( !ItemHandlerHelper.canItemStacksStack( stack, stackInSlot ) )
            {
                return stack;
            }

            if ( !inventory.isItemValidForSlot( slot, stack ) )
            {
                return stack;
            }

            m = slotLimit - count;

            if ( stack.getCount() <= m )
            {
                if ( !simulate )
                {
                    ItemStack copy = stack.copy();
                    copy.grow( count );
                    inventory.setInventorySlotContents( slot, copy );
                    inventory.markDirty();
                }

                return ItemStack.EMPTY;
            }
            else
            {
                // copy the stack to not modify the original one
                stack = stack.copy();
                if ( !simulate )
                {
                    ItemStack copy = stack.splitStack( m );
                    copy.grow( count );
                    inventory.setInventorySlotContents( slot, copy );
                    inventory.markDirty();
                    return stack;
                }
                else
                {
                    stack.shrink( m );
                    return stack;
                }
            }
        }
        else
        {
            if ( !inventory.isItemValidForSlot( slot, stack ) )
            {
                return stack;
            }

            m = slotLimit;
            if ( m < stack.getCount() )
            {
                // copy the stack to not modify the original one
                stack = stack.copy();
                if ( !simulate )
                {
                    inventory.setInventorySlotContents( slot, stack.splitStack( m ) );
                    inventory.markDirty();
                    return stack;
                }
                else
                {
                    stack.shrink( m );
                    return stack;
                }
            }
            else
            {
                if ( !simulate )
                {
                    inventory.setInventorySlotContents( slot, stack );
                    inventory.markDirty();
                }
                return ItemStack.EMPTY;
            }
        }
    }

    @Override
    public ItemStack extractItem( int slot, int amount, boolean simulate )
    {
        if ( amount == 0 )
        {
            return ItemStack.EMPTY;
        }

        ItemStack stackInSlot = inventory.getStackInSlot( slot );

        if ( stackInSlot.isEmpty() )
        {
            return ItemStack.EMPTY;
        }

        if ( simulate )
        {
            if ( stackInSlot.getCount() < amount )
            {
                return stackInSlot.copy();
            }
            else
            {
                ItemStack copy = stackInSlot.copy();
                copy.setCount( amount );
                return copy;
            }
        }
        else
        {
            int m = Math.min( stackInSlot.getCount(), amount );

            ItemStack decrStackSize = inventory.decrStackSize( slot, m );
            inventory.markDirty();
            return decrStackSize;
        }
    }

    @Override
    public int getSlotLimit( int slot )
    {
        return inventory.getInventoryStackLimit();
    }

    @Override
    public boolean isItemValid( int slot, ItemStack stack )
    {
        return inventory.isItemValidForSlot( slot, stack );
    }
}
