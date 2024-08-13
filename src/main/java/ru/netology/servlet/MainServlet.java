package ru.netology.servlet;

import ru.netology.controller.PostController;
import ru.netology.repository.PostRepository;
import ru.netology.service.PostService;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MainServlet extends HttpServlet {

    private static final String METHOD_GET = "GET";
    private static final String METHOD_POST = "POST";
    private static final String METHOD_DELETE = "DELETE";
    private static final String PATH_POSTS = "/api/posts";
    private static final String PATH_SEARCH = "/api/posts/\\d+";
    private static final String PATH_DELIMIT = "/";

    private PostController controller;

    @Override
    public void init() {
        final var repository = new PostRepository();
        final var service = new PostService(repository);
        controller = new PostController(service);
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) {
        // если деплоились в root context, то достаточно этого
        try {
            final var path = req.getRequestURI();
            final var method = req.getMethod();
            // primitive routing
            if (method.equals(METHOD_GET) && path.equals(PATH_POSTS)) {
                controller.all(resp);
                return;
            }
            if (method.equals(METHOD_GET) && path.matches(PATH_SEARCH)) {
                // easy way
                final var id = Long.parseLong(path.substring(path.lastIndexOf(PATH_DELIMIT)+1));
                controller.getById(id, resp);
                return;
            }
            if (method.equals(METHOD_POST) && path.equals(PATH_POSTS)) {
                controller.save(req.getReader(), resp);
                return;
            }
            if (method.equals(METHOD_DELETE) && path.matches(PATH_SEARCH)) {
                // easy way
                final var id = Long.parseLong(path.substring(path.lastIndexOf(PATH_DELIMIT)+1));
                controller.removeById(id, resp);
                return;
            }
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
