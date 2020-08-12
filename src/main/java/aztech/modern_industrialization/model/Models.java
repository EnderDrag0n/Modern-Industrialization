package aztech.modern_industrialization.model;

import aztech.modern_industrialization.MIIdentifier;
import aztech.modern_industrialization.ModernIndustrialization;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.util.Identifier;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class Models {
    public static final MachineModel STEAM_BOILER = new MachineModel(new MIIdentifier("block/steam_boiler"), new MIIdentifier("blocks/casings/steam/bricked_bronze/")).withFrontOverlay(new MIIdentifier("blocks/generators/boiler/coal/overlay_front"));

    public static Map<Identifier, UnbakedModel> getModelMap() {
        Map<Identifier, UnbakedModel> modelMap = new HashMap<>();
        Field[] declaredFields = Models.class.getDeclaredFields();
        for (Field field : declaredFields) {
            if (java.lang.reflect.Modifier.isStatic(field.getModifiers())) {
                try {
                    MachineModel model = (MachineModel)field.get(null);
                    modelMap.put(model.id, model);
                } catch (IllegalAccessException e) {
                    ModernIndustrialization.LOGGER.error("Exception:", e);
                }
            }
        }
        return modelMap;
    }
}
