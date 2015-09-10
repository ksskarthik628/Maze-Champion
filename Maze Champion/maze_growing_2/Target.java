import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;

public class Target {

  public int target1_x, target1_y, target2_x, target2_y; // the target locations
  public int key1_x, key1_y, key2_x, key2_y; // the key locations
  boolean key1_taken, key2_taken; // whether the key has been taken or not
  public boolean key1_placed, key2_placed; // whether the key has been placed or not
  public boolean target1_placed, target2_placed; // whether the target has been placed or not


  public void Target() // initialize the target object
  {
    key1_taken = false; // keys have not been taken, they have not been placed and the targets have not been placed
    key2_taken = false;
    key1_placed = false;
    key2_placed = false;
    target1_placed = false;
    target2_placed = false;
  }

  public void target_init(int level) // initialize the target object for the next maze
  {
    key1_taken = false; // keys have not been taken, they have not been placed and the targets have not been placed
    key2_taken = false;
    key1_placed = false;
    key2_placed = false;
    target1_placed = false;
    target2_placed = false;
  }

  public int[] findPath(int[][][] maze, int level, int player_x, int player_y, int steps) { // path finding algorithm implementaion almost same as that in Enemy class
    int[] player_pos = new int[2]; // except here, there is no need for back tracking and hence no came_from variable. This is a breadth first search algorithm implementation
    player_pos[0] = player_x; // get the player location from the main game (sketch)
    player_pos[1] = player_y;
    structure start = new structure(player_pos, 0); // make initial structure

    PriorityQueue<structure> frontier = new PriorityQueue<structure>(10, new comparator()); // make a priority queue
    frontier.add(start); // add start to this priority queue

    int[] current = new int[2];

    int[][] cost_so_far = new int[level][level]; // cost of getting to that location in maze from starting position
    int[][] closed = new int[level][level]; // whether the location in the maze has already been seen or not

    for (int i = 0; i < level; i++)
      for (int j = 0; j < level; j++)
        cost_so_far[i][j] = -10;

    for (int i = 0; i < level; i++)
      for (int j = 0; j < level; j++)
        closed[i][j] = 0;

    cost_so_far[start.node[1]][start.node[0]] = 0; // cost from starting node to itself is 0

    int[] above_node; // neighbor variables
    int[] below_node;
    int[] left_node;
    int[] right_node;

    ArrayList<int[]> neighbors = new ArrayList<int[]>(); // array of neighbors
    structure temp1;
    int fvalue;
    while (!frontier.isEmpty ()) { // while priority queue is not empty

      neighbors.clear(); // clear all neighnors
      temp1 = frontier.poll(); // get the first of priority queue based on comparator for breadth first search
      current = temp1.node;
      fvalue = temp1.fvalue;

      closed[current[1]][current[0]] = 666; // mark this location as seen
      
      if (fvalue > steps) { // if algorithm has moved beyond specifies number of steps
        return current; // return the location
      }

      if ((current[1] - 1) >= 0 && maze[current[1]][current[0]][0] == 1) { // if above neighbor is accessible
        above_node = new int[] { 
          current[0], current[1] - 1
        };
        neighbors.add(above_node);
      }

      if ((current[1] + 1) < level && maze[current[1]][current[0]][2] == 1) { // if below neighbor is accessible
        below_node = new int[] { 
          current[0], current[1] + 1
        };
        neighbors.add(below_node);
      }

      if ((current[0] - 1) >= 0 && maze[current[1]][current[0]][3] == 1) { // if left neighbor is accessible
        left_node = new int[] { 
          current[0] - 1, current[1]
        };
        neighbors.add(left_node);
      }

      if ((current[0] + 1) < level && maze[current[1]][current[0]][1] == 1) { // if right neighbor is accessible
        right_node = new int[] { 
          current[0] + 1, current[1]
        };
        neighbors.add(right_node);
      }

      int new_cost;
      for (int[] next : neighbors) { // for all neighnors

        new_cost = cost_so_far[current[1]][current[0]] + 1; // new cost to this neighbor

        if (cost_so_far[next[1]][next[0]] == -10) // if cost to reach this location is not yet set
          cost_so_far[next[1]][next[0]] = new_cost;
        else if (cost_so_far[next[1]][next[0]] > new_cost) // if the ost was already set but is more than the current cost to reach
          cost_so_far[next[1]][next[0]] = new_cost;

        Integer priority = new_cost + 1; // the priority of this neighbor is its cost so far plus 1

        if (closed[next[1]][next[0]] != 666) { // if the neighbor has nto yet been seen
          frontier.add(new structure(next, priority));
        }
      }
    }
    return current;
  }

  public void key1_placement(int level, int p1_x, int p1_y, int[][][] maze) { // take the position of player 1 and figure out where to put this key

    int[] key1;
    int steps;
    steps = 50; // place the key after 50 steps from the player's current location
    key1 = findPath(maze, level, p1_x, p1_y, steps);
    key1_x = key1[0];
    key1_y = key1[1];
    key1_placed = true;
  }

  public void key2_placement(int level, int p2_x, int p2_y, int[][][] maze) { // take the position of player 2 and figure out where to put this key
    int[] key2;
    int steps; 
    steps = 50; // place the key after 50 steps from the player's current location
    key2 = findPath(maze, level, p2_x, p2_y, steps);
    key2_x = key2[0];
    key2_y = key2[1];
    key2_placed = true;
  }

  public void target1_placement(int level, int p1_x, int p1_y, int[][][] maze) // take the position of player 1 and figure out where to put this target
  {
    int[] target1;
    int steps;
    steps = 75; // place the target after 75 steps from the player's current location
    target1 = findPath(maze, level, p1_x, p1_y, steps);
    target1_x = target1[0];
    target1_y = target1[1];
    target1_placed = true;
  }
  
  public void target2_placement(int level, int p2_x, int p2_y, int[][][] maze) // take the position of player 2 and figure out where to put this target
  {
    int[] target2;
    int steps;
    steps = 75; // place the target after 75 steps from the player's current location
    target2 = findPath(maze, level, p2_x, p2_y, steps);
    target2_x = target2[0];
    target2_y = target2[1];
    target2_placed = true;
  }

  public void key_take_1() // key 1 was taken by player 1
  {
    key1_taken = true;
  }

  public void key_lost_1() // player 1 was killed while in possession of the key
  {
    key1_taken = false;
    key1_placed = false;
    target1_placed = false;
  }

  public void key_take_2() // key 2 was taken by player 2
  {
    key2_taken = true;
  }

  public void key_lost_2() // player 2 was killed while in possession of the key
  {
    key2_taken = false;
    key2_placed = false;
    target2_placed = false;
  }
}
