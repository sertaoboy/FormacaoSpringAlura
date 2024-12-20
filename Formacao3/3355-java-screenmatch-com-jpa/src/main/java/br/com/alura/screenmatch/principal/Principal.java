package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.model.*;
import br.com.alura.screenmatch.repository.SerieRepository;
import br.com.alura.screenmatch.service.ConsumoApi;
import br.com.alura.screenmatch.service.ConverteDados;

import java.util.*;
import java.util.stream.Collectors;

public class Principal {

    private Scanner leitura = new Scanner(System.in);
    private ConsumoApi consumo = new ConsumoApi();
    private ConverteDados conversor = new ConverteDados();
    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=6585022c";
    List<DadosSerie> dadosSeries = new ArrayList<>();

    private SerieRepository repositorio;

    private List<Serie> series = new ArrayList<>();

    private Optional<Serie> serieBusca;




    public void exibeMenu() {

        int opcao;
        do{
            var menu = """
                1 - Buscar séries
                2 - Salvar episodios de uma serie 
                3 - Listar series buscadas
                4 - Buscar serie por titulo
                5 - Buscar series por ator
                6 - Buscar top 5 series
                7 - Buscar series por categoria
                8 - Filtrar series pela quantidades de temporada
                9 - Filtrar episodio por trecho
                10 - Listar top 5 episodios de uma serie
                11 - Buscar episodios a partir de uma data
                
                0 - Sair                                 
                """;

            System.out.println(menu);
            opcao = leitura.nextInt();
            leitura.nextLine();
            switch (opcao) {
                case 1:
                    System.out.println("----------------------------------------------------------");
                    buscarSerieWeb();
                    System.out.println("----------------------------------------------------------");
                    break;
                case 2:
                    System.out.println("----------------------------------------------------------");
                    buscarEpisodioPorSerie();
                    System.out.println("----------------------------------------------------------");
                    break;
                case 3:
                    System.out.println("----------------------------------------------------------");
                    listarSeriesBuscadas();
                    System.out.println("----------------------------------------------------------");
                    break;
                case 4:
                    System.out.println("----------------------------------------------------------");
                    buscarSeriePorTitulo();
                    System.out.println("----------------------------------------------------------");
                    break;
                case 5:
                    System.out.println("----------------------------------------------------------");
                    buscarSeriesPorAtor();
                    System.out.println("----------------------------------------------------------");
                    break;
                case 6:
                    System.out.println("----------------------------------------------------------");
                    buscarTopCincoSeries();
                    System.out.println("----------------------------------------------------------");
                    break;
                case 7:
                    System.out.println("----------------------------------------------------------");
                    buscarSeriesPorCategoria();
                    System.out.println("----------------------------------------------------------");
                    break;
                case 8:
                    System.out.println("----------------------------------------------------------");
                    buscarSeriesPorTemporadasEAvaliacao();
                    System.out.println("----------------------------------------------------------");
                    break;
                case 9:
                    System.out.println("----------------------------------------------------------");
                    buscarEpisodioPorTrecho();
                    System.out.println("----------------------------------------------------------");
                    break;
                case 10:
                    System.out.println("----------------------------------------------------------");
                    topEpisodiosPorSerie();
                    System.out.println("----------------------------------------------------------");
                    break;
                case 0:
                    System.out.println("Saindo...");
                    break;
                case 11:
                    System.out.println("----------------------------------------------------------");
                    buscarEpisodiosAposUmaData();
                    System.out.println("----------------------------------------------------------");
                default:
                    System.out.println("Opção inválida");
            }
        }while(opcao!=0);

    }

    private void buscarEpisodiosAposUmaData() {
        buscarSeriePorTitulo();
        if(serieBusca.isPresent()){
            Serie serie = serieBusca.get();
            System.out.println("Digite o ano de lancamento desejado:");
            int anoLancamento = leitura.nextInt();
            leitura.nextLine();
            List<Episodio> episodiosAno = repositorio.episodiosPorSerieEAno(serie,anoLancamento);
            episodiosAno.forEach(System.out::println);
        }
    }


    private void buscarSeriesPorTemporadasEAvaliacao(){
        System.out.println("Insira um total de temporadas para filtrar:");
        var totalTemporadas = leitura.nextInt();
        System.out.println("Com a avaliacao a partir de qual valor?");
        var avaliacaoInserida = leitura.nextDouble();
//        List<Serie> filtroSeries = repositorio.findByTotalTemporadasLessThanEqualAndAvaliacaoGreaterThanEqual(totalTemporadas,avaliacaoInserida);
        List<Serie> filtroSeries = repositorio.seriesPorTemporadaEAvaliacao(totalTemporadas,avaliacaoInserida);
        System.out.println("*** Series filtradas ***");
        filtroSeries.forEach(s -> System.out.println(s.getTitulo()+" - avaliacao:"+s.getAvaliacao()));
    }

    private void buscarSeriesPorCategoria(){
        System.out.println("Qual categoria deseja buscar?");
        var nomeGenero = leitura.nextLine();
        Categoria categoria = Categoria.fromPortugues(nomeGenero);
        List<Serie> seriesPorCategoria = repositorio.findByGenero(categoria);
        System.out.println("Series da categoria "+nomeGenero+": ");
        seriesPorCategoria.forEach(System.out::println);
    }

