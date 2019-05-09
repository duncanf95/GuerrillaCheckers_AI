package com.CardboardGames.GuerillaCheckers.Core.Actions;

import com.CardboardGames.Core.Math.Point2I;
import com.CardboardGames.GuerillaCheckers.Core.BoardState;

public class CaptureCoinPiece extends Capture {
	
	public CaptureCoinPiece(Point2I position) {
		super(position);
	}

	@Override
	public void doExecute(BoardState boardState) {
		assert(boardState.getCoinPieceAt(this.getPosition()) != null);
		boardState.removeCoinPieceAt(getPosition());
		assert(boardState.getCoinPieceAt(this.getPosition()) == null);
	}

	@Override
	protected void doUndo(BoardState boardState) {
		assert(boardState.getCoinPieceAt(this.getPosition()) == null);
		boardState.addCoinPieceAt(getPosition());
		assert(boardState.getCoinPieceAt(this.getPosition()) != null);
	}
}
