import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;
import java.util.HashMap;
import edu.princeton.cs.algs4.Stack;

public class SAP {
    private final Digraph dig;

    private int min = Integer.MAX_VALUE;
    private int an = -1;
    
    private class Pair {
        int v;  // v and ancestor
        int w;  // w and length
        Pair(int v, int w) {
            this.v = v;
            this.w = w;
        }
    }

    private final HashMap<String, Pair> cache;

    // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph G) {
        if (G == null) {
            throw new IllegalArgumentException();
        }
        dig = G;
        cache = new HashMap<String, Pair>();
    }
 
    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {
        ancestor(v, w);

        if (an == -1) 
            return -1;
        else
            return min;
    }

    private HashMap<Integer, Integer> dfs(int v) {
        HashMap<Integer, Integer> hash = new HashMap<Integer, Integer>();
        hash.put(v, 0);
        Stack<Pair> s = new Stack<Pair>();
        s.push(new Pair(v, 0));

        while (!s.isEmpty()) {
            Pair top = s.pop();
            
            for (int i : dig.adj(top.v)) {
                if (!hash.containsKey(i)) { // check for cycles
                    hash.put(i, top.w+1);
                    s.push(new Pair(i, top.w+1));
                }
                else {
                    // update depth if we find a shorter path
                    if (hash.get(i) > top.w+1)
                        hash.put(i, top.w+1);
                }
            }
        }
        return hash;
    }

    public int ancestor(int v, int w) {
        if (v < 0 || v > dig.V() || w < 0 || w > dig.V())
            throw new IllegalArgumentException();

        // order v and w
        if (v > w) {
            int temp = v;
            v = w;
            w = temp;
        }
        String nodes = v + "-" + w;

        // search cache
        if (cache.containsKey(nodes)) {
            min = cache.get(nodes).w;
            an = cache.get(nodes).v;

            return an;
        }

        // find all parents of v and w
        HashMap<Integer, Integer> vParent = dfs(v);
        HashMap<Integer, Integer> wParent = dfs(w);

        
        min = Integer.MAX_VALUE;
        an = -1;
        
        // find intersection of the set
        for (int i : vParent.keySet()) {
            if (wParent.containsKey(i)) {
                int lv = vParent.get(i);
                int lw = wParent.get(i);
                int len = lv + lw;
                if (len < min) {
                    min = len;
                    an = i;
                }
            }
        }

        // put in cache
        cache.put(nodes, new Pair(an, min));
        return an;
    }

    // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        if (ancestor(v, w) != -1)
            return min;

        return -1;
    }
 
    // a common ancestor that participates in shortest ancestral path; -1 if no such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        if (v == null || w == null)
            throw new IllegalArgumentException();

        int len = Integer.MAX_VALUE;
        int ver = -1;

        for (Integer n : v) {
            for (Integer p: w) {
                if (n == null || p == null)
                    throw new IllegalArgumentException();
                ancestor(n, p);
                if (an != -1) {
                    if (min < len) {
                        len = min;
                        ver = an;
                    }
                }
            }
        }

        min = len;
        an = ver;

        return an;
    }
 
    // do unit testing of this class
    public static void main(String[] args) {
        In in = new In("digraph2.txt");
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);
        while (!StdIn.isEmpty()) {
            int v = StdIn.readInt();
            int w = StdIn.readInt();
            int length   = sap.length(v, w);
            int ancestor = sap.ancestor(v, w);
            StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
        }
    }
 }
