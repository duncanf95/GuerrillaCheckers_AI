package com.CardboardGames.Views;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;

import com.CardboardGames.R;
import com.CardboardGames.Models.BoardModel;


public class BoardView extends View
{
	/// PUBLIC METHODS

	public BoardView(Context ctx, BoardModel model) {
		super(ctx);
		m_model = model;
		m_paint.setFlags(Paint.ANTI_ALIAS_FLAG);
		Resources resources = getResources();
		m_board_texture = (BitmapDrawable)resources.getDrawable(R.drawable.board_default);
	}

	@Override
	public void onDraw(Canvas canvas) {
		myC = canvas;
		drawBoard(canvas);
		drawPieces(canvas);
		drawHUD(canvas);
		Log.d("onDraw", "Active");
		if (m_model.isGameOver())
			drawGameOver(canvas);
		drawUpdated = true;
	}

	public void setM_model(BoardModel model){
		try {
			m_model = model.clone();
		}catch (Exception e){

		}
	}

	public void forceDraw(){
		drawBoard(myC);
		drawPieces(myC);
		drawHUD(myC);
		Log.d("onDraw", "Active");
		if (m_model.isGameOver())
			drawGameOver(myC);
		drawUpdated = true;
	}

	public void setDrawUpdated(boolean updated){
		drawUpdated = updated;
	}

	public Point getCoinBoardCoords(float viewx, float viewy) {
		float rect_size = getRectSize();
		Point board_pos = getBoardPosition();
		int xoffset = board_pos.x + BORDER_SIZE_PX;
		int yoffset = board_pos.y + BORDER_SIZE_PX;
		double xcoord = Math.floor((viewx - xoffset) / rect_size);
		double ycoord = Math.floor((viewy - yoffset) / rect_size);
		return new Point((int)xcoord, (int)ycoord);
	}

	public Point getGuerillaBoardCoords(float viewx, float viewy) {
		float half_rect = 0.5f * getRectSize();
		return getCoinBoardCoords(viewx - half_rect, viewy - half_rect);
	}

	// PRIVATE METHODS

	private Rect getRect(int row, int col, Point board_pos, int board_size) {
		double rect_width = (double)board_size / BoardModel.COLS;
		double rect_height = (double)board_size / BoardModel.ROWS;

		double left = board_pos.x + BORDER_SIZE_PX + col * rect_width;
		double top = board_pos.y + BORDER_SIZE_PX + row * rect_height;
		double right = left + rect_width;
		double bottom = top + rect_height;

		return new Rect((int)left, (int)top, (int)right, (int)bottom);
	}

	private int getRectSize() {
		return (int)((double)getBoardSize() / BoardModel.COLS);
	}

	private int getBoardSizeInclBorder() {
		return Math.min(getWidth(), getHeight());
	}

	private int getBoardSize() {
		return getBoardSizeInclBorder() - 2*BORDER_SIZE_PX;
	}

	private Point getBoardPosition() {
		int board_size = getBoardSizeInclBorder();
		int offsetx = (getWidth() - board_size) / 2;
		int offsety = (getHeight() - board_size) / 2;
		return new Point(offsetx, offsety);
	}

	private void drawBoard(Canvas canvas) {
		Point board_pos = getBoardPosition();
		int board_size_incl_border = getBoardSizeInclBorder();
		int board_size_px = getBoardSize();

		m_board_texture.setBounds(board_pos.x + BORDER_SIZE_PX,
				                  board_pos.y + BORDER_SIZE_PX,
				                  board_pos.x + BORDER_SIZE_PX + board_size_px,
				                  board_pos.y + BORDER_SIZE_PX + board_size_px);
		m_board_texture.draw(canvas);
		if(DEBUG_VIEW) {
			m_paint.setColor(BORDER_CLR);
			Style style = m_paint.getStyle();
			try {
				m_paint.setStyle(Style.STROKE);
				canvas.drawRect(
					board_pos.x,
					board_pos.y,
					board_pos.x + board_size_incl_border,
					board_pos.y + board_size_incl_border,
					m_paint);

				m_paint.setColor(PINK);
				canvas.drawRect(
					board_pos.x + BORDER_SIZE_PX,
					board_pos.y + BORDER_SIZE_PX,
					board_pos.x + BORDER_SIZE_PX + board_size_px,
					board_pos.y + BORDER_SIZE_PX + board_size_px,
					m_paint);

				m_paint.setColor(PINK);
				for (int idx_row = 0; idx_row < BoardModel.ROWS; ++idx_row) {
					for (int idx_col = 0; idx_col < BoardModel.COLS; ++idx_col) {
						if (m_model.isBlack(idx_row, idx_col))
							continue;

						Rect rect = getRect(idx_row, idx_col, board_pos, board_size_px);
						canvas.drawRect(rect, m_paint);
					}
				}
			}
			finally {
				m_paint.setStyle(style);
			}			
		}		
	}

