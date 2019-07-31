# CelularGeneticAlgorithmForKnapsack
A Celuar Genetic algoirhtm to solve the Knapsack problem.

This project is the implementation of a Celuar Genetic Algorithm (CGA) Each where each cell is a Genetic Algorithm with 
different probability configurations, mutation operator, and crossover operators. 
The CGA is composed of four cells:
- The firs cell has a position based crossover and a insertion mutation.
- The second cell has a two point crossover and a bit flip mutation.
- The third cell has a uniform corssover and custom mutation. The mutation turn off randomly 10% of the genes (items); then some genes are 
randomly turned on until the sack is full.
- The fourth cell has a position based crossover and a custom mutation. The mutation turn off randomly 5 genes(items) of the sack;
then some genes are randomly turned on until the sack is full.
