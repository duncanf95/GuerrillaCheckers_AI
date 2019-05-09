package com.CardboardGames.AI;

import android.app.Application;

import android.graphics.Point;
import android.util.Log;
import com.CardboardGames.Controllers.GameController;
import com.CardboardGames.Listeners.BooVariable;
import com.CardboardGames.Models.BoardModel;
import com.CardboardGames.Models.BoardModel.Piece;
import com.CardboardGames.Views.BoardView;
import com.CardboardGames.DataStructures.BinarySort;

import java.lang.reflect.Array;
import java.util.BitSet;
import java.util.Random;

import java.util.ArrayList;

public class ADVAgent {
    private BoardModel model;
    private BoardView view;
    private GameController controller = null;
    private ArrayList<BoardModel.Piece> g_pieces = null;
    private ArrayList<BoardModel.Piece> c_pieces = null;
    private char agentPlayer;
    private ArrayList<BinarySort> nodes = new ArrayList<BinarySort>();
    private int maxDepth = 15;
    public BooVariable alisten;

    public ADVAgent() {
        //model = in_model;
        //view = in_view;
        //debugInfo();
    }
    public char GetAgentChoice(){
        return agentPlayer;
    }

    public void setView(BoardView in_view) {
        view = in_view;
    }

    public void setModel(BoardModel in_model) {

        model = in_model;
        Log.d("Class load Agent", "successful");
    }

    public void setController(GameController in_controller) {
        controller = in_controller;
    }

    public void setAgentPlayer(char p) {
        agentPlayer = p;
    }

    public void debugInfo() {
        Log.d("Agent", "in debug method");
        if (model.getGPieces() != null) g_pieces = model.getGPieces();
        if (model.getCPieces() != null) c_pieces = model.getCPieces();

        //Log.d("in", "debug ");

        Point pos = null;
        int counter = 1;


        // piece positions
        /*
        Log.d("count Gpieces", Integer.toString(g_pieces.size()));

        for(BoardModel.Piece piece: g_pieces){
            pos = piece.getPosition();
            Log.d("G_Piece Pos X" + Integer.toString(counter), Integer.toString(pos.x));
            Log.d("G_Piece Pos Y" + Integer.toString(counter), Integer.toString(pos.y));
            counter++;
        }
*/
        //Log.d("count Cpieces", Integer.toString(c_pieces.size()));

        for (BoardModel.Piece piece : c_pieces) {
            pos = piece.getPosition();
            Log.d("C_Piece Pos X" + Integer.toString(counter), Integer.toString(pos.x));
            Log.d("C_Piece Pos Y" + Integer.toString(counter), Integer.toString(pos.y));
            counter++;
        }


        // potential moves
        /*
        ArrayList<Point> gMoves;

        if(view.getGuerillaPotentialMoves() != null) {
            gMoves = view.getGuerillaPotentialMoves();
            for (Point p :gMoves){
                Log.d("Pot G moves X " + counter, Integer.toString(p.x));
                Log.d("Pot G moves Y " + counter, Integer.toString(p.y));
                counter ++;
            }
        }

        */

    }

    public void makeMove() {
        Log.d("Agent", "makeMove");
        if (agentPlayer == 'g') {
            placePieceGuerilla();
            //alisten.setBoo(true);
        } else {
            Log.d("Agent", "choose coin");
            placePieceCoin();
            alisten.setBoo(true);
        }
    }

    private void placePieceGuerilla() {
        Point decision = guerillaDecision();
        if (decision != null) {
            Log.d("placePieceGuerilla", "decision not null");
            model.placeGuerillaPiece(decision);
        }
    }

    private Point guerillaDecision() {
        Point decision = null;
        ArrayList<Point> potMoves = model.getPotentialGuerillaMoves();
        Random rand = new Random();


        ArrayList<Node> currentLevel = new ArrayList<Node>();

        decision = treeSearch();


        return decision;
    }

