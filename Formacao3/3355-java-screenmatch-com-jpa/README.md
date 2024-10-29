# Anotacoes
- Por se tratar de uma implementacao para um aplicacao web ha algumas alteracoes a serem consideradas:
- Adicao de uma dependencia do proprio Spring para aplicacao Web;
- A remocao da interface `CommandLineRunner` na aplicacao Spring e 
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