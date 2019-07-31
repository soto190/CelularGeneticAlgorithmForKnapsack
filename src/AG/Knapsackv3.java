package AG;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import Tools.Utilities;

/**
 * 
 * @author soto190
 * 
 */
public class Knapsackv3 {

	private int numero_generaciones;
	private int tamano_poblacion;
	private int numero_padres;
	private int chromosoma_size;
	private int current_generation;
	private double probabilidad_cruza;
	private double probabilidad_mutacion;

	private Individuo individuoGlobal;
	private Individuo poblacion[];
	private Individuo padre[];
	private Individuo descendientes[];

	// int indicePadres[];
	int indiceDescartados[];

	Instance inst;
	long init_time;
	double timeBestSolution;
	double totalTime;

	Knapsackv3(Instance inst) {
		this.inst = inst;
		init_time = System.currentTimeMillis();
	}

	/**
	 * 
	 * @param probCruza
	 *            Probabilidad de cruza entre 0 y 1.
	 * @param probMut
	 *            Probabilidad de mutación entre 0 y 1.
	 * @param tamPob
	 *            Tamaño de la poblacion.
	 * @param numGen
	 *            Número de generaciones.
	 */
	public void AlgoritmoGenetico(double probCruza, double probMut, int tamPob,
			int numGen) {

		inicializarParametrosGeneticos(probCruza, probMut, tamPob, numGen);
		generarPoblacionInicial();
		// generarPoblacionElite();

		// evaluarPoblacion();
		ordenarPoblacion();

		Utilities.printObject(poblacion);
		individuoGlobal.copyThis(poblacion[0]);
		System.out.print("->>");
		// Utilities.printObject(poblacion);

		for (current_generation = 1; current_generation < numero_generaciones; current_generation++) {
			int[] padres = seleccionarPadresSobranteEstocastico();
			// int[] padres = seleccionarPadresEscalamientoSigma();

			descendientes = cruzaPadresCrossover(padres); // descendientes =
															// cruzaDosPuntos(padres);

			descendientes = mutarHijosFlip(getDescendientes()); // descendientes
																// =
																// mutarHijosInsercion(getDescendientes());

			descendientes = busquedaLocalBest(descendientes);
			poblacion = generarNuevaPoblacion(getDescartados(),
					getDescendientes(), getPoblacion());

			evaluarPoblacion();
			ordenarPoblacion();

			for (int i = 0; i < 10; i++)
				if (poblacion[i].getAptitud() > individuoGlobal.getAptitud()) {
					individuoGlobal.copyThis(poblacion[i]);
					setTimeBestSolution((System.currentTimeMillis() - init_time) / 1000.0);

				}
			// poblacion[11].copyThis(individuoGlobal);

			// System.out.println("->>>");
			// Utilities.printObject(descendientes);
			// System.out.println("->>>");

			// System.out.println("IG -> " + individuoGlobal);
		}
		setTotalTime((System.currentTimeMillis() - init_time) / 1000.0);
		System.out.println("IG ->" + individuoGlobal);
		System.out.print("->>");

		// System.out.println(individuoGlobal.getX());
	}

	/**
	 * 
	 * @param probCruza
	 *            Probabilidad de cruza entre 0 y 1.
	 * @param probMut
	 *            Probabilidad de mutación entre 0 y 1.
	 * @param tamPob
	 *            Tamaño de la poblacion.
	 * @param numGen
	 *            Número de generaciones.
	 */

