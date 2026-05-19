package com.nadia.utm.projectile;

import net.minecraft.client.model.TridentModel;
import net.minecraft.client.model.geom.ModelPart;

public class AridTridentModel extends TridentModel {
    public AridTridentModel(ModelPart root) {
        super(root);
    }
    // separate entity model and item model. its easier
     // make your item model have the ovveride for item hotbar thing like the real trident. then overrides for charging. right.
      // then make the entity have the same json as your like. literally the same json or something as the item model but instead have the activated texture. boom
       // see if you can make the unactivated texture display underneath the activated one and then make the activated one emissive. also maybe you should add a spot
        // at the base of the trident that is emissive so it looks cooler. also maybe give it a particle trail.
         // also fix the offset of the crit particles on the sabel and such because those ar ebroekn and offset idk how to make a random offset
}
