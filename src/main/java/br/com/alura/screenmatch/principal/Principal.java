package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.model.DadosEpisodio;
import br.com.alura.screenmatch.model.DadosSerie;
import br.com.alura.screenmatch.model.DadosTemporada;
import br.com.alura.screenmatch.model.Episodio;
import br.com.alura.screenmatch.service.ConsumoApi;
import br.com.alura.screenmatch.service.ConverteDados;
import io.github.cdimascio.dotenv.Dotenv;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

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

        List<DadosEpisodio> listaDadosTemporadas = dadosTemporadas.stream()
                .flatMap(t -> t.episodios().stream())
                .collect(Collectors.toList());

        System.out.println("\nTop 10 episodios:");
        listaDadosTemporadas.stream()
                .filter(e -> !e.avaliacao().equalsIgnoreCase("N/A"))
                .peek(e -> System.out.println("Filtrando(N/A) "+ e))
                .sorted(Comparator.comparing(DadosEpisodio::avaliacao).reversed())
                .peek(e -> System.out.println("Ordenacao "+ e))
                .limit(10)
                .peek(e -> System.out.println("Limite "+ e))
                .map(e -> e.titulo().toUpperCase())
                .peek(e -> System.out.println("Mapeando "+ e))
                .forEach(System.out::println);

        System.out.println("Lista de episodios:");
        List<Episodio> episodios = dadosTemporadas.stream()
                .flatMap(t -> t.episodios().stream()
                        .map(d -> new Episodio(t.numero(), d))
                ).collect(Collectors.toList());
        episodios.forEach(System.out::println);
        System.out.println("Insira o titulo desejado para busca");
        String trechoTitulo = leitura.nextLine();
        Optional<Episodio> episodioBuscado = episodios.stream()
                .filter(e -> e.getTitulo().toUpperCase().contains(trechoTitulo.toUpperCase())) //convertendo o titulo do episodio buscado para maiusculo; evitando excecoes do input
                .findFirst();
        if(episodioBuscado.isPresent()) {
            System.out.println("Episodio encontrado");
            System.out.println("Temporada: "+episodioBuscado.get().getTemporada());
        }else{
            System.out.println("Episodio nao encontrado");
        }


        System.out.println("Insira o ano dos episodios desejados:");
        var ano = leitura.nextInt();
        leitura.nextLine();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate dataBusca = LocalDate.of(ano,1,1);
        episodios.stream()
                .filter(e -> e.getDataLancamento() != null && e.getDataLancamento().isAfter(dataBusca))
                .forEach(e -> System.out.println(
                        "Temporada:"+e.getTemporada() +
                                "Episodio:"+e.getTitulo() +
                                "Data lancamento:"+e.getDataLancamento().format(formatter)
                ));
        Map<Integer, Double>avaliacoesPorTemporada = episodios.stream()
                .filter(e -> e.getAvaliacao() > 0.0)
                .collect(Collectors.groupingBy(Episodio::getTemporada,
                        Collectors.averagingDouble(Episodio::getAvaliacao)));
        System.out.println(avaliacoesPorTemporada); //retorna a media de cada temporada



    }


}
