/*
 * MIT License
 *
 * Copyright (c) 2020 Azercoco & Technici4n
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
package aztech.modern_industrialization.blocks.creativetank;

import aztech.modern_industrialization.ModernIndustrialization;
import aztech.modern_industrialization.tools.IWrenchable;
import aztech.modern_industrialization.util.MobSpawning;
import java.util.Arrays;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class CreativeTankBlock extends Block implements BlockEntityProvider, IWrenchable {
    public CreativeTankBlock(Settings settings) {
        super(settings.nonOpaque().allowsSpawning(MobSpawning.NO_SPAWN));
    }

    @Override
    public BlockEntity createBlockEntity(BlockView world) {
        return new CreativeTankBlockEntity();
    }

    public boolean isTranslucent(BlockState state, BlockView world, BlockPos pos) {
        return true;
    }

    @Override
    public int getOpacity(BlockState state, BlockView world, BlockPos pos) {
        return 0;
    }

    private ItemStack getStack(BlockEntity entity) {
        CreativeTankBlockEntity tankEntity = (CreativeTankBlockEntity) entity;
        ItemStack stack = new ItemStack(asItem());
        if (!tankEntity.isEmpty()) {
            CompoundTag tag = new CompoundTag();
            tag.put("BlockEntityTag", tankEntity.toClientTag(new CompoundTag()));
            stack.setTag(tag);
        }
        return stack;
    }

    @Override
    public List<ItemStack> getDroppedStacks(BlockState state, LootContext.Builder builder) {
        LootContext lootContext = builder.parameter(LootContextParameters.BLOCK_STATE, state).build(LootContextTypes.BLOCK);
        return Arrays.asList(getStack(lootContext.get(LootContextParameters.BLOCK_ENTITY)));
    }

    @Override
    public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
        return getStack(world.getBlockEntity(pos));
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (player.isSneaking() && ModernIndustrialization.WRENCHES.contains(player.inventory.getMainHandStack().getItem())) {
            world.spawnEntity(new ItemEntity(world, hit.getPos().x, hit.getPos().y, hit.getPos().z, getStack(world.getBlockEntity(pos))));
            world.setBlockState(pos, Blocks.AIR.getDefaultState());
            // TODO: play sound
            return ActionResult.success(world.isClient);
        } else if (((CreativeTankBlockEntity) world.getBlockEntity(pos)).onPlayerUse(player)) {
            return ActionResult.success(world.isClient);
        }
        return ActionResult.PASS;
    }

    @Override
    public ActionResult onWrenchUse(ItemUsageContext context) {
        // FIXME: create an api for this.
        BlockHitResult hit = new BlockHitResult(context.getHitPos(), context.getSide(), context.getBlockPos(), context.hitsInsideBlock());
        return onUse(context.getWorld().getBlockState(hit.getBlockPos()), context.getWorld(), hit.getBlockPos(), context.getPlayer(),
                context.getHand(), hit);
    }
}
