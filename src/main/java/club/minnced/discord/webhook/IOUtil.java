package club.minnced.discord.webhook;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okio.BufferedSink;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.zip.GZIPInputStream;

public class IOUtil {
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    public static final MediaType OCTET = MediaType.parse("application/octet-stream; charset=utf-8");
    public static final byte[] EMPTY_BYTES = new byte[0];

    @NotNull
    public static byte[] readAllBytes(@NotNull InputStream stream) throws IOException {
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

    @Nullable
    public static InputStream getBody(okhttp3.Response req) throws IOException {
        List<String> encoding = req.headers("content-encoding");
        ResponseBody body = req.body();
        if (!encoding.isEmpty() && body != null) {
            return new GZIPInputStream(body.byteStream());
        }
        return body != null ? body.byteStream() : null;
    }

    @NotNull
    public static JSONObject toJSON(@NotNull InputStream input) {
        return new JSONObject(new JSONTokener(input));
    }

    public interface SilentSupplier<T> {
        @Nullable
        T get() throws Exception;
    }

    public static class Lazy {
        private final SilentSupplier<?> supply;

        public Lazy(SilentSupplier<?> supply) {
            this.supply = supply;
        }

        @NotNull
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

    public static class OctetBody extends RequestBody {
        private final InputStream in;

        public OctetBody(@NotNull InputStream in) {
            this.in = in;
        }

        @Override
        public MediaType contentType() {
            return OCTET;
        }

        @Override
        public void writeTo(BufferedSink sink) throws IOException {
            byte[] data = readAllBytes(in);
            sink.write(data);
        }
    }
}
