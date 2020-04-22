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

public class MoneyTransactionServlet extends HttpServlet {

    BankClientService bankClientService = new BankClientService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.getWriter().println(PageGenerator.getInstance().getPage("moneyTransactionPage.html", new HashMap<>()));
        resp.setContentType("text/html;charset=utf-8");
        resp.setStatus(HttpServletResponse.SC_OK);

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        BankClient sender = new BankClient();
        sender.setName(req.getParameter("senderName"));
        sender.setPassword(req.getParameter("senderPass"));
        try {
            if (bankClientService.sendMoneyToClient(
                    sender, req.getParameter("nameTo"), Long.valueOf(req.getParameter("count")))
            ) {
                req.setAttribute("message", "The transaction was successful");
            } else {
                req.setAttribute("message", "transaction rejected");
            }
        } catch (DBException e) {
            e.printStackTrace();
        }
        getServletContext().getRequestDispatcher("/result").forward(req, resp);
    }
}
