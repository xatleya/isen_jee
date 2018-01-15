package yncrea.pw01.controller;

import yncrea.pw01.model.Drug;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(urlPatterns = "/drugs")
public class DrugsServlet extends HttpServlet {
    private List<Drug> drugs;

    @Override
    public void init() {
        this.drugs = new ArrayList<>();
        this.drugs.add(new Drug("Drug1", "Lab1"));
        this.drugs.add(new Drug("Drug2", "Lab2"));
    }

    public void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        req.setAttribute("drugs", drugs);
        req.getRequestDispatcher("DrugsList.jsp").forward(req, res);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        String name = req.getParameter("name");
        String lab = req.getParameter("lab");
        Drug newDrug = new Drug(name, lab);
        this.drugs.add(newDrug);
        res.sendRedirect("contextPath/drugs");
    }
}
