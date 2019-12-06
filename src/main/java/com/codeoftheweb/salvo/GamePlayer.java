package com.codeoftheweb.salvo;

import org.hibernate.annotations.GenericGenerator;
import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

@Entity
public class GamePlayer {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
	@GenericGenerator(name = "native", strategy = "native")
	private long id;
	public Date gameDate;

	//Relacion con Games
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "game_id")
	private Game game;

	//Relacion con Players
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "player_id")
	private Player player;

	//Relacion con Ships
	@OneToMany(mappedBy = "gamePlayer", fetch = FetchType.EAGER)
	Set<Ship> ships;

	//Relacion con Salvos
	@OneToMany(mappedBy = "gamePlayer", fetch = FetchType.EAGER)
	Set<Salvo> salvoes;

	//Métodos
	public GamePlayer() {}

	public GamePlayer(Game game, Player player) {
		this.game = game;
		this.player = player;
	}

	public long getId() {
		return this.id;
	}

	public Game getGame() {
		return this.game;
	}

	public Player getPlayer() {
		return this.player;
	}

	public Date getGameDate() {
		return this.gameDate;
	}

	public Set<Ship> getShips() {
		return this.ships.stream().sorted((s1,s2) -> (int)(s1.getId() - s2.getId())).collect(Collectors.toCollection(LinkedHashSet::new));
	}

	public Set<Salvo> getSalvoes() {
		return salvoes;
	}

	public Score getScore () {
		return player.getScores().stream().filter(s -> s.getGame() == game).findFirst().orElse(null);
	}


	//Salida DTO GamePlayer
	public Map<String, Object> makeGamePlayerDTO() {
		Map<String, Object> dto = new LinkedHashMap<>();
		dto.put("id", this.getId());
		dto.put("created", this.getGameDate());
		dto.put("player", this.getPlayer().makePlayerDTO());
		if (this.getScore() != null) {
			dto.put("score", this.getScore().getScore());
		}
		return dto;
	}
}
