package vn.iotstar.controller;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.iotstar.service.IVideoService;
import vn.iotstar.service.impl.VideoServiceImpl;
import vn.iotstar.util.Constant;

@WebServlet(urlPatterns = { "/home", "/admin/home", "/manager/home" })
public class HomeController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final IVideoService videoService = new VideoServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // 10 video mới nhất hiển thị trên trang chủ
        req.setAttribute("latestVideos", videoService.findLatest(Constant.HOME_VIDEO_LIMIT));
        req.getRequestDispatcher("/views/home.jsp").forward(req, resp);
    }
}