    private void placePieceCoin() {
        Point decision = null;
        if (model.lastCoinMoveCaptured()) {
            decision = coinCaptureDecision();
        } else {
            decision = coinDecision();
        }
        model.moveSelectedCoinPiece(decision);
    }

    private void print_Pieces() {
        for (Piece p : model.getCPieces()) {
            Log.d("print_Pieces C", p.getPosition().toString());
        }

        for (Piece p : model.getGPieces()) {
            Log.d("print_Pieces G", p.getPosition().toString());
        }
    }

    private ArrayList<Boolean> priorities_c() {
        ArrayList<Boolean> pris = new ArrayList<Boolean>();
        boolean point1, point2, point3, point4;
        boolean priority = false;
        point1 = false;
        point2 = false;
        point3 = false;
        point4 = false;

        g_pieces = model.getGPieces();
        print_Pieces();

        for (Piece cpiece : model.getCPieces()) {
            for (Piece gpiece : g_pieces) {
                if (cpiece.getPosition().x == gpiece.getPosition().x && cpiece.getPosition().y == gpiece.getPosition().y) {
                    point1 = true;
                }
                if (cpiece.getPosition().x - 1 == gpiece.getPosition().x && cpiece.getPosition().y == gpiece.getPosition().y) {
                    point2 = true;
                }
                if (cpiece.getPosition().x == gpiece.getPosition().x && cpiece.getPosition().y - 1 == gpiece.getPosition().y) {
                    point3 = true;
                }

                if (cpiece.getPosition().x - 1 == gpiece.getPosition().x && cpiece.getPosition().y - 1 == gpiece.getPosition().y) {
                    point4 = true;
                }
            }
            Log.d("Priority", "Point 1" + Boolean.toString(point1));
            Log.d("Priority", "Point 2" + Boolean.toString(point2));
            Log.d("Priority", "Point 3" + Boolean.toString(point3));
            Log.d("Priority", "Point 4" + Boolean.toString(point4));

            if ((point1 && point3) || (point1 && point2)) {
                priority = true;
            }

            if ((point4 && point3) || (point4 && point2)) {
                priority = true;
            }

            if(cpiece.getPosition().x > 6 || cpiece.getPosition().x < 1){
                priority = true;
            }

            if(cpiece.getPosition().y > 6 || cpiece.getPosition().y < 1){
                priority = true;
            }

            if (priority) {
                pris.add(true);
            } else {
                pris.add(false);
            }
            priority = false;
            point1 = false;
            point2 = false;
            point3 = false;
            point4 = false;


        }

        int c = 0;
        int false_c = 0;
        for (Boolean pri : pris) {
            c++;
            Log.d("Priority" + Integer.toString(c), pri.toString());
            if (!pri) {
                false_c++;
            }

        }

        if(false_c == pris.size()){
            for (int i = 0; i < pris.size(); i ++) {
               pris.set(i,true);
            }
        }
        return pris;
    }



