package com.CardboardGames.AI;

import android.graphics.Point;
import android.util.Log;
import com.CardboardGames.Models.BoardModel;

import java.util.ArrayList;
import java.util.Random;

public class BoardAnalyser {
    private BoardModel bm;
    public BoardAnalyser(BoardModel boardModel){
        bm = boardModel;
    }

    public float gAnalyseGuerilla(ArrayList<Point> in_gPoints, Point choice,
                          boolean takeC, boolean takeG, int pieceRatio){
        int x = choice.x;
        int y = choice.y;
        int average = 1;
        int differenceX = 0, differenceY = 0;
        Point  changeX, changeY;
        int counterPlus = 1, counterMinus = 1;
        boolean found = false;
        int result = 2000;
        if(takeC){
            result += 2000;
            Log.d("gAnalyse", "take available");
        }else if(takeG){
            result -= 532;
        }
        if(in_gPoints.size() < 2){

            result += 187;

            if(choice.x < 3){
                differenceX = 3 - choice.x;
            }else if(choice.x > 3){
                differenceX = 3 + choice.x;
            }else{
                result += 279;
            }

            if(choice.y < 3){
                differenceX = 3 - choice.y;
            }else if(choice.y > 3){
                differenceX = 3 + choice.y;
            }else{
                result += 279;
            }

            differenceX = differenceX * 35;
            differenceY = differenceY * 35;

            result -= differenceX + differenceY;

        }

        else{
            while(!found){
                changeX = new Point(x - counterMinus, y);
                changeY = new Point(x,y-counterMinus);

                if(changeX.x > 0){
                    if(in_gPoints.contains(changeX)){
                        result -= 179;
                    }
                }

                if(changeY.y > 0){
                    if(in_gPoints.contains(changeY)){
                        result -= 179;
                    }
                }

                if(!(in_gPoints.contains(changeX))&&!(in_gPoints.contains(changeY))) {
                    result += 57 * counterMinus;
                    found = true;
                }

                counterMinus ++;
            }

            found = false;
            while(!found){
                changeX = new Point(x + counterPlus, y);
                changeY = new Point(x,y+counterPlus);

                if(changeX.x < 7){
                    if(in_gPoints.contains(changeX)){
                        result -= 179;
                    }
                }

                if(changeY.y < 7){
                    if(in_gPoints.contains(changeY)){
                        result -= 179;
                    }
                }

                if(!(in_gPoints.contains(changeX))&&!(in_gPoints.contains(changeY))) {
                    result -= 57 * counterPlus;
                    found = true;
                }
                Log.d("gAnalyseGuerilla", "centre loop");
                counterPlus ++;
            }
        }

        if(choice.x < 3){
            differenceX = 3 - choice.x;
        }else if(choice.x > 3){
            differenceX = 3 + choice.x;


        if(choice.y < 3){
            differenceX = 3 - choice.y;
        }else if(choice.y > 3) {
            differenceX = 3 + choice.y;

        }

        if(differenceX != 0 && differenceY != 0){
            average = (differenceX + differenceY)/2;
        }
        }
        return (result * pieceRatio) * average;
    }

    public float gAnalyseCoin(Point choice, boolean cTake, boolean gTake, int ratio){
       float result = 4000;
       Random rand = new Random();

       int average = 1;
       int differenceX = 1, differenceY = 1;


       int distance = 0;
       if(choice .y < 3){
           result += (3 - choice.y) * 50;
           differenceY = 3 - choice.y;
       }else if(choice.x < 3){
           result += (3 - choice.x) * 50;
           differenceX = 3 - choice.x;
       }

       if (choice.y > 3){
           result += (choice.y - 3) * 50;
           differenceY = choice.y - 3;
       }else if(choice.x > 3){
           result += (choice.x - 3) * 50;
           differenceX = choice.x - 3;
       }



       if(gTake){
           result -=600;
       }

       average = (differenceX + differenceY)/2;


       //result = rand.nextInt(1000);

       return (result - ((average * 219))) * ratio;
    }

