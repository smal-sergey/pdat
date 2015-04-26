package com.smalser.pdat.core.calculator;

import com.smalser.pdat.core.structure.TaskConstraints;
import org.apache.commons.math3.analysis.solvers.AbstractUnivariateSolver;
import org.apache.commons.math3.analysis.solvers.BrentSolver;
import org.apache.commons.math3.distribution.AbstractRealDistribution;

public class BetaFinder
{
    private final AbstractRealDistribution distribution;
    private final double precision;
    private final TaskConstraints taskConstraints;

    public BetaFinder(AbstractRealDistribution distribution, TaskConstraints taskConstraints)
    {
        this.taskConstraints = taskConstraints;
        this.distribution = distribution;
//        this.precision = taskConstraints.gamma / 100;
        this.precision = 1.0e-6;
    }

    public double findMinBeta()
    {
        double maxBeta = taskConstraints.rightBound;
        AbstractUnivariateSolver solver = new BrentSolver(precision);
        return solver.solve(100, this::integrateDensity, taskConstraints.alpha + 1.0e-4, maxBeta);
    }

    private double integrateDensity(double beta)
    {
        return distribution.cumulativeProbability(beta) - taskConstraints.gamma;
    }
}










