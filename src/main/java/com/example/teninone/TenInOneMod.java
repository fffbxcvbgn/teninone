package com.example.teninone;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod(TenInOneMod.MODID)
public class TenInOneMod {
    public static final String MODID = "teninone";

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final RegistryObject<Block> REACTOR = BLOCKS.register("reactor",
            () -> new ReactorBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).lightLevel(s -> 15).strength(5.0F, 6.0F)));

    public static final RegistryObject<Item> REACTOR_ITEM = ITEMS.register("reactor",
            () -> new BlockItem(REACTOR.get(), new Item.Properties()));

    public static final RegistryObject<Item> TECHNO_MULTITOOL = ITEMS.register("techno_multitool",
            () -> new AbilityItem(new Item.Properties().stacksTo(1), AbilityItem.Type.HASTE));

    public static final RegistryObject<Item> MAGIC_WAND = ITEMS.register("magic_wand",
            () -> new AbilityItem(new Item.Properties().stacksTo(1), AbilityItem.Type.TELEPORT));

    public static final RegistryObject<Item> MANA_CRYSTAL = ITEMS.register("mana_crystal",
            () -> new AbilityItem(new Item.Properties().stacksTo(16), AbilityItem.Type.HEAL));

    public static final RegistryObject<Item> EXPLOSIVE_APPLE = ITEMS.register("explosive_apple",
            ExplosiveApple::new);

    public static final RegistryObject<Item> RUNIC_SWORD = ITEMS.register("runic_sword",
            RunicSword::new);

    public static final RegistryObject<Item> LAVA_BOOTS = ITEMS.register("lava_boots",
            LavaBoots::new);

    public static final RegistryObject<Item> TELEPORT_PEARL = ITEMS.register("teleport_pearl",
            () -> new AbilityItem(new Item.Properties().stacksTo(16), AbilityItem.Type.RANDOM_TELEPORT));

    public static final RegistryObject<Item> GROWTH_STAFF = ITEMS.register("growth_staff",
            () -> new AbilityItem(new Item.Properties().stacksTo(1), AbilityItem.Type.GROWTH));

    public static final RegistryObject<Item> VAMPIRE_FANG = ITEMS.register("vampire_fang",
            () -> new AbilityItem(new Item.Properties().stacksTo(1), AbilityItem.Type.VAMPIRE));

    public static final RegistryObject<CreativeModeTab> TAB = TABS.register("main",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.teninone"))
                    .icon(() -> new ItemStack(TECHNO_MULTITOOL.get()))
                    .displayItems((params, output) -> {
                        output.accept(TECHNO_MULTITOOL.get());
                        output.accept(MAGIC_WAND.get());
                        output.accept(MANA_CRYSTAL.get());
                        output.accept(EXPLOSIVE_APPLE.get());
                        output.accept(RUNIC_SWORD.get());
                        output.accept(LAVA_BOOTS.get());
                        output.accept(TELEPORT_PEARL.get());
                        output.accept(GROWTH_STAFF.get());
                        output.accept(VAMPIRE_FANG.get());
                        output.accept(REACTOR_ITEM.get());
                    })
                    .build());

    public TenInOneMod() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        BLOCKS.register(bus);
        ITEMS.register(bus);
        TABS.register(bus);
    }

    public static class ReactorBlock extends Block {
        public ReactorBlock(Properties props) {
            super(props);
        }

        @Override
        public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
            if (!level.isClientSide) {
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 200, 1));
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 200, 1));
            }
            return InteractionResult.SUCCESS;
        }
    }

    public static class ExplosiveApple extends Item {
        public ExplosiveApple() {
            super(new Item.Properties().food(new FoodProperties.Builder().nutrition(4).saturationMod(0.3F).build()));
        }

        @Override
        public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
            ItemStack stack = player.getItemInHand(hand);
            if (!level.isClientSide) {
                level.explode(null, player.getX(), player.getY(), player.getZ(), 2.0F, Level.ExplosionInteraction.NONE);
                player.hurt(level.damageSources().magic(), 2.0F);
            }
            stack.shrink(1);
            return InteractionResultHolder.success(stack);
        }
    }

    public static class AbilityItem extends Item {
        public enum Type { HASTE, TELEPORT, HEAL, RANDOM_TELEPORT, GROWTH, VAMPIRE }
        private final Type type;

        public AbilityItem(Properties props, Type type) {
            super(props);
            this.type = type;
        }

        @Override
        public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
            ItemStack stack = player.getItemInHand(hand);
            if (!level.isClientSide) {
                switch (type) {
                    case HASTE -> {
                        player.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 200, 1));
                        player.getCooldowns().addCooldown(this, 200);
                    }
                    case TELEPORT -> {
                        Vec3 look = player.getLookAngle();
                        player.teleportTo(player.getX() + look.x * 8.0D, player.getY() + look.y * 8.0D, player.getZ() + look.z * 8.0D);
                        player.getCooldowns().addCooldown(this, 60);
                    }
                    case HEAL -> {
                        player.heal(6.0F);
                        stack.shrink(1);
                    }
                    case RANDOM_TELEPORT -> {
                        double x = player.getX() + (level.random.nextDouble() - 0.5D) * 40.0D;
                        double z = player.getZ() + (level.random.nextDouble() - 0.5D) * 40.0D;
                        int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING, (int) x, (int) z);
                        player.teleportTo(x, y, z);
                        stack.shrink(1);
                    }
                    case GROWTH -> {
                        BlockPos.betweenClosed(player.blockPosition().offset(-3, -1, -3), player.blockPosition().offset(3, 2, 3)).forEach(pos -> {
                            if (level.random.nextFloat() < 0.35F) {
                                BoneMealItem.growCrop(new ItemStack(Items.BONE_MEAL), level, pos);
                            }
                        });
                        stack.shrink(1);
                    }
                    case VAMPIRE -> {
                        var list = level.getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(5.0D), e -> e != player && e instanceof Monster);
                        for (LivingEntity e : list) {
                            e.hurt(level.damageSources().magic(), 4.0F);
                            player.heal(2.0F);
                        }
                        player.getCooldowns().addCooldown(this, 60);
                    }
                }
            }
            return InteractionResultHolder.success(stack);
        }
    }

    public static class RunicSword extends SwordItem {
        public RunicSword() {
            super(Tiers.NETHERITE, 3, -2.4F, new Item.Properties().fireResistant());
        }

        @Override
        public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
            target.setSecondsOnFire(5);
            if (attacker instanceof Player p) {
                p.heal(1.0F);
            }
            return super.hurtEnemy(stack, target, attacker);
        }
    }

    public static class LavaBoots extends ArmorItem {
        public LavaBoots() {
            super(ArmorMaterials.NETHERITE, ArmorItem.Type.BOOTS, new Item.Properties().fireResistant());
        }

        @Override
        public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
            if (!level.isClientSide && entity instanceof Player player && slotId == 36 && player.tickCount % 20 == 0) {
                player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 40, 0, false, false));
                if (player.isInLava()) {
                    player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 40, 1, false, false));
                }
            }
        }
    }
}
