package com.CardboardGames.GuerillaCheckers.Core;

import java.util.Stack;

import com.CardboardGames.Core.Math.Point2I;
import com.CardboardGames.GuerillaCheckers.Core.GameState.PlayerTurn;
import com.CardboardGames.GuerillaCheckers.Core.Actions.Action;
import com.CardboardGames.GuerillaCheckers.Core.Actions.CaptureGuerillaPiece;
import com.CardboardGames.GuerillaCheckers.Core.Actions.Move;

public class GameManager {
	private GameState m_gameState;
	private BoardState m_boardState;
	private Stack<Action> m_undoStack;

	public GameManager() {
		this(new GameState(), new BoardState());
	}

	public GameManager(GameState gameState, BoardState boardState) {
		m_gameState = gameState;
		m_boardState = boardState;
		m_undoStack = new Stack<Action>();
	}

	public void Execute(Action action) {
		action.execute(m_boardState);
		m_undoStack.push(action);
	}

	public void Undo() {
		if (!m_undoStack.empty()) {
			Action action = m_undoStack.pop();
			Undo(action);
		}
	}

	public void Undo(Action action) {
		action.undo(m_boardState);
	}

	public Move CreateMove(Point2I from, Point2I to)
			throws InvalidMessageException {
		if (m_gameState.getCurrentTurn() == PlayerTurn.COIN) {
			throw new InvalidMessageException();
		}
		if (m_boardState.getCoinPieceAt(from) != null) {
			throw new InvalidMessageException();
		}

		CaptureGuerillaPiece potentialCapture = getCaptureFromMove(from, to);
		if (potentialCapture != null) {
			return new Move(from, to, potentialCapture);
		} else {
			return new Move(from, to);
		}
	}

	private Point2I getCapturePointFromMove(Point2I from, Point2I to) {
		Point2I point = new Point2I();
		point.x = to.x > from.x ? from.x : to.x;
		point.y = to.y > from.y ? from.y : to.y;
		return point;
	}

	private CaptureGuerillaPiece getCaptureFromMove(Point2I from, Point2I to) {
		Point2I position = getCapturePointFromMove(from, to);
		assert (m_boardState.getGuerillaPieceAt(position) != null);

		assert (position != null);
		if (m_boardState.getGuerillaPieceAt(position) != null) {
			return new CaptureGuerillaPiece(position);
		} else {
			return null;
		}

	}

}
