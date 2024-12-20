# Notes
- Por se tratar de uma implementacao para um aplicacao web ha algumas alteracoes a serem consideradas:
- Adicao de uma dependencia do proprio Spring para aplicacao Web;
- A remocao da interface `CommandLineRunner` na aplicacao Spring, repositorio e o metodo `run()`:
```java
@SpringBootApplication
public class ScreenmatchApplication {
	public static void main(String[] args) {
		SpringApplication.run(ScreenmatchApplication.class, args);
	}
}
```
- Desta maneira, ao executar a aplicacao, o Tomcat sobe na porta 8080 esperando o mapeamento.
- O Tomcat utiliza a porta 8080 por padrao, por isso caso ela ja estiver em uso podemos alterar as configuracoes no `application.properties`:
```properties
server.port=8081
```
- Durante o curso, trabalharemos com uma API REST(Representational State Transfer, ou Transferência de Estado Representacional). Quando falamos desse conceito, estamos nos referindo a um conjunto de princípios de arquitetura que especificam como solicitações e respostas devem ser construídas em uma API.
- Os dados em uma API REST são geralmente enviados e recebidos no formato JSON (JavaScript Object Notation). No entanto, eles também podem ser enviados em outros formatos, como XML.
- A arquitetura REST sugere o uso de métodos HTTP padronizados para executar tarefas específicas, incluindo:
> *GET* para obter dados; <br>
> *POST* para enviar novos dados; <br>
> *PUT* para atualizar dados existentes; <br>
> *DELETE* para remover dados. <br>
- Tendo um padrão, a comunicação fica muito mais eficiente. Além disso, as APIs REST devem ser stateless, o que significa que as solicitações do cliente podem ser processadas independentemente umas das outras. Isso as torna altamente escaláveis, facilitando a vida dos desenvolvedores que trabalham em grandes projetos web, pois facilita o gerenciamento, a manutenção e o teste do software.

- Utilizacao do padrao MVC;
- O MVC é um conceito muito importante no mundo da programação. Esta é uma sigla que significa Model-View-Controller, ou Modelo-Visualização-Controlador, em português. Ele é usado para organizar o código de muitos tipos de software, facilitando o desenvolvimento e tornando o programa mais fácil de ser mantido e atualizado. Vamos entender melhor como isso tudo funciona.
### Por que o Modelo MVC e importante?
- Imagine que você está construindo uma casa. Se você simplesmente começar a construir sem um plano, pode acabar com cômodos estranhos ou estruturas mal posicionadas. Mas, se você definir bem os projetos antes de começar, sua casa será melhor organizada e será mais fácil de viver. O mesmo se aplica ao código de um programa, onde a casa é equivalente ao seu aplicativo e o plano é o modelo MVC.
- Ao seguir o modelo MVC, dividimos o código do nosso aplicativo em três partes:
> 1 - O Modelo, que é onde todos os dados e as regras de negócio são processados. <br>
> 2 - A Visualização, que é a interface de usuário, onde você vê os resultados das operações. <br>
> 3 - E o Controlador, que é como o cérebro que opera as outras duas partes, recebendo entradas do usuário e enviando comandos para o Modelo e a Visualização. <br>
- Para mapear a rota HTTP, precisamos de um controlador. Seguindo as boas praticas, devemos criar um pacote `controller`. Nele, iremos criar a classe `SerieController`. Ela deve ser anotada com `@RestController`, que permite que a classe seja gerenciada pelo Spring e que ele reconheca que ela e um controlador.
- Ainda no controller, criaremos o metodo `obterSeries()`, que deve ser anotado com `@GetMapping("/series)`, indicando para qual url nossa aplicacao sera mapeada.
### Como o Modelo MVC ajuda na organizacao do codigo e na manuntencao do aplicativo?
- Quando o código é bem organizado, é muito mais fácil identificar problemas, fazer atualizações e adicionar novas funcionalidades. Por exemplo, se um dia você decidir que quer mudar a cor de fundo do seu site, você só precisa ir na Visualização e fazer a mudança. Se decidir mudar as regras para adicionar um novo livro, vai direto para o Modelo.
- Essa organização também facilita muito a vida de qualquer outra pessoa que possa trabalhar no seu código no futuro. E se você decidir oferecer seu site em diferentes linguagens, por exemplo, você pode simplesmente adicionar uma nova Visualização, sem precisar mexer no Modelo ou no Controlador.
- Então, o Modelo MVC é um grande aliado na hora de desenvolver um aplicativo. Além de facilitar o entendimento e a manutenção do código, ajuda a prepará-lo para as mudanças e melhorias que sempre surgem ao longo do tempo.
### MVC em Java:
> Em Java, o padrão MVC era comumente aplicado utilizando as tecnologias JSP ou Thymeleaf. Com o JSP (Java Server Pages), é possível misturar códigos Java e HTML para criar páginas web dinâmicas. Já no Thymeleaf, os códigos não se misturam, mas temos páginas HTML dentro da pasta resources do projeto. Essas páginas se comunicam com o código Java e fazem com que possamos visualizar as informações no navegador. <br>
> Essas tecnologias faziam com que a View do MVC estivesse dentro da aplicação Java. Porém, atualmente o mais comum no mercado é ter uma separação entre front-end e back-end. No back-end, temos a Model e o Controller. O back-end fornece informações que serão utilizadas pelo front-end, que será responsável pela parte da View. <br>

