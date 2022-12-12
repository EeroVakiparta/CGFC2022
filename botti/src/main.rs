use std::io;

macro_rules! parse_input {
    ($x:expr, $t:ident) => ($x.trim().parse::<$t>().unwrap())
}

const MOVE: &str = "MOVE";
const BUILD: &str = "BUILD";
const SPAWN: &str = "SPAWN";
const WAIT: &str = "WAIT";
/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
fn main() {
    let mut input_line = String::new();
    io::stdin().read_line(&mut input_line).unwrap();
    let inputs = input_line.split(" ").collect::<Vec<_>>();
    let width = parse_input!(inputs[0], i32);
    let height = parse_input!(inputs[1], i32);
    eprintln!("width: {}", width);
    eprintln!("height: {}", height);

    

    // game loop
    loop {
        let mut cells:Vec<Cell> = Vec::new();
        //make vector of units
        let mut robotUnits:Vec<Unit> = Vec::new();
        let mut input_line = String::new();
        io::stdin().read_line(&mut input_line).unwrap();
        let inputs = input_line.split(" ").collect::<Vec<_>>();
        let my_matter = parse_input!(inputs[0], i32);
        eprintln!("my_matter: {}", my_matter);
        let opp_matter = parse_input!(inputs[1], i32);
        eprintln!("opp_matter: {}", opp_matter);
        for y in 0..height as usize {
            for x in 0..width as usize {
                let mut input_line = String::new();
                io::stdin().read_line(&mut input_line).unwrap();
                let inputs = input_line.split(" ").collect::<Vec<_>>();
                let scrap_amount = parse_input!(inputs[0], i32);
                let owner = parse_input!(inputs[1], i32); // 1 = me, 0 = foe, -1 = neutral
                let units = parse_input!(inputs[2], i32);
                let recycler = parse_input!(inputs[3], i32);
                let can_build = parse_input!(inputs[4], i32);
                let can_spawn = parse_input!(inputs[5], i32);
                let in_range_of_recycler = parse_input!(inputs[6], i32);
                //put the cell in the cells vector
                cells.push(Cell {
                    x: x as i32,
                    y: y as i32,
                    scrap_amount,
                    owner,
                    units,
                    recycler,
                    can_build,
                    can_spawn,
                    in_range_of_recycler,
                });
                robotUnits.push(Unit {
                    x: x as i32,
                    y: y as i32,
                    owner,
                    in_range_of_recycler,
                    can_build,
                });

            }
        }
        //allocate actions to units
        let actions = allocate_actions(robotUnits, cells);
        //print actions
        print_actions(actions);
        


    }
}

//implement copy trait for cell
#[derive(Copy, Clone)]
struct Cell {
    x: i32,
    y: i32,
    scrap_amount: i32,
    owner: i32,
    units: i32,
    recycler: i32,
    can_build: i32,
    can_spawn: i32,
    in_range_of_recycler: i32,
}

struct Unit {
    x: i32,
    y: i32,
    owner: i32,
    in_range_of_recycler: i32,
    can_build: i32,
}

struct Action {
    action_type: String,
    x: i32,
    y: i32,
}

//make action types: MOVE, BUILD, SPAWN, WAIT as strings

//function to print actions separated by semicolons
fn print_actions(actions: Vec<Action>) {
    let mut action_string = String::new();
    for action in actions {
        action_string.push_str(&action.action_type);
        action_string.push_str(" ");
        action_string.push_str(&action.x.to_string());
        action_string.push_str(" ");
        action_string.push_str(&action.y.to_string());
        action_string.push_str(";");
    }
    println!("{}", action_string);
}



