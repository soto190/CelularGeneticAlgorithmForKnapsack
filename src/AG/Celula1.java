package AG;

import java.util.Random;

public class Celula1 extends Celula {

	public Celula1(Instance inst, double probCruza, double probMut, int tamPob) {
		super(inst, probCruza, probMut, tamPob);
		id = 1;
	}

	/**
	 * Position based crossover.
	 */
	@Override
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

				/**
				 * Si true se mantiene, si false se intercambia por el alelo del
				 * padre2.
				 **/
				for (int l = 0; l < chromosoma_size; l++) {
					if (!rand.nextBoolean())
						descendientes[i].setGen(l,
								poblacion[indicePadre[i + 1]].getGen(l),
								getInst().getItemGain(l), getInst()
										.getItemWeight(l));

					if (!rand.nextBoolean())
						descendientes[i + 1].setGen(l,
								poblacion[indicePadre[i]].getGen(l), getInst()
										.getItemGain(l), getInst()
										.getItemWeight(l));
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
	 * Mutation by insertion.
	 **/
	@Override
	protected Individuo mutar(Individuo ind) {
		Random rnd = new Random();

		int posOrigen = rnd.nextInt(getChromosomaSize());
		int posDestino = rnd.nextInt(getChromosomaSize());

		int origen = ind.getGen(posOrigen);

		if (posOrigen > posDestino)
			/** Desplaza los elementos hacia la derecha **/
			for (int j = posOrigen; j > posDestino; j--)
				ind.setGen(j, ind.getGen(j - 1));

		else
			/** Desplaza los elementos hacia la izquierda **/
			for (int j = posOrigen; j < posDestino; j++)
				ind.setGen(j, ind.getGen(j + 1));

		ind.setGen(posDestino, origen);

		return ind;
	}



}
