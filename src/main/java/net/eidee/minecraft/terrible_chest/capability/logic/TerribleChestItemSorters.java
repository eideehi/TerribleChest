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

package net.eidee.minecraft.terrible_chest.capability.logic;

import java.util.Comparator;

import net.eidee.minecraft.terrible_chest.capability.TerribleChestItem;

import net.minecraft.util.ResourceLocation;

public class TerribleChestItemSorters
{
    public static final Comparator< TerribleChestItem > DEFAULT;

    public static final Comparator< TerribleChestItem > ITEM_NAME;

    public static final Comparator< TerribleChestItem > ITEM_COUNT;

    public static final Comparator< TerribleChestItem > MOD_ID;

    static
    {
        ITEM_NAME = ( item1, item2 ) -> {
            ResourceLocation registryName1 = item1.getStack().getItem().getRegistryName();
            ResourceLocation registryName2 = item2.getStack().getItem().getRegistryName();
            if ( registryName1 != null && registryName2 != null )
            {
                return registryName1.getPath().compareTo( registryName2.getPath() );
            }
            else if ( registryName1 == null && registryName2 == null )
            {
                return 0;
            }
            return registryName1 == null ? 1 : -1;
        };
        ITEM_COUNT = ( item1, item2 ) -> {
            return Integer.compare( item1.getCount(), item2.getCount() );
        };
        MOD_ID = ( item1, item2 ) -> {
            ResourceLocation registryName1 = item1.getStack().getItem().getRegistryName();
            ResourceLocation registryName2 = item2.getStack().getItem().getRegistryName();
            if ( registryName1 != null && registryName2 != null )
            {
                return registryName1.getNamespace().compareTo( registryName2.getNamespace() );
            }
            else if ( registryName1 == null && registryName2 == null )
            {
                return 0;
            }
            return registryName1 == null ? 1 : -1;
        };
        DEFAULT = ITEM_NAME.thenComparing( MOD_ID ).thenComparing( ITEM_COUNT.reversed() );
    }
}