## SerieController
```java
@RestController
public class SerieController {


    @GetMapping("/series")
    public String obterSeries(){
        return "aqui vao ser listadas as series";
    }
}
```
- Anotacao `@RestController`: Basicamente ele é o responsável por controlar as requisições indicando quem deve receber as requisições para quem deve responde-las. 
- Anotacao `@GetMapping("rota")`: Responsavel para sinalizar a rota ao metodo evidenciado.

## Utilizando uma classe gerenciada pelo Spring (SerieController)
- Nessa formacao, e possivel fazer com que a propria classe seja gerenciada pelo Spring, ao inves da formacao anterior na classe Principal. Agora podemos atribuir o nosso repository diretamente na classe SerieController:

```java
import br.com.alura.screenmatch.model.Serie;
import br.com.alura.screenmatch.repository.SerieRepository;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
public class SerieController {
    @Autowired
    private SerieRepository repositorio;
    
    @GetMapping("/series")
    public List<Serie> obterSeries() {
        return repositorio.findAll();
    }
}
```
- Apesar desta implementacao estar teoricamente correta, ha o retorno de diversas excecoes por conta de *referencia circular*, causada pelo relacionamento bi-direcional entre Serie e Episodio. Em resumo, quando o Spring tenta serializar os dados, o algoritmo cai em looping e retorna varios dados repetidos.

### Para saber mais: estrutura de pacotes em Java
- Durante as nossas aulas, estamos utilizando uma divisão de pacotes bem específica: para cada parte do projeto, ou camada, criamos um pacote diferente. Por isso trabalhamos com controller, model, repository e outros pacotes. Esse estilo de organização é chamado de Package by Layer, ou pacotes por camadas.
- Package by Layer é uma abordagem que diz que você deve dividir seu código com base em suas responsabilidades funcionais. Isso pode incluir coisas como 'model', 'view', 'controller', e 'repository'. Cada camada tem uma responsabilidade específica. Por exemplo, a camada 'view' manipula a interface do usuário, enquanto a camada 'controller' lidará com a lógica de negócio.
- Segue um exemplo:
```text
com.myblog
    .controller
        .PostController
        .CommentController
    .model
        .Post
        .Comment
    .repository
        .PostRepository
        .CommentRepository
```
- Neste exemplo, todas as classes relacionadas aos posts do blog estão espalhadas por diferentes pacotes, baseados na função que desempenham. O mesmo se aplica às classes de comentários.
- Porém, existe um outro tipo de organização, utilizado, por exemplo, na formação Spring Boot. Ele é chamado Package by Feature, ou pacotes por funcionalidades. Ele sugere que você deve organizar seu código com base nos recursos individuais do seu aplicativo. Em vez de dividir seu código com base em sua função, você divide com base no recurso que ele implementa
- Usando o mesmo exemplo do blog, com `Package by Feature`:
```text
com.myblog
    .post
        .Post
        .PostController
        .PostRepository
    .comment
        .Comment
        .CommentController
        .CommentRepository
```
- Neste exemplo, todas as classes relacionadas aos posts estão no mesmo pacote. O mesmo se aplica aos comentários. Cada pacote é autossuficiente e contém tudo o que precisa para implementar um recurso específico.
- Quando usar cada um?
> Então, qual abordagem você deve usar? Depende. 'Package by Layer' pode ser útil se você tiver uma equipe grande e complexa, na qual muitas pessoas podem estar trabalhando em diferentes camadas ao mesmo tempo. Ele separa as responsabilidades claramente, portanto, é menos provável que as pessoas pisem nos pés umas das outras. <br>
> No entanto, 'Package by Feature' é muitas vezes preferido para projetos menores e mais ágeis. Ele mantém todas as classes relacionadas a um recurso juntas, tornando mais fácil para um desenvolvedor entender completamente um recurso. Também é mais fácil de manter, porque quando um recurso é adicionado ou removido, você sabe exatamente onde todas as classes relacionadas estão. <br>
> Aqui, optamos por utilizar o Package by Layer, mas é interessante que você analise todas as condições para ver a estrutura que melhor se adequa a seu projeto. <br>

