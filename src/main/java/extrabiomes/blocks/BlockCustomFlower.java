/**
 * This work is licensed under the Creative Commons
 * Attribution-ShareAlike 3.0 Unported License. To view a copy of this
 * license, visit http://creativecommons.org/licenses/by-sa/3.0/.
 */

package extrabiomes.blocks;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;

import com.google.common.collect.Maps;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import extrabiomes.Extrabiomes;
import extrabiomes.helpers.LogHelper;
import extrabiomes.lib.BiomeSettings;
import extrabiomes.proxy.CommonProxy;

public class BlockCustomFlower extends Block implements IPlantable
{
	public static int	NUM_GROUPS	= 2;	// number of flower groups

    public enum BlockType
    {
    	// group 0 - original flowers
		AUTUMN_SHRUB(0, 0, "autumnshrub", 0, -1),
		HYDRANGEA(0, 1, "hydrangea", 2, 12),
		BUTTERCUP(0, 2, "buttercup", 5, 14), // was "ORANGE"
		LAVENDER(0, 3, "lavender", 5, 5), // was "PURPLE"
		TINY_CACTUS(0, 4, "tinycactus", 5, -1),
		ROOT(0, 5, "root", 0, -1),
		TOADSTOOL(0, 6, "toadstools", 0, -1),
		CALLA_WHITE(0, 7, "calla", 5, 7), // was "WHITE"
        // group 1 - added in 3.15
		ALLIUM(1, 0, "allium", 3, 13),
		AMARYLLIS_PINK(1, 1, "amaryllis_pink", 3, 9),
		AMARYLLIS_RED(1, 2, "amaryllis_red", 3, 1),
		AMARYLLIS_WHITE(1, 3, "amaryllis_white", 3, -1 /* 15 */),
		BACHELORS_BUTTON(1, 4, "bachelorsbutton", 3, -1 /* 4 */),
		BELLS_OF_IRELAND(1, 5, "bellsofireland", 3, 10),
		BLUEBELL(1, 6, "bluebell", 3, 12),
		CALLA_BLACK(1, 7, "calla_black", 3, -1 /* 0 */),
		DAISY(1, 8, "daisy", 3, -1 /* 15 */),
		DANDELION(1, 9, "dandelion", 3, 11),
		EELGRASS(1, 10, "eelgrass", 3, -1),
		GARDENIA(1, 11, "gardenia", 3, 7),
		GERBERA_ORANGE(1, 12, "gerbera_orange", 3, 14),
		GERBERA_PINK(1, 13, "gerbera_pink", 3, 9),
		GERBERA_RED(1, 14, "gerbera_red", 3, 1),
		GERBERA_YELLOW(1, 15, "gerbera_yellow", 3, 11),
        // group 2 - added in 3.15
		ORIENTAL_PINK_LILY(2, 0, "orientalpinklily", 3, 9),
		IRIS_BLUE(2, 1, "iris_blue", 3, -1 /* 4 */),
		IRIS_PURPLE(2, 2, "iris_purple", 3, 5),
		LILY(2, 3, "lily", 3, 13),
		MARSH_MARIGOLD(2, 4, "marshmarigold", 3, 11),
		PANSY(2, 5, "pansy", 3, -1 /* special case, yellow + purple */),
		POPPY(2, 6, "poppy", 3, 1),
		REDROVER(2, 7, "redrover", 3, 1),
		SNAPDRAGON(2, 8, "snapdragon", 3, -1 /* future special case? */),
		TULIP(2, 9, "tulips", 3, 14),
		VIOLET(2, 10, "violet", 3, 5),
		YARROW(2, 11, "yarrow", 3, 11);
        
		private final int		group;
		private final int		metadata;
		private final int		weight;
		private final String	texture;
		private final int		color;		// what color of dye should this make?

		BlockType(int group, int metadata, String texture, int weight, int color) {
			this.group = group;
            this.metadata = metadata;
			this.texture = texture;
			this.weight = weight;
			this.color = color;
		}
        
		public int color() {
			return color;
		}

		public int group() {
			return group;
		}

		public int metadata() {
            return metadata;
        }

		private Icon	icon;
		public Icon getIcon() {
			return icon;
		}

		public Icon registerIcon(IconRegister iconRegister) {
			icon = iconRegister.registerIcon(Extrabiomes.TEXTURE_PATH + this.texture);
			return icon;
		}
    }

