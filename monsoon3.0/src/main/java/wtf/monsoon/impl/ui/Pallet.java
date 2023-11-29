package wtf.monsoon.impl.ui;

import lombok.Data;
import lombok.NonNull;

import java.awt.*;

@Data
public class Pallet {
    @NonNull Color main, mainDarker, mainDarkest, background, misc;
}
