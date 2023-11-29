package net.minecraft.util;

import net.minecraft.client.settings.GameSettings;
import wtf.monsoon.Wrapper;
import wtf.monsoon.client.event.EventMovementInput;

public class MovementInputFromOptions extends MovementInput
{
    private final GameSettings gameSettings;

    public MovementInputFromOptions(GameSettings gameSettingsIn)
    {
        this.gameSettings = gameSettingsIn;
    }

    public void updatePlayerMoveState()
    {
        this.moveStrafe = 0.0F;
        this.moveForward = 0.0F;

        if (this.gameSettings.keyBindForward.isKeyDown()) {
            ++this.moveForward;
        }

        if (this.gameSettings.keyBindBack.isKeyDown()) {
            --this.moveForward;
        }

        if (this.gameSettings.keyBindLeft.isKeyDown()) {
            ++this.moveStrafe;
        }

        if (this.gameSettings.keyBindRight.isKeyDown()) {
            --this.moveStrafe;
        }

        this.jump = this.gameSettings.keyBindJump.isKeyDown();
        this.sneak = this.gameSettings.keyBindSneak.isKeyDown();

        EventMovementInput event = new EventMovementInput(moveForward, moveStrafe, jump, sneak, 0.3D);
        Wrapper.getMonsoon().getBus().post(event);

        this.moveForward = event.getForward();
        this.moveStrafe = event.getStrafe();
        this.jump = event.getJump();
        this.sneak = event.getSneak();

        if (this.sneak) {
            this.moveStrafe = (float) ((double) this.moveStrafe * event.getSneakSlowdown());
            this.moveForward = (float) ((double) this.moveForward * event.getSneakSlowdown());
        }
    }
}
