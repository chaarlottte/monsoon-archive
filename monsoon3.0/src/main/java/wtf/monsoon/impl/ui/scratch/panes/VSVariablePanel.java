package wtf.monsoon.impl.ui.scratch.panes;

import lombok.NonNull;
import net.minecraft.util.ChatAllowedCharacters;
import org.lwjgl.input.Keyboard;
import wtf.monsoon.api.ui.Comp;

import java.awt.*;
import java.util.HashMap;

public class VSVariablePanel extends Comp {
    VarType createVarType = VarType.String;
    String createVarName = "";
    Object value = "";
    String temp = "";
    boolean valError, typingName, typingValue;
    public HashMap<String,Object> variables = new HashMap<>();
    public VSVariablePanel(@NonNull float x, @NonNull float y, @NonNull float w, @NonNull float h) {
        super(x, y, w, h);
        Keyboard.enableRepeatEvents(true);
    }

    @Override
    public void render(float mx, float my) {
        ui.rect(x,y,w,h, new Color(0x2B2D30));
        ui.rect(x,y,w,18, new Color(0x313336));
        ui.text("Variables", "product_sans", 16, x+3, y+3, new Color(0xcac6c6));

        ui.text("Type", "product_sans", 14, x+3, y+18+3, new Color(0xcac6c6)); {
            int xof = 0;
            for (VarType value : VarType.values()) {
                ui.rect(x+48+45*xof, y+18+3+1,40,14-2, new Color(0x36383B));
                if(value == createVarType) {
                    ui.rect(x+48+45*xof,y+18+3+1,40,1, new Color(0x545960));
                    ui.rect(x+48+45*xof,y+18+3+13,40,1, new Color(0x545960));
                    ui.rect(x+48+45*xof,y+18+3+1,1,14-2, new Color(0x545960));
                    ui.rect(x+48+45*xof+39,y+18+3+1,1,14-2, new Color(0x545960));
                }
                ui.text(value.name(), "product_sans", 12, x+48+45*xof+20, y+18+3+7+1, new Color(0xcac6c6), 2 | 16);
                xof++;
            }
        }

        ui.text("Name", "product_sans", 14, x+3, y+18+3+14, new Color(0xcac6c6)); {
            ui.rect(x+48, y+18+3+14+1,130,14-2, new Color(0x545960));
            ui.rect(x+48+1, y+18+3+14+1+1,130-2,14-2-2, new Color(0x36383B));
            ui.text(createVarName, "product_sans",10, x+48+3, y+18+3+14+1+2, new Color(0xcac6c6));
        }

        ui.text("Value", "product_sans", 14, x+3, y+18+3+14+14, new Color(0xcac6c6)); {
            ui.rect(x+48, y+18+3+14+14,130,14-2, new Color(0x545960));
            ui.rect(x+48+1, y+18+3+14+14+1,130-2,14-2-2, new Color(0x36383B));
            ui.text(temp, "product_sans",10, x+48+3, y+18+3+14+14+1+2, valError ? new Color(0xFD526F) : new Color(0xcac6c6));
        }
        if(hovered(x,y+18+3+14+14+14,w,14))
            ui.rect(x,y+18+3+14+14+14,w,14,new Color(0x313336));
        ui.text("Create Variable", "product_sans", 14, x+w/2f, y+18+3+14+14+14+7, new Color(0xcac6c6), 2 | 16);

        int[] i = {0};
        variables.forEach((name,val) -> {
            ui.text(name,"product_sans",14, x+4, y+18+3+14+14+14+14+14*i[0],new Color(0xcac6c6));
            i[0]++;
        });

        ui.rect(x,y+18,w,1, new Color(0x141517));
        ui.rect(x,y,1,h, new Color(0x141517));
        ui.rect(x,y+18+44+14,w,1, new Color(0x141517));
    }

    @Override
    public void click(float mx, float my, int button) {

        int xof = 0;
        for (VarType value : VarType.values()) {
            if(hovered(x+48+42*xof, y+18+3,40,14) && button == 0)
                createVarType = value;
            xof++;
        }
        typingName = false;
        typingValue = false;
        if(hovered(x+48, y+18+3+14+1,130,14-2) & button == 0) { //name
            typingName=true;
        }
        if(hovered(x+48, y+18+3+14+14,130,14-2) & button == 0) { //value
            typingValue = true;
        }
        if(hovered(x,y+18+3+14+14+14,w,14) && button == 0) {
            if(!createVarName.isEmpty())
                createVariable();
        }
    }

    public void onKey(char c, int keycode) {
            if(typingName) {
                if(ChatAllowedCharacters.isAllowedCharacter(c))
                    createVarName+=c;
                if(createVarName.length() != 0 && keycode==Keyboard.KEY_BACK)
                    createVarName = createVarName.substring(0, createVarName.length()-1);
            }
            if(typingValue) {
                if(ChatAllowedCharacters.isAllowedCharacter(c))
                    temp+=c;
                if(temp.length() != 0 && keycode==Keyboard.KEY_BACK)
                    temp = temp.substring(0, temp.length()-1);

                switch (createVarType) {
                    case String:
                        valError = false;
                        value = temp;
                        break;
                    case Float:
                        try {
                            value = Float.parseFloat(temp);
                            valError = false;
                        } catch (NumberFormatException ex) {
                            value = 0;
                            valError = true;
                        }
                        break;
                    case Object:

                        break;
                }
            }
    }

    void createVariable() {
        variables.put(createVarName, value);

        createVarName = "";
        createVarType = VarType.String;
        value = null;
        temp = "";
    }

    enum VarType {
        String,Float,Object;
    }
}
