//Bundle uploaded at 12/12/2022 19:44:08
import java.util.*;
import java.util.stream.Collectors;
/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class Player {
    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        int width = in.nextInt();
        int height = in.nextInt();
        // game loop
        while (true) {
            int myMatter = in.nextInt();
            int oppMatter = in.nextInt();
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    int scrapAmount = in.nextInt();
                    int owner = in.nextInt(); // 1 = me, 0 = foe, -1 = neutral
                    int units = in.nextInt();
                    int recycler = in.nextInt();
                    int canBuild = in.nextInt();
                    int canSpawn = in.nextInt();
                    int inRangeOfRecycler = in.nextInt();
                }
            }
            // Write an action using System.out.println()
            // To debug: System.err.println("Debug messages...");
            System.out.println("WAIT");
        }
    }
}
