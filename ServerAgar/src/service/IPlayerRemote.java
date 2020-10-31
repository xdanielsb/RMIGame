package service;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import metier.Player;
import metier.DataInfo;

public interface IPlayerRemote extends Remote {
	public int registerPlayer(String p) throws RemoteException;
	public void Move(int id, double x, double y) throws RemoteException;
	public List<DataInfo> UpdateAllPositions() throws RemoteException;

}
