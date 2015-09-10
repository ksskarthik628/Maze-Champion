/**
 * Created by Karthik on 11/27/2014.
 */
public class Players {

  public int player1_x, player1_y, p1_blocks, p1_blk_esc_counter; // player 1's position, number of blocks placed and number of frames passed since getting stuck in block
  public int player2_x, player2_y, p2_blocks, p2_blk_esc_counter; // player 2's position, number of blocks placed and number of frames passed since getting stuck in block
  public int[][] player1_blocks, player2_blocks; // block positions for both players
  public boolean player1_blocked, player2_blocked; // whether player is blocked or not
  public int p1_score, p2_score; // number of games won by the players so far
  public boolean p1_won, p2_won; // game state variables
  public boolean player1_key, player2_key; // players have taken their respective keys or not
  public int p1_steps, p2_steps; // number of steps taken by the player so far

  public void Players(int blk_limit) // initialize the player objects
  {
    player1_blocks = new int[blk_limit][2]; // initialize the blocks
    player2_blocks = new int[blk_limit][2]; // initialize the blocks

    p1_score = 0; // initialize player scores
    p2_score = 0;
    
    p1_steps = 0; // initialize steps
    p2_steps = 0;
  }

  public void player1_reset(int level) // reset player 1 when killed by an enemy
  {
    player1_x = 0; // put player 1 at this location
    player1_y = 0;
    player1_key = false; // player 1 lost the key if they had it
    p1_steps = 0; // step counter is also reset
  }
  
  public void player2_reset(int level)  // reset player 2 when killed by an enemy
  {
    player2_x = level - 1; // put player 2 at this location
    player2_y = 0;
    player2_key = false; // player 2 lost the key if they had it
    p2_steps = 0; // step counter is also reset
  }

  public void players_init(int level, int blk_limit) // initialize the players object for the new maze
  {
    if (p1_won) // if in previous game player 1 won
        p1_score++; // increment player 1 score
    if (p2_won) // if in previous game player 2 won
        p2_score++; // increment player 2 score

    p1_won = false; // initialize player 1 win status
    p2_won = false; // initialize player 2 win status

    player1_key = false; // no player has the key
    player2_key = false;

    p1_steps = 0; // no player has moved a step
    p2_steps = 0;

    player1_x = 0; // put player 1 at this location
    player1_y = 0;
    p1_blocks = 0; // remove all player 1's blocks
    p1_blk_esc_counter = 0; // initialize the number of frames since blocked
    player2_x = level - 1; // put player 2 at this location
    player2_y = 0;
    p2_blocks = 0; // remove all player 2's blocks
    p2_blk_esc_counter = 0; // initialize the number of frames since blocked

    player1_blocks = new int[blk_limit][2]; // initialize the blocks
    player2_blocks = new int[blk_limit][2]; // initialize the blocks

    for (int i = 0; i < blk_limit; i++) // initialize the blocks of each player to none
    {
      player1_blocks[i][0] = 0;
      player1_blocks[i][1] = 0;
      player2_blocks[i][0] = 0;
      player2_blocks[i][1] = 0;
    }

    player1_blocked = false; // make player 1 movable
    player2_blocked = false; // make player 2 movable
  }

  public boolean player1Up(int[][][] maze, Target target, boolean game_state, int blk_limit) // move player 1 up
  {
    if (maze[player1_y][player1_x][0] == 1) // only if the curent cell allows player to move up
    {
      if (p2_blocks == 0 && !player1_blocked) // if player 1 is not blocked and there are no opponent blocks
      {
        player1_y = player1_y - 1; // move player up
        p1_steps++; // increment steps
      } else if (!player1_blocked) // if player is not blocked but there are blokcs by opponent in maze
      {
        player1_y = player1_y - 1; // move player
        p1_steps++; // increment steps
        for (int i = 0; i < p2_blocks; i++) // check if player moved into a block
        {
          if (player2_blocks[i][0] == player1_x && player2_blocks[i][1] == player1_y) // if player enters a block
          {
            player1_blocked = true; // player is blocked for 5 seconds
            p2_blocks--; // replenish opponent blocks
            mashup1(i, blk_limit); // house keeping with block positions
          }
        }
      }
    }
    if (player1_x == target.target1_x && player1_y == target.target1_y && player1_key) // if player has reached target and has the key
    {
      p1_won = true; // player won
      game_state = true; // maze has been solved
    }
    if (player1_x == target.key1_x && player1_y == target.key1_y && target.key1_placed) // if player has reached corresponding key
      player1_key = true;
    return game_state;
  }