## DTO - Data Transfer Object ou Objeto de Transferencia de Dados
- **Desgin Pattern**: foca na transferencias de dados entre camadas de uma aplicacao.
- Criando o pacote `dto` e a record SerieDTO, visa-se somente devolver os atributos do objeto relacionado (Serie)
```java
public record SerieDTO( Long id,
                        String titulo,
                        Integer totalTemporadas,
                        Double avaliacao,
                        Categoria genero,
                        String atores,
                        String posterUrl,
                        String sinpose,
                        String premio,
                        LocalDate lancamento,
                        LocalDate duracao,
                        List<String> votacoes) {
}
```
- Com a record modelada, podemos dar continuidade ao retorno do metodo `obterSeries()` em SerieController. Como a record que sera atribuida para retornar os dados espera os atributos de Serie, podemos utilizar `stream()` para facilitar o instanciamento e o retorno das listas de objetos Serie:
```java
@RestController
public class SerieController {
    @Autowired
    private SerieRepository repositorio;

    @GetMapping("/series")
    public List<SerieDTO> obterSeries() {
        return repositorio.findAll()
                .stream()
                .map(s -> new SerieDTO(s.getId(), s.getTitulo(), s.getTotalTemporadas(), s.getAvaliacao(), s.getGenero(), s.getAtores(), s.getPosterUrl(), s.getSinpose(), s.getPremio(), s.getLancamento(), s.getDuracao(), s.getVotacoes()))
                .collect(Collectors.toList());
    }
}
```
- Agora desta maneira, a rota `/series` na porta 8080 estara retornando corretamente as series.
- Apesar de estar funcional, ao abrir a aplicacao pelo navegador nota-se erros de cabecalhos. Isso acontece devido a politica do CORS, pois no caso a aplicacao origem esta em uma porta diferente da aplicacao destino;
### Para saber mais: CORS
- O CORS (Cross-origin Resource Sharing) é um mecanismo usado para adicionar cabeçalhos HTTP que informam aos navegadores para permitir que uma aplicação Web seja executada em uma origem e acesse recursos de outra origem diferente. Esse tipo de ação é chamada de requisição cross-origin HTTP.
- É usado para habilitar solicitações entre sites para chamadas XMLHttpRequest ou FetchAPI (entre origens diferentes), web fonts (@font do CSS), texturas WebGL e frames de desenhos usando o drawImage().
- Referencias:
> https://www.alura.com.br/artigos/como-resolver-erro-de-cross-origin-resource-sharing <br>
> https://www.alura.com.br/artigos/desmistificando-o-protocolo-http-parte-1 <br>
> https://www.alura.com.br/artigos/diferencas-entre-get-e-post <br>
> https://www.alura.com.br/artigos/qual-e-diferenca-entre-http-e-https <br>
- config.CorsConfiguration: 
```java
@Configuration
public class CorsConfiguration implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://127.0.0.1:5500")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "TRACE", "CONNECT");
    }
}
```
### Configurando o Live-Reload
- O Spring é muito eficaz e já oferece uma função para nós, o DevTools, que vai nos auxiliar exatamente com isso. Para usar o DevTools no nosso projeto, podemos utilizar uma dependência, que vamos copiar e colar no código do arquivo pom.xml.
```xml
<dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-devtools</artifactId>
        <scope>runtime</scope>
        <optional>true</optional>
</dependency>
```
- Feito isso, vamos rodar o Maven novamente. Em seguida, precisaremos configurar também o IntelliJ, porque o projeto já está configurado para utilizar o DevTools, mas devemos ativar algumas permissões no IntelliJ.
- Então, vamos em "File > Settings… > Build, Execution, Deployment > Compiler". Marcaremos a opção "Build project automatically". Além dessa opção, precisaremos de mais uma. Primeiro, vamos aplicar a alteração anterior clicando em "Apply" no canto inferior direito.
- "Advanced Settings", último item do menu lateral esquerdo, vamos marcar a opção "Allow auto-make to start even if developed application is currently running", que é a segunda opção da lista. Feito isso, podemos aplicar e clicar em "OK".

