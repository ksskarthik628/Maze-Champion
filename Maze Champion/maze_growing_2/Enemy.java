import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Random;

// Enemy class
public class Enemy {

  public int enemy_x, enemy_y; // enemy's position
  public int enemy_target_x, enemy_target_y; // enemy's target
  public boolean blocked; // if enemy is blocked or not
  public int mode, prev_mode; // enemy's current and previous mode
  public int quarter; // which quarter of the maze is this enemy's territory
  public int corner; // which corner will this enemy target while patrolling
  public int enemy_esc_ctr; // counter for relieving enemy from block

  private long framecount, framelimit; // count for number of frames it has been so far and the number of frames for updating the enemy location
  private int count; // pro\ivate variable for patrolling around a player's target

    Random randomGenerator = new Random(); // random number generator

  int[][][] came_from; // path finding variable

  public boolean target_reached; // whether the enemy has reached its current target
  
  public boolean guarding; // whether the enemy is guarding or patrolling

  public void Enemy(int level) { // initialize the enemy object for the maze
    target_reached = false; // target has not yet been reached
    came_from = new int[level][level][2];
  }

  public void init_enemy(int level, int[][][] maze, int enemy_mode, int quart) // initialize the eneemy for the maze
  {
    framecount = 0; // no frames have been seen
    framelimit = 4; // update enemy location every 4 frames
    target_reached = false;
    came_from = new int[level][level][2];
    quarter = quart;
    mode = enemy_mode;
    blocked = false;
    corner = randomGenerator.nextInt(4); // choose random corner
    set_start_end(level); // set up the starting location for enemy and its immediate target
    findPath(enemy_target_x, enemy_target_y, maze, level); // find path from the current start positiona nd the immediate target
    enemy_esc_ctr = 0;
    count = 0;
    guarding = false;
  }

