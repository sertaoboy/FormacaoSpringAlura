package br.com.alura.screenmatch.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SerieController {


    @GetMapping("/series")
    public String obterSeries(){
        return "aqui vao ser listadas as series";
    }

}