	private void drawStar(Canvas canvas, Point center, int radius) {
		final int SEGMENTS = 10;
		final double IN_OUT_RATIO = 0.4;
		final double SEGMENT_ANGLE = 2 * Math.PI / SEGMENTS;
		final double INITIAL_ROTATION = 0.5 * Math.PI;

		Path star = new Path();
		int initx = (int)(radius * Math.cos(INITIAL_ROTATION));
		int inity = (int)(radius * Math.sin(INITIAL_ROTATION));
		star.moveTo(center.x + initx, center.y - inity);
		double theta = INITIAL_ROTATION;
		for (int idx = 1; idx < 12; ++idx) {
			theta += SEGMENT_ANGLE;
			double radius_multiplier = (idx % 2 == 1) ? IN_OUT_RATIO : 1;
			double r = radius * radius_multiplier;
			int x = (int)(r * Math.cos(theta));
			int y = (int)(r * Math.sin(theta));
			star.lineTo(center.x + x, center.y - y);
		}
		canvas.drawPath(star, m_paint);
	}

	private void drawCoinPiece(
		Canvas canvas, Point center, int fg_color, int bg_color, int radius)
	{
		m_paint.setColor(bg_color);
		canvas.drawCircle(center.x, center.y, radius, m_paint);

		m_paint.setColor(fg_color);
		int star_radius = (int)(radius * COIN_FG_BG_RATIO);
		drawStar(canvas, center, star_radius);
	}

	private void drawGuerillaPiece(Canvas canvas, int cx, int cy, int radius) {
		canvas.drawCircle(cx, cy, radius, m_paint);
	}

	private int getCoinPieceRadius(int board_size) {
		return (int)(0.5 * getRectSize() * COIN_PIECE_TO_SQUARE_RATIO);
	}

	private int getCoinPieceFGColor(BoardModel.Piece piece) {
		return piece == m_model.getSelectedCoinPiece() ?
			SELECTED_COIN_PIECE_SECONDARY_CLR :
			COIN_PIECE_SECONDARY_CLR;
	}

	private int getCoinPieceBGColor(BoardModel.Piece piece) {
		return piece == m_model.getSelectedCoinPiece() ?
			SELECTED_COIN_PIECE_CLR :
			COIN_PIECE_CLR;
	}

	private void drawCoinPieces(
		Canvas canvas,
		List<BoardModel.Piece> pieces)
	{
		int board_size = getBoardSize();
		Point board_pos = getBoardPosition();
		int radius = getCoinPieceRadius(board_size);

		for (BoardModel.Piece piece : pieces) {
			int fg_color = getCoinPieceFGColor(piece);
			int bg_color = getCoinPieceBGColor(piece);
			Point pos = piece.getPosition();
			Rect r = getRect(pos.y, pos.x, board_pos, board_size);
			Point center = new Point(r.centerX(), r.centerY());
			drawCoinPiece(canvas, center, fg_color, bg_color, radius);
		}
	}

	private int setAlpha(int color, int alpha) {
		return (color & 0x00FFFFFF) | (alpha << 24);
	}