    private void buscarTopCincoSeries(){
        List<Serie> seriesTop = repositorio.findTop5ByOrderByAvaliacaoDesc();
        seriesTop.forEach(s -> System.out.println(s.getTitulo()+" avaliacao:"+s.getAvaliacao()));
    }

    private void buscarSeriesPorAtor(){
        System.out.println("Qual o nome para a busca?");
        String nomeAtor = leitura.nextLine();
        System.out.println("Avaliacoes a partir de que valor? ");
        var avaliacao = leitura.nextDouble();
        List<Serie> seriesEncontradas = repositorio.findByAtoresContainingIgnoreCaseAndAvaliacaoGreaterThanEqual(nomeAtor,avaliacao);
        System.out.println("Series em que "+nomeAtor+" trabalhou:");
        seriesEncontradas.forEach(s -> System.out.println(s.getTitulo() + ": "+s.getAvaliacao()));
    }


    private void buscarSeriePorTitulo() {
        System.out.println("Escolha uma serie pelo nome:");
        String nomeSerie = leitura.nextLine();
        serieBusca = repositorio.findByTituloContainingIgnoreCase(nomeSerie);
        if(serieBusca.isPresent()) {
            System.out.println("Dados da serie: "+serieBusca.get());
        }else{
            System.out.println("Serie nao encontrada.4");
        }
    }

    public Principal(SerieRepository repo) {
        this.repositorio = repo;
    }

    private void listarSeriesBuscadas(){

        series = repositorio.findAll();
//                dadosSeries.stream()
//                        .map(d-> new Serie(d))
//                                .collect(Collectors.toList());
        series.stream()
                .sorted(Comparator.comparing(Serie::getGenero))
                .forEach(System.out::println);
//       dadosSeries.forEach(System.out::println);
    }

    private void buscarSerieWeb() {
        System.out.println("Buscando dados da serie");
        DadosSerie dados = getDadosSerie();
        System.out.println("Instanciando serie");
        Serie serie = new Serie(dados);
        System.out.println("Salvando serie no banco de dados");
        repositorio.save(serie);
//        dadosSeries.add(dados);
//       System.out.println(dados);
    }

    private DadosSerie getDadosSerie() {
        System.out.println("Digite o nome da série para busca");
        var nomeSerie = leitura.nextLine();
        var json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);
        DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
        return dados;
    }

    private void buscarEpisodioPorSerie(){
        System.out.println("Listando series procuradas...");
        listarSeriesBuscadas();
//        DadosSerie dadosSerie = getDadosSerie();
        System.out.println("Escolha uma serie pelo nome:");
        String nomeSerie = leitura.nextLine();
        System.out.println("Procurando no banco de dados...");
        Optional<Serie> serie = repositorio.findByTituloContainingIgnoreCase(nomeSerie);
        if(serie.isPresent()) {
            System.out.println("Serie encontrada");
            Serie serieEncontrada = serie.get();
            System.out.println("Criando listas de temporadas...");
            List<DadosTemporada> temporadas = new ArrayList<>();
            for (int i = 1; i <= serieEncontrada.getTotalTemporadas(); i++) {
                System.out.println("Obtendo os dados da serie...");
                var json = consumo.obterDados(ENDERECO + serieEncontrada.getTitulo().replace(" ", "+") + "&season=" + i + API_KEY);
                System.out.println("Instanciando os dados das temporadas pelas series recebidas da API...");
                DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
                System.out.println("Adicioando os dados na lista...");
                temporadas.add(dadosTemporada);
            }
            System.out.println("*** Lista de Episodios***");
            temporadas.forEach(System.out::println);
            System.out.println("**************************");

            System.out.println("Criando uma stream de uma lista de episodios");
            List<Episodio> episodios = temporadas.stream()
                    .flatMap(d -> d.episodios().stream()
                            .map(e -> new Episodio(d.numero(), e)))
                    .collect(Collectors.toList());
            System.out.println("Setando episodios da serie ");
            serieEncontrada.setEpisodios(episodios);
            System.out.println("Salvando no banco de dados...");
            repositorio.save(serieEncontrada);
            System.out.println("Episodios salvos.");
        }else{
            System.out.println("Serie nao encontrada.");
        }
    }

    private void buscarEpisodioPorTrecho() {
        System.out.println("Insira o trecho que preocura:");
        String trechoInserido = leitura.nextLine();
        List<Episodio> episodiosEncontrados = repositorio.episodiosPorTrecho(trechoInserido);
        episodiosEncontrados.forEach(e ->
                System.out.printf("Serie-> %s: Temporada %s - Episodio %s - %s\n" ,
                        e.getSerie().getTitulo(), e.getTemporada(), e.getNumeroEpisodio(),
                        e.getTitulo()));
    }

    private void topEpisodiosPorSerie() {
        buscarSeriePorTitulo();
        if(serieBusca.isPresent()) {
            Serie serie = serieBusca.get();
            List<Episodio> topEpisodios = repositorio.topEpisodiosPorSerie(serie);
            topEpisodios.forEach(e ->
                    System.out.printf("Serie -> %s Temporada %s - Episodio %s - %s Avaliacao %s\n",
                            e.getSerie().getTitulo(), e.getTemporada(), e.getNumeroEpisodio(), e.getTitulo(), e.getAvaliacao()));
        }
    }
}