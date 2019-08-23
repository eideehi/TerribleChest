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

import net.eidee.minecraft.terrible_chest.inventory.SingleStackInventory;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IntArray;

public class SinglePageContainer
    extends TerribleChestContainer
{
    public SinglePageContainer( int id, PlayerInventory playerInventory )
    {
        this( id, playerInventory, new SingleStackInventory( 133 ), new IntArray( 133 ) );
    }

    public SinglePageContainer( int id,
                                PlayerInventory playerInventory,
                                IInventory chestInventory,
                                IIntArray data )
    {
        super( id, playerInventory, chestInventory, data );

        assertInventorySize( this.chestInventory, 133 );
        assertIntArraySize( this.data, 133 );

        for ( int i = 0; i < 7; ++i )
        {
            for ( int j = 0; j < 19; ++j )
            {
                addSlot( new Slot( this.chestInventory, j + i * 19, 8 + j * 18, 17 + i * 18 ) );
            }
        }

        for ( int i = 0; i < 3; ++i )
        {
            for ( int j = 0; j < 9; ++j )
            {
                addSlot( new Slot( this.playerInventory, j + i * 9 + 9, 98 + j * 18, 156 + i * 18 ) );
            }
        }

        for ( int i = 0; i < 9; ++i )
        {
            addSlot( new Slot( this.playerInventory, i, 98 + i * 18, 214 ) );
        }
    }
}
