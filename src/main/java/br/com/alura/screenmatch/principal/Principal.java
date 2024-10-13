package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.model.DadosEpisodio;
import br.com.alura.screenmatch.model.DadosSerie;
import br.com.alura.screenmatch.model.DadosTemporada;
import br.com.alura.screenmatch.service.ConsumoApi;
import br.com.alura.screenmatch.service.ConverteDados;
import io.github.cdimascio.dotenv.Dotenv;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Principal {
    private Scanner leitura = new Scanner(System.in);
    private ConsumoApi consumo = new ConsumoApi();
    private ConverteDados conversor = new ConverteDados();
    private Dotenv dotenv = Dotenv.load();
    private final String apiKey = dotenv.get("API_KEY");
    private final String ENDERECO = "https://www.omdbapi.com/?t=";

    public void exibirMenu(){
        System.out.println("Insira o nome da serie desejada:");
        String serieInserida = leitura.nextLine();
        var json = consumo.obterDados(ENDERECO + serieInserida.replace(" ","+") + apiKey);
        DadosSerie dadosSerie = conversor.obterDados(json, DadosSerie.class);

        List<DadosTemporada> dadosTemporadas = new ArrayList<>();

        for(int i = 1; i<=dadosSerie.totalTemporadas(); i++) {
        	json = consumo.obterDados(ENDERECO + serieInserida.replace(" ","+")+ "&season="+ i + apiKey);
            DadosTemporada dadosTemporada = conversor.obterDados(json,DadosTemporada.class);
            dadosTemporadas.add(dadosTemporada);
        }
        for(DadosTemporada d :dadosTemporadas) {
            System.out.println(d);
        }





//        for(int i=0;i<dadosSerie.totalTemporadas();i++){
//            List<DadosEpisodio> episodiosTemporada = dadosTemporadas.get(i).episodios();
//            for(int j = 0; j<episodiosTemporada.size(); j++) {
//                System.out.println(episodiosTemporada.get(j).titulo());
//            }
//        }
        dadosTemporadas.forEach(t -> t.episodios().forEach(e -> System.out.println(e.titulo())));







    }


}