  private void set_start_end(int level) // this function sets up the initial position of the enemy and its immediate target
  {
    int temp;
    switch (quarter)
    {
    case 0: // if its the top left quarter of the maze
      switch (corner) 
      {
      case 0: // if its starting position is in the top left corner
        enemy_x = 1;
        enemy_y = 1;
        //corner = random(4);
        break;
      case 1: // if its starting position is in the top right corner
        enemy_x = level / 2 - 1;
        enemy_y = 0;
        //corner = random(4);
        break;
      case 2: // if its starting position is in the bottom right corner
        enemy_x = level / 2 - 1;
        enemy_y = level / 2 - 1;
        //corner = random(4);
        break;
      case 3: // if its starting position is in the bottom left corner
        enemy_x = 0;
        enemy_y = level / 2 - 1;
        //corner = random(4);
        break;
      }
      temp = corner;
      corner = randomGenerator.nextInt(4); // randomly choose the next target location corner
      while (temp == corner)
        corner = randomGenerator.nextInt(4);
      switch (corner)
      {
      case 0: // if its immediate target is in the top left corner
        enemy_target_x = 1;
        enemy_target_y = 1;
        //corner = random(4);
        break;
      case 1: // if its immediate target is in the top right corner
        enemy_target_x = level / 2 - 1;
        enemy_target_y = 0;
        //corner = random(4);
        break;
      case 2: // if its immediate target is in the bottom right corner
        enemy_target_x = level / 2 - 1;
        enemy_target_y = level / 2 - 1;
        //corner = random(4);
        break;
      case 3: // if its immediate target is in the bottom left corner
        enemy_target_x = 0;
        enemy_target_y = level / 2 - 1;
        //corner = random(4);
        break;
      }
      break;
    case 1: // if its the top right quarter of the maze
      switch (corner)
      {
      case 0:
        enemy_x = level / 2;
        enemy_y = 0;
        //corner = random(4);
        break;
      case 1:
        enemy_x = level - 2;
        enemy_y = 1;
        //corner = random(4);
        break;
      case 2:
        enemy_x = level - 1;
        enemy_y = level / 2 - 1;
        //corner = random(4);
        break;
      case 3:
        enemy_x = level / 2;
        enemy_y = level / 2 - 1;
        //corner = random(4);
        break;
      }
      temp = corner;
      corner = randomGenerator.nextInt(4);
      while (temp == corner)
        corner = randomGenerator.nextInt(4);
      switch (corner)
      {
      case 0:
        enemy_target_x = level / 2;
        enemy_target_y = 0;
        //corner = random(4);
        break;
      case 1:
        enemy_target_x = level - 2;
        enemy_target_y = 1;
        //corner = random(4);
        break;
      case 2:
        enemy_target_x = level - 1;
        enemy_target_y = level / 2 - 1;
        //corner = random(4);
        break;
      case 3:
        enemy_target_x = level / 2;
        enemy_target_y = level / 2 - 1;
        //corner = random(4);
        break;
      }
      break;
    case 2: // if its the bottom right quarter of the maze
      switch (corner)
      {
      case 0:
        enemy_x = level / 2;
        enemy_y = level / 2;
        //corner = random(4);
        break;
      case 1:
        enemy_x = level - 1;
        enemy_y = level / 2;
        //corner = random(4);
        break;
      case 2:
        enemy_x = level - 1;
        enemy_y = level - 1;
        //corner = random(4);
        break;
      case 3:
        enemy_x = level / 2;
        enemy_y = level - 1;
        //corner = random(4);
        break;
      }
      temp = corner;
      corner = randomGenerator.nextInt(4);
      while (temp == corner)
        corner = randomGenerator.nextInt(4);
      switch (corner)
      {
      case 0:
        enemy_target_x = level / 2;
        enemy_target_y = level / 2;
        //corner = random(4);
        break;
      case 1:
        enemy_target_x = level - 1;
        enemy_target_y = level / 2;
        //corner = random(4);
        break;
      case 2:
        enemy_target_x = level - 1;
        enemy_target_y = level - 1;
        //corner = random(4);
        break;
      case 3:
        enemy_target_x = level / 2;
        enemy_target_y = level - 1;
        //corner = random(4);
        break;
      }
      break;
    case 3: // if its the bottom left quarter of the maze
      switch (corner)
      {
      case 0:
        enemy_x = 0;
        enemy_y = level / 2;
        //corner = random(4);
        break;
      case 1:
        enemy_x = level / 2 - 1;
        enemy_y = level / 2;
        //corner = random(4);
        break;
      case 2:
        enemy_x = level / 2 - 1;
        enemy_y = level - 1;
        //corner = random(4);
        break;
      case 3:
        enemy_x = 0;
        enemy_y = level - 1;
        //corner = random(4);
        break;
      }
      temp = corner;
      corner = randomGenerator.nextInt(4);
      while (temp == corner)
        corner = randomGenerator.nextInt(4);
      switch (corner)
      {
      case 0:
        enemy_target_x = 0;
        enemy_target_y = level / 2;
        //corner = random(4);
        break;
      case 1:
        enemy_target_x = level / 2 - 1;
        enemy_target_y = level / 2;
        //corner = random(4);
        break;
      case 2:
        enemy_target_x = level / 2 - 1;
        enemy_target_y = level - 1;
        //corner = random(4);
        break;
      case 3:
        enemy_target_x = 0;
        enemy_target_y = level - 1;
        //corner = random(4);
        break;
      }
      break;
    }
  }

