package com.CardboardGames.GuerillaCheckers.Core;

import java.util.ArrayList;



import com.CardboardGames.Core.Math.Point2I;
import com.CardboardGames.GuerillaCheckers.Core.Piece;
import com.CardboardGames.GuerillaCheckers.Core.CoinPiece;
import com.CardboardGames.GuerillaCheckers.Core.GuerillaPiece;

public class BoardState {

	// methods
	private <T extends Piece> T getPieceAt(Point2I position,
			ArrayList<T> pieces) {
		for (T piece : pieces) {
			if (piece.getPosition().equals(position))
				return piece;
		}
		return null;
	}
	
	private boolean isInsideBounds(Point2I position, int boardSize) {
		return position.x >= 0 && position.x < boardSize &&
		       position.y >= 0 && position.y < boardSize;
	}
	
	public CoinPiece getCoinPieceAt(Point2I position) {
		System.out.println("test");
		assert(isInsideBounds(position, getCoinBoardWidth()));
		return getPieceAt(position, m_coinPieces);
	}
	
	public GuerillaPiece getGuerillaPieceAt(Point2I position) {
		System.out.println("test");
		assert(isInsideBounds(position, getGuerillaBoardWidth()));
		return getPieceAt(position, m_guerillaPieces);
	}

	public boolean coinPieceExistsAt(Point2I position) {
		return getCoinPieceAt(position) != null;
	}

	public boolean guerillaPieceExistsAt(Point2I position) {
		return getGuerillaPieceAt(position) != null;
	}
	
	public GuerillaPiece addGuerillaPieceAt(Point2I position) {
		System.out.println("test");
		assert(!guerillaPieceExistsAt(position));
		GuerillaPiece piece = new GuerillaPiece(position);
		m_guerillaPieces.add(piece);
		assert(guerillaPieceExistsAt(position));
		return piece;
	}
	
	public CoinPiece addCoinPieceAt(Point2I position) {
		System.out.println("test");
		assert(!coinPieceExistsAt(position));
		CoinPiece piece = new CoinPiece(position);
		m_coinPieces.add(piece);
		assert(coinPieceExistsAt(position));
		return piece;
	}
	
	private <T extends Piece> T removePieceAt(Point2I position,
			ArrayList<T> pieces) {
		for (int idx = 0; idx < pieces.size(); ++idx) {
			if (pieces.get(idx).getPosition().equals(position)) {
				return pieces.remove(idx);
			}
		}
		return null;
	}

	public GuerillaPiece removeGuerillaPieceAt(Point2I position) {
		assert(guerillaPieceExistsAt(position));
		GuerillaPiece piece = removePieceAt(position, m_guerillaPieces);
		assert(!guerillaPieceExistsAt(position));
		assert(piece != null);
		return piece;
	}
	
	public CoinPiece removeCoinPieceAt(Point2I position) {
		assert(coinPieceExistsAt(position));
		CoinPiece piece = removePieceAt(position, m_coinPieces);
		assert(!coinPieceExistsAt(position));
		assert(piece != null);
		return piece;
	}

	public Integer getCoinBoardWidth() {
		return 8;
	}

	public Integer getGuerillaBoardWidth() {
		return 7;
	}

	// members
	protected ArrayList<CoinPiece> m_coinPieces;
	protected ArrayList<GuerillaPiece> m_guerillaPieces;
}
