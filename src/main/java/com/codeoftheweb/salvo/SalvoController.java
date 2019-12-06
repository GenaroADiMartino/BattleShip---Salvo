package com.codeoftheweb.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static java.util.stream.Collectors.toList;

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
						.map(game -> game.makeGameDTO())
						.collect(toList()));
		return dto;
	}

	// @PostMapping("/games")
	@RequestMapping(path = "/games", method = RequestMethod.POST)
	public ResponseEntity<Map<String,Object>> createGame(Authentication auth) {
		if (auth != null){
			Game game = new Game(8);
			gameRepository.save(game);
			Player player = playerRepository.findByUserName(auth.getName());
			GamePlayer gamePlayer = new GamePlayer(game, player);
			gameplayerRepository.save(gamePlayer);
			return new ResponseEntity<>(makeMap("gpid", gamePlayer.getId()), HttpStatus.CREATED);
		}
		else {
			return new ResponseEntity<>(makeMap("gpid", "No player logged in"), HttpStatus.UNAUTHORIZED);
		}
	}

	// @GetMapping("/scoreboard")
	@RequestMapping("/scoreboard")
	public List<Object> getScores(){
		return playerRepository
			.findAll()
			.stream()
			.map(score -> score.makeScoreDTO())
			.collect(toList());
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
	public ResponseEntity<Map<String, Object>> getGamePlayerData(@PathVariable Long gamePlayerId, Authentication auth) {
		GamePlayer user = gameplayerRepository.getOne(gamePlayerId);
		Map<String, Object> gameView = new LinkedHashMap<>();
		HttpStatus status;

		if (auth==null || user.getPlayer().getId() != getCurrentUser(auth).getId()) {
			gameView.put ("Cheat", 401);
			status = HttpStatus.UNAUTHORIZED;
			return new ResponseEntity<>(gameView, status);
		}

		Set<GamePlayer> gamePlayerSet = user.getGame().getGamePlayers();
		gameView.put("game", user.getGame().makeGameDTO());
		gameView.put("ships", user.getShips()
								 .stream()
								 .map(thisShip -> thisShip.makeShipDTO())
								 .collect(toList()));
		for (GamePlayer gamePlayer : gamePlayerSet) {
			if (gamePlayer.getId() != user.getId()) {
				gameView.put("opp_salvoes", gamePlayer.getSalvoes()
										 .stream()
										 .map(salvo -> salvo.makeSalvoDTO())
										 .collect((toList())));
			}
			else {
				gameView.put("your_salvoes", user.getSalvoes()
										 .stream()
										 .map(salvo -> salvo.makeSalvoDTO())
										 .collect((toList())));
			}
		}
		status = HttpStatus.OK;
		return new ResponseEntity<>(makeMap("gameview", gameView), status);
	}

	// @PostMapping(path = "/game/{gameId}/players")	
	@RequestMapping(path = "/games/{gameId}/players", method = RequestMethod.POST)
	public ResponseEntity<Map<String,Object>> joinGame(@PathVariable Long gameId, Authentication auth) {
		if (auth != null) {
			Game game = gameRepository.getOne(gameId);
			Player player = getCurrentUser(auth);
			Date date = new Date();
			if (gameRepository.getOne(gameId) != null) {
				if (gameRepository.getOne(gameId).getGamePlayers().size() == 1) {
					if (game.getGamePlayers().stream().filter(player1 -> player1.getPlayer().getId() == getCurrentUser(auth).getId()).findFirst().orElse(null) == null){
						GamePlayer gamePlayer = new GamePlayer(game, player);
						gameplayerRepository.save(gamePlayer);
						return new ResponseEntity<>(makeMap("gpid", gamePlayer.getId()), HttpStatus.CREATED);
					}
				}
				else
					return new ResponseEntity<>(makeMap("Game is full", gameId), HttpStatus.FORBIDDEN);
			}
			else
				return new ResponseEntity<>(makeMap("No such game", gameId), HttpStatus.FORBIDDEN);
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
			if (gamePlayer != null && ships.size() == 5 && gamePlayer.getShips().size() == 0) {
				for (Ship ship : ships) {
					ship.setGamePlayer(gamePlayer);
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
            if (gamePlayer != null && salvo.getSalvoLocations().size() == 5) {
                salvo.setGamePlayer(gamePlayer);
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

}
