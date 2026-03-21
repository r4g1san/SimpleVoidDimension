package org.kaito.simplevoiddimension;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

@Mod("simplevoiddimension")
public class Simplevoiddimension {
    public static final String MODID = "simplevoiddimension";
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, MODID);

    public static final Supplier<AttachmentType<BlockPos>> RETURN_POS = ATTACHMENT_TYPES.register(
            "return_pos", () -> AttachmentType.builder(() -> BlockPos.ZERO).serialize(BlockPos.CODEC).build()
    );

    public static final DeferredBlock<Block> DIMENSION_BLOCK = BLOCKS.register("dimension_block",
            () -> new DimensionBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)));

    public static final DeferredItem<Item> DIMENSION_BLOCK_ITEM = ITEMS.register("dimension_block",
            () -> new BlockItem(DIMENSION_BLOCK.get(), new Item.Properties()));

    public Simplevoiddimension(IEventBus modEventBus) {
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        ATTACHMENT_TYPES.register(modEventBus);

        // クリエイティブタブへの登録イベントをリスニング
        modEventBus.addListener(this::addCreative);
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        // 「機能」タブなどに表示
        if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            event.accept(DIMENSION_BLOCK_ITEM);
        }
    }
}