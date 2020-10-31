package service;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

import metier.Player;
import metier.PlayerManager;
import metier.DataInfo;

public class PlayerRemoteImpl extends UnicastRemoteObject implements IPlayerRemote {
	private PlayerManager playerManager = new PlayerManager();
	
	public PlayerRemoteImpl() throws RemoteException
	{
		
	}

	@Override
	public int registerPlayer(String p) throws RemoteException {
		int pID = playerManager.getPlayerNumber();
		Player newPlayer = new Player(pID,0,p);
		playerManager.addPlayer(newPlayer);
		System.out.println("Ajout de : " + p);
		return pID;
		
	}

	@Override
	public void Move(int id, double x, double y) throws RemoteException {
		playerManager.Move(id, x, y);
		
	}

	@Override
	public List<DataInfo> UpdateAllPositions() throws RemoteException {
		List<DataInfo> res = new ArrayList<>();
		for(Player p:playerManager.listAllPlayers())
		{
			res.add(new DataInfo(p.getX(),p.getY(),p.getSize(),p.getTeamID()));
		}
		return res;
	}
	
}
