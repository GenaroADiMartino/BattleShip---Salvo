package com.codeoftheweb.salvo;

import org.hibernate.annotations.GenericGenerator;
import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.toList;

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

	public void setGamePlayer(GamePlayer gamePlayer) {  //
		this.gamePlayer = gamePlayer;
	}

	public List<String> getShipLocations() {
		return shipLocations;
	}

	//Other Methods implemented (Game Logic)
	private List<String> salvoLocations (GamePlayer gamePlayer) {
		return gamePlayer.getSalvoes()
			.stream()
			.flatMap(salvo -> salvo.getSalvoLocations().stream())
			.collect(Collectors.toList());
	}

	public List<String> getHits(){
		return this.getShipLocations()
			.stream()
			.filter(cell -> salvoLocations(gamePlayer).contains(cell))
			.collect(toList());
	}

	public boolean getSinks(){
		return this.getShipLocations()
			.stream()
			.allMatch(location -> this.getHits()
								.stream()
								.anyMatch(hit -> hit.equals(location)));
	}

	//Salida DTO Ships
	public Map<String, Object> makeShipDTO() {
		Map<String, Object> dto = new LinkedHashMap<String, Object>();
		dto.put("shipType", this.getShipType());
		dto.put("shipLocations", this.getShipLocations());
		if(this.getGamePlayer().getGame().getGamePlayers().size() == 2){
			dto.put("sunk", this.getSinks());
		}
		return dto;
	}

}
