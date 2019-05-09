package com.CardboardGames.Controllers;

import android.graphics.Point;
import android.util.Log;

import com.CardboardGames.AI.ADVAgent;
import com.CardboardGames.Models.BoardModel;
import com.CardboardGames.Views.BoardView;

public class GameController
{
	/// PUBLIC METHODS

	public GameController(BoardModel model, BoardView view) {
		m_model = model;
		m_view = view;
	}

	public void setView(BoardView view) {
		m_view = view;
	}

	public void setAgent(ADVAgent a){agent = a;}

	public void handleCoinInput(float viewx, float viewy)
	{
		Point board_coords = m_view.getCoinBoardCoords(viewx, viewy);
		BoardModel.Piece old_piece = m_model.getSelectedCoinPiece();
		if (m_model.hasSelectedCoinPiece()
			&& m_model.isValidCoinMove(old_piece, board_coords))
		{
			m_model.moveSelectedCoinPiece(board_coords);

			m_view.invalidate();
			moveMade = true;
			moveToNextState();
			return;
		}

		m_model.selectCoinPieceAt(board_coords);
		if (old_piece != m_model.getSelectedCoinPiece())

			m_view.invalidate();
	}

	public void handleGuerillaInput(float viewx, float viewy) {
		Point board_coords = m_view.getGuerillaBoardCoords(viewx, viewy);
		if (!m_model.isValidGuerillaPlacement(board_coords))
			return;

		m_model.placeGuerillaPiece(board_coords);

		m_view.invalidate();
		if(m_state == GameState.GUERILLA_MOVE_SECOND || m_state == GameState.GUERILLA_SETUP_SECOND){
			moveMade = true;
		}
		moveToNextState();


	}

	public void moveToNextState() {
		if (m_state != GameState.END_GAME && m_model.isGameOver()) {

			m_view.invalidate();
			m_state = GameState.END_GAME;
			return;
		}
		if(agent != null) {
			agent.debugInfo();
		}else{
			Log.d("Agent", "is Null");
		}
		char playerSelection = m_model.getPlayer();

		switch (m_state) {
		case GUERILLA_SETUP_FIRST:
			m_view.postInvalidate();
			m_view.invalidate();
			m_state = GameState.GUERILLA_SETUP_SECOND;
			Log.d("state", "moveToNextState: g setup 1");
			return;
		case GUERILLA_SETUP_SECOND:
			m_view.postInvalidate();
			m_view.invalidate();
			Log.d("state", "moveToNextState: g setup 2");
			m_model.setCurrentPlayer(BoardModel.Player.COIN_PLAYER);

			if(playerSelection == 'q'){
				agent.makeMove();
				if(m_model.lastCoinMoveCaptured()){
					m_model.setCoinMustCapture(true);
					if (m_model.selectedCoinPieceHasValidMoves()) {
						while(m_model.lastCoinMoveCaptured()){
							Log.d("coinmove", "found take");
							if(m_model.getCoinPotentialMoves(m_model.getSelectedCoinPiece()).size() > 0){
								m_view.postInvalidate();
								agent.makeMove();
							}else{
								m_model.setCoinMustCapture(false);
							}

						}
					}else{
						m_model.setCoinMustCapture(false);
					}
				}
				m_state = GameState.COIN_CAPTURE;
				moveToNextState();
			}else{
				m_state = GameState.COIN_MOVE;
			}
			return;
		case COIN_MOVE:
			m_view.postInvalidate();
			//m_view.invalidate();
			Log.d("state", "moveToNextState: coin move");
		case COIN_CAPTURE:
			m_view.postInvalidate();
			//m_view.invalidate();
			Log.d("state", "moveToNextState: coin capture");
			if (m_model.lastCoinMoveCaptured()) {
				m_model.setCoinMustCapture(true);
				if (m_model.selectedCoinPieceHasValidMoves()) {
					m_state = GameState.COIN_CAPTURE;
					return;
				}
			}

			m_model.setCoinMustCapture(false);
			m_model.setLastCoinMoveCaptured(false);
			m_model.deselectCoinPiece();
			m_model.setCurrentPlayer(BoardModel.Player.GUERILLA_PLAYER);

//			m_view.invalidate();

			if(playerSelection == 'c') {

				agent.makeMove();


				m_view.invalidate();
				agent.makeMove();

				m_view.invalidate();
				m_state = GameState.GUERILLA_MOVE_SECOND;
				moveToNextState();
			}else{
				m_state = GameState.GUERILLA_MOVE_FIRST;
			}
			return;
		case GUERILLA_MOVE_FIRST:
			m_view.postInvalidate();
			m_view.invalidate();
			Log.d("state", "moveToNextState: g move 1");
			if (m_model.hasValidGuerillaPlacements()) {
				m_state = GameState.GUERILLA_MOVE_SECOND;
				return;
			}
		case GUERILLA_MOVE_SECOND:
			m_view.postInvalidate();
			m_view.invalidate();// pass-through from first
			Log.d("state", "moveToNextState: g move 2");
			m_model.clearGuerillaPieceHistory();
			m_model.setCurrentPlayer(BoardModel.Player.COIN_PLAYER);

			m_view.invalidate();

			if(playerSelection == 'q'){
				agent.makeMove();
				m_view.invalidate();
				if(m_model.lastCoinMoveCaptured()){
					m_model.setCoinMustCapture(true);

					while(m_model.getCoinMustCapture()){
						Log.d("GAME_TAKE", "found take");

						agent.coinTake();

						if(!(m_model.selectedCoinPieceHasValidMoves())){
							m_model.setCoinMustCapture(false);
						}
					}
					/*if (m_model.selectedCoinPieceHasValidMoves()) {
						while(m_model.lastCoinMoveCaptured()){
							Log.d("coinmove", "found take");

							agent.coinTake();

							if(!(m_model.selectedCoinPieceHasValidMoves())){
								m_model.setCoinMustCapture(false);
							}



						}
					}else{
						m_model.setCoinMustCapture(false);
					}*/
				}
				m_state = GameState.COIN_CAPTURE;
				moveToNextState();
			}else{
				m_state = GameState.COIN_MOVE;
			}
			return;
		case END_GAME:
			m_view.invalidate();
			Log.d("state", "moveToNextState: end");
			m_model.reset();
			m_view.invalidate();
			m_state = GameState.GUERILLA_SETUP_FIRST;
			break;
		}
	}

