package net.eidee.minecraft.terrible_chest.tileentity;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.UUID;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import mcp.MethodsReturnNonnullByDefault;
import net.eidee.minecraft.terrible_chest.capability.Capabilities;
import net.eidee.minecraft.terrible_chest.capability.TerribleChestCapability;
import net.eidee.minecraft.terrible_chest.inventory.TerribleChestInventory;
import net.eidee.minecraft.terrible_chest.inventory.container.TerribleChestContainer;
import net.eidee.minecraft.terrible_chest.item.ItemStackContainer;
import net.eidee.minecraft.terrible_chest.item.TerribleChestItemHandler;
import net.eidee.minecraft.terrible_chest.settings.Config;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIntArray;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class TerribleChestTileEntity
    extends TileEntity
    implements INamedContainerProvider
{
    static ITextComponent CONTAINER_NAME = new TranslationTextComponent( "container.terrible_chest.terrible_chest" );

    private UUID ownerId;
    private int page;

    public TerribleChestTileEntity()
    {
        super( TileEntityTypes.TERRIBLE_CHEST );
        this.ownerId = null;
        this.page = 0;
    }

    private void dataFix( CompoundNBT compound )
    {
        if ( compound.contains( "OwnerIdMost" ) && compound.contains( "OwnerIdLeast" ) )
        {
            UUID uuid = new UUID( compound.getLong( "OwnerIdMost" ), compound.getLong( "OwnerIdLeast" ) );
            compound.putUniqueId( "OwnerId", uuid );
        }
    }

    private TerribleChestItemHandler createItemHandler( Int2ObjectMap< ItemStackContainer > containers )
    {
        if ( Config.COMMON.useSinglePageMode.get() )
        {
            return TerribleChestItemHandler.create( containers, 0, 133 );
        }
        else
        {
            int slots = Config.COMMON.inventoryRows.get() * 9;
            return TerribleChestItemHandler.create( containers, () -> page * slots, () -> slots );
        }
    }

    private TerribleChestInventory createInventory( TerribleChestCapability chest )
    {
        TerribleChestItemHandler itemHandler = createItemHandler( chest.getContainers() );
        return new TerribleChestInventory()
        {
            @Override
            public TerribleChestItemHandler getItemHandler()
            {
                return itemHandler;
            }

            @Override
            public void markDirty()
            {
            }

            @Override
            public boolean isUsableByPlayer( PlayerEntity player )
            {
                World world = getWorld();
                BlockPos pos = getPos();
                if ( world == null || world.getTileEntity( pos ) != TerribleChestTileEntity.this )
                {
                    return false;
                }
                else if ( !isOwner( player ) )
                {
                    return false;
                }
                else
                {
                    return !( player.getDistanceSq( pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D ) > 64.0D );
                }
            }
        };
    }

    private IIntArray createChestData( TerribleChestCapability chest )
    {
        return new IIntArray()
        {
            @Override
            public int get( int index )
            {
                if ( index == TerribleChestContainer.DATA_MAX_PAGE )
                {
                    return chest.getMaxPage();
                }
                if ( index == TerribleChestContainer.DATA_CURRENT_PAGE )
                {
                    return page;
                }
                return 0;
            }

            @Override
            public void set( int index, int value )
            {
                if ( index == TerribleChestContainer.DATA_MAX_PAGE )
                {
                    chest.setMaxPage( value );
                }
                if ( index == TerribleChestContainer.DATA_CURRENT_PAGE )
                {
                    page = value;
                }
            }

            @Override
            public int size()
            {
                return 2;
            }
        };
    }

    public void setOwnerId( UUID ownerId )
    {
        this.ownerId = ownerId;
    }

    public boolean isOwner( PlayerEntity player )
    {
        return Objects.equals( ownerId, player.getUniqueID() );
    }

    @Override
    public ITextComponent getDisplayName()
    {
        return CONTAINER_NAME;
    }

    @Nullable
    @Override
    public Container createMenu( int id, PlayerInventory playerInventory, PlayerEntity playerEntity )
    {
        TerribleChestCapability chest = playerEntity.getCapability( Capabilities.TERRIBLE_CHEST )
                                                    .orElseThrow( NoSuchElementException::new );
        return new TerribleChestContainer( id, playerInventory, createInventory( chest ), createChestData( chest ) );
    }

    @Override
    public void read( BlockState state, CompoundNBT compound )
    {
        super.read( state, compound );

        dataFix( compound );

        ownerId = null;
        if ( compound.hasUniqueId( "OwnerId" ) )
        {
            ownerId = compound.getUniqueId( "OwnerId" );
        }
        page = compound.getInt( "Page" );
    }

    @Override
    public CompoundNBT write( CompoundNBT compound )
    {
        super.write( compound );
        if ( ownerId != null )
        {
            compound.putUniqueId( "OwnerId", ownerId );
        }
        compound.putInt( "Page", page );
        return compound;
    }
}
