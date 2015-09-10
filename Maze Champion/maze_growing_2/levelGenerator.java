import java.util.HashSet;
import java.util.LinkedList;
import java.util.Random;

/**
 * Created by Karthik on 11/18/2014.
 * This program generates a random maze based on Growing Tree algorithm. The following points demonstrate how this is done
 * 1. Let C be a list of cells, initially empty. Add one cell to C, at random.
 * 2. Choose a random cell from C, and carve a passage to any unvisited neighbor of that cell, adding that neighbor to C as well.
 *    If there are no unvisited neighbors, remove the cell from C.
 * 3. Repeat #2 until C is empty.
 * Instead of holding the cells in a list, we have used an imitation maze grid that keeps track of which cells are in the
 * hypothetical list C, which are not yet seen and which have been processed till it has no unvisited neighbors.
 * The imitation maze is of the same size as the maze, and each cell in this imitation maze is either 0, 1 or 2.
 * If a cell is 0, it means it is yet to be visited. If it is 1, then it has been visited but all its neighbors are not yet
 * exhausted. If it is 2, then it is visited and its neighbors have been exhausted.
 * Each cell in the main maze holds an integer array of size 4, in which the 0th position denotes whether it has an opening in
 * the north direction, 1st position denotes whether it has an opening in the east direction, 2nd position denotes whether it
 * has an opening in the south direction and 3rd position denotes whether it has an opening in the west direction.
 * A 0 denotes that a wall is present in that direction and a 1 denotes an opening in that direction.
 */
public class levelGenerator {

  public int level; // the maze would be of size level x level
  public int[][][] maze; // stores the maze
  public int[][] mz_coll; // stores maze processing progress
  int count, mz_count; // house keeping variables
  Random randomGenerator = new Random(); // random number generator
  HashSet<Integer> check_count = new HashSet<Integer>(); // house keeping variable

  // initialization function
  public void levelGenerator()
  {
    level = 35; // set level to 35
    count = 0; // count of neighbors vizited around a particular cell
    mz_count = 0; // number of cells in state 2
    maze = new int[level][level][4]; // initialize the maze
    mz_coll = new int[level][level]; // initialize the imitation maze
    for (int i = 0; i < level; i++)
    {
      for (int j = 0; j < level; j++)
      {
        maze[i][j][0] = 0;
        maze[i][j][1] = 0;
        maze[i][j][2] = 0;
        maze[i][j][3] = 0;
        mz_coll[i][j] = 0;
      }
    }
  }

  // main function to make and display a maze
  public static void main(String[]args)
  {
    levelGenerator gen = new levelGenerator();
    gen.levelGenerator();
    gen.mazeGenerator();

    for (int i = 0; i < gen.level; i++)
    {
      for (int j = 0; j < gen.level; j++)
      {
        System.out.print(gen.maze[i][j][0] + "" + gen.maze[i][j][1] + "" + gen.maze[i][j][2] + "" + gen.maze[i][j][3] + "\t");
      }
      System.out.println();
    }
  }

  // the main function
  public void mazeGenerator()
  {
    int x = randomGenerator.nextInt(level); // select a random cell
    int y = randomGenerator.nextInt(level);
    mz_coll[y][x] = 1; // make the imitation maze position corresponding to the cell 1

      while (mz_count < level * level) // while not all cells have been put into state 2
    {
      boolean found_1 = false; // whether a cell with 1 has been found
      while (!found_1) { // while such a cell has not been found
        x = randomGenerator.nextInt(level); // get a random cell from the maze
        y = randomGenerator.nextInt(level);
        if (mz_coll[y][x] == 1) // if the cell found, then exit this loop
          found_1 = true;
      }
      processNeighbor(x, y); // process its neighbors
    }
  }

