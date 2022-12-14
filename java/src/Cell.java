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
    int scrapValue;

    int battleValue;

    int totalValue;

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