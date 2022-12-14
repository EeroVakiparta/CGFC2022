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
