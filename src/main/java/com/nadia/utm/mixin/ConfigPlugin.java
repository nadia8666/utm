package com.nadia.utm.mixin;

import net.neoforged.fml.loading.LoadingModList;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class ConfigPlugin implements IMixinConfigPlugin {
    @Override
    public void onLoad(String mixinPackage) {

    }

    @Override
    public String getRefMapperConfig() {
        return "";
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        // spears compat
        if (mixinClassName.contains("SpearsMixin")) {
            return LoadingModList.get().getModFileById("spears") != null;
        }

        // showcase item x jei compat
        if (mixinClassName.contains("ClientShowcaseMixin")) {
            return LoadingModList.get().getModFileById("showcaseitem") != null && LoadingModList.get().getModFileById("jei") != null;
        }

        // figura
        if (mixinClassName.contains("FiguraCompatMixin")) {
            return LoadingModList.get().getModFileById("figura") != null;
        }

        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

    }

    @Override
    public List<String> getMixins() {
        return List.of();
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }
}