### Dividindo as responsabilidades; diminuindo o acoplamento:
- Visando boas praticas e performance, observamos que o Spring trabalha com *injecao de dependencias*, ou seja, garante que o proprio Spring gerencie nossas classes e instancias no momento em que ha necessidade.
- Ao longo da formacao, foi criado o pacote `service`, que e justamente o pacote pra lidar com servicos. Classes que auxiliam as regras de negocio da nossa aplicacao. Entao com isso, podemos *desacoplar* o nosso repositorio da classe `SerieController`.
- Com isso, podemos delegar o metodo `obterSeries()` e transformacao de `stream` para outra classe diferente, especificamente de `services`. Diminuindo assim a responsabilidade do controlador:
```java
@Service
public class SerieService {
    @Autowired
    private SerieRepository repositorio;

    public List<SerieDTO> obterTodasAsSeries(){
        return repositorio.findAll()
                .stream()
                .map(s -> new SerieDTO(s.getId(),s.getTitulo(),s.getTotalTemporadas(),s.getAvaliacao(),s.getGenero(),s.getAtores(),s.getPosterUrl(),s.getSinpose(),s.getPremio(),s.getLancamento(),s.getDuracao(),s.getVotacoes()))
                .collect(Collectors.toList());
    }
}
```
- Enquanto na classe `SerieController`:
```java
@RestController
public class SerieController {
    @Autowired
    private SerieService servico;

    @GetMapping("/series")
    public List<SerieDTO> obterSeries(){
        return servico.obterTodasAsSeries();
    }

}
```
> "Baixo acoplamento, alta coesao". Premissa de Orientacao a Objetos. O que significa essa "alta coesao"? Significa que uma classe esta coesa, ou seja, uma responsabilidade bem definida, deixando-a mais enxuta possivel. <br>

