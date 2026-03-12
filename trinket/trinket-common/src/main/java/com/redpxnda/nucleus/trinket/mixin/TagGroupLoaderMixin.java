package com.redpxnda.nucleus.trinket.mixin;

import com.redpxnda.nucleus.trinket.NucleusTrinket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.TagLoader;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mixin(TagLoader.class)
public class TagGroupLoaderMixin {

    @Shadow
    @Final
    private String directory;

    @Inject(method = "load(Lnet/minecraft/server/packs/resources/ResourceManager;)Ljava/util/Map;", at = @At("TAIL"))
    public void injectValues(ResourceManager manager, CallbackInfoReturnable<Map<ResourceLocation, List<TagLoader.EntryWithSource>>> cir) {
        var map = cir.getReturnValue();
        List<ResourceLocation> ids = new ArrayList<>(map.keySet());

        for (ResourceLocation id : ids) {
            List<TagLoader.EntryWithSource> tags = map.get(id);
            if (
                    id.getNamespace().equals(NucleusTrinket.MOD_ID)
                    //|| NucleusTrinket.WATCHED_NAMESPACES.contains(id.getNamespace())
            ) {
                tags.forEach(entryWithSource -> {
                    var curioList = map.computeIfAbsent(ResourceLocation.fromNamespaceAndPath("curios", id.getPath()), i -> new ArrayList<>());
                    var accessoriesList = map.computeIfAbsent(ResourceLocation.fromNamespaceAndPath("accessories", id.getPath()), i -> new ArrayList<>());
                    curioList.add(entryWithSource);
                    accessoriesList.add(entryWithSource);
                });
            }
        }
    }

}
