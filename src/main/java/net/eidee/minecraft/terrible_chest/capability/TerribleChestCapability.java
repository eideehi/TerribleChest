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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import mcp.MethodsReturnNonnullByDefault;
import net.eidee.minecraft.terrible_chest.TerribleChest;
import net.eidee.minecraft.terrible_chest.item.ItemStackContainer;
import net.eidee.minecraft.terrible_chest.util.ItemStackContainerUtil;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.INBTSerializable;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class TerribleChestCapability
    implements INBTSerializable< NBTTagCompound >
{
    public static final ResourceLocation REGISTRY_KEY;

    static
    {
        REGISTRY_KEY = new ResourceLocation( TerribleChest.MOD_ID, "terrible_chest" );
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
    public NBTTagCompound serializeNBT()
    {
        NBTTagCompound nbt = new NBTTagCompound();
        ItemStackContainerUtil.saveAllItems( nbt, containers );
        nbt.setInteger( "MaxPage", maxPage );
        return nbt;
    }

    @Override
    public void deserializeNBT( NBTTagCompound nbt )
    {
        containers.clear();
        ItemStackContainerUtil.loadAllItems( nbt, containers );
        maxPage = nbt.getInteger( "MaxPage" );
    }

    public static class Storage
        implements Capability.IStorage< TerribleChestCapability >
    {
        @Nullable
        @Override
        public NBTBase writeNBT( Capability< TerribleChestCapability > capability,
                                 TerribleChestCapability instance,
                                 EnumFacing side )
        {
            return instance.serializeNBT();
        }

        @Override
        public void readNBT( Capability< TerribleChestCapability > capability,
                             TerribleChestCapability instance,
                             EnumFacing side,
                             NBTBase nbt )
        {
            if ( nbt instanceof NBTTagCompound )
            {
                instance.deserializeNBT( ( NBTTagCompound )nbt );
            }
        }

    }

    public static class Provider
        implements ICapabilitySerializable< NBTTagCompound >
    {
        private final Capability< TerribleChestCapability > _cap;
        private TerribleChestCapability instance;

        public Provider( Capability< TerribleChestCapability > _cap )
        {
            this._cap = _cap;
            this.instance = this._cap.getDefaultInstance();
        }

        @Override
        public boolean hasCapability( @Nonnull Capability< ? > capability, @Nullable EnumFacing facing )
        {
            return capability == _cap;
        }

        @SuppressWarnings( "unchecked" )
        @Nullable
        @Override
        public < T > T getCapability( @Nonnull Capability< T > capability, @Nullable EnumFacing facing )
        {
            return capability == _cap ? ( T )instance : null;
        }

        @Override
        public NBTTagCompound serializeNBT()
        {
            return ( NBTTagCompound )_cap.writeNBT( instance, null );
        }

        @Override
        public void deserializeNBT( NBTTagCompound nbt )
        {
            _cap.readNBT( instance, null, nbt );
        }
    }
}
