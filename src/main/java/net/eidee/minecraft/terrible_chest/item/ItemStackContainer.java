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

package net.eidee.minecraft.terrible_chest.item;

import javax.annotation.ParametersAreNonnullByDefault;

import mcp.MethodsReturnNonnullByDefault;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.items.ItemHandlerHelper;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ItemStackContainer
{
    public static ItemStackContainer EMPTY = new ItemStackContainer( ItemStack.EMPTY, 0 );

    private boolean empty;
    private ItemStack stack;
    private long count;

    private ItemStackContainer( ItemStack stack, long count )
    {
        this.stack = stack;
        this.count = count;
    }

    private ItemStackContainer( CompoundNBT nbt )
    {
        this.stack = ItemStack.read( nbt.getCompound( "Stack" ) );
        this.count = Integer.toUnsignedLong( nbt.getInt( "Count" ) );
    }

    public static ItemStackContainer create( ItemStack stack, int count )
    {
        if ( stack.isEmpty() || count == 0 )
        {
            return EMPTY;
        }
        ItemStack copy = ItemHandlerHelper.copyStackWithSize( stack, 1 );
        ItemStackContainer container = new ItemStackContainer( copy, count );
        container.updateEmptyState();
        return container;
    }

    public static ItemStackContainer create( ItemStack stack )
    {
        return create( stack, stack.getCount() );
    }

    public static ItemStackContainer read( CompoundNBT nbt )
    {
        ItemStackContainer container = new ItemStackContainer( nbt );
        container.updateEmptyState();
        return container;
    }

    private void updateEmptyState()
    {
        empty = isEmpty();
    }

    public boolean isEmpty()
    {
        if ( this == EMPTY )
        {
            return true;
        }
        else if ( stack.isEmpty() )
        {
            return true;
        }
        else
        {
            return count <= 0;
        }
    }

    public ItemStack getStack()
    {
        return empty ? ItemStack.EMPTY : stack;
    }

    public long getCount()
    {
        return empty ? 0 : count;
    }

    public void setCount( long count )
    {
        this.count = count;
        updateEmptyState();
    }

    public void growCount( long count )
    {
        setCount( this.count + count );
    }

    public void shrinkCount( long count )
    {
        growCount( -count );
    }

    public CompoundNBT serializeNBT()
    {
        CompoundNBT nbt = new CompoundNBT();
        nbt.put( "Stack", stack.serializeNBT() );
        nbt.putInt( "Count", ( int )count );
        return nbt;
    }
}
