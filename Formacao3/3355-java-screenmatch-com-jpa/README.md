# Alteracoes:
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