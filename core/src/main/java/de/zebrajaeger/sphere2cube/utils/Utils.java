package de.zebrajaeger.sphere2cube.utils;

import de.zebrajaeger.sphere2cube.img.ITargetImage;
import org.apache.commons.io.FilenameUtils;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class Utils {
    private Utils() {
    }

    public static String durationToString(long durationMs) {
        long days = TimeUnit.MILLISECONDS.toDays(durationMs);
        long hours = TimeUnit.MILLISECONDS.toHours(durationMs) % 24;
        long minutes = TimeUnit.MILLISECONDS.toMinutes(durationMs) % 60;
        long seconds = TimeUnit.MILLISECONDS.toSeconds(durationMs) % 60;
        long milliseconds = durationMs % 1000;

        StringBuilder sb = new StringBuilder();
        boolean forcePrint = false;

        if (days > 0) {
            sb.append(String.format("%d Days", days));
            forcePrint = true;
        }

        if (forcePrint || hours > 0) {
            if (forcePrint) {
                sb.append(" ");
            }
            sb.append(String.format("%d Hours", hours));
            forcePrint = true;
        }

        if (forcePrint || minutes > 0) {
            if (forcePrint) {
                sb.append(" ");
            }
            sb.append(String.format("%d Minutes", minutes));
            forcePrint = true;
        }

        if (forcePrint) {
            sb.append(" ");
        }
        sb.append(String.format("%d.%03d Seconds", seconds, milliseconds));

        return sb.toString();
    }

    public static Optional<ITargetImage.Format> findImageFormat(String fileName) {
        String ext = FilenameUtils.getExtension(fileName).toLowerCase();
        if ("jpg".equals(ext) || "jpeg".equals(ext)) {
            return Optional.of(ITargetImage.Format.JPG);
        } else if ("png".equals(ext)) {
            return Optional.of(ITargetImage.Format.PNG);
        } else {
            return Optional.empty();
        }
    }
}
