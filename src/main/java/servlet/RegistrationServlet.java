package servlet;

import exception.DBException;
import model.BankClient;
import service.BankClientService;
import util.PageGenerator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;

public class RegistrationServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.getWriter().println(PageGenerator.getInstance().getPage("registrationPage.html", new HashMap<>()));
        resp.setContentType("text/html;charset=utf-8");
        resp.setStatus(HttpServletResponse.SC_OK);


    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        BankClient bankClient = new BankClient();
        bankClient.setName(req.getParameter("name"));
        bankClient.setPassword(req.getParameter("password"));
        bankClient.setMoney(Long.valueOf(req.getParameter("money")));
        try {
            if (new BankClientService().addClient(bankClient)) {
                req.setAttribute("message", "Add client successful");
            } else {
                req.setAttribute("message", "Client not add");
            }
        } catch (DBException e) {
            e.printStackTrace();
        }
        getServletContext().getRequestDispatcher("/result").forward(req, resp);
    }
}
