package com.codeoftheweb.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.*;

@SpringBootApplication
public class SalvoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SalvoApplication.class, args);
	}

	@Bean
	public PasswordEncoder passwordEncoder(){
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}

	@Bean
	public CommandLineRunner initData(PlayerRepository playerRepository,
																		GameRepository gameRepository,
																		GamePlayerRepository gameplayerRepository,
																		ShipRepository shipRepository,
																		SalvoRepository salvoRepository,
																		ScoreRepository scoreRepository) {

		return (args) -> {

			// New Players
			Player Jack_Bauer = new Player("j.bauer@ctu.gov", passwordEncoder().encode("24"));
			Player Chloe_Obrian = new Player("c.obrian@ctu.gov", passwordEncoder().encode("42"));
			Player Kim_Bauer = new Player("kim_bauer@gmail.com",  passwordEncoder().encode("kb"));
			Player Tony_Almeida = new Player("t.almeida@ctu.gov", passwordEncoder().encode("mole"));
			playerRepository.save(Jack_Bauer);
			playerRepository.save(Chloe_Obrian);
			playerRepository.save(Kim_Bauer);
			playerRepository.save(Tony_Almeida);

			// New Games
			Game game1 = new Game(1);
			Game game2 = new Game(2);
			Game game3 = new Game(3);
			Game game4 = new Game(4);
			Game game5 = new Game(5);
			Game game6 = new Game(6);
			Game game7 = new Game(7);
			Game game8 = new Game(8);
			gameRepository.save(game1);
			gameRepository.save(game2);
			gameRepository.save(game3);
			gameRepository.save(game4);
			gameRepository.save(game5);
			gameRepository.save(game6);
			gameRepository.save(game7);
			gameRepository.save(game8);

			// New GamePlayers
			gameplayerRepository.save(new GamePlayer(game1, Jack_Bauer));
			gameplayerRepository.save(new GamePlayer(game1, Chloe_Obrian));
			gameplayerRepository.save(new GamePlayer(game2, Jack_Bauer));
			gameplayerRepository.save(new GamePlayer(game2, Chloe_Obrian));
			gameplayerRepository.save(new GamePlayer(game3, Chloe_Obrian));
			gameplayerRepository.save(new GamePlayer(game3, Tony_Almeida));
			gameplayerRepository.save(new GamePlayer(game4, Chloe_Obrian));
			gameplayerRepository.save(new GamePlayer(game4, Jack_Bauer));
			gameplayerRepository.save(new GamePlayer(game5, Tony_Almeida));
			gameplayerRepository.save(new GamePlayer(game5, Jack_Bauer));
			gameplayerRepository.save(new GamePlayer(game6, Kim_Bauer));
			gameplayerRepository.save(new GamePlayer(game7, Tony_Almeida));
			gameplayerRepository.save(new GamePlayer(game8, Kim_Bauer));
			gameplayerRepository.save(new GamePlayer(game8, Tony_Almeida));

			//New Ships		
			//Carrier 5 - Battleship 4 - Submarine 3 - Destroyer 3 - Patrolboat 2
			shipRepository.save(new Ship("carrier", Arrays.asList("H5", "H6", "H7", "H8", "H9"), gameplayerRepository.findByGameAndPlayer(game1, Jack_Bauer)));
			shipRepository.save(new Ship("destroyer", Arrays.asList("A9", "B9", "C9"), gameplayerRepository.findByGameAndPlayer(game1, Jack_Bauer)));
			shipRepository.save(new Ship("battleship", Arrays.asList("E4", "E5", "E6", "E7"), gameplayerRepository.findByGameAndPlayer(game1, Jack_Bauer)));
			shipRepository.save(new Ship("submarine", Arrays.asList("E1", "F1", "G1"), gameplayerRepository.findByGameAndPlayer(game1, Jack_Bauer)));
			shipRepository.save(new Ship("patrolboat", Arrays.asList("B4", "B5"), gameplayerRepository.findByGameAndPlayer(game1, Jack_Bauer)));
			shipRepository.save(new Ship("carrier", Arrays.asList("A5", "A6", "A7", "A8", "A9"), gameplayerRepository.findByGameAndPlayer(game1, Chloe_Obrian)));
			shipRepository.save(new Ship("destroyer", Arrays.asList("C9", "D9", "E9"), gameplayerRepository.findByGameAndPlayer(game1, Chloe_Obrian)));
			shipRepository.save(new Ship("battleship", Arrays.asList("E4", "E5", "E6", "E7"), gameplayerRepository.findByGameAndPlayer(game1, Chloe_Obrian)));
			shipRepository.save(new Ship("submarine", Arrays.asList("H8", "H9", "H10"), gameplayerRepository.findByGameAndPlayer(game1, Chloe_Obrian)));
			shipRepository.save(new Ship("patrolboat", Arrays.asList("J1", "J2"), gameplayerRepository.findByGameAndPlayer(game1, Chloe_Obrian)));
			shipRepository.save(new Ship("carrier", Arrays.asList("C5", "C6", "C7", "C8", "C9"), gameplayerRepository.findByGameAndPlayer(game2, Jack_Bauer)));
			shipRepository.save(new Ship("destroyer", Arrays.asList("F9", "G9", "H9"), gameplayerRepository.findByGameAndPlayer(game2, Jack_Bauer)));
			shipRepository.save(new Ship("battleship", Arrays.asList("E1", "E2", "E3", "E4"), gameplayerRepository.findByGameAndPlayer(game2, Jack_Bauer)));
			shipRepository.save(new Ship("submarine", Arrays.asList("H1", "H2", "H3"), gameplayerRepository.findByGameAndPlayer(game2, Jack_Bauer)));
			shipRepository.save(new Ship("patrolboat", Arrays.asList("J7", "J8"), gameplayerRepository.findByGameAndPlayer(game2, Jack_Bauer)));
			shipRepository.save(new Ship("carrier", Arrays.asList("D9", "E9", "F9", "G9", "H9"), gameplayerRepository.findByGameAndPlayer(game2, Chloe_Obrian)));
			shipRepository.save(new Ship("destroyer", Arrays.asList("G1", "G2", "G3"), gameplayerRepository.findByGameAndPlayer(game2, Chloe_Obrian)));
			shipRepository.save(new Ship("battleship", Arrays.asList("F4", "F5", "F6", "F7"), gameplayerRepository.findByGameAndPlayer(game2, Chloe_Obrian)));
			shipRepository.save(new Ship("submarine", Arrays.asList("B1", "B2", "B3"), gameplayerRepository.findByGameAndPlayer(game2, Chloe_Obrian)));
			shipRepository.save(new Ship("patrolboat", Arrays.asList("I1", "I2"), gameplayerRepository.findByGameAndPlayer(game2, Chloe_Obrian)));
			shipRepository.save(new Ship("carrier", Arrays.asList("C5", "C6", "C7", "C8", "C9"), gameplayerRepository.findByGameAndPlayer(game3, Chloe_Obrian)));
			shipRepository.save(new Ship("destroyer", Arrays.asList("E9", "F9", "G9"), gameplayerRepository.findByGameAndPlayer(game3, Chloe_Obrian)));
			shipRepository.save(new Ship("battleship", Arrays.asList("E4", "E5", "E6", "E7"), gameplayerRepository.findByGameAndPlayer(game3, Chloe_Obrian)));
			shipRepository.save(new Ship("submarine", Arrays.asList("A1", "A2", "A3"), gameplayerRepository.findByGameAndPlayer(game3, Chloe_Obrian)));
			shipRepository.save(new Ship("patrolboat", Arrays.asList("J5", "J6"), gameplayerRepository.findByGameAndPlayer(game3, Chloe_Obrian)));
			shipRepository.save(new Ship("carrier", Arrays.asList("E4", "E5", "E6", "E7", "E8"), gameplayerRepository.findByGameAndPlayer(game3, Tony_Almeida)));
			shipRepository.save(new Ship("destroyer", Arrays.asList("A1", "A2", "A3"), gameplayerRepository.findByGameAndPlayer(game3, Tony_Almeida)));
			shipRepository.save(new Ship("battleship", Arrays.asList("H4", "H5", "H6", "H7"), gameplayerRepository.findByGameAndPlayer(game3, Tony_Almeida)));
			shipRepository.save(new Ship("submarine", Arrays.asList("B8", "B9", "B10"), gameplayerRepository.findByGameAndPlayer(game3, Tony_Almeida)));
			shipRepository.save(new Ship("patrolboat", Arrays.asList("J5", "J6"), gameplayerRepository.findByGameAndPlayer(game3, Tony_Almeida)));
			shipRepository.save(new Ship("carrier", Arrays.asList("E1", "F1", "G1", "H1", "I1"), gameplayerRepository.findByGameAndPlayer(game4, Chloe_Obrian)));
			shipRepository.save(new Ship("destroyer", Arrays.asList("E6", "F6", "G6"), gameplayerRepository.findByGameAndPlayer(game4, Chloe_Obrian)));
			shipRepository.save(new Ship("battleship", Arrays.asList("C4", "C5", "C6", "C7"), gameplayerRepository.findByGameAndPlayer(game4, Chloe_Obrian)));
			shipRepository.save(new Ship("submarine", Arrays.asList("B1", "B2", "B3"), gameplayerRepository.findByGameAndPlayer(game4, Chloe_Obrian)));
			shipRepository.save(new Ship("patrolboat", Arrays.asList("J9", "J10"), gameplayerRepository.findByGameAndPlayer(game4, Chloe_Obrian)));
			shipRepository.save(new Ship("carrier", Arrays.asList("A1", "A2", "A3", "A4", "A5"), gameplayerRepository.findByGameAndPlayer(game4, Jack_Bauer)));
			shipRepository.save(new Ship("destroyer", Arrays.asList("D9", "E9", "F9"), gameplayerRepository.findByGameAndPlayer(game4, Jack_Bauer)));
			shipRepository.save(new Ship("battleship", Arrays.asList("E4", "E5", "E6", "E7"), gameplayerRepository.findByGameAndPlayer(game4, Jack_Bauer)));
			shipRepository.save(new Ship("submarine", Arrays.asList("H1", "H2", "H3"), gameplayerRepository.findByGameAndPlayer(game4, Jack_Bauer)));
			shipRepository.save(new Ship("patrolboat", Arrays.asList("J7", "J8"), gameplayerRepository.findByGameAndPlayer(game4, Jack_Bauer)));
			shipRepository.save(new Ship("carrier", Arrays.asList("B1", "B2", "B3", "B4", "B5"), gameplayerRepository.findByGameAndPlayer(game5, Tony_Almeida)));
			shipRepository.save(new Ship("destroyer", Arrays.asList("C9", "D9", "E9"), gameplayerRepository.findByGameAndPlayer(game5, Tony_Almeida)));
			shipRepository.save(new Ship("battleship", Arrays.asList("F4", "F5", "F6", "F7"), gameplayerRepository.findByGameAndPlayer(game5, Tony_Almeida)));
			shipRepository.save(new Ship("submarine", Arrays.asList("H3", "H4", "H5"), gameplayerRepository.findByGameAndPlayer(game5, Tony_Almeida)));
			shipRepository.save(new Ship("patrolboat", Arrays.asList("I9", "I10"), gameplayerRepository.findByGameAndPlayer(game5, Tony_Almeida)));
			shipRepository.save(new Ship("carrier", Arrays.asList("A6", "A7", "A8", "A9", "A10"), gameplayerRepository.findByGameAndPlayer(game5, Jack_Bauer)));
			shipRepository.save(new Ship("destroyer", Arrays.asList("F9", "G9", "H9"), gameplayerRepository.findByGameAndPlayer(game5, Jack_Bauer)));
			shipRepository.save(new Ship("battleship", Arrays.asList("E1", "E2", "E3", "E4"), gameplayerRepository.findByGameAndPlayer(game5, Jack_Bauer)));
			shipRepository.save(new Ship("submarine", Arrays.asList("J8", "J9", "J10"), gameplayerRepository.findByGameAndPlayer(game5, Jack_Bauer)));
			shipRepository.save(new Ship("patrolboat", Arrays.asList("B1", "B2"), gameplayerRepository.findByGameAndPlayer(game5, Jack_Bauer)));

			// New Salvos
			salvoRepository.save(new Salvo(1, Arrays.asList("B5", "C5", "A1", "B2", "C3"), gameplayerRepository.findByGameAndPlayer(game1, Jack_Bauer)));
			salvoRepository.save(new Salvo(1, Arrays.asList("B4", "B5", "B6", "B7", "B8"), gameplayerRepository.findByGameAndPlayer(game1, Chloe_Obrian)));
			salvoRepository.save(new Salvo(2, Arrays.asList("F2", "D5", "F3", "A5", "A6"), gameplayerRepository.findByGameAndPlayer(game1, Jack_Bauer)));
			salvoRepository.save(new Salvo(2, Arrays.asList("E1", "H3", "A3", "A4", "A5"), gameplayerRepository.findByGameAndPlayer(game1, Chloe_Obrian)));
			salvoRepository.save(new Salvo(1, Arrays.asList("A2", "A4", "G6", "G7", "G8"), gameplayerRepository.findByGameAndPlayer(game2, Jack_Bauer)));			
			salvoRepository.save(new Salvo(1, Arrays.asList("B5", "D5", "C7", "C8", "C9"), gameplayerRepository.findByGameAndPlayer(game2, Chloe_Obrian)));			
			salvoRepository.save(new Salvo(2, Arrays.asList("H3", "H6", "H7", "H8", "H9"), gameplayerRepository.findByGameAndPlayer(game2, Jack_Bauer)));
			salvoRepository.save(new Salvo(2, Arrays.asList("C5", "C6", "F4", "A2", "A3"), gameplayerRepository.findByGameAndPlayer(game2, Chloe_Obrian)));
			salvoRepository.save(new Salvo(1, Arrays.asList("G6", "H6", "A4", "A5", "A6"), gameplayerRepository.findByGameAndPlayer(game3, Chloe_Obrian)));			
			salvoRepository.save(new Salvo(1, Arrays.asList("H1", "H2", "H3", "H4", "H5"), gameplayerRepository.findByGameAndPlayer(game3, Tony_Almeida)));			
			salvoRepository.save(new Salvo(2, Arrays.asList("A2", "A3", "D8", "D9", "D10"), gameplayerRepository.findByGameAndPlayer(game3, Chloe_Obrian)));
			salvoRepository.save(new Salvo(2, Arrays.asList("E1", "F2", "G3", "E2", "E3"), gameplayerRepository.findByGameAndPlayer(game3, Tony_Almeida)));			
			salvoRepository.save(new Salvo(1, Arrays.asList("A3", "A4", "F7", "A5", "A6"), gameplayerRepository.findByGameAndPlayer(game4, Chloe_Obrian)));			
			salvoRepository.save(new Salvo(1, Arrays.asList("B5", "C6", "H1", "C1", "C3"), gameplayerRepository.findByGameAndPlayer(game4, Jack_Bauer)));			
			salvoRepository.save(new Salvo(2, Arrays.asList("A2", "G6", "H6" ,"B5" ,"B6"), gameplayerRepository.findByGameAndPlayer(game4, Chloe_Obrian)));			
			salvoRepository.save(new Salvo(2, Arrays.asList("C5", "C7", "D5", "A1", "A2"), gameplayerRepository.findByGameAndPlayer(game4, Jack_Bauer)));
			salvoRepository.save(new Salvo(1, Arrays.asList("A1", "A2", "A3" ,"A4" ,"A5"), gameplayerRepository.findByGameAndPlayer(game5, Tony_Almeida)));
			salvoRepository.save(new Salvo(1, Arrays.asList("B5", "B6", "C7", "A1", "A4"), gameplayerRepository.findByGameAndPlayer(game5, Jack_Bauer)));		
			salvoRepository.save(new Salvo(2, Arrays.asList("G6", "G7", "G8", "G9", "G10"), gameplayerRepository.findByGameAndPlayer(game5, Tony_Almeida)));			
			salvoRepository.save(new Salvo(2, Arrays.asList("C6", "D6", "E6", "F6", "A6"), gameplayerRepository.findByGameAndPlayer(game5, Jack_Bauer)));			

			//Scores
			scoreRepository.save(new Score(game1, Jack_Bauer, 1.0));
			scoreRepository.save(new Score(game1, Chloe_Obrian, 0.0));
			scoreRepository.save(new Score(game2, Jack_Bauer, 0.5));
			scoreRepository.save(new Score(game2, Chloe_Obrian, 0.5));
			scoreRepository.save(new Score(game3, Chloe_Obrian, 1.0));
			scoreRepository.save(new Score(game3, Tony_Almeida, 0.0));
			scoreRepository.save(new Score(game4, Chloe_Obrian, 0.5));
			scoreRepository.save(new Score(game4, Tony_Almeida, 0.5));
			scoreRepository.save(new Score(game5, Jack_Bauer, 0.0));
			scoreRepository.save(new Score(game5, Tony_Almeida, 1.0));

		};
	}
}

