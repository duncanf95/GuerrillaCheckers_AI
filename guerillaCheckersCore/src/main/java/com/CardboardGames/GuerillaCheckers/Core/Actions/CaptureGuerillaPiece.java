package com.CardboardGames.GuerillaCheckers.Core.Actions;

import com.CardboardGames.Core.Math.Point2I;
import com.CardboardGames.GuerillaCheckers.Core.BoardState;

public class CaptureGuerillaPiece extends Capture {

	public CaptureGuerillaPiece(Point2I position) {
		super(position);
	}
	
	@Override
	public void doExecute(BoardState boardState) {
		assert(boardState.getGuerillaPieceAt(this.getPosition()) != null);
		boardState.removeGuerillaPieceAt(getPosition());
		assert(boardState.getGuerillaPieceAt(this.getPosition()) == null);
	}

	@Override
	protected void doUndo(BoardState boardState) {
		assert(boardState.getGuerillaPieceAt(this.getPosition()) == null);
		boardState.addGuerillaPieceAt(getPosition());
		assert(boardState.getGuerillaPieceAt(this.getPosition()) != null);
	}
}
