package AG;

public abstract class Celula extends AlgoritmoGenetico {
	protected int id;
	protected boolean stagnated = false;
	protected static Instance inst;
	protected int generationsWithoutImprovement = 0;

	public Celula(Instance inst, double probCruza, double probMut, int tamPob) {
		super(inst, probCruza, probMut, tamPob);
		Celula.inst = inst;
	}

	protected boolean isStagnated() {
		return stagnated;
	}

	protected void setStagnated() {
		stagnated = true;
		System.out.println("Celula " + id + " se ha estancado...");

	}
	/**
	 * Reinicia la celula.
	 */
	protected void restart() {
		stagnated = false;
		generationsWithoutImprovement = 0;
		System.out.println("Celula " + id + " se ha reiniciado..");
	}
	
	/**
	 * Reinicia la celula cambiando la poblacion.
	 * @param poblacion
	 */

	protected void restart(Individuo[] poblacion) {
		stagnated = false;
		generationsWithoutImprovement = 0;
		this.poblacion = poblacion.clone();

		System.out.println("Celula " + id + " se ha reiniciado..");
	}
	
	
	/**
	 * Reinicia la celula con una nueva solucion global y poblacion.
	 * Cambia el espacio de busqueda.
	 * @param solGlobal
	 * @param poblacion
	 */
	
	protected void restart(Individuo solGlobal, Individuo[] poblacion) {
		stagnated = false;
		generationsWithoutImprovement = 0;
		this.poblacion = poblacion.clone();
		this.individuoGlobal.copyThis(solGlobal);

		System.out.println("Celula " + id + " se ha reiniciado..");
	}
	
	/**
	 *  Reinicia la celula con una nueva solucion global.
	 * @param solGlobal
	 */
	protected void restart(Individuo solGlobal) {
		stagnated = false;
		generationsWithoutImprovement = 0;
		this.individuoGlobal.copyThis(solGlobal);
		
		System.out.println("Celula " + id + " se ha reiniciado..");
	}

	protected Individuo getIndividuoGlobal() {
		return this.individuoGlobal;
	}

	public void start(Individuo[] pob, double probCruza, double probMut,
			int tamPob, int numGen) {

		System.out.println("Started cell: " + id);
		inicializarParametrosGeneticos(probCruza, probMut, tamPob, numGen);
		poblacion = pob.clone();
		ordenarPoblacion();

		individuoGlobal.copyThis(poblacion[0]);

		while (!isStagnated()) {
			increaseGeneration();
			int[] padres = seleccionarPadresSobranteEstocastico();

			descendientes = cruza(padres); // cruzaPadresCrossover

			descendientes = mutacion(getDescendientes(), 2); // mutarHijosFlip

			poblacion = generarNuevaPoblacion(getDescartados(),
					getDescendientes(), getPoblacion());

			evaluarPoblacion();
			ordenarPoblacion();
			boolean improve = false;
			for (int i = 0; i < 10; i++)
				if (poblacion[i].getAptitud() > individuoGlobal.getAptitud()) {
					individuoGlobal.copyThis(poblacion[i]);
					setTimeBestSolution((System.currentTimeMillis() - init_time) / 1000.0);
					improve = true;
				}

			if (improve)
				generationsWithoutImprovement = 0;
			else if (generationsWithoutImprovement++ >= 25) {
				setStagnated();
			}
			setTotalTime((System.currentTimeMillis() - init_time) / 1000.0);
			// System.out.println("IG ->" + individuoGlobal);

		}

	}

	public void nextGeneration() {

		// System.out.println("Ejecutando celula " + id);
		increaseGeneration();
		int[] padres = seleccionarPadresSobranteEstocastico();

		descendientes = cruza(padres); // cruzaPadresCrossover

		descendientes = mutacion(getDescendientes(), 2); // mutarHijosFlip

		poblacion = generarNuevaPoblacion(getDescartados(), getDescendientes(),
				getPoblacion());

		evaluarPoblacion();
		ordenarPoblacion();
		boolean improve = false;
		for (int i = 0; i < 10; i++)
			if (poblacion[i].getAptitud() > individuoGlobal.getAptitud()) {
				individuoGlobal.copyThis(poblacion[i]);
				setTimeBestSolution((System.currentTimeMillis() - init_time) / 1000.0);
				improve = true;
			}

		if (improve)
			generationsWithoutImprovement = 0;
		else if (generationsWithoutImprovement++ == 50)
			setStagnated();
		
		setTotalTime((System.currentTimeMillis() - init_time) / 1000.0);
		// System.out.println("IG ->" + individuoGlobal);

	}

	public static Instance getInst() {
		return inst;
	}

	public static void setInst(Instance inst) {
		Celula.inst = inst;
	}
}