	private void drawGuerillaPotentialMoves(Canvas canvas) {
		Point board_pos = getBoardPosition();
		int board_size = getBoardSize();
		int radius = getGuerillaPieceRadius(board_size);
		int color = setAlpha(GUERILLA_PIECE_CLR, 0x66);
		m_paint.setColor(color);
		for (int idx_col = 0; idx_col < BoardModel.COLS; ++idx_col) {
			for (int idx_row = 0; idx_row < BoardModel.ROWS; ++idx_row) {
				Point point = new Point(idx_col, idx_row);
				if (!m_model.isValidGuerillaPlacement(point))
					continue;
				Rect r = getRect(idx_row, idx_col, board_pos, board_size);
				drawGuerillaPiece(canvas, r.right, r.bottom, radius);
			}
		}
	}

	public ArrayList<Point> getGuerillaPotentialMoves(){
		ArrayList<Point> potentialMoves = new ArrayList<Point>();
		int color = setAlpha(GUERILLA_PIECE_CLR, 0x66);
		for (int idx_col = 0; idx_col < BoardModel.COLS; ++idx_col) {
			for (int idx_row = 0; idx_row < BoardModel.ROWS; ++idx_row) {
				Point point = new Point(idx_col, idx_row);
				if (m_model.isValidGuerillaPlacement(point)) {
					potentialMoves.add(point);
				}
			}
		}

		return potentialMoves;

	}

	private void drawCoinPotentialMoves(Canvas canvas) {
		BoardModel.Piece piece = m_model.getSelectedCoinPiece();
		if (piece == null)
			return;

		Point board_pos = getBoardPosition();
		int board_size = getBoardSize();
		int radius = getCoinPieceRadius(board_size);
		int alpha = 0x66;
		int fg_color = setAlpha(SELECTED_COIN_PIECE_SECONDARY_CLR, alpha);
		int bg_color = setAlpha(SELECTED_COIN_PIECE_CLR, alpha);
		for (int idx_col = 0; idx_col < BoardModel.COLS; ++idx_col) {
			for (int idx_row = 0; idx_row < BoardModel.ROWS; ++idx_row) {
				if (!m_model.isValidCoinMove(piece, idx_col, idx_row))
					continue;
				Rect r = getRect(idx_row, idx_col, board_pos, board_size);
				Point center = new Point(r.centerX(), r.centerY());
				drawCoinPiece(canvas, center, fg_color, bg_color, radius);
			}
		}
	}

	public ArrayList<Point> getCoinPotentialMoves(BoardModel.Piece p){
		ArrayList<Point> potentialMoves = new ArrayList<Point>();
		BoardModel.Piece piece = p;
		for (int idx_col = 0; idx_col < BoardModel.COLS; ++idx_col) {
			for (int idx_row = 0; idx_row < BoardModel.ROWS; ++idx_row) {
				Point point = new Point(idx_col, idx_row);
				if (m_model.isValidCoinMove(p, idx_col, idx_row)){
					potentialMoves.add(point);
				}

			}
		}


		return potentialMoves;
	}

	private int getGuerillaPieceRadius(int board_size) {
		return (int)(0.5 * getRectSize() * GUERILLA_PIECE_TO_SQUARE_RATIO);
	}

	private void drawGuerillaPieces(
		Canvas canvas,
		List<BoardModel.Piece> pieces)
	{
		m_paint.setColor(GUERILLA_PIECE_CLR);
		int board_size = getBoardSize();
		Point board_pos = getBoardPosition();
		int radius = getGuerillaPieceRadius(board_size);
		for (BoardModel.Piece piece : pieces) {
			Point pos = piece.getPosition();
			Rect rect = getRect(pos.y, pos.x, board_pos, board_size);
			drawGuerillaPiece(canvas, rect.right, rect.bottom, radius);
		}
	}

	private boolean shouldDrawCoinPotentialMoves() {
		return m_model.hasSelectedCoinPiece();
	}

	private void drawPieces(Canvas canvas) {
		drawCoinPieces(canvas, m_model.getCoinPieces());
		drawGuerillaPieces(canvas, m_model.getGuerillaPieces());
		if (shouldDrawCoinPotentialMoves())
			drawCoinPotentialMoves(canvas);
		if (m_model.getCurrentPlayer() == BoardModel.Player.GUERILLA_PLAYER)
			drawGuerillaPotentialMoves(canvas);
	}