    private Point coinDecision(){
        Point decision = null;

        ArrayList<Point> potMoves = null;model.getPotentialGuerillaMoves();
        ArrayList<Point> OriginalPositions= new ArrayList<Point>();
        ArrayList<Piece> Gpieces = new ArrayList<Piece>();
        ArrayList<Node >firstLevel = new ArrayList<Node>();
        ArrayList<Point> Selectedpieces = new ArrayList<Point>();
        ArrayList<Integer> selint = new ArrayList<Integer>();
        ArrayList<Boolean> pris = new ArrayList<Boolean>();
        ArrayList<BinarySort> choices = new ArrayList<BinarySort>();

        pris = priorities_c();

        for (BoardModel.Piece p: model.getCPieces()){
            int x = 0;
            int y = 0;


            x += p.getPosition().x;
            y += p.getPosition().y;

            OriginalPositions.add(new Point(x,y));
        }

        ArrayList<Array> moves = new ArrayList<Array>();

        int counter = 0;
        int bscounter = 0;
        long startTime = System.currentTimeMillis();
        long elapsedTime = 0;
        for(int i = 0; i < OriginalPositions.size(); i++) {
            Log.d("CPC OPS", Integer.toString(OriginalPositions.size()));
            if (pris.get(i) == true) {
                choices.add(new BinarySort());

                Piece piece = model.getCPieces().get(i);
                Log.d("CPC PP", model.getCPieces().get(i).getPosition().toString());
                Log.d("CPC OPP", OriginalPositions.get(i).toString());
                Log.d("CPC B", Integer.toString(counter));
                if (model.getCoinPotentialMoves(piece) == null) {
                    Log.d("CPC", "Piece unavaliable");
                }
                Log.d("CPC Moves", Integer.toString(model.getCoinPotentialMoves(piece).size()));
                Log.d("CPC OP", Integer.toString(OriginalPositions.size()));
                for (Point point : model.getCoinPotentialMoves(piece)) {
                    Log.d("CPC F", Integer.toString(counter));
                    Node newNode = new Node(model, point, null, ' ', agentPlayer, new ArrayList<Point>(), startTime, model.getCPieces().size());
                    Selectedpieces.add(OriginalPositions.get(i));
                    newNode.setState('c');
                    newNode.makeCMove(piece);
                    choices.get(bscounter).push(newNode);
                    //newNode.Expand(maxDepth, 0);
                    //selint.add(i + 0);
                    //firstLevel.add(newNode);


                    Log.d("CPC Size", Integer.toString(model.getCPieces().size()));

                    while (model.getCPieces().size() < OriginalPositions.size()) {
                        model.RestorePiece();
                        Log.d("CPC A", "Added Piece");
                    }

                    for (int x = 0; x < OriginalPositions.size(); x++) {
                        model.getCPieces().get(x).setPosition(OriginalPositions.get(x));
                    }

                    Log.d("CPC", Integer.toString(counter));
                }
                bscounter++;
                selint.add(i + 0);
                Log.d("CPC O", Integer.toString(counter));


            }
            counter++;
        }
        /*Node max = firstLevel.get(0);
        int maxIterator = selint.get(0);
        counter = 0;
        for(Node n: firstLevel){
            if (n.getReward() > max.getReward()){
                max = n;
                maxIterator = 0;
                maxIterator += selint.get(counter);
                elapsedTime = (((System.currentTimeMillis() - startTime) / 1000)%60);
                if(elapsedTime >= 10){
                    break;
                }
            }
            counter ++;
        }*/

        Node max = choices.get(0).GetSort().get(0);
        int maxIterator = selint.get(0);
        counter = 0;
        for (BinarySort bs:choices){
            for (Node n : bs.GetSort()){
                if(n.getReward() >= max.getReward()){
                    n.Expand(maxDepth,0);
                    max = n;
                    maxIterator = selint.get(counter);
                }
            }
            counter++;
        }
        Log.d("CPC FL", Integer.toString(firstLevel.size()));
        Log.d("CPC MS", Integer.toString(maxIterator));
        Log.d("CPC S", OriginalPositions.get(maxIterator).toString());
        model.selectCoinPieceAt(OriginalPositions.get(maxIterator));
        Log.d("CPC D", max.getChoice().toString());
        decision = max.getChoice();

        return decision;
    }

