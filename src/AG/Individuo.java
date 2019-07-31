package AG;

public class Individuo {
	static int totalIndividuos = 0;
	int id;
	int[] chromosoma;
	int[] item;
	int weight;
	double gain;
	int totalItemsInTheBin = 0;
	double fitness;
	boolean feasible;
	int generation;
	int[] idPadre = new int[2];

	public Individuo() {
	}

	public Individuo(Individuo ind) {
		this.id = totalIndividuos++;
		this.generation = ind.getGeneration() + 1;
		this.fitness = ind.getAptitud();
		this.weight = ind.getWeight();
		this.gain = ind.getGain();
		this.chromosoma = ind.getChromosoma();

		// this.itemGain = ind.getItemsGains();
		this.item = ind.getItems();
		this.totalItemsInTheBin = ind.getTotalItemsInTheBin();

		this.item = ind.getItems();

		this.idPadre[0] = ind.getId();
		this.idPadre[1] = -1;

	}

	public Individuo(Individuo padre1, Individuo padre2, int generation) {
		this.id = totalIndividuos++;
		this.generation = generation;
		this.fitness = padre1.getAptitud();
		this.weight = padre1.getWeight();
		this.gain = padre1.getGain();

		this.chromosoma = padre1.getChromosoma();
		this.idPadre[0] = padre1.getId();
		this.idPadre[1] = padre2.getId();

		this.item = padre1.getItems();

		// this.itemGain = padre1.getItemsGains();
		// this.item = padre1.getItems();
		// this.totalItemsInTheBin = padre1.getNItems();

	}

