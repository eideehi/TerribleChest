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

package net.eidee.minecraft.terrible_chest.config;

import net.eidee.minecraft.terrible_chest.TerribleChest;

@net.minecraftforge.common.config.Config( modid = TerribleChest.MOD_ID )
public class Config
{
    @net.minecraftforge.common.config.Config.Comment( "If true, enable ItemHandler capability." )
    @net.minecraftforge.common.config.Config.LangKey( "terrible_chest.config.use_itemhandler_capability" )
    public static boolean useItemHandlerCapability;

    @net.minecraftforge.common.config.Config.Comment( "If true, stop item collection and deliver." )
    @net.minecraftforge.common.config.Config.LangKey( "terrible_chest.config.stop_item_collection_and_deliver" )
    public static boolean stopItemCollectionAndDeliver;

    @net.minecraftforge.common.config.Config.Comment( "Maximum page limit" )
    @net.minecraftforge.common.config.Config.LangKey( "terrible_chest.config.maxPageLimit" )
    @net.minecraftforge.common.config.Config.RangeInt( min = 1 )
    public static int maxPageLimit = Integer.MAX_VALUE;

    @net.minecraftforge.common.config.Config.Comment( "Stack size limit of slot" )
    @net.minecraftforge.common.config.Config.LangKey( "terrible_chest.config.slotStackLimit" )
    @net.minecraftforge.common.config.Config.RangeInt
    public static int slotStackLimit = -1;

    private Config()
    {
    }
}
