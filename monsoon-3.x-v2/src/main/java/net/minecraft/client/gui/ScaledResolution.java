package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.util.MathHelper;
import spritz.api.annotations.Excluded;
import spritz.api.annotations.Identifier;

public class ScaledResolution {
    private final double scaledWidthD;
    private final double scaledHeightD;
    private int scaledWidth;
    private int scaledHeight;
    private int scaleFactor;

    public ScaledResolution(Minecraft p_i46445_1_) {
        this.scaledWidth = p_i46445_1_.displayWidth;
        this.scaledHeight = p_i46445_1_.displayHeight;
        this.scaleFactor = 1;
        boolean flag = p_i46445_1_.isUnicode();
        int i = p_i46445_1_.gameSettings.guiScale;

        if (i == 0)
        {
            i = 1000;
        }

        while (this.scaleFactor < i && this.scaledWidth / (this.scaleFactor + 1) >= 320 && this.scaledHeight / (this.scaleFactor + 1) >= 240)
        {
            ++this.scaleFactor;
        }

        if (flag && this.scaleFactor % 2 != 0 && this.scaleFactor != 1)
        {
            --this.scaleFactor;
        }

        this.scaledWidthD = (double)this.scaledWidth / (double)this.scaleFactor;
        this.scaledHeightD = (double)this.scaledHeight / (double)this.scaleFactor;
        this.scaledWidth = MathHelper.ceiling_double_int(this.scaledWidthD);
        this.scaledHeight = MathHelper.ceiling_double_int(this.scaledHeightD);
    }

    @Identifier(identifier = "get_scaled_width")
    public int getScaledWidth()
    {
        return this.scaledWidth;
    }

    @Identifier(identifier = "get_scaled_height")
    public int getScaledHeight()
    {
        return this.scaledHeight;
    }

    @Excluded
    public double getScaledWidth_double()
    {
        return this.scaledWidthD;
    }

    @Excluded
    public double getScaledHeight_double()
    {
        return this.scaledHeightD;
    }

    @Identifier(identifier = "get_scale_factor")
    public int getScaleFactor()
    {
        return this.scaleFactor;
    }

}
