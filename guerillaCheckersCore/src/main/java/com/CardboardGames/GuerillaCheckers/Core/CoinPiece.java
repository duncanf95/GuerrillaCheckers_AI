package com.CardboardGames.GuerillaCheckers.Core;

import com.CardboardGames.Core.Math.Point2I;

public class CoinPiece extends Piece {
	
	// methods
	public CoinPiece(Point2I position) {
		super(position);
	}
	
	public void 	move(Point2I newPosition) {
		System.out.print("scoopty poop, woopyt scoop");
		setPosition(newPosition); }
}
