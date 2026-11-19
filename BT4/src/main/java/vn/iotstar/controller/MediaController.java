package vn.iotstar.controller;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.iotstar.util.Constant;

/**
 * Phát video từ thư mục upload: /media?fname=xxx.mp4
 * Hỗ trợ HTTP Range (206 Partial Content) để thẻ &lt;video&gt; tua được.
 */
@WebServlet("/media")
public class MediaController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String fname = req.getParameter("fname");
        if (fname == null || fname.isEmpty()) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        // getName() chặn kiểu ../../ truy cập file ngoài thư mục upload
        File file = new File(Constant.DIR, new File(fname).getName());
        if (!file.isFile()) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        long length = file.length();
        long start = 0;
        long end = length - 1;

        String range = req.getHeader("Range");
        boolean partial = false;
        if (range != null && range.startsWith("bytes=")) {
            try {
                String[] p = range.substring(6).split("-", 2);
                if (!p[0].isEmpty()) {
                    start = Long.parseLong(p[0]);
                    if (p.length > 1 && !p[1].isEmpty()) end = Long.parseLong(p[1]);
                } else { // "bytes=-N": N byte cuối
                    start = Math.max(0, length - Long.parseLong(p[1]));
                }
            } catch (NumberFormatException e) {
                resp.setHeader("Content-Range", "bytes */" + length);
                resp.sendError(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);
                return;
            }
            end = Math.min(end, length - 1);
            if (start > end || start >= length) {
                resp.setHeader("Content-Range", "bytes */" + length);
                resp.sendError(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);
                return;
            }
            partial = true;
        }

        String mime = getServletContext().getMimeType(file.getName());
        resp.setContentType(mime != null ? mime : "application/octet-stream");
        resp.setHeader("Accept-Ranges", "bytes");
        if (partial) {
            resp.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
            resp.setHeader("Content-Range", "bytes " + start + "-" + end + "/" + length);
        }
        long toSend = end - start + 1;
        resp.setContentLengthLong(toSend);

        try (RandomAccessFile raf = new RandomAccessFile(file, "r");
             OutputStream out = resp.getOutputStream()) {
            raf.seek(start);
            byte[] buf = new byte[8192];
            long remaining = toSend;
            int n;
            while (remaining > 0 && (n = raf.read(buf, 0, (int) Math.min(buf.length, remaining))) != -1) {
                out.write(buf, 0, n);
                remaining -= n;
            }
        }
    }
}
