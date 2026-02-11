package com.nadia.utm.gui.glint;

import com.nadia.utm.block.entity.GlintTableBlockEntity;
import com.nadia.utm.registry.block.utmBlocks;
import com.nadia.utm.registry.data.utmDataComponents;
import com.nadia.utm.registry.ui.utmMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;

import java.util.List;

import static com.nadia.utm.client.renderer.glint.utmGlintContainer.DEFAULT_COLOR;
import static com.nadia.utm.client.renderer.glint.utmGlintContainer.GLINT_DEFAULT;

public class GlintMenu extends AbstractContainerMenu {
    public final GlintTableBlockEntity blockEntity;
    private final Level level;

    private final Container container = new SimpleContainer(1);
    public final ResultContainer resultContainer = new ResultContainer();

    public int COLOR = 0;
    public boolean ADDITIVE = true;
    public ResourceLocation TYPE = GLINT_DEFAULT;
    public Vector2f SCALE = new Vector2f(1, 1);
    public Vector2f SPEED = new Vector2f(1, 1);

    public static final List<ResourceLocation> TEXTURES = List.of(
            GLINT_DEFAULT,
            ResourceLocation.fromNamespaceAndPath("utm", "textures/misc/test_glint.png"),
            ResourceLocation.fromNamespaceAndPath("utm", "textures/misc/gyig1.png"),
            ResourceLocation.fromNamespaceAndPath("utm", "textures/misc/boundtosmile.png"),
            ResourceLocation.fromNamespaceAndPath("utm", "textures/misc/along.png"),
            ResourceLocation.fromNamespaceAndPath("utm", "textures/misc/along2.png"),
            ResourceLocation.fromNamespaceAndPath("utm", "textures/misc/along3.png"),
            ResourceLocation.fromNamespaceAndPath("utm", "textures/misc/along4.png"),
            ResourceLocation.fromNamespaceAndPath("utm", "textures/misc/glint_grid_thin.png"),
            ResourceLocation.fromNamespaceAndPath("utm", "textures/misc/glint_grid.png"),
            ResourceLocation.fromNamespaceAndPath("utm", "textures/misc/glint_checkers.png"),
            ResourceLocation.fromNamespaceAndPath("utm", "textures/misc/glint_items.png"),
            ResourceLocation.fromNamespaceAndPath("utm", "textures/misc/best.png"),
            ResourceLocation.fromNamespaceAndPath("utm", "textures/misc/raginglint.png"),
            ResourceLocation.fromNamespaceAndPath("utm", "textures/misc/threeg.png"),
            ResourceLocation.fromNamespaceAndPath("utm", "textures/misc/glowy.png"),
            ResourceLocation.fromNamespaceAndPath("utm", "textures/misc/threegnoglow.png")

    );

