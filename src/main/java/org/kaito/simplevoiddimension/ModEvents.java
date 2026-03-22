package org.kaito.simplevoiddimension;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = "simplevoiddimension")
@SuppressWarnings("resource")
public class ModEvents {
    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            ResourceKey<Level> voidDimKey = ResourceKey.create(Registries.DIMENSION, ResourceLocation.fromNamespaceAndPath("simplevoiddimension", "void"));

            // Voidディメンションにいる、かつY座標が-64より下か確認
            if (player.level().dimension() == voidDimKey && player.getY() < -64) {
                ServerLevel overworld = player.server.getLevel(Level.OVERWORLD);
                if (overworld != null) {
                    BlockPos savedPos = player.getData(Simplevoiddimension.RETURN_POS);

                    // 保存された座標がなければ現世のスポーン地点へ
                    BlockPos targetPos = savedPos.equals(BlockPos.ZERO) ? overworld.getSharedSpawnPos() : savedPos;

                    // 現世へ強制移動
                    player.teleportTo(overworld, targetPos.getX() + 0.5, targetPos.getY(), targetPos.getZ() + 0.5, player.getYRot(), player.getXRot());

                    // 修正箇所：メソッドではなくフィールドに直接 0 を代入する
                    player.fallDistance = 0.0F;

                    player.setData(Simplevoiddimension.RETURN_POS, BlockPos.ZERO);
                }
            }
        }
    }
}