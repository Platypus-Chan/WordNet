import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class Outcast {
    private final WordNet net;
    // constructor takes a WordNet object
    public Outcast(WordNet wordnet) {
        if (wordnet == null)
            throw new IllegalArgumentException();
        net = wordnet;
    }  

    // given an array of WordNet nouns, return an outcast       
    public String outcast(String[] nouns) {
        int maxdis = Integer.MIN_VALUE;
        String cast = null;

        for (String s : nouns) {
            int dis = 0;
            for (String t : nouns) {
                if (!t.equals(s)) {
                    dis += net.distance(s, t);
                }
            }
            if (dis > maxdis) {
                maxdis = dis;
                cast = s;
            }
        }

        return cast;
    }
    public static void main(String[] args) {
        WordNet wordnet = new WordNet("synsets.txt", "hypernyms.txt");
        Outcast outcast = new Outcast(wordnet);
        
        In in = new In("outcast5.txt");
        String[] nouns = in.readAllStrings();
        StdOut.println("outcast5.txt" + ": " + outcast.outcast(nouns));

        in = new In("outcast8.txt");
        nouns = in.readAllStrings();
        StdOut.println("outcast8.txt" + ": " + outcast.outcast(nouns));

        in = new In("outcast11.txt");
        nouns = in.readAllStrings();
        StdOut.println("outcast11.txt" + ": " + outcast.outcast(nouns));
        
    }
}
