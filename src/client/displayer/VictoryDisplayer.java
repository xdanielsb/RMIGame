package client.displayer;

import java.awt.Color;

import interfaces.Team;
import processing.core.PApplet;
import processing.core.PConstants;

public class VictoryDisplayer {
	public static void draw(Team winner, PApplet sketch)
	{
		sketch.textSize(80);
		sketch.textAlign(PConstants.CENTER);
		Color teamColor = winner.getColor();
		sketch.fill(teamColor.getRed(), teamColor.getGreen(), teamColor.getBlue());
		sketch.text("Team " + winner.getTeamName() + " wins", sketch.width/2, sketch.height/2);
	}

}