  public void findPath(int targetx, int targety, int[][][] maze, int level) { // the main path finding function, takes the target location, the maze and the level of the maze
    int[] enemy_pos = new int[2]; // current enemy position
    enemy_pos[0] = enemy_x;
    enemy_pos[1] = enemy_y;
    // make the structure for the starting position of the current iteration of the path finding function
    structure start = new structure(enemy_pos, 0 + (Math.abs(enemy_x - targetx) + Math.abs(enemy_y - targety)));

    PriorityQueue<structure> frontier = new PriorityQueue<structure>(10, new comparator()); // priority queue for picking the next
    frontier.add(start); // put the starting node in the priority queue

    int[] current = new int[2]; 

    came_from = new int[level][level][2]; // initialize the 3 dimensional array for storing parents of each node in the path
    int[][] cost_so_far = new int[level][level]; // variable to store the cost of reaching each node on the path
    int[][] closed = new int[level][level]; // variable for indicating whether a node has already been seen or not

    for (int i = 0; i < level; i++) // initialize the variables
      for (int j = 0; j < level; j++)
        cost_so_far[i][j] = -10;

    for (int i = 0; i < level; i++)
      for (int j = 0; j < level; j++)
        closed[i][j] = 0;

    for (int i = 0; i < level; i++)
      for (int j = 0; j < level; j++) {
        came_from[i][j][0] = -10;
        came_from[i][j][1] = -10;
      }

    came_from[start.node[1]][start.node[0]][0] = start.node[0]; // start node comes from itself
    came_from[start.node[1]][start.node[0]][1] = start.node[1];
    cost_so_far[start.node[1]][start.node[0]] = 0; // there is no cost in going start node to itself

    int[] above_node; // neighbor nodes
    int[] below_node;
    int[] left_node;
    int[] right_node;

    ArrayList<int[]> neighbors = new ArrayList<int[]>(); // array of neighbors
    structure temp1;

    while (!frontier.isEmpty ()) { // the A* algorithm begins

      neighbors.clear(); // clear all the neighbors in the list
      temp1 = frontier.poll(); // get the first structure in the priority quese
      current = temp1.node; // get the node from the structure

        closed[current[1]][current[0]] = 666; // make this node a seen node

      if (current[0] == targetx && current[1] == targety) { // if this node is the target, then the algorithm exits
        break;
      }

      if ((current[1] - 1) >= 0 && maze[current[1]][current[0]][0] == 1) { // if the above neighbor is still in range of the maze and is accessible
        above_node = new int[] { 
          current[0], current[1] - 1
        };
        neighbors.add(above_node);
      }

      if ((current[1] + 1) < level && maze[current[1]][current[0]][2] == 1) { // if the below neighbor is still in range of the maze and is accessible
        below_node = new int[] { 
          current[0], current[1] + 1
        };
        neighbors.add(below_node);
      }

      if ((current[0] - 1) >= 0 && maze[current[1]][current[0]][3] == 1) { // if the left neighbor is still in range of the maze and is accessible
        left_node = new int[] { 
          current[0] - 1, current[1]
        };
        neighbors.add(left_node);
      }

      if ((current[0] + 1) < level && maze[current[1]][current[0]][1] == 1) { // if the right neighbor is still in range of the maze and is accessible
        right_node = new int[] { 
          current[0] + 1, current[1]
        };
        neighbors.add(right_node);
      }

      int new_cost;
      for (int[] next : neighbors) { // for all neighbor candidates

          new_cost = cost_so_far[current[1]][current[0]] + (Math.abs(current[0] - next[0]) + Math.abs(current[1] - next[1])); // calculate the estimated cost to this neighbor from the current node

        if (cost_so_far[next[1]][next[0]] == -10)
          cost_so_far[next[1]][next[0]] = new_cost; // if cost so far for the neighbor is not yet set, set it
        else if (cost_so_far[next[1]][next[0]] > new_cost) // if it has been previously set but this new cost is smaller, reset it
            cost_so_far[next[1]][next[0]] = new_cost;

        Integer priority = new_cost + (Math.abs(targetx - next[0]) + Math.abs(targety - next[1])); // canculate the priority of this neighbor
        // the cost so far is taken from memory and the heuristic is manhatten distance between the neighbor and the target

        if (closed[next[1]][next[0]] != 666) { // if this neighbor has not already been seen
          frontier.add(new structure(next, priority)); // add it to the priority queue
          came_from[next[1]][next[0]][0] = current[0]; // record its parents
          came_from[next[1]][next[0]][1] = current[1];
        }
      }
    }

    /*int back_x = targetx; // print the path from the target to the starting position in terminal if you want
     int back_y = targety;
     
     while (targetx != enemy_x || targety != enemy_y)
     {
     System.out.println(back_x + " " + back_y);
     targetx = back_x;
     targety = back_y;
     back_x = came_from[targety][targetx][0];
     back_y = came_from[targety][targetx][1];
     }*/
  }

