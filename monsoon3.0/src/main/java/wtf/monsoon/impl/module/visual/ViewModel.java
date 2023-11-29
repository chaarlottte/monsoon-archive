package wtf.monsoon.impl.module.visual;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import wtf.monsoon.api.module.Category;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.impl.event.EventFOVUpdate;
import wtf.monsoon.impl.event.EventScaleItem;
import wtf.monsoon.impl.event.EventTransformItem;

public class ViewModel extends Module {

    public final Setting<Boolean> combineScale = new Setting<>("Combine Scale", true)
            .describedBy("Combine X, Y, and Z scale into one.");

    public final Setting<Double> scaleCombine = new Setting<>("Scale", 1.0)
            .minimum(0.1)
            .maximum(5.0)
            .incrementation(0.05)
            .describedBy("The scale of the item.")
            .visibleWhen(combineScale::getValue);

    public final Setting<Double> scaleX = new Setting<>("X Scale", 1.0)
            .minimum(0.1)
            .maximum(5.0)
            .incrementation(0.05)
            .describedBy("The scale of the item on the X axis.")
            .visibleWhen(() -> !combineScale.getValue());

    public final Setting<Double> scaleY = new Setting<>("Y Scale", 1.0)
            .minimum(0.1)
            .maximum(5.0)
            .incrementation(0.05)
            .describedBy("The scale of the item on the Y axis.")
            .visibleWhen(() -> !combineScale.getValue());

    public final Setting<Double> scaleZ = new Setting<>("Z Scale", 1.0)
            .minimum(0.1)
            .maximum(5.0)
            .incrementation(0.05)
            .describedBy("The scale of the item on the Z axis.")
            .visibleWhen(() -> !combineScale.getValue());

    public final Setting<Boolean> modifyPosition = new Setting<>("Modify Position", false)
            .describedBy("Whether to modify the position of the item.");

    public final Setting<Double> posX = new Setting<>("Pos X", 0.56)
            .minimum(-2.0)
            .maximum(2.0)
            .incrementation(0.01)
            .describedBy("The position of the item on the X axis.")
            .visibleWhen(modifyPosition::getValue);

    public final Setting<Double> posY = new Setting<>("Pos Y", -0.52)
            .minimum(-2.0)
            .maximum(2.0)
            .incrementation(0.01)
            .describedBy("The position of the item on the Y axis.")
            .visibleWhen(modifyPosition::getValue);

    public final Setting<Double> posZ = new Setting<>("Pos Z", -0.72)
            .minimum(-2.0)
            .maximum(2.0)
            .incrementation(0.01)
            .describedBy("The position of the item on the Z axis.")
            .visibleWhen(modifyPosition::getValue);

    public final Setting<Boolean> enableItemFOV = new Setting<>("Enable Item FOV", false)
            .describedBy("Whether to enable item FOV.");

    public final Setting<Double> itemFOV = new Setting<>("Item FOV", 110D)
            .minimum(70D)
            .maximum(130D)
            .incrementation(1D)
            .describedBy("Your item FOV.")
            .visibleWhen(enableItemFOV::getValue);

    public final Setting<Boolean> onlyEmptyHand = new Setting<>("Only When Empty Handed", false)
            .describedBy("Whether to only change the FOV when your hand is empty.")
            .visibleWhen(enableItemFOV::getValue);

    public ViewModel() {
        super("View Model", "Change the view model of the item.", Category.VISUAL);
    }

    @EventLink
    private final Listener<EventFOVUpdate> eventFOVUpdateListener = e -> {
        if (enableItemFOV.getValue()) {
            if (mc.thePlayer.getHeldItem() == null || !onlyEmptyHand.getValue()) {
                e.setNewFOV((float) (e.getFov() + (itemFOV.getValue() - e.getFov())));
            }
        }
    };

    @EventLink
    private final Listener<EventTransformItem> eventRenderItemListener = e -> {
        if (modifyPosition.getValue()) {
            e.setPosX(posX.getValue().floatValue());
            e.setPosY(posY.getValue().floatValue());
            e.setPosZ(posZ.getValue().floatValue());
        }
    };

    @EventLink
    private final Listener<EventScaleItem> eventScaleItemListener = e -> {
        if (combineScale.getValue()) {
            e.setScaleX(e.getScaleX() * scaleCombine.getValue().floatValue());
            e.setScaleY(e.getScaleY() * scaleCombine.getValue().floatValue());
            e.setScaleZ(e.getScaleZ() * scaleCombine.getValue().floatValue());
        } else {
            e.setScaleX(e.getScaleX() * scaleX.getValue().floatValue());
            e.setScaleY(e.getScaleY() * scaleY.getValue().floatValue());
            e.setScaleZ(e.getScaleZ() * scaleZ.getValue().floatValue());
        }
    };
}
