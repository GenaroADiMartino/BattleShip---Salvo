package com.codeoftheweb.salvo;

import org.hibernate.annotations.GenericGenerator;
import javax.persistence.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.List;

@Entity
public class Ship {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
	@GenericGenerator(name = "native", strategy = "native")
	private long id;
	private String shipType;

	//Relacion con GamePlayers
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "gamePlayer_id")
	private GamePlayer gamePlayer;

	@ElementCollection
	private List<String> shipLocations;

	//Métodos
	public Ship(){}

	public Ship(String shipType, List<String> shipLocations, GamePlayer gamePlayer) {
		this.shipType = shipType;
		this.shipLocations = shipLocations;
		this.gamePlayer = gamePlayer;
	}

	public long getId() {
		return this.id;
	}

	public String getShipType() {
		return shipType;
	}

	public GamePlayer getGamePlayer() {
		return gamePlayer;
	}

	public void setGamePlayer(GamePlayer gamePlayer) {
		this.gamePlayer = gamePlayer;
	}

	public List<String> getShipLocations() {
		return shipLocations;
	}

	//Salida DTO Ships
	public Map<String, Object> makeShipDTO() {
		Map<String, Object> dto = new LinkedHashMap<>();
		dto.put("shipType", this.getShipType());
		dto.put("shipLocations", this.getShipLocations());
		return dto;
	}
}
