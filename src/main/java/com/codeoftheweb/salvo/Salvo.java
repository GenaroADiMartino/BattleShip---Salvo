package com.codeoftheweb.salvo;

import org.hibernate.annotations.GenericGenerator;
import javax.persistence.*;
import java.util.*;

@Entity
public class Salvo {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
	@GenericGenerator(name = "native", strategy = "native")
	private long id;
	private Integer turn;  

	// Relacion con GamePlayer
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "gamePlayer_id")
	private GamePlayer gamePlayer;

	@ElementCollection
	private List<String> salvoLocations;

	//Mètodos
	public Salvo(){}

	public Salvo(Integer turn, List<String> salvoLocations, GamePlayer gamePlayer) {
		this.turn = turn;
		this.salvoLocations = salvoLocations;
		this.gamePlayer = gamePlayer;
	}

	public long getId() {
		return this.id;
	}

	public GamePlayer getGamePlayer() {
		return this.gamePlayer;
	}

	public void setGamePlayer(GamePlayer gamePlayer) {  //
		this.gamePlayer = gamePlayer;
	}

	public Integer getTurn() {
		return this.turn;
	}

	public List<String> getSalvoLocations() {
		return salvoLocations;
	}

	//Salida DTO Salvo
	public Map<String, Object> makeSalvoDTO() {
		Map<String, Object> dto = new LinkedHashMap<>();
		dto.put("turn",this.getTurn());
		dto.put("location",this.getSalvoLocations());
		return dto;
	}
}
