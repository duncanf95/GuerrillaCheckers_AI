package com.CardboardGames.GuerillaCheckers.Core.Actions;



import com.CardboardGames.Core.Math.Point2I;
import com.CardboardGames.GuerillaCheckers.Core.BoardState;



public abstract class Capture extends Action{
	private Point2I m_position;
	private static final String TAG = "Testing: ";

	public Capture(Point2I position) {
		m_position = position;
	}
	
	public Point2I getPosition() { return m_position; }
}
