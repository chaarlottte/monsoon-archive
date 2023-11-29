package wtf.monsoon.impl.ui.scratch.panes;

import com.google.gson.internal.Streams;
import lombok.Getter;
import lombok.NonNull;
import org.lwjgl.nanovg.NanoVG;
import wtf.monsoon.api.ui.Comp;
import wtf.monsoon.api.util.font.FontUtil;

import java.awt.*;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public class VSOutputPanel extends Comp {
    @Getter
    ArrayList<String> lines = new ArrayList<>();
    public VSOutputPanel(@NonNull float x, @NonNull float y, @NonNull float w, @NonNull float h) {
        super(x, y, w, h);
    }

    @Override
    public void render(float mx, float my) {
        ui.rect(x,y-1,w,1, new Color(0x141517));
        ui.rect(x,y,w,20, new Color(0x313336));
        ui.rect(x,y+20,w,h-20, new Color(0x1E1F22));

        int i = 0;
        for (String line : lines) {
            ui.text(line,"product_sans", 16, x+24, y+24+16*i, new Color(0xcac6c6));
            i++;
        }

        ui.rect(x,y+20,20,h-20, new Color(0x2B2D30));
        ui.rect(x,y+20,w,1, new Color(0x141517));
        ui.rect(x+20,y+20,1,h-20, new Color(0x141517));
        ui.text("Output","product_sans", 16, x+3, y+3, new Color(0xcac6c6));

        if(hovered(x+2,y+23, 16, 16)) {
            ui.round(x + 2, y + 23, 16, 16, 3, new Color(0x4D5056));
            ui.rect(x+25-1, y+23-3-1, 66+2, 16+6+2, new Color(0x141517));
            ui.rect(x+25, y+23-3, 66, 16+6, new Color(0x2B2D30));
            ui.text("Clear All", "product_sans", 16, x+25+33, y+20+12,new Color(0xcac6c6), 2 | 16);
        }

        ui.text(FontUtil.UNICODES_UI.TRASH,"entypo", 16, x+4, y+24, new Color(0xFD526F));
    }

    @Override
    public void click(float mx, float my, int button) {
        if(hovered(x+2,y+23, 16, 16) && button == 0)
            lines.clear();
    }

    public void print(Object obj) {
        lines.addAll(Arrays.stream(obj.toString().split("\n")).collect(Collectors.toList()));
    }
}
