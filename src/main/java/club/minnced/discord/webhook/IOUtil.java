package club.minnced.discord.webhook;

import java.io.IOException;
import java.io.InputStream;

public class IOUtil {
    private static final byte[] EMPTY_BYTES = new byte[0];

    public static byte[] readAllBytes(InputStream stream) throws IOException {
        int count = 0, pos = 0;
        byte[] output = EMPTY_BYTES;
        byte[] buf = new byte[1024];
        while ((count = stream.read(buf)) > 0) {
            if (pos + count >= output.length) {
                byte[] tmp = output;
                output = new byte[pos + count];
                System.arraycopy(tmp, 0, output, 0, tmp.length);
            }

            for (int i = 0; i < count; i++) {
                output[pos++] = buf[i];
            }
        }
        return output;
    }

    public interface SilentSupplier<T> {
        T get() throws Exception;
    }

    public static class Lazy {
        private final SilentSupplier<?> supply;

        public Lazy(SilentSupplier<?> supply) {
            this.supply = supply;
        }

        @Override
        public String toString() {
            try {
                return String.valueOf(supply.get());
            }
            catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }
    }
}