	public final int						group;
	private final Map<Integer, BlockType>	groupMap;
	public BlockCustomFlower(int id, int group, Material material)
    {
        super(id, material);
        
        final float offset = 0.2F;
        setBlockBounds(0.5F - offset, 0.0F, 0.5F - offset, 0.5F + offset, offset * 3.0F, 0.5F + offset);
        
        this.group = group;
		this.groupMap = Maps.newHashMap();

        final CommonProxy proxy = Extrabiomes.proxy;
        for( BlockType type : BlockType.values() ) {
			if (type.group == this.group) {
				LogHelper.info(this+": "+group+":"+type.metadata+" = "+type);
				groupMap.put(type.metadata, type);
				if (type.weight > 0) {
					proxy.addGrassPlant(this, type.metadata, type.weight);
				}
        	}
        }
		LogHelper.fine(this.toString() + ": initialized group " + group + ", "
				+ groupMap.size() + " flowers");
    }

	public Collection<BlockType> getGroupTypes() {
		return groupMap.values();
	}
    
    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister iconRegister)
    {
		LogHelper.fine(this.toString() + ": registerIcons");
		for (BlockType type : groupMap.values()) {
			final Icon icon = type.registerIcon(iconRegister);
			if (icon == null)
				LogHelper.warning("No icon found for " + type+" (" + type.group + "," + type.metadata + ")");
			else
				LogHelper.fine(this.toString() + ": " + type + " = " + icon);
		}
    }
    
    @Override
    public boolean canBlockStay(World world, int x, int y, int z)
    {
        return (world.getFullBlockLightValue(x, y, z) >= 8 || world.canBlockSeeTheSky(x, y, z))
                && canThisPlantGrowOnThisBlockID(world.getBlockId(x, y - 1, z));
    }
    
    @Override
    public boolean canPlaceBlockAt(World world, int x, int y, int z)
    {
        return super.canPlaceBlockAt(world, x, y, z) && canThisPlantGrowOnThisBlockID(world.getBlockId(x, y - 1, z));
    }
    
    private boolean canThisPlantGrowOnThisBlockID(int id)
    {
		// TODO: separate rules for edge cases (like cactus)
        return id == Block.grass.blockID || id == Block.dirt.blockID || id == Block.tilledField.blockID || id == Block.sand.blockID || (BiomeSettings.MOUNTAINRIDGE.getBiome().isPresent() && (byte) id == BiomeSettings.MOUNTAINRIDGE.getBiome().get().topBlock);
    }
    
    private void checkFlowerChange(World world, int x, int y, int z)
    {
        if (!canBlockStay(world, x, y, z))
        {
            dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
            world.setBlock(x, y, z, 0);
        }
    }
    
    @Override
    public int damageDropped(int metadata)
    {
        return metadata;
    }
    
    @Override
	@SideOnly(Side.CLIENT)
    public Icon getIcon(int side, int metadata)
    {
		BlockType type = groupMap.get(metadata);
		if( type != null ) {
			return type.getIcon();
		} else {
			return null;
		}
    }
    
    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z)
    {
        return null;
    }
    
    @Override
    public int getPlantID(World world, int x, int y, int z)
    {
        return blockID;
    }
    
    @Override
    public int getPlantMetadata(World world, int x, int y, int z)
    {
        return world.getBlockMetadata(x, y, z);
    }
    
    @Override
    public EnumPlantType getPlantType(World world, int x, int y, int z)
    {
        final int metadata = world.getBlockMetadata(x, y, z);
        if (metadata == BlockType.TINY_CACTUS.metadata())
            return EnumPlantType.Desert;
        return EnumPlantType.Plains;
    }
    
    @Override
    public int getRenderType()
    {
        return 1;
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z)
    {
        final int metadata = world.getBlockMetadata(x, y, z);
        
        if (metadata == BlockType.TINY_CACTUS.metadata())
            return super.getSelectedBoundingBoxFromPool(world, x, y, z);
        
        return AxisAlignedBB.getAABBPool().getAABB(x, y, z, x + 1, y + maxY, z + 1);
        
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(int id, CreativeTabs tab, List itemList)
    {
        for (final BlockType type : BlockType.values()) {
        	if(type.metadata() != 0 && type.metadata() != 5) itemList.add(new ItemStack(this, 1, type.metadata()));
        }
    }
    
    @Override
    public boolean isOpaqueCube()
    {
        return false;
    }
    
    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, int id)
    {
        checkFlowerChange(world, x, y, z);
    }
    
    @Override
    public boolean renderAsNormalBlock()
    {
        return false;
    }
    
    @Override
    public void updateTick(World world, int x, int y, int z, Random rand)
    {
        checkFlowerChange(world, x, y, z);
    }
}
