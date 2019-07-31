package AG;

import java.util.Random;

public class Celula2 extends Celula {

	public Celula2(Instance inst, double probCruza, double probMut, int tamPob) {
		super(inst, probCruza, probMut, tamPob);
		id = 2;
	}

	/**
	 * Cruza dos puntos
	 */
	@Override
	protected Individuo[] cruza(int[] indicePadre) {
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
	 * Mutacion por flib de un bit.
	 */
	@Override
	protected Individuo mutar(Individuo ind) {
		Random rnd = new Random();
		int pos = rnd.nextInt(chromosoma_size);
		if (ind.getGen(pos) == 1)
			ind.setGen(pos, 0);
		else
			ind.setGen(pos, 1);

		return ind;
	}

}
