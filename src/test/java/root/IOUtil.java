package root;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IOUtil {

    public static boolean isMultiPart(RequestBody body) {
        return getBoundary(body) != null;
    }

    public static String readRequestBody(RequestBody body) throws IOException {
        Buffer sink = new Buffer();
        body.writeTo(sink);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        sink.copyTo(bos);
        return new String(bos.toByteArray(), 0, bos.size(), StandardCharsets.UTF_8);
    }

    private static final Pattern CONTENT_DISPOSITION_PATTERN =
            Pattern.compile("^Content-Disposition: form-data; name=\"([^\"]+)\"(?:; filename=\"([^\"]+)\")?$");
    private static final String validFileContent = "Content-Type: application/octet-stream; charset=utf-8";

    public static Map<String, Object> parseMultipart(RequestBody body) throws IOException {
        //primitive multipart parser
        String boundary = getBoundary(body);
        if(boundary == null)
            throw new IllegalArgumentException("RequestBody is not of type Multipart");
        String[] lines = readRequestBody(body).split("\r\n");
        if(lines.length == 0)
            return Collections.emptyMap();
        if(!lines[0].equals(boundary) ||!lines[lines.length-1].equals(boundary+"--"))
            throw new IllegalArgumentException("Boundary given is invalid");

        Map<String, Object> parts = new HashMap<>();
        String name = null, filename = null;
        StringBuilder sb = new StringBuilder();
        int lengthToCheck = -1;
        boolean inBody = false;

        for(int i = 1; i < lines.length; i++) {
            String line = lines[i];
            if(line.equals(boundary) || i == lines.length - 1) {
                if(!inBody)
                    throw new IllegalStateException("Multipart body was never reached");
                if(lengthToCheck != -1) {
                    if(sb.length() != lengthToCheck)
                        throw new IllegalStateException("Length check for multipart content failed");
                    lengthToCheck = -1;
                }
                if(filename != null) {
                    parts.put(name, new MultiPartFile(filename, sb.toString().getBytes(StandardCharsets.UTF_8)));
                    filename = null;
                } else {
                    parts.put(name, sb.toString());
                }
                name = null;
                inBody = false;
                sb.setLength(0);
            } else if(inBody) {
                if(sb.length() > 0)
                    sb.append("\r\n");
                sb.append(line);
            } else {
                if(line.isEmpty()) {
                    if(name == null)
                        throw new IllegalStateException("multipart name was never given");
                    inBody = true;
                } else if(line.startsWith("Content-Disposition:")) {
                    Matcher matcher = CONTENT_DISPOSITION_PATTERN.matcher(line);
                    if(!matcher.matches())
                        throw new IllegalStateException("Content-Disposition does not match pattern");
                    name = matcher.group(1);
                    if(matcher.group(2) != null)
                        filename = matcher.group(2);
                } else if(line.startsWith("Content-Type:") && !line.equals(validFileContent)) {
                    throw new IllegalStateException("Invalid Content-type provided");
                } else if(line.startsWith("Content-Length:")) {
                    lengthToCheck = Integer.parseInt(line.substring(15).trim());
                }
            }
        }
        return parts;
    }

    private static Pattern MULTIPART_TYPE_PATTERN = Pattern.compile("^multipart/form-data; boundary=(.+)$");

    private static String getBoundary(RequestBody body) {
        MediaType mediaType = body.contentType();
        if(mediaType == null)
            return null;
        Matcher matcher = MULTIPART_TYPE_PATTERN.matcher(mediaType.toString());
        if(!matcher.matches())
            return null;
        return "--" + matcher.group(1);
    }

    public static class MultiPartFile {
        public final String filename;
        public final byte[] content;

        private MultiPartFile(String filename, byte[] content) {
            this.filename = filename;
            this.content = content;
        }
    }
}
