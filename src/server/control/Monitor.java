package server.control;

import interfaces.Team;

/** This class will handle all synchronization for variables update */
public class Monitor {

  /**
   * Method to update a team score
   *
   * @param team : Team who will have it score updated
   * @param amount : value to add to the score (positive or negative)
   */
  public void addScore(Team team, int amount) {
    if (team == null) {
      return;
    }
    synchronized (team.getBell()) {
      team.addToScore(amount);
    }
  }
}
;
