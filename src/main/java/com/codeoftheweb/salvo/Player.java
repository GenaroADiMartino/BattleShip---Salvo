package com.codeoftheweb.salvo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;
import javax.persistence.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.Map;

import static java.util.stream.Collectors.toList;

@Entity
public class Player {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
	@GenericGenerator(name = "native", strategy = "native")
	private long id; 
	private String userName;
	private String passWord;

	// Relacion con GamePlayers
	@OneToMany(mappedBy = "player", fetch = FetchType.EAGER)
	Set<GamePlayer> gamePlayers;

	// Relacion con Scores
	@OneToMany(mappedBy="player", fetch=FetchType.EAGER)
	Set<Score> scores;

	//Métodos
	public Player() {}

	public Player(String user, String passWord) {
		this.userName = user;
		this.passWord = passWord;
	}

	public long getId() {
		return this.id;
	}

	public String getUserName() {
		return this.userName;
	}

	public String getPassWord() {
		return this.passWord;
	}

	public Set<GamePlayer> getGamePlayers() {
		return gamePlayers;
	}

	@JsonIgnore
	public List<Game> getGames() {
		return gamePlayers
			.stream()
			.sorted((gp1,gp2) -> (int)(gp1.getId() - gp2.getId()))
			.map(gp -> gp.getGame())
			.collect(toList());
	}

	public Set<Score> getScores() {
		return scores;
	}

	public String toString() {
		return userName;
	}

	// Salida DTO Player
	public Map<String, Object> makePlayerDTO() {
		Map<String, Object> dto = new LinkedHashMap<>();
		dto.put("id", this.getId());
		dto.put("username", this.getUserName());		
		return dto;
	}

	// Salida DTO Score
	public Map<String, Object> makeScoreDTO(){
		Map<String, Object> dto = new LinkedHashMap<>();
		dto.put("player", this.getUserName());
		dto.put("totalscore", this.getScores().stream().map(s-> s.getScore()).mapToDouble(s -> s).sum());
		dto.put("wins", this.getScores().stream().filter(s -> s.getScore() == 1.0).count());
		dto.put("losses", this.getScores().stream().filter(s -> s.getScore() == 0.0).count());
		dto.put("ties", this.getScores().stream().filter(s -> s.getScore() == 0.5).count());
		return dto;
	}
}