  public boolean player1Right(int[][][] maze, Target target, boolean game_state, int blk_limit) // move player 1 right
  {
    if (maze[player1_y][player1_x][1] == 1) // only if the curent cell allows player to move right
    {
      if (p2_blocks == 0 && !player1_blocked) // if player 1 is not blocked and there are no opponent blocks
      {
        player1_x = player1_x + 1; // move player right
        p1_steps++; // increment steps
      } else if (!player1_blocked) // if player is not blocked but there are blokcs by opponent in maze
      {
        player1_x = player1_x + 1; // move player
        p1_steps++; // increment steps
        for (int i = 0; i < p2_blocks; i++) // check if player moved into a block
        {
          if (player2_blocks[i][0] == player1_x && player2_blocks[i][1] == player1_y) // if player enters a block
          {
            player1_blocked = true; // player is blocked for 5 seconds
            p2_blocks--; // replenish opponent blocks
            mashup1(i, blk_limit); // house keeping with block positions
          }
        }
      }
    }
    if (player1_x == target.target1_x && player1_y == target.target1_y && player1_key) // if player has reached target and has the key
    {
      p1_won = true; // player won
      game_state = true; // maze has been solved
    }
    if (player1_x == target.key1_x && player1_y == target.key1_y && target.key1_placed) // if player has reached corresponding key
      player1_key = true;
    return game_state;
  }

  public boolean player1Down(int[][][] maze, Target target, boolean game_state, int blk_limit) // move player 1 down
  {
    if (maze[player1_y][player1_x][2] == 1) // only if the curent cell allows player to move down
    {
      if (p2_blocks == 0 && !player1_blocked) // if player 1 is not blocked and there are no opponent blocks
      {
        player1_y = player1_y + 1; // move player down
        p1_steps++; // increment steps
      } else if (!player1_blocked) // if player is not blocked but there are blokcs by opponent in maze
      {
        player1_y = player1_y + 1; // move player
        p1_steps++; // increment steps
        for (int i = 0; i < p2_blocks; i++) // check if player moved into a block
        {
          if (player2_blocks[i][0] == player1_x && player2_blocks[i][1] == player1_y) // if player enters a block
          {
            player1_blocked = true; // player is blocked for 5 seconds
            p2_blocks--; // replenish opponent blocks
            mashup1(i, blk_limit); // house keeping with block positions
          }
        }
      }
    }
    if (player1_x == target.target1_x && player1_y == target.target1_y && player1_key) // if player has reached target and has the key
    {
      p1_won = true; // player won
      game_state = true; // maze has been solved
    }
    if (player1_x == target.key1_x && player1_y == target.key1_y && target.key1_placed) // if player has reached corresponding key
      player1_key = true;
    return game_state;
  }