    public float cAnalyseGuerilla(ArrayList<Point> in_gPoints, Point choice,
                                  boolean takeC, boolean takeG, int pieceRatio, int diffamount){
        int x = choice.x;
        int y = choice.y;
        int average = 1;
        int differenceX = 0, differenceY = 0;
        Point  changeX, changeY;
        int counterPlus = 1, counterMinus = 1;
        boolean found = false;
        int result = 1750;
        if(takeC){
            result -= 3000;
            Log.d("gAnalyse", "take available");
        }else {
            //result += 3000;
        }
        if(in_gPoints.size() < 2){

            result += 187;

            if(choice.x < 3){
                differenceX = 3 - choice.x;
            }else if(choice.x > 3){
                differenceX = 3 + choice.x;
            }else{
                result -= 279;
            }

            if(choice.y < 3){
                differenceX = 3 - choice.y;
            }else if(choice.y > 3){
                differenceX = 3 + choice.y;
            }else{
                result -= 279;
            }

            differenceX = differenceX * 35;
            differenceY = differenceY * 35;

            result += differenceX + differenceY;

        }

        if(bm.getGuerillaPieceAt(choice.x + 1, choice.y + 1) != null){
            Log.d("CGCheck", "true");

        }

        else{
            while(!found){
                changeX = new Point(x - counterMinus, y);
                changeY = new Point(x,y-counterMinus);

                if(changeX.x > 0){
                    if(in_gPoints.contains(changeX)){
                        result += 179;
                    }
                }

                if(changeY.y > 0){
                    if(in_gPoints.contains(changeY)){
                        result += 179;
                    }
                }

                if(!(in_gPoints.contains(changeX))&&!(in_gPoints.contains(changeY))) {
                    result += 57 * counterMinus;
                    found = true;
                }

                counterMinus ++;
            }

            found = false;
            while(!found){
                changeX = new Point(x + counterPlus, y);
                changeY = new Point(x,y+counterPlus);

                if(changeX.x < 7){
                    if(in_gPoints.contains(changeX)){
                        result += 179;
                    }
                }

                if(changeY.y < 7){
                    if(in_gPoints.contains(changeY)){
                        result += 179;
                    }
                }

                if(!(in_gPoints.contains(changeX))&&!(in_gPoints.contains(changeY))) {
                    result += 57 * counterPlus;
                    found = true;
                }
                Log.d("gAnalyseGuerilla", "centre loop");
                counterPlus ++;
            }
        }

        if(choice.x < 3){
            differenceX = 3 - choice.x;
        }else if(choice.x > 3){
            differenceX = 3 + choice.x;


            if(choice.y < 3){
                differenceX = 3 - choice.y;
            }else if(choice.y > 3) {
                differenceX = 3 + choice.y;

            }

            if(differenceX != 0 && differenceY != 0){
                average = (differenceX + differenceY)/2;
            }
        }


        diffamount += 1;
        float output = ((result * pieceRatio) / average)/ diffamount;
        Log.d("cAnalyseG: ", Float.toString(output));
        return output;
    }

    public float cAnalyseCoin(Point choice, boolean cTake, boolean gTake, int ratio, int difamount, boolean win, int numCoin, int numG){
        float result = 350;
        Random rand = new Random();
        ArrayList<Boolean> potTaks = priorities_c(choice);

        int average = 1;
        int differenceX = 1, differenceY = 1;

        if(bm.getGuerillaPieceAt(choice.x + 1, choice.y + 1) != null){
            Log.d("CGCheck", "true");

        }

        for(Boolean b : potTaks){
            if(b){
                result -= 1000;
            }
        }


        int distance = 0;
        if(choice .y < 3){
            result += (3 - choice.y) * 190;
            differenceY = 3 - choice.y;
        }else if(choice.x < 3){
            result += (3 - choice.x) * 190;
            differenceX = 3 - choice.x;
        }

        if (choice.y > 3){
            result += (choice.y - 3) * 190;
            differenceY = choice.y - 3;
        }else if(choice.x > 3){
            result += (choice.x - 3) * 190;
            differenceX = choice.x - 3;
        }

        if(choice.x == 6 || choice.y == 6 || choice.x == 0 || choice.y == 0){
            result -=500;
        }



        if(gTake){
            result +=1000;
        }
        if(cTake){
            result -= 500;
        }
        if(win){
            result+=4000;
        }

        average = (differenceX + differenceY)/2;


        //result = rand.nextInt(1000);
        difamount += 1;
        if(numG == 0){
            numG += 1;
        }
        float output = (((result)/ratio) / (average * 219))/ difamount * ((numCoin * 10))*750;
        Log.d("cAnalyseC: ", Float.toString(output));
        return output;
    }

    private ArrayList<Boolean> priorities_c(Point c_choice) {
        BoardModel model = bm;
        ArrayList<Boolean> pris = new ArrayList<Boolean>();
        ArrayList<BoardModel.Piece> g_pieces = new ArrayList<BoardModel.Piece>();
        boolean point1, point2, point3, point4;
        boolean priority = false;
        point1 = false;
        point2 = false;
        point3 = false;
        point4 = false;

        g_pieces = model.getGPieces();
        //print_Pieces();


            for (BoardModel.Piece gpiece : g_pieces) {
                if (c_choice.x == gpiece.getPosition().x && c_choice.y == gpiece.getPosition().y) {
                    point1 = true;
                }
                if (c_choice.x - 1 == gpiece.getPosition().x && c_choice.y == gpiece.getPosition().y) {
                    point2 = true;
                }
                if (c_choice.x == gpiece.getPosition().x && c_choice.y - 1 == gpiece.getPosition().y) {
                    point3 = true;
                }

                if (c_choice.x - 1 == gpiece.getPosition().x && c_choice.y - 1 == gpiece.getPosition().y) {
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

            if(c_choice.x > 6 || c_choice.x < 1){
                priority = true;
            }

            if(c_choice.y > 6 || c_choice.y < 1){
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

}