### Para saber mais: anotacoes do SpringBoot
- O Spring Framework oferece uma ampla gama de anotações para desenvolvimento de aplicações web. Aqui estão algumas das anotações mais comuns e importantes usadas no Spring para aplicações web:
- `@Controller`: Usada para marcar uma classe como um controlador no padrão MVC (Model-View-Controller). Essa anotação é usada para receber requisições e manipular lógica de negócios.
- `@RestController`: Uma variação de @Controller, específica para APIs RESTful. Combina as anotações `@Controller` e `@ResponseBody`, indicando que cada método retorna um objeto serializado diretamente em JSON ou XML como resposta.
- `@RequestMapping`: Define mapeamentos entre URLs e métodos de controlador. Especifica as URLs para as quais um método do controlador deve responder e os métodos HTTP correspondentes (GET, POST, PUT, DELETE etc.).
- `@GetMapping`, `@PostMapping`, `@PutMapping`, `@DeleteMapping`: Atalhos para as operações HTTP GET, POST, PUT e DELETE, respectivamente, em métodos de controlador.
- `@RequestParam`: Usada para mapear os parâmetros de requisição HTTP para os parâmetros do método do controlador.
- `@PathVariable`: Usada para vincular variáveis de template de URL a parâmetros de métodos de controlador.
- `@RequestBody`: Utilizada para mapear o corpo da requisição HTTP para um objeto de entrada do método do controlador.
- `@ResponseBody`: Indica que o valor retornado pelo método do controlador deve ser usado diretamente como corpo da resposta HTTP.
- `@Valid` e `@Validated`: Utilizadas para ativar a validação de entrada no lado do servidor. Geralmente combinadas com anotações de validação, como `@NotNull`, `@Size`, `@Min`, `@Max`, entre outras.
- `@CrossOrigin`: Utilizada para configurar permissões de acesso a recursos de diferentes origens (CORS - Cross-Origin Resource Sharing).

### Algumas refatoracoes:
- Tendo como objetivo a escalabilidade e reducao de codigos repetidos, foi criado um metodo *privado* na classe `SerieService` em que ele faz a atribuicao dos dados de series do banco de dados para uma `SerieDTO`, e nos outros metodos da classe, chamamos o recem criado metodo `converteDados(List<SerieDTO>series)` e passamos no parametro o `repositorio.`:
```java
@Service
public class SerieService {
    @Autowired
    private SerieRepository repositorio;


    public List<SerieDTO> obterTodasAsSeries(){
        return converteDados(repositorio.findAll());
    }

    public List<SerieDTO> obterTop5Series() {
        return converteDados(repositorio.findTop5ByOrderByAvaliacaoDesc());

    }

    private List<SerieDTO> converteDados(List<Serie> series){
        return series
                .stream()
                .map(s -> new SerieDTO(s.getId(),s.getTitulo(),s.getTotalTemporadas(),s.getAvaliacao(),s.getGenero(),s.getAtores(),s.getPosterUrl(),s.getSinpose(),s.getPremio(),s.getLancamento(),s.getDuracao(),s.getVotacoes()))
                .collect(Collectors.toList());
    }
}
```
- Anotacao `@RequestMapping(rota)`: para configurar os endpoints a partir de uma rota ja definida, no caso `/series`, podemos usar essa anotacao. Assim configuramos os metodos sem repetir essa rota e acrescentando os endpoints desejados dependendo do metodo:
```java
@RestController
@RequestMapping("/series")
public class SerieController {
    @Autowired
    private SerieService servico;

    @GetMapping
    public List<SerieDTO> obterSeries(){
        return servico.obterTodasAsSeries();
    }

    @GetMapping("/top5")
    public List<SerieDTO> obterTop5Series(){
        return servico.obterTop5Series();
    }

}
```
- Como e possivel notar em `obterTop5Series()`, onde a anotacao `@GetMapping("/top5")` ja parte de `/series` para o endpoint desejado (`/top5`).
- Criacao do metodo para retornar as series mais recentes no banco de dados: 
- Usando a JPQL em `SerieRepository`
```java
interface SerieRepository{
    ...
    @Query("SELECT s FROM Serie s " +
            "JOIN s.episodios e " +
            "GROUP BY s " +
            "ORDER BY MAX(e.dataLancamento) DESC LIMIT 5")
    List<Serie> encontrarEpisodiosMaisRecentes();
}
```
- Adicionando o metodo em `SerieService`
```java
class SerieService{
    ...
    public List<SerieDTO> obterSeriesMaisRecentes(){
        return converteDados(repositorio.encontrarEpisodiosMaisRecentes());
    }
}
```
- Chamando a o metodo no controller
```java
class SerieController{
    ...
    @GetMapping("/lancamentos")
    public List<SerieDTO> obterLancamentos() {
//        return servico.obterLancamentos(); deprecated
        return servico.obterSeriesMaisRecentes();
    }
}
```
### Terminando os metodos:
```java
class SerieService {
    ...
    
    public SerieDTO obterPorId(Long id) {
        Optional <Serie> serie =repositorio.findById(id);
        if(serie.isPresent()) {
            Serie s = serie.get();
            return new SerieDTO(s.getId(),s.getTitulo(),s.getTotalTemporadas(),s.getAvaliacao(),s.getGenero(),s.getAtores(),s.getPosterUrl(),s.getSinpose(),s.getPremio(),s.getLancamento(),s.getDuracao(),s.getVotacoes());
        }else{
            return null;
        }
    }

    public List<EpisodioDTO> obterTodasTemporadas(Long id) {
        Optional <Serie> serie =repositorio.findById(id);
        if(serie.isPresent()) {
            Serie s = serie.get();
            return s.getEpisodios().stream()
                    .map(e-> new EpisodioDTO(e.getTemporada(),e.getNumeroEpisodio(),e.getTitulo()))
                    .collect(Collectors.toList());
        }else{
            return null;
        }
    }
}
```
```java
class SerieController {
    ...
    @GetMapping("/{id}")
    public SerieDTO obterPorId(@PathVariable Long id){
        return servico.obterPorId(id);
    }

    @GetMapping("/{id}/temporadas/todas")
    public List<EpisodioDTO> obterTodasTemporadas(@PathVariable Long id){
        return servico.obterTodasTemporadas(id);
    }

}
```
- Acima, o metodo `obterTodasTemproadas(id)` do controller e do DTO precisa ao inves de retornar a lista de series, um episdio, que sera vinculado a determinada temporada. Por isso houve a necessidade de criarmos uma record `EpisodioDTO`:
```java
record EpisodioDTO (Integer temporada, Integer numeroEpisodio, String titulo)  {
}
```
- Exibindo episodios por temporada: 



