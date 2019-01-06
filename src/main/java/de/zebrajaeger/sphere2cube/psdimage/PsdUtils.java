package de.zebrajaeger.sphere2cube.psdimage;

import javax.imageio.stream.FileImageInputStream;
import java.io.IOException;

public class PsdUtils {
    public static String readCString(FileImageInputStream is, int bytes) throws IOException {
        byte[] buffer = new byte[bytes];
        is.read(buffer);
        return new String(buffer);
    }

    public static long[] readInts(FileImageInputStream is, int count) throws IOException {
        long[] result = new long[count];
        for(int i=0; i<count; ++i){
            result[i] = is.readInt();
        }
        return result;
    }
}
