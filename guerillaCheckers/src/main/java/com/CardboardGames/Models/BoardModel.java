package com.CardboardGames.Models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.graphics.Point;
import android.util.Log;
import com.CardboardGames.AI.ADVAgent;

public class BoardModel implements Cloneable {

	/// PUBLIC METHODS

	public BoardModel(ADVAgent in_agent) {

		if(in_agent != null){
			agent = in_agent;

			agent.setModel(this);
		}
		initPieces();

	}

	public void setSelectedCoinPiece(Piece p){
		m_selectedCoinPiece = p;

	}

	public boolean isBlack(final int row, final int col) {
		return row % 2 != col % 2;
	}

	public boolean hasSelectedCoinPiece() {
		return m_selectedCoinPiece != null;
	}

	public Piece getSelectedCoinPiece() {
		return m_selectedCoinPiece;
	}

	private boolean hasGuerillaPieceOrBoardEdge(int x, int y) {
		if (x < 0 || y < 0 || x >= COLS-1 || y >= ROWS-1)
			return true;
		return getGuerillaPieceAt(x, y) != null;
	}

	public boolean coinPieceWouldBeCapturedAt(int x, int y) {
		if (hasGuerillaPieceOrBoardEdge(x-1, y-1) &&
			hasGuerillaPieceOrBoardEdge(x-1, y) &&
			hasGuerillaPieceOrBoardEdge(x, y-1) &&
			hasGuerillaPieceOrBoardEdge(x, y)) {
			return true;
		}
		return false;
	}

	public boolean isValidCoinMove(Piece piece, int x, int y) {
		if (x < 0 || x >= COLS)
			return false;
		if (y < 0 || y >= ROWS)
			return false;

		Point piece_pos = piece.getPosition();
		int xdiff = Math.abs(x - piece_pos.x);
		int ydiff = Math.abs(y - piece_pos.y);

		if (xdiff != 1 || ydiff != 1)
			return false;

		if (getCoinPieceAt(x, y) != null)
			return false;

		if (m_coinMustCapture
			&& !wouldCaptureGuerillaPiece(piece, new Point(x, y)))
			return false;

		return true;
	}

	public boolean isValidCoinMove(Piece piece, Point pos) {
		return isValidCoinMove(piece, pos.x, pos.y);
	}

	private Piece getPieceAt(final Point point, List<Piece> pieces) {
		for (Piece piece : pieces) {
			if (piece.getPosition().equals(point))
				return piece;
		}
		return null;
	}

	public Piece getCoinPieceAt(final Point point) {
		return getPieceAt(point, m_coinPieces);
	}

	public Piece getCoinPieceAt(int x, int y) {
		return getCoinPieceAt(new Point(x, y));
	}

	public Piece getGuerillaPieceAt(final Point point) {
		return getPieceAt(point, m_guerillaPieces);
	}

	public Piece getGuerillaPieceAt(int x, int y) {
		return getGuerillaPieceAt(new Point(x, y));
	}

	public List<Piece> getCoinPieces() {
		return Collections.unmodifiableList(m_coinPieces);
	}

	public List<Piece> getGuerillaPieces() {
		return Collections.unmodifiableList(m_guerillaPieces);
	}

	public int getNumCoinPieces() {
		return m_coinPieces.size();
	}

	public int getNumGuerillaPieces() {
		return m_guerillaPieces.size();
	}

	public int getRemainingGuerillaPieces() {
		return m_numGuerillaPiecesLeft;
	}

	private boolean areGuerillaPiecesAdjacent(Point first, Point second) {
		int xdiff = Math.abs(first.x - second.x);
		int ydiff = Math.abs(first.y - second.y);
		return (xdiff == 0 && ydiff == 1 || xdiff == 1 && ydiff == 0);
	}

