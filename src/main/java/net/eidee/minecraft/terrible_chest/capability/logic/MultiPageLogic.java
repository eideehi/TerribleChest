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

import javax.annotation.ParametersAreNonnullByDefault;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import mcp.MethodsReturnNonnullByDefault;
import net.eidee.minecraft.terrible_chest.capability.TerribleChestItem;
import net.eidee.minecraft.terrible_chest.config.Config;

import net.minecraft.nbt.CompoundNBT;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class MultiPageLogic
    extends TerribleChestItemsLogic
{
    private int maxPage;

    public MultiPageLogic( Int2ObjectMap< TerribleChestItem > items )
    {
        super( items );
        this.maxPage = 1;
    }

    public int getMaxPage()
    {
        return maxPage;
    }

    public void unlockMaxPage()
    {
        this.maxPage = Math.min( this.maxPage + 1, Config.COMMON.maxPageLimit.get() );
    }

    @Override
    protected int getInventorySize()
    {
        return maxPage * 27;
    }

    @Override
    public CompoundNBT writeToNBT( CompoundNBT nbt )
    {
        super.writeToNBT( nbt );
        nbt.putInt( "MaxPage", maxPage );
        return nbt;
    }

    @Override
    public void readFromNBT( CompoundNBT nbt )
    {
        super.readFromNBT( nbt );
        maxPage = Math.max( nbt.getInt( "MaxPage" ), Config.COMMON.resetMaxPage.get() );
    }
}