  public boolean player1Left(int[][][] maze, Target target, boolean game_state, int blk_limit) // move player 1 left
  {
    if (maze[player1_y][player1_x][3] == 1) // only if the curent cell allows player to move left
    {
      if (p2_blocks == 0 && !player1_blocked) // if player 1 is not blocked and there are no opponent blocks
      {
        player1_x = player1_x - 1; // move player left
        p1_steps++; // increment steps
      } else if (!player1_blocked) // if player is not blocked but there are blokcs by opponent in maze
      {
        player1_x = player1_x - 1; // move player
        p1_steps++; // increment steps
        for (int i = 0; i < p2_blocks; i++) // check if player moved into a block
        {
          if (player2_blocks[i][0] == player1_x && player2_blocks[i][1] == player1_y) // if player enters a block
          {
            player1_blocked = true; // player is blocked for 5 seconds
            p2_blocks--; // replenish opponent blocks
            mashup1(i, blk_limit); // house keeping with block positions
          }
        }
      }
    }
    if (player1_x == target.target1_x && player1_y == target.target1_y && player1_key) // if player has reached target and has the key
    {
      p1_won = true; // player won
      game_state = true; // maze has been solved
    }
    if (player1_x == target.key1_x && player1_y == target.key1_y && target.key1_placed) // if player has reached corresponding key
      player1_key = true;
    return game_state;
  }

  public boolean player2Up(int[][][] maze, Target target, boolean game_state, int blk_limit) // move player 2 up
  {
    if (maze[player2_y][player2_x][0] == 1) // only if the curent cell allows player to move up
    {
      if (p1_blocks == 0 && !player2_blocked) // if player 2 is not blocked and there are no opponent blocks
      {
        player2_y = player2_y - 1; // move player up
        p2_steps++; // increment steps
      } else if (!player2_blocked) // if player is not blocked but there are blokcs by opponent in maze
      {
        player2_y = player2_y - 1; // move player
        p2_steps++; // increment steps
        for (int i = 0; i < p1_blocks; i++) // check if player moved into a block
        {
          if (player1_blocks[i][0] == player2_x && player1_blocks[i][1] == player2_y) // if player enters a block
          {
            player2_blocked = true; // player is blocked for 5 seconds
            p1_blocks--; // replenish opponent blocks
            mashup2(i, blk_limit); // house keeping with block positions
          }
        }
      }
    }
    if (player2_x == target.target2_x && player2_y == target.target2_y && player2_key) // if player has reached target and has the key
    {
      p2_won = true; // player won
      game_state = true; // maze has been solved
    }
    if (player2_x == target.key2_x && player2_y == target.key2_y && target.key2_placed) // if player has reached corresponding key
      player2_key = true;
    return game_state;
  }

  public boolean player2Right(int[][][] maze, Target target, boolean game_state, int blk_limit) // move player 2 right
  {
    if (maze[player2_y][player2_x][1] == 1) // only if the curent cell allows player to move right
    {
      if (p1_blocks == 0 && !player2_blocked) // if player 2 is not blocked and there are no opponent blocks
      {
        player2_x = player2_x + 1; // move player right
        p2_steps++; // increment steps
      } else if (!player2_blocked) // if player is not blocked but there are blokcs by opponent in maze
      {
        player2_x = player2_x + 1; // move player
        p2_steps++; // increment steps
        for (int i = 0; i < p1_blocks; i++) // check if player moved into a block
        {
          if (player1_blocks[i][0] == player2_x && player1_blocks[i][1] == player2_y) // if player enters a block
          {
            player2_blocked = true; // player is blocked for 5 seconds
            p1_blocks--; // replenish opponent blocks
            mashup2(i, blk_limit); // house keeping with block positions
          }
        }
      }
    }
    if (player2_x == target.target2_x && player2_y == target.target2_y && player2_key) // if player has reached target and has the key
    {
      p2_won = true; // player won
      game_state = true; // maze has been solved
    }
    if (player2_x == target.key2_x && player2_y == target.key2_y && target.key2_placed) // if player has reached corresponding key
      player2_key = true;
    return game_state;
  }

