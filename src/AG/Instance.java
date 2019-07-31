package AG;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import Tools.Utilities;

/**
 * 
 * @author soto190 Instancia para el problema Knapsack.
 */
public class Instance {

	private String name;
	private int[][] items;
	private double[] quality;
	private int NoItems;
	private int capacity;
	private int number;

	private int totalItems;
	private Item item[];
	private int indexItem[];

	/**
	 * @return the totalItems
	 */
	public int getTotalItems() {
		return totalItems;
	}

	/**
	 * @param totalItems the totalItems to set
	 */
	public void setTotalItems(int totalItems) {
		this.totalItems = totalItems;
	}

	/**
	 * @return the item
	 */
	public Item getItem(int index) {
		return item[index];
	}

	/**
	 * @param item the item to set
	 */
	public void setItem(Item[] item) {
		this.item = item;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the items
	 */
	public int[][] getItems() {
		return items;
	}
	
	public int getItemWeight(int item){
		return items[item][1];
	}
	
	public int getItemGain(int item){
		return items[item][0];
	}

	/**
	 * @param items
	 *            the items to set
	 */
	public void setItems(int[][] items) {
		this.items = items;
	}

	public void setItem(int index, int weight, int gain) {
		this.items[index][0] = gain;/* gain */
		this.items[index][1] = weight;/* weight */

		this.indexItem[totalItems] = totalItems;
		this.item[totalItems++] = new Item(index, gain, weight);
		
		
	}

	/**
	 * @return the noItems
	 */
	public int getNoItems() {
		return NoItems;
	}

	/**
	 * @param noItems
	 *            the noItems to set
	 */
	public void setNoItems(int noItems) {
		this.NoItems = noItems;
		this.items = new int[noItems][2];
		this.item = new Item[noItems];
		this.indexItem = new int[noItems];
	}

	/**
	 * @return the capacity
	 */
	public int getCapacity() {
		return capacity;
	}

	/**
	 * @param capacity
	 *            the capacity to set
	 */
	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	/**
	 * @return the number
	 */
	public int getNumber() {
		return number;
	}

	/**
	 * @param number
	 *            the number to set
	 */
	public void setNumber(int number) {
		this.number = number;
	}

	public static Instance readInstance(String instance) {
		Instance inst = new Instance();
		try {
			BufferedReader br = new BufferedReader(new FileReader(instance));

			String[] ndat = instance.split("\\\\");

			inst.setName(ndat[ndat.length - 1]);
			inst.setNoItems(Utilities.toInt(br.readLine()));
			String dat = br.readLine();
			inst.setCapacity(Utilities.toInt(br.readLine()));
			dat = br.readLine();
			String[] data;

			for (int i = 0; i < inst.getNoItems(); i++) {
				data = br.readLine().split("\\s+");
				inst.setItem(i, Utilities.toInt(data[0]),
						Utilities.toInt(data[1]));
			}

			inst.sortItemsByQuality();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return inst;
	}

	private void sortItemsByQuality() {
		Item aux;
		for (int j = 0; j < this.NoItems; j++)
			for (int i = 0; i < this.NoItems - 1; i++)
				if (this.item[i].getQuality() < this.item[i + 1].getQuality()) {
					
					aux = this.item[i];
					this.item[i] = this.item[i + 1];
					this.item[i + 1] = aux;
					
					this.indexItem[item[i].getId()] = i;
					this.indexItem[item[i + 1].getId()] = i + 1;
					
				}
	}

	public static String getInstance(int n_n) {

		String workPath = System.getProperty("user.dir") + File.separator
				+ "Instances" + File.separator;

		String nameInst = null;
		try {
			BufferedReader br = new BufferedReader(new FileReader(workPath
					+ "Index"));

			for (int i = 0; i < n_n; i++)
				nameInst = br.readLine();

			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return workPath + nameInst.trim();
	}

	@Override
	public String toString() {

		return this.name;
	}

	public static void main(String[] args) {

		Instance inst = Instance.readInstance(getInstance(2));

		System.out.println(inst);
		Utilities.printMatrix(inst.getItems());
		
		for (int i = 0; i < inst.getTotalItems(); i++) {
			System.out.println(inst.getItem(i));
		}

	}

}
