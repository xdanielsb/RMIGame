package metier;

import java.util.ArrayList;
import java.util.List;

public class GameManager {
	private List<DataInfo> foods;
	
	public GameManager()
	{
		foods = new ArrayList<>();
		foods.add(new DataInfo(200,200,10,2));
	}
	
	public List<DataInfo> GetFoods()
	{
		return foods;
	}
	
	public void RemoveFood(DataInfo di)
	{
		foods.remove(di);
	}

}