  public boolean player2Down(int[][][] maze, Target target, boolean game_state, int blk_limit) // move player 2 down
  {
    if (maze[player2_y][player2_x][2] == 1) // only if the curent cell allows player to move down
    {
      if (p1_blocks == 0 && !player2_blocked) // if player 2 is not blocked and there are no opponent blocks
      {
        player2_y = player2_y + 1; // move player down
        p2_steps++; // increment steps
      } else if (!player2_blocked) // if player is not blocked but there are blokcs by opponent in maze
      {
        player2_y = player2_y + 1; // move player
        p2_steps++; // increment steps
        for (int i = 0; i < p1_blocks; i++) // check if player moved into a block
        {
          if (player1_blocks[i][0] == player2_x && player1_blocks[i][1] == player2_y) // if player enters a block
          {
            player2_blocked = true; // player is blocked for 5 seconds
            p1_blocks--; // replenish opponent blocks
            mashup2(i, blk_limit); // house keeping with block positions
          }
        }
      }
    }
    if (player2_x == target.target2_x && player2_y == target.target2_y && player2_key) // if player has reached target and has the key
    {
      p2_won = true; // player won
      game_state = true; // maze has been solved
    }
    if (player2_x == target.key2_x && player2_y == target.key2_y && target.key2_placed) // if player has reached corresponding key
      player2_key = true;
    return game_state;
  }

  public boolean player2Left(int[][][] maze, Target target, boolean game_state, int blk_limit) // move player 2 left
  {
    if (maze[player2_y][player2_x][3] == 1) // only if the curent cell allows player to move left
    {
      if (p1_blocks == 0 && !player2_blocked) // if player 2 is not blocked and there are no opponent blocks
      {
        player2_x = player2_x - 1; // move player left
        p2_steps++; // increment steps
      } else if (!player2_blocked) // if player is not blocked but there are blokcs by opponent in maze
      {
        player2_x = player2_x - 1; // move player
        p2_steps++; // increment steps
        for (int i = 0; i < p1_blocks; i++) // check if player moved into a block
        {
          if (player1_blocks[i][0] == player2_x && player1_blocks[i][1] == player2_y) // if player enters a block
          {
            player2_blocked = true; // player is blocked for 5 seconds
            p1_blocks--; // replenish opponent blocks
            mashup2(i, blk_limit); // house keeping with block positions
          }
        }
      }
    }
    if (player2_x == target.target2_x && player2_y == target.target2_y && player2_key) // if player has reached target and has the key
    {
      p2_won = true; // player won
      game_state = true; // maze has been solved
    }

    if (player2_x == target.key2_x && player2_y == target.key2_y && target.key2_placed) // if player has reached corresponding key
      player2_key = true;

    return game_state;
  }

  public void mashup1(int i, int blk_limit) // remove player 1's block at position i and bring all the remaining blocks up
  {
    for (int j = i; j < blk_limit - 1; j++)
    {
      player2_blocks[j][0] = player2_blocks[j + 1][0];
      player2_blocks[j][1] = player2_blocks[j + 1][1];
    }
  }
  public void mashup2(int i, int blk_limit) // remove player 2's block at position i and bring all the remaining blocks up
  {
    for (int j = i; j < blk_limit - 1; j++)
    {
      player1_blocks[j][0] = player1_blocks[j + 1][0];
      player1_blocks[j][1] = player1_blocks[j + 1][1];
    }
  }

  // everytime draw is called, increment the counter for number of frames player 1 has been blocked if player 1 is blocked
  public void player1_block_handle(int stuck_limit, float frameRate)
  {
    if (player1_blocked) // if player 1 is blocked then
    {
      if (p1_blk_esc_counter <= stuck_limit * frameRate) // if the counter is still less than 5 seconds worth frames
      {
        p1_blk_esc_counter++; // increment counter
      } else
      {
        player1_blocked = false; // unblock player 1
        p1_blk_esc_counter = 0; // reset counter
      }
    }
  }

  // everytime draw is called, increment the counter for number of frames player 2 has been blocked if player 2 is blocked
  public void player2_block_handle(int stuck_limit, float frameRate)
  {
    if (player2_blocked) // if player 2 is blocked then
    {
      if (p2_blk_esc_counter <= stuck_limit * frameRate) // if the counter is still less than 5 seconds worth frames
      {
        p2_blk_esc_counter++; // increment counter
      } else
      {
        player2_blocked = false; // unblock player 2
        p2_blk_esc_counter = 0; // reset counter
      }
    }
  }
}

