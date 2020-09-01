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

package net.eidee.minecraft.terrible_chest.capability;

import static net.eidee.minecraft.terrible_chest.TerribleChestMod.MOD_ID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import mcp.MethodsReturnNonnullByDefault;
import net.eidee.minecraft.terrible_chest.item.ItemStackContainer;
import net.eidee.minecraft.terrible_chest.settings.Config;
import net.eidee.minecraft.terrible_chest.util.ItemStackContainerUtil;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class TerribleChestCapability
    implements INBTSerializable< CompoundNBT >
{
    public static final ResourceLocation REGISTRY_KEY;

    static
    {
        REGISTRY_KEY = new ResourceLocation( MOD_ID, "terrible_chest" );
    }

    private Int2ObjectMap< ItemStackContainer > containers;
    private int maxPage;

    public TerribleChestCapability()
    {
        this.containers = ItemStackContainerUtil.newContainers();
        this.maxPage = 1;
    }

    public Int2ObjectMap< ItemStackContainer > getContainers()
    {
        return containers;
    }

    public int getMaxPage()
    {
        return maxPage;
    }

    public void setMaxPage( int maxPage )
    {
        this.maxPage = maxPage;
    }

    @Override
    public CompoundNBT serializeNBT()
    {
        CompoundNBT nbt = new CompoundNBT();
        ItemStackContainerUtil.saveAllItems( nbt, containers );
        nbt.putInt( "MaxPage", maxPage );
        return nbt;
    }

    @Override
    public void deserializeNBT( CompoundNBT nbt )
    {
        containers.clear();
        ItemStackContainerUtil.loadAllItems( nbt, containers );
        maxPage = Math.max( nbt.getInt( "MaxPage" ), Config.COMMON.resetMaxPage.get() );
    }

    public static Capability.IStorage< TerribleChestCapability > storage()
    {
        return new Capability.IStorage< TerribleChestCapability >()
        {
            @Override
            public INBT writeNBT( Capability< TerribleChestCapability > capability,
                                  TerribleChestCapability instance,
                                  Direction side )
            {
                return instance.serializeNBT();
            }

            @Override
            public void readNBT( Capability< TerribleChestCapability > capability,
                                 TerribleChestCapability instance,
                                 Direction side,
                                 INBT nbt )
            {
                if ( nbt instanceof CompoundNBT )
                {
                    instance.deserializeNBT( ( CompoundNBT )nbt );
                }
            }
        };
    }

    public static ICapabilitySerializable< CompoundNBT > provider()
    {
        Capability< TerribleChestCapability > _cap = Capabilities.TERRIBLE_CHEST;
        TerribleChestCapability instance = _cap.getDefaultInstance();
        return new ICapabilitySerializable< CompoundNBT >()
        {
            @Nonnull
            @Override
            public < T > LazyOptional< T > getCapability( @Nonnull Capability< T > cap, @Nullable Direction side )
            {
                return _cap.orEmpty( cap, LazyOptional.of( instance == null ? null : () -> instance ) );
            }

            @Override
            public CompoundNBT serializeNBT()
            {
                INBT inbt = _cap.writeNBT( instance, null );
                return inbt instanceof CompoundNBT ? ( CompoundNBT )inbt : new CompoundNBT();
            }

            @Override
            public void deserializeNBT( CompoundNBT nbt )
            {
                _cap.readNBT( instance, null, nbt );
            }
        };
    }
}
