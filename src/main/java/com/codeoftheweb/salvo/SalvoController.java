package com.codeoftheweb.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

@RestController
@RequestMapping("/api")
public class SalvoController {

	@Autowired
	private GameRepository gameRepository;

	@Autowired
	private PlayerRepository playerRepository;

	@Autowired
	private GamePlayerRepository gameplayerRepository;

	@Autowired
	private ScoreRepository scoreRepository;

	@Autowired
	private ShipRepository shipRepository;

	@Autowired
	private SalvoRepository salvoRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	// @GetMapping("/games")
	@RequestMapping(path = "/games", method = RequestMethod.GET)
	public Map<String, Object> getGames(Authentication auth) {
		Map<String, Object> dto = new LinkedHashMap<>();
		if (auth != null) {
			dto.put("player", getCurrentUser(auth).makePlayerDTO());
		}
		else {
			dto.put("player","No player logged in");
		}
		dto.put("games", gameRepository
						.findAll()
						.stream()
						.map(Game::makeGameDTO)
						.collect(toList()));
		return dto;
	}

	// @PostMapping("/games")
	@RequestMapping(path = "/games", method = RequestMethod.POST)
	public ResponseEntity<Map<String,Object>> createGame(Authentication auth) {
		if (auth != null){
			Date date = new Date();
			Game game = new Game(date, false);
			gameRepository.save(game);
			Player player = playerRepository.findByUserName(auth.getName());
			GamePlayer gamePlayer = new GamePlayer(player, game, date);
			gameplayerRepository.save(gamePlayer);
			return new ResponseEntity<>(makeMap("gpid", gamePlayer.getId()), HttpStatus.CREATED);
		}
		else {
			return new ResponseEntity<>(makeMap("gpid", "No player logged in"), HttpStatus.UNAUTHORIZED);
		}
	}