@Configuration
class WebSecurityAuthentication extends GlobalAuthenticationConfigurerAdapter {

	@Autowired
	PlayerRepository playerRepository;
	public void init(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(inputName-> {
			Player player = playerRepository.findByUserName(inputName);
			if (player != null) {
				return new User(player.getUserName(),player.getPassWord(),
												AuthorityUtils.createAuthorityList("USER"));
			} else {
				throw new UsernameNotFoundException("Unknown user: " + inputName);
			}
		});
	}
}

@Configuration
@EnableWebSecurity
class WebSecurityAuthorization extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
			.antMatchers("/rest/*").denyAll()
			.antMatchers("/web/games.html").permitAll()
			.antMatchers("/web/css").permitAll()
			.antMatchers("/web/images").permitAll()
			.antMatchers("/web/scripts").permitAll()	
			.antMatchers("/api/login").permitAll()
			.antMatchers("/api/games").permitAll()
			.antMatchers("/api/scoreboard").permitAll()
			.antMatchers("/api/players").permitAll()
			.antMatchers("/api/**", "/web/game.html**").hasAuthority("USER");

		http.formLogin()
			.usernameParameter("username")
			.passwordParameter("password")
			.loginPage("/api/login");

		http.logout().logoutUrl("/api/logout");

		// turn off checking for CSRF tokens
		http.csrf().disable();

		// if user is not authenticated, just send an authentication failure response
		http.exceptionHandling().authenticationEntryPoint((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

		// if login is successful, just clear the flags asking for authentication
		http.formLogin().successHandler((req, res, auth) -> clearAuthenticationAttributes(req));

		// if login fails, just send an authentication failure response
		http.formLogin().failureHandler((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

		// if logout is successful, just send a success response
		http.logout().logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler());
	}

	private void clearAuthenticationAttributes(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session != null) {
			session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
		}
	}
}