	public void addTouch(float viewx, float viewy) {
		Log.d("current state", m_state.toString());
		char playerSelection = m_model.getPlayer();

		switch (m_state) {
		case GUERILLA_SETUP_FIRST:
			Log.d("debug", "addTouch: gs1");
			if(playerSelection == 'g') {
				handleGuerillaInput(viewx, viewy);
			}else{

				m_view.invalidate();
				m_state = GameState.GUERILLA_SETUP_SECOND;
				moveToNextState();
			}
		case GUERILLA_SETUP_SECOND:
			if(playerSelection == 'g') {
				handleGuerillaInput(viewx, viewy);
			}else{

				m_view.invalidate();
				m_state = GameState.GUERILLA_SETUP_SECOND;
				moveToNextState();
			}
			Log.d("debug", "addTouch: gs2");
		case GUERILLA_MOVE_FIRST:
			if(playerSelection == 'g') {
				handleGuerillaInput(viewx, viewy);
			}else{

				m_view.invalidate();
				m_state = GameState.GUERILLA_MOVE_SECOND;
				moveToNextState();
			}
			Log.d("debug", "addTouch: gm1");
		case GUERILLA_MOVE_SECOND:
			Log.d("debug", "addTouch: gm2");
			if(playerSelection == 'g') {
				handleGuerillaInput(viewx, viewy);
			}else{

				m_view.invalidate();
				moveToNextState();
			}
			break;
		case COIN_CAPTURE:
			Log.d("debug", "addTouch: coin cap");
		case COIN_MOVE:
			Log.d("debug", "addTouch: coin move");
			handleCoinInput(viewx, viewy);
			break;
		case END_GAME:
			Log.d("debug", "addTouch: endgame");
			moveToNextState();
			break;
		}

	}

	public void setupGuerilla(){
		m_state = GameState.GUERILLA_SETUP_SECOND;

		m_view.invalidate();
		moveToNextState();
	}

	public String getState(){
		return m_state.toString();
	}


	/// PRIVATE TYPES

	private enum GameState {
		GUERILLA_SETUP_FIRST{
			@Override
			public String toString(){
				return "GS1";
			}
		},
		GUERILLA_SETUP_SECOND{
			@Override
			public String toString(){
				return "GS2";
			}
		},
		COIN_MOVE{
			@Override
			public String toString(){
				return "CM";
			}
		},
		COIN_CAPTURE{
			@Override
			public String toString(){
				return "CC";
			}
		},
		GUERILLA_MOVE_FIRST{
			@Override
			public String toString(){
				return "GM1";
			}
		},
		GUERILLA_MOVE_SECOND{
			@Override
			public String toString(){
				return "GM2";
			}
		},
		END_GAME{
			@Override
			public String toString(){
				return "END";
			}
		}
	}

	/// PRIVATE MEMBERS

	private GameState m_state = GameState.GUERILLA_SETUP_FIRST;
	private BoardModel m_model = null;
	private BoardView m_view = null;
	private ADVAgent agent = null;
	private int moveCounter = 0;
	public boolean moveMade = false;
}
