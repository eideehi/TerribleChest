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

import net.eidee.minecraft.terrible_chest.TerribleChest;
import net.eidee.minecraft.terrible_chest.block.Blocks;
import net.eidee.minecraft.terrible_chest.block.TerribleChestBlock;
import net.eidee.minecraft.terrible_chest.constants.Names;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;

@Mod.EventBusSubscriber( modid = TerribleChest.MOD_ID )
public class BlockRegistry
{
    @SubscribeEvent
    public static void register( RegistryEvent.Register< Block > event )
    {
        IForgeRegistry< Block > registry = event.getRegistry();

        Block block;
        {
            block = new TerribleChestBlock().setUnlocalizedName( Names.TERRIBLE_CHEST.replace( ':', '.' ) )
                                            .setRegistryName( Names.TERRIBLE_CHEST )
                                            .setCreativeTab( CreativeTabs.DECORATIONS );
            registry.register( block );
        }
    }

    @SubscribeEvent
    public static void itemRegister( RegistryEvent.Register< Item > event )
    {
        IForgeRegistry< Item > registry = event.getRegistry();

        Item item;
        {
            item = new ItemBlock( Blocks.TERRIBLE_CHEST ).setRegistryName( Names.TERRIBLE_CHEST );
            registry.register( item );
        }
    }

    @SubscribeEvent
    @SideOnly( Side.CLIENT )
    public static void modelRegister( ModelRegistryEvent event )
    {
        Item item = Item.getItemFromBlock( Blocks.TERRIBLE_CHEST );
        ModelResourceLocation key = new ModelResourceLocation( Names.TERRIBLE_CHEST, "inventory" );
        ModelLoader.setCustomModelResourceLocation( item, 0, key );
    }
}
