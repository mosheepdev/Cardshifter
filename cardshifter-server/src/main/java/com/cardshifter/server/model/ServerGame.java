package com.cardshifter.server.model;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.cardshifter.api.ClientIO;
import com.cardshifter.api.messages.Message;
import com.cardshifter.api.outgoing.GameOverMessage;
import com.cardshifter.api.outgoing.NewGameMessage;
import com.cardshifter.modapi.base.ECSGame;

/**
 * Handles the state of the game and knows the current players
 * 
 * @author Simon Forsberg
 */
public abstract class ServerGame {
	private static final Logger logger = LogManager.getLogger(ServerGame.class);

	private final List<ClientIO> players;
	private final int id;

	private Instant active;
	/**
	 * This is where the current game state is stored
	 */
	private GameState state;

	protected final ECSGame game;
//	private final Set<ClientIO> observers;
//	private final ChatArea chat;
	
	/**
	 * Initializes the player collection, sets the initial game state
	 * 
	 * @param server Server where the game is located
	 * @param id ID of the game
	 * @param game ECS Game object
	 */
	public ServerGame(int id, ECSGame game) {
		this.id = id;
		this.players = Collections.synchronizedList(new ArrayList<>());
		this.state = GameState.NOT_STARTED;
		this.active = Instant.now();
		this.game = game;
//		this.chat = server.newChatRoom(this.toString());
	}

	/**
	 * Checks if the game is already over, sets state if not and sends a message to players
	 */
	public void endGame() {
		if (state == GameState.ENDED) {
			throw new IllegalStateException("Game can only be ended once");
		}
		logger.info("Game Ended: " + this + " with players " + players);
		this.send(new GameOverMessage());
		this.active = Instant.now();
		this.state = GameState.ENDED;
	}

	/**
	 * 
	 * @return Whether or not the game is in the ended state
	 */
	public boolean isGameOver() {
		return state == GameState.ENDED;
	}
	
	/**
	 * This is where the players are added to the ServerGame, also sets the game state
	 * 
	 * @param players The players to add to the game
	 */
	public void start(List<ClientIO> players) {
		if (state != GameState.NOT_STARTED) {
			throw new IllegalStateException("Game can only be started once");
		}
		this.players.addAll(players);
		for (ClientIO player : players) {
			player.sendToClient(new NewGameMessage(this.id, players.indexOf(player)));
		}
		this.onStart();
		this.active = Instant.now();
		this.state = GameState.RUNNING;
	}

	/**
	 * Called when the game starts
	 */
	protected abstract void onStart();
	
	/**
	 * Sends a message to all players in the game
	 * 
	 * @param data Message to send
	 */
	public void send(Message data) {
		players.forEach(player -> player.sendToClient(data));
	}
	
	/**
	 * 
	 * @return The ID of the ServerGame
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * 
	 * @return The time elapsed since active time was stored
	 */
	public Duration getLastActive() {
		return Duration.between(active, Instant.now());
	}
	
	/**
	 * 
	 * @return The current state of the game
	 */
	public GameState getState() {
		return state;
	}
	
	public ECSGame getGameModel() {
		return game;
	}
	
	/**
	 * 
	 * @return The current players for this game
	 */
	public List<ClientIO> getPlayers() {
		return Collections.unmodifiableList(players);
	}

	/**
	 * 
	 * @param client Which client to look for
	 * @return Whether or not the given player is in this game
	 */
	public boolean hasPlayer(ClientIO client) {
		return players.contains(client);
	}
	
}
