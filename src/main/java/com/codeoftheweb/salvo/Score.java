package com.codeoftheweb.salvo;

import javax.persistence.*;
import java.util.Date;

@Entity
public class Score {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id1;
	private Date finishDate;
	private Double score;

	//Relacion con Players
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="player_id")
	private Player player;

	//Relacion con Games
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="game_id")
	private Game game;

	//Metodos
	public Score(){}

	public Score (Game game, Player player, Double score) {
		this.game = game;
		this.player = player;
		this.score = score;
		this.finishDate = new Date();
	}

	public Long getId1() {
		return id1;
	}

	public Date getFinishDate() {
		return finishDate;
	}

	public Double getScore() {
		return score;
	}

	public Player getPlayer() {
		return player;
	}

	public Game getGame() {
		return game;
	}

}
