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

package net.eidee.minecraft.terrible_chest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.eidee.minecraft.terrible_chest.init.CommonProxy;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod( modid = TerribleChest.MOD_ID, version = TerribleChest.VERSION, name = TerribleChest.NAME )
public class TerribleChest
{
    private static final Logger logger;

    public static final String MOD_ID = "terrible_chest";
    public static final String VERSION = "1.12.2-1";
    public static final String NAME = "Terrible Chest";

    @Mod.Instance( MOD_ID )
    public static TerribleChest INSTANCE;

    @SidedProxy( modId = MOD_ID,
                 clientSide = "net.eidee.minecraft.terrible_chest.init.ClientProxy",
                 serverSide = "net.eidee.minecraft.terrible_chest.init.CommonProxy" )
    public static CommonProxy proxy;

    static
    {
        logger = LogManager.getLogger( MOD_ID );
    }

    public static Logger logger()
    {
        return logger;
    }

    @Mod.EventHandler
    private void preInit( FMLPreInitializationEvent event )
    {
        proxy.preInit( event );
    }

    @Mod.EventHandler
    private void init( FMLInitializationEvent event )
    {
        proxy.init( event );
    }
}