    public GlintMenu(int containerId, Inventory inv, FriendlyByteBuf extraData) {
        this(containerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    public GlintMenu(int containerId, Inventory inv, BlockEntity blockEntity) {
        super(utmMenus.GLINT_MENU.get(), containerId);

        this.blockEntity = (GlintTableBlockEntity) blockEntity;
        this.level = inv.player.level();

        addPlayerInventory(inv);
        addPlayerHotbar(inv);

        Slot inSlot = this.addSlot(new Slot(container, 0, 8, 10));
        this.addSlot(new Slot(resultContainer, 0, 34, 10) {
            @Override
            public boolean mayPlace(@NotNull ItemStack stack) {
                return false;
            }

            @Override
            public void onTake(@NotNull Player player, @NotNull ItemStack stack) {
                ItemStack input = inSlot.getItem();
                if (!input.isEmpty()) {
                    input.shrink(1);

                    if (input.getCount() <= 0) {
                        inSlot.set(ItemStack.EMPTY);
                    }

                    slotsChanged();
                }

                super.onTake(player, stack);
            }
        });

        this.addDataSlot(new DataSlot() {
            @Override public int get() { return COLOR; }
            @Override public void set(int val) { COLOR = val; }
        });
        this.addDataSlot(new DataSlot() {
            @Override public int get() { return ADDITIVE ? 1 : 0; }
            @Override public void set(int val) { ADDITIVE = val != 0; }
        });
        this.addDataSlot(new DataSlot() {
            @Override public int get() { return Float.floatToIntBits(SCALE.x); }
            @Override public void set(int val) { SCALE.x = Float.intBitsToFloat(val); }
        });
        this.addDataSlot(new DataSlot() {
            @Override public int get() { return Float.floatToIntBits(SCALE.y); }
            @Override public void set(int val) { SCALE.y = Float.intBitsToFloat(val); }
        });
        this.addDataSlot(new DataSlot() {
            @Override public int get() { return Float.floatToIntBits(SPEED.x); }
            @Override public void set(int val) { SPEED.x = Float.intBitsToFloat(val); }
        });
        this.addDataSlot(new DataSlot() {
            @Override public int get() { return Float.floatToIntBits(SPEED.y); }
            @Override public void set(int val) { SPEED.y = Float.intBitsToFloat(val); }
        });
        this.addDataSlot(new DataSlot() {
            @Override public int get() { return Math.max(0, TEXTURES.indexOf(TYPE)); }
            @Override public void set(int val) { TYPE = TEXTURES.get(Math.min(val, TEXTURES.size() - 1)); }
        });

        this.slotsChanged();
    }

    public void slotsChanged() {
        ItemStack inputStack = this.container.getItem(0);

        if (inputStack.isEmpty()) {
            this.resultContainer.setItem(0, ItemStack.EMPTY);
        } else {
            ItemStack preview = inputStack.copy();
            preview.setCount(1);
            updateComponents(preview);
            this.resultContainer.setItem(0, preview);
        }
        this.broadcastChanges();
    }

    // slots 36+ = TileInventory slots
    public static final int INPUT_SLOT_INDEX = 36;
    private static final int HOTBAR_SLOT_COUNT = 9;
    private static final int PLAYER_INVENTORY_ROW_COUNT = 3;
    private static final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
    private static final int PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT;
    private static final int VANILLA_SLOT_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT;
    private static final int VANILLA_FIRST_SLOT_INDEX = 0;
    private static final int TE_INVENTORY_FIRST_SLOT_INDEX = VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT;

    private static final int TE_INVENTORY_SLOT_COUNT = 2;  // item in , item out

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player playerIn, int pIndex) {
        Slot sourceSlot = slots.get(pIndex);
        if (!sourceSlot.hasItem()) return ItemStack.EMPTY;
        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyOfSourceStack = sourceStack.copy();

        if (pIndex < VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT) {
            if (!moveItemStackTo(sourceStack, INPUT_SLOT_INDEX, INPUT_SLOT_INDEX + 1, false)) {
                return ItemStack.EMPTY;
            }
        } else if (pIndex < TE_INVENTORY_FIRST_SLOT_INDEX + TE_INVENTORY_SLOT_COUNT) {
            if (!moveItemStackTo(sourceStack, VANILLA_FIRST_SLOT_INDEX, VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;
            }
        } else {
            return ItemStack.EMPTY;
        }

        if (sourceStack.getCount() == 0) {
            sourceSlot.set(ItemStack.EMPTY);
        } else {
            sourceSlot.setChanged();
        }

        sourceSlot.onTake(playerIn, sourceStack);
        return copyOfSourceStack;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()),
                player, utmBlocks.GLINT_TABLE.block.get());
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int y = 0; y < 3; ++y) {
            for (int x = 0; x < 9; ++x) {
                this.addSlot(new Slot(playerInventory, x + y * 9 + 9, 8 + x * 18, 84 + y * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int x = 0; x < 9; ++x) {
            this.addSlot(new Slot(playerInventory, x, 8 + x * 18, 142));
        }
    }

    protected void updateComponents(ItemStack stack) {
        stack.set(utmDataComponents.GLINT_COLOR, this.COLOR);
        stack.set(utmDataComponents.GLINT_TYPE, this.TYPE);
        stack.set(utmDataComponents.GLINT_ADDITIVE, this.ADDITIVE);
        stack.set(utmDataComponents.GLINT_SCALE, this.SCALE);
        stack.set(utmDataComponents.GLINT_SPEED, this.SPEED);
    }

    protected void updateGlintConfig(ItemStack stack) {
        this.COLOR = stack.getOrDefault(utmDataComponents.GLINT_COLOR, DEFAULT_COLOR);
        this.ADDITIVE = stack.getOrDefault(utmDataComponents.GLINT_ADDITIVE, true);
        this.SCALE = new Vector2f(stack.getOrDefault(utmDataComponents.GLINT_SCALE, new Vector2f(1, 1)));
        this.SPEED = new Vector2f(stack.getOrDefault(utmDataComponents.GLINT_SPEED, new Vector2f(1, 1)));
        this.TYPE = stack.getOrDefault(utmDataComponents.GLINT_TYPE, TEXTURES.getFirst());
    }

    @Override
    public void broadcastChanges() {
        updateOutput();

        super.broadcastChanges();
    }

    private ItemStack LAST_STACK;
    private void updateOutput() {
        ItemStack inputStack = container.getItem(0);
        ItemStack currentOutput = resultContainer.getItem(0);

        if (LAST_STACK != inputStack) {
            updateGlintConfig(inputStack);
            LAST_STACK = inputStack;
        }

        if (inputStack.isEmpty()) {
            if (!currentOutput.isEmpty()) {
                resultContainer.setItem(0, ItemStack.EMPTY);
            }
        } else {
            ItemStack preview = inputStack.copy();
            preview.setCount(1);

            updateComponents(preview);

            if (!ItemStack.matches(currentOutput, preview)) {
                resultContainer.setItem(0, preview);
            }
        }
    }

    @Override
    public void removed(@NotNull Player player) {
        super.removed(player);
        this.clearContainer(player, container);
    }
}
