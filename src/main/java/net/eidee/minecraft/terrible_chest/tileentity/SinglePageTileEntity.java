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
import net.eidee.minecraft.terrible_chest.capability.logic.SinglePageLogic;
import net.eidee.minecraft.terrible_chest.capability.logic.TerribleChestItemsLogic;
import net.eidee.minecraft.terrible_chest.inventory.container.SinglePageContainer;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.IIntArray;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class SinglePageTileEntity
    extends TerribleChestTileEntity< SinglePageLogic, SinglePageTileEntity >
{
    @Override
    protected SinglePageTileEntity getSelf()
    {
        return this;
    }

    @Override
    protected SinglePageLogic castLogic( TerribleChestItemsLogic logic )
    {
        return ( SinglePageLogic )logic;
    }

    @Override
    protected TerribleChestInventoryWrapper.Factory< SinglePageLogic, SinglePageTileEntity > getInventoryWrapperFactory()
    {
        return Inventory::new;
    }

    @Nullable
    @Override
    public Container createMenu( int id, PlayerInventory playerInventory, PlayerEntity playerEntity )
    {
        TerribleChestInventoryWrapper< SinglePageLogic, SinglePageTileEntity > inventory;
        inventory = createInventoryWrapper().orElseThrow( () -> {
            return new IllegalStateException( "テリブルチェストのインベントリラッパー作成に失敗" );
        } );
        return new SinglePageContainer( id, playerInventory, inventory, inventory.getData() );
    }

    private static class Inventory
        extends TerribleChestInventoryWrapper< SinglePageLogic, SinglePageTileEntity >
    {
        private IIntArray data;

        {
            data = new IIntArray()
            {
                @Override
                public int get( int index )
                {
                    if ( index >= 0 && index < inventory.getSizeInventory() )
                    {
                        return logic.getItemCount( index );
                    }
                    return 0;
                }

                @Override
                public void set( int index, int value )
                {
                    if ( index >= 0 && index < inventory.getSizeInventory() )
                    {
                        logic.setItemCount( index, value );
                    }
                }

                @Override
                public int size()
                {
                    return inventory.getSizeInventory();
                }
            };
        }

        Inventory( SinglePageTileEntity tileEntity, SinglePageLogic logic )
        {
            super( tileEntity, logic );
        }

        @Override
        protected IIntArray getData()
        {
            return data;
        }
    }
}