  public void move_enemy() // this function makes the move after looking up at came_from array
  {
    int target_x = enemy_target_x;
    int target_y = enemy_target_y;
    if (target_x != enemy_x || target_y != enemy_y) // if the target has yet to be reached
    {
      enemy_target_x = target_x;
      enemy_target_y = target_y;

      int back_x = target_x;
      int back_y = target_y;

      while (back_x != enemy_x || back_y != enemy_y) // backtrack till back_x and back_y are same as enemy_x and enemy_y then target_x and target_y are the next steps
      {
        //System.out.println(back_x + " " + back_y);
        target_x = back_x;
        target_y = back_y;
        back_x = came_from[target_y][target_x][0];
        back_y = came_from[target_y][target_x][1];
      }

      enemy_x = target_x;
      enemy_y = target_y;
    } else // when target_x and target_y are the same as enemy_x and enemy_y, target had been reached
    target_reached = true;
  }

  public Players getNextMove(int[][][] maze, int level, int blk_limit, int stuck_limit, Players players, float framerate, Target target) // this is the main interface function for making the enemy move
  {
    if (framecount % (framelimit*2) == 0) { // move only if the framerate is a multiple of framelimit

      switch (mode) // depending on the mode of the enemy, call the respective motion handling functions
      {
      case 0:
        players = mode0_move(maze, level, blk_limit, stuck_limit, players, framerate); // normal patrolling mode
        break;
      case 1:
        players = mode1_move(maze, level, blk_limit, stuck_limit, players, framerate); // attack player 1 with twice the normal speed
        break;
      case 2:
        players = mode2_move(maze, level, blk_limit, stuck_limit, players, framerate); // attack player 2 with twice the speed
        break;
      case 3:
        players = mode3_move(maze, level, blk_limit, stuck_limit, players, framerate, target); // patrol around target for player 1 by choosing to go to one of the four neighbors of target
        break;
      case 4:
        players = mode4_move(maze, level, blk_limit, stuck_limit, players, framerate, target); // patrol around target for player 2 by choosing to go to one of the four neighbors of target
        break;
      }
    }

    framecount++; // increment framecount

    return players;
  }

  public void enemy_reset() // reset the enemy everytime it reaches its immediate target
  {
    target_reached = false;
  }

  public Players mode0_move(int[][][] maze, int level, int blk_limit, int stuck_limit, Players players, float framerate) // normal patrol mode
  {
    framelimit = 4; // for every four frames
    if (blocked)
    {
      if (enemy_esc_ctr * 2 <= stuck_limit * framerate) // if the counter is still less than 5 seconds worth frames
      {
        enemy_esc_ctr++; // increment counter
      } else
      {
        blocked = false; // unblock enemy
        enemy_esc_ctr = 0; // reset counter
      }
    } else // if enemy is not blocked
    {
      if (target_reached) // if target has been reached, pick a new target randomly
      {
        set_start_end(level); // randomly pick new target
        findPath(enemy_target_x, enemy_target_y, maze, level); // form path from current enemy location to the new immediate target 
        target_reached = false;
      }
      move_enemy(); // make the move
      for (int i = 0; i < players.p1_blocks; i++) // check if enemy moved into a block by player 1
      {
        if (players.player1_blocks[i][0] == enemy_x && players.player1_blocks[i][1] == enemy_y) // if enemy enters a block by player 1
        {
          blocked = true; // enemy is blocked for 5 seconds
          players.p1_blocks--; // replenish player 1 blocks
          players.mashup2(i, blk_limit); // house keeping with block positions
          prev_mode = mode; // record current mode
          mode = 1; // change mode to attack player 1
          enemy_target_x = players.player1_x; // set immediate next target as player 1 location
          enemy_target_y = players.player1_y;
          findPath(enemy_target_x, enemy_target_y, maze, level); // form path to new immediate target
        }
      }
      for (int i = 0; i < players.p2_blocks; i++) // check if enemy moved into a block by player 1
      {
        if (players.player2_blocks[i][0] == enemy_x && players.player2_blocks[i][1] == enemy_y) // if enemy enters a block by player 2
        {
          blocked = true; // enemy is blocked for 5 seconds
          players.p2_blocks--; // replenish player 2 blocks
          players.mashup1(i, blk_limit); // house keeping with block positions
          prev_mode = mode; // record current mode
          mode = 2; // change mode to attack player 1
          enemy_target_x = players.player2_x; // set immediate next target as player 1 location
          enemy_target_y = players.player2_y;
          findPath(enemy_target_x, enemy_target_y, maze, level); // form path to new immediate target
        }
      }
    }

    return players;
  }

