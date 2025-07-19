package UMCS.Games;
import  UMCS.Games.Lib.*; 
import  java.util.Random; 

/**
 * MERSENNE TWISTER RANDOM GAME
 * Implements an incremental random game tree (for OmSearch and PromSearch).
 * Gives evaluations simultaneously for a set of opponents.
 * The Branchingfactor is selected randomly per child between minwidth and maxwidth
 * (inclusive).
 *
 * Parent node base[0] is used as random seed for children. 
 * Games played with the same start move will be identical, independent of the
 * used search algorithm.
 *
 * This version uses the MersenneTwister random number generator
 *
 * @author Jeroen Donkers
 * @version September 2000
 **/

public class MTRandomGame extends RandomGame implements Game
{

   public MTRandomGame(int minw, int maxw, int nopp) {
      super(minw,maxw,nopp);
      useRandomGenerator(new UMCS.stat.MersenneTwister());
   } 

}
