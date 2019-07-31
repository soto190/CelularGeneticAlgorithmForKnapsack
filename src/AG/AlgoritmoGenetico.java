package AG;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class AlgoritmoGenetico {

	protected int numero_generaciones;
	protected int tamano_poblacion = 200;
	protected int numero_padres;
	protected int chromosoma_size;
	protected int current_generation = 0;
	protected double probabilidad_cruza;
	protected double probabilidad_mutacion;

	protected long init_time;
	protected double timeBestSolution;
	protected double totalTime;
	protected int indiceDescartados[];

	protected Individuo individuoGlobal;
	protected Individuo poblacion[];
	protected Individuo descendientes[];

	protected Instance inst;

	AlgoritmoGenetico(Instance inst) {
		this.inst = inst;
		init_time = System.currentTimeMillis();
		chromosoma_size = inst.getNoItems();

		poblacion = new Individuo[tamano_poblacion];
	}

	AlgoritmoGenetico(Instance inst, double probCruza, double probMut,
			int tamPob) {
		this.inst = inst;
		init_time = System.currentTimeMillis();
		chromosoma_size = inst.getNoItems();

		init_time = System.currentTimeMillis();
		probabilidad_cruza = probCruza;
		probabilidad_mutacion = probMut;
		tamano_poblacion = tamPob;
		descendientes = new Individuo[tamPob];
		poblacion = new Individuo[tamPob];

		numero_padres = tamano_poblacion / 2;
		if (numero_padres % 2 != 0)
			numero_padres++;

		descendientes = new Individuo[numero_padres];
		individuoGlobal = new Individuo();
	}

	/**
	 * 
	 * @param probCruza
	 *            between 0 and 1.
	 * @param probMutacion
	 *            between 0 and 1.
	 * @param tamPoblacion
	 *            Positive integer.
	 * @param numeroGeneraciones
	 *            Positive integer.
	 */
	protected void inicializarParametrosGeneticos(double probCruza,
			double probMutacion, int tamPoblacion, int numeroGeneraciones) {

		chromosoma_size = inst.getNoItems();
		probabilidad_cruza = probCruza;
		probabilidad_mutacion = probMutacion;
		tamano_poblacion = tamPoblacion;
		numero_generaciones = numeroGeneraciones;

		poblacion = new Individuo[tamano_poblacion];
		individuoGlobal = new Individuo(new int[chromosoma_size],
				current_generation);

		numero_padres = tamano_poblacion / 2;
		if (numero_padres % 2 != 0)
			numero_padres++;

		descendientes = new Individuo[numero_padres];
	}

	protected Individuo[] generarPoblacionInicial(int size) {
		Random rnd = new Random();
		for (int i = 0; i < size; i++) {
			poblacion[i] = new Individuo(new int[chromosoma_size],
					current_generation);
			int gen = 0;
			while (poblacion[i].getWeight() < inst.getCapacity()) {
				gen = rnd.nextInt(chromosoma_size);
				poblacion[i].setGen(gen, rnd.nextInt(2), inst.getItemGain(gen),
						inst.getItemWeight(gen));
			}

			poblacion[i].flipGen(gen, inst.getItemGain(gen),
					inst.getItemWeight(gen));
		}

		// ordenarPoblacion();
		return poblacion;
	}

	protected void ordenarPoblacion() {
		ordenarBurbuja(poblacion);
	}

	/**
	 * 
	 * @param ind
	 *            An array of the population to be sort.
	 * @return An array with the sorted population.
	 */
	public Individuo[] ordenarBurbuja(Individuo[] ind) {
		Individuo aux;
		for (int j = 0; j < tamano_poblacion; j++)
			for (int i = 0; i < tamano_poblacion - 1; i++)
				if (ind[i].getAptitud() < ind[i + 1].getAptitud()) {
					/*
					 * Si el valor mas a la izquierda es mayor que su
					 * consecutivo de la derecha entonces se realiza el
					 * intercambio tanto en el vector que almacena los valores
					 * objetivos (valores_poblacion) como en la misma poblacion.
					 */

					/* Intercambio en los valores objetivo */
					aux = ind[i];
					ind[i] = ind[i + 1];
					ind[i + 1] = aux;

				}

		return ind;
	}

	protected int[] seleccionarPadresSobranteEstocastico() {

		int padres[] = new int[numero_padres], totalPicked = 0;
		int descartados[] = new int[tamano_poblacion], totalDescartados = 0;
		@SuppressWarnings("unused")
		int enteros = 0;
		double mat_cuadrados[] = new double[tamano_poblacion];
		double desviacion_estandar, sumacuadrados = 0, sumatoria = 0;
		double promedio = 0;
		double matriz[][] = new double[tamano_poblacion][5];

		for (int i = 0; i < tamano_poblacion; i++) {
			sumatoria += poblacion[i].getAptitud();
			mat_cuadrados[i] = poblacion[i].getAptitud()
					* poblacion[i].getAptitud();
			sumacuadrados += mat_cuadrados[i];
		}
		promedio = sumatoria / tamano_poblacion;

		desviacion_estandar = Math
				.sqrt((tamano_poblacion * sumacuadrados - Math
						.pow(sumatoria, 2)) / Math.pow(tamano_poblacion, 2));

		// System.out.println("/*******Seleccionar padres.***************/");

		for (int i = 0; i < tamano_poblacion; i++) {
			matriz[i][0] = i; /* Numero de individuo. */
			matriz[i][1] = poblacion[i].getAptitud(); /* Aptitud. */
			matriz[i][2] = 1 + ((poblacion[i].getAptitud() - promedio))
					/ (2 * desviacion_estandar); /* Valor esperado. */
			if (matriz[i][2] < 0)
				matriz[i][2] *= -1;

			matriz[i][3] = Math.floor(matriz[i][2]); /* enteros */
			matriz[i][4] = matriz[i][2] - matriz[i][3]; /* diferencia */
			enteros += matriz[i][3];

			if (matriz[i][3] > 0 && totalPicked < numero_padres)
				padres[totalPicked++] = i;
			else
				descartados[totalDescartados++] = i;
			// System.out.print(matriz[i][2] + " " +matriz[i][3] +" "+
			// matriz[i][4] +" "+poblacion[i]);
		}
		// System.out.println("/*******Fin seleccionar padres.***********/");

		for (int i = 0; totalPicked < numero_padres; i++) {
			padres[totalPicked++] = descartados[i];
			descartados[i] = descartados[totalDescartados - 1];
			descartados[totalDescartados - 1] = 0;
			totalDescartados--;
		}

		setDescartados(descartados);
		return padres;
	}

	
	public int[] seleccionarPadresEscalamientoSigma() {
		double suma = 0, aux = 0, aleat = 0;
		boolean repetido = false;

		// for(i=0; i<valores_poblacion.length; i++)
		// {
		// System.out.println(valores_poblacion[i] + "----" + i);
		// }

		int i, j;
		int indicePadre[] = new int[numero_padres];
		double mat_cuadrados[] = new double[tamano_poblacion];
		double desviacion, sumacuadrados = 0, sumatoria = 0;
		double mat_esperados[] = new double[tamano_poblacion];
		double promedio = 0;

		double ruleta[][] = new double[tamano_poblacion][5];

		/*
		 * Para la seleccion de los padres se selecciono el metodo de la
		 * escalaamiento sigma
		 */

		for (i = 0; i < tamano_poblacion; i++) {
			sumatoria = sumatoria + poblacion[i].getAptitud();
		}
		promedio = sumatoria / tamano_poblacion;

		for (i = 0; i < tamano_poblacion; i++) {
			mat_cuadrados[i] = poblacion[i].getAptitud()
					* poblacion[i].getAptitud();
		}
		for (i = 0; i < tamano_poblacion; i++) {
			sumacuadrados = sumacuadrados + mat_cuadrados[i];
		}

		desviacion = Math.sqrt((tamano_poblacion * sumacuadrados - Math.pow(
				sumatoria, 2)) / Math.pow(tamano_poblacion, 2));

		if (desviacion != 0) {
			for (i = 0; i < tamano_poblacion; i++) {
				mat_esperados[i] = 1 + (poblacion[i].getAptitud() - promedio)
						/ (2 * desviacion);
			}
		} else {
			for (i = 0; i < tamano_poblacion; i++) {
				mat_esperados[i] = 1.0;
			}
		}

		// for (i = 0; i < tamano_poblacion; i++) {
		// System.out.println("---" + mat_esperados[i]);
		//
		// }

		// ------------------------------------------------------------------------------

		/*
		 * Primero se debe crear la tabla de frecuencias relativas (ruleta)
		 * empezamos llenando las dos primeras columnas
		 */
		suma = 0;
		for (i = 0; i < tamano_poblacion; i++) {
			ruleta[i][0] = i;
			ruleta[i][1] = poblacion[i].getAptitud();
			suma += poblacion[i].getAptitud();
		}

		/*
		 * Ahora, para construir la tercera columna, se tomara en cuenta el
		 * valor de la variable acumuladora "suma", obteniendo la frecuencia
		 * relativa
		 */
		for (i = 0; i < tamano_poblacion; i++)
			ruleta[i][2] = ruleta[i][1] / suma;

		/*
		 * Una vez hecho esto, es necesario invertir las frecuencias relativas
		 * de modo que el de menor valor objetivo tenga la mayor probabilidad de
		 * ser elegido
		 */
		for (i = 0; i < tamano_poblacion / 2; i++) {
			aux = ruleta[i][2];
			ruleta[i][2] = ruleta[tamano_poblacion - i - 1][2];
			ruleta[tamano_poblacion - i - 1][2] = aux;
		}

		/*
		 * Lo que sigue es crear los rangos de las frecuencias, el primero y el
		 * ultimo se calculan fuera del ciclo
		 */
		ruleta[0][3] = 0;
		ruleta[0][4] = ruleta[0][2];

		for (i = 1; i < tamano_poblacion; i++) {
			ruleta[i][3] = ruleta[i - 1][4];
			ruleta[i][4] = ruleta[i][3] + ruleta[i][2];
		}
		ruleta[tamano_poblacion - 1][4] = 1;

		/*
		 * Ahora es necesario seleccionar los padres, el numero de padres
		 * seleccionados se encuentra definido en la variable "numero_padres"
		 */
		int apt_padres = 0;
		for (i = 0; i < numero_padres; i++) {
			/* Genera un numero aleatorio */
			aleat = Math.random();

			/* Verifica dentro de cual rango cayo el aleatorio */
			for (j = 0; j < tamano_poblacion; j++) {
				/* Si se encuentra el padre seleccionado */
				if (ruleta[j][3] < aleat && ruleta[j][4] >= aleat) {
					repetido = false;
					/* Checar que no haya sido seleccionado con anterioridad */
					for (int k = 0; k < apt_padres; k++) {
						/* si el padre ya se encuentra seleccionado */
						if (ruleta[j][0] == indicePadre[k]) {
							repetido = true;
							i--;
						}
					}

					if (repetido == false) {
						/*
						 * Si el padre no habia sido seleccionado con
						 * anterioridad entonces el id se copia a
						 * "indice_padres_seleccionados" y el padre a la matriz
						 * "padres"
						 */

						indicePadre[apt_padres] = (int) ruleta[j][0];
						apt_padres++;
					}
					j = tamano_poblacion;
				}
			}
		}
		
		int des[] = new int[tamano_poblacion/2];
		for (int k = 0; k < tamano_poblacion/2; k++) 
			des[k] = (tamano_poblacion/2) + k;
		
		setDescartados(des);
		
		return indicePadre;
	}
	/**
	 * Selecciona diferentes puntos del padre 1 y acompleta tomando los valores
	 * del padre con los puntos no seleccionados para generar el hijo 1.
	 * 
	 * @param indicePadre
	 * @return
	 */
	protected Individuo[] cruza(int[] indicePadre) {
		double num_aleat;
		Random rand = new Random();

		for (int i = 0; i < numero_padres; i += 2) {

			num_aleat = Math.random();
			if (num_aleat <= probabilidad_cruza) {

				descendientes[i] = new Individuo(poblacion[indicePadre[i]],
						poblacion[indicePadre[i + 1]], current_generation);

				descendientes[i + 1] = new Individuo(
						poblacion[indicePadre[i + 1]],
						poblacion[indicePadre[i]], current_generation);

				/* 1.- Seleccionar puntos aleatorios. */

				/*
				 * Si true se mantiene, si false se intercambia por el alelo del
				 * padre2.
				 */
				for (int l = 0; l < chromosoma_size; l++) {
					if (!rand.nextBoolean())
						descendientes[i].setGen(l,
								poblacion[indicePadre[i + 1]].getGen(l),
								inst.getItemGain(l), inst.getItemWeight(l));

					if (!rand.nextBoolean())
						descendientes[i + 1].setGen(l,
								poblacion[indicePadre[i]].getGen(l),
								inst.getItemGain(l), inst.getItemWeight(l));
				}

			} else {
				descendientes[i] = new Individuo(poblacion[indicePadre[i]]);

				descendientes[i + 1] = new Individuo(
						poblacion[indicePadre[i + 1]]);
			}
		}
		return descendientes;
	}

	protected Individuo[] getDescendientes() {
		return this.descendientes;
	}

	private void setDescartados(int[] descartados) {
		this.indiceDescartados = descartados.clone();
	}

	protected int[] getDescartados() {
		return this.indiceDescartados;
	}

	protected Individuo[] getPoblacion() {
		return this.poblacion;
	}

	protected void setCurrentGeneration(int generation) {
		this.current_generation = generation;
	}

	protected int getCurrentGeneration() {
		return this.current_generation;
	}

	protected void setPoblacion(Individuo[] poblacion) {
		this.poblacion = poblacion.clone();
	}

	protected int increaseGeneration() {
		return this.current_generation++;
	}

	protected Individuo[] mutacion(Individuo[] ind, int TYPE) {
		if (TYPE == 1) {
			Random rnd = new Random();

			for (int i = 0; i < ind.length; i++)
				if (rnd.nextDouble() < probabilidad_mutacion)
					ind[i] = mutar(ind[i]);

			return ind;
		} else if (TYPE == 2) {
			for (int i = 0; i < ind.length; i++)
				ind[i] = mutacion2(ind[i]);
			return ind;
		}
		return null;
	}

	protected Individuo mutar(Individuo ind) {
		Random rnd = new Random();
		int pos = rnd.nextInt(chromosoma_size);
		if (ind.getGen(pos) == 1)
			ind.setGen(pos, 0);
		else
			ind.setGen(pos, 1);

		return ind;
	}

	protected Individuo[] generarNuevaPoblacion(int[] descartados,
			Individuo[] descendientes, Individuo[] poblacion) {

		for (int i = 0; i < numero_padres; i++)
			poblacion[descartados[i]].copyThis(descendientes[i]);
		return poblacion;
	}

	/**
	 * 
	 * @param ind
	 *            Individuo to be evaluate and transform in a feasible
	 *            Individuo.
	 * @return A feasible Individuo.
	 * 
	 */

	protected Individuo evaluarIndividuo(Individuo ind) {
		Random rnd = new Random();

		ind.setWeight(0);
		ind.setGain(0);
		ind.setTotalItemsInTheBin(0);

		for (int i = 0; i < chromosoma_size; i++)
			if (ind.getGen(i) == 1)
				ind.addItem(i, inst.getItemGain(i), inst.getItemWeight(i));

		int tmp = ind.getTotalItemsInTheBin();

		while (ind.getWeight() > inst.getCapacity()) {
			int index = rnd.nextInt(tmp);
			int item = ind.getItems()[index];
			ind.removeItem(item, inst.getItemGain(item),
					inst.getItemWeight(item));
		}
		ind.setFeasible(1);
		ind.setAptitud(ind.getGain() * 0.01);

		return ind;
	}

	protected void evaluarPoblacion() {
		for (int i = 0; i < tamano_poblacion; i++)
			poblacion[i] = evaluarIndividuo(poblacion[i]);

	}

	/**
	 * @param timeBestSolution
	 *            the timeBestSolution to set
	 */
	public void setTimeBestSolution(double timeBestSolution) {
		this.timeBestSolution = timeBestSolution;
	}

	/**
	 * @return the timeBestSolution
	 */
	public double getTimeBestSolution() {
		return timeBestSolution;
	}

	/**
	 * @param totalTime
	 *            the totalTime to set
	 */
	public void setTotalTime(double totalTime) {
		this.totalTime = totalTime;
	}

	public double getProbabilidadCruza() {
		return this.probabilidad_cruza;
	}

	public int getChromosomaSize() {
		return this.chromosoma_size;
	}

	protected Individuo[] generarPoblacionElite(int tambPobl) {
		poblacion[0] = new Individuo(new int[chromosoma_size],
				current_generation);

		for (int i = 0; i < chromosoma_size; i++)
			if (poblacion[0].getWeight() + inst.getItem(i).getWeight() <= inst
					.getCapacity())
				poblacion[0].setGen(inst.getItem(i).getId(), 1, inst.getItem(i)
						.getGain(), inst.getItem(i).getWeight());

		for (int i = 1; i < tambPobl; i++)
			poblacion[i] = mutacion2(new Individuo(poblacion[0]));

		return poblacion;

	}

	private Individuo mutacion2(Individuo ind) {

		Random rnd = new Random();
		int totalGenesToChange = (int) (chromosoma_size * 0.005);

		for (int i = 0; i < totalGenesToChange; i++) {

			int genToTurnOff = rnd.nextInt(chromosoma_size);
			ind.setGen(genToTurnOff, 0, inst.getItemGain(genToTurnOff),
					inst.getItemWeight(genToTurnOff));

		}
		int genToTurnOn = rnd.nextInt(chromosoma_size);

		while (ind.getWeight() + inst.getItemWeight(genToTurnOn) <= inst
				.getCapacity()) {
			ind.setGen(genToTurnOn, 1, inst.getItemGain(genToTurnOn),
					inst.getItemWeight(genToTurnOn));
			genToTurnOn = rnd.nextInt(chromosoma_size);
		}
		return ind;
	}

	/**
	 * @return the totalTime
	 */
	public double getTotalTime() {
		return totalTime;
	}

	public void saveResults(String file) {
		String workPath = System.getProperty("user.dir") + File.separator
				+ "Experiments" + File.separator + file;
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File(
					workPath), true));

			bw.write("" + this);
			bw.flush();
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void start(double probCruza, double probMut, int tamPob, int numGen) {

		inicializarParametrosGeneticos(probCruza, probMut, tamPob, numGen);
		generarPoblacionElite(tamPob);

		ordenarPoblacion();

		individuoGlobal.copyThis(poblacion[0]);

		for (current_generation = 1; current_generation < numero_generaciones; current_generation++) {
			int[] padres = seleccionarPadresSobranteEstocastico();
//			 int[] padres = seleccionarPadresEscalamientoSigma();

			descendientes = cruza(padres); // cruzaPadresCrossover

			descendientes = mutacion(getDescendientes(), 2); // mutarHijosFlip

			poblacion = generarNuevaPoblacion(getDescartados(),
					getDescendientes(), getPoblacion());

			evaluarPoblacion();
			ordenarPoblacion();

			for (int i = 0; i < 10; i++)
				if (poblacion[i].getAptitud() > individuoGlobal.getAptitud()) {
					individuoGlobal.copyThis(poblacion[i]);
					setTimeBestSolution((System.currentTimeMillis() - init_time) / 1000.0);
				}
			
			setTotalTime((System.currentTimeMillis() - init_time) / 1000.0);
			System.out.println("IG ->" + individuoGlobal);
			System.out.print("->>");

		}
	}

	public String toString() {
		return String
				.format("Instance: %7s AG: %1.2f probCruza %1.2f probMutacion %4s TamPob %4s NumGener TotalTime  %6.6f seg BestTime   %6.6f seg %s ",
						inst, this.probabilidad_cruza,
						this.probabilidad_mutacion, this.tamano_poblacion,
						this.numero_generaciones, this.getTotalTime(),
						this.getTimeBestSolution(), this.individuoGlobal);
	}

	public static void main(String[] args) {

		Instance instance = Instance.readInstance(Instance.getInstance(1));
		AlgoritmoGenetico bpp = new AlgoritmoGenetico(instance);
		bpp.start(0.8, 0.1, 600, 1000);
		// bpp.saveResults("E7-parametros");
	}
}
