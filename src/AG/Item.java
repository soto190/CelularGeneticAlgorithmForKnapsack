package AG;

/**
 * Esta clase no se usa. Pendiente por adaptar el código para usar esta clase.
 * 
 * @author soto190
 * 
 */
public class Item {

	private int id;
	private int gain;
	private int weight;
	private double quality;

	public Item(int id, int gain, int weight) {

		this.id = id;
		this.gain = gain;
		this.weight = weight;
		this.quality = gain / (weight * 1.0);
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the gain
	 */
	public int getGain() {
		return gain;
	}

	/**
	 * @param gain
	 *            the gain to set
	 */
	public void setGain(int gain) {
		this.gain = gain;
	}

	/**
	 * @return the weight
	 */
	public int getWeight() {
		return weight;
	}

	/**
	 * @param weight
	 *            the weight to set
	 */
	public void setWeight(int weight) {
		this.weight = weight;
	}

	/**
	 * @return the quality
	 */
	public double getQuality() {
		return quality;
	}

	/**
	 * @param quality
	 *            the quality to set
	 */
	public void setQuality(double quality) {
		this.quality = quality;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 **/
	@Override
	public String toString() {
		return "[id=" + id + ", gain=" + gain + ", weight=" + weight
				+ ", quality=" + quality + "]";
	}

}