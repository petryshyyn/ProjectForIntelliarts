package com.company.springApplication.controllers;

import com.company.springApplication.model.Currency;
import com.company.springApplication.model.Expenses;
import com.company.springApplication.rep.ExpensesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class MainController {

    @Autowired
    private ExpensesRepository expensesRepository;

    @GetMapping("/")
    public String homePage(Model model) {
        return "home";
    }

    @GetMapping("add")
    public String addPage(Model model){
        return "add";
    }

    @PostMapping("add")
    public String expenseNewAdd(@RequestParam String date, @RequestParam double amount, @RequestParam String currency,
                                @RequestParam String product , Model model) throws ParseException {
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date utilDate = formatter.parse(date);
        java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
        Currency newCurrency =  Currency.valueOf(currency);
        Expenses expenses = new Expenses(sqlDate, amount, newCurrency, product);
        expensesRepository.save(expenses);
        return "redirect:all";
    }

    @GetMapping("all")
    public String allPage(Model model){
        Iterable<Expenses> expenses = expensesRepository.findAllByOrderByDate(Sort.by("date"));

        model.addAttribute("expenses", expenses);
        return "all";
    }

    @GetMapping("remove")
    public String removePage(Model model){
        return "remove";
    }

    @PostMapping("remove")
    public String remove(@RequestParam String date, Model model) throws ParseException {
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date utilDate = formatter.parse(date);
        java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
        if(!expensesRepository.existsByDate(sqlDate)){
            model.addAttribute("again" , "enter the date again");
            return "remove";
        }

                List<java.sql.Date> listOfDate = new ArrayList<>();
                for(Expenses expenses : expensesRepository.findByDate(sqlDate)){
                    listOfDate.add(expenses.getDate());
                }
                    expensesRepository.deleteByDateIn(listOfDate);

        return "redirect:all";
    }

    @GetMapping("get_total")
    public String getTotalPage(Model model){
        return "get_total";
    }

    @PostMapping("get_total")
    public String getTotal(@RequestParam String currency , Model model){
        double listUAH = 0d;
        double listUSD = 0d;
        double listEUR = 0d;
        double sum = 0d;
        for(Expenses i : expensesRepository.findAll()){
            if(i.getCurrency() == Currency.UAH){
                listUAH += i.getAmount();
            }
            else if(i.getCurrency() == Currency.USD){
                listUSD += i.getAmount();

            } else {
                listEUR += i.getAmount();
            }
        }
        Currency newCurrency =  Currency.valueOf(currency);
        if(newCurrency == Currency.UAH){
            sum = listUAH + listEUR * 33.12 + listUSD * 27.72;
        }
        if(newCurrency == Currency.EUR){
            sum = listUAH/33.12 + listEUR + listUSD * 1.19;
        }
        if(newCurrency == Currency.USD){
            sum = listUAH/27.72 + listEUR/1.19 + listUSD;
        }
        model.addAttribute("base", sum);
        return "get_total";
    }
}
