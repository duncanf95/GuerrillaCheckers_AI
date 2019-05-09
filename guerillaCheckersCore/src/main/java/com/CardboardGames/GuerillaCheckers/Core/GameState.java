package com.CardboardGames.GuerillaCheckers.Core;

import com.CardboardGames.GuerillaCheckers.Core.GameState.PlayerTurn;


public class GameState {

	public enum PlayerTurn { GUERILLA, COIN }

	private PlayerTurn m_playerTurn;;
	
	public PlayerTurn getCurrentTurn() { 
		return m_playerTurn;
	}
	
	public void setCurrentTurn(PlayerTurn playerTurn) {
		m_playerTurn = playerTurn;
	}
}