  public Players mode1_move(int[][][] maze, int level, int blk_limit, int stuck_limit, Players players, float framerate) // attack player 1 mode
  {
    framelimit = 3; // for every three frames

    if (blocked)
    {
      if (enemy_esc_ctr * 2 <= stuck_limit * framerate) // if the counter is still less than 5 seconds worth frames
      {
        enemy_esc_ctr++; // increment counter
      } else
      {
        blocked = false; // unblock enemy
        enemy_esc_ctr = 0; // reset counter
      }
    } else // if enemy is not blocked
    {
      if (target_reached) // if enemy has killed player 1
      {
        corner = 4; // from current position
        set_start_end(level); // find new random corner in its quarter of maze
        mode = prev_mode; // go back to previous mode
        prev_mode = mode; // resert previous mode
        framelimit = 4; // any other mode apart from attack mode is for every 4 frames 
        findPath(enemy_target_x, enemy_target_y, maze, level); // form path to new immediate target
        target_reached = false;
        move_enemy(); // make the move
      } else { // if target is not yet reached
        move_enemy(); // make move towards immediate target
        enemy_target_x = players.player1_x; // update target location to new player 1 location
        enemy_target_y = players.player1_y;
        findPath(enemy_target_x, enemy_target_y, maze, level); // form path to this new immediate target
      }
      for (int i = 0; i < players.p1_blocks; i++) // check if enemy moved into a block by player 1
      {
        if (players.player1_blocks[i][0] == enemy_x && players.player1_blocks[i][1] == enemy_y)
        {
          blocked = true; // enemy is blocked for 5 seconds
          players.p1_blocks--; // replenish player 1 blocks
          players.mashup2(i, blk_limit); // house keeping with block positions
        }
      }
      for (int i = 0; i < players.p2_blocks; i++) // check if enemy moved into a block by player 2
      {
        if (players.player2_blocks[i][0] == enemy_x && players.player2_blocks[i][1] == enemy_y) // if enemy enters a block
        {
          blocked = true; // enemy is blocked for 5 seconds
          players.p2_blocks--; // replenish player 2 blocks
          players.mashup1(i, blk_limit); // house keeping with block positions
        }
      }
    }

    return players;
  }

  public Players mode2_move(int[][][] maze, int level, int blk_limit, int stuck_limit, Players players, float framerate) // attack player 1 mode
  {
    framelimit = 3; // for every three frames

    if (blocked)
    {
      if (enemy_esc_ctr * 2 <= stuck_limit * framerate) // if the counter is still less than 5 seconds worth frames
      {
        enemy_esc_ctr++; // increment counter
      } else
      {
        blocked = false; // unblock enemy
        enemy_esc_ctr = 0; // reset counter
      }
    } else // if enemy is not blocked
    {
      if (target_reached) // if enemy has killed player 2
      {
        corner = 4; // from current position
        set_start_end(level); // find new random corner in its quarter of maze
        mode = prev_mode; // go back to previous mode
        prev_mode = mode; // resert previous mode
        framelimit = 4; // any other mode apart from attack mode is for every 4 frames 
        findPath(enemy_target_x, enemy_target_y, maze, level); // form path to new immediate target
        target_reached = false;
        move_enemy(); // make the move
      } else { // if target is not yet reached
        move_enemy(); // make the move towards immediate target
        enemy_target_x = players.player2_x; // update target location to new player 2 location
        enemy_target_y = players.player2_y;
        findPath(enemy_target_x, enemy_target_y, maze, level); // form path to this new immediate target
      }

      for (int i = 0; i < players.p1_blocks; i++) // check if enemy moved into a block by player 1
      {
        if (players.player1_blocks[i][0] == enemy_x && players.player1_blocks[i][1] == enemy_y) // if enemy enters a block by player 2
        {
          blocked = true; // enemy is blocked for 5 seconds
          players.p1_blocks--; // replenish player 1 blocks
          players.mashup2(i, blk_limit); // house keeping with block positions
        }
      }
      for (int i = 0; i < players.p2_blocks; i++) // check if enemy moved into a block by player 2
      {
        if (players.player2_blocks[i][0] == enemy_x && players.player2_blocks[i][1] == enemy_y) // if enemy enters a block by player 2
        {
          blocked = true; // enemy is blocked for 5 seconds
          players.p2_blocks--; // replenish player 2 blocks
          players.mashup1(i, blk_limit); // house keeping with block positions
        }
      }
    }

    return players;
  }

