# Resolucao do desafio proposto
- Montando o esboco da classe principal `App`:
```java
public class App {
    private Scanner leitura = new Scanner(System.in);

    public void exibirMenu() {
        var opcao = -1;
        while (opcao != 9) {
            var menu = """
                    *** Screen Sound Músicas ***                    
                    
                    1- Cadastrar artistas
                    2- Cadastrar músicas
                    3- Listar músicas
                    4- Buscar músicas por artistas
                    5- Pesquisar dados sobre um artista
                    
                    9 - Sair
                    """;
            System.out.println(menu);
            opcao = leitura.nextInt();
            leitura.nextLine();
            switch (opcao) {
                case 1:
                    cadastrarArtistas();
                    break;
                case 2:
                    cadastrarMusicas();
                    break;
                case 3:
                    listarMusicas();
                    break;
                case 4:
                    buscarMusicasPorArtista();
                    break;
                case 5:
                    pesquisarDadosDoArtista();
                    break;
                case 9:
                    System.out.println("Encerrando a aplicação!");
                    break;
                default:
                    System.out.println("Opção inválida!");
            }
        }

    }

    private void pesquisarDadosDoArtista() {
    }

    private void buscarMusicasPorArtista() {
    }

    private void listarMusicas() {
    }

    private void cadastrarMusicas() {
    }

    private void cadastrarArtistas() {
    }
}
```
- Implementacao da interface `CommandLineRunner` e rescrita do metodo `run()` na classe da aplicacao Spring:
```java
@SpringBootApplication
public class ScreensoundApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(ScreensoundApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		App princpial = new App();
		princpial.exibirMenu();
	}
}
```
- Por ultimo, configurando as propriedes do JPA,Hibernate:
```properties
spring.datasource.url=jdbc:postgresql://localhost/alura_sounds 
spring.datasource.username=postgres
spring.datasource.password=
spring.datasource.driver-class-name=org.postgresql.Driver
hibernate.dialect=org.hibernate.dialect.HSQLDialect
```
- Mapeando as classes Artista e Musica para que podemos realizar o relacionamento entre eles no banco de dados;
```java
@Entity
@Table(name = "musicas")
public class Musica {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String titulo;
    @ManyToOne
    private Artista artista;

}
```
```java
@Entity
@Table(name = "artistas")
public class Artista {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String nome;
    @Enumerated(EnumType.STRING)
    private TipoArtista tipo;
    @OneToMany(mappedBy = "artista")
    private List<Musica> musicas = new ArrayList<>();

}
```
- Adicao da seguinte propriedade para a o Hibernate conseguir gerar as tabelas no PostgreSQL:
```properties
spring.jpa.hibernate.ddl-auto=update
```
- Criacao do Repositorio `ArtistaRepository` para realizar as Queries devidas:
```java
public interface ArtistaRepository extends JpaRepository<Artista, Long> {
    Optional<Artista> findByNomeContainingIgnoreCase(String nomeInserido);

    @Query("SELECT m FROM Artista a JOIN a.musicas m WHERE a.nome ILIKE %:nome%")
    List<Musica> buscaMusicasPorArtista(String nome);
}
```
- No metodo `buscaMusicasPorArtista(nome)` foi usado JPQL para as Queries no banco de dados.
- Corrigindo o *cascade* no mapemanto de artista para musica:
```java
class Artista {
    ...
    @OneToMany(mappedBy = "artista", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Musica> musicas = new ArrayList<>();
    ...
}
```
- Desta forma a querie consegue salvar os dados relacionados no banco de dados.
- Atibuindo o Repositorio na aplicacao Spring e passando-a como parametro para o construtor de App:
```java
@SpringBootApplication
public class ScreensoundApplication implements CommandLineRunner {
	@Autowired
	private ArtistaRepository repositorio;
    ...
    @Override
    public void run(String... args) throws Exception {
        App princpial = new App(repositorio);
        princpial.exibirMenu();
    }
```
> Corrigindo problema de recursao chamando o .toString() de Musica: <br>
```java
class Musica {
    ...
    @Override
    public String toString() {
        return "Musica:" +
                ", titulo='" + titulo + '\'' +
                ", artista=" + artista.getNome(); //correcao de recursao!
    }
}
```
- Agora com o repositorio pronto prosseguimos com os metodos:
```java
class App {
    private final ArtistaRepository repositorio;
    ...
    public App(ArtistaRepository repositorio) {
        this.repositorio = repositorio;
    }

    private void buscarMusicasPorArtista() {
        System.out.println("De qual Artista deseja buscar as musicas?");
        String nomeArtista = leitura.nextLine();
        List<Musica>musicas = repositorio.buscaMusicasPorArtista(nomeArtista);
        musicas.forEach(System.out::println);
    }

    private void listarMusicas() {
        List<Artista> artistas = repositorio.findAll();
        artistas.forEach(a -> a.getMusicas().forEach(System.out::println));
    }
    private void cadastrarMusicas() {
        System.out.println("Cadastar musicas de que Artista?");
        var nomeInserido = leitura.nextLine();
        Optional<Artista>artista =repositorio.findByNomeContainingIgnoreCase(nomeInserido);
        if(artista.isPresent()) {
            System.out.println("Informe o titulo da musica");
            var nomemusicaInserida = leitura.nextLine();
            Musica musica = new Musica(nomemusicaInserida);
            System.out.println("Salvando artista...");
            musica.setArtista(artista.get());
            System.out.println("Salvando musica...");
            artista.get().getMusicas().add(musica);
            repositorio.save(artista.get());
            System.out.println("Musica salva!");
        }else {
            System.out.println("Artista nao encontrado.");
        }
    }
    private void cadastrarArtistas() {
        String cadastrarNovo = "S";
        while (cadastrarNovo.equalsIgnoreCase("s")) {
            System.out.println("Informe o nome do artista:");
            String nomeInserido = leitura.nextLine();
            System.out.println("Informe o tipo desse artista (solo,dupla ou banda)");
            var tipo = leitura.nextLine();
            TipoArtista tipoArtista = TipoArtista.valueOf(tipo.toUpperCase()); //conversao de string para enum(TipoArtista)
            Artista artista = new Artista(nomeInserido,tipoArtista);
            repositorio.save(artista);
            System.out.println("Deseja cadastar outro artista? (S/N)");
            cadastrarNovo=leitura.nextLine();
        }
    }
}
```