	private boolean isValidGuerillaPlacement(Point point, boolean is_first) {
		if (point.x < 0 || point.x >= COLS - 1)
			return false;
		if (point.y < 0 || point.y >= ROWS - 1)
			return false;
		if (getGuerillaPieceAt(point) != null)
			return false;
		if (is_first)
			return true;
		if (getGuerillaPieceAt(point.x + 1, point.y) == null &&
			getGuerillaPieceAt(point.x - 1, point.y) == null &&
			getGuerillaPieceAt(point.x, point.y + 1) == null &&
			getGuerillaPieceAt(point.x, point.y - 1) == null) {
			return false;
		}
		if (m_firstGuerillaPiece != null) {
			Point first_pos = m_firstGuerillaPiece.getPosition();
			if (!areGuerillaPiecesAdjacent(point, first_pos))
				return false;
		}
		return true;
	}

	public boolean isValidGuerillaPlacement(final Point point) {
		boolean is_first = getNumGuerillaPieces() == 0;
		return isValidGuerillaPlacement(point, is_first);
	}

	public boolean isValidGuerillaPlacement(int x, int y) {
		return isValidGuerillaPlacement(new Point(x, y));
	}

	public boolean hasValidGuerillaPlacements() {
		if (m_coinPieces.size() == (ROWS-1)*(COLS-1))
			return false;
		if (m_firstGuerillaPiece == null)
			return true;

		Point pos = m_firstGuerillaPiece.getPosition();
		int x = pos.x;
		int y = pos.y;
		if (isValidGuerillaPlacement(x+1, y) ||
			isValidGuerillaPlacement(x-1, y) ||
			isValidGuerillaPlacement(x, y+1) ||
			isValidGuerillaPlacement(x, y-1)) {
			return true;
		}
		return false;
	}

	public boolean isGameOver() {
		if (!m_gameStarted)
			return false;
		if (m_guerillaPieces.size() == 0)
			return true;
		if (m_coinPieces.size() == 0)
			return true;
		if (m_numGuerillaPiecesLeft <= 0)
			return true;
		return false;
	}

	private void captureCoinPiecesAroundGuerillaAt(int x, int y) {
		if (coinPieceWouldBeCapturedAt(x, y))
			captureCoinPieceAt(x, y);
		if (coinPieceWouldBeCapturedAt(x, y+1))
			captureCoinPieceAt(x, y+1);
		if (coinPieceWouldBeCapturedAt(x+1, y))
			captureCoinPieceAt(x+1, y);
		if (coinPieceWouldBeCapturedAt(x+1, y+1))
			captureCoinPieceAt(x+1, y+1);
	}

	public void placeGuerillaPiece(final Point point) {
		Piece piece = new Piece(point);
		m_guerillaPieces.add(piece);

		captureCoinPiecesAroundGuerillaAt(point.x, point.y);
		--m_numGuerillaPiecesLeft;

		m_gameStarted = true;
		if (m_firstGuerillaPiece == null) {
			Log.d("gpp", "piece places");
			Log.d("Gpp amount of left", Integer.toString(m_numGuerillaPiecesLeft));
			m_firstGuerillaPiece = piece;
		}
		else
			m_firstGuerillaPiece = null;
	}

	public boolean hasSelectedPiece() {
		return m_selectedCoinPiece != null;
	}

	public boolean selectCoinPieceAt(Point point) {
		if (m_coinMustCapture)
			return false;

		m_selectedCoinPiece = getCoinPieceAt(point);
		m_lastCoinMoveCaptured = false;
		return m_selectedCoinPiece != null;
	}

	public boolean captureCoinPieceAt(Point point) {
		int num_pieces = m_coinPieces.size();
		for (int idx = 0; idx < num_pieces; ++idx) {
			if (m_coinPieces.get(idx).getPosition().equals(point)) {
				m_coinPieces.remove(idx);
				return true;
			}
		}
		return false;
	}

	public boolean captureCoinPieceAt(int x, int y) {
		return captureCoinPieceAt(new Point(x, y));
	}