	public void AlgoritmoMemetico(double probCruza, double probMut, int tamPob,
			int numGen) {

		inicializarParametrosGeneticos(probCruza, probMut, tamPob, numGen);
		generarPoblacionInicial();

		 poblacion = busquedaLocalBestV3(getPoblacion());
//		 poblacion = busquedaLocalIterada(getPoblacion());
//		poblacion = busquedaLocalVNSv2(getPoblacion());

		ordenarPoblacion();

		// Utilities.printObject(poblacion);
		individuoGlobal.copyThis(poblacion[0]);
		System.out.print("->>");
		// Utilities.printObject(poblacion);

		for (current_generation = 1; current_generation < numero_generaciones; current_generation++) {
			int[] padres = seleccionarPadresSobranteEstocastico();

			descendientes = cruzaPadresCrossover(padres);
			descendientes = mutarHijosFlip(getDescendientes());

			// descendientes = busquedaLocalBest(getDescendientes());
			// descendientes = busquedaLocalBestV2(getDescendientes());
			// descendientes = busquedaLocalBestV3(getDescendientes());
			// descendientes = busquedaLocalIterada(getDescendientes());
			// descendientes = busquedaLocalVNSv2(getDescendientes());

			poblacion = generarNuevaPoblacion(getDescartados(),
					getDescendientes(), getPoblacion());

			evaluarPoblacion();
			ordenarPoblacion();

			for (int i = 0; i < 10; i++)
				if (poblacion[i].getAptitud() > individuoGlobal.getAptitud()) {
					individuoGlobal.copyThis(poblacion[i]);
					setTimeBestSolution((System.currentTimeMillis() - init_time) / 1000.0);
				}
		}

		setTotalTime((System.currentTimeMillis() - init_time) / 1000.0);
		System.out.println("IG ->" + individuoGlobal);
		System.out.print("->>" + getTotalTime());

	}

	/**
	 * private Individuo[] busquedaLocal(Individuo[] sol){
	 * 
	 * for (int i = 0; i < sol.length; i++) { for (int gen = 0; gen <
	 * inst.getNoItems(); gen++) { } } }
	 **/

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
	private void inicializarParametrosGeneticos(double probCruza,
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

		padre = new Individuo[tamano_poblacion];
		descendientes = new Individuo[numero_padres];
	}

