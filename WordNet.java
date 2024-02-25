import java.util.ArrayList;
import java.util.HashMap;

import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdOut;

public class WordNet {
   private final ArrayList<String> arr = new ArrayList<String>();
   private final HashMap<String, ArrayList<Integer>> dic = new HashMap<String, ArrayList<Integer>>(); 
   private final SAP sip;

   // constructor takes the name of the two input files
   public WordNet(String synsets, String hypernyms) {
    In in1 = new In(synsets);
    while (in1.hasNextLine()) {
      String s = in1.readLine();
      String[] ab = s.split(",");

      int i = Integer.parseInt(ab[0]);
      arr.add(i, ab[1]);   
      String[] bc = ab[1].split(" ");

      for (String a : bc) {
         ArrayList<Integer> ai;
         if (dic.containsKey(a)) {
            ai = dic.get(a);
         }
         else {
            ai = new ArrayList<Integer>();
         }
         ai.add(i);
         dic.put(a, ai); 
      }
    }

    Digraph digi = new Digraph(arr.size());

    In in2 = new In(hypernyms);
    while (in2.hasNextLine()) {
      String s = in2.readLine();
      String[] ab = s.split(",");

      int i = Integer.parseInt(ab[0]);
      for (int j = 1; j < ab.length; j++) {
        digi.addEdge(i, Integer.parseInt(ab[j]));
      }
      
    }
    

    // test for only one root
   boolean rooted = false;
   for (int i = 0; i < digi.V(); i++) {
      if (digi.outdegree(i) == 0) {
         if (rooted) { // if there's already a root, 
            throw new IllegalArgumentException();
         }
         else {
            rooted = true;
         }

         // test for DAG
         if (cycle(digi, i)) {
            throw new IllegalArgumentException();
         }
      }
   }
   if (!rooted) {
   throw new IllegalArgumentException();
   }

   // construct the SAP
   sip = new SAP(digi);

   }

   private class Node {
      int vertex;
      int depth;
      HashMap<Integer, Integer> hash;

      Node(int v, int d, HashMap<Integer, Integer> h) {
          vertex = v;
          depth = d;
          hash = h;
      }
   }
   private boolean cycle(Digraph dig, int w)
    {
      HashMap<Integer, Integer> hash = new HashMap<Integer, Integer>();
      hash.put(w, 0);
      Stack<Node> s = new Stack<Node>();
      s.push(new Node(w, 0, hash));

      while (!s.isEmpty()) {
         Node top = s.pop();
                     
         for (int i : dig.adj(top.vertex)) {  
               if (!top.hash.containsKey(i)) { // check for cycle
                  HashMap<Integer, Integer> h = new HashMap<Integer, Integer>(top.hash);
                  h.put(i, top.depth+1);      
                  s.push(new Node(i, top.depth+1, h));
               }
               else {
                  return true;
               }
         }
      }

      return false;
   }
   // returns all WordNet nouns
   public Iterable<String> nouns() {
      return dic.keySet();
   }

   // is the word a WordNet noun?
   public boolean isNoun(String word) {
      if (word == null) {
         throw new IllegalArgumentException();
      }
      return dic.containsKey(word); 
   }

   // distance between nounA and nounB (defined below)
   public int distance(String nounA, String nounB) {
      if (nounA == null || nounB == null || !isNoun(nounA) || !isNoun(nounB))
         throw new IllegalArgumentException();
      
      return sip.length(dic.get(nounA), dic.get(nounB));
   }

   // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
   // in a shortest ancestral path (defined below)
   public String sap(String nounA, String nounB) {
      if (nounA == null || nounB == null || !isNoun(nounA) || !isNoun(nounB))
         throw new IllegalArgumentException();

      int an = sip.ancestor(dic.get(nounA), dic.get(nounB));
      if (an != -1) {
         return arr.get(an);
      }
      
      return null;
   }

   // do unit testing of this class
   public static void main(String[] args) {
      WordNet w = new WordNet("synsets.txt", "hypernyms.txt");

      String s1 = "victim";
      String s2 = "lava";

      StdOut.println(s1 + "\t" + s2 + " - " + w.sap(s1, s2) + " (" +w.distance(s1, s2) + ")");

      In in = new In("outcast11.txt");
      String[] nouns = in.readAllStrings();

      int maxdis = Integer.MIN_VALUE;
      String cast = null;
      for (String s : nouns) {
         int dis = 0;
         StdOut.println(s + ":");
         for (String t : nouns) {
             if (!t.equals(s)) {
               StdOut.println("\t" + t + " - " + w.sap(s, t) + " (" +w.distance(s, t) + ")");
               dis += w.distance(s, t);
             }
         }
         StdOut.println("Total distance = " + dis);
         if (dis > maxdis) {
             maxdis = dis;
             cast = s;
         }
     }
     StdOut.println("Outcast is: " + cast);
      

   }
}
