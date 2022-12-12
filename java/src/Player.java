import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/

class Player {

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        int width = in.nextInt();
        int height = in.nextInt();
        boolean firstTurn = true;

        // game loop
        while (true) {
            int myMatter = in.nextInt();
            int oppMatter = in.nextInt();
            int myRecyclerCount = 0;
            int myCellCount = 0;
            int enemyCellCount = 0;
            Boolean buildRecycler = false;
            List<Cell> cells = new ArrayList<Cell>();
            List<Robot> robots = new ArrayList<Robot>();
            List<String> actions = new ArrayList<String>();
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int scrapAmount = in.nextInt();
                    int owner = in.nextInt(); // 1 = me, 0 = foe, -1 = neutral
                    int units = in.nextInt();
                    int recycler = in.nextInt();
                    int canBuild = in.nextInt();
                    int canSpawn = in.nextInt();
                    int inRangeOfRecycler = in.nextInt();
                    cells.add(
                            new Cell(x, y, scrapAmount, owner, units, recycler, canBuild, canSpawn, inRangeOfRecycler));
                    if (owner == 1 && recycler == 0) {
                        robots.add(new Robot(x, y, owner, units, canBuild, canSpawn, inRangeOfRecycler));
                        //TODO: bug robots are actually owner tiles? :D
                    }
                    if (owner == 1 && recycler == 1) {
                        myRecyclerCount++;
                    }
                    if (owner == 1) {
                        myCellCount++;
                    } else if (owner == 0) {
                        enemyCellCount++;
                    }

                }
            }
            if ((myRecyclerCount < 1 && myCellCount > enemyCellCount + 4) || firstTurn || myRecyclerCount < 10 ) {
                // iterate through cells with owner 1 and units 0 and recycler 0 and canBuild 1
                // and make a list of them
                List<Cell> buildCellsCandidates = new ArrayList<Cell>();
                for (Cell cell : cells) {
                    if (cell.owner == 1 && cell.units == 0 && cell.recycler == 0 && cell.canBuild == 1) {
                        buildCellsCandidates.add(cell);
                    }
                }

                // iterate through buildCellsCandidates and sum up the scrapAmount of the cells
                // in range of recycler
                int maxScrapAmount = 0;
                int maxScrapAmountCell = 0;
                for (Cell cell : buildCellsCandidates) {
                    int scrapAmount = 0;
                    for (Cell cell2 : cells) {
                        if (Math.abs(cell.x - cell2.x) + Math.abs(cell.y - cell2.y) == 1) {
                            if(cell2.owner == 0){
                                scrapAmount += cell2.scrapAmount + 10;
                            }else if(cell2.recycler == 0 && cell2.inRangeOfRecycler == 0){
                                scrapAmount += cell2.scrapAmount;
                            }else{
                                //check if there is a recycler in range of recycler
                                scrapAmount = -1000;
                            }                            
                        }
                    }
                    
                    
                    if (scrapAmount > maxScrapAmount && cell.scrapAmount > 9) {
                        maxScrapAmount = scrapAmount;
                        maxScrapAmountCell = cells.indexOf(cell);
                    }
                }
                // build recycler on cell with maxScrapAmountCell
                if (maxScrapAmount > 20) {
                    actions.add("BUILD " + cells.get(maxScrapAmountCell).x + " " + cells.get(maxScrapAmountCell).y);
                    buildRecycler = true;
                }

            }

            //make list of cells where I'm about to move
            List<Cell> toMoveCells = new ArrayList<Cell>();

            // move each robot to the closest Cell with owner -1 or 0. Do not move if there
            // is no scrap. Do not move if there is a recycler in range. Don't move if there
            // is a robot in range. move to Cell where move is already planned.
            for (Robot robot : robots) {

                if (robot.units > 0) {
                    int closestCell = 0;
                    int closestDistance = 1000;
                    for (Cell cell : cells) {
                        //check that cell is not in toMoveCells
                        if(toMoveCells.contains(cell)){
                            continue;
                        }
                        if (cell.scrapAmount == 0) {
                            continue;
                        }
                        if (cell.owner == -1 || cell.owner == 0 || (cell.owner == -1 && cell.units <= 1)) {
                            int distance = Math.abs(robot.x - cell.x) + Math.abs(robot.y - cell.y);
                            if (distance < closestDistance) {
                                closestDistance = distance;
                                closestCell = cells.indexOf(cell);
                            }
                        }
                    }
                    actions.add(
                            "MOVE " + robot.units + " " + robot.x + " " + robot.y + " " + cells.get(closestCell).x + " "
                                    + cells.get(closestCell).y);
                    toMoveCells.add(cells.get(closestCell));
                }

            }

            // spawn a robot on each cell with owner 1 and units 0 and recycler 0 and
            // canSpawn and myMatter > 3 and scrapAmount > 0
            // check how many times myMatter can be divided by 6
            if(buildRecycler){
                myMatter -= 10;
            }
            int spawnCount = myMatter / 10;

            // make list of cells where I can spawn
            List<Cell> spawnCells = new ArrayList<Cell>();
            for (Cell cell : cells) {
                if (cell.owner == 1 && cell.units == 0 && cell.recycler == 0 && cell.canSpawn == 1
                        && cell.scrapAmount > 0 && !firstTurn) {
                    spawnCells.add(cell);
                }
            }

            // go through spawnCells and find cell next to cell with owner 0 or -1 and
            // scrapAmount > 0
            List<Cell> spawnCellsCandidates = new ArrayList<Cell>();
            for (Cell cell : spawnCells) {
                for (Cell cell2 : cells) {
                    if (Math.abs(cell.x - cell2.x) + Math.abs(cell.y - cell2.y) == 1
                            && (cell2.owner == 0 || cell2.owner == -1) && cell2.scrapAmount > 0) {
                        spawnCellsCandidates.add(cell);
                    }
                }
            }
            //spawn in every cell in spawnCellsCandidates
            for (Cell cell : spawnCellsCandidates) {
                if (spawnCount > 0) {
                    actions.add("SPAWN 1 " + cell.x + " " + cell.y);
                    spawnCount--;
                }
            }

            //if spawnCount > 0, spawn in every cell in spawnCells
            for (Cell cell : spawnCells) {
                if (spawnCount > 0) {
                    actions.add("SPAWN 1 " + cell.x + " " + cell.y);
                    spawnCount--;
                }
            }
            

            // if actions is empty, print WAIT else print all actions separated by semicolon
            if (actions.isEmpty()) {
                System.out.println("WAIT");
            } else {
                for (String action : actions) {
                    System.out.print(action + ";");
                }
                System.out.println();
            }
            firstTurn = false;
        }
    }
}

class Cell {
    int x;
    int y;
    int scrapAmount;
    int owner;
    int units;
    int recycler;
    int canBuild;
    int canSpawn;
    int inRangeOfRecycler;

    public Cell(int x, int y, int scrapAmount, int owner, int units, int recycler, int canBuild, int canSpawn,
            int inRangeOfRecycler) {
        this.x = x;
        this.y = y;
        this.scrapAmount = scrapAmount;
        this.owner = owner;
        this.units = units;
        this.recycler = recycler;
        this.canBuild = canBuild;
        this.canSpawn = canSpawn;
        this.inRangeOfRecycler = inRangeOfRecycler;
    }
}

class Robot {
    int x;
    int y;
    int owner;
    int units;
    int canBuild;
    int canSpawn;
    int inRangeOfRecycler;

    public Robot(int x, int y, int owner, int units, int canBuild, int canSpawn, int inRangeOfRecycler) {
        this.x = x;
        this.y = y;
        this.owner = owner;
        this.units = units;
        this.canBuild = canBuild;
        this.canSpawn = canSpawn;
        this.inRangeOfRecycler = inRangeOfRecycler;
    }
}
