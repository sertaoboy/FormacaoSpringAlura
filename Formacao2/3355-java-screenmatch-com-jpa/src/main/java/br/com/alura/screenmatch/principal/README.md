# Aula 1
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
# Aula 2
- Instalacao e configuracao do ambiente Postgres (Banco de dados relacional), alem de criar o banco de series.
- Adicao de dependencias: Dependencia da JPA ao `pom.xml` e as configuracoes do banco de dados em `application.properties`.
```xml

		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-data-jpa</artifactId>
		</dependency>

		<dependency>
			<groupId>org.postgresql</groupId>
			<artifactId>postgresql</artifactId>
			<scope>runtime</scope>
		</dependency>
```
```properties
spring.datasource.url=jdbc:postgresql://localhost/alura_series
spring.datasource.username=postgres
spring.datasource.password=${DB_PASSWORD}
spring.datasource.driver-class-name=org.postgresql.Driver
hibernate.dialect=org.hibernate.dialect.HSQLDialect

spring.jpa.hibernate.ddl-auto=update

spring.jpa.show-sql=true
spring.jpa.format-sql=true
```
- Utilzacao de anotacoes do Hibernate para mapear as entidades. `@Entity`, `@Transient` e `@Column` na classe `Serie`, indicando como seriam as configuracoes da tabela correspondente, foi criado a `SerieRepository`.
```java
@Entity
@Table(name = "series")
public class Serie {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //Indicando que o identificador sera autoincremental para a JPA
    private Long id;
    @Column(unique = true)
    private String titulo;
    private Integer totalTemporadas;
    private Double avaliacao;
    @Enumerated(EnumType.STRING)
    private Categoria genero;
    private String atores;
    private String posterUrl;
    @Column(length = 1000)
    private String sinpose;
...
...
...
```
- Injecao de dependencias. Interfaces do tipo Repository nao podem ser instanciadas se nao houver uma declaracao em classes gerenciadas pelo Spring, precedidas de `@Autowired`, indicando que esta sendo realizada uma injecao de dependencias.
```java
@SpringBootApplication
public class ScreenmatchApplication implements CommandLineRunner {
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
- Utilizar variaveis de ambiente. Utilizacao de variaveis no linux para proteger dados senviveis com a conexao do banco de dados.
- Manipular interfaces do tipo Repository. Para fazer operacoes basicas no banco de dados, como um CRUD, precisamos de uma interface do tipo Repository com o nosso tipo de dados. No caso, `SerieRepository`:
```java 
public interface SerieRepository extends JpaRepository<Serie, Long> {
    Optional <Serie> findByTituloContainingIgnoreCase(String nomeSerie);

    List<Serie> findByAtoresContainingIgnoreCaseAndAvaliacaoGreaterThanEqual(String nomeAtor, Double avaliacao);

    List<Serie> findTop5ByOrderByAvaliacaoDesc();

    List<Serie> findByGenero(Categoria categoria);

    List<Serie> findByTotalTemporadasLessThanEqualAndAvaliacaoGreaterThanEqual(int totalTemporadas, double totalAvaliacao);
}
```

# Aula 3
- Mapeamento relacionados entre entidades da JPA. Uso de anotacoes `@OneToMany` e `@ManyToOne` para identificar o relacionamento "um para muitos" de series e episodios.
```java
@Entity
@Table(name = "series")
public class Serie {
   ...
   ...
   ...
   ...
    OneToMany(mappedBy = "serie",cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Episodio> episodios = new ArrayList<>();

	...
	...
	...
}
```
```java

@Entity
@Table(name = "episodios")
public class Episodio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer temporada;
    private String titulo;
    private Integer numeroEpisodio;
    private Double avaliacao;
    private LocalDate dataLancamento;

    @ManyToOne
    private Serie serie;

