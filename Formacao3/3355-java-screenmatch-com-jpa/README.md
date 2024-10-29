# Anotacoes
- Por se tratar de uma implementacao para um aplicacao web ha algumas alteracoes a serem consideradas:
- Adicao de uma dependencia do proprio Spring para aplicacao Web;
- A remocao da interface `CommandLineRunner` na aplicacao Spring:
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
### Como o Modeelo MVC ajuda na organizacao do codigo e na manuntencao do aplicativo?
- Quando o código é bem organizado, é muito mais fácil identificar problemas, fazer atualizações e adicionar novas funcionalidades. Por exemplo, se um dia você decidir que quer mudar a cor de fundo do seu site, você só precisa ir na Visualização e fazer a mudança. Se decidir mudar as regras para adicionar um novo livro, vai direto para o Modelo.
- Essa organização também facilita muito a vida de qualquer outra pessoa que possa trabalhar no seu código no futuro. E se você decidir oferecer seu site em diferentes linguagens, por exemplo, você pode simplesmente adicionar uma nova Visualização, sem precisar mexer no Modelo ou no Controlador.
- Então, o Modelo MVC é um grande aliado na hora de desenvolver um aplicativo. Além de facilitar o entendimento e a manutenção do código, ajuda a prepará-lo para as mudanças e melhorias que sempre surgem ao longo do tempo.
### MVC em Java:
> Em Java, o padrão MVC era comumente aplicado utilizando as tecnologias JSP ou Thymeleaf. Com o JSP (Java Server Pages), é possível misturar códigos Java e HTML para criar páginas web dinâmicas. Já no Thymeleaf, os códigos não se misturam, mas temos páginas HTML dentro da pasta resources do projeto. Essas páginas se comunicam com o código Java e fazem com que possamos visualizar as informações no navegador. <br>
> Essas tecnologias faziam com que a View do MVC estivesse dentro da aplicação Java. Porém, atualmente o mais comum no mercado é ter uma separação entre front-end e back-end. No back-end, temos a Model e o Controller. O back-end fornece informações que serão utilizadas pelo front-end, que será responsável pela parte da View. <br>
