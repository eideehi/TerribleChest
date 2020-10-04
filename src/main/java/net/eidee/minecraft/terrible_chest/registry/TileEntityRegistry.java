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

import static net.eidee.minecraft.terrible_chest.TerribleChest.MOD_ID;

import javax.annotation.ParametersAreNonnullByDefault;

import mcp.MethodsReturnNonnullByDefault;
import net.eidee.minecraft.terrible_chest.constants.Names;
import net.eidee.minecraft.terrible_chest.tileentity.TerribleChestTileEntity;
import net.eidee.minecraft.terrible_chest.tileentity.TerribleChestTileEntity2;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class TileEntityRegistry
{

    private static final ResourceLocation KEY_TERRIBLE_CHEST;
    private static final ResourceLocation KEY_TERRIBLE_CHEST_2;

    static
    {
        KEY_TERRIBLE_CHEST = new ResourceLocation( MOD_ID, Names.TERRIBLE_CHEST );
        KEY_TERRIBLE_CHEST_2 = new ResourceLocation( MOD_ID, Names.TERRIBLE_CHEST_2 );
    }

    public static void register()
    {
        GameRegistry.registerTileEntity( TerribleChestTileEntity.class, KEY_TERRIBLE_CHEST );
        GameRegistry.registerTileEntity( TerribleChestTileEntity2.class, KEY_TERRIBLE_CHEST_2 );
    }
}
