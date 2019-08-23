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

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import mcp.MethodsReturnNonnullByDefault;
import net.eidee.minecraft.terrible_chest.capability.logic.MultiPageLogic;
import net.eidee.minecraft.terrible_chest.capability.logic.TerribleChestItemSorters;
import net.eidee.minecraft.terrible_chest.capability.logic.TerribleChestItemsLogic;
import net.eidee.minecraft.terrible_chest.inventory.container.MultiPageContainer;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.IIntArray;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class MultiPageTileEntity
    extends TerribleChestTileEntity< MultiPageLogic, MultiPageTileEntity >
{
    public static final int DATA_PAGE = 27;
    public static final int DATA_MAX_PAGE = 28;
    public static final int DATA_SIZE = 29;

    private int page;

    @Override
    protected MultiPageTileEntity getSelf()
    {
        return this;
    }

    @Override
    protected MultiPageLogic castLogic( TerribleChestItemsLogic logic )
    {
        return ( MultiPageLogic )logic;
    }

    @Override
    protected TerribleChestInventoryWrapper.Factory< MultiPageLogic, MultiPageTileEntity > getInventoryWrapperFactory()
    {
        return Inventory::new;
    }

    @Nullable
    @Override
    public Container createMenu( int id, PlayerInventory playerInventory, PlayerEntity player )
    {
        TerribleChestInventoryWrapper< MultiPageLogic, MultiPageTileEntity > inventory;
        inventory = createInventoryWrapper().orElseThrow( () -> {
            return new IllegalStateException( "テリブルチェストのインベントリラッパー作成に失敗" );
        } );
        return new MultiPageContainer( id, playerInventory, inventory, inventory.getData() );
    }

    @Override
    public void read( CompoundNBT compound )
    {
        super.read( compound );
        page = compound.getInt( "Page" );
    }

    @Override
    public CompoundNBT write( CompoundNBT compound )
    {
        super.write( compound );
        compound.putInt( "Page", page );
        return compound;
    }

    private static class Inventory
        extends TerribleChestInventoryWrapper< MultiPageLogic, MultiPageTileEntity >
    {
        private IIntArray data;

        {
            data = new IIntArray()
            {
                @Override
                public int get( int index )
                {
                    if ( index >= 0 && index < getSizeInventory() )
                    {
                        return logic.getItemCount( getOffset() + index );
                    }
                    else if ( index == DATA_PAGE )
                    {
                        return tileEntity.page;
                    }
                    else if ( index == DATA_MAX_PAGE )
                    {
                        return logic.getMaxPage();
                    }
                    return 0;
                }

                @Override
                public void set( int index, int value )
                {
                    if ( index >= 0 && index < getSizeInventory() )
                    {
                        logic.setItemCount( getOffset() + index, value );
                    }
                    else if ( index == DATA_PAGE )
                    {
                        tileEntity.page = value;
                    }
                    else if ( index == DATA_MAX_PAGE )
                    {
                        logic.unlockMaxPage();
                    }
                }

                @Override
                public int size()
                {
                    return DATA_SIZE;
                }
            };
        }

        Inventory( MultiPageTileEntity tileEntity, MultiPageLogic logic )
        {
            super( tileEntity, logic );
        }

        private int getOffset()
        {
            return tileEntity.page * getSizeInventory();
        }

        @Override
        protected IIntArray getData()
        {
            return data;
        }

        @Override
        public void sort( int sortType )
        {
            int offset = getOffset();
            logic.sort( TerribleChestItemSorters.DEFAULT, offset, offset + getSizeInventory() );
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
            for ( int i = 0; i < getSizeInventory(); i++ )
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
        public void clear()
        {
            int offset = getOffset();
            for ( int i = 0; i < getSizeInventory(); i++ )
            {
                inventory.removeStackFromSlot( offset + i );
            }
        }
    }
}
