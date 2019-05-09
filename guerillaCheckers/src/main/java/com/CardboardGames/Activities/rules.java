package com.CardboardGames.Activities;

public class rules {
    public String Rules;

    public rules(){
        Rules = "<h3><b>Pieces:</b>" +

                "<p>6 large Counterinsurgent (COIN) pieces, 66 small Guerrilla pieces</p>" +

                "<h3>Moving and Capturing: </h3>"+

                "<p>The Guerrilla player does not move their pieces. Instead, they place two and only two pieces per turn"+
                "on the board, on the points (intersections) of the squares. The first piece must be orthogonally"+
                "adjacent to any stone on the board; the second piece must be orthogonally adjacent to the first piece "+
                "placed. the Guerrilla may not place pieces on the exterior board edge points (i.e. any place it is impossible for"+
                "them to be captured). He captures an enemy piece by surrounding it (i.e. having a piece, or an"+
                "exterior board edge point, on each of the four points of the square the piece occupies – note this"+
                "makes the edge of the board very dangerous for the COIN player). they remove the piece. The"+
                "COIN player either: moves one piece per turn, one square diagonally in any direction; or makes"+
                "captures with one piece by jumping over the point between two squares into an empty square,"+
                "removing Guerrilla pieces as he goes. they are not forced to capture if they do not want to, but if they"+
                "start capturing they must continue to make captures for as long as it is possible for them to do so, "+
                "along the path they choose. Players may not pass. Victory: The player who clears the board of all "+
                "enemy pieces at the end of his turn wins. The Guerrilla player loses if he runs out of pieces.</p>";


    }
}
