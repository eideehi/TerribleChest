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

package net.eidee.minecraft.terrible_chest.capability;

import javax.annotation.ParametersAreNonnullByDefault;

import mcp.MethodsReturnNonnullByDefault;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public final class TerribleChestItem
    implements INBTSerializable< CompoundNBT >
{
    public static final TerribleChestItem EMPTY = new TerribleChestItem( ItemStack.EMPTY, 0 );

    private ItemStack stack;
    private int count;

    public TerribleChestItem( ItemStack stack, int count )
    {
        this.stack = stack.copy();
        this.count = count;
        this.stack.setCount( 1 );
    }

    public TerribleChestItem( ItemStack stack )
    {
        this( stack, stack.getCount() );
    }

    public static TerribleChestItem read( CompoundNBT nbt )
    {
        TerribleChestItem item = new TerribleChestItem( ItemStack.EMPTY );
        item.deserializeNBT( nbt );
        return item;
    }

    public boolean isEmpty()
    {
        return stack.isEmpty() || count == 0;
    }

    public boolean isNotEmpty()
    {
        return !isEmpty();
    }

    public ItemStack getStack()
    {
        return isEmpty() ? ItemStack.EMPTY : stack.copy();
    }

    public int getCount()
    {
        return count;
    }

    public void setCount( int count )
    {
        this.count = count;
    }

    @Override
    public CompoundNBT serializeNBT()
    {
        CompoundNBT nbt = new CompoundNBT();
        nbt.put( "Stack", stack.serializeNBT() );
        nbt.putInt( "Count", count );
        return nbt;
    }

    @Override
    public void deserializeNBT( CompoundNBT nbt )
    {
        stack = ItemStack.read( nbt.getCompound( "Stack" ) );
        count = nbt.getInt( "Count" );
    }
}
