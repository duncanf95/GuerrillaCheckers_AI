package com.CardboardGames.DataStructures;

import android.util.Log;
import com.CardboardGames.AI.Node;

import java.util.ArrayList;

public class BinarySort {

    private ArrayList<Node> NodeList= new ArrayList<Node>();

    public BinarySort(){

    }

    public void push(Node node){
        ArrayList<Node> half1 = new ArrayList<Node>();
        ArrayList<Node> half2 = new ArrayList<Node>();

        if(NodeList.size() == 0){
            NodeList.add(node);
        }else if(NodeList.size() == 1){
            if(NodeList.get(0).getReward() >= node.getReward()){
                NodeList.add(node);
            }else{
                half1 = NodeList;
                half2.add(node);

                NodeList.addAll(half1);
                NodeList.addAll(half2);
            }
        }else{

            int start = 0;
            int end = NodeList.size() - 1;

            int mid = (start + end)/2;
            int add = (start + end)/2;

            boolean found = false;


            while (!found){
                //Log.d("mid", String.valueOf(mid));
               // Log.d("add", String.valueOf(add));
                if(mid == 0){
                    half1.add(node);
                    half2 = NodeList;

                    NodeList= new ArrayList<Node>();

                    NodeList.addAll(half1);
                    NodeList.addAll(half2);
                    found = true;
                    //Log.d("push: ", "found");
                }else if(mid == NodeList.size() - 1){
                    half1 = NodeList;
                    half2.add(node);

                    NodeList= new ArrayList<Node>();

                    NodeList.addAll(half1);
                    NodeList.addAll(half2);
                    found = true;
                    //Log.d("push: ", "found");
                }
                else if(NodeList.get(mid).getReward() <= node.getReward() &&
                        NodeList.get(mid + 1).getReward() >= node.getReward()){
                    half1 = new ArrayList<Node>(NodeList.subList(0,mid)) ;
                    half2 = new ArrayList<Node>(NodeList.subList(mid + 1,NodeList.size()));

                    half1.add(node);

                    NodeList= new ArrayList<Node>();

                    NodeList.addAll(half1);
                    NodeList.addAll(half2);
                    found = true;
                    //Log.d("push: ", "found");

                }else if(NodeList.get(mid).getReward() == node.getReward()){
                    half1 = new ArrayList<Node>(NodeList.subList(0,mid)) ;
                    half2 = new ArrayList<Node>(NodeList.subList(mid + 1,NodeList.size()));

                    half1.add(node);

                    NodeList= new ArrayList<Node>();

                    NodeList.addAll(half1);
                    NodeList.addAll(half2);
                    found = true;
                    //Log.d("push: ", "found");
                }else if(NodeList.get(mid).getReward() > node.getReward()){
                    add = add/2;
                    if(add == 0) add = 1;
                    mid = mid - add;
                    //Log.d("push: ", "infinite");
                }else if(NodeList.get(mid).getReward() < node.getReward()){
                    add = add/2;
                    if(add == 0) add = 1;
                    mid = mid + add;
                    //Log.d("push: ", "infinite");
            }
            }
            Log.d("push", "exit loop");

        }
    }

    public void pop(Node n){
        NodeList.remove(n);
    }

    public ArrayList<Node> GetSort(){return NodeList;}

}
