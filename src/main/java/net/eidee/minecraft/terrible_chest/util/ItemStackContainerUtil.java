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

package net.eidee.minecraft.terrible_chest.util;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.eidee.minecraft.terrible_chest.item.ItemStackContainer;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.common.util.Constants;

public class ItemStackContainerUtil
{
    private ItemStackContainerUtil()
    {
    }

    public static Int2ObjectMap< ItemStackContainer > newContainers()
    {
        Int2ObjectOpenHashMap< ItemStackContainer > containers = new Int2ObjectOpenHashMap<>();
        containers.defaultReturnValue( ItemStackContainer.EMPTY );
        return containers;
    }

    public static void saveAllItems( CompoundNBT nbt, Int2ObjectMap< ItemStackContainer > containers )
    {
        ListNBT list = new ListNBT();
        for ( Int2ObjectMap.Entry< ItemStackContainer > entry : containers.int2ObjectEntrySet() )
        {
            int index = entry.getIntKey();
            ItemStackContainer item = entry.getValue();
            if ( !item.isEmpty() )
            {
                CompoundNBT compound = item.serializeNBT();
                compound.putInt( "Index", index );
                list.add( compound );
            }
        }
        nbt.put( "Items", list );
    }

    public static void loadAllItems( CompoundNBT nbt, Int2ObjectMap< ItemStackContainer > containers )
    {
        ListNBT list = nbt.getList( "Items", Constants.NBT.TAG_COMPOUND );
        for ( int i = 0; i < list.size(); i++ )
        {
            CompoundNBT compound = list.getCompound( i );
            int index = compound.getInt( "Index" );
            ItemStackContainer item = ItemStackContainer.read( compound );
            if ( !item.isEmpty() )
            {
                containers.put( index, item );
            }
        }
    }
}