  public Players mode3_move(int[][][] maze, int level, int blk_limit, int stuck_limit, Players players, float framerate, Target target) // patrolling target of player 1
  {
    framelimit = 4; // for every 4 frames
    set_target_patrol(level, target, 1, maze); // select a randon neighbor around the target for player 1 to go to and form path
    if (blocked)
    {
      if (enemy_esc_ctr * 2 <= stuck_limit * framerate) // if the counter is still less than 5 seconds worth frames
      {
        enemy_esc_ctr++; // increment counter
      } else
      {
        blocked = false; // unblock enemy
        enemy_esc_ctr = 0; // reset counter
      }
    } else // if enemy is not blocked
    {
      if (target_reached) // if it has reached that target
      {
        count = 0; // internal count for selecting new target and forming path
        set_target_patrol(level, target, 1, maze); // select the new target location anf form path to it
        target_reached = false;
      }
      move_enemy(); // make the move
      for (int i = 0; i < players.p1_blocks; i++) // check if enemy moved into a block by player 1
      {
        if (players.player1_blocks[i][0] == enemy_x && players.player1_blocks[i][1] == enemy_y) // if enemy enters a block by player 1
        {
          blocked = true; // enemy is blocked for 5 seconds
          players.p1_blocks--; // replenish player 1 blocks
          players.mashup2(i, blk_limit); // house keeping with block positions
          prev_mode = mode; // record current mode
          mode = 1; // change mode to attack player 1
          enemy_target_x = players.player1_x; // set immediate next target as player 1 location
          enemy_target_y = players.player1_y;
          findPath(enemy_target_x, enemy_target_y, maze, level); // form path to new immediate target
        }
      }
      for (int i = 0; i < players.p2_blocks; i++) // check if enemy moved into a block by player 1
      {
        if (players.player2_blocks[i][0] == enemy_x && players.player2_blocks[i][1] == enemy_y) // if enemy enters a block by player 2
        {
          blocked = true; // enemy is blocked for 5 seconds
          players.p2_blocks--; // replenish player 2 blocks
          players.mashup1(i, blk_limit); // house keeping with block positions
          prev_mode = mode; // record current mode
          mode = 2; // change mode to attack player 1
          enemy_target_x = players.player2_x; // set immediate next target as player 1 location
          enemy_target_y = players.player2_y;
          findPath(enemy_target_x, enemy_target_y, maze, level); // form path to new immediate target
        }
      }
    }

    return players;
  }

