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

package net.eidee.minecraft.terrible_chest.registry;

import java.util.function.Supplier;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import com.mojang.datafixers.types.Type;
import mcp.MethodsReturnNonnullByDefault;
import net.eidee.minecraft.terrible_chest.TerribleChest;
import net.eidee.minecraft.terrible_chest.block.Blocks;
import net.eidee.minecraft.terrible_chest.config.Config;
import net.eidee.minecraft.terrible_chest.constants.Names;
import net.eidee.minecraft.terrible_chest.tileentity.MultiPageTileEntity;
import net.eidee.minecraft.terrible_chest.tileentity.SinglePageTileEntity;
import net.eidee.minecraft.terrible_chest.tileentity.TerribleChestTileEntity;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
@Mod.EventBusSubscriber( modid = TerribleChest.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD )
public class TileEntityTypeRegistry
{
    private static < T extends TileEntity > void registerTileEntity( IForgeRegistry< TileEntityType< ? > > registry,
                                                                     String registryName,
                                                                     Supplier< T > factory,
                                                                     @Nullable Type< T > dataFixerType,
                                                                     Block... blocks )
    {
        TileEntityType< T > type = TileEntityType.Builder.create( factory, blocks ).build( dataFixerType );
        type.setRegistryName( registryName );
        registry.register( type );
    }

    @SubscribeEvent
    public static void register( RegistryEvent.Register< TileEntityType< ? > > event )
    {
        Supplier< TerribleChestTileEntity > factory = () ->
            Config.COMMON.useSinglePageMode.get() ? new SinglePageTileEntity()
                                                  : new MultiPageTileEntity();

        registerTileEntity( event.getRegistry(), Names.TERRIBLE_CHEST, factory, null, Blocks.TERRIBLE_CHEST );
    }
}