//find best action for each unit. Return vector of actions. 
//Build recycler if possible and move towards most scrap. If no recycler, build recycler. 
//If recycler, move towards most scrap. Spawn if possible.
fn allocate_actions(units: Vec<Unit>, cells: Vec<Cell>) -> Vec<Action> {
    let mut actions: Vec<Action> = Vec::new();
    let mut most_scrap_cell = get_most_scrap(cells);
    for unit in units {
        if unit.can_build == 1 {
            actions.push(Action {
                action_type: BUILD.to_string(),
                x: unit.x,
                y: unit.y,
            });
        } else if unit.in_range_of_recycler == 1 {
            actions.push(Action {
                action_type: MOVE.to_string(),
                x: most_scrap_cell.x,
                y: most_scrap_cell.y,
            });
        } else if unit.in_range_of_recycler == 1 {
            actions.push(Action {
                action_type: MOVE.to_string(),
                x: most_scrap_cell.x,
                y: most_scrap_cell.y,
            });
        }else {
            actions.push(Action {
                action_type: WAIT.to_string(),
                x: 0,
                y: 0,
            });
        }
    }
    actions
}





//function to get the cell with the most scrap
fn get_most_scrap(cells: Vec<Cell>) -> Cell {
    let mut most_scrap = 0;
    let mut most_scrap_cell = Cell {
        x: 0,
        y: 0,
        scrap_amount: 0,
        owner: 0,
        units: 0,
        recycler: 0,
        can_build: 0,
        can_spawn: 0,
        in_range_of_recycler: 0,
    };
    for cell in cells {
        if cell.scrap_amount > most_scrap {
            most_scrap = cell.scrap_amount;
            most_scrap_cell = cell;
        }
    }
    most_scrap_cell
}

//function to get the cell with the most units
fn get_most_units(cells: Vec<Cell>) -> Cell {
    let mut most_units = 0;
    let mut most_units_cell = Cell {
        x: 0,
        y: 0,
        scrap_amount: 0,
        owner: 0,
        units: 0,
        recycler: 0,
        can_build: 0,
        can_spawn: 0,
        in_range_of_recycler: 0,
    };
    for cell in cells {
        if cell.units > most_units {
            most_units = cell.units;
            most_units_cell = cell;
        }
    }
    most_units_cell
}

//find best cell to move to
fn find_best_move(cells: Vec<Cell>) -> Cell {
    let mut best_cell = Cell {
        x: 0,
        y: 0,
        scrap_amount: 0,
        owner: 0,
        units: 0,
        recycler: 0,
        can_build: 0,
        can_spawn: 0,
        in_range_of_recycler: 0,
    };
    let mut best_score = 0;
    for cell in cells {
        let mut score = 0;
        if cell.scrap_amount > 0 {
            score += 1;
        }
        if cell.recycler > 0 {
            score += 1;
        }
        if cell.owner > 0 {
            score += 1;
        }
        if cell.owner < 0 {
            score += 1;
        }
        if score > best_score {
            best_score = score;
            best_cell = cell;
        }
    }
    best_cell
}

//find best cell to build on
fn find_best_build(cells: Vec<Cell>) -> Cell {
    let mut best_cell = Cell {
        x: 0,
        y: 0,
        scrap_amount: 0,
        owner: 0,
        units: 0,
        recycler: 0,
        can_build: 0,
        can_spawn: 0,
        in_range_of_recycler: 0,
    };
    let mut best_score = 0;
    for cell in cells {
        let mut score = 0;
        if cell.scrap_amount > 0 {
            score += 1;
        }
        if cell.recycler > 0 {
            score += 1;
        }
        if cell.owner > 0 {
            score += 1;
        }
        if cell.owner < 0 {
            score += 1;
        }
        if score > best_score {
            best_score = score;
            best_cell = cell;
        }
    }
    best_cell
}

//find best cell to spawn on
fn find_best_spawn(cells: Vec<Cell>) -> Cell {
    let mut best_cell = Cell {
        x: 0,
        y: 0,
        scrap_amount: 0,
        owner: 0,
        units: 0,
        recycler: 0,
        can_build: 0,
        can_spawn: 0,
        in_range_of_recycler: 0,
    };
    let mut best_score = 0;
    for cell in cells {
        let mut score = 0;
        if cell.scrap_amount > 0 {
            score += 1;
        }
        if cell.recycler > 0 {
            score += 1;
        }
        if cell.owner > 0 {
            score += 1;
        }
        if cell.owner < 0 {
            score += 1;
        }
        if score > best_score {
            best_score = score;
            best_cell = cell;
        }
    }
    best_cell
}











