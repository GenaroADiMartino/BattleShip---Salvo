package com.codeoftheweb.salvo;

import org.hibernate.annotations.GenericGenerator;
import javax.persistence.*;
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

	// Relacion con GamePlayers
	@OneToMany(mappedBy = "game", fetch = FetchType.EAGER)
	Set<GamePlayer> gamePlayers;

	// Relacion con Scores
	@OneToMany(mappedBy="game", fetch=FetchType.EAGER)
	Set<Score> Scores;

	//Métodos
	public Game() {}

	public Game(int hours){
		Date now = new Date();
		this.gameDate = Date.from(now.toInstant().plusSeconds(hours*3600));
		this.gamePlayers = new HashSet<>();
	}

	public Date getDate() {
		return this.gameDate;
	}

	public long getId() {
		return this.id;
	}

	public List<Player> getPlayers() {
		return gamePlayers.stream().map(sub -> sub.getPlayer()).collect(toList());
	}

	public Set<GamePlayer> getGamePlayers(){
		return this.gamePlayers.stream().sorted((gp1,gp2) -> (int)(gp1.getId() - gp2.getId())).collect(Collectors.toCollection(LinkedHashSet::new));
	}

	public Set<Score> getScores() {
		return Scores;
	}

	// Salida DTO Game
	public Map<String, Object> makeGameDTO() {
		Map<String, Object> dto = new LinkedHashMap<>();
		dto.put("id", this.getId());
		dto.put("created", this.getDate());
		dto.put("gameplayers", this.getGamePlayers().stream()
						.map(gp -> gp.makeGamePlayerDTO())
						.collect(toList()));
		return dto;
	}
}
