package wtf.monsoon.impl.ui.notification;

import lombok.AllArgsConstructor;
import lombok.Getter;
import wtf.monsoon.api.util.font.FontUtil;

import java.awt.*;

@Getter
@AllArgsConstructor
public enum NotificationType {
    INFO(new Color(255, 255, 255, 195), FontUtil.UNICODES_UI.INFO),
    YES(new Color(56, 182, 42, 195), FontUtil.UNICODES_UI.YES),
    NO(new Color(182, 42, 42, 195), FontUtil.UNICODES_UI.NO),
    WARNING(new Color(194, 161, 7, 194), FontUtil.UNICODES_UI.WARN),
    ERROR(new Color(168, 20, 20, 194), FontUtil.UNICODES_UI.BLOCK);

    final Color color;
    final String icon;
}