    public Point coinCaptureDecision(){
        Point decision = null;
        ArrayList<Point> potMoves = model.getCoinPotentialMoves(model.getSelectedCoinPiece());
        ArrayList<Point> OriginalPositions= new ArrayList<Point>();
        ArrayList<Node>firstLevel = new ArrayList<Node>();

        for (BoardModel.Piece p: model.getCPieces()){
            int x = 0;
            int y = 0;


            x += p.getPosition().x;
            y += p.getPosition().y;

            OriginalPositions.add(new Point(x,y));
        }


        long startTime = System.currentTimeMillis();
        for (Point p: potMoves){
            Node newNode = new Node(model, p, null, ' ', agentPlayer, new ArrayList<Point>(), startTime,model.getCPieces().size());
            newNode.setState('c');
            newNode.makeCMove(model.getSelectedCoinPiece());
            newNode.Expand(maxDepth,0);

            firstLevel.add(newNode);

            while(model.getCPieces().size() < OriginalPositions.size()){
                model.RestorePiece();
            }

            for (int i = 0; i < OriginalPositions.size(); i++){
                model.getCPieces().get(i).setPosition(OriginalPositions.get(i));
            }
        }

        Node maxNode = firstLevel.get(0);

        for(Node n: firstLevel){
            if(n.getReward() > maxNode.getReward()){
                maxNode = n;
            }
        }

        decision = maxNode.getChoice();
        return decision;
    }

    private Point treeSearch()
    {
        Log.d("makeMove", "start");
        ArrayList<Point> potMoves = model.getPotentialGuerillaMoves();
        ArrayList<Point> OriginalPositions= new ArrayList<Point>();

        for (BoardModel.Piece p: model.getCPieces()){
            int x = 0;
            int y = 0;


            x += p.getPosition().x;
            y += p.getPosition().y;

            OriginalPositions.add(new Point(x,y));
        }

        Log.d("treeSearch", "init");
        BinarySort firstLevel = new BinarySort();
        int nodesChecked = 0;
        int nodesExpanded = 0;
        ArrayList<Point> piecePoints = new ArrayList<Point>();

        Log.d("treeSearch", "entering potential moves");
        int counter = 0;
        for(Piece p: model.getGPieces()){
            piecePoints.add(p.getPosition());
        }
        long startTime = System.currentTimeMillis();
        for (Point p: potMoves){
            Log.d("treePotMoves", Integer.toString(counter));
            Log.d("treeSearch", "potential move");
            Node newNode = new Node(model, p, null, ' ', agentPlayer, piecePoints, startTime,model.getCPieces().size());
            newNode.setState('g');
            newNode.makeGMove();
            Log.d("MainModel", Integer.toString(model.getNumGuerillaPieces()));
            Log.d("agent", "model mem " + model.toString());
            Log.d("agent", "view model mem " + view.getModelString());

            firstLevel.push(newNode);
            counter += 1;

        }
        Log.d("treeSearch", "selecting node");
        Node maxNode = null;
        if(firstLevel.GetSort().size() > 0) {
            maxNode = firstLevel.GetSort().get(0);
        }

        long elapsedTime = 0;

        if(firstLevel.GetSort().size() != 0){
        //Node currentNode = firstLevel.GetSort().get(firstLevel.GetSort().size() - 1);
        for (Node n: firstLevel.GetSort()){
            if(n.getReward() >= maxNode.getReward()){
                n.Expand(maxDepth,0);
                maxNode = n;


            }
            elapsedTime = (((System.currentTimeMillis() - startTime) / 1000)%60);

            if(elapsedTime >= 5){
                break;
            }

            Log.d("TIME", Long.toString((elapsedTime / 1000)%60));
        }


        }



        float max = 0;

        Log.d("treeSearch, choice size", Integer.toString(firstLevel.GetSort().size()));

        if(maxNode != null) {
            Log.d("treeSearch, choice", maxNode.getChoice().toString());
        }
        Log.d("treeSearch", "Nodes expanded: " + nodesExpanded);
        Log.d("treeSearch", "Nodes checked " + nodesChecked);


        for(int i = 0; i < OriginalPositions.size(); i++){
            c_pieces.get(i).setPosition(OriginalPositions.get(i));
        }
        if(maxNode != null) {
            return maxNode.getChoice();
        }else{
            return null;
        }
    }

    public void coinTake(){
        for(Point p: model.getCoinPotentialMoves(model.getSelectedCoinPiece())) {
            if(model.selectedCoinPieceHasValidMoves()) {
                model.moveSelectedCoinPiece(p);
            }
        }
    }




}