	public Individuo(int[] chromosoma, int generation) {
		super();

		this.id = totalIndividuos++;
		this.generation = generation;

		this.chromosoma = chromosoma.clone();

		this.item = new int[chromosoma.length];

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
	 * @return the chromosoma
	 */
	public int[] getChromosoma() {
		return chromosoma.clone();
	}

	/**
	 * @param chromosoma
	 *            the chromosoma to set
	 */
	public void setChromosoma(int[] chromosoma) {
		this.chromosoma = chromosoma;
	}

	public void setGen(int pos, int alelo) {
		this.chromosoma[pos] = alelo;
	}

	public void setGen(int pos, int alelo, int gain, int weight) {

		if (this.chromosoma[pos] == 0 && alelo == 1) {
			this.chromosoma[pos] = alelo;
			this.weight += weight;
			this.gain += gain;
			this.fitness = this.gain * 0.01;
			this.totalItemsInTheBin++;
		}
		if (this.chromosoma[pos] == 1 && alelo == 0) {
			this.chromosoma[pos] = alelo;
			this.weight -= weight;
			this.gain -= gain;
			this.fitness = this.gain * 0.01;
			this.totalItemsInTheBin--;
		}

	}

	public void flipGen(int gen, Item item) {
		if (this.chromosoma[gen] == 0) {
			this.chromosoma[gen] = 1;
			this.weight += item.getWeight();
			this.gain += item.getGain();
			this.fitness = this.gain * 0.01;
		}
		if (this.chromosoma[gen] == 1) {
			this.chromosoma[gen] = 0;
			this.weight -= item.getWeight();
			this.gain -= item.getGain();
			this.fitness = this.gain * 0.01;
		}

	}

	public void flipGen(int gen, int gain, int weight) {
		if (this.chromosoma[gen] == 0) {
			this.chromosoma[gen] = 1;
			this.weight += weight;
			this.gain += gain;
			this.fitness = this.gain * 0.01;
			this.totalItemsInTheBin++;
		}
		if (this.chromosoma[gen] == 1) {
			this.chromosoma[gen] = 0;
			this.weight -= weight;
			this.gain -= gain;
			this.fitness = this.gain * 0.01;
			this.totalItemsInTheBin--;

		}

	}

	public int getGen(int pos) {
		return this.chromosoma[pos];
	}

	/**
	 * @return the generation
	 */
	public int getGeneration() {
		return generation;
	}

	/**
	 * @param generation
	 *            the generation to set
	 */
	public void setGeneration(int generation) {
		this.generation = generation;
	}

	/**
	 * @return the idPadre
	 */
	public int[] getIdPadre() {
		return idPadre;
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
	 * @return the gain
	 */
	public double getGain() {
		return gain;
	}

	/**
	 * @param gain
	 *            the gain to set
	 */
	public void setGain(double gain) {
		this.gain = gain;
	}

	/**
	 * @return the aptitud
	 */
	public double getAptitud() {
		return fitness;
	}

	/**
	 * @param aptitud
	 *            the aptitud to set
	 */
	public void setAptitud(double aptitud) {
		this.fitness = aptitud;
	}

	/**
	 * @return the bins
	 */
	public int getTotalItemsInTheBin() {
		return totalItemsInTheBin;
	}

	public void setTotalItemsInTheBin(int n) {
		this.totalItemsInTheBin = n;
	}

	public int[] getItems() {
		return this.item.clone();
		// return null;
	}

	public void addItem(int item, int gain) {
		this.chromosoma[item] = 1;
		this.gain += gain;
		this.item[totalItemsInTheBin++] = item;
	}

	public void removeItem(int item, int weight, int gain) {
		if (this.chromosoma[item] == 1) {
			/*
			 * for (int i = 0; i < this.item.length; i++) if (item ==
			 * this.item[i]) { this.item[i] = 0; i = this.item.length; }
			 */
			this.chromosoma[item] = 0;
			this.weight -= weight;
			this.gain -= gain;
			this.totalItemsInTheBin--;
		}
	}

	public boolean equals(Individuo ind) {

		// implementar algo para probar con el id.
		for (int gen = 0; gen < this.chromosoma.length; gen++)
			if (this.chromosoma[gen] != ind.chromosoma[gen])
				return false;

		return true;
	}

	public void setFeasible(int is) {
		if (is == 1)
			this.feasible = true;
		else
			this.feasible = false;
	}

	public boolean isFeasible() {
		return this.feasible;
	}

	public void addItem(int item, int weight, int gain) {
		this.chromosoma[item] = 1;
		this.weight += weight;
		this.gain += gain;
		this.item[totalItemsInTheBin++] = item;

	}

	public void addWeight(int val) {
		this.weight += val;
	}

	public void addGain(int val) {
		this.gain += val;
	}

	public void copyThis(Individuo ind) {

		this.id = ind.getId();
		this.generation = ind.getGeneration();
		this.fitness = ind.getAptitud();
		this.gain = ind.getGain();
		this.weight = ind.getWeight();
		this.feasible = ind.isFeasible();
		this.totalItemsInTheBin = ind.getTotalItemsInTheBin();
		this.chromosoma = ind.getChromosoma();
		this.idPadre[0] = ind.idPadre[0];
		this.idPadre[1] = ind.idPadre[1];
		this.item = ind.getItems();

		// this.itemGain = ind.getItemsGains();
		// this.item = ind.getItems();
		// this.totalItemsInTheBin = ind.getNItems();
	}

	public String getX() {
		String text = "";
		for (int i = 0; i < chromosoma.length; i++)
			if (chromosoma[i] == 1)
				text += "x" + (i + 1) + "\n";

		return text;
	}

	public String toString() {

		@SuppressWarnings("unused")
		String textC = "[";
		for (int i = 0; i < chromosoma.length; i++)
			textC += String.format("%1d"
					+ (i < chromosoma.length - 1 ? "|" : "]\n"), chromosoma[i]);

		String text2 = String
				.format("[Id: %7d | Generation: %3d | Fitness: %6.2f | Gain: %7.2f | Weight: %5d | Feasible: %5s | Items: %7d]",
						this.getId(), this.generation, this.getAptitud(),
						this.getGain(), this.getWeight(), this.isFeasible(),
						this.totalItemsInTheBin);

		return text2 + "\n"; // + " " + textC;
	}

}