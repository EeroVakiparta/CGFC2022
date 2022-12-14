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
            int buildRecycler = 0;
            int enemyUnitCount = 0;
            int myUnitCount = 0;
            int enemyRecyclerCount = 0;
            int mapMatrix[][] = new int[width][height];
            //System.err.println("width: " + width + " height: " + height);
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
                    //input integers to mapMatrix. If recycler is 1, set to 0. If scrapAmount is 0, set to 0.If owner is 1, set to 3. Otherwise, set to 2.
                    if (recycler == 1) {
                        mapMatrix[x][y] = 0; //recycler blocks
                    } else if (scrapAmount == 0) {
                        mapMatrix[x][y] = 0; //empty cells block
                    } else if (owner == 1) {
                        mapMatrix[x][y] = 3; //my cells are passable
                    } else {
                        mapMatrix[x][y] = 2; //enemy cells and neutral cells are destinations
                    }
                    cells.add(
                            new Cell(x, y, scrapAmount, owner, units, recycler, canBuild, canSpawn, inRangeOfRecycler));
                    if (owner == 1 && recycler == 0) {
                        robots.add(new Robot(x, y, owner, units, canBuild, canSpawn, inRangeOfRecycler));
                        // TODO: bug robots are actually owner tiles? :D
                    }
                    if (owner == 1 && recycler == 1) {
                        myRecyclerCount++;
                    }
                    if (owner == 1) {
                        myCellCount++;
                    } else if (owner == 0) {
                        enemyCellCount++;
                    }
                    if (owner == 0 && units > 0) {
                        enemyUnitCount += units;
                    }
                    if (owner == 1 && units > 0) {
                        myUnitCount += units;
                    }
                    if (owner == 0 && recycler == 1) {
                        enemyRecyclerCount++;
                    }
                }
            }
            //print out mapMatrix
            //Utils.printOutMapMatrix(mapMatrix, width, height);

            System.err.println("myR: " + myRecyclerCount + " enemyRec: " + enemyRecyclerCount + " enemyUnit: " + enemyUnitCount + " myUnit: " + myUnitCount);
            int trueCellCount = myCellCount - myRecyclerCount;
            if (trueCellCount > enemyCellCount && myRecyclerCount * 10 + myUnitCount * 10 + myMatter > enemyRecyclerCount * 10 + enemyUnitCount * 10 + oppMatter) {
                System.err.println("I think I am winning?");
            } else {
                System.err.println("Enemy is winning?");
            }

            // make List of buildCells
            List<Cell> buildCells = new ArrayList<Cell>();

            if ((myRecyclerCount < 1 || (myCellCount > enemyCellCount + 20 && myRecyclerCount < 5) || firstTurn)) {
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
                    int battleValue = 0;
                    for (Cell cell2 : cells) {
                        if (Math.abs(cell.x - cell2.x) + Math.abs(cell.y - cell2.y) == 1) {
                            if (cell2.units >= 1 && cell2.owner == 0) {
                                scrapAmount += cell2.scrapAmount;
                                battleValue += 20 + cell2.units * 20;
                            } else if (cell2.owner == 0) {
                                scrapAmount += cell2.scrapAmount;
                                battleValue += 10;
                            } else if (cell2.recycler == 0 && cell2.inRangeOfRecycler == 0) {
                                scrapAmount += cell2.scrapAmount;
                            } else {
                                // check if there is a recycler in range of recycler
                                // check if the recycler is mine
                                scrapAmount = -10;

                            }
                        }
                    }

                    // if (scrapAmount > maxScrapAmount && cell.scrapAmount > 9) {
                    // maxScrapAmount = scrapAmount;
                    // maxScrapAmountCell = cells.indexOf(cell);
                    // }
                    cell.scrapValue = scrapAmount;
                    cell.battleValue = battleValue;
                    cell.totalValue = scrapAmount + battleValue;
                    if (cell.battleValue > 0) {
                        //Cell has strategic value
                        buildCells.add(cell);
                    } else {
                        //Cell has no strategic value but has scrap
                        //check if cell has at least 5 scrap
                        if (cell.scrapAmount < 5) {
                            System.err.println("cell has no strategic value and not enough scrap, will not build" + cell.x + " " + cell.y + " scrap: " + cell.scrapAmount);
                        } else {
                            buildCells.add(cell);
                        }
                    }
                }
                // build recycler on cell with maxScrapAmountCell
                // if (maxScrapAmount > 20) {
                // actions.add("BUILD " + cells.get(maxScrapAmountCell).x + " " +
                // cells.get(maxScrapAmountCell).y);
                // buildRecycler = true;
                // }

                // sort buildCells by value
                buildCells.sort((c1, c2) -> c2.totalValue - c1.totalValue);
                //print out top 3 buildCells. Check for size of buildCells first to avoid index out of bounds
                if (buildCells.size() > 0) {
                    System.err.println("1st buildCells: " + buildCells.get(0).x + " " + buildCells.get(0).y + " value: " + buildCells.get(0).totalValue + " scrap: " + buildCells.get(0).scrapAmount + " battle: " + buildCells.get(0).battleValue);
                }
                if (buildCells.size() > 1) {
                    System.err.println("2nd buildCells: " + buildCells.get(1).x + " " + buildCells.get(1).y + " value: " + buildCells.get(1).totalValue + " scrap: " + buildCells.get(1).scrapAmount + " battle: " + buildCells.get(1).battleValue);
                }
                if (buildCells.size() > 2) {
                    System.err.println("3rd buildCells: " + buildCells.get(2).x + " " + buildCells.get(2).y + " value: " + buildCells.get(2).totalValue + " scrap: " + buildCells.get(2).scrapAmount + " battle: " + buildCells.get(2).battleValue);
                }

                if (buildCells.size() > 1) {
                    //check if any of the buildCells are in closer than 2 from each other
                    //if so, remove the one with the lower totalValue
                    //allow 2 cells close to eachother if they have battleValue
                    for (int i = 0; i < buildCells.size(); i++) {
                        for (int j = i + 1; j < buildCells.size(); j++) {
                            if (Math.abs(buildCells.get(i).x - buildCells.get(j).x) + Math.abs(buildCells.get(i).y - buildCells.get(j).y) < 3) {
                                System.err.println("build candidate cells are too close to eachother, removing one!!");
                                if (buildCells.get(i).totalValue > buildCells.get(j).totalValue) {
                                    if (buildCells.get(j).battleValue <= 0) {
                                        System.err.println("removing cell: " + buildCells.get(j).x + " " + buildCells.get(j).y);
                                        buildCells.remove(j);
                                    }
                                } else {
                                    if (buildCells.get(i).battleValue <= 0) {
                                        System.err.println("removing cell: " + buildCells.get(j).x + " " + buildCells.get(j).y);
                                        buildCells.remove(i);
                                    }
                                }
                            }
                        }
                    }


                }

                int howManyCanBuild = myMatter / 10;
                if (buildCells.size() < howManyCanBuild) {
                    howManyCanBuild = buildCells.size();
                }
                for (int i = 0; i < howManyCanBuild; i++) {
                    System.err.println("Trying to building recycler on cell: " + buildCells.get(i).x + " " + buildCells.get(i).y);
                    if (myCellCount >= enemyCellCount && myRecyclerCount >= enemyRecyclerCount
                            && myUnitCount >= enemyUnitCount && !firstTurn) {
                        actions.add("MESSAGE I feel I am winning");
                    } else {

                        //Check if recycler has path to enemy or neutral territory
                        boolean hasPath = false;
                        //set cell in mapMatrix to 1
                        mapMatrix[buildCells.get(i).x][buildCells.get(i).y] = 1;
                        hasPath = Utils.isPath(mapMatrix, width, height);
                        if (hasPath) {
                            actions.add("BUILD " + buildCells.get(i).x + " " + buildCells.get(i).y);
                        } else {
                            System.err.println("No path to enemy or neutral territory, will not build" + buildCells.get(i).x + " " + buildCells.get(i).y);
                        }
                        //set cell in mapMatrix back to 3
                        mapMatrix[buildCells.get(i).x][buildCells.get(i).y] = 3;
                    }
                }
            }

            // make list of cells where I'm about to move
            List<Cell> toMoveCells = new ArrayList<Cell>();

            // move each robot to the closest Cell with owner -1 or 0. Do not move if there
            // is no scrap. Do not move if there is a recycler in range. Don't move if there
            // is a robot in range. move to Cell where move is already planned.
            for (Robot robot : robots) {

                if (robot.units > 0) {
                    for (int i = 0; i < robot.units; i++) {

                        int closestCell = 0;
                        int closestDistance = 1000;
                        for (Cell cell : cells) {
                            // check that cell is not in toMoveCells
                            if (toMoveCells.contains(cell)) {
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
                                "MOVE " + "1" + " " + robot.x + " " + robot.y + " " + cells.get(closestCell).x
                                        + " "
                                        + cells.get(closestCell).y);
                        toMoveCells.add(cells.get(closestCell));
                    }
                }

            }

            // spawn a robot on each cell with owner 1 and units 0 and recycler 0 and
            // canSpawn and myMatter > 3 and scrapAmount > 0
            // check how many times myMatter can be divided by 6
            if (buildRecycler > 0) {
                myMatter -= 10 * buildRecycler;
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

            // spawn in every cell in spawnCellsCandidates
            for (Cell cell : spawnCellsCandidates) {
                if (spawnCount > 0) {
                    //check cell has path to neutral or enemy cell
                    boolean hasPath = false;
                    //set cell in mapMatrix to 1
                    mapMatrix[cell.x][cell.y] = 1;
                    hasPath = Utils.isPath(mapMatrix, width, height);
                    if (hasPath) {
                        actions.add("SPAWN 1 " + cell.x + " " + cell.y);
                        spawnCount--;
                    }
                    //set cell in mapMatrix back to 3
                    mapMatrix[cell.x][cell.y] = 3;
                }
            }

            // if spawnCount > 0, spawn in every cell in spawnCells
            for (Cell cell : spawnCells) {
                if (spawnCount > 0) {
                    //check cell has path to neutral or enemy cell
                    boolean hasPath = false;
                    //set cell in mapMatrix to 1
                    mapMatrix[cell.x][cell.y] = 1;
                    hasPath = Utils.isPath(mapMatrix, width, height);
                    if (hasPath) {
                        actions.add("SPAWN 1 " + cell.x + " " + cell.y);
                        spawnCount--;
                    }
                    //set cell in mapMatrix back to 3
                    mapMatrix[cell.x][cell.y] = 3;
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
