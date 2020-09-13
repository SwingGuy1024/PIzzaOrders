package com.disney.miguelmunoz.challenge;

import com.disney.miguelmunoz.challenge.exception.ResponseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@ComponentScan(basePackages = {"io.swagger", "io.swagger.api", "com.disney.miguelmunoz.challenge"})
@SpringBootApplication
public class Application implements CommandLineRunner {
	private static final Logger log = LoggerFactory.getLogger(Application.class);

	public static void main(String[] args) {
		Thread.UncaughtExceptionHandler handler = (t, e) -> {
					if (e instanceof ResponseException) {
						log.warn("Thread {}: {}", t, e.getMessage(), e);
					} else {
						log.error("Thread {}: {}", t, e.getMessage(), e);
					}
				};
		Thread.setDefaultUncaughtExceptionHandler(handler);
		//noinspection resource
		new SpringApplication(Application.class).run(args);
//		SpringApplication.run(Application.class, args);
	}

	@Override
	public void run(String... arg0) {
		if ((arg0.length > 0) && "exitcode".equals(arg0[0])) {
			throw new ExitException();
		}
	}


	static class ExitException extends RuntimeException implements ExitCodeGenerator {
		private static final long serialVersionUID = 1L;

		@Override
		public int getExitCode() {
			return 10;
		}

	}
}