  public Players mode4_move(int[][][] maze, int level, int blk_limit, int stuck_limit, Players players, float framerate, Target target) // patrolling target of player 2
  {
    framelimit = 4; // for every 4 frames
    set_target_patrol(level, target, 2, maze); // select a randon neighbor around the target for player 2 to go to and form path
    if (blocked)
    {
      if (enemy_esc_ctr * 2 <= stuck_limit * framerate) // if the counter is still less than 5 seconds worth frames
      {
        enemy_esc_ctr++; // increment counter
      } else
      {
        blocked = false; // unblock enemy
        enemy_esc_ctr = 0; // reset counter
      }
    } else // if enemy is not blocked
    {
      if (target_reached) // if it has reached that target
      {
        count = 0; // internal count for selecting new target and forming path
        set_target_patrol(level, target, 2, maze); // select the new target location anf form path to it
        target_reached = false;
      }
      move_enemy(); // make the move
      for (int i = 0; i < players.p1_blocks; i++) // check if enemy moved into a block by player 1
      {
        if (players.player1_blocks[i][0] == enemy_x && players.player1_blocks[i][1] == enemy_y) // if enemy enters a block by player 1
        {
          blocked = true; // enemy is blocked for 5 seconds
          players.p1_blocks--; // replenish player 1 blocks
          players.mashup2(i, blk_limit); // house keeping with block positions
          prev_mode = mode; // record current mode
          mode = 1; // change mode to attack player 1
          enemy_target_x = players.player1_x; // set immediate next target as player 1 location
          enemy_target_y = players.player1_y;
          findPath(enemy_target_x, enemy_target_y, maze, level); // form path to new immediate target
        }
      }
      for (int i = 0; i < players.p2_blocks; i++) // check if enemy moved into a block by player 1
      {
        if (players.player2_blocks[i][0] == enemy_x && players.player2_blocks[i][1] == enemy_y) // if enemy enters a block by player 2
        {
          blocked = true; // enemy is blocked for 5 seconds
          players.p2_blocks--; // replenish player 2 blocks
          players.mashup1(i, blk_limit); // house keeping with block positions
          prev_mode = mode; // record current mode
          mode = 2; // change mode to attack player 1
          enemy_target_x = players.player2_x; // set immediate next target as player 1 location
          enemy_target_y = players.player2_y;
          findPath(enemy_target_x, enemy_target_y, maze, level); // form path to new immediate target
        }
      }
    }

    return players;
  }

  private void set_target_patrol(int level, Target target, int pl, int[][][] maze) // pick the new immediate target and form a path to it
  {
    int temp;
    while (count == 0) // increment count only if the next chosen target is not the same as the previous one and is accessible
    {
      temp = corner;
      corner = randomGenerator.nextInt(4);
      while (temp == corner)
        corner = randomGenerator.nextInt(4);
      switch(corner)
      {
      case 0: // above target for player 
        if (pl == 1) { // if in mode 3
          if (target.target1_y > 0)
          {
            enemy_target_x = target.target1_x;
            enemy_target_y = target.target1_y - 1;
            count++;
          }
        }
        if (pl == 2) { // if in mode 4
          if (target.target2_y > 0)
          {
            enemy_target_x = target.target2_x;
            enemy_target_y = target.target2_y - 1;
            count++;
          }
        }
        break;
      case 1: // right of target for player 
        if (pl == 1) {
          if (target.target1_x < level - 1)
          {
            enemy_target_x = target.target1_x + 1;
            enemy_target_y = target.target1_y;
            count++;
          }
        }
        if (pl == 2) {
          if (target.target2_x < level - 1)
          {
            enemy_target_x = target.target2_x + 1;
            enemy_target_y = target.target2_y;
            count++;
          }
        }
        break;
      case 2: // below target for player 
        if (pl == 1) {
          if (target.target1_y < level - 1)
          {
            enemy_target_x = target.target1_x;
            enemy_target_y = target.target1_y + 1;
            count++;
          }
        }
        if (pl == 2) {
          if (target.target2_y < level - 1)
          {
            enemy_target_x = target.target2_x;
            enemy_target_y = target.target2_y + 1;
            count++;
          }
        }
        break;
      case 3: // left of target for player 
        if (pl == 1) {
          if (target.target1_x > 0)
          {
            enemy_target_x = target.target1_x - 1;
            enemy_target_y = target.target1_y;
            count++;
          }
        }
        if (pl == 2) {
          if (target.target2_x > 0)
          {
            enemy_target_x = target.target2_x - 1;
            enemy_target_y = target.target2_y;
            count++;
          }
        }
        break;
      }
    }
    if (count == 1)
    {
      findPath(enemy_target_x, enemy_target_y, maze, level); // form path to new immediate target
      count++;
    }
  }
}
class comparator implements Comparator <structure> { // new comparator class for picking the best from proirity queue

  public int compare(structure one, structure two)
  {
    if (one.fvalue < two.fvalue)
      return -1;
    else if (one.fvalue > two.fvalue)
      return 1;
    else
      return 0;
  }
}

class structure {
  public int fvalue;
  public int[] node;

  public structure(int[] node, int fvalue) {
    this.fvalue = fvalue;
    this.node = node;
  }
} 