	...
	...
	...
}
```
- Associar chaves estrangeiras. Entender o conceito de chave estrangeira, que e como o banco de dados identifica e configura relacionamentos.
- Trabalhar com tipos de Cascade. Como o fluxo de salvamento era salvar series e depois episodios, foi preciso configurar isso utilizando o atributo `Cascade`.
- Identificar como os dados sao carregados. Uso com o atributo `fetch`, que fala sobre carregar os dados de forma "preguicosa"(`lazy`) ou "ansiosa"(`eager`).
```java
public class Episodio {
    ...
    @ManyToOne
    private Serie serie;
    ...
    ...
```
```java
public class Serie {
   @OneToMany(mappedBy = "serie",cascade = CascadeType.ALL, fetch = FetchType.EAGER)
   private List<Episodio> episodios = new ArrayList<>();
```
- Configurar relacionamentos bidirecionais. Importancia de relacionamentos bidirecionais e deixamos as modificacoes aparecendo dos dois lados da relacao, fazendo tanto `setEpisodios()` na Serie quanto `setSerie()` nos Episodios.
```java
class Serie {
	...
	...
	...
	public void setEpisodios(List<Episodio> episodios) {
        	 episodios.forEach(e->e.setSerie(this));
     		 this.episodios = episodios;
    	}
}

class Episodio {
	...
	...
	public void setSerie(Serie serie) {
        this.serie = serie;
    }
}
```
# Aula 4
- Criacao de queries derivadas com a JPA. Conhecemos o recurso padrao da JPA para fazer buscas utilizando palavras-chave em metodos que utilizam a interface Repository:
```java
public interface SerieRepository extends JpaRepository<Serie, Long> {
    Optional <Serie> findByTituloContainingIgnoreCase(String nomeSerie);

    List<Serie> findByAtoresContainingIgnoreCaseAndAvaliacaoGreaterThanEqual(String nomeAtor, Double avaliacao);

    List<Serie> findTop5ByOrderByAvaliacaoDesc();

    List<Serie> findByGenero(Categoria categoria);

    List<Serie> findByTotalTemporadasLessThanEqualAndAvaliacaoGreaterThanEqual(int totalTemporadas, double totalAvaliacao);
}
```
- Mudancas quando utilizou-se streams e as derived queries:
```java
class principal {
	

	    private void listarSeriesBuscadas(){
       		 series = repositorio.findAll();
                 //dadosSeries.stream()
                	 //.map(d-> new Serie(d))
                 	 	//.collect(Collectors.toList());

                series.stream()
               		.sorted(Comparator.comparing(Serie::getGenero))
                		.forEach(System.out::println);
                // dadosSeries.forEach(System.out::println);
            }
    }
```
- Ler dados dinamicamente e armazenar em um Enum. Fazer correspondencia entre o que esta sendo digitado e um campo no Enum:
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
# Aula 5
- Utilizando query na interface SerieRepository. Com a anotacao `@Query` conseguimos passar um valor no seu parametro onde serie a query do banco de dados:
```java
public interface SerieRepository extends JpaRepository<Serie, Long> {
    ...
    @Query(value = "select * from series WHERE series.total_temporadas <= 5 AND series.avaliacao >= 7.5", nativeQuery = true)
    List<Serie> seriesPorTemporadaEAvaliacao();
}
```
- Assim, podemos alterar o metodo `buscarSeriesPorTemporada()` e passar a utilizar a implementacao da interface que modificamos:
```java
class Principal {
    ...
    private void buscarSeriesPorTemporadasEAvaliacao(){
        System.out.println("Insira um total de temporadas para filtrar:");
        var totalTemporadas = leitura.nextInt();
        System.out.println("Com a avaliacao a partir de qual valor?");
        var avaliacaoInserida = leitura.nextDouble();
//        List<Serie> filtroSeries = repositorio.findByTotalTemporadasLessThanEqualAndAvaliacaoGreaterThanEqual(totalTemporadas,avaliacaoInserida);  ---> antiga implementacao
        List<Serie> filtroSeries = repositorio.seriesPorTemporadaEAvaliacao();
        System.out.println("*** Series filtradas ***");
        filtroSeries.forEach(s -> System.out.println(s.getTitulo()+" - avaliacao:"+s.getAvaliacao()));
    }
    ...
} 
```
- *JPQL*. Java Persistence Query Language, linguagem de consulta da JPA. Torna mais flexiveis as consultas, podemos agora realizar pelos atributos e nao ficar presos as colunas do banco de dados.
```java
import br.com.alura.screenmatch.model.Serie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SerieRepository extends JpaRepository<Serie, Long> {
    ...
    @Query("select s from Serie s WHERE s.totalTemporadas <= :totalTemporadas AND s.avaliacao >= :avaliacao")
    List<Serie> seriesPorTemporadaEAvaliacao(int totalTemporadas, double avaliacao);
} 
```
- A principal diferença entre JPQL e SQL Nativo é, portanto, o nível de abstração. A JPQL abstrai os detalhes do banco de dados, permitindo que você trabalhe no nível do modelo de domínio. Isso pode tornar o código mais legível e fácil de manter.
- O SQL Nativo, por outro lado, permite um controle mais detalhado e direto sobre as consultas ao banco de dados. Isso pode ser útil se você precisa otimizar suas consultas para um banco de dados específico.
- Porém, há a questão da portabilidade. Como a JPQL é uma abstração de alto nível, ela é compatível com qualquer banco de dados que suporte a especificação JPA. Se você precisar mudar seu aplicativo de um banco de dados para outro, a maioria das suas consultas JPQL continuarão funcionando sem alterações. 
- Já com o SQL Nativo, se você decidir mudar de um banco de dados para outro, provavelmente terá que reescrever uma parte das suas consultas, visto que cada banco tem suas particularidades de sintaxe e funcionamento.
- A escolha entre JPQL e SQL Nativo muitas vezes depende das necessidades do seu projeto. Se a portabilidade é uma prioridade e se o seu modelo de domínio é complexo e rico de informações, a JPQL pode ser a melhor escolha. Se o desempenho é uma prioridade e você precisa de controle detalhado sobre suas consultas, o SQL Nativo pode ser a melhor opção.

- Adicao de nova funcionalidade. Atraves de um novo metodo na interface SerieRepository:

```java
import br.com.alura.screenmatch.model.Episodio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

interface SerieRepository extends JpaRepository<Serie, Long> {
    ...
    @Query("SELECT e from Serie s JOIN s.episodios e WHERE e.titulo ILIKE %:trechoEpisodio")
    List<Episodio> episodiosPorTrecho(String trechoEpisodio);
}
```
- LIKE: um comando SQL usado na clausula WHERE para procurar um padrao especifico em uma coluna. E uma maneira que voce pode usar um filtro de pesquisa em uma planilha Excel, ou utilizar o metodo `contains()` do java.
- Mas onde se encaixa o comando ILIKE que utilizamos em aula? O comando LIKE é sensível a maiúsculas e minúsculas em alguns bancos de dados. Portanto, se você quiser uma busca que não leve isso em consideração, como se fosse o IgnoreCase em Java, você deve utilizar o ILIKE.
- Chamando o metodo do repositorio na classe Principal:
```java
class Principal {
    ...
    private void buscarEpisodioPorTrecho() {
        System.out.println("Insira o trecho que preocura:");
        String trechoInserido = leitura.nextLine();
        List<Episodio> episodiosEncontrados = repositorio.episodiosPorTrecho(trechoInserido);
        episodiosEncontrados.forEach(e ->
                System.out.printf("Serie-> %s: Temporada %s - Episodio %s - %s\n" ,
                        e.getSerie().getTitulo(), e.getTemporada(), e.getNumeroEpisodio(),
                        e.getTitulo()));
    }
}
```
- Adicionando uma consulta para listar os melhores episodios de uma serie inserida:
```java
class Principal {


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
```

```java
import org.springframework.data.jpa.repository.JpaRepository;

interface SerieRepository extends JpaRepository<Serie,Long> {



