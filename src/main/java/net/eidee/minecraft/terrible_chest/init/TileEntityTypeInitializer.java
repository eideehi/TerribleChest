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

package net.eidee.minecraft.terrible_chest.init;

import java.util.function.Supplier;
import javax.annotation.ParametersAreNonnullByDefault;

import mcp.MethodsReturnNonnullByDefault;
import net.eidee.minecraft.terrible_chest.TerribleChestMod;
import net.eidee.minecraft.terrible_chest.block.Blocks;
import net.eidee.minecraft.terrible_chest.constants.RegistryNames;
import net.eidee.minecraft.terrible_chest.tileentity.TerribleChestTileEntity;
import net.eidee.minecraft.terrible_chest.tileentity.TerribleChestTileEntity2;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
@Mod.EventBusSubscriber( modid = TerribleChestMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD )
public class TileEntityTypeInitializer
{
    private static < T extends TileEntity > void registerTileEntity( IForgeRegistry< TileEntityType< ? > > registry,
                                                                     String registryName,
                                                                     Supplier< T > factory,
                                                                     Block... blocks )
    {
        TileEntityType< T > type = TileEntityType.Builder.create( factory, blocks ).build( null );
        type.setRegistryName( registryName );
        registry.register( type );
    }

    @SubscribeEvent
    public static void register( RegistryEvent.Register< TileEntityType< ? > > event )
    {
        registerTileEntity( event.getRegistry(),
                            RegistryNames.TERRIBLE_CHEST,
                            TerribleChestTileEntity::new,
                            Blocks.TERRIBLE_CHEST );

        registerTileEntity( event.getRegistry(),
                            RegistryNames.TERRIBLE_CHEST_2,
                            TerribleChestTileEntity2::new,
                            Blocks.TERRIBLE_CHEST_2 );
    }
}
