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

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import mcp.MethodsReturnNonnullByDefault;
import net.eidee.minecraft.terrible_chest.config.Config;
import net.eidee.minecraft.terrible_chest.util.IntUtil;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.ItemHandlerHelper;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class TerribleChestInventory
    implements IInventory,
               INBTSerializable< NBTTagCompound >
{
    private int maxPage;
    private Int2ObjectMap< Item > items;

    public TerribleChestInventory()
    {
        this.maxPage = 1;
        this.items = new Int2ObjectOpenHashMap<>();
    }

    private boolean isValidIndex( int index )
    {
        return index >= 0 && index < getSizeInventory();
    }

    public int getMaxPage()
    {
        return Math.min( maxPage, Config.maxPageLimit );
    }

    public void setMaxPage( int maxPage )
    {
        this.maxPage = maxPage;
    }

    public int getItemCount( int index )
    {
        if ( isValidIndex( index ) )
        {
            return items.getOrDefault( index, Item.EMPTY ).getCount();
        }
        return 0;
    }

    public void setItemCount( int index, int count )
    {
        if ( isValidIndex( index ) )
        {
            Item item = items.getOrDefault( index, Item.EMPTY );
            if ( item.isNotEmpty() )
            {
                item.setCount( count );
            }
        }
    }

    public void swap( int index1, int index2 )
    {
        Item item1 = items.remove( index1 );
        Item item2 = items.remove( index2 );
        if ( item2 != null && item2.isNotEmpty() )
        {
            items.put( index1, item2 );
        }
        if ( item1 != null && item1.isNotEmpty() )
        {
            items.put( index2, item1 );
        }
    }

    @Override
    public int getSizeInventory()
    {
        return getMaxPage() * 27;
    }

    @Override
    public boolean isEmpty()
    {
        return items.isEmpty();
    }

    @Override
    public ItemStack getStackInSlot( int index )
    {
        return isValidIndex( index ) ? items.getOrDefault( index, Item.EMPTY ).getStack()
                                     : ItemStack.EMPTY;
    }

    @Override
    public ItemStack decrStackSize( int index, int count )
    {
        if ( isValidIndex( index ) )
        {
            Item item = items.getOrDefault( index, Item.EMPTY );
            if ( item.isNotEmpty() )
            {
                int itemCount = item.getCount();
                int _count = IntUtil.minUnsigned( count, itemCount );
                if ( _count == itemCount )
                {
                    items.remove( index );
                    markDirty();
                }
                item.setCount( itemCount - _count );
                return ItemHandlerHelper.copyStackWithSize( item.stack, _count );
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeStackFromSlot( int index )
    {
        if ( isValidIndex( index ) )
        {
            Item item = items.remove( index );
            if ( item != null )
            {
                markDirty();
                return item.getStack();
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
                Item item = items.remove( index );
                if ( item != null )
                {
                    markDirty();
                }
            }
            else
            {
                Item item = new Item( stack );
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
    public boolean isUsableByPlayer( EntityPlayer player )
    {
        return true;
    }

    @Override
    public void clear()
    {
        items.clear();
    }

    @Override
    public NBTTagCompound serializeNBT()
    {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setInteger( "MaxPage", maxPage );

        NBTTagList list = new NBTTagList();
        for ( Int2ObjectMap.Entry< Item > entry : items.int2ObjectEntrySet() )
        {
            Item item = entry.getValue();
            if ( item.isNotEmpty() )
            {
                NBTTagCompound compound = new NBTTagCompound();
                compound.setInteger( "Index", entry.getIntKey() );
                compound.setTag( "Stack", item.getStack().serializeNBT() );
                compound.setInteger( "Count", item.getCount() );
                list.appendTag( compound );
            }
        }
        nbt.setTag( "Items", list );

        return nbt;
    }

    @Override
    public void deserializeNBT( NBTTagCompound nbt )
    {
        maxPage = nbt.getInteger( "MaxPage" );

        NBTTagList list = nbt.getTagList( "Items", Constants.NBT.TAG_COMPOUND );
        items.clear();
        for ( int i = 0; i < list.tagCount(); i++ )
        {
            NBTTagCompound compound = list.getCompoundTagAt( i );
            int index = compound.getInteger( "Index" );
            ItemStack stack = new ItemStack( compound.getCompoundTag( "Stack" ) );
            int count = compound.getInteger( "Count" );

            items.put( index, new Item( stack, count ) );
        }
    }

    @Override
    public int getInventoryStackLimit()
    {
        return Config.slotStackLimit < 0 ? Integer.MAX_VALUE : Config.slotStackLimit;
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
    public boolean isItemValidForSlot( int index, ItemStack stack )
    {
        return true;
    }

    @Override
    public int getField( int id )
    {
        return 0;
    }

    @Override
    public void setField( int id, int value )
    {
    }

    @Override
    public int getFieldCount()
    {
        return 0;
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

    public static final class Item
    {
        static final Item EMPTY = new Item( ItemStack.EMPTY, 0 );

        private ItemStack stack;
        private int count;

        Item( ItemStack stack, int count )
        {
            this.stack = stack.copy();
            this.count = count;
            this.stack.setCount( 1 );
        }

        Item( ItemStack stack )
        {
            this( stack, stack.getCount() );
        }

        boolean isNotEmpty()
        {
            return !stack.isEmpty() && count != 0;
        }

        ItemStack getStack()
        {
            return stack.copy();
        }

        int getCount()
        {
            return count;
        }

        void setCount( int count )
        {
            this.count = count;
        }
    }
}