    @Query("SELECT e from Serie s Join s.episodios e WHERE s = :serie ORDER BY e.avaliacao DESC LIMIT 5")
    List<Episodio> topEpisodiosPorSerie(Serie serie);
}
```
- Diferenciacao dos tipos de consulta da JPA. Vimmos que podemos trabalhar com derived queries, com queries nativas usando o `nativequery` e a JPQL, linguagem de busca da JPA.
- Criacao de metodos personalizdos e mais legiveis: Utilizar a JPQL pode auxiliar na escrita de metodos mais legiveis. Para isso, basta escrever o nome do metodo e anota-lo como `@Query`
- Aprofundamento em linguagem SQL. Expressoes utilizadas: LIKE,ORDER e LIMIT.
- Recuperacao de informacoes secundarias relacionadas a episodios a partir de serie, utilizando o recurso das *junções* (JOIN).
- O Java tem uma API de datas, o SQL tambem tem sua forma de lidar com datas atraves da funcao YEAR do SQL:
```java
interface SerieRepository {
    ...
    
    @Query("SELECT e FROM Serie s JOIN s.episodios e WHERE e.titulo ILIKE %:trechoEpisodio")
    List<Episodio> episodiosPorTrecho(String trechoEpisodio);

    @Query("SELECT e FROM Serie s JOIN s.episodios e WHERE s = :serie ORDER BY e.avaliacao DESC LIMIT 5")
    List<Episodio> topEpisodiosPorSerie(Serie serie);

    @Query("SELECT e FROM Serie s JOIN s.episodios e WHERE s = :serie AND YEAR(e.dataLancamento) >= :anoLancamento")
    List<Episodio> episodiosPorSerieEAno(Serie serie, int anoLancamento);
    
}
```