	private boolean doCaptureGuerillaPiece(
		Point coin_from,
		Point coin_to,
		boolean do_capture)
	{
		int guerilla_x = coin_from.x - (coin_to.x < coin_from.x ? 1 : 0);
		int guerilla_y = coin_from.y - (coin_to.y < coin_from.y ? 1 : 0);
		Point capture_point = new Point(guerilla_x, guerilla_y);

		int num_pieces = m_guerillaPieces.size();
		for (int idx = 0; idx < num_pieces; ++idx) {
			if (m_guerillaPieces.get(idx).getPosition().equals(capture_point)) {
				if (do_capture)
					m_guerillaPieces.remove(idx);
				return true;
			}
		}
		return false;
	}

	public boolean captureGuerillaPiece(Point coin_from, Point coin_to) {
		Log.d("Logic", "captureGuerillaPiece");
		//agent.debugInfo();
		return doCaptureGuerillaPiece(coin_from, coin_to, true);
	}

	public boolean wouldCaptureGuerillaPiece(Piece piece, Point coin_to) {
		Log.d("Logic", "wouldCaptureGuerillaPiece");
		return doCaptureGuerillaPiece(piece.getPosition(), coin_to, false);
	}

	public boolean wouldCaptureGuerillaPiece(Piece piece, int x, int y) {
		return wouldCaptureGuerillaPiece(piece, new Point(x, y));
	}

	public boolean moveSelectedCoinPiece(Point point) {
		if (m_selectedCoinPiece == null)
			return false;
		if (!isValidCoinMove(m_selectedCoinPiece, point))
			return false;

		Point piece_pos = m_selectedCoinPiece.getPosition();
		m_lastCoinMoveCaptured = captureGuerillaPiece(piece_pos, point);
		m_selectedCoinPiece.setPosition(point);
		return true;
	}

	public void deselectCoinPiece() {
		m_selectedCoinPiece = null;
	}

	public boolean lastCoinMoveCaptured() {
		return m_lastCoinMoveCaptured;
	}

	public void setLastCoinMoveCaptured(boolean captured) {
		m_lastCoinMoveCaptured = captured;
	}

	public boolean selectedCoinPieceHasValidMoves() {
		if (m_selectedCoinPiece == null)
			return false;
		Point pos = m_selectedCoinPiece.getPosition();
		int x = pos.x;
		int y = pos.y;

		if (isValidCoinMove(m_selectedCoinPiece, x-1, y-1) ||
			isValidCoinMove(m_selectedCoinPiece, x-1, y+1) ||
			isValidCoinMove(m_selectedCoinPiece, x+1, y-1) ||
			isValidCoinMove(m_selectedCoinPiece, x+1, y+1)) {
			return true;
		}
		return false;
	}

	public void setCoinMustCapture(boolean must_capture) {
		m_coinMustCapture = must_capture;
	}

	public void clearGuerillaPieceHistory() {
		m_firstGuerillaPiece = null;
	}

	/// PRIVATE METHODS

	private void initPieces() {
		m_coinPieces.add(new Piece(new Point(3,2)));
		m_coinPieces.add(new Piece(new Point(2,3)));
		m_coinPieces.add(new Piece(new Point(4,3)));
		m_coinPieces.add(new Piece(new Point(3,4)));
		m_coinPieces.add(new Piece(new Point(5,4)));
		m_coinPieces.add(new Piece(new Point(4,5)));
	}

	public void reset() {
		m_currentPlayer = Player.GUERILLA_PLAYER;
		m_numGuerillaPiecesLeft = MAX_GUERILLA_PIECES;
		m_firstGuerillaPiece = null;
		m_selectedCoinPiece = null;
		m_lastCoinMoveCaptured = false;
		m_coinMustCapture = false;
		m_gameStarted = false;
		m_guerillaPieces.clear();
		m_coinPieces.clear();
		initPieces();
	}

	public void setCurrentPlayer(Player player) {
		m_currentPlayer = player;
	}

	public Player getCurrentPlayer() {
		return m_currentPlayer;
	}

	/// PRIVATE TYPES

	public class Piece {
		private Point position = new Point(0,0);

		public Piece(Point position) {
			this.setPosition(position);
		}

