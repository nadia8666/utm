package com.nadia.utm.block.entity;

import com.nadia.utm.event.ForceLoad;
import com.nadia.utm.event.utmEvents;
import com.nadia.utm.gui.OxygenFurnaceMenu;
import com.nadia.utm.registry.block.utmBlockEntities;
import com.nadia.utm.registry.fluid.utmFluids;
import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import com.simibubi.create.foundation.fluid.CombinedTankWrapper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

@ForceLoad
public class OxygenFurnaceBlockEntity extends SmartBlockEntity implements MenuProvider, IHaveGoggleInformation {
    public SmartFluidTankBehaviour LOX;
    public SmartFluidTankBehaviour STEEL;
    public CombinedTankWrapper CAPABILITY;

    public final ItemStackHandler inventory = new ItemStackHandler(2) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    public int progress = 0;
    public int maxProgress = 200;

    public final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            return index == 0 ? progress : maxProgress;
        }

        @Override
        public void set(int index, int value) {}

        @Override
        public int getCount() {
            return 2;
        }
    };

    public OxygenFurnaceBlockEntity(BlockPos pos, BlockState blockState) {
        super(utmBlockEntities.OXYGEN_FURNACE.get(), pos, blockState);
    }

    @Override
    public void tick() {
        super.tick();

        Level level = this.getLevel();
        if (level == null || level.isClientSide) return;

        boolean hasIron = this.inventory.getStackInSlot(0).is(Items.IRON_INGOT) && this.inventory.getStackInSlot(0).getCount() >= 10;
        boolean hasFuel = this.inventory.getStackInSlot(1).is(ItemTags.COALS);
        boolean hasOxygen = this.LOX.getPrimaryHandler().getFluidAmount() >= 1000 && this.STEEL.getPrimaryHandler().getSpace() >= 1000;

        if (this.progress == 0 && hasIron && hasOxygen && hasFuel) {
            this.inventory.getStackInSlot(1).shrink(1);
            this.progress = this.maxProgress;
            this.setChanged();
        }

        if (this.progress > 0) {
            this.progress--;

            if (this.progress == 0) {
                if (this.STEEL.getCapability() instanceof SmartFluidTankBehaviour.InternalFluidHandler handler) {
                    this.inventory.getStackInSlot(0).shrink(10);
                    this.LOX.getPrimaryHandler().drain(1000, IFluidHandler.FluidAction.EXECUTE);

                    handler.forceFill(new FluidStack(utmFluids.MOLTEN_STEEL.get(), 1000), IFluidHandler.FluidAction.EXECUTE);
                }
            }
            this.setChanged();
        }
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        this.LOX = new SmartFluidTankBehaviour(SmartFluidTankBehaviour.INPUT, this, 1, 1000, true);
        this.LOX.getPrimaryHandler().setValidator((fluid) -> fluid.is(utmFluids.LIQUID_OXYGEN));

        this.STEEL = new SmartFluidTankBehaviour(SmartFluidTankBehaviour.OUTPUT, this, 1, 1000, true).forbidInsertion();
        this.STEEL.getPrimaryHandler().setValidator((fluid) -> fluid.is(utmFluids.MOLTEN_STEEL));

        CAPABILITY = new CombinedTankWrapper(this.LOX.getCapability(), this.STEEL.getCapability());

        behaviours.add(this.LOX);
        behaviours.add(this.STEEL);
    }

    @Override
    protected void read(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries, boolean clientPacket) {
        super.read(tag, registries, clientPacket);
        inventory.deserializeNBT(registries, tag.getCompound("Inventory"));
        progress = tag.getInt("Progress");
    }

    @Override
    public void write(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries, boolean clientPacket) {
        super.write(tag, registries, clientPacket);
        tag.put("Inventory", inventory.serializeNBT(registries));
        tag.putInt("Progress", progress);
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("utm.gui.oxygen_furnace_menu");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, @NotNull Inventory inventory, @NotNull Player player) {
        return new OxygenFurnaceMenu(i, inventory, this, data);
    }

    @Override
    public @NotNull ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.@NotNull Provider pRegistries) {
        return saveWithoutMetadata(pRegistries);
    }

    @OnlyIn(Dist.CLIENT)
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        containedFluidTooltip(tooltip, isPlayerSneaking, this.LOX.getCapability());
        containedFluidTooltip(tooltip, isPlayerSneaking, this.STEEL.getCapability());
        return true;
    }

    static {
        utmEvents.register(RegisterCapabilitiesEvent.class, (event) -> {
            event.registerBlockEntity(
                    Capabilities.FluidHandler.BLOCK,
                    utmBlockEntities.OXYGEN_FURNACE.get(),
                    (be, side) -> be.CAPABILITY
            );

            event.registerBlockEntity(
                    Capabilities.ItemHandler.BLOCK,
                    utmBlockEntities.OXYGEN_FURNACE.get(),
                    (be, side) -> be.inventory
            );
        });
    }
}