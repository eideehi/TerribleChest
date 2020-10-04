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

import java.util.function.IntSupplier;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.IntIterator;
import mcp.MethodsReturnNonnullByDefault;
import net.eidee.minecraft.terrible_chest.config.Config;
import net.eidee.minecraft.terrible_chest.util.ItemStackContainerUtil;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public abstract class TerribleChestItemHandler
    implements IItemHandlerModifiable
{
    public static TerribleChestItemHandler create( Int2ObjectMap< ItemStackContainer > containers,
                                                   IntSupplier offset,
                                                   IntSupplier length )
    {
        return new TerribleChestItemHandler()
        {
            @Override
            protected Int2ObjectMap< ItemStackContainer > getContainers()
            {
                return containers;
            }

            @Override
            protected int getOffset()
            {
                return offset.getAsInt();
            }

            @Override
            public int getSlots()
            {
                return length.getAsInt();
            }
        };
    }

    public static TerribleChestItemHandler create( Int2ObjectMap< ItemStackContainer > containers,
                                                   int offset,
                                                   int length )
    {
        return new TerribleChestItemHandler()
        {
            @Override
            protected Int2ObjectMap< ItemStackContainer > getContainers()
            {
                return containers;
            }

            @Override
            protected int getOffset()
            {
                return offset;
            }

            @Override
            public int getSlots()
            {
                return length;
            }
        };
    }

    public static TerribleChestItemHandler dummy()
    {
        Int2ObjectMap< ItemStackContainer > containers = ItemStackContainerUtil.newContainers();
        return new TerribleChestItemHandler()
        {
            @Override
            protected Int2ObjectMap< ItemStackContainer > getContainers()
            {
                return containers;
            }

            @Override
            protected int getOffset()
            {
                return 0;
            }
        };
    }

    protected abstract Int2ObjectMap< ItemStackContainer > getContainers();

    protected abstract int getOffset();

    public ItemStackContainer getContainerInSlot( int slot )
    {
        return getContainers().getOrDefault( getOffset() + slot, ItemStackContainer.EMPTY );
    }

    public void setContainerInSlot( int slot, ItemStackContainer container )
    {
        getContainers().put( getOffset() + slot, container );
    }

    public ItemStackContainer removeContainerInSlot( int slot )
    {
        return getContainers().remove( getOffset() + slot );
    }

    public long getSlotFreeSpace( int slot )
    {
        return getSlotLimit( slot ) - getContainerInSlot( slot ).getCount();
    }

    @Override
    public void setStackInSlot( int slot, @Nonnull ItemStack stack )
    {
        setContainerInSlot( slot, ItemStackContainer.create( stack ) );
    }

    @Override
    public int getSlots()
    {
        return 27;
    }

    @Nonnull
    @Override
    public ItemStack getStackInSlot( int slot )
    {
        return getContainerInSlot( slot ).getStack();
    }

    @Nonnull
    @Override
    public ItemStack insertItem( int slot, @Nonnull ItemStack stack, boolean simulate )
    {
        if ( stack.isEmpty() )
        {
            return ItemStack.EMPTY;
        }

        if ( !isItemValid( slot, stack ) )
        {
            return stack;
        }

        ItemStackContainer container = getContainerInSlot( slot );
        ItemStack stackInSlot = container.getStack();

        long limit = getSlotLimit( slot );

        if ( !container.isEmpty() )
        {
            if ( !ItemHandlerHelper.canItemStacksStack( stack, stackInSlot ) )
            {
                return stack;
            }
            limit -= container.getCount();
        }

        if ( limit <= 0 )
        {
            return stack;
        }

        int toInsert = ( int )Math.min( stack.getCount(), limit );

        if ( !simulate )
        {
            if ( container.isEmpty() )
            {
                setContainerInSlot( slot, ItemStackContainer.create( stack, toInsert ) );
            }
            else
            {
                container.growCount( toInsert );
            }
        }

        return ItemHandlerHelper.copyStackWithSize( stack, stack.getCount() - toInsert );
    }

    @Nonnull
    @Override
    public ItemStack extractItem( int slot, int amount, boolean simulate )
    {
        if ( amount == 0 )
        {
            return ItemStack.EMPTY;
        }

        ItemStackContainer container = getContainerInSlot( slot );

        if ( container.isEmpty() )
        {
            return ItemStack.EMPTY;
        }

        ItemStack stackInSlot = container.getStack();
        long stackCount = container.getCount();
        int toExtract = ( int )Math.min( Math.min( amount, stackCount ), stackInSlot.getMaxStackSize() );

        if ( !simulate )
        {
            if ( stackCount > toExtract )
            {
                container.shrinkCount( toExtract );
            }
            else
            {
                removeContainerInSlot( slot );
            }
        }

        return ItemHandlerHelper.copyStackWithSize( stackInSlot, toExtract );
    }

    @Override
    public int getSlotLimit( int slot )
    {
        return Config.slotStackLimit < 0 ? Integer.MAX_VALUE : Config.slotStackLimit;
    }
}
