/*
 * Copyright 2018-2020 Florian Spieß
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package root;

import okhttp3.*;
import okio.Buffer;
import okio.Timeout;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPOutputStream;

public class IOTestUtil {

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

    public static Call forgeCall(Request req, String json, boolean useGzip) {
        return new FakeCall(req, json, useGzip);
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

    private static class FakeCall implements Call {
        private final Request req;
        private final String jsonResponse;
        private final boolean isGzip;

        public FakeCall(Request req, String jsonResponse, boolean isGzip) {
            this.req = req;
            this.jsonResponse = jsonResponse;
            this.isGzip = isGzip;
        }

        @Override
        public Timeout timeout() {
            return Timeout.NONE;
        }

        @Override
        public Request request() {
            return null;
        }

        @Override
        public Response execute() throws IOException {
            Response.Builder builder = new Response.Builder()
                    .request(req)
                    .protocol(Protocol.HTTP_1_1)
                    .code(200)
                    .message("OK")
                    .header("X-RateLimit-Remaining", "199")
                    .header("X-RateLimit-Limit", "200");

            if(isGzip) {
                builder.header("content-encoding", "gzip");
                ByteArrayOutputStream bout = new ByteArrayOutputStream();
                GZIPOutputStream gzipout = new GZIPOutputStream(bout);
                gzipout.write(jsonResponse.getBytes(StandardCharsets.UTF_8));
                gzipout.close();
                builder.body(ResponseBody.create(club.minnced.discord.webhook.IOUtil.JSON, bout.toByteArray()));
            } else {
                builder.body(ResponseBody.create(club.minnced.discord.webhook.IOUtil.JSON, jsonResponse));
            }

            return builder.build();
        }

        @Override
        public void enqueue(Callback responseCallback) {

        }

        @Override
        public void cancel() {

        }

        @Override
        public boolean isExecuted() {
            return false;
        }

        @Override
        public boolean isCanceled() {
            return false;
        }

        @Override
        public Call clone() {
            return null;
        }
    }
}