# Aula 1
- Como conectar o back-end ao front-end. Vimos que o front-end esperava buscar dados de uma url especifica, `localhost:8080`, que onde subimos o servidor TomCat.
- Configurar uma aplicacao web com Spring Boot. Conhecemos a dependencia *starter-web* do Spring, que baixa outras varias dependencias e configura automaticamente um servidor na porta 8080, ou em outra que configuremos nossa aplicacao.
- Organizar um projeto MVC. Aprendemos como estruturar o projeto em varias chamadas e como conecta-las.
- Retornar uma informacao do navegador. Criamos nosso primeiro controller e nossa primeira rota da API, utilizando as anotacoes `@RestController` e `@GetMapping()`

# Aula 2
- Devolver os dados do nosso banco para o navegador. Trabalhamos devolvendo os dados do nosso banco no Controller, devidamente serializados. 
- Tratar serialização circular. Vimos os problemas que ocorrem ao tentar serializar entidades mapeadas de forma bidirecional e como resolvê-los.
- Utilizar o padrão DTO. Para evitar a serialização circular e principalmente para seguir boas práticas, criamos nossos DTOs. Assim, nossos dados ficaram mais seguros e foram devolvidos de forma personalizada.
- Lidar com o erro de CORS. Conhecemos o erro entre a comunicação entre rotas de origens diferentes e pudemos tratá-lo, criando a classe CorsConfiguration.
- Configurar o Live Reload. Para que a aplicação não precise ser parada e reinicializada sempre que houver mudanças, usamos o Devtools e mudamos as configurações necessárias no Intellij.

# Aula 3
- Deixar o código mais limpo e organizado. Vimos que a única responsabilidade de um controlador é tratar da comunicação e das rotas da API. Assim, ele não deve conter regras de negócio. E para fazer essa divisão, criamos uma classe de serviços, a SerieService.
- Utilizar boas práticas de extração de métodos Aplicamos princípios da orientação a objetos, extraindo métodos que eram comuns no código, facilitando a manutenção.
- Criar uma url fixa para o Controller. Usamos o @RequestMapping para que todas as urls mapeadas pelo controlador de séries tenham como prefixo o “/series”.
- Retornar os dados de uma única série. Para buscar uma série, precisamos que seu id seja passado como parâmetro. Conhecemos o @PathVariable, que nos auxilia nesse objetivo.