  // process the neighbors of the selected cell
  private void processNeighbor(int x, int y)
  {
    if (mz_coll[y][x] == 1 || mz_coll[y][x] == 0) // only if the selected cell is either in state 0 or 1
    {
      int next = randomGenerator.nextInt(4); // get a random neighbor

      if (count < 4) // if the number of neighbors processed so far is less than 4
      {
        switch (next) // depending on which neighbor was chosen
        {
        case 0: // north neighnor
          if (y - 1 >= 0) // north neighbor should be within the maze
          {
            if (mz_coll[y - 1][x] == 0) // if the neighbor is an unvisited cell
            {
              count = 0;  // neighbors evaluated to 0
              check_count.clear(); // clear the list of neighbors sought
              maze[y][x][0] = 1; // open current cell into the neighbor
              maze[y - 1][x][2] = 1; // open neighbor into current cell
              mz_coll[y - 1][x] = 1; // make neighbor visited by changing state to 1
            } else // if it is a visited neighbor
            {
              if (!check_count.contains(next)) { // if this neighbor has not been seen so far in current iteration of current cell
                count++; // increase neighbor count by 1
                check_count.add(next); // add this neighbor to list of those that have been seen in current iteration of current cell
              }
              processNeighbor(x, y); // and call back this function with current cell
            }
          } else // if found a neighbor out of bounds of maze
          {
            if (!check_count.contains(next)) { // if this neighbor has not been seen so far in current iteration of current cell
              count++; // increase neighbor count by 1
              check_count.add(next); // add this neighbor to list of those that have been seen in current iteration of current cell
            }
            processNeighbor(x, y); // and call back this function with current cell
          }
          break;
        case 1: // east neighbor
          if (x + 1 < level) // east neighbor should be within the maze
          {
            if (mz_coll[y][x + 1] == 0) // if the neighbor is an unvisited cell
            {
              count = 0; // neighbors evaluated to 0
              check_count.clear(); // clear the list of neighbors sought
              maze[y][x][1] = 1; // open current cell into the neighbor
              maze[y][x + 1][3] = 1; // open neighbor into current cell
              mz_coll[y][x + 1] = 1; // make neighbor visited by changing state to 1
            } else // if it is a visited neighbor
            {
              if (!check_count.contains(next)) { // if this neighbor has not been seen so far in current iteration of current cell
                count++; // increase neighbor count by 1
                check_count.add(next); // add this neighbor to list of those that have been seen in current iteration of current cell
              }
              processNeighbor(x, y); // and call back this function with current cell
            }
          } else // if found a neighbor out of bounds of maze
          {
            if (!check_count.contains(next)) { // if this neighbor has not been seen so far in current iteration of current cell
              count++; // increase neighbor count by 1
              check_count.add(next); // add this neighbor to list of those that have been seen in current iteration of current cell
            }
            processNeighbor(x, y); // and call back this function with current cell
          }
          break;
        case 2: // south neighbor
          if (y + 1 < level) // south neighbor should be within the maze
          {
            if (mz_coll[y + 1][x] == 0) // if the neighbor is an unvisited cell
            {
              count = 0; // neighbors evaluated to 0
              check_count.clear(); // clear the list of neighbors sought
              maze[y][x][2] = 1; // open current cell into the neighbor
              maze[y + 1][x][0] = 1; // open neighbor into current cell
              mz_coll[y + 1][x] = 1; // make neighbor visited by changing state to 1
            } else // if it is a visited neighbor
            {
              if (!check_count.contains(next)) { // if this neighbor has not been seen so far in current iteration of current cell
                count++; // increase neighbor count by 1
                check_count.add(next); // add this neighbor to list of those that have been seen in current iteration of current cell
              }
              processNeighbor(x, y); // and call back this function with current cell
            }
          } else // if found a neighbor out of bounds of maze
          {
            if (!check_count.contains(next)) { // if this neighbor has not been seen so far in current iteration of current cell
              count++; // increase neighbor count by 1
              check_count.add(next); // add this neighbor to list of those that have been seen in current iteration of current cell
            }
            processNeighbor(x, y); // and call back this function with current cell
          }
          break;
        case 3: // west neighbor
          if (x - 1 >= 0) // west neighbor should be within the maze
          {
            if (mz_coll[y][x - 1] == 0) // if the neighbor is an unvisited cell
            {
              count = 0; // neighbors evaluated to 0
              check_count.clear(); // clear the list of neighbors sought
              maze[y][x][3] = 1; // open current cell into the neighbor
              maze[y][x - 1][1] = 1; // open neighbor into current cell
              mz_coll[y][x - 1] = 1; // make neighbor visited by changing state to 1
            } else // if it is a visited neighbor
            {
              if (!check_count.contains(next)) { // if this neighbor has not been seen so far in current iteration of current cell
                count++; // increase neighbor count by 1
                check_count.add(next); // add this neighbor to list of those that have been seen in current iteration of current cell
              }
              processNeighbor(x, y); // and call back this function with current cell
            }
          } else // if found a neighbor out of bounds of maze
          {
            if (!check_count.contains(next)) { // if this neighbor has not been seen so far in current iteration of current cell
              count++; // increase neighbor count by 1
              check_count.add(next); // add this neighbor to list of those that have been seen in current iteration of current cell
            }
            processNeighbor(x, y); // and call back this function with current cell
          }
          break;
        }
      } else // if all neighbors have been already seen
      {
        mz_coll[y][x] = 2; // convert the current cell into state 2
        mz_count++; // increment the count of cells in state 2 by one
        count = 0; // neighbors evaluated to 0
        check_count.clear(); // clear the list of neighbors sought
      }
    }
  }
}

