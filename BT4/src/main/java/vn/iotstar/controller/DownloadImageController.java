package vn.iotstar.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.iotstar.util.Constant;

@WebServlet("/image")
public class DownloadImageController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String fname = req.getParameter("fname");
        if (fname == null || fname.isEmpty()) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        // getName() để chặn kiểu ../../ truy cập file ngoài thư mục
        File file = new File(Constant.DIR, new File(fname).getName());
        if (!file.exists()) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        String mime = getServletContext().getMimeType(file.getName());
        resp.setContentType(mime != null ? mime : "application/octet-stream");
        Files.copy(file.toPath(), resp.getOutputStream());
    }
}