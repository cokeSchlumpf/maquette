package maquette;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.theme.Theme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Theme("maquette")
@SpringBootApplication
public class MaquetteApplication implements AppShellConfigurator {

	public static void main(String[] args) {
		SpringApplication.run(MaquetteApplication.class, args);
	}

}