	// @PostMapping("/players")
	@RequestMapping(path = "/players", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> createPlayer(@RequestParam String username, @RequestParam String password, Authentication auth){
		Map<String, Object> map = new LinkedHashMap<>();
		HttpStatus status;

		if(!isGuest(auth)){ 
			map.put("error", "User logged in ");
			status = HttpStatus.CONFLICT;
		}
		else if (username.isEmpty()){
			map.put("error", "No name");
			status = HttpStatus.EXPECTATION_FAILED;
		}
		else if (playerRepository.findByUserName(username) != null){ 
			map.put("error", "Name in use");
			status = HttpStatus.FORBIDDEN;
		}
		else { 
			Player player = playerRepository.save(new Player(username, passwordEncoder.encode(password)));
			map.put("username", player.getUserName());
			status = HttpStatus.CREATED;
		}
		return new ResponseEntity<>(map, status);
	}

	// @GetMapping("/game_view/{gamePlayerId}")
	@RequestMapping(path = "/game_view/{gamePlayerId}", method = RequestMethod.GET)
	public ResponseEntity getGamePlayerData(@PathVariable Long gamePlayerId, Authentication authentication) {
		GamePlayer user = gameplayerRepository.getOne(gamePlayerId);
		GamePlayer opponent = getOpponent(user);
		if (user.getPlayer().getId() == getCurrentUser(authentication).getId()) {
			Map<String, Object> gameView = new LinkedHashMap<>();
			gameView.put("game", user.getGame().makeGameDTO());
			gameView.put("gamestate", getGameState(user.getGame()));
			gameView.put("ships", user.getShips()
									 .stream()
									 .map(thisShip -> thisShip.makeShipDTO()) 
									 .collect(toList()));
			if (user.getGame().getGamePlayers().size() == 2) {		
				gameView.put("your_salvoes", getUserSalvos(user));
				gameView.put("your_hits", getHits(user));   
				gameView.put("your_sinks", getSunkShips(user)
										 .stream()
										 .map(Ship::getShipType)
										 .collect(toList())); 
			}
			else {
				gameView.put("your_salvoes", null);
				gameView.put("your_hits", null);
				gameView.put("your_sinks", null);
			}
			if (user.getGame().getGamePlayers().size() == 2) {
				gameView.put("opp_salvoes", getOppSalvos(user));
				gameView.put("opp_hits", getHits(opponent));
				gameView.put("opp_sinks", getSunkShips(opponent)
										 .stream()
										 .map(Ship::getShipType)
										 .collect(toList()));
			}
			else {
				gameView.put("opp_salvoes", null);
				gameView.put("opp_hits", null);
				gameView.put("opp_sinks", null);
			}
			return new ResponseEntity(makeMap("gameview", gameView), HttpStatus.OK);
		}
		else {
			return new ResponseEntity(makeMap("error", "Unauthorized"), HttpStatus.UNAUTHORIZED);
		}
	}

	// @PostMapping(path = "/game/{gameId}/players")	
	@RequestMapping(path = "/games/{gameId}/players", method = RequestMethod.POST)
	public ResponseEntity<Map<String,Object>> joinGame(@PathVariable Long gameId, Authentication auth) {
		if (auth != null) {
			Game game = gameRepository.getOne(gameId);
			Player player = getCurrentUser(auth);
			Date date = new Date();
			gameRepository.getOne(gameId);
			if (gameRepository.getOne(gameId).getGamePlayers().size() == 1) {
				if (game.getGamePlayers().stream().filter(player1 -> player1.getPlayer().getId() == getCurrentUser(auth).getId()).findFirst().orElse(null) == null){
					GamePlayer gamePlayer = new GamePlayer(player, game, date);
					gameplayerRepository.save(gamePlayer);
					return new ResponseEntity<>(makeMap("gpid", gamePlayer.getId()), HttpStatus.CREATED);
				}
			}
			else
				return new ResponseEntity<>(makeMap("Game is full", gameId), HttpStatus.FORBIDDEN);
		}
		return new ResponseEntity<>(makeMap("not logged in", gameId), HttpStatus.UNAUTHORIZED);
	}

	// @PostMapping(path = "/games/players/{gamePlayerId}/ships")	
	@RequestMapping(path = "/games/players/{gamePlayerId}/ships", method = RequestMethod.POST)
	public ResponseEntity<String> saveShips(@PathVariable Long gamePlayerId,
																					Authentication auth,
																					@RequestBody Set<Ship> ships) {
		GamePlayer gamePlayer = gameplayerRepository.getOne(gamePlayerId);
		if (gamePlayer.getPlayer().getId() == getCurrentUser(auth).getId() && getCurrentUser(auth) != null) {
			if (ships.size() == 5 && gamePlayer.getShips().size() == 0) {
				for (Ship ship : ships) {
					ship.setGamePlayer(gamePlayer);  //
					shipRepository.save(ship);
				}
				return new ResponseEntity("created", HttpStatus.CREATED);
			} 
			else {
				return new ResponseEntity(makeMap("error", "Forbidden"), HttpStatus.FORBIDDEN);
			}
		} 
		else {
			return new ResponseEntity(makeMap("error", "Unauthorized"), HttpStatus.UNAUTHORIZED);
		}
	}

	// @PostMapping(path = "/games/players/{gamePlayerId}/salvos")	
	@RequestMapping(path = "/games/players/{gamePlayerId}/salvos", method = RequestMethod.POST)
	public ResponseEntity<String> placeSalvoes(@PathVariable Long gamePlayerId,
																						 Authentication auth,
																						 @RequestBody Salvo salvo) {
		GamePlayer gamePlayer = gameplayerRepository.getOne(gamePlayerId);
		if (gamePlayer.getPlayer().getId() == getCurrentUser(auth).getId() || getCurrentUser(auth) == null) {
			if (gamePlayer != null && salvo.getSalvoLocations().size() == 5 && salvoRepository.findAll().stream().filter(salvorep -> salvorep.getTurn() == salvo.getTurn() && salvorep.getGamePlayer() == gamePlayer ).findFirst().orElse(null) == null) {
				salvo.setGamePlayer(gamePlayer);  //
				salvoRepository.save(salvo);
				return new ResponseEntity("created", HttpStatus.CREATED);
			}
			else {   
				return new ResponseEntity(makeMap("error", "Forbidden"), HttpStatus.FORBIDDEN);
			}
		} 
		else {
			return new ResponseEntity(makeMap("error", "Unauthorized"), HttpStatus.UNAUTHORIZED);
		}
	}

	// @GetMapping("/scoreboard")
	@RequestMapping("/scoreboard")
	public List<Object> getScores(){
		return playerRepository
			.findAll()
			.stream()
			.map(Player::makeScoreDTO)
			.collect(toList());
	}

	//Support Methods
	private Player getCurrentUser(Authentication authentication){
		return playerRepository.findByUserName(authentication.getName());
	}

	private Map<String, Object> makeMap(String key, Object value) {
		Map<String, Object> map = new HashMap<>();
		map.put(key, value);
		return map;
	}

	private boolean isGuest(Authentication auth) {
		return auth == null || auth instanceof AnonymousAuthenticationToken;
	}

	//Other Methods implemented (Game Logic)
	public Object getUserSalvos(GamePlayer gamePlayer){
		return getSalvoes(Objects.requireNonNull(gamePlayer.getGame().getGamePlayers()
																						 .stream()
																						 .filter(player -> player.getId() == gamePlayer.getId())
																						 .findFirst()
																						 .orElse(null)));
	}

	public Object getOppSalvos(GamePlayer gamePlayer) {
		return getSalvoes(Objects.requireNonNull(gamePlayer.getGame().getGamePlayers()
																						 .stream()
																						 .filter(player -> player.getId() != gamePlayer.getId())
																						 .findFirst()
																						 .orElse(null)));
	}

	private List<String> shipLocations (GamePlayer gamePlayer) {
		return gamePlayer.getShips()
			.stream()
			.flatMap(s -> s.getShipLocations().stream())
			.collect(toList());
	}

	private List<String> salvoLocations (GamePlayer gamePlayer) {
		return gamePlayer.getSalvoes()
			.stream()
			.flatMap(salvo -> salvo.getSalvoLocations().stream())
			.collect(Collectors.toList());
	}

	private List<String> getHits(GamePlayer gamePlayer){
		GamePlayer opponent = getOpponent(gamePlayer);
		return shipLocations(opponent)
			.stream()
			.filter(cell -> salvoLocations(gamePlayer).contains(cell))
			.collect(toList());
	}

	private boolean getSinks(Ship ship){
		GamePlayer opponent = getOpponent(ship.getGamePlayer());
		return ship.getShipLocations()
			.stream()
			.allMatch(location -> getHits(opponent)
								.stream()
								.anyMatch(hit -> hit == location));
	}

	private Set<Ship> getSunkShips(GamePlayer gamePlayer){
		return gamePlayer.getShips().stream().filter(ship -> getSinks(ship)).collect(toSet());
	}

	public Object getSalvoes(GamePlayer gamePlayer) {
		return gamePlayer.getSalvoes()
			.stream()
			.map(Salvo::makeSalvoDTO)
			.collect(toList());
	}

	private GamePlayer getOpponent(GamePlayer gamePlayer){
		return gamePlayer.getGame().getGamePlayers()
			.stream()
			.filter(gp -> gp != gamePlayer)
			.findFirst()
			.orElse(null);
	}


	public Map<String, Object> getGameState(Game game){
		Map<String, Object> dto = new LinkedHashMap<String, Object>();
		if(game.getGamePlayers().size() == 2 && game.getGamePlayers().stream().mapToInt(gp -> gp.getShips().size()).sum() == 10){
			int leastSalvoes = game.getGamePlayers().stream().mapToInt(gameplayer -> gameplayer.getSalvoes().size()).min().orElse(-1);
			int mostSalvoes = game.getGamePlayers().stream().mapToInt(gameplayer -> gameplayer.getSalvoes().size()).max().orElse(-1);

			GamePlayer firstPlayer = gameplayerRepository.getOne(game.getGamePlayers().stream().mapToLong(GamePlayer::getId).min().orElse(-1));
			GamePlayer secondPlayer = gameplayerRepository.getOne(game.getGamePlayers().stream().mapToLong(GamePlayer::getId).max().orElse(-1));
			String winnerName = "none";
			GamePlayer gamePlayerToFire;
			if (leastSalvoes == mostSalvoes) {
				gamePlayerToFire = firstPlayer;
			} 
			else {
				gamePlayerToFire = secondPlayer;
			}
			if (game.getGamePlayers().stream().filter(gamePlayer -> getSunkShips(gamePlayer).size() == 5).findFirst().orElse(null) != null)
			{
				winnerName = getWinner(game, leastSalvoes == mostSalvoes);
			}
			dto.put("turn", leastSalvoes + 1);
			dto.put("playerToFire", gamePlayerToFire.getId());
			dto.put("winner", winnerName);
			dto.put("gameOver", game.getGameOver());
		}
		else {
			dto.put("state", "waiting for second player");}
		return dto;
	}

	private String getWinner(Game game, Boolean sameTurn){
		try{
			GamePlayer loser = game.getGamePlayers().stream().filter(gamePlayer -> getSunkShips(gamePlayer).size() == 5).findFirst().orElse(null);
			assert loser != null;
			GamePlayer winner = getOpponent(loser);
			if (getSunkShips(getOpponent(loser)).size() == 5 && sameTurn){
				Score losing = new Score(loser.getGame(), loser.getPlayer(), 0.5);
				Score winning = new Score(winner.getGame(), winner.getPlayer(), 0.5);
				if(!game.getGameOver()){
					saveScores(winning);
					saveScores(losing);
				}
				game.setGameOver(true);
				gameRepository.save(game);
				return "tie";
			}
			else if(sameTurn) {
				Score winning = new Score(winner.getGame(), winner.getPlayer(), 1.0);
				if(!game.getGameOver()){
					saveScores(winning);
				}
				game.setGameOver(true);
				gameRepository.save(game);
				return String.valueOf(winner.getId());
			}
			else 
				return "none, but tried";
		}
		catch(Exception exc) {
			return "none";
		}
	}

	public void saveScores(Score score){
		scoreRepository.save(score);
	}

}
