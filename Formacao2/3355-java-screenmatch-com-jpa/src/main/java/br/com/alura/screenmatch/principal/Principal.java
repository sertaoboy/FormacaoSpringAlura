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




    public void exibeMenu() {

        int opcao;
        do{
            var menu = """
                1 - Buscar séries
                2 - Buscar episódios
                3 - Listar series buscadas
                4 - Buscar serie por titulo
                5 - Buscar series por ator
                6 - Buscar top 5 series
                7 - Buscar series por categoria
                8 - Filtrar series pela quantidades de temporada
                
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

                case 0:
                    System.out.println("Saindo...");
                    break;
                default:
                    System.out.println("Opção inválida");
            }
        }while(opcao!=0);

    }

    private void buscarSeriesPorTemporadasEAvaliacao(){
        System.out.println("Insira um total de temporadas para filtrar:");
        var totalTemporadas = leitura.nextInt();
        System.out.println("Com a avaliacao a partir de qual valor?");
        var avaliacaoInserida = leitura.nextDouble();
        List<Serie> filtroSeries = repositorio.findByTotalTemporadasLessThanEqualAndAvaliacaoGreaterThanEqual(totalTemporadas,avaliacaoInserida);
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
        Optional<Serie> serieBuscada = repositorio.findByTituloContainingIgnoreCase(nomeSerie);
        if(serieBuscada.isPresent()) {
            System.out.println("Dados da serie: "+serieBuscada.get());
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
        DadosSerie dados = getDadosSerie();
        Serie serie = new Serie(dados);
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
        listarSeriesBuscadas();
//        DadosSerie dadosSerie = getDadosSerie();
        System.out.println("Escolha uma serie pelo nome:");
        String nomeSerie = leitura.nextLine();
        Optional<Serie> serie = repositorio.findByTituloContainingIgnoreCase(nomeSerie);
        if(serie.isPresent()) {
            Serie serieEncontrada = serie.get();
            List<DadosTemporada> temporadas = new ArrayList<>();
            for (int i = 1; i <= serieEncontrada.getTotalTemporadas(); i++) {
                var json = consumo.obterDados(ENDERECO + serieEncontrada.getTitulo().replace(" ", "+") + "&season=" + i + API_KEY);
                DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
                temporadas.add(dadosTemporada);
            }
            temporadas.forEach(System.out::println);

            List<Episodio> episodios = temporadas.stream()
                    .flatMap(d -> d.episodios().stream()
                            .map(e -> new Episodio(d.numero(), e)))
                    .collect(Collectors.toList());
            serieEncontrada.setEpisodios(episodios);
            repositorio.save(serieEncontrada);
        }else{
            System.out.println("Serie nao encontrada.");
        }
    }
}