	private void drawNumGuerillaPiecesLeft(Canvas canvas) {
		m_paint.setTextAlign(Align.LEFT);
		int num_left = m_model.getRemainingGuerillaPieces();
		String text = "Guerrillas Left: " + num_left;
		canvas.drawText(text, 2*BORDER_SIZE_PX, 2*BORDER_SIZE_PX, m_paint);
	}

	private void drawHUD(Canvas canvas) {
		m_paint.setColor(HUD_TEXT_CLR);
		m_paint.setTextSize(24.0f);
		m_paint.setTypeface(Typeface.DEFAULT_BOLD);
		drawNumGuerillaPiecesLeft(canvas);
	}

	private BoardModel.Player getWinner() {
		return (m_model.getNumCoinPieces() == 0) ?
			BoardModel.Player.GUERILLA_PLAYER :
			BoardModel.Player.COIN_PLAYER;
	}

	private String getWinnerText(BoardModel.Player winner) {
		return winner == BoardModel.Player.GUERILLA_PLAYER ?
			"GUERRILLAS WIN!" : "COUNTER-INSURGENTS WIN!";
	}

	private String getGuerillasPlayedText() {
		int num_pieces_used = BoardModel.MAX_GUERILLA_PIECES
			- m_model.getRemainingGuerillaPieces();
		return num_pieces_used + " GUERRILLA PIECES PLAYED";
	}

	private void drawGameOver(Canvas canvas) {
		m_paint.setColor(GAME_OVER_TEXT_CLR);
		m_paint.setTextSize(60.0f);
		m_paint.setTypeface(Typeface.DEFAULT_BOLD);
		m_paint.setTextAlign(Align.CENTER);

		FontMetrics metrics = m_paint.getFontMetrics();
		float font_height = metrics.top;
		int cx = getWidth() / 2;
		int cy = (int)(getHeight()/2 - font_height/4);
		canvas.drawText("GAME OVER", cx, cy, m_paint);

		BoardModel.Player winner = getWinner();
		m_paint.setTextSize(20.0f);
		cy = (int)(getHeight()/2 - 3*font_height/5);
		canvas.drawText(getWinnerText(winner), cx, cy, m_paint);

		if (winner == BoardModel.Player.GUERILLA_PLAYER) {
			cy = (int)(getHeight()/2 - font_height);
			canvas.drawText(getGuerillasPlayedText(), cx, cy, m_paint);
		}
	}




	public String getModelString(){
		return m_model.toString();
	}

	/// PRIVATE MEMBERS

	private final Paint m_paint = new Paint();

	/// Board Model
	private BoardModel m_model = null;
	private BitmapDrawable m_board_texture;

	/// @{
	/// Board Properties
	private static final int BORDER_SIZE_PX = 10;
	private static final double COIN_PIECE_TO_SQUARE_RATIO = 0.9;
	private static final double COIN_FG_BG_RATIO = 0.7;
	private static final double GUERILLA_PIECE_TO_SQUARE_RATIO = 0.4;
	/// @}

	/// @{
	/// Board Colors (32 bit ARGB format)
	private static final int PINK                              = 0xFFFF00FF;
	private static final int BORDER_CLR                        = 0xFFA66000;
	private static final int COIN_PIECE_CLR                    = 0xFF3B9E00;
	private static final int COIN_PIECE_SECONDARY_CLR          = 0xFFAAAA00;
	private static final int SELECTED_COIN_PIECE_CLR           = 0xFF00AA72;
	private static final int SELECTED_COIN_PIECE_SECONDARY_CLR = 0xFFBBBB00;
	private static final int GUERILLA_PIECE_CLR                = 0xFF222222;
	private static final int WHITE_CLR                         = 0xFF9B7D27;
	private static final int BLACK_CLR                         = 0xFFC1A657;
	private static final int GAME_OVER_TEXT_CLR                = 0xFF000000;
	private static final int HUD_TEXT_CLR                      = 0xFFFFFFFF;
	/// @}
	private static final boolean DEBUG_VIEW = false;
	private boolean drawUpdated = false;
	private Canvas myC = null;
}