		public Point getPosition() { return position; }
		public void setPosition(Point position) { this.position = position; }
		public Piece clone() throws CloneNotSupportedException {
			Piece newPiece = (Piece)super.clone();

			try{
				int x = 0;
				int y = 0;
				x += position.x;
				y += position.y;
				newPiece.setPosition(new Point(x,y));

			}catch (Exception e){

			}
			return newPiece;
		}
	}

	public enum Player {
		GUERILLA_PLAYER,
		COIN_PLAYER
	}

	public ArrayList<Piece> getGPieces(){
		return m_guerillaPieces;
	}

	public ArrayList<Piece> getCPieces(){
		return m_coinPieces;
	}

	public void setGpieces(ArrayList<Piece> pieces){
		m_guerillaPieces = pieces;
	}

	public void setCpieces(ArrayList<Piece> pieces){
		m_coinPieces = pieces;
	}

	public void setPlayer(char p){
		playerSelection = p;

		if(playerSelection == 'c'){
			agentSelection = 'g';
			agent.setAgentPlayer(agentSelection);
		}else{
			agentSelection = 'c';
			agent.setAgentPlayer(agentSelection);
		}

		Log.d("debug", "set player: " + playerSelection);
		Log.d("debug", "set agent: " + agentSelection);
	}

	public BoardModel clone() throws CloneNotSupportedException
	{
		BoardModel cloneModel = (BoardModel) super.clone();
		try {
			cloneModel.setGpieces((ArrayList<Piece>) getGPieces().clone());
			cloneModel.setCpieces((ArrayList<Piece>) getCPieces().clone());
			ArrayList<Piece> newPieces = new ArrayList<Piece>();
			for(Piece p: cloneModel.getCPieces()){
				newPieces.add(p.clone());
			}
			cloneModel.setCpieces(newPieces);
		}catch(Exception e){

		}


		return cloneModel;
	}

	public ArrayList<Point> getPotentialGuerillaMoves(){
		ArrayList<Point> potentialMoves = new ArrayList<Point>();
		for (int idx_col = 0; idx_col < COLS; ++idx_col) {
			for (int idx_row = 0; idx_row < ROWS; ++idx_row) {
				Point point = new Point(idx_col, idx_row);
				if (isValidGuerillaPlacement(point)) {
					potentialMoves.add(point);
				}
			}
		}
		return potentialMoves;
	}

	public ArrayList<Point> getCoinPotentialMoves(BoardModel.Piece p){
		ArrayList<Point> potentialMoves = new ArrayList<Point>();

		for (int idx_col = 0; idx_col < COLS; ++idx_col) {
			for (int idx_row = 0; idx_row < ROWS; ++idx_row) {
				Point point = new Point(idx_col, idx_row);
				if (isValidCoinMove(p, idx_col, idx_row)){
					potentialMoves.add(point);
				}

			}
		}


		return potentialMoves;
	}

	public void RestorePiece(){
		m_coinPieces.add(new Piece(new Point(0,0)));
	}

	public boolean getCoinMustCapture(){
		return m_coinMustCapture;
	}


	public char getPlayer(){ return playerSelection; }

	/// CONSTANTS

	public static final int MAX_GUERILLA_PIECES = 66;

	/// PRIVATE MEMBERS

	private Player m_currentPlayer = Player.GUERILLA_PLAYER;
	private int m_numGuerillaPiecesLeft = MAX_GUERILLA_PIECES;
	private Piece m_firstGuerillaPiece = null;
	private Piece m_selectedCoinPiece = null;
	private boolean m_lastCoinMoveCaptured = false;
	private boolean m_coinMustCapture = false;
	private boolean m_gameStarted = false;

	/// @{
	/// Pieces
	private  ArrayList<Piece> m_coinPieces = new ArrayList<Piece>();
	private  ArrayList<Piece> m_guerillaPieces = new ArrayList<Piece>();

	private ADVAgent agent = null;
	private char playerSelection;
	private char agentSelection;
	/// @}

	/// @{
	/// Grid Dimensions
	public static final int ROWS = 8;
	public static final int COLS = 8;
	/// @}
}