	private void generarPoblacionInicial() {
		Random rnd = new Random();
		for (int i = 0; i < tamano_poblacion; i++) {
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
	}

	private void generarPoblacionElite() {
		
		
		for (int i = 0; i < tamano_poblacion; i++) {
			poblacion[i] = new Individuo(new int[chromosoma_size],
					current_generation);
			for (int j = 0; j < chromosoma_size; j++) {
				poblacion[i].setGen(inst.getItem(j).getId(), 1);
				poblacion[i].setWeight(poblacion[i].getWeight()
						+ inst.getItem(j).getWeight());
				if (poblacion[i].getWeight() > inst.getCapacity())
					j = chromosoma_size;
			}

			poblacion[i] = mutar(poblacion[i]);
		}
	}

	private Individuo mutar(Individuo ind) {

		Random rnd = new Random();
		int totalGenesToChange = (int) (chromosoma_size * 0.10);
		for (int i = 0; i < totalGenesToChange; i++) {
			int genToTurnOff = rnd.nextInt(chromosoma_size);
			int genToTurnOn = rnd.nextInt(chromosoma_size);

			ind.setGen(genToTurnOff, 0, inst.getItemGain(genToTurnOff),
					inst.getItemWeight(genToTurnOff));

			if (ind.getWeight() + inst.getItemWeight(genToTurnOn)
					- inst.getItemWeight(genToTurnOff) <= inst.getCapacity())
				ind.setGen(genToTurnOn, 1, inst.getItemGain(genToTurnOn),
						inst.getItemWeight(genToTurnOn));

		}

		return ind;
	}

	private void ordenarPoblacion() {
		ordenarBurbuja(poblacion);
	}

	private int[] seleccionarPadresSobranteEstocastico() {

		int padres[] = new int[numero_padres], totalPicked = 0;
		int descartados[] = new int[tamano_poblacion], totalDescartados = 0;
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
		return indicePadre;
	}

	private Individuo[] cruzaDosPuntos(int[] indicePadre) {
		double num_aleat;

		for (int i = 0; i < numero_padres; i += 2) {

			num_aleat = Math.random();
			if (num_aleat <= probabilidad_cruza) {

				descendientes[i] = new Individuo(poblacion[indicePadre[i]],
						poblacion[indicePadre[i + 1]], current_generation);

				descendientes[i + 1] = new Individuo(
						poblacion[indicePadre[i + 1]],
						poblacion[indicePadre[i]], current_generation);

				for (int l = 0; l < chromosoma_size; l++) {
					if (l < chromosoma_size / 2)
						descendientes[i].setGen((l + chromosoma_size / 2),
								poblacion[indicePadre[i + 1]].getGen(l));

					else
						descendientes[i + 1].setGen(l - chromosoma_size / 2,
								poblacion[indicePadre[i]].getGen(l));
				}

			} else {
				descendientes[i] = new Individuo(poblacion[indicePadre[i]]);

				descendientes[i + 1] = new Individuo(
						poblacion[indicePadre[i + 1]]);
			}
		}

		return descendientes;
	}

	/**
	 * Selecciona diferentes puntos del padre 1 y acompleta tomando los valores
	 * del padre con los puntos no seleccionados para generar el hijo 1.
	 * 
	 * @param indicePadre
	 * @return
	 */
	private Individuo[] cruzaPadresCrossover(int[] indicePadre) {
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

	private Individuo[] cruzarPadresPositionBasedCrossover(int[] indicePadre) {

		Individuo hijo[] = new Individuo[2], padre1tmp, padre2tmp;
		Individuo padre1 = null;
		Individuo padre2 = null;
		int valores[] = new int[inst.getNoItems()];
		int t = 0, ptrDesc = 0;
		double num_aleat;
		Random rand = new Random();

		for (int i = 0; i < numero_padres; i += 2) {

			num_aleat = Math.random();
			if (num_aleat <= probabilidad_cruza) {
				hijo = new Individuo[2];

				padre1 = poblacion[indicePadre[i]];
				padre2 = poblacion[indicePadre[i + 1]];
				padre2tmp = padre2;
				padre1tmp = padre1;

				for (int k = 0; k < 2; k++) {

					/* 1.- Seleccionar puntos aleatorios. */

					boolean posiciones_aleatorias[] = new boolean[chromosoma_size];
					t = 0;
					for (int l = 0; l < inst.getNoItems(); l++)
						posiciones_aleatorias[l] = rand.nextBoolean();

					if (k == 0) {
						/*
						 * 2.- Produce hijo utilizando de P1 los puntos
						 * seleccionados
						 */
						hijo[k] = new Individuo(new int[chromosoma_size],
								current_generation);

						for (int l = 0; l < chromosoma_size; l++)
							if (posiciones_aleatorias[l]) {
								hijo[k].setGen(l, padre1.getGen(l));
								valores[t++] = padre1.getGen(l);
							}

						/*
						 * 3.- Borra los valores seleccionados de P2. La
						 * secuencia resultante de valores se usará para
						 * completar el hijo.
						 */
						for (int l = 0; l < t; l++)
							for (int j = 0; j < chromosoma_size; j++)
								if (valores[l] == padre2tmp.getGen(l)) {
									padre2tmp.setGen(j, 0);
									j = chromosoma_size;
								}

						/*
						 * 4.- Coloca en el hijo los valores faltantes de
						 * izquierda a derecha, de acuerdo a la secuencia P2.
						 */

						int pointer = 0;
						for (int l = 0; l < chromosoma_size; l++)
							if (hijo[k].getGen(l) == 0)
								for (int j = pointer; j < chromosoma_size; j++)
									if (padre2tmp.getGen(j) != 0) {
										hijo[k].setGen(l, padre2tmp.getGen(j));
										pointer = j + 1;
										j = chromosoma_size;
									}
					}

					if (k == 1) {
						/*
						 * Se repite del paso 1 - 4 para el segundo hijo
						 * utilizando P2.
						 */
						hijo[k] = new Individuo(new int[chromosoma_size],
								current_generation);
						for (int l = 0; l < chromosoma_size; l++)
							if (posiciones_aleatorias[l]) {
								hijo[k].setGen(l, padre2.getGen(l));
								valores[t++] = padre2.getGen(l);
							}

						for (int l = 0; l < t; l++)
							for (int j = 0; j < chromosoma_size; j++)
								if (valores[l] == padre1tmp.getGen(j)) {
									padre1tmp.setGen(j, 0);
									j = chromosoma_size;
								}

						int pointer = 0;
						for (int l = 0; l < chromosoma_size; l++)
							if (hijo[k].getGen(l) == 0)
								for (int j = pointer; j < chromosoma_size; j++)
									if (padre1tmp.getGen(j) != 0) {
										hijo[k].setGen(l, padre1tmp.getGen(j));
										pointer = j + 1;
										j = chromosoma_size;
									}
					}

					descendientes[ptrDesc] = new Individuo(hijo[k]);
					ptrDesc++;

				}
				System.out.println(/** Cruza **/
				);
				Utilities.printArray(padre1.getChromosoma());
				Utilities.printArray(padre2.getChromosoma());

				Utilities.printArray(hijo[0].getChromosoma());
				Utilities.printArray(hijo[1].getChromosoma());
				System.out.println("/**Fin de cruza**/");

			} else {

				descendientes[ptrDesc] = new Individuo(
						poblacion[indicePadre[i]]);
				descendientes[ptrDesc + 1] = new Individuo(
						poblacion[indicePadre[i + 1]]);

				ptrDesc += 2;

			}

		}
		return descendientes;
	}

	private Individuo[] mutarHijosInsercion(Individuo[] descendientes) {
		Random rnd = new Random();
		for (int i = 0; i < descendientes.length; i++) {

			int posOrigen = rnd.nextInt(chromosoma_size);
			int posDestino = rnd.nextInt(chromosoma_size);

			int origen = descendientes[i].getGen(posOrigen);

			if (posOrigen > posDestino)
				/* Desplaza los elementos hacia la derecha */
				for (int j = posOrigen; j > posDestino; j--)
					descendientes[i].setGen(j, descendientes[i].getGen(j - 1));

			// descendientes[i].setGen(j, descendientes[i].getGen(j - 1),
			// inst.getItemGain(j), inst.getItemWeight(j));
			else
				/* Desplaza los elementos hacia la izquierda */
				for (int j = posOrigen; j < posDestino; j++)
					descendientes[i].setGen(j, descendientes[i].getGen(j + 1));
			// descendientes[i].setGen(j, descendientes[i].getGen(j + 1),
			// inst.getItemGain(j), inst.getItemWeight(j));

			descendientes[i].setGen(posDestino, origen);
			// descendientes[i].setGen(posDestino, origen,
			// inst.getItemGain(posDestino), inst.getItemWeight(posDestino));
		}

		return descendientes;
	}

	private Individuo[] mutarHijosFlip(Individuo[] descendientes) {
		Random rnd = new Random();
		for (int ind = 0; ind < descendientes.length; ind++) {
			int pos = rnd.nextInt(chromosoma_size);
			if (descendientes[ind].getGen(pos) == 1)
				descendientes[ind].setGen(pos, 0);
			else
				descendientes[ind].setGen(pos, 1);
		}

		return descendientes;
	}

	private Individuo[] generarNuevaPoblacion(int[] descartados,
			Individuo[] descendientes, Individuo[] poblacion) {

		for (int i = 0; i < numero_padres; i++)
			poblacion[descartados[i]].copyThis(descendientes[i]);
		return poblacion;
	}

	private void seleccionarMasAptos(Individuo[] ind) {
		int apto[] = new int[tamano_poblacion];
		int noApto[] = new int[tamano_poblacion];
		int ptrAptos = 0, ptrNoAptos = 0;

		// Probar con el Promedio
		for (int i = 0; i < tamano_poblacion; i++)
			if (ind[i].getAptitud() > 40 && !isOverWeight(ind[i].getWeight()))
				apto[ptrAptos++] = i;
			else
				noApto[ptrNoAptos++] = i;
		// setAptos();
		// setNoAptos();
	}

	private boolean isOverWeight(int weight) {
		if (weight > inst.getCapacity())
			return true;
		return false;
	}

	private void evaluarPoblacion() {
		for (int i = 0; i < tamano_poblacion; i++)
			poblacion[i] = evaluarIndividuo(poblacion[i]);

	}

	/**
	 * 
	 * @param ind
	 *            Sample to be evaluate.
	 * @return An array of size 3 including the weight, gain, and 1 if is a
	 *         feasible solution, 0 elsewhere.
	 */

	private Individuo evaluarIndividuo(Individuo ind) {
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

	public Individuo[] busquedaKNN(Individuo[] pob) {

		return pob;
	}

	public Individuo[] busquedaLocalBest(Individuo[] pobl) {

		for (int ind = 0; ind < pobl.length; ind++) {

			Individuo auxInd = new Individuo(pobl[ind]);
			int mejorGen1 = 0;
			int mejorGen2 = 0;
			int mejorAlelo = 0;
			int mejorAlelo2 = 0;

			double mejorAptitud = pobl[ind].getAptitud();

			for (int pos = 0; pos < chromosoma_size; pos++) {
				int original = auxInd.getGen(pos);

				int aleloFlip = (original == 0 ? 1 : 0);
				auxInd.setGen(pos, aleloFlip, inst.getItemGain(pos),
						inst.getItemWeight(pos));

				for (int posNext = 0; posNext < chromosoma_size; posNext++) {

					int orNext = auxInd.getGen(posNext);
					int aleloNextFlip = (original == 0 ? 1 : 0);

					auxInd.setGen(posNext, aleloNextFlip,
							inst.getItemGain(posNext),
							inst.getItemWeight(posNext));

					if (auxInd.getAptitud() > mejorAptitud
							&& auxInd.getWeight() <= inst.getCapacity()) {

						mejorGen1 = pos;
						mejorGen2 = posNext;
						mejorAlelo = aleloFlip;
						mejorAlelo2 = aleloNextFlip;
						mejorAptitud = auxInd.getAptitud();

					}

					auxInd.setGen(posNext, orNext, inst.getItemGain(posNext),
							inst.getItemWeight(posNext));
				}

				auxInd.setGen(pos, original, inst.getItemGain(pos),
						inst.getItemWeight(pos));
			}

			if (mejorAptitud > pobl[ind].getAptitud()) {

				// System.out.println("mejoro: " + individuo[ind].getAptitud()
				// + " -> " + mejorAptitud);

				pobl[ind].setGen(mejorGen1, mejorAlelo,
						inst.getItemGain(mejorGen1),
						inst.getItemWeight(mejorGen1));

				pobl[ind].setGen(mejorGen2, mejorAlelo2,
						inst.getItemGain(mejorGen2),
						inst.getItemWeight(mejorGen2));
			}
		}

		return pobl;
	}

	/**
	 * Swap entre dos genes, se queda con el mejor intermabio. El segundo gen se
	 * escoge aleatoriamente.
	 * 
	 * @param individuo
	 * @return
	 */

	public Individuo[] busquedaLocalBestV2(Individuo[] individuo) {

		Random rnd = new Random();
		for (int ind = 0; ind < individuo.length; ind++) {

			Individuo auxInd = new Individuo(individuo[ind]);
			int mejorGen1 = 0;
			int mejorGen2 = 0;
			int mejorAlelo = 0;
			int mejorAlelo2 = 0;

			double mejorAptitud = individuo[ind].getAptitud();

			for (int pos = 0; pos < chromosoma_size; pos++) {

				int original = auxInd.getGen(pos);
				int aleloFlip = (original == 0 ? 1 : 0);

				auxInd.setGen(pos, aleloFlip, inst.getItemGain(pos),
						inst.getItemWeight(pos));

				for (int i = 0; i < chromosoma_size * 0.25; i++) {

					int gen = rnd.nextInt(chromosoma_size);
					int aleloOrgChang = auxInd.getGen(gen);
					int aleloChangFlip = (original == 0 ? 1 : 0);

					auxInd.setGen(gen, aleloChangFlip, inst.getItemGain(gen),
							inst.getItemWeight(gen));

					if (auxInd.getAptitud() > mejorAptitud
							&& auxInd.getWeight() <= inst.getCapacity()) {

						mejorGen1 = pos;
						mejorGen2 = gen;
						mejorAlelo = aleloFlip;
						mejorAlelo2 = aleloChangFlip;
						mejorAptitud = auxInd.getAptitud();

					}

					auxInd.setGen(gen, aleloOrgChang, inst.getItemGain(gen),
							inst.getItemWeight(gen));
				}

				auxInd.setGen(pos, original, inst.getItemGain(pos),
						inst.getItemWeight(pos));
			}

			if (mejorAptitud > individuo[ind].getAptitud()) {

				individuo[ind].setGen(mejorGen1, mejorAlelo,
						inst.getItemGain(mejorGen1),
						inst.getItemWeight(mejorGen1));

				individuo[ind].setGen(mejorGen2, mejorAlelo2,
						inst.getItemGain(mejorGen2),
						inst.getItemWeight(mejorGen2));
			}
		}

		return individuo;
	}

	/**
	 * hace un flip en cada gen y se queda con el mejor.
	 * 
	 * @param individuo
	 * @return
	 */

	public Individuo[] busquedaLocalBestV3(Individuo[] individuo) {

		for (int ind = 0; ind < individuo.length; ind++) {

			Individuo auxInd = new Individuo(individuo[ind]);
			int mejorGen1 = 0;
			int mejorAlelo = 0;
			double mejorAptitud = individuo[ind].getAptitud();

			for (int pos = 0; pos < chromosoma_size; pos++) {

				int original = auxInd.getGen(pos);
				int chang = (original == 0 ? 1 : 0);

				auxInd.setGen(pos, chang, inst.getItemGain(pos),
						inst.getItemWeight(pos));

				if (auxInd.getAptitud() > mejorAptitud
						&& auxInd.getWeight() <= inst.getCapacity()) {

					mejorGen1 = pos;
					mejorAlelo = chang;
					mejorAptitud = auxInd.getAptitud();
				}

				auxInd.setGen(pos, original, inst.getItemGain(pos),
						inst.getItemWeight(pos));
			}

			if (mejorAptitud > individuo[ind].getAptitud()) {
				individuo[ind].setGen(mejorGen1, mejorAlelo,
						inst.getItemGain(mejorGen1),
						inst.getItemWeight(mejorGen1));
			}
		}

		return individuo;
	}

	public Individuo busquedaLocalBestV3(Individuo individuo) {

		Individuo auxInd = new Individuo(individuo);
		int mejorGen1 = 0;
		int mejorAlelo = 0;
		double mejorAptitud = individuo.getAptitud();

		for (int pos = 0; pos < chromosoma_size; pos++) {
			int original = auxInd.getGen(pos);
			auxInd.setGen(pos, (original == 0 ? 1 : 0), inst.getItemGain(pos),
					inst.getItemWeight(pos));
			if (auxInd.getAptitud() > mejorAptitud
					&& auxInd.getWeight() <= inst.getCapacity()) {
				mejorGen1 = pos;
				mejorAlelo = original;
				mejorAptitud = auxInd.getAptitud();

			}
		}

		if (mejorAptitud > individuo.getAptitud()) {

			individuo.setGen(mejorGen1, mejorAlelo,
					inst.getItemGain(mejorGen1), inst.getItemWeight(mejorGen1));

		}

		return individuo;
	}

	public Individuo busquedaLocalBestILS(Individuo individuo) {

		Individuo auxInd = new Individuo(individuo);
		int mejorGen1 = 0;
		int mejorAlelo = 0;
		double mejorAptitud = individuo.getAptitud();

		for (int pos = 0; pos < chromosoma_size; pos++) {
			if (!genesPerturbados[pos]) {
				int original = auxInd.getGen(pos);
				auxInd.setGen(pos, (original == 0 ? 1 : 0),
						inst.getItemGain(pos), inst.getItemWeight(pos));
				if (auxInd.getAptitud() > mejorAptitud
						&& auxInd.getWeight() <= inst.getCapacity()) {
					mejorGen1 = pos;
					mejorAlelo = original;
					mejorAptitud = auxInd.getAptitud();

				}
			}
		}

		if (mejorAptitud > individuo.getAptitud()) {

			individuo.setGen(mejorGen1, mejorAlelo,
					inst.getItemGain(mejorGen1), inst.getItemWeight(mejorGen1));

		}

		return individuo;
	}

	public Individuo[] busquedaLocalIterada(Individuo[] pobl) {
		double intensity = 0.20;
		for (int ind = 0; ind < pobl.length; ind++) {

			Individuo solPrima = perturbation(new Individuo(pobl[ind]),
					intensity);

			Individuo solOptPrima = busquedaLocalBestILS(new Individuo(solPrima));

			pobl[ind] = acceptanceCriterion(pobl[ind], solOptPrima);

		}

		return pobl;
	}

	public Individuo[] busquedaLocalVNS(Individuo[] pobl) {

		boolean stopCriteria = false;
		int ite = 10;
		while (!stopCriteria)
			for (int ind = 0; ind < pobl.length; ind++) {
				Individuo sPrima = variacion(new Individuo(pobl[ind]), 0.20);
				Individuo sOptPrima = busquedaLocalBestV3(sPrima);

				if (sOptPrima.getAptitud() > pobl[ind].getAptitud()
						&& sOptPrima.getWeight() <= inst.getCapacity())
					pobl[ind].copyThis(sOptPrima);

				if (ite < 0)
					stopCriteria = true;
				ite--;

			}
		return pobl;
	}

	public Individuo[] busquedaLocalVNSv2(Individuo[] pobl) {

		boolean stopCriteria = false;
		int ite = 10;
		while (!stopCriteria)
			for (int ind = 0; ind < pobl.length; ind++) {
				Individuo sPrima = perturbation(new Individuo(pobl[ind]), 0.20);
				Individuo sOptPrima = busquedaLocalBestV3(sPrima);

				if (sOptPrima.getAptitud() > pobl[ind].getAptitud()
						&& sOptPrima.getWeight() <= inst.getCapacity()) {
					pobl[ind].copyThis(sOptPrima);
					ind = 0;

				} else
					ind++;

				if (ite < 0)
					stopCriteria = true;
				ite--;
			}
		return pobl;
	}

	boolean[] genesPerturbados;

	private Individuo perturbation(Individuo ind, double intensity) {
		int numeroDeElementosACambiar = (int) (inst.getNoItems() * intensity);
		genesPerturbados = new boolean[chromosoma_size];
		Random rnd = new Random();
		for (int i = 0; i < numeroDeElementosACambiar; i++) {
			int gen = rnd.nextInt(chromosoma_size);
			genesPerturbados[gen] = true;
			ind.setGen(gen, (ind.getGen(gen) == 0 ? 1 : 0),
					inst.getItemGain(gen), inst.getItemWeight(gen));
		}
		return ind;
	}

	private Individuo variacion(Individuo ind, double intensity) {
		int numeroDeElementosACambiar = (int) (inst.getNoItems() * intensity);
		genesPerturbados = new boolean[chromosoma_size];
		Random rnd = new Random();
		for (int i = 0; i < numeroDeElementosACambiar; i++) {
			int gen = rnd.nextInt(chromosoma_size);
			genesPerturbados[gen] = true;
			ind.setGen(gen, (ind.getGen(gen) == 0 ? 1 : 0),
					inst.getItemGain(gen), inst.getItemWeight(gen));
		}
		return ind;
	}

	private Individuo acceptanceCriterion(Individuo ind, Individuo ind2) {
		if (ind.getAptitud() > ind2.getAptitud())
			return ind;
		else
			return ind2;
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

	/**
	 * @return the individuoGlobal
	 */
	public Individuo getIndividuoGlobal() {
		return individuoGlobal;
	}

	/**
	 * @param individuoGlobal
	 *            the individuoGlobal to set
	 */
	public void setIndividuoGlobal(Individuo individuoGlobal) {
		this.individuoGlobal = individuoGlobal;
	}

	private void setDescendientes(Individuo[] descendientes) {
		this.descendientes = descendientes;
	}

	private void setPoblacion(Individuo[] poblacion) {
		this.poblacion = poblacion;
	}

	private Individuo[] getPoblacion() {
		return this.poblacion;
	}

	/**
	 * @return the timeBestSolution
	 */
	public double getTimeBestSolution() {
		return timeBestSolution;
	}

	/**
	 * @param timeBestSolution
	 *            the timeBestSolution to set
	 */
	public void setTimeBestSolution(double timeBestSolution) {
		this.timeBestSolution = timeBestSolution;
	}

	/**
	 * @return the totalTime
	 */
	public double getTotalTime() {
		return totalTime;
	}

	/**
	 * @param totalTime
	 *            the totalTime to set
	 */
	public void setTotalTime(double totalTime) {
		this.totalTime = totalTime;
	}

	private Individuo[] getDescendientes() {
		return this.descendientes;
	}

	private void setDescartados(int[] descartados) {
		this.indiceDescartados = descartados;
	}

	private int[] getDescartados() {
		return this.indiceDescartados;
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
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		for (int i = 1; i <= 4; i++) {
			Instance instance = Instance.readInstance(Instance.getInstance(i));
			for (int j = 1; j <= 5; j++) {
				Knapsackv3 bpp = new Knapsackv3(instance);
				bpp.AlgoritmoMemetico(0.8, 0.1, 100, 200);
				bpp.saveResults("E7-parametros");
			}
		}

		// Knapsackv3 bpp = new
		// Knapsackv3(Instance.readInstance(Instance.getInstance(1)));
		// bpp.algoritmoMemetico(0.8, 0.1, 100, 200);
		// bpp.saveResults("E5-Big Generation");
	}
}
