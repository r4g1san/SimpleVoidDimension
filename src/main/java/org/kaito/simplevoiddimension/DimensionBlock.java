package org.kaito.simplevoiddimension;

import javax.annotation.Nonnull;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class DimensionBlock extends Block {
    public DimensionBlock(Properties properties) {
        super(properties);
    }

    @Override
    @Nonnull
    protected InteractionResult useWithoutItem(
            @Nonnull BlockState state,
            @Nonnull Level level,
            @Nonnull BlockPos pos,
            @Nonnull Player player,
            @Nonnull BlockHitResult hitResult) {
        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            ResourceKey<Level> voidDimKey = ResourceKey.create(Registries.DIMENSION, ResourceLocation.fromNamespaceAndPath("simplevoiddimension", "void"));

            ServerLevel targetLevel;
            BlockPos targetPos;

            if (level.dimension() == voidDimKey) {
                // 帰還処理
                targetLevel = serverPlayer.server.getLevel(Level.OVERWORLD);
                if (targetLevel == null) return InteractionResult.PASS;

                // 保存された座標を取得
                BlockPos savedPos = serverPlayer.getData(Simplevoiddimension.RETURN_POS);

                // 初期値(0,0,0)でなければその座標へ、そうでなければスポーン地点へ
                if (!savedPos.equals(BlockPos.ZERO)) {
                    targetPos = savedPos;
                    // 使用後はデータをリセット
                    serverPlayer.setData(Simplevoiddimension.RETURN_POS, BlockPos.ZERO);
                } else {
                    targetPos = targetLevel.getSharedSpawnPos();
                }
            } else {
                // 移動処理
                targetLevel = serverPlayer.server.getLevel(voidDimKey);
                if (targetLevel == null) return InteractionResult.PASS;

                // 現在の座標をプレイヤーのデータとして保存（永続化）
                serverPlayer.setData(Simplevoiddimension.RETURN_POS, serverPlayer.blockPosition());

                targetPos = new BlockPos(0, 64, 0);
                generatePlatform(targetLevel, targetPos);
            }

            serverPlayer.teleportTo(targetLevel, targetPos.getX() + 0.5, targetPos.getY(), targetPos.getZ() + 0.5, serverPlayer.getYRot(), serverPlayer.getXRot());
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.SUCCESS;
    }

    private void generatePlatform(ServerLevel level, BlockPos center) {
        if (!level.getBlockState(center.below()).isAir()) return;
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                level.setBlockAndUpdate(center.offset(x, -1, z), Blocks.STONE.defaultBlockState());
            }
        }
        level.setBlockAndUpdate(center, this.defaultBlockState());
    }
}