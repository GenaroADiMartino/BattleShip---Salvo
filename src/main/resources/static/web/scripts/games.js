var app = new Vue({
	el: '#app',
	data: {
		scoreData: [],
		gameData: [],
		gamesList: [],
		game: {
			date: "",
			player1: "",
			player2: ""
		},
		date: 0,
		userLoggedIn: false,
		createPlayerMessage: "",
		currentUser: ""
	},

	methods: {
		getScores: function (gamePlayer) {
			var fetchConfig =
					fetch("/api/scoreboard", {
						method: "GET",
						credential: "include"
					}).then(this.onScoreDataFetched)
			},

		onScoreConversionToJsonSuccessful: function (json) {
			this.scoreData = json;            
		},

		onScoreDataFetched: function (response) {
			response.json()
				.then(this.onScoreConversionToJsonSuccessful)
		},

		getDataObject: function () {
			var fetchConfig =
					fetch("/api/games", {
						method: "GET",
						credentials: "include"
					}).then(this.onDataFetched)
			},

		onConversionToJsonSuccessful: function (json) {
			app.gameData = json;
			if (this.gameData.player != "No player logged in") {
				this.userLoggedIn = true;
			} else {
				this.userLoggedIn = false;
			}
			this.createGameList();
			this.currentUser = this.gameData.player.username;
		},

		onDataFetched: function (response) {
			response.json()
				.then(app.onConversionToJsonSuccessful)
		},

		createGameList: function () {
			this.gamesList = []
			for (i = 0; i < this.gameData.games.length; i++) {
				this.game = {
					date: "",
					game: 0,
					player1: "",
					player2: "",
					gameLink: 0,
					numberOfPlayers: 0
				}
				this.date = new Date(this.gameData.games[i].created);
				this.game.date = this.date.toLocaleString()
				this.game.game = this.gameData.games[i].id
				this.game.player1 = this.gameData.games[i].gameplayers[0].player.username
				if (this.gameData.games[i].gameplayers.length > 1) {
					this.game.numberOfPlayers = 2;
					this.game.player2 = this.gameData.games[i].gameplayers[1].player.username;
					if (this.gameData.player.id == this.gameData.games[i].gameplayers[1].player.id) {
						this.game.gameLink = "/web/game.html?gp=" + this.gameData.games[i].gameplayers[1].id
					}
				} else this.gameplayer = "";
				if (this.gameData.player.id == this.gameData.games[i].gameplayers[0].player.id) {
					this.game.gameLink = "/web/game.html?gp=" + this.gameData.games[i].gameplayers[0].id
				}
				this.gamesList.push(this.game)
			}
			var date = Date()
			},

		login: function () {
			let username = document.getElementById("username").value
			let password = document.getElementById("password").value
			fetch("/api/login", {
				credentials: 'include',
				method: 'POST',
				headers: {
					'Accept': 'application/json',
					'Content-Type': 'application/x-www-form-urlencoded'
				},
				body: 'username=' + username + '&password=' + password,
			})
				.then(r => {
				if (r.status == 200) {
					this.createPlayerMessage = "Successful Login";
					this.getDataObject();
				}
				else {
					this.createPlayerMessage = "Error";
				}
			})
				.catch(function (res) {
			})
		},

		logout: function () {
			fetch("/api/logout")
				.then(this.getDataObject())
				.catch();
		},

		createPlayer: function () {
			let username = document.getElementById("username").value
			let password = document.getElementById("password").value
			fetch("/api/players", {
				credentials: 'include',
				method: 'POST',
				headers: {
					'Accept': 'application/json',
					'Content-Type': 'application/x-www-form-urlencoded'
				},
				body: 'username=' + username + '&password=' + password,
			})
				.then(r => {
				if (r.status == 201) {
					this.createPlayerMessage = "Player created";
					this.getDataObject()
					this.login()
				} else if (r.status == 409) {
					this.createPlayerMessage = "Name already in use"
				} else if (r.status == 403) {
					this.createPlayerMessage = "Please enter a valid name"
				} else {
					this.createPlayerMessage = "Unknown error"
				}
			})
				.catch(function (res) {
			})
		},

		createGame: function(){
			fetch("/api/games", {
				credentials: 'include',
				method: 'POST',
				headers: {
					'Accept': 'application/json',
					'Content-Type': 'application/x-www-form-urlencoded'
				},
				body: 'username=' + this.gameData.player.username
			})
				.then(r => {
				if (r.status == 201) {
					this.createPlayerMessage = "Game created"
					window.setTimeout(function(){window.location.reload()}, 1000)
				} else if (r.status == 409) {
					this.createPlayerMessage = "Name already in use"
				} else if (r.status == 403) {
					this.createPlayerMessage = "Please enter a valid name"
				} else {
					this.createPlayerMessage = "Unknown error"
				}
			})
				.catch(function (res) {
			})  
		},

		joinGame: function (game) {
			fetch("/api/games/" + game + "/players", {
				credentials: 'include',
				method: 'POST',
			})
				.then(response =>
							response.json().then(data => ({
				data: data,
				status: response.status
			})).then(res => {
				if (res.status == 201) {
					res => res.json();
					window.location.href = "/web/game.html?gp=" + res.data.gpid
				} else {
					alert("error")
				}
			}))
				.catch(function (res) {
			})
		},

		goToGamePage: function (link) {
			window.location.href = link

		}
	},

	created: function () {
		this.getDataObject();
		this.getScores();
	}
})
