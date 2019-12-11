package com.codeoftheweb.salvo;

import org.hibernate.annotations.GenericGenerator;
import javax.persistence.*;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.toList;

@Entity
public class Game {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
	@GenericGenerator(name = "native", strategy = "native")
	private long id;
	private Date gameDate;
	private Boolean gameOver;

	// Relacion con GamePlayers
	@OneToMany(mappedBy = "game", fetch = FetchType.EAGER)
	Set<GamePlayer> gamePlayers;

	// Relacion con Scores
	@OneToMany(mappedBy="game", fetch=FetchType.EAGER)
	Set<Score> Scores;

	//Métodos
	public Game() {}

	public Game(Date date, Boolean gameOver) {
		this.gameDate = date;
		this.gameOver = gameOver;
	}

	public Date getDate() {
		return this.gameDate;
	}

	public Boolean getGameOver() {
		return gameOver;
	}

	public void setGameOver(Boolean gameOver) {
		this.gameOver = gameOver;
	}

	public long getId() {
		return this.id;
	}

	public List<Player> getPlayers() {
		return gamePlayers
			.stream()
			.map(sub -> sub.getPlayer())
			.collect(toList());
	}

	public Set<GamePlayer> getGamePlayers(){
		return this.gamePlayers.stream()
			.sorted((gp1,gp2) -> (int)(gp1.getId() - gp2.getId()))
			.collect(Collectors.toCollection(LinkedHashSet::new));
	}

	public Set<Score> getScores() {
		return Scores;
	}

	public Date calculateDate(Integer seconds) {
		Instant dateToInstant = this.gameDate.toInstant();
		Instant calculatedDate = dateToInstant.plusSeconds(seconds);
		this.gameDate = Date.from(calculatedDate);
		return this.gameDate;
	}

	// Salida DTO Game
	public Map<String, Object> makeGameDTO() {
		Map<String, Object> dto = new LinkedHashMap<>();
		dto.put("id", this.getId());
		dto.put("created", this.getDate());
		dto.put("gameplayers", this.getGamePlayers()
						.stream()
						.map(gp -> gp.makeGamePlayerDTO())
						.collect(toList()));
		return dto;
	}
}
