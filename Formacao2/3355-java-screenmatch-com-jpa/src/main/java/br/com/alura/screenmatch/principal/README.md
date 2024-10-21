# 1a aula
- Buscar series na API; Interacao com o usuario atraves do loop.
- Metodos privados; Encapsulamento nos metodos que somente a classe ira usar.
>Excecao do construtor que espera um repositorio `SerieRepository`, pois ele esta sendo chamado no metodo `run()` na aplicacao principal: <br>

```java


class Principal {
    ...
            ...
            ...
            ...

    public Principal(SerieRepository repo) {
        this.repositorio = repo;
    }
}

@SpringBootApplication
class ScreenmatchApplication {
    @Autowired
    private SerieRepository repositorio;

    public static void main(String[] args) throws NullPointerException {
        SpringApplication.run(ScreenmatchApplication.class, args);
    }
    @Override
    public void run(String... args) throws Exception {
        Principal principal = new Principal(repositorio);
        principal.exibeMenu();
    }
}
```
- Adicionar mais informacoes aos dados buscados. Realizar mapemamento entre os atributos da API e os atributos das classes Dados n (Record):
```java
@JsonIgnoreProperties(ignoreUnknown = true)
public record DadosSerie(
                        @JsonAlias("Title") String titulo,
                         @JsonAlias("totalSeasons") Integer totalTemporadas,
                         @JsonAlias("Genre") String genero,
                         @JsonAlias("imdbRating") String avaliacao,
                         @JsonAlias("Actors") String atores,
                         @JsonAlias("Plot") String sinopse,
                         @JsonAlias("Awards") String premio,
                         @JsonAlias("Runtime") String duracao,
                         @JsonAlias("Released") String lancamento,
                         @JsonAlias("Country") String pais,
                         @JsonAlias("Ratings") List<Rating> votacoes,
                        @JsonAlias("Poster" ) String posterUrl
        ){
}
@JsonIgnoreProperties(ignoreUnknown = true)
public record DadosEpisodio(@JsonAlias("Title") String titulo,
                            @JsonAlias("Episode") Integer numero,
                            @JsonAlias("imdbRating") String avaliacao,
                            @JsonAlias("Released") String dataLancamento) {
}@JsonIgnoreProperties(ignoreUnknown = true)
public record DadosTemporada(@JsonAlias("Season") Integer numero,
                             @JsonAlias("Episodes") List<DadosEpisodio> episodios) {
}
```

- Converter os dados da API para uma classe. A classe `Serie` foi criada para representar de maneira clara nossos dados; Foi utilizado varios metodos de conversao:
```java

public interface IConverteDados {
    <T> T  obterDados(String json, Class<T> classe);
}
public class ConverteDados implements IConverteDados {
    private ObjectMapper mapper = new ObjectMapper();

    @Override
    public <T> T obterDados(String json, Class<T> classe) {
        try {
            return mapper.readValue(json, classe);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
public class ConsumoApi {

    public String obterDados(String endereco) {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endereco))
                .build();
        HttpResponse<String> response = null;
        try {
            response = client
                    .send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        String json = response.body();
        return json;
    }
}

class Principal {
    private ConsumoApi consumo = new ConsumoApi();
    private ConverteDados conversor = new ConverteDados();
    ...
            ...
            ...
    private DadosSerie getDadosSerie() {
        System.out.println("Digite o nome da série para busca");
        var nomeSerie = leitura.nextLine();
        var json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);
        DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
        return dados;
    }
    private void buscarEpisodioPorSerie(){
        listarSeriesBuscadas();
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
```
- Revisao de como utilizar o "if reduzido". Utilizou-se a classe `OptionalDouble` para lidar com valores  decimais com seus possiveis erros, foi utilizado os metodos `of` e `orElse`, que lembram muito o codigo if e else, e sao muiteis para lidar com Exceptions:
```java
class Serie {
    public Serie(DadosSerie dadosSerie){
        this.titulo = dadosSerie.titulo();
        this.totalTemporadas = dadosSerie.totalTemporadas();
        this.avaliacao= OptionalDouble.of(Double.valueOf(dadosSerie.avaliacao())).orElse(0);
        this.genero=Categoria.fromString(dadosSerie.genero().split(",")[0].trim());
        this.atores = dadosSerie.atores();
        this.posterUrl = dadosSerie.posterUrl();
        this.sinpose = ConsultaMyMemory.obterTraducao(dadosSerie.sinopse()).trim();
        //this.sinpose = ConsultaChatGPT.obterTraducao(dadosSerie.sinopse()).trim();
    }
}
```
- Criacao de um `Enum`. Foi utilizado para organizar as series por genero:
```java
public enum Categoria {
    ACAO("Action", "Acao"),
    ROMANCE("Romance", "Romance"),
    COMEDIA("Comedy", "Comedia"),
    DRAMA("Drama", "Drama"),
    CRIME("Crime", "Crime"),
    AVENTURA("Adventure", "Aventura"),
    ANIMACAO("Animation", "Animacao"),
    DOCUMENTARIO("Documentary", "Documentario"),
    TERROR("Horror", "Terror");
    

    private String categoriaOmdb;
    private String categoriaPortugues;
    Categoria(String categoriaOmdb, String categoriaPortugues){
        this.categoriaOmdb = categoriaOmdb;
        this.categoriaPortugues = categoriaPortugues;
    }

    public static Categoria fromPortugues(String text) {
        for (Categoria categoria : Categoria.values()) {
            if (categoria.categoriaPortugues.equalsIgnoreCase(text)) {
                return categoria;
            }
        }
        throw new IllegalArgumentException("Nenhuma categoria encontrada para a string fornecida: " + text);
    }

    public static Categoria fromString(String text) {
        for (Categoria categoria : Categoria.values()) {
            if (categoria.categoriaOmdb.equalsIgnoreCase(text)) {
                return categoria;
            }
        }
        throw new IllegalArgumentException("Nenhuma categoria encontrada para a string fornecida: " + text);
    }
}
```
- Consumir a API do ChatGPT para traduzir os dados: !Deprecated
```java
public static String obterTraducao(String texto) {
        OpenAiService service = new OpenAiService("");

        CompletionRequest requisicao = CompletionRequest.builder()
                .model("")
                .prompt("traduza para o português o texto: " + texto)
                .maxTokens(1000)
                .temperature(0.7)
                .build();

        var resposta = service.createCompletion(requisicao);
        return resposta.getChoices().get(0).getText();
} 
```
- Consumir a API do MyMemori para traduzir os dados:
```java
public class ConsultaMyMemory {
    public static String obterTraducao(String text) {
        ObjectMapper mapper = new ObjectMapper();

        ConsumoApi consumo = new ConsumoApi();

        String texto = URLEncoder.encode(text);
        String langpair = URLEncoder.encode("en|pt-br");

        String url = "https://api.mymemory.translated.net/get?q=" + texto + "&langpair=" + langpair;

        String json = consumo.obterDados(url);

        DadosTraducao traducao;
        try {
            traducao = mapper.readValue(json, DadosTraducao.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return traducao.dadosResposta().textoTraduzido();
    }
}
@JsonIgnoreProperties(ignoreUnknown = true)
public record DadosResposta(@JsonAlias(value = "translatedText") String textoTraduzido) {
}
@JsonIgnoreProperties(ignoreUnknown = true)
public record DadosTraducao(@JsonAlias(value = "responseData") DadosResposta dadosResposta) {
}


```