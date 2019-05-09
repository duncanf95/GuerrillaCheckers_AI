package com.CardboardGames.GuerillaCheckers.Core;

import com.CardboardGames.Core.Math.Point2I;

public abstract class Piece {
	
	// methods
	public Piece(Point2I position) {
		assert(position != null);
		m_position = position;
	}
	
	public Point2I   getPosition() { return m_position; }
	public void      setPosition(Point2I position) { m_position = position; }
	
	// members
	private Point2I  m_position;
}
