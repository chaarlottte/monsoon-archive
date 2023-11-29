package wtf.monsoon.api.util.font;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.util.font.impl.FontRenderer;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;

public class FontUtil {

    public FontRenderer productSansSmall, productSansSmaller, productSans, productSansMedium, productSansBold, productSansSmallBold,
            greycliff40, greycliff26, greycliff19,
            entypo14, entypo18,
            menuIcons, minecraft,
            comicSans, comicSansSmall, comicSansBold, comicSansSmallBold, comicSansMedium, comicSansMediumBold,
            ubuntuwu, ubuntuwuSmall, ubuntuwuMedium;

    public static Font getFont(String name, int size) {
        try {
            InputStream fontStream = Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation("monsoon/font/" + name)).getInputStream();
            Font font = Font.createFont(Font.TRUETYPE_FONT, fontStream);

            fontStream.close();

            return font.deriveFont(Font.PLAIN, size);
        } catch (IOException | FontFormatException e) {
            e.printStackTrace();
            return new Font("plain", Font.PLAIN, size);
        }
    }

    public void bootstrap() {
        productSans = new FontRenderer(getFont("product_sans.ttf", 38));
        productSansBold = new FontRenderer(getFont("product_sans_bold.ttf", 38));
        productSansSmallBold = new FontRenderer(getFont("product_sans_bold.ttf", 30));
        productSansSmall = new FontRenderer(getFont("product_sans.ttf", 30));
        productSansSmaller = new FontRenderer(getFont("product_sans.ttf", 24));
        productSansMedium = new FontRenderer(getFont("product_sans.ttf", 34));
        greycliff40 = new FontRenderer(getFont("greycliff.ttf", 80));
        greycliff26 = new FontRenderer(getFont("greycliff.ttf", 52));
        greycliff19 = new FontRenderer(getFont("greycliff.ttf", 38));
        entypo14 = new FontRenderer(getFont("entypo.ttf", 28));
        entypo18 = new FontRenderer(getFont("entypo.ttf", 36));
        menuIcons = new FontRenderer(getFont("menu_icons.ttf", 180));
        minecraft = new FontRenderer(getFont("minecraft.otf", 38));
        comicSans = new FontRenderer(getFont("comic_sans.ttf", 38));
        comicSansMedium = new FontRenderer(getFont("comic_sans.ttf", 34));
        comicSansSmall = new FontRenderer(getFont("comic_sans.ttf", 30));
        comicSansBold = new FontRenderer(getFont("comic_sans_bold.ttf", 38));
        comicSansMediumBold = new FontRenderer(getFont("comic_sans_bold.ttf", 34));
        comicSansSmallBold = new FontRenderer(getFont("comic_sans_bold.ttf", 30));
        ubuntuwu = new FontRenderer(getFont("ubuntuwu.ttf", 48));
        ubuntuwuSmall = new FontRenderer(getFont("ubuntuwu.ttf", 40));
        ubuntuwuMedium = new FontRenderer(getFont("ubuntuwu.ttf", 44));

        Wrapper.setFont(productSans);
    }

    public static class UNICODES_UI {
        public static String TRASH = "a";
        public static String EYE = "b";
        public static String LOCK = "c";
        public static String UNLOCK = "d";
        public static String FILLED_HEART = "e";
        public static String EMPTY_HEART = "f";
        public static String PENCIL = "g";
        public static String INFO = "h";
        public static String MINUS = "i";
        public static String PLUS = "j";
        public static String YES = "k";
        public static String NO = "l";
        public static String SEARCH = "m";
        public static String LIGHT = "n";
        public static String HOME = "o";
        public static String SETTINGS = "p";
        public static String COPY = "q";
        public static String CIRCLE_FULL = "r";
        public static String CIRCLE_EMPTY = "s";
        public static String LEFT = "t";
        public static String RIGHT = "u";
        public static String UP = "v";
        public static String DOWN = "w";
        public static String CLOCK = "x";
        public static String TAG = "y";
        public static String BLOCK = "z";
        public static String USER = "A";
        public static String WARN = "B";
        public static String ERROR = "C";
        public static String LOAD = "D";
